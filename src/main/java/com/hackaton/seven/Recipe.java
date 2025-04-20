package com.hackaton.seven;

import java.util.List;
import java.util.Set;

public class Recipe {
    String name, mealType;
    List<String> dietaryTags, allergies, ingredients;
    double calories, carbs, protein, fats;

    public Recipe(String name, String mealType, List<String> dietaryTags, List<String> allergies,
                  double calories, double carbs, double protein, double fats, List<String> ingredients) {
        this.name = name;
        this.mealType = mealType;
        this.dietaryTags = dietaryTags;
        this.allergies = allergies;
        this.calories = calories;
        this.carbs = carbs;
        this.protein = protein;
        this.fats = fats;
        this.ingredients = ingredients;
    }

    public boolean matchesRestrictions(Set<String> userDiet, Set<String> userAllergies) {
        return dietaryTags.containsAll(userDiet) && allergies.stream().noneMatch(userAllergies::contains);
    }

    public double balanceScore() {
        return 100 - Math.abs(carbs - 50) - Math.abs(protein - 20) - Math.abs(fats - 30);
    }

    @Override
    public String toString() {
        return name + " [" + mealType + "]: " + calories + " kcal | " + ingredients;
    }
}
