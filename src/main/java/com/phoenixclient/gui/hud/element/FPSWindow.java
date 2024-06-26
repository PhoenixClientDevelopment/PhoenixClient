package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class FPSWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;

    public FPSWindow(Screen screen, Vector pos) {
        super(screen, "FPS", pos, Vector.NULL());
        this.label = new SettingGUI<>(this, "Label","Show the label",true);
        addSettings(label);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        String label = (this.label.get() ? "FPS " : "");
        String text =  (Minecraft.getInstance().fpsString.substring(0,3)).replace("/","").replace("f","").replace(" ","");

        setSize(new Vector((int) DrawUtil.getTextWidth(label + text) + 6,13));

        DrawUtil.drawDualColorText(graphics,label,text,getPos().getAdded(new Vector(2,2)), ColorUtil.HUD_LABEL, Color.WHITE);
    }

}
