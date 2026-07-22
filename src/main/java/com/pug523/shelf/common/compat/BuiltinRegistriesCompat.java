package com.pug523.shelf.compat;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
//#if MC >= 11900
import net.minecraft.core.registries.BuiltInRegistries;
//#endif
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public class BuiltinRegistriesCompat {
    //#if MC >= 11900
    public static final DefaultedRegistry<Block> BLOCK = BuiltInRegistries.BLOCK;
    public static final DefaultedRegistry<Item> ITEM = BuiltInRegistries.ITEM;
    public static final DefaultedRegistry<Fluid> FLUID = BuiltInRegistries.FLUID;
    public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = BuiltInRegistries.ENTITY_TYPE;
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = BuiltInRegistries.BLOCK_ENTITY_TYPE;
    public static final Registry<PoiType> POINT_OF_INTEREST_TYPE = BuiltInRegistries.POINT_OF_INTEREST_TYPE;
    //#else
    //$$ public static final DefaultedRegistry<Block> BLOCK = DefaultedRegistry.BLOCK;
    //$$ public static final DefaultedRegistry<Item> ITEM = DefaultedRegistry.ITEM;
    //$$ public static final DefaultedRegistry<Fluid> FLUID = DefaultedRegistry.FLUID;
    //$$ public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = DefaultedRegistry.ENTITY_TYPE;
    //$$ public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DefaultedRegistry.BLOCK_ENTITY_TYPE;
    //$$ public static final DefaultedRegistry<PoiType> POINT_OF_INTEREST_TYPE = DefaultedRegistry.POINT_OF_INTEREST_TYPE;
    //#endif

    public static Item getItem(Identifier id) {
        //#if MC >= 12102
        return ITEM.get(id).map(item -> item.value()).orElse(Items.AIR);
        //#else
        //$$ return ITEM.get(id);
        //#endif
    }
}
