package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.hud.HUDGUI;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.setting.IParentGUI;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.setting.Container;
import com.phoenixclient.util.setting.Setting;
import com.phoenixclient.util.setting.SettingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.phoenixclient.PhoenixClient.MC;


//TODO: Lots of the methods in this class are dirty and are in need of cleaning
public abstract class GuiWindow extends GuiWidget implements IParentGUI {

    private final ArrayList<SettingGUI<?>> settingList;

    private final String title;

    private final Container<Boolean> isPinned;
    private final Container<Vector> posScale;
    private final Container<String> anchorX;
    private final Container<String> anchorY;

    private boolean isDragging;
    private Vector dragOffset;

    private double pinScale;

    private SettingsWindow settingsWindow;
    private boolean settingsOpen;

    public GuiWindow(Screen screen, String title, Vector pos, Vector size) {
        super(screen, pos, size, true);
        this.title = title;
        this.settingList = new ArrayList<>();
        this.settingsOpen = false;

        this.dragOffset = Vector.Null();

        this.pinScale = 0;

        if (this instanceof SettingsWindow) {
            this.isPinned = new Container<>(false);
            this.posScale = new Container<>(new Vector(.1, .1));
            this.anchorX = new Container<>("NONE");
            this.anchorY = new Container<>("NONE");
        } else {
            SettingManager manager = PhoenixClient.getSettingManager();
            this.isPinned = new Setting<>(manager, title + "_pinned", false);
            this.posScale = new Setting<>(manager, title + "_posScale", new Vector(.1, .1)); //this.pos = new Setting<>(manager, title + "_pos",pos);
            this.anchorX = new Setting<>(manager, title + "_anchorX", "NONE");
            this.anchorY = new Setting<>(manager, title + "_anchorY", "NONE");
        }
    }


    @Override
    public void drawWidget(GuiGraphics graphics, Vector mousePos) {
        if (!isSettingsWindow()) {
            updateWindowCoordinatesFromScale();
            updateAnchoredCoordinates();
        }

        if (Minecraft.getInstance().screen == PhoenixClient.getGuiManager().getHudGui()) {

            if (isDragging()) {
                setPos(mousePos.getAdded(getDragOffset()));
                posScale.set(getPos().getScaled((double) 1 / MC.getWindow().getGuiScaledWidth(), (double) 1 /MC.getWindow().getGuiScaledHeight()));
            }

            bindWindowCoordinates();

            drawAnchoredLines(graphics, mousePos);
        }

        graphics.setColor(1f,1f,1f,1f);
        drawWindow(graphics, mousePos);
        if (isSettingsOpen()) getSettingWindow().draw(graphics, mousePos);
        graphics.setColor(1f,1f,1f,1f);

        drawPin(graphics, mousePos);
    }

    protected abstract void drawWindow(GuiGraphics graphics, Vector mousePos);

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (button == 0 && !Key.KEY_LSHIFT.isKeyDown())
            updateAnchored(button, state, mousePos);

        if (state == 1 && isMouseOver()) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                updateDragState(button, state, mousePos);

                //NOTE: If the window is not focused, it will not be able to detect if shift is down
                if (!isSettingsOpen() && Key.KEY_LSHIFT.isKeyDown() && !isSettingsWindow())
                    openSettingWindow();
            }
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
                setPinned(!isPinned());
        }

        if (isSettingsOpen())
            getSettingWindow().mousePressed(button, state, mousePos);

        if (isDragging() && state == 0)
            setDragging(false);
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        if (isSettingsOpen()) getSettingWindow().keyPressed(key,scancode,modifiers);
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        if (getSettingWindow() != null) getSettingWindow().runAnimation(speed);
        if (isPinned()) {
            if (pinScale < 1) pinScale += .01 * speed;
            else pinScale = 1;
        } else {
            if (pinScale > 0) pinScale -= .01 * speed;
            else pinScale = 0;
        }
    }

    //TODO: Clean this code, its ugly and fat
    private void updateDragState(int button, int state, Vector mousePos) {
        //TODO: Maybe use a comparator or the comparable interface to streamline this
        this.dragOffset = getPos().getAdded(mousePos.getMultiplied(-1)).clone();
        ArrayList<GuiWidget> widgetList = (ArrayList<GuiWidget>) ((HUDGUI) getScreen()).getGuiElementList().clone();
        Collections.reverse(widgetList);
        ArrayList<GuiWindow> windowList = new ArrayList<>();
        for (GuiWidget widget : widgetList) {
            if (widget instanceof GuiWindow window) {
                if (window.getSettingWindow() != null) windowList.add(window.getSettingWindow());
                windowList.add(window);
            }
        }

        if (!Key.KEY_LSHIFT.isKeyDown()) {
            boolean shouldDrag = true;
            for (GuiWindow window : windowList) {
                if (window.isMouseOver()) {
                    if (!window.equals(this)) shouldDrag = false;
                    break;
                }
            }
            if (shouldDrag) setDragging(true);

        }
    }

    private void updateWindowCoordinatesFromScale() {
        //TODO: Right now, the pos is set at the corner from the scale, try and make it set the center instead for more consistency
        setPos(new Vector(MC.getWindow().getGuiScaledWidth(),MC.getWindow().getGuiScaledHeight()).getScaled(posScale.get()));
    }

    private void bindWindowCoordinates() {
        if (getPos().getX() < 0)
            setPos(new Vector(0, getPos().getY()));

        if (getPos().getY() < 0)
            setPos(new Vector(getPos().getX(), 0));

        if (getPos().getX() + getSize().getX() > getScreen().width)
            setPos(new Vector(getScreen().width - getSize().getX(), getPos().getY()));

        if (getPos().getY() + getSize().getY() > getScreen().height)
            setPos(new Vector(getPos().getX(), getScreen().height - getSize().getY()));
    }

    //This method will be called in the set position method every time the position is set
    public void updateAnchoredCoordinates() {
        if (anchorX.get().equals("NONE") && anchorY.get().equals("NONE")) return;

        //Set Anchor Coordinates
        if (anchorX.get().equals("L"))
            setPos(new Vector(0, getPos().getY()));
        else if (anchorX.get().equals("R"))
            setPos(new Vector(MC.getWindow().getGuiScaledWidth() - getSize().getX(), getPos().getY()));

        if (anchorY.get().equals("U"))
            setPos(new Vector(getPos().getX(), 0));
        else if (anchorY.get().equals("D"))
            setPos(new Vector(getPos().getX(), MC.getWindow().getGuiScaledHeight() - getSize().getY()));
    }

    private void updateAnchored(int button, int state, Vector mousePos) {
        if (isMouseOver() && state == 1) {
            anchorX.set("NONE");
            anchorY.set("NONE");
        } else {
            //Set if anchored
            if (getPos().getX() <= 0) {
                if (isDragging()) anchorX.set("L");
            } else if (getPos().getX() + getSize().getX() >= getScreen().width) {
                if (isDragging()) anchorX.set("R");
            }
            if (getPos().getY() <= 0) {
                if (isDragging()) anchorY.set("U");
            } else if (getPos().getY() + getSize().getY() >= getScreen().height) {
                if (isDragging()) anchorY.set("D");
            }
        }
    }

    private void drawAnchoredLines(GuiGraphics graphics, Vector mousePos) {
        switch (anchorX.get()) {
            case "L" -> DrawUtil.drawRectangle(graphics,getPos(),new Vector(1,getSize().getY()), Color.RED);
            case "R" -> DrawUtil.drawRectangle(graphics,getPos().getAdded(getSize().getX() - 1,0),new Vector(1,getSize().getY()),Color.RED);
        }

        switch (anchorY.get()) {
            case "U" -> DrawUtil.drawRectangle(graphics,getPos(),new Vector(getSize().getX(),1),Color.RED);
            case "D" -> DrawUtil.drawRectangle(graphics,getPos().getAdded(0,getSize().getY() - 1),new Vector(getSize().getX(),1),Color.RED);
        }
    }

    private void drawPin(GuiGraphics graphics, Vector mousePos) {
        if (Minecraft.getInstance().screen == PhoenixClient.getGuiManager().getHudGui() && shouldDrawPin()) {
            Color pinColor = ColorUtil.getRedGreenScaledColor(pinScale);//isPinned() ? new Color(0,255,0,125) : new Color(255,0,0,125);
            DrawUtil.drawRectangleRound(graphics, getPos(),new Vector(10,10), new Color(pinColor.getRed(),pinColor.getGreen(),pinColor.getBlue(),125));
        }
    }

    private void openSettingWindow() {
        this.settingsWindow = new SettingsWindow(getScreen(), this, new Vector(getScreen().width / 2f - 100 / 2f, getScreen().height / 2f - 100 / 2f)) {
            public boolean shouldDrawPin() {
                return false;
            }
        };
        setSettingsOpen(true);
    }

    @Override
    public void setSize(Vector vector) {
        super.setSize(vector);
        updateAnchoredCoordinates();
    }

    public void setPinned(boolean pinned) {
        isPinned.set(pinned);
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public void setSettingsOpen(boolean settingsOpen) {
        this.settingsOpen = settingsOpen;
        if (!settingsOpen && settingsWindow != null) this.settingsWindow = null;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPinned() {
        return isPinned.get();
    }

    public boolean isDragging() {
        return isDragging;
    }

    public Vector getDragOffset() {
        return dragOffset;
    }

    public boolean shouldDrawPin() {
        return true;
    }

    public boolean isSettingsOpen() {
        return settingsOpen;
    }

    public boolean isSettingsWindow() {
        return this instanceof SettingsWindow;
    }

    public SettingsWindow getSettingWindow() {
        return settingsWindow;
    }


    protected void addSettings(SettingGUI<?>... settings) {
        settingList.addAll(Arrays.asList(settings));
    }

    public ArrayList<SettingGUI<?>> getSettings() {
        return settingList;
    }


}