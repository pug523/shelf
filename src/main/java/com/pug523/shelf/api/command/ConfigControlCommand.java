package com.pug523.shelf.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.pug523.shelf.core.config.ConfigTreeWalker;
import com.pug523.shelf.common.compat.ComponentCompat;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;

import java.lang.reflect.Field;
import java.util.Arrays;

import static com.pug523.shelf.common.command.CommandUtil.literal;
import static com.pug523.shelf.common.command.CommandUtil.argument;

/// A command registration factory that generates nested subcommands for
/// querying, modifying, and resetting configuration options.
public class ConfigControlCommand<T> implements Command {
    private final String commandBaseName;
    private final String subCommandName;
    private final T configInstance;
    private final T defaultInstance;
    private final Runnable onUpdate;

    private static final String GET_COMMAND_NAME = "get";
    private static final String RESET_COMMAND_NAME = "reset";
    private static final String SET_COMMAND_NAME = "set";
    private static final String VALUE_ARGUMENT_NAME = "value";

    public ConfigControlCommand(String commandBaseName, String subCommandName, T configInstance, T defaultInstance,
            Runnable onUpdate) {
        this.commandBaseName = commandBaseName;
        this.subCommandName = subCommandName;
        this.configInstance = configInstance;
        this.defaultInstance = defaultInstance;
        this.onUpdate = onUpdate;
    }

    private ConfigControlCommand(Builder<T> builder) {
        this(builder.commandBaseName, builder.subCommandName, builder.configInstance, builder.defaultInstance,
                builder.onUpdate);
    }

    /// Recursively walks the configuration object hierarchy to register nested
    /// command nodes.
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> baseCommand = literal(this.commandBaseName);
        LiteralArgumentBuilder<FabricClientCommandSource> subCommand = literal(this.subCommandName);

        LiteralArgumentBuilder<FabricClientCommandSource> getRoot = literal(GET_COMMAND_NAME);
        LiteralArgumentBuilder<FabricClientCommandSource> setRoot = literal(SET_COMMAND_NAME);
        LiteralArgumentBuilder<FabricClientCommandSource> resetRoot = literal(RESET_COMMAND_NAME);

        ConfigTreeWalker.walk(this.configInstance, this.defaultInstance, ctx -> {
            String nodeName = ctx.field().getName();

            if (ctx.isLeaf()) {
                if (isCommandParsable(ctx.type())) {
                    LiteralArgumentBuilder<FabricClientCommandSource> getLeaf = literal(nodeName);
                    getLeaf.executes(c -> readValue(c, ctx.field(), ctx.instance(), nodeName));
                    getRoot.then(getLeaf);

                    LiteralArgumentBuilder<FabricClientCommandSource> setLeaf = literal(nodeName);
                    addSetArgument(setLeaf, ctx.field(), ctx.instance(), nodeName, ctx.type());
                    setRoot.then(setLeaf);

                    LiteralArgumentBuilder<FabricClientCommandSource> resetLeaf = literal(nodeName);
                    resetLeaf
                            .executes(c -> resetValue(c, ctx.field(), ctx.instance(), ctx.defaultInstance(), nodeName));
                    resetRoot.then(resetLeaf);
                }
            } else {
                LiteralArgumentBuilder<FabricClientCommandSource> getBranch = literal(nodeName);
                getRoot.then(getBranch);
                LiteralArgumentBuilder<FabricClientCommandSource> setBranch = literal(nodeName);
                setRoot.then(setBranch);
                LiteralArgumentBuilder<FabricClientCommandSource> resetBranch = literal(nodeName);
                resetRoot.then(resetBranch);

                ctx.recurse();
            }
        });

        subCommand.then(getRoot).then(setRoot).then(resetRoot);
        baseCommand.then(subCommand);
        dispatcher.register(baseCommand);
    }

    private int readValue(CommandContext<FabricClientCommandSource> ctx, Field field, Object inst, String name) {
        try {
            Object val = field.get(inst);
            ctx.getSource()
                    .sendFeedback(ComponentCompat.literal(name).withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD)
                            .append(ComponentCompat.literal(" = ").withStyle(ChatFormatting.RESET))
                            .append(ComponentCompat.literal(String.valueOf(val)).withStyle(ChatFormatting.AQUA)));
        } catch (Exception e) {
            ctx.getSource().sendError(ComponentCompat.literal("Failed to read field " + name + ": " + e.getMessage())
                    .withStyle(ChatFormatting.RED));
        }
        return 1;
    }

    private int resetValue(CommandContext<FabricClientCommandSource> ctx, Field field, Object inst, Object defInst,
            String name) {
        try {
            Object defaultVal = field.get(defInst);
            field.set(inst, defaultVal);
            onUpdate.run();
            ctx.getSource().sendFeedback(ComponentCompat.literal("Reset ")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                    .append(ComponentCompat.literal(name).withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD))
                    .append(ComponentCompat.literal(" to default: ").withStyle(ChatFormatting.BLUE))
                    .append(ComponentCompat.literal(String.valueOf(defaultVal)).withStyle(ChatFormatting.AQUA)));
        } catch (Exception e) {
            ctx.getSource().sendError(ComponentCompat.literal("Failed to reset " + name + ": " + e.getMessage())
                    .withStyle(ChatFormatting.RED));
        }
        return 1;
    }

    private void addSetArgument(LiteralArgumentBuilder<FabricClientCommandSource> node, Field field, Object inst,
            String name, Class<?> type) {
        if (type == boolean.class || type == Boolean.class) {
            node.then(argument(VALUE_ARGUMENT_NAME, BoolArgumentType.bool()).executes(
                    ctx -> setValue(ctx, field, inst, name, BoolArgumentType.getBool(ctx, VALUE_ARGUMENT_NAME))));
        } else if (type == int.class || type == Integer.class) {
            node.then(argument(VALUE_ARGUMENT_NAME, IntegerArgumentType.integer()).executes(
                    ctx -> setValue(ctx, field, inst, name, IntegerArgumentType.getInteger(ctx, VALUE_ARGUMENT_NAME))));
        } else if (type == float.class || type == Float.class) {
            node.then(argument(VALUE_ARGUMENT_NAME, FloatArgumentType.floatArg()).executes(
                    ctx -> setValue(ctx, field, inst, name, FloatArgumentType.getFloat(ctx, VALUE_ARGUMENT_NAME))));
        } else if (type == double.class || type == Double.class) {
            node.then(argument(VALUE_ARGUMENT_NAME, DoubleArgumentType.doubleArg()).executes(
                    ctx -> setValue(ctx, field, inst, name, DoubleArgumentType.getDouble(ctx, VALUE_ARGUMENT_NAME))));
        } else if (type == String.class) {
            node.then(argument(VALUE_ARGUMENT_NAME, StringArgumentType.greedyString()).executes(
                    ctx -> setValue(ctx, field, inst, name, StringArgumentType.getString(ctx, VALUE_ARGUMENT_NAME))));
        } else if (type.isEnum()) {
            node.then(argument(VALUE_ARGUMENT_NAME, StringArgumentType.string()).suggests((ctx, b) -> {
                for (Object c : type.getEnumConstants())
                    b.suggest(c.toString());
                return b.buildFuture();
            }).executes(ctx -> {
                String input = StringArgumentType.getString(ctx, VALUE_ARGUMENT_NAME);
                for (Object c : type.getEnumConstants()) {
                    if (c.toString().equalsIgnoreCase(input))
                        return setValue(ctx, field, inst, name, c);
                }
                ctx.getSource()
                        .sendError(ComponentCompat
                                .literal("Invalid value. Allowed: " + Arrays.toString(type.getEnumConstants()))
                                .withStyle(ChatFormatting.RED));
                return 0;
            }));
        }
    }

    private int setValue(CommandContext<FabricClientCommandSource> ctx, Field f, Object inst, String fieldName,
            Object val) {
        try {
            Object oldVal = f.get(inst);
            f.set(inst, val);
            onUpdate.run();
            ctx.getSource().sendFeedback(ComponentCompat.literal("Updated ")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    .append(ComponentCompat.literal(fieldName).withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD))
                    .append(ComponentCompat.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(ComponentCompat.literal(String.valueOf(oldVal)).withStyle(ChatFormatting.DARK_PURPLE))
                    .append(ComponentCompat.literal(" -> ").withStyle(ChatFormatting.GRAY))
                    .append(ComponentCompat.literal(String.valueOf(val)).withStyle(ChatFormatting.AQUA)));
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendError(
                    ComponentCompat.literal("Error assigning value to " + fieldName).withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    private boolean isCommandParsable(Class<?> type) {
        return type.isPrimitive() || type == Boolean.class || type == Integer.class || type == Float.class
                || type == Double.class || type == String.class || type.isEnum();
    }

    /// Fluent builder pattern class for configuring and creating
    /// `ConfigControlCommand` instances.
    public static class Builder<T> {
        private final String commandBaseName;
        private final T configInstance;
        private final T defaultInstance;

        private String subCommandName = "config";
        private Runnable onUpdate = () -> {
        };

        /// Creates a base builder context bound to specific runtime
        /// configurations and targets.
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
        public Builder<T> onUpdate(Runnable onUpdate) {
            this.onUpdate = onUpdate;
            return this;
        }

        /// Instantiates a final executable command setup pipeline state.
        public ConfigControlCommand<T> build() {
            return new ConfigControlCommand<>(this);
        }
    }
}
