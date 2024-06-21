package com.phoenixclient.gui.hud;

import com.phoenixclient.gui.GUI;
import com.phoenixclient.gui.hud.element.*;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class HUDGUI extends GUI {

    // DEFINE WINDOWS
    // -----------------------------------------------------------------

    private final ArmorWindow armor = new ArmorWindow(this,new Vector(0,0));

    private final CoordinatesWindow coordinates = new CoordinatesWindow(this,new Vector(10,0));

    private final DirectionWindow direction = new DirectionWindow(this,new Vector(20,0));

    private final EntityListWindow entityList = new EntityListWindow(this,new Vector(70,0));

    private final EntityDataWindow entityData = new EntityDataWindow(this,new Vector(80,0));

    private final FPSWindow fps = new FPSWindow(this,new Vector(30,0));

    private final InventoryWindow inventory = new InventoryWindow(this,new Vector(40,0));

    private final SpeedWindow speed = new SpeedWindow(this,new Vector(50,0));

    private final TPSWindow tps = new TPSWindow(this,new Vector(60,0));

    private final ModuleListWindow moduleList = new ModuleListWindow(this, new Vector(70, 0));

    //private final PacketInflowWindow packetInflowWindow = new PacketInflowWindow(this,new Vector(80,0));

    // -----------------------------------------------------------------

    public HUDGUI(Component title) {
        super(title);
        addGuiElements(inventory,armor,direction,fps,speed,coordinates, entityList, entityData, tps, moduleList);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        drawHintText(guiGraphics, "Shift Click a window to see options!");
    }

}
