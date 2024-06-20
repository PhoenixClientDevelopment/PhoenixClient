package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoWalk extends Module {

    public AutoWalk() {
        super("AutoWalk", "Automatically walks forward", Category.MOTION, false, -1);
        addEventActions(onPlayerUpdate);
    }

    private final EventAction onPlayerUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        MC.options.keyUp.setDown(true);
    });

    @Override
    public void onEnabled() {}

    @Override
    public void onDisabled() {
        MC.options.keyUp.setDown(false);
    }

}
