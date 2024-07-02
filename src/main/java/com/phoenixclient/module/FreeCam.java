package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameType;

import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

public class FreeCam extends Module {

    private final OnChange<Vector> onChangeView = new OnChange<>();
    private AbstractClientPlayer dummyPlayer;
    private GameType gameMode;
    private double swingAnimationTime = 3;
    private ServerboundMovePlayerPacket interactRotationPacket = null;

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of FreeCam",
            "Ghost")
            .setModeData("Ghost", "Interact");

    public FreeCam() {
        super("FreeCam", "Allows the camera to move out of the body", Category.PLAYER, false, -1);
        addEventActions(onPacket, onUpdate);
        addSettings(mode);
    }

    private final EventAction onUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        if (dummyPlayer != null) {
            updateDummyPlayerInventory();
            switch (mode.get()) {
                case "Ghost" -> {
                    //Nothing Extra Yet ¯\_(ツ)_/¯
                }
                case "Interact" -> {
                    MC.player.getAbilities().flying = true;
                    updateDummyPlayerSwinging();
                    updateDummyPlayerLookVector();
                }
            }
        }
        mode.runOnChange(this::disable);
    });

    //It is safe to cancel these packets. Anti-cheat will not detect. If the player is naturally not moving, it won't send these packets
    private final EventAction onPacket = new EventAction(Event.EVENT_PACKET, () -> {
        Packet<?> packet = Event.EVENT_PACKET.getPacket();
        if (packet instanceof ServerboundMovePlayerPacket.Rot
                || packet instanceof ServerboundMovePlayerPacket.PosRot
                || packet instanceof ServerboundMovePlayerPacket.Pos
                || packet instanceof ServerboundAcceptTeleportationPacket
                || packet instanceof ServerboundPlayerInputPacket
                || packet instanceof ClientboundPlayerLookAtPacket
                || packet instanceof ClientboundPlayerPositionPacket
        ) {
            if (!packet.equals(interactRotationPacket)) Event.EVENT_PACKET.setCancelled(true);
        }
    });

    @Override
    public void onEnabled() {
        //Disable the mod if there is no player
        if (MC.player == null) {
            disable();
            for (EventAction action : getEventActions()) action.unsubscribe();
            return;
        }
        //Set Mode Change Detector off to reset detector
        mode.runOnChange(() -> {});

        //Log Original Game Mode
        gameMode = MC.gameMode.getPlayerMode();

        //Set Mode Data
        switch (mode.get()) {
            case "Ghost" -> {
                MC.gameMode.setLocalMode(GameType.SPECTATOR);
                MC.player.onGameModeChanged(GameType.SPECTATOR);
                //Make it so you cannot attack entities here
            }
            case "Interact" -> {
                //TODO: Find a way to make block mining faster while flying
                MC.smartCull = false;
                MixinHooks.noClip = true;
                MixinHooks.noSuffocationHud = true;
            }
        }

        summonDummyPlayer();
    }

    @Override
    public void onDisabled() {
        if (MC.player == null || dummyPlayer == null) return;

        dummyPlayer.remove(Entity.RemovalReason.KILLED);

        resetPlayerData();

        MC.smartCull = true;
        MixinHooks.noClip = false;
        MixinHooks.noSuffocationHud = false;
    }


    private void summonDummyPlayer() {
        MC.level.addEntity(dummyPlayer = new AbstractClientPlayer(MC.level,MC.player.getGameProfile()) {
            public boolean isSpectator() {
                return false;
            }
            public UUID getUUID() {
                return UUID.randomUUID();
            }
            protected PlayerInfo getPlayerInfo() {
                return MC.getConnection().getPlayerInfo(MC.player.getUUID());
            }
        });
        dummyPlayer.setPos(MC.player.getPosition(0));
        dummyPlayer.setYRot(MC.player.getYRot());
        dummyPlayer.setYBodyRot(MC.player.getYRot());
        dummyPlayer.setYHeadRot(MC.player.getYHeadRot());
        dummyPlayer.setXRot(MC.player.getXRot());
        dummyPlayer.getAbilities().flying = MC.player.getAbilities().flying;
        if (MC.player.isFallFlying()) dummyPlayer.startFallFlying();
    }

    private void updateDummyPlayerSwinging() {
        if (MC.player.swinging) dummyPlayer.swing(InteractionHand.MAIN_HAND);
        if (dummyPlayer.swinging) {
            swingAnimationTime += .125;
            if (swingAnimationTime >= 1) {
                swingAnimationTime = 0;
                dummyPlayer.swinging = false;
            }
        } else {
            swingAnimationTime = 0;
        }
        dummyPlayer.attackAnim = (float) swingAnimationTime;
    }

    private void updateDummyPlayerInventory() {
        dummyPlayer.setItemSlot(EquipmentSlot.HEAD, MC.player.getItemBySlot(EquipmentSlot.HEAD));
        dummyPlayer.setItemSlot(EquipmentSlot.CHEST, MC.player.getItemBySlot(EquipmentSlot.CHEST));
        dummyPlayer.setItemSlot(EquipmentSlot.LEGS, MC.player.getItemBySlot(EquipmentSlot.LEGS));
        dummyPlayer.setItemSlot(EquipmentSlot.FEET, MC.player.getItemBySlot(EquipmentSlot.FEET));
        dummyPlayer.setItemSlot(EquipmentSlot.MAINHAND, MC.player.getItemBySlot(EquipmentSlot.MAINHAND));
        dummyPlayer.setItemSlot(EquipmentSlot.OFFHAND, MC.player.getItemBySlot(EquipmentSlot.OFFHAND));
        dummyPlayer.calculateEntityAnimation(true);
    }

    private void updateDummyPlayerLookVector() {
        Vector lookVec = new Vector(MC.hitResult.getLocation()).getSubtracted(new Vector(dummyPlayer.getEyePosition()));
        float y = (float) lookVec.getYaw().getDegrees();
        float p = (float) lookVec.getPitch().getDegrees();
        if (!Float.isNaN(y) && !Float.isNaN(p)) {
            dummyPlayer.setYHeadRot(y);
            dummyPlayer.setYRot(y);
            dummyPlayer.setXRot(p);

            //After time, the server realizes we are "AFK", then the rotation packets are no longer accepted
            onChangeView.run(lookVec, () -> MC.getConnection().send(interactRotationPacket = new ServerboundMovePlayerPacket.Rot(y, p, MC.player.onGround())));
        }
    }

    private void resetPlayerData() {
        MC.player.setPos(dummyPlayer.getPosition(0));
        MC.player.setYRot(dummyPlayer.getYRot());
        MC.player.setYHeadRot(dummyPlayer.getYHeadRot());
        MC.player.setXRot(dummyPlayer.getXRot());
        if (dummyPlayer.isFallFlying()) MC.player.startFallFlying();
        MC.player.getAbilities().flying = dummyPlayer.getAbilities().flying;
        MC.player.onUpdateAbilities();

        MC.player.setDeltaMovement(0, 0, 0);

        MC.gameMode.setLocalMode(gameMode);
        MC.player.onGameModeChanged(gameMode);
    }

}
