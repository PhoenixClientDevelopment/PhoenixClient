package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;

import com.phoenixclient.module.Module;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.render.ColorUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ModuleListWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    private final SettingGUI<Double> scale;

    public ModuleListWindow(Screen screen, Vector pos) {
        super(screen, "ModuleList", pos, Vector.Null());
        this.label = new SettingGUI<>(this,"Label","Show the label",true);
        this.scale = new SettingGUI<>(this,"Scale","The scale of the list",1d,.25d,1d,.05d);
        addSettings(label,scale);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        ArrayList<Module> sortedDisplayList = new ArrayList<>();
        for (Module module : PhoenixClient.getModules()) {
            if (module.isEnabled()) sortedDisplayList.add(module);
        }

        Module[] mods = sortedDisplayList.toArray(new Module[0]);
        Arrays.sort(mods, new ModuleComparator());
        sortedDisplayList = new ArrayList<>(Arrays.asList(mods));

        int yOff = 0;

        if (label.get()) {
            DrawUtil.drawText(graphics,"Module List:",getPos().getAdded(2,2), ColorUtil.HUD_LABEL);
            yOff += 12;
        }

        float scale = this.scale.get().floatValue();
        graphics.pose().scale(scale,scale,1f);
        for (Module module : sortedDisplayList) {
            String tag = module.getModTag().isEmpty() ? "" : "[" + module.getModTag() + "]";
            DrawUtil.drawDualColorText(graphics,module.getTitle(),tag,getPos().getAdded(2,2 + yOff).getMultiplied(1 / scale), ColorUtil.getTheme().getBaseColor(), Color.WHITE);
            yOff += 12 * scale;
        }
        setSize(new Vector(72 * scale, yOff == 0 ? 12 : yOff));
        graphics.pose().scale(1 / scale,1 / scale,1f);
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        //TODO: Add enable/disable animation
    }

    public static class ModuleComparator implements Comparator<Module> {

        @Override
        public int compare(Module module1, Module module2) {
            String tag1 = module1.getModTag().isEmpty() ? "" : "[" + module1.getModTag() + "]";
            String tag2 = module2.getModTag().isEmpty() ? "" : "[" + module2.getModTag() + "]";
            return (int)(DrawUtil.getTextWidth(module2.getTitle() + tag2) - DrawUtil.getTextWidth(module1.getTitle() + tag1));
        }

    }
}