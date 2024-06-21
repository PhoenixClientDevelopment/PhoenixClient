package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.awt.*;

public class SpeedWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    public SettingGUI<String> mode;

    public SpeedWindow(Screen screen, Vector pos) {
        super(screen, "Speed", pos, Vector.NULL());
        this.label = new SettingGUI<>(this, "Label","Show the label",true);
        this.mode = new SettingGUI<>(this,"Mode","Detects whether speed is in XY or XYZ","2D","2D","3D");
        addSettings(label,mode);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        String label = (this.label.get() ? "Speed " : "");
        String currentSpeed = String.format("%.2f", getEntitySpeed(Minecraft.getInstance().player, mode.get().equals("3D"))) + "m/s";

        setSize(new Vector((int) DrawUtil.getTextWidth(label + currentSpeed) + 6,13));

        DrawUtil.drawDualColorText(graphics,label,currentSpeed,getPos().getAdded(new Vector(2,2)), ColorUtil.HUD_LABEL, Color.WHITE);
    }

    private double getEntitySpeed(Entity entity, boolean XYZ) {
        if (entity != null) {
            if (!XYZ) {
                double distTraveledLastTickX = entity.getX() - entity.xo;
                double distTraveledLastTickZ = entity.getZ() - entity.zo;
                double speed = Mth.sqrt((float) (distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ));
                return speed * 20;
            } else {
                double distTraveledLastTickX = entity.getX() - entity.xo;
                double distTraveledLastTickY = entity.getY() - entity.yo;
                double distTraveledLastTickZ = entity.getZ() - entity.zo;
                double speed = Mth.sqrt((float)(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ + distTraveledLastTickY * distTraveledLastTickY));
                return speed * 20;
            }
        }
        return 0;
    }
}
