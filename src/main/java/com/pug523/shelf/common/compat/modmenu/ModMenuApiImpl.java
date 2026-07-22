package com.pug523.shelf.compat.modmenu;

import com.pug523.shelf.ShelfConfigScreen;

//#if MC >= 11600
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
//#elseif MC >= 11500
//$$ import io.github.prospector.modmenu.api.ConfigScreenFactory;
//$$ import io.github.prospector.modmenu.api.ModMenuApi;
//#else
//$$ import com.pug523.shelf.Shelf;
//$$ import io.github.prospector.modmenu.api.ModMenuApi;
//$$ import java.util.function.Function;
//$$ import net.minecraft.client.gui.screens.Screen;
//#endif

public class ModMenuApiImpl implements ModMenuApi {
    //#if MC < 11500
    //$$ public String getModId() {
    //$$     return Shelf.MOD_ID;
    //$$ }
    //#endif

    @Override
    // @formatter:off
    //#if MC >= 11500
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
    //#else
    //$$ public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    //#endif
    // @formatter:on
        return ShelfConfigScreen::createConfigScreen;
    }
}
