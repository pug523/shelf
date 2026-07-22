package com.pug523.shelf.compat;

import net.minecraft.util.profiling.ProfilerFiller;

//#if MC >= 12102
import net.minecraft.util.profiling.Profiler;
//#else
//$$ import net.minecraft.client.Minecraft;
//#endif

public class ProfilerCompat {
    public static ProfilerFiller getProfiler() {
        //#if MC >= 12102
        return Profiler.get();
        //#else
        //$$ return Minecraft.getInstance().getProfiler();
        //#endif
    }
}
