package com.pug523.shelf.compat;

//#if MC >= 12105
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public class Matrix3x2fCompat {
    //#if MC >= 12105
    public final Matrix3x2fc pose;
    //#else
    //$$ public final PoseStack.Pose pose;
    //#endif

    // @formatter:off
    //#if MC >= 12105
    public Matrix3x2fCompat(Matrix3x2fc pose) {
    //#else
    //$$ public Matrix3x2fCompat(PoseStack.Pose pose) {
    //#endif
    // @formatter:on
        this.pose = pose;
    }

    //#if MC <= 12004
    //$$ public Matrix3x2fCompat(PoseStack poseStack) {
    //$$     this(poseStack.last());
    //$$ }
    //#endif

    public Matrix3x2fCompat copy() {
        return new Matrix3x2fCompat(pose);
    }

    // @formatter:off
    //#if MC >= 12105
    public static Matrix3x2fCompat copy(Matrix3x2fc src) {
    //#else
    //$$ public static Matrix3x2fCompat copy(PoseStack src) {
    //#endif
    // @formatter:on
        //#if MC >= 12106
        return new Matrix3x2fCompat(new Matrix3x2f(src));
        //#elseif MC >= 12105
        //$$ return new Matrix3x2fCompat(src.last().copy());
        //#else
        //$$ PoseStack.Pose p = src.last();
        //$$ return new Matrix3x2fCompat(new PoseStack.Pose(p.pose(), p.normal()));
        //#endif
    }
}
