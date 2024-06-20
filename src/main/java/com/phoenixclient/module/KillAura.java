package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;

public class KillAura extends Module {

    private final SettingGUI<Double> range = new SettingGUI<>(
            this,
            "Range",
            "Attack Range of Kill Aura",
            4.5d,.5,5,.5);

    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT,false, -1);
        addEventActions(onPlayerUpdate);
        addSettings(range);
    }

    private final EventAction onPlayerUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        //still need to code this
    });

    @Override
    public void onEnabled() {
    }

    @Override
    public void onDisabled() {
    }

}
