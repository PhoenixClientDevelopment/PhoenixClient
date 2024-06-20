package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: Rename this BoatMod, Have boat strafing and fly in here

public class BoatJump extends Module {

    private final SettingGUI<Double> power = new SettingGUI<>(
            this,
            "Power",
            "Jump Power",
            1d,.1,2,.1);

    public BoatJump() {
        super("BoatJump", "Allows for boats to jump", Category.MOTION, false, -1);
        addEventActions(onPlayerUpdate);
        addSettings(power);
    }

    private final EventAction onPlayerUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        if (MC.player.getVehicle() != null && MC.player.getVehicle() instanceof Boat boat) {
            if (boat.onGround() && MC.options.keyJump.isDown()) boat.addDeltaMovement(new Vec3(0,power.get() * 1/3,0));
        }
    });

    @Override
    public void onEnabled() {
    }

    @Override
    public void onDisabled() {
    }

}
