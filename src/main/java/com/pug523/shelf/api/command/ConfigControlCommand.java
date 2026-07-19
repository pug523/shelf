package com.pug523.shelf.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.pug523.shelf.config.ConfigTreeWalker;
import com.pug523.shelf.compat.ComponentCompat;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.util.Stack;
import java.util.function.BiFunction;

import static com.pug523.shelf.command.CommandUtil.literal;
import static com.pug523.shelf.command.CommandUtil.argument;

/// A command registration factory that generates nested subcommands for querying,
/// modifying, and resetting configuration options.
public class ConfigControlCommand<T> implements Command {
    private final String commandBaseName;
    private final String subCommandName;
    private final T configInstance;
    private final T defaultInstance;
    private final Runnable onSave;
    private final BiFunction<Field, Object, String> getFormatter;

    private ConfigControlCommand(Builder<T> builder) {
        this.commandBaseName = builder.commandBaseName;
        this.subCommandName = builder.subCommandName;
        this.configInstance = builder.configInstance;
        this.defaultInstance = builder.defaultInstance;
        this.onSave = builder.onSave;
        this.getFormatter = builder.getFormatter;
    }

    /// Recursively walks the configuration object hierarchy to register nested command nodes.
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> baseCommand = literal(this.commandBaseName);
        LiteralArgumentBuilder<FabricClientCommandSource> subCommand = literal(this.subCommandName);

        Stack<LiteralArgumentBuilder<FabricClientCommandSource>> nodeStack = new Stack<>();
        nodeStack.push(subCommand);

        ConfigTreeWalker.walk(this.configInstance, this.defaultInstance, ctx -> {
            String nodeName = ctx.field().getName();

            if (ctx.isLeaf()) {
                if (isCommandParsable(ctx.type())) {
                    LiteralArgumentBuilder<FabricClientCommandSource> leafNode = getFieldNode(ctx.field(), nodeName, ctx.type(), ctx.instance(), ctx.defaultInstance());
                    nodeStack.peek().then(leafNode);
                }
            } else {
                LiteralArgumentBuilder<FabricClientCommandSource> branchNode = literal(nodeName);
                nodeStack.push(branchNode);
                ctx.recurse();
                nodeStack.pop();
                nodeStack.peek().then(branchNode);
            }
        });

        baseCommand.then(subCommand);
        dispatcher.register(baseCommand);
    }

    private @NonNull LiteralArgumentBuilder<FabricClientCommandSource> getFieldNode(Field field, String name, Class<?> type, Object inst, Object defInst) {
        LiteralArgumentBuilder<FabricClientCommandSource> fieldNode = literal(name);

        fieldNode.executes(ctx -> {
            try {
                Object val = field.get(inst);
                ctx.getSource().sendFeedback(ComponentCompat.literal(getFormatter.apply(field, val)));
            } catch (Exception e) {
                ctx.getSource().sendError(ComponentCompat.literal("Failed to read field: " + e.getMessage()));
            }
            return 1;
        });

        fieldNode.then(literal("reset").executes(ctx -> {
            try {
                field.set(inst, field.get(defInst));
                onSave.run();
                ctx.getSource().sendFeedback(ComponentCompat.literal("Reset " + name + " to default."));
            } catch (Exception e) {
                ctx.getSource().sendError(ComponentCompat.literal("Failed to reset: " + e.getMessage()));
            }
            return 1;
        }));

        if (type == boolean.class || type == Boolean.class) {
            fieldNode.then(literal("set").then(argument("value", BoolArgumentType.bool()).executes(ctx -> setValue(ctx, field, inst, BoolArgumentType.getBool(ctx, "value")))));
        } else if (type == int.class || type == Integer.class) {
            fieldNode.then(literal("set").then(argument("value", IntegerArgumentType.integer()).executes(ctx -> setValue(ctx, field, inst, IntegerArgumentType.getInteger(ctx, "value")))));
        } else if (type == float.class || type == Float.class) {
            fieldNode.then(literal("set").then(argument("value", FloatArgumentType.floatArg()).executes(ctx -> setValue(ctx, field, inst, FloatArgumentType.getFloat(ctx, "value")))));
        } else if (type == double.class || type == Double.class) {
            fieldNode.then(literal("set").then(argument("value", DoubleArgumentType.doubleArg()).executes(ctx -> setValue(ctx, field, inst, DoubleArgumentType.getDouble(ctx, "value")))));
        } else if (type == String.class) {
            fieldNode.then(literal("set").then(argument("value", StringArgumentType.greedyString()).executes(ctx -> setValue(ctx, field, inst, StringArgumentType.getString(ctx, "value")))));
        } else if (type.isEnum()) {
            fieldNode.then(literal("set").then(argument("value", StringArgumentType.string()).suggests((ctx, b) -> {
                for (Object c : type.getEnumConstants()) b.suggest(c.toString());
                return b.buildFuture();
            }).executes(ctx -> {
                String input = StringArgumentType.getString(ctx, "value");
                for (Object c : type.getEnumConstants()) {
                    if (c.toString().equalsIgnoreCase(input)) return setValue(ctx, field, inst, c);
                }
                return 0;
            })));
        }
        return fieldNode;
    }

    private int setValue(CommandContext<FabricClientCommandSource> ctx, Field f, Object inst, Object val) {
        try {
            f.set(inst, val);
            onSave.run();
            ctx.getSource().sendFeedback(ComponentCompat.literal("Updated field."));
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean isCommandParsable(Class<?> type) {
        return type.isPrimitive() || type == Boolean.class || type == Integer.class || type == Float.class || type == Double.class || type == String.class || type.isEnum();
    }

    /// Fluent builder pattern class for configuring and creating `ConfigControlCommand` instances.
    public static class Builder<T> {
        private final String commandBaseName;
        private final T configInstance;
        private final T defaultInstance;

        private String subCommandName = "config";
        private Runnable onSave = () -> {
        };
        private BiFunction<Field, Object, String> getFormatter = (field, val) -> field.getName() + " is currently: " + val;

        /// Creates a base builder context bound to specific runtime configurations and targets.
        public Builder(String commandBaseName, T configInstance, T defaultInstance) {
            this.commandBaseName = commandBaseName;
            this.configInstance = configInstance;
            this.defaultInstance = defaultInstance;
        }

        /// Overrides the standard default subcommand node name endpoint.
        public Builder<T> subCommandName(String name) {
            this.subCommandName = name;
            return this;
        }

        /// Assigns a post-modification action runner task callback.
        public Builder<T> onSave(Runnable onSave) {
            this.onSave = onSave;
            return this;
        }

        /// Customizes the visual message layout context when reading configuration entries.
        public Builder<T> formatGetOutput(BiFunction<Field, Object, String> formatter) {
            this.getFormatter = formatter;
            return this;
        }

        /// Instantiates a final executable command setup pipeline state.
        public ConfigControlCommand<T> build() {
            return new ConfigControlCommand<>(this);
        }
    }
}
