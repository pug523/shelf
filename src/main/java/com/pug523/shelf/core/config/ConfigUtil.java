package com.pug523.shelf.core.config;

import java.io.File;

import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectDeserializerBuilder;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializerBuilder;
import com.pug523.shelf.common.compat.BuiltinRegistriesCompat;
import com.pug523.shelf.common.compat.IdentifierCompat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ConfigUtil {
    public static File resolveConfigFile(String dir, String file) {
        return FabricLoader.getInstance().getConfigDir().resolve(dir).resolve(file).toFile();
    }

    private static final String AIR_KEY = BuiltinRegistriesCompat.ITEM.getKey(Items.AIR).toString();

    public static ObjectSerializer createSerializer() {
        ObjectSerializerBuilder builder = ObjectSerializer.builder();

        builder.withSerializerForClass(Item.class, (value, context) -> {
            if (value == null) {
                return AIR_KEY;
            }
            return BuiltinRegistriesCompat.ITEM.getKey(value).toString();
        });

        builder.withSerializerProvider((valueClass, context) -> {
            if (valueClass == null) {
                return (value, ctx) -> null;
            }
            return null;
        });

        return builder.build();
    }

    public static ObjectDeserializer createDeserializer() {
        ObjectDeserializerBuilder builder = ObjectDeserializer.builder();

        builder.withDeserializerForClass(String.class, Item.class, (value, constraint, context) -> {
            if (value == null) {
                return Items.AIR;
            }
            Identifier id = IdentifierCompat.tryParse(value);
            return BuiltinRegistriesCompat.getItem(id);
        });

        builder.withDeserializerForClass(Double.class, float.class, (value, constraint, context) -> value.floatValue());
        builder.withDeserializerForClass(Double.class, Float.class, (value, constraint, context) -> value.floatValue());
        builder.withDeserializerForClass(Long.class, int.class, (value, constraint, context) -> value.intValue());
        builder.withDeserializerForClass(Long.class, Integer.class, (value, constraint, context) -> value.intValue());
        builder.withDeserializerForClass(Long.class, double.class, (value, constraint, context) -> value.doubleValue());
        builder.withDeserializerForClass(Long.class, Double.class, (value, constraint, context) -> value.doubleValue());
        builder.withDeserializerForClass(Long.class, float.class, (value, constraint, context) -> value.floatValue());
        builder.withDeserializerForClass(Long.class, Float.class, (value, constraint, context) -> value.floatValue());

        return builder.build();
    }
}
