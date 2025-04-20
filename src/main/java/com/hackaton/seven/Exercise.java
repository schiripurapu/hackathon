package com.hackaton.seven;

public class Exercise {
    String name, equipment, intensity, category;

    public Exercise(String name, String equipment, String intensity, String category) {
        this.name = name;
        this.equipment = equipment;
        this.intensity = intensity;
        this.category = category;
    }

    public boolean matches(String userEquipment, String userIntensity) {
        return equipment.equalsIgnoreCase(userEquipment) && intensity.equalsIgnoreCase(userIntensity);
    }

    @Override
    public String toString() {
        return name + " [" + category + "] - " + intensity + " (" + equipment + ")";
    }
}
