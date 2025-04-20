package com.hackaton.seven;

public class DailyPlan {
    Recipe breakfast;
    Recipe lunch;
    Recipe dinner;
    Exercise upper;
    Exercise lower;
    Exercise core;
    Exercise cardio;

    public String toString() {
        return String.format(
                "\n🍳 Breakfast: %s\n🥗 Lunch: %s\n🍽️ Dinner: %s\n" +
                        "🏋️ Exercises:\n - Upper Body: %s\n - Lower Body: %s\n - Core: %s\n - Cardio: %s",
                breakfast, lunch, dinner, upper, lower, core, cardio
        );
    }
}
