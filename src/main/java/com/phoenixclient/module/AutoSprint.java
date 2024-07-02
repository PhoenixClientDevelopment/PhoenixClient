package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoSprint extends Module {

    public AutoSprint() {
        super("AutoSprint", "Automatically sprints", Category.MOTION, false, -1);
        addEventActions(onPlayerUpdate);
    }

    private final EventAction onPlayerUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        if (MC.options.keyUp.isDown()) MC.player.setSprinting(true);
        if (MC.options.keyShift.isDown()) MC.player.setSprinting(false);
    });

    @Override
    public void onEnabled() {
    }

    @Override
    public void onDisabled() {
    }

}
