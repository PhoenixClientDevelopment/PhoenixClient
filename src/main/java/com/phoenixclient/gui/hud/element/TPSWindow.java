package com.phoenixclient.gui.hud.element;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.game.*;

import java.awt.*;

public class TPSWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    private final StopWatch tpsWatch = new StopWatch();
    private int ticks = 0;
    private int tps;

    public TPSWindow(Screen screen, Vector pos) {
        super(screen, "TPS", pos, Vector.NULL());
        this.label = new SettingGUI<>(this, "Label","Show the label",true);
        addSettings(label);
        packetEvent.subscribe();
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        String label = (this.label.get() ? "TPS " : "");
        String text = String.valueOf(tps);

        setSize(new Vector((int) DrawUtil.getTextWidth(label + text) + 6,13));

        DrawUtil.drawDualColorText(graphics,label,text,getPos().getAdded(new Vector(2,2)), ColorUtil.HUD_LABEL, Color.WHITE);
    }

    private final EventAction packetEvent = new EventAction(Event.EVENT_PACKET, () -> {
        PacketEvent event = Event.EVENT_PACKET;
        tpsWatch.run(20 * 1000, () -> {
            tps = ticks;
            ticks = 0;
        });
        if (event.getPacket() instanceof ClientboundSetTimePacket) {
            ticks++;
        }
    });
}
