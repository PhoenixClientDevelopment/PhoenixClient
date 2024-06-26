package com.phoenixclient;

import com.phoenixclient.gui.GuiManager;
import com.phoenixclient.module.*;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.RotationManager;
import com.phoenixclient.util.file.CSVFile;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.render.FontRenderer;
import com.phoenixclient.util.setting.SettingManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PhoenixClient implements ModInitializer {

    public static final Minecraft MC = Minecraft.getInstance();

    private static final SettingManager SETTING_MANAGER = new SettingManager(new CSVFile("PhoenixClient", "settings"));
    private static final GuiManager GUI_MANAGER = new GuiManager();
    private static final RotationManager ROTATION_MANAGER = new RotationManager();
    private static final FontRenderer FONT_RENDERER = new FontRenderer("Segoe Print", Font.PLAIN);

    private static final LinkedHashMap<String,Module> MODULES_LIST = new LinkedHashMap<>(); //Key value pair: ModName, Module


    @Override
    public void onInitialize() {
        addModules(
                new AutoFish(),
                new FullBright(),
                new ElytraFly(),
                new KillAura(),
                new AutoSwing(),
                new YawLock(),
                new BoatMod(),
                new AutoWalk(),
                new AutoSprint(),
                new FastBridge(),
                new FreeCam(),
                new AntiRender(),
                new Step(),
                //new Speed(),
                new ShulkerView()
        );

        getGuiManager().instantiateHUDGUI();
        getGuiManager().instantiateModuleGUI();

        getSettingManager().loadAll();

        Key.KEY_PRESS_ACTION.subscribe();
        Mouse.MOUSE_CLICK_ACTION.subscribe();

		Module.MODULE_KEYBIND_ACTION.subscribe();

        getGuiManager().guiOpenAction.subscribe();
        getGuiManager().renderHudAction.subscribe();

        //I'm worried this causes a performance impact.
        //I've moved the animation calculations back to the render thread, which is now dependent on FPS
        //getGuiManager().startAnimationThread();

        //Toggle all mods that are saved as "Enabled"
        for (Module module : getModules()) if (module.isEnabled()) module.enable();
    }

    public static Module getModule(String modName) {
        return MODULES_LIST.get(modName);
    }

    public static void addModules(Module... modules) {
        ArrayList<Module> moduleList = new ArrayList<>(List.of(modules));
        Collections.sort(moduleList);
        for (Module module : moduleList) MODULES_LIST.put(module.getTitle(),module);
    }


    public static SettingManager getSettingManager() {
        return SETTING_MANAGER;
    }

    public static GuiManager getGuiManager() {
        return GUI_MANAGER;
    }

    public static RotationManager getRotationManager() {
        return ROTATION_MANAGER;
    }

    public static FontRenderer getFontRenderer() {
        return FONT_RENDERER;
    }

    public static Collection<Module> getModules() {
        return MODULES_LIST.values();
    }

}
