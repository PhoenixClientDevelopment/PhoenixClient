package com.phoenixclient.gui.element;

import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class GuiModeCycle<T> extends GuiWidget {

    private final SettingGUI<T> setting;

    private Color baseColor;
    private Color colorR;
    private Color colorL;

    private int arrayNumber;

    public GuiModeCycle(Screen screen, SettingGUI<T> setting, Vector pos, Vector size, Color color) {
        super(screen, pos, size, true);
        this.setting = setting;
        this.baseColor = color;
        this.colorL = color;
        this.colorR = color;
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics,getPos(),getSize(), bgc);

        //Draw Mode Arrows
        DrawUtil.drawArrowHead(graphics,getPos().getAdded(2,2),(float)getSize().getY()-4,getColorL(),false,true);
        DrawUtil.drawArrowHead(graphics,getPos().getAdded(-2 + getSize().getX() - 3,2),(float)getSize().getY()-4,getColorR(),false,false);

        //Draw the text
        double scale = 1;
        String text = getSetting().getName() + ": " + getSetting().get();
        if (DrawUtil.getTextWidth(text) > getSize().getX()) scale = getSize().getX()/DrawUtil.getTextWidth(text);
        DrawUtil.drawText(graphics,text,getPos().getAdded(getSize().getX()/2 - DrawUtil.getTextWidth(text)/2,1 + getSize().getY()/2 - DrawUtil.getTextHeight()/2),Color.WHITE,true,(float)scale);
    }

    @Override
    public void mousePressed(int button, int action, Vector mousePos) {
        int RIGHT = 1;
        int LEFT = 0;
        if (isMouseOver()) {
            if (action == 0) {
                if (button == RIGHT) {
                    arrayNumber = getSetting().getModes().indexOf(getSetting().get());
                    arrayNumber += 1;
                    if (arrayNumber != getSetting().getModes().size()) {
                        getSetting().set(getSetting().getModes().get(arrayNumber));
                    } else {
                        getSetting().set(getSetting().getModes().get(0));
                        arrayNumber = 0;
                    }
                }
                if (button == LEFT) {
                    arrayNumber = getSetting().getModes().indexOf(getSetting().get());
                    arrayNumber -= 1;
                    if (arrayNumber != -1) {
                        getSetting().set(getSetting().getModes().get(arrayNumber));
                    } else {
                        getSetting().set(getSetting().getModes().get(getSetting().getModes().size() - 1));
                        arrayNumber = getSetting().getModes().size() - 1;
                    }
                }
            }
            if (action == Key.ACTION_PRESS) {
                int[] colVal = {getColor().getRed(),getColor().getGreen(),getColor().getBlue()};
                for (int i = 0; i < colVal.length; i++) {
                    int col = colVal[i] - 50;
                    col = MathUtil.getBoundValue(col,0,255).intValue();
                    colVal[i] = col;
                }
                if (button == RIGHT) setColorR(new Color(colVal[0],colVal[1],colVal[2],getColor().getAlpha()));
                if (button == LEFT) setColorL(new Color(colVal[0],colVal[1],colVal[2],getColor().getAlpha()));
            }
        }
        if (action == Key.ACTION_RELEASE) {
            if (button == RIGHT) setColorR(baseColor);
            if (button == LEFT) setColorL(baseColor);
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        //TODO: ADd a left/right scroll animation when changing modes
    }

    public void setColor(Color color) {
        this.baseColor = color;
    }

    private void setColorR(Color colorR) {
        this.colorR = colorR;
    }

    private void setColorL(Color colorL) {
        this.colorL = colorL;
    }


    public Color getColor() {
        return baseColor;
    }

    private Color getColorR() {
        return colorR;
    }

    private Color getColorL() {
        return colorL;
    }

    @Override
    public SettingGUI<T> getSetting() {
        return setting;
    }
}
