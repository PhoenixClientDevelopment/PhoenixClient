package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.phoenixclient.PhoenixClient.MC;

public class EntityListWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    private final SettingGUI<Integer> range;
    private final SettingGUI<Double> scale;

    public EntityListWindow(Screen screen, Vector pos) {
        super(screen, "EntityList", pos, Vector.Null());
        this.label = new SettingGUI<>(this,"Label","Show the label",true);
        this.range = new SettingGUI<>(this,"Range","Block range away from player of entities",64,1,200,1);
        this.scale = new SettingGUI<>(this,"Scale","The scale of the list",1d,.25d,1d,.05d);
        addSettings(label,range,scale);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        assert MC.level != null;
        int yOff = 0;
        LinkedHashMap<String, Integer> entityNameList = new LinkedHashMap<>();
        for (Entity entity : MC.level.entitiesForRendering()) {
            if (entity.distanceTo(MC.player) > range.get()) continue;
            if (entity instanceof LocalPlayer) continue;
            String rawName = entity.getType().toShortString();
            String entityName = rawName.replaceFirst(String.valueOf(rawName.charAt(0)), String.valueOf(rawName.charAt(0)).toUpperCase()).replace("_"," ");
            if (entityNameList.containsKey(entityName)) {
                entityNameList.put(entityName,entityNameList.get(entityName) + 1);
                continue;
            }
            entityNameList.put(entityName,1);
        }

        if (label.get()) {
            DrawUtil.drawText(graphics,"Entity List:",getPos().getAdded(2,2), ColorUtil.HUD_LABEL);
            yOff += 12;
        }

        float scale = this.scale.get().floatValue();
        graphics.pose().scale(scale,scale,1f);
        for (Map.Entry<String, Integer> set : entityNameList.entrySet()) {
            DrawUtil.drawDualColorText(graphics,set.getKey(),(set.getValue() == 1) ? "" : "(" + set.getValue() + ")",getPos().getAdded(2,2 + yOff).getMultiplied(1 / scale), Color.WHITE,new Color(0,190,255));
            yOff += 12 * scale;
        }
        setSize(new Vector(72 * scale, yOff == 0 ? 12 : yOff));
        graphics.pose().scale(1 / scale,1 / scale,1f);
    }
}