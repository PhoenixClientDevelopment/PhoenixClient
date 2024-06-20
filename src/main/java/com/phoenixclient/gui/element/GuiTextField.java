package com.phoenixclient.gui.element;

import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

//TODO: This is ugly, clean it up
public class GuiTextField extends GuiWidget {

    private final SettingGUI<String> setting;
    private final String title;

    private final StopWatch cursorTimer = new StopWatch();
    private boolean typing;


    private GuiTextField(Screen screen, String title, SettingGUI<String> setting, Vector pos, Vector size) {
        super(screen, pos, size, true);
        this.setting = setting;
        this.title = title;

        this.typing = false;
    }

    public GuiTextField(Screen screen, SettingGUI<String> setting, Vector pos, Vector size) {
        this(screen,setting.getName(),setting,pos,size);
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics,getPos(), getSize(), bgc);

        //Draw Underline
        DrawUtil.drawRectangle(graphics,
                getPos().getAdded(new Vector(2 + DrawUtil.getTextWidth(getTitle() + ": "), getSize().getY()- 2)),
                getSize().getAdded(new Vector(- 4 - DrawUtil.getTextWidth(getTitle() + ": "),1-getSize().getY())),
                Color.WHITE);

        String title = getTitle() + ": ";
        String msg = getSetting().get();

        //Draw Blinking Cursor
        if (isTyping()) {
            cursorTimer.start();
            if (cursorTimer.hasTimePassedS(1)) cursorTimer.restart();
            int alpha = cursorTimer.hasTimePassedMS(500) ? 255 : 0;
            double x = (DrawUtil.getTextWidth(title.concat(msg)) > getSize().getX() - 4)
                    ? getPos().getX() + getSize().getX() - 1
                    : (getPos().getX() + 3 + DrawUtil.getTextWidth(getTitle() + ": ") + DrawUtil.getTextWidth(getSetting().get()));
            DrawUtil.drawRectangle(graphics, new Vector(x,getPos().getY() + 3),new Vector(1,getSize().getY() - 6),new Color(255,255,255,alpha));
        }

        //Draw Text
        double scale = 1;
        if (DrawUtil.getTextWidth(title.concat(msg)) + 2 > getSize().getX()) scale = getSize().getX()/(DrawUtil.getTextWidth(title.concat(msg)) + 2);
        DrawUtil.drawDualColorText(graphics,title,msg,getPos().getAdded(new Vector(2, 1 + getSize().getY() / 2 - DrawUtil.getTextHeight() / 2)), Color.WHITE,isTyping() ? Color.GREEN : Color.WHITE,true,1,(float)scale);
    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (button == 0 && state == 1) {
            if (isTyping()) setTyping(false);
            if (isMouseOver()) setTyping(true);
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        String buttonText = getSetting().get();
        try {
            if (isTyping()) {
                if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_ENTER) {
                    setTyping(false);
                } else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                    if (buttonText.length() > 0) buttonText = buttonText.substring(0, buttonText.length() - 1);
                } else if (key == GLFW.GLFW_KEY_SPACE) {
                    if (isValidPress(getSetting(), key)) buttonText = buttonText + " ";
                } else if (!GLFW.glfwGetKeyName(key, -1).equals("null")) {
                    if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == 1 || GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT) == 1) {
                        buttonText = buttonText + GLFW.glfwGetKeyName(key, -1).toUpperCase();
                    } else {
                        if (isValidPress(getSetting(), key)) buttonText = buttonText + GLFW.glfwGetKeyName(key, -1);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        getSetting().set(buttonText);
    }

    public static boolean isValidPress(Setting<?> setting, int key) {
        if (setting instanceof SettingGUI<?> settingw && settingw.isNumbersOnly()) {
            return key == GLFW.GLFW_KEY_PERIOD ||
                    key == GLFW.GLFW_KEY_KP_SUBTRACT ||
                    key == GLFW.GLFW_KEY_MINUS ||
                    key == GLFW.GLFW_KEY_0 ||
                    key == GLFW.GLFW_KEY_1 ||
                    key == GLFW.GLFW_KEY_2 ||
                    key == GLFW.GLFW_KEY_3 ||
                    key == GLFW.GLFW_KEY_4 ||
                    key == GLFW.GLFW_KEY_5 ||
                    key == GLFW.GLFW_KEY_6 ||
                    key == GLFW.GLFW_KEY_7 ||
                    key == GLFW.GLFW_KEY_8 ||
                    key == GLFW.GLFW_KEY_9 ||
                    key == GLFW.GLFW_KEY_KP_0 ||
                    key == GLFW.GLFW_KEY_KP_1 ||
                    key == GLFW.GLFW_KEY_KP_2 ||
                    key == GLFW.GLFW_KEY_KP_3 ||
                    key == GLFW.GLFW_KEY_KP_4 ||
                    key == GLFW.GLFW_KEY_KP_5 ||
                    key == GLFW.GLFW_KEY_KP_6 ||
                    key == GLFW.GLFW_KEY_KP_7 ||
                    key == GLFW.GLFW_KEY_KP_8 ||
                    key == GLFW.GLFW_KEY_KP_9;
        }
        return true;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }


    public boolean isTyping() {
        return typing;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public SettingGUI<String> getSetting() {
        return setting;
    }

}
