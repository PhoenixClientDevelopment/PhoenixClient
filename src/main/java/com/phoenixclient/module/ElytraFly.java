package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.world.phys.Vec3;

import static com.phoenixclient.PhoenixClient.MC;

public class ElytraFly extends Module {

    private final SettingGUI<Double> speed = new SettingGUI<>(
            this,
            "Speed",
            "Speed of ElytaFly",
            1d)
            .setSliderData(.1,2,.1);

    public ElytraFly() {
        super("ElytraFly", "Allows control of the Elytra", Category.MOTION, false, -1);
        addEventActions(onPlayerUpdate);
        addSettings(speed);
    }

    private final EventAction onPlayerUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        if (MC.player.isFallFlying()) {

            double speed = this.speed.get() * (double) 1 / 10;

            Angle yaw = new Angle(MC.player.getRotationVector().y,true);
            Angle pitch = new Angle(MC.player.getRotationVector().x,true);

            if (MC.options.keyUp.isDown())
                MC.player.addDeltaMovement(new Vector(yaw,pitch,speed).getVec3());

            if (MC.options.keyDown.isDown())
                MC.player.addDeltaMovement(new Vector(yaw.getAdded(180,true),pitch,speed).getVec3());

            if (MC.options.keyLeft.isDown())
                MC.player.addDeltaMovement(new Vector(yaw.getAdded(-90,true),pitch,speed).getVec3());

            if (MC.options.keyRight.isDown())
                MC.player.addDeltaMovement(new Vector(yaw.getAdded(90,true),pitch,speed).getVec3());

            if (MC.options.keyJump.isDown())
                MC.player.addDeltaMovement(new Vec3(0, speed, 0));

            if (MC.options.keyShift.isDown())
                MC.player.addDeltaMovement(new Vec3(0, -speed, 0));
        }
    });

    @Override
    public void onEnabled() {
    }

    @Override
    public void onDisabled() {
    }

}
