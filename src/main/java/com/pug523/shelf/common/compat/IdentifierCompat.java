package com.pug523.shelf.common.compat;

import com.pug523.shelf.Shelf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public class IdentifierCompat {
    public static Identifier ofVanilla(String id) {
        //#if MC >= 12100
        return Identifier.withDefaultNamespace(id);
        //#else
        //$$ return new ResourceLocation(id);
        //#endif
    }

    public static Identifier of(String namespace, String path) {
        //#if MC == 12006
        //$$ return ResourceLocation.tryBuild(namespace, path);
        //#elseif MC >= 12100
        return Identifier.fromNamespaceAndPath(namespace, path);
        //#else
        //$$ return new ResourceLocation(namespace, path);
        //#endif
    }

    public static Identifier ofShelf(String id) {
        return IdentifierCompat.of(Shelf.MOD_ID, id);
    }

    public static Identifier tryParse(String path) {
        return Identifier.tryParse(path);
    }

    public static Identifier parse(String path) {
        //#if MC >= 12100
        return Identifier.parse(path);
        //#else
        //$$ return new ResourceLocation(path);
        //#endif
    }

    public static Identifier tryBuild(String namespace, String path) {
        //#if MC >= 11900
        return Identifier.tryBuild(namespace, path);
        //#else
        //$$ return new ResourceLocation(namespace, path);
        //#endif
    }

    public static Identifier id(Item item) {
        return BuiltinRegistriesCompat.ITEM.getKey(item);
    }

    public static Identifier id(Block block) {
        return BuiltinRegistriesCompat.BLOCK.getKey(block);
    }

    public static Identifier id(Fluid fluid) {
        return BuiltinRegistriesCompat.FLUID.getKey(fluid);
    }

    public static Identifier id(EntityType<?> entityType) {
        return BuiltinRegistriesCompat.ENTITY_TYPE.getKey(entityType);
    }

    public static Identifier id(BlockEntityType<?> blockEntityType) {
        return BuiltinRegistriesCompat.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
    }

    public static Identifier id(PoiType poiType) {
        return BuiltinRegistriesCompat.POINT_OF_INTEREST_TYPE.getKey(poiType);
    }
}
