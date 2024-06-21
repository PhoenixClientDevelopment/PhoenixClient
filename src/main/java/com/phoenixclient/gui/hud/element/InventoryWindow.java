package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.List;

import static com.phoenixclient.PhoenixClient.MC;

public class InventoryWindow extends GuiWindow {

    public SettingGUI<Integer> transparency;
    public SettingGUI<Double> scale;

    public InventoryWindow(Screen screen, Vector pos) {
        super(screen,"Inventory" ,pos, new Vector(178,82));
        transparency = new SettingGUI<>(this,"Transparency","The transparency of the inventory window",125).setSliderData(0,255,1);
        scale = new SettingGUI<>(this,"Scale","The scale of the inventory window",1d).setSliderData(.25d,1d,.05d);
        addSettings(transparency,scale);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        float scale = this.scale.get().floatValue();
        setSize(new Vector(178,82).getMultiplied(scale));

        graphics.pose().scale(scale,scale,1f);

        Vector adjustedPos = getPos().getAdded(1,1).getMultiplied(1/scale);

        graphics.setColor(1,1,1,transparency.get() / 255f);
        renderChestInventory(graphics,"Inventory",adjustedPos);

        //graphics.setColor(1,1,1,transparency.get() / 255f);
        renderInventoryItems(graphics, adjustedPos);

        graphics.setColor(1f,1f,1f,1f);
        graphics.pose().scale(1 / scale,1 / scale,1f);
    }

    private void renderInventoryItems(GuiGraphics graphics, Vector pos) {
        List<ItemStack> list = MC.player.inventoryMenu.getItems();
        int itemSize = 16;

        int i = (list.size() % 9 == 0) ? 0 : 1 + list.size()/9;
        int size = list.size();
        for (int l = 0; l < size; l++) {
            if (l > 8 && l < 36) {
                Vector pos2 = new Vector(
                        (pos.getX() + 8) + ((itemSize + 2) * (l % 9)),
                        (pos.getY() - 126) + (itemSize + 2) * (l / 9 + 1) + (itemSize + 2) * i);

                DrawUtil.drawItemStack(graphics,list.get(l),pos2);
            }
        }
    }

    private void renderChestInventory(GuiGraphics graphics, String title, Vector pos) {
        //INFO: Width = 176, Height = 80
        double x = pos.getX();
        double y = pos.getY();
        ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
        RenderSystem.enableBlend();
        DrawUtil.drawTexturedRect(graphics,GUI_TEXTURE,pos.getAdded(new Vector(0,74)),new Vector(176,6),new Vector(0,160), new Vector(256,256));
        DrawUtil.drawTexturedRect(graphics,GUI_TEXTURE,pos,new Vector(176,74),Vector.NULL(), new Vector(256,256));
        DrawUtil.drawText(graphics, title, new Vector(x + 7, y + 5), new Color(175, 175, 175, 220));
        RenderSystem.disableBlend();
    }

}
