package com.phoenixclient.module;

import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;

public class AntiRender extends Module {

    private final SettingGUI<Boolean> noBob = new SettingGUI<>(
            this,
            "No Bob",
            "Stops bobbing while walking",
            true
    );

    private final SettingGUI<Boolean> noHurtCam = new SettingGUI<>(
            this,
            "No Hurt Cam",
            "Stops camera tilting on hurt",
            true
    );

    private final SettingGUI<Boolean> noConfusion = new SettingGUI<>(
            this,
            "No Confusion",
            "Stops the confusion effect - Currently Unimplemented",
            true
    );

    private final SettingGUI<Boolean> noFireOverlay = new SettingGUI<>(
            this,
            "No Fire",
            "Stops the Fire Overlay",
            true
    );

    public AntiRender() {
        super("AntiRender", "Disables rendering of certain things", Category.RENDER, false, -1);
        addEventActions(onPlayerUpdate);
        addSettings(noBob,noHurtCam,noConfusion,noFireOverlay);
    }

    private final EventAction onPlayerUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        noBob.runOnChange(() -> MixinHooks.noCameraBob = noBob.get());
        noHurtCam.runOnChange(() -> MixinHooks.noHurtCam = noHurtCam.get());
        noConfusion.runOnChange(() -> MixinHooks.noConfusion = noConfusion.get()); //TODO: Implement noConfusion
        noFireOverlay.runOnChange(() -> MixinHooks.noFireHud = noFireOverlay.get());
    });

    @Override
    public void onEnabled() {
        for (SettingGUI<?> setting : getSettings()) setting.resetOnChange();
    }

    @Override
    public void onDisabled() {
        MixinHooks.noCameraBob = false;
        MixinHooks.noHurtCam = false;
        MixinHooks.noConfusion = false;
        MixinHooks.noFireHud = false;
    }

}
