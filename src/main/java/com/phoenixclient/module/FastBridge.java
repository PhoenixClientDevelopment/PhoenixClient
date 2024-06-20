package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.phoenixclient.PhoenixClient.MC;

public class FastBridge extends Module {

    private final SettingGUI<Boolean> lockPitch = new SettingGUI<>(
            this,
            "LockPitch",
            "Locks the pitch of your player to 75 degrees",
            true
    );

    private float angleHold = 0;
    private final DoOnce setAngleHold = new DoOnce(false);
    private final DoOnce unPressLeft = new DoOnce(false);
    private final DoOnce unPressShift = new DoOnce(false);

    public FastBridge() {
        super("FastBridge", "Automatically shifts and aligns at edges to bridge", Category.MOTION, false, -1);
        addEventActions(onPlayerUpdate);
        addSettings(lockPitch);
    }

    private final EventAction onPlayerUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        if (MC.options.keyDown.isDown()) {
            if (MC.player.onGround()) {

                pressLeft();

                setAngleHold();

                float antiCheatOffset = (float) generateRandomNumber(0, 3) * (float) generateRandomNumber(-1, 1) / 100;

                if (lockPitch.get()) MC.player.setXRot(75 + antiCheatOffset);
                MC.player.setYRot(angleHold + antiCheatOffset);

                if (getBlockBelowEntity(MC.player).equals(Blocks.AIR))
                    pressShift();
                else
                    unPressShift();
            }
        } else {
            if (!getBlockBelowEntity(MC.player).equals(Blocks.AIR))
                unPressShift();
            unPressLeft();
            unSetAngleHold();
        }
    });

    @Override
    public void onEnabled() {
    }

    @Override
    public void onDisabled() {
        MC.options.keyShift.setDown(false);
        MC.options.keyLeft.setDown(false);
    }

    private void pressShift() {
        unPressShift.reset();
        MC.options.keyShift.setDown(true);
    }

    private void pressLeft() {
        unPressLeft.reset();
        MC.options.keyLeft.setDown(true);
    }

    private void unPressShift() {
        unPressShift.run(() -> MC.options.keyShift.setDown(false));
    }

    private void unPressLeft() {
        unPressLeft.run(() -> MC.options.keyLeft.setDown(false));
    }


    private void setAngleHold() {
        setAngleHold.run(() -> angleHold = switch (MC.player.getDirection()) {
            case NORTH -> 135F;
            case SOUTH -> -45F;
            case WEST -> 45F;
            case EAST -> -135F;
            default -> 0F;
        });
    }

    private void unSetAngleHold() {
        setAngleHold.reset();
    }


    private Block getBlockBelowEntity(Entity entity) {
        Vector vectorClient = new Vector(entity.getX() + (entity.getX() < 0 ? -1 : 0),entity.getY() - 1,entity.getZ() + (entity.getZ() < 0 ? -1 : 0));
        return (MC.level.getBlockState(vectorClient.getBlockPos()).getBlock());
    }

    private double generateRandomNumber(double min, double max) {
        return Math.floor(Math.random() * (max - min + 1) + min);
    }

}
