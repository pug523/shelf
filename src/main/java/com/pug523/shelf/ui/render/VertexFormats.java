package com.pug523.shelf.ui.render;

import com.mojang.blaze3d.vertex.VertexFormat;
//#if MC >= 260200
import com.mojang.blaze3d.GpuFormat;
//#else
//$$ import com.mojang.blaze3d.vertex.VertexFormatElement;

//#if MC <= 12006
//$$ import com.google.common.collect.ImmutableMap;
//$$ import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//$$ import com.mojang.blaze3d.vertex.VertexFormat;
//#endif
//#endif

public class VertexFormats {
    //#if MC >= 260200
    public static final GpuFormat POSITION_FORMAT = GpuFormat.RGB32_FLOAT;
    public static final GpuFormat COLOR_FORMAT = GpuFormat.RGBA8_UNORM;
    public static final GpuFormat UV0_FORMAT = GpuFormat.RG32_FLOAT;
    public static final GpuFormat UV1_FORMAT = GpuFormat.RG16_SINT;
    public static final GpuFormat UV2_FORMAT = GpuFormat.RG16_SINT;
    // public static final GpuFormat NORMAL_FORMAT = GpuFormat.RGBA8_SNORM;
    // public static final GpuFormat LINE_WIDTH_FORMAT = GpuFormat.R32_FLOAT;
    //#endif

    public static final VertexFormat POSITION_COLOR_TEX_CORNER_RADIUS_RAW_SIZE =
        //#if MC >= 260200
        VertexFormat.builder(0)
            .addAttribute("Position", POSITION_FORMAT)
            .addAttribute("Color", COLOR_FORMAT)
            .addAttribute("UV0", UV0_FORMAT)
            .addAttribute("UV1", UV1_FORMAT)
            .addAttribute("UV2", UV2_FORMAT)
            .build();
        //#elseif MC >= 12100
        //$$ VertexFormat.builder()
        //$$     .add("Position", VertexFormatElement.POSITION)
        //$$     .add("Color", VertexFormatElement.COLOR)
        //$$     .add("UV0", VertexFormatElement.UV0)
        //$$     .add("UV1", VertexFormatElement.UV1)
        //$$     .add("UV2", VertexFormatElement.UV2)
        //$$     .build();
        //#else
        //$$ new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
        //$$     .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
        //$$     .put("Color", DefaultVertexFormat.ELEMENT_COLOR)
        //$$     .put("UV0", DefaultVertexFormat.ELEMENT_UV0)
        //$$     .put("UV1", DefaultVertexFormat.ELEMENT_UV1)
        //$$     .put("UV2", DefaultVertexFormat.ELEMENT_UV2)
        //$$     .build()
        //$$ );
        //#endif
}
