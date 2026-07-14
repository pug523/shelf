package com.pug523.shelf.gui.renderer.shader;

//#if MC <= 12105
//$$ import com.mojang.blaze3d.pipeline.RenderPipeline;
//$$ import com.mojang.blaze3d.pipeline.RenderTarget;
//$$ import com.mojang.blaze3d.systems.RenderPass;
//$$ import com.mojang.blaze3d.vertex.MeshData;
//$$ import com.mojang.blaze3d.vertex.VertexFormat;
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import com.pug523.shelf.gui.renderer.state.SdfRenderState;
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
    //$$     super(name, parentToken.bufferSize(), parentToken.affectsCrumbling(), parentToken.sortOnUpload(),
    //$$         () -> {}, () -> {});
    //$$     this.parentToken = parentToken;
    //$$     this.sdfState = sdfState;
    //$$ }

    //$$ public SdfRenderState getSdfState() {
    //$$     return this.sdfState;
    //$$ }

    //$$ @Override
    //$$ public void draw(MeshData meshData) {
    //$$     this.parentToken.draw(meshData);
    //$$ }

    //$$ @Override
    //$$ public RenderTarget getRenderTarget() { return this.parentToken.getRenderTarget(); }
    //$$ @Override
    //$$ public RenderPipeline getRenderPipeline() { return this.parentToken.getRenderPipeline(); }
    //$$ @Override
    //$$ public VertexFormat format() { return this.parentToken.format(); }
    //$$ @Override
    //$$ public VertexFormat.Mode mode() { return this.parentToken.mode(); }

    //$$ @Override
    //$$ public boolean equals(Object obj) {
    //$$     if (this == obj) return true;
    //$$     if (!(obj instanceof SdfRenderType other)) return false;
    //$$     return this.sdfState.equals(other.sdfState);
    //$$ }

    //$$ @Override
    //$$ public int hashCode() {
    //$$     return this.sdfState.hashCode();
    //$$ }
    //#endif
}
