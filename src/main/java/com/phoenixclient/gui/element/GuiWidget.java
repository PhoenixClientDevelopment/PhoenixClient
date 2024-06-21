package com.phoenixclient.gui.element;

import com.phoenixclient.gui.GUI;
import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.setting.Container;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public abstract class GuiWidget {

    protected final Color bgc = ColorUtil.getTheme().getBackgroundColor();

    private final Screen screen;

    protected Container<Vector> pos;
    protected Container<Vector> size;

    private boolean mouseOver;

    private boolean shouldDraw;
    private boolean drawTooltip;
    private boolean drawHoverFade;

    protected float hoverFade;

    public GuiWidget(Screen screen, Vector pos, Vector size, boolean shouldDraw) {
        this.screen = screen;
        this.pos = new Container<>(pos);
        this.size = new Container<>(size);
        this.shouldDraw = shouldDraw;
        this.drawTooltip = true;
        this.drawHoverFade = true;
        this.hoverFade = 0;
    }

    public void draw(GuiGraphics graphics, Vector mousePos) {
        this.mouseOver = updateMouseOver(mousePos);

        drawWidget(graphics, mousePos);

        if (drawHoverFade) drawHoverFade(graphics, mousePos);
        if (drawTooltip) drawTooltip(graphics,mousePos);
    }


    protected abstract void drawWidget(GuiGraphics graphics, Vector mousePos);

    public abstract void mousePressed(int button, int state, Vector mousePos);

    public abstract void keyPressed(int key, int scancode, int modifiers);


    public void runAnimation(int speed) {
        hoverFade = getNextFade(isMouseOver(), hoverFade, 0, 50, speed);
    }

    private void drawHoverFade(GuiGraphics graphics, Vector mousePos) {
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(255, 255, 255, (int) hoverFade));
    }

    private void drawTooltip(GuiGraphics graphics, Vector mousePos) {
        if (getSetting() != null && isMouseOver() && !(this instanceof GuiWindow)) {
            Vector pos = mousePos.getAdded(6, -8).clone();
            if (pos.getX() + DrawUtil.getTextWidth(getSetting().getDescription()) + 2 > MC.getWindow().getGuiScaledWidth())
                pos.setX(MC.getWindow().getGuiScaledWidth() - DrawUtil.getTextWidth(getSetting().getDescription()) - 2);

            DrawUtil.drawRectangleRound(graphics, pos, new Vector(DrawUtil.getTextWidth(getSetting().getDescription()) + 4, DrawUtil.getTextHeight() + 3), new Color(bgc.getRed(), bgc.getGreen(), bgc.getBlue(), bgc.getAlpha() * 2 / 3));
            DrawUtil.drawText(graphics, getSetting().getDescription(), pos.getAdded(2, 2), Color.WHITE);
        }
    }

    protected boolean updateMouseOver(Vector mousePos) {
        boolean mouseOverX = mousePos.getX() >= getPos().getX() && mousePos.getX() <= getPos().getX() + getSize().getX();
        boolean mouseOverY = mousePos.getY() >= getPos().getY() && mousePos.getY() <= getPos().getY() + getSize().getY();
        if (mouseOverX && mouseOverY) {
            if (getScreen() instanceof GUI gui) {
                gui.hoveredElements.add(this);
                return mouseOver = (!gui.prevHoveredElements.isEmpty() && gui.prevHoveredElements.getLast() == this);
            }
        }
        return mouseOver = false;
    }

    public float getNextFade(boolean condition, float fade, int min, int max, float speed) {
        if (condition) {
            if (fade < max) fade += speed;
        } else {
            if (fade > min) fade -= speed;
        }
        return MathUtil.getBoundValue(fade,0,255).floatValue();
    }


    public void setPos(Vector vector) {
        pos.set(vector);
    }

    public void setSize(Vector vector) {
        this.size.set(vector);
    }

    public void setDrawn(boolean drawn) {
        shouldDraw = drawn;
    }

    public void setHoverFadeVisible(boolean drawHoverFade) {
        this.drawHoverFade = drawHoverFade;
    }

    public void setTooltipVisible(boolean drawTooltip) {
        this.drawTooltip = drawTooltip;
    }


    public boolean isMouseOver() {
        return mouseOver;
    }

    public Vector getPos() {
        return pos.get();
    }

    public Vector getSize() {
        return size.get();
    }

    public boolean isDrawn() {
        return shouldDraw;
    }

    public Screen getScreen() {
        return screen;
    }

    public boolean shouldDrawSetting() {
        if (getSetting() == null) return true;
        if (getSetting() != null && getSetting().getDependency() == null) return true;
        return (getSetting().getDependency().setting().get().equals(getSetting().getDependency().value()));
    }

    public SettingGUI<?> getSetting() {
        return null;
    }

}
