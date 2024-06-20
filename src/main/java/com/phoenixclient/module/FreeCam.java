package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameType;

import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

public class FreeCam extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of FreeCam",
            "Ghost", "Ghost", "Interact");

    private final OnChange<Vector> onChangeView = new OnChange<>();
    private final OnChange<String> onChangeMode = new OnChange<>();

    private AbstractClientPlayer dummyPlayer;
    private GameType gameMode;
    private ServerboundMovePlayerPacket interactRotationPacket = null;

    public FreeCam() {
        super("FreeCam", "Allows the camera to move out of the body", Category.PLAYER, false, -1);
        addEventActions(onPacket, onUpdate);
        addSettings(mode);
    }

    private final EventAction onUpdate = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        //Update Dummy Player Attributes
        if (dummyPlayer != null) {
            //Set Dummy Inventory
            dummyPlayer.setItemSlot(EquipmentSlot.HEAD, MC.player.getItemBySlot(EquipmentSlot.HEAD));
            dummyPlayer.setItemSlot(EquipmentSlot.CHEST, MC.player.getItemBySlot(EquipmentSlot.CHEST));
            dummyPlayer.setItemSlot(EquipmentSlot.LEGS, MC.player.getItemBySlot(EquipmentSlot.LEGS));
            dummyPlayer.setItemSlot(EquipmentSlot.FEET, MC.player.getItemBySlot(EquipmentSlot.FEET));
            dummyPlayer.setItemSlot(EquipmentSlot.MAINHAND, MC.player.getItemBySlot(EquipmentSlot.MAINHAND));
            dummyPlayer.setItemSlot(EquipmentSlot.OFFHAND, MC.player.getItemBySlot(EquipmentSlot.OFFHAND));
            switch (mode.get()) {
                case "Ghost" -> {
                    //Nothing Extra
                }
                case "Interact" -> {
                    MC.player.getAbilities().flying = true;

                    if (MC.player.swinging) {
                        dummyPlayer.swing(dummyPlayer.swingingArm);
                        //TODO: Have the player shield block & swing when the player does
                    }

                    //look at the hit result location
                    Vector lookVec = new Vector(MC.hitResult.getLocation()).getSubtracted(new Vector(dummyPlayer.getEyePosition()));
                    float y = (float) lookVec.getYaw().getDegrees();
                    float p = (float) lookVec.getPitch().getDegrees();
                    if (!Float.isNaN(y) && !Float.isNaN(p)) {
                        dummyPlayer.setYHeadRot(y);
                        dummyPlayer.setYRot(y);
                        dummyPlayer.setXRot(p);
                        onChangeView.run(lookVec, () -> MC.getConnection().send(interactRotationPacket = new ServerboundMovePlayerPacket.Rot(y, p, MC.player.onGround())));
                    }
                }
            }
        }

        onChangeMode.run(mode.get(), this::disable);
    });

    //It is safe to cancel these packets. Anti-cheat will not detect. If the player is naturally not moving, it won't send these packets
    private final EventAction onPacket = new EventAction(Event.EVENT_PACKET, () -> {
        Packet<?> packet = Event.EVENT_PACKET.getPacket();
        if (packet instanceof ServerboundMovePlayerPacket || packet instanceof ServerboundAcceptTeleportationPacket || packet instanceof ServerboundPlayerInputPacket) {
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
        onChangeMode.run(mode.get(), () -> {});

        //Log Original Game Mode
        gameMode = MC.gameMode.getPlayerMode();

        //Summon Dummy Entity
        MC.level.addEntity(dummyPlayer = new AbstractClientPlayer(MC.level,MC.player.getGameProfile()) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public UUID getUUID() {
                return UUID.randomUUID();
            }

            @Override
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

        //Set Mode Data
        switch (mode.get()) {
            case "Ghost" -> {
                MC.gameMode.setLocalMode(GameType.SPECTATOR);
                MC.player.onGameModeChanged(GameType.SPECTATOR);
                //Make it so you cannot attack entities here
            }
            case "Interact" -> {
                MC.player.noCulling = true;
                //PhoenixClientHooks.setNoClip(true);
            }
        }
    }

    @Override
    public void onDisabled() {
        if (MC.player == null || dummyPlayer == null) return;

        //Kill Dummy Entity
        dummyPlayer.remove(Entity.RemovalReason.KILLED);

        //Reset Player Values
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

        MC.player.noCulling = false;
        //PhoenixClientHooks.setNoClip(false);
    }

}
