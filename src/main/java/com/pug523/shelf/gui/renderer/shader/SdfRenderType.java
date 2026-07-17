package com.pug523.shelf.gui.renderer.shader;

//#if MC <= 12105
//$$ import com.mojang.blaze3d.vertex.VertexFormat;
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import com.pug523.shelf.gui.renderer.state.SdfRenderState;
//#if MC >= 12104
//$$ import com.mojang.blaze3d.pipeline.RenderTarget;
//$$ import com.mojang.blaze3d.pipeline.RenderPipeline;
//$$ import com.mojang.blaze3d.systems.RenderPass;
//#endif
//#if MC >= 12100
//$$ import com.mojang.blaze3d.vertex.MeshData;
//#else
//$$ import com.mojang.blaze3d.vertex.BufferBuilder;
//#if MC >= 12000
//$$ import com.mojang.blaze3d.vertex.VertexSorting;
//#endif
//#endif
//#endif

//#if MC >= 12106
public class SdfRenderType {
//#else
//$$ public class SdfRenderType extends RenderType {
//#endif
    //#if MC <= 12105
    //$$ private final RenderType parentToken;
    //$$ private final SdfRenderState sdfState;

    //$$ public SdfRenderType(String name, RenderType parentToken, SdfRenderState sdfState) {
    //$$     // Delegate buffer sizes and pipeline behaviors to the parent GUI render type
    //#if MC >= 12104
    //$$     super(name, parentToken.bufferSize(), parentToken.affectsCrumbling(), parentToken.sortOnUpload(),
    //$$         () -> {}, () -> {});
    //#elseif MC >= 12100
    //$$     super(name, parentToken.format(), parentToken.mode(), parentToken.bufferSize(), parentToken.affectsCrumbling(), parentToken.sortOnUpload(),
    //$$         () -> {}, () -> {});
    //#else
    //$$     super(name, parentToken.format(), parentToken.mode(), parentToken.bufferSize(), parentToken.affectsCrumbling(), parentToken.sortOnUpload,
    //$$         () -> {}, () -> {});
    //#endif
    //$$     this.parentToken = parentToken;
    //$$     this.sdfState = sdfState;
    //$$ }

    //$$ public SdfRenderState getSdfState() {
    //$$     return this.sdfState;
    //$$ }

    //#if MC >= 12100
    //$$ @Override
    //$$ public void draw(MeshData meshData) {
    //$$     this.parentToken.draw(meshData);
    //$$ }
    //#elseif MC >= 12000
    //$$ @Override
    //$$ public void end(BufferBuilder bufferBuilder, VertexSorting vertexSorting) {
    //$$     this.parentToken.end(bufferBuilder, vertexSorting);
    //$$ }
    //#else
    //$$ @Override
    //$$ public void end(BufferBuilder bufferBuilder, int i, int j, int k) {
    //$$     this.parentToken.end(bufferBuilder, i, j, k);
    //$$ }
    //#endif

    //#if MC >= 12104
    //$$ @Override
    //$$ public RenderTarget getRenderTarget() { return this.parentToken.getRenderTarget(); }
    //$$ @Override
    //$$ public RenderPipeline getRenderPipeline() { return this.parentToken.getRenderPipeline(); }
    //#endif
    //$$ @Override
    //$$ public VertexFormat format() { return this.parentToken.format(); }
    //$$ @Override
    //$$ public VertexFormat.Mode mode() { return this.parentToken.mode(); }

    //$$ @Override
    //$$ public boolean equals(Object obj) {
    //$$     if (this == obj) return true;
    //$$     if (!(obj instanceof SdfRenderType)) return false;
    //$$     SdfRenderType other = (SdfRenderType) obj;
    //$$     return this.sdfState.equals(other.sdfState);
    //$$ }

    //$$ @Override
    //$$ public int hashCode() {
    //$$     return this.sdfState.hashCode();
    //$$ }
    //#endif
}
