package com.phoenixclient.gui.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.GUI;
import com.phoenixclient.gui.module.element.ModuleOptionsMenu;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.gui.module.element.ModuleMenu;
import com.phoenixclient.module.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class ModuleGUI extends GUI {

    public final ModuleOptionsMenu moduleOptionsMenu = new ModuleOptionsMenu(this, Vector.Null());

    private final ModuleMenu combatMenu = new ModuleMenu(this, Module.Category.COMBAT,new Vector(2,32 + 52 * 0));
    private final ModuleMenu playerMenu = new ModuleMenu(this, Module.Category.PLAYER,new Vector(2,32 + 52 * 1));
    private final ModuleMenu movementMenu = new ModuleMenu(this, Module.Category.MOTION,new Vector(2,32 + 52 * 2));
    private final ModuleMenu renderMenu = new ModuleMenu(this, Module.Category.RENDER,new Vector(2,32 + 52 * 3));
    private final ModuleMenu serverMenu = new ModuleMenu(this, Module.Category.SERVER,new Vector(2,32 + 52 * 4));

    public ModuleGUI(Component title) {
        super(title);
        addGuiElements(combatMenu,playerMenu, movementMenu, renderMenu,serverMenu,moduleOptionsMenu);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        drawHintText(guiGraphics);
    }

    private double hintFade = 0;
    private boolean hintFadeIn = true;

    private void drawHintText(GuiGraphics guiGraphics) {
        String txt = "Press " + GLFW.glfwGetKeyName(PhoenixClient.getGuiManager().hudGuiOpenKey.get(),-1).toUpperCase() + " to open the HUD menu!";

        if (hintFade <= 50) {
            hintFadeIn = true;
            hintFade = 50;
        }
        if (hintFade >= 255) {
            hintFadeIn = false;
            hintFade = 255;
        }

        if (hintFadeIn) hintFade += 3;
        else hintFade -= 3;

        DrawUtil.drawText(guiGraphics,txt,new Vector((double) MC.getWindow().getGuiScaledWidth() / 2 - DrawUtil.getTextWidth(txt) / 2, MC.getWindow().getGuiScaledHeight() - 14), new Color(255,255,255, MathUtil.getBoundValue(hintFade,0,255).intValue()));

    }
}
