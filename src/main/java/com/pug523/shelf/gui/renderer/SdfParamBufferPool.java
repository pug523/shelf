package com.pug523.shelf.gui.renderer;

//#if MC >= 12106

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
//#endif

// Ring buffer
public class SdfParamBufferPool {
    //#if MC >= 12106
    private static final int USAGE = GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_UNIFORM;
    public static final int ELEMENT_SIZE = new Std140SizeCalculator().putVec4().get();

    private static final int INITIAL_ELEMENTS = 64;
    private static final int MAX_ELEMENTS = 2048;

    private static GpuBuffer buffer;
    private static int currentCapacity = INITIAL_ELEMENTS;
    private static int offset = 0;
    private static int cachedAlignment = -1;

    public static GpuBufferSlice allocate() {
        if (cachedAlignment == -1) {
            try {
                //#if MC >= 260200
                cachedAlignment = RenderSystem.getDevice().getDeviceInfo().limits().minUniformOffsetAlignment();
                //#else
                //$$ cachedAlignment = RenderSystem.getDevice().getUniformOffsetAlignment();
                //#endif
            } catch (Exception e) {
                cachedAlignment = 256;
            }
            cachedAlignment = Math.max(1, cachedAlignment);
        }

        int alignedElementSize = (ELEMENT_SIZE + cachedAlignment - 1) & -cachedAlignment;

        if (offset + ELEMENT_SIZE > currentCapacity * alignedElementSize) {
            int newCapacity = Math.min(currentCapacity * 2, MAX_ELEMENTS);

            if (newCapacity > currentCapacity) {
                reallocateBuffer(newCapacity, alignedElementSize);
            } else {
                offset = 0;
            }
        }

        if (buffer == null || buffer.isClosed()) {
            reallocateBuffer(currentCapacity, alignedElementSize);
        }

        offset = (offset + cachedAlignment - 1) & -cachedAlignment;

        GpuBufferSlice slice = buffer.slice(offset, ELEMENT_SIZE);
        offset += alignedElementSize;

        return slice;
    }

    private static void reallocateBuffer(int newCapacity, int alignedElementSize) {
        int newBufferSize = newCapacity * alignedElementSize;
        GpuBuffer oldBuffer = buffer;

        buffer = RenderSystem.getDevice().createBuffer(
            () -> "SDF Params Dynamic Buffer (Size: " + newCapacity + ")",
            USAGE,
            newBufferSize
        );
        currentCapacity = newCapacity;

        if (oldBuffer != null && !oldBuffer.isClosed()) {
            oldBuffer.close();
        }

        offset = 0;
    }

    public static void tryShrink() {
        int alignedElementSize = (ELEMENT_SIZE + cachedAlignment - 1) & -cachedAlignment;
        if (currentCapacity > INITIAL_ELEMENTS && offset < (INITIAL_ELEMENTS / 2) * alignedElementSize) {
            reallocateBuffer(INITIAL_ELEMENTS, alignedElementSize);
        }
        offset = 0;
    }
    //#endif
}
