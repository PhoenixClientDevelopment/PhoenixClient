package com.phoenixclient.util;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;

//TODO: ADD THIS!!! Just use MIXIN to modify sendPosition() in LocalPlayer to have the according spoofed rotation packets.
// use onChanged to replace change rotation boolean in the method

/**
 * Allows for serverside changing of angles without sending extra packets. It just modifies outgoing ones
 */
public class RotationManager {

    private boolean spoofing;

    private float spoofedYaw;
    private float spoofedPitch;

    public RotationManager() {
        this.spoofedYaw = 0;
        this.spoofedPitch = 0;
        this.spoofing = false;
    }

    public void spoof(float yaw, float pitch) {
        spoofedYaw = yaw;
        spoofedPitch = pitch;
        spoofing = true;
    }

    public boolean isSpoofing() {
        return spoofing;
    }

    public float getSpoofedYaw() {
        return spoofedYaw;
    }

    public float getSpoofedPitch() {
        return spoofedPitch;
    }

    public void stopSpoofing() {
        spoofing = false;
    }

    public final EventAction setSpoofedAngles = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        if (spoofing) {

        }
    });
}
