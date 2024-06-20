package com.phoenixclient.util.setting;

import com.phoenixclient.PhoenixClient;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingGUI<T> extends Setting<T> {

    private final IParentGUI parent;

    private final String name;
    private final String description;

    private Double min;
    private Double max;
    private Double step;

    private T[] modes;

    private final boolean numbersOnly;

    private Dependency<?> dependency;

    public SettingGUI(IParentGUI parent, String name, String description, T defaultValue, double min, double max, double step) {
        super(PhoenixClient.getSettingManager(), parent.getTitle() + "_" + name, defaultValue);
        this.parent = parent;
        this.name = name;
        this.description = description;

        this.min = min;
        this.max = max;
        this.step = step;

        this.numbersOnly = false;
        this.dependency = null;
    }

    @SafeVarargs
    public SettingGUI(IParentGUI parent, String name, String description, T defaultValue, T... modes) {
        super(PhoenixClient.getSettingManager(), parent.getTitle() + "_" + name, defaultValue);
        this.parent = parent;
        this.name = name;
        this.description = description;

        this.modes = modes;

        this.numbersOnly = false;
        this.dependency = null;
    }

    public SettingGUI(IParentGUI parent, String name, String description, T defaultValue, boolean numbersOnly) {
        super(PhoenixClient.getSettingManager(), parent.getTitle() + "_" + name, defaultValue);
        this.parent = parent;
        this.name = name;
        this.description = description;

        this.numbersOnly = numbersOnly;
        this.dependency = null;
    }

    public <E> SettingGUI<T> setSettingDependency(SettingGUI<E> setting, E value) {
        this.dependency = new Dependency<>(setting,value);
        return this;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getStep() {
        return step;
    }

    public ArrayList<T> getModes() {
        try {
            return new ArrayList<>(Arrays.asList(modes));
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public boolean isNumbersOnly() {
        return numbersOnly;
    }

    public IParentGUI getParent() {
        return parent;
    }

    public Dependency<?> getDependency() {
        return dependency;
    }


    public record Dependency<E>(SettingGUI<E> setting, E value) {
    }

}
