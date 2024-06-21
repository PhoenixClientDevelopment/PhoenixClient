package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

import static com.phoenixclient.PhoenixClient.MC;

public class BoatMod extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of BoatMod",
            "Jump","Jump","Fly");

    private final SettingGUI<Double> power = new SettingGUI<>(
            this,
            "Power",
            "Jump Power",
            1d,.1,2,.1).setSettingDependency(mode,"Jump");

    private final SettingGUI<Double> speed = new SettingGUI<>(
            this,
            "Speed",
            "Fly Speed",
            1d,.1,2,.1).setSettingDependency(mode,"Fly");

    private final SettingGUI<Boolean> yawlock = new SettingGUI<>(
            this,
            "Yaw Lock",
            "Have the boat lock its YAW to the player's",
            false);

    public BoatMod() {
        super("BoatMod", "Allows for different boat movement types", Category.MOTION, false, -1);
        addEventActions(onPlayerUpdate);
        addSettings(mode,power, yawlock,speed);
    }

    private final EventAction onPlayerUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        if (MC.player.getVehicle() != null && MC.player.getVehicle() instanceof Boat boat) {

            if (yawlock.get()) MC.player.getVehicle().setYRot(MC.player.getYRot());

            switch (mode.get()) {
                case "Jump" -> {
                    if (boat.onGround() && MC.options.keyJump.isDown()) boat.addDeltaMovement(new Vec3(0,power.get() * 1/3,0));
                }
                case "Fly" -> {
                    //TODO: Add Fly
                }
            }

        }
    });

    @Override
    public void onEnabled() {
    }

    @Override
    public void onDisabled() {
    }

}
