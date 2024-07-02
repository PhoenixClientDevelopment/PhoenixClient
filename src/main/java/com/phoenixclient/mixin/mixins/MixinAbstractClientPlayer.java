package com.phoenixclient.mixin.mixins;

import com.phoenixclient.PhoenixClient;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    /**
     * @author Me
     * @reason FreeCam doesn't like me
     */
    @Overwrite
    public boolean isSpectator() {
        return MC.gameMode.getPlayerMode() == GameType.SPECTATOR;
    }


}
