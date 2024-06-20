package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.IParentGUI;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.setting.SettingGUI;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static com.phoenixclient.PhoenixClient.MC;

public abstract class Module implements IParentGUI, Comparable<Module> {

    private final ArrayList<EventAction> eventActionList = new ArrayList<>();

    private final ArrayList<SettingGUI<?>> settingList = new ArrayList<>();

    private final String name;
    private final String description;
    private final Category category;
    private final SettingGUI<Boolean> enabled;
    private final SettingGUI<Integer> keyBind;

    public Module(String name, String description, Category category, boolean defaultEnabled, int defaultKeyBind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = new SettingGUI<>(this,"enabled","Is the mod enabled",defaultEnabled);
        this.keyBind = new SettingGUI<>(this,"keyBind","Key binding to active the mod",defaultKeyBind);
    }

    public abstract void onEnabled();

    public abstract void onDisabled();

    public void toggle() {
        if (isEnabled()) {
            disable();
        } else {
            enable();
        }
    }

    public void enable() {
        enabled.set(true);
        onEnabled();
        for (EventAction action : getEventActions()) action.subscribe();
    }

    public void disable() {
        for (EventAction action : getEventActions()) action.unsubscribe();
        onDisabled();
        enabled.set(false);
    }

    public String getModTag() {
        return "";
    }


    protected void addEventActions(EventAction... actions) {
        eventActionList.addAll(Arrays.asList(actions));
    }

    protected void addSettings(SettingGUI<?>... settings) {
        settingList.addAll(Arrays.asList(settings));
    }


    public void setKeyBind(int keyBind) {
        this.keyBind.set(keyBind);
    }


    public String getTitle() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }


    public boolean isEnabled() {
        return enabled.get();
    }

    public int getKeyBind() {
        return keyBind.get();
    }


    protected ArrayList<EventAction> getEventActions() {
        return eventActionList;
    }

    public ArrayList<SettingGUI<?>> getSettings() {
        return settingList;
    }


    @Override //Alphabetical Comparison
    public int compareTo(@NotNull Module o) {
        return getTitle().compareTo(o.getTitle());
    }



    public static EventAction MODULE_KEYBIND_ACTION = new EventAction(Event.EVENT_KEY_PRESS, () -> {
        if (MC.screen != null) return;
        for (Module module : PhoenixClient.getModules()) {
            if (Event.EVENT_KEY_PRESS.getKey() == module.getKeyBind() && Event.EVENT_KEY_PRESS.getState() == Key.ACTION_PRESS)
                module.toggle();
        }
    });

    public enum Category {
        COMBAT("Combat"),
        PLAYER("Player"),
        MOTION("Motion"),
        RENDER("Render"),
        SERVER("Server");

        final String name;
        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
