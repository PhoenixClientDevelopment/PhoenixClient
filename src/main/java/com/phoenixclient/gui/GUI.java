package com.phoenixclient.gui;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.module.ModuleGUI;
import com.phoenixclient.gui.module.element.ModuleMenu;
import com.phoenixclient.gui.module.element.ModuleOptionsMenu;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.gui.element.GuiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.phoenixclient.PhoenixClient.MC;

public class GUI extends Screen {

    private final ArrayList<GuiWidget> guiElementList = new ArrayList<>();

    public GUI(Component title) {
        super(title);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Vector mousePos = new Vector(mouseX, mouseY);

        //DRAW BACKGROUND
        MC.gameRenderer.processBlurEffect(200);
        MC.getMainRenderTarget().bindWrite(false);
        DrawUtil.drawRectangle(guiGraphics, Vector.Null(), getSize(), new Color(0, 0, 0, 100));
        guiGraphics.setColor(1f,1f,1f,1f);

        //DRAW ALL ELEMENTS
        for (GuiWidget element : getGuiElementList()) {
            if (!element.isDrawn()) continue;
            element.draw(guiGraphics, mousePos);
        }
    }

    @Override
    public boolean keyPressed(int key, int scancode, int modifiers) {
        boolean ret = super.keyPressed(key,scancode,modifiers);
        for (GuiWidget element : getGuiElementList()) {
            if (element.isDrawn()) {
                element.keyPressed(key,scancode,modifiers);
            }
        }
        return ret;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        Vector mousePos = new Vector(x,y);
        for (GuiWidget element : getGuiElementList()) {
            if (element.isDrawn()) {
                element.mousePressed(button,1,mousePos);
            }
        }
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        Vector mousePos = new Vector(x,y);
        for (GuiWidget element : getGuiElementList()) {
            if (element.isDrawn()) {
                element.mousePressed(button,0,mousePos);
            }
        }
        return super.mouseReleased(x, y, button);
    }

    public void toggleOpen() {
        boolean active = MC.screen == this;
        boolean isOtherActive = false; //Allows the GUI to be opened while in the other
        if (this == PhoenixClient.getGuiManager().getModuleGui()) isOtherActive = MC.screen == PhoenixClient.getGuiManager().getHudGui();
        if (this == PhoenixClient.getGuiManager().getHudGui()) isOtherActive = MC.screen == PhoenixClient.getGuiManager().getModuleGui();

        if (MC.screen == null || isOtherActive) {
            for (GuiWidget widget : getGuiElementList()) {
                if (widget instanceof ModuleMenu sw) sw.setScaling(.1f);
                if (widget instanceof ModuleOptionsMenu mow) mow.setScaling(0f);
            }
            MC.setScreen(this);

        } else if (active) {
            PhoenixClient.getSettingManager().saveAll(); //Whenever the GUI closes, save all settings
            Minecraft.getInstance().setScreen(null);
        }
    }


    public void addGuiElements(GuiWidget... elements) {
        guiElementList.addAll(Arrays.asList(elements));
    }


    public Vector getSize() {
        return new Vector(this.width,this.height);
    }

    public ArrayList<GuiWidget> getGuiElementList() {
        return guiElementList;
    }

}