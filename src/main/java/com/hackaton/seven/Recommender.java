package com.hackaton.seven;

import java.util.*;
import java.util.stream.Collectors;

public class Recommender {

    private List<Recipe> allRecipes = new ArrayList<>();

    private List<Exercise> allExercises = new ArrayList<>();

    public Recommender() {
        this.initializeRecipes();
        this.initializeExercises();
    }


    public static void main(String[] args) {

        Recommender recommender = new Recommender();

        Scanner scanner = new Scanner(System.in);

        // Meal Preferences
        System.out.println("Enter dietary restrictions (comma separated, e.g., vegan,gluten_free,pescatarian):");
        Set<String> dietary = new HashSet<>(Arrays.asList(scanner.nextLine().split(",")));

        System.out.println("Enter allergy restrictions (comma separated, e.g., dairy,nuts):");
        Set<String> allergies = new HashSet<>(Arrays.asList(scanner.nextLine().split(",")));


        System.out.println("Enter your daily calorie target:");
        double dailyCalories = scanner.nextDouble();
        scanner.nextLine(); // clear line

        // Exercise Preferences
        System.out.println("Enter preferred equipment (e.g., None, Dumbbells):");
        String equipment = scanner.nextLine().trim();

        System.out.println("Enter preferred intensity (Low, Medium, High):");
        String intensity = scanner.nextLine().trim();

        Map<String, DailyPlan> weeklyPlan = recommender.generateWeeklyPlanWithExercise(
                dailyCalories, dietary, allergies, equipment, intensity
        );

        System.out.println("\n 7-Day Diet and Exercise Plan:");
        for (Map.Entry<String, DailyPlan> entry : weeklyPlan.entrySet()) {
            System.out.println("\n " + entry.getKey() + ":" + entry.getValue());
        }


        scanner.close();
    }

    Map<String, DailyPlan> generateWeeklyPlanWithExercise(
            double dailyCalories,
            Set<String> diet,
            Set<String> allergies,
            String equipment,
            String intensity
    ) {
        Map<String, DailyPlan> weekPlan = new LinkedHashMap<>();
        double perMeal = dailyCalories / 3;
        Random rand = new Random();

        for (int i = 1; i <= 7; i++) {
            String day = "Day " + i;

            Recipe breakfast = getRandomMeal("breakfast", perMeal, diet, allergies, rand);
            Recipe lunch = getRandomMeal("lunch", perMeal, diet, allergies, rand);
            Recipe dinner = getRandomMeal("dinner", perMeal, diet, allergies, rand);

            Exercise upper = getRandomExercise("upper_body", equipment, intensity, rand);
            Exercise lower = getRandomExercise("lower_body", equipment, intensity, rand);
            Exercise core = getRandomExercise("core", equipment, intensity, rand);
            Exercise cardio = getRandomExercise("cardio", equipment, intensity, rand);

            DailyPlan plan = new DailyPlan();
            plan.breakfast = breakfast;
            plan.lunch = lunch;
            plan.dinner = dinner;
            plan.upper = upper;
            plan.lower = lower;
            plan.core = core;
            plan.cardio = cardio;

            weekPlan.put(day, plan);
        }

        return weekPlan;
    }

    Recipe getRandomMeal(String mealType, double targetCalories, Set<String> diet, Set<String> allergies, Random rand) {
        List<Recipe> candidates = allRecipes.stream()
                .filter(r -> r.mealType.equalsIgnoreCase(mealType))
                .filter(r -> r.matchesRestrictions(diet, allergies))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            return new Recipe("No suitable " + mealType + " found", mealType, new ArrayList<>(), new ArrayList<>(), 0, 0, 0, 0, Arrays.asList());
        }

        // Pick random, prefer those close to targetCalories
        candidates.sort(Comparator.comparingDouble(r -> Math.abs(r.calories - targetCalories)));
        int bound = Math.min(5, candidates.size());
        return candidates.get(rand.nextInt(bound)); // Random among top 5 closest
    }

    Exercise getRandomExercise(String category, String equipment, String intensity, Random rand) {
        List<Exercise> filtered = allExercises.stream()
                .filter(e -> e.category.equalsIgnoreCase(category))
                .filter(e -> e.matches(equipment, intensity))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return new Exercise("No suitable " + category + " exercise", equipment, intensity, category);
        }

        return filtered.get(rand.nextInt(filtered.size()));
    }

    private void initializeRecipes() {
        // Breakfast Recipes
        allRecipes.add(new Recipe("Chia Pudding with Almond Butter", "breakfast",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                350, 30, 12, 18,
                Arrays.asList("Chia seeds", "Almond milk", "Almond butter", "Maple syrup", "Vanilla extract")));

        allRecipes.add(new Recipe("Banana Pancakes", "breakfast",
                Arrays.asList("vegetarian"), Arrays.asList("gluten"),
                450, 50, 12, 15,
                Arrays.asList("Bananas", "Oats", "Eggs", "Almond milk", "Baking powder", "Cinnamon")));

        allRecipes.add(new Recipe("Smoothie Bowl", "breakfast",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                400, 45, 8, 20,
                Arrays.asList("Frozen berries", "Banana", "Almond milk", "Chia seeds", "Granola", "Coconut flakes")));

        allRecipes.add(new Recipe("Avocado Toast with Tomato", "breakfast",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                350, 35, 8, 18,
                Arrays.asList("Whole grain bread", "Avocado", "Tomato", "Lemon", "Chili flakes", "Olive oil")));

        // Lunch Recipes
        allRecipes.add(new Recipe("Lentil Salad with Feta", "lunch",
                Arrays.asList("vegetarian"), Arrays.asList("dairy"),
                500, 40, 25, 18,
                Arrays.asList("Lentils", "Feta cheese", "Spinach", "Olives", "Tomatoes", "Olive oil", "Lemon")));

        allRecipes.add(new Recipe("Spicy Quinoa Stir Fry", "lunch",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                550, 60, 15, 20,
                Arrays.asList("Quinoa", "Bell peppers", "Tofu", "Soy sauce", "Garlic", "Ginger", "Sesame oil")));

        allRecipes.add(new Recipe("Sweet Potato & Chickpea Buddha Bowl", "lunch",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                600, 70, 18, 22,
                Arrays.asList("Sweet potatoes", "Chickpeas", "Spinach", "Avocado", "Tahini", "Cumin", "Olive oil")));

        allRecipes.add(new Recipe("Chicken Caesar Salad", "lunch",
                Arrays.asList("gluten_free"), Arrays.asList("dairy"),
                480, 20, 35, 25,
                Arrays.asList("Chicken breast", "Romaine lettuce", "Caesar dressing", "Parmesan", "Croutons")));

        allRecipes.add(new Recipe("Grilled Veggie Wrap", "lunch",
                Arrays.asList("vegetarian"), Arrays.asList("gluten"),
                450, 45, 15, 18,
                Arrays.asList("Whole wheat tortilla", "Zucchini", "Bell peppers", "Hummus", "Spinach", "Olive oil")));

        // Dinner Recipes
        allRecipes.add(new Recipe("Baked Salmon with Asparagus", "dinner",
                Arrays.asList("pescatarian", "gluten_free"), Arrays.asList(),
                650, 25, 45, 30,
                Arrays.asList("Salmon fillet", "Asparagus", "Olive oil", "Lemon", "Garlic", "Herbs")));

        allRecipes.add(new Recipe("Tofu and Vegetable Stir Fry", "dinner",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                550, 60, 25, 18,
                Arrays.asList("Tofu", "Broccoli", "Carrots", "Bell peppers", "Soy sauce", "Garlic", "Ginger")));

        allRecipes.add(new Recipe("Vegan Buddha Bowl", "dinner",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                600, 70, 18, 25,
                Arrays.asList("Quinoa", "Avocado", "Chickpeas", "Spinach", "Carrots", "Tahini", "Lemon")));

        allRecipes.add(new Recipe("Grilled Chicken with Roasted Veggies", "dinner",
                Arrays.asList("gluten_free"), Arrays.asList(),
                550, 25, 40, 20,
                Arrays.asList("Chicken breast", "Zucchini", "Carrots", "Sweet potatoes", "Olive oil", "Garlic")));

        allRecipes.add(new Recipe("Spaghetti Squash with Marinara", "dinner",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                450, 40, 12, 20,
                Arrays.asList("Spaghetti squash", "Tomato sauce", "Garlic", "Basil", "Olive oil")));

        allRecipes.add(new Recipe("Vegan Chili", "dinner",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                500, 55, 25, 15,
                Arrays.asList("Black beans", "Kidney beans", "Tomatoes", "Onions", "Chili powder", "Garlic", "Olive oil")));

        // Adding more random healthy meals (you can expand this further)
        allRecipes.add(new Recipe("Mango Chicken Salad", "lunch",
                Arrays.asList("gluten_free"), Arrays.asList(),
                550, 40, 35, 15,
                Arrays.asList("Chicken breast", "Mango", "Spinach", "Avocado", "Cucumber", "Lime")));

        allRecipes.add(new Recipe("Grilled Shrimp Tacos", "lunch",
                Arrays.asList("gluten_free"), Arrays.asList("shellfish"),
                450, 35, 28, 15,
                Arrays.asList("Shrimp", "Corn tortillas", "Cabbage", "Avocado", "Salsa", "Lime")));

        allRecipes.add(new Recipe("Vegetable Stir Fry with Tempeh", "dinner",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                500, 55, 20, 18,
                Arrays.asList("Tempeh", "Bell peppers", "Broccoli", "Carrots", "Soy sauce", "Garlic", "Ginger")));

        allRecipes.add(new Recipe("Baked Cod with Roasted Potatoes", "dinner",
                Arrays.asList("pescatarian", "gluten_free"), Arrays.asList(),
                550, 35, 40, 20,
                Arrays.asList("Cod fillet", "Potatoes", "Garlic", "Olive oil", "Rosemary", "Lemon")));

        allRecipes.add(new Recipe("Cabbage and Lentil Soup", "dinner",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                400, 50, 18, 12,
                Arrays.asList("Lentils", "Cabbage", "Carrots", "Onions", "Vegetable broth", "Garlic", "Tomato paste")));

        allRecipes.add(new Recipe("Zucchini Noodles with Pesto", "dinner",
                Arrays.asList("vegetarian"), Arrays.asList(),
                450, 35, 20, 25,
                Arrays.asList("Zucchini", "Pesto", "Parmesan", "Olive oil", "Garlic")));

        allRecipes.add(new Recipe("Chickpea and Sweet Potato Stew", "dinner",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                500, 60, 18, 15,
                Arrays.asList("Chickpeas", "Sweet potatoes", "Spinach", "Coconut milk", "Tomatoes", "Garlic", "Onions")));

        allRecipes.add(new Recipe("Tofu Stir Fry with Rice", "dinner",
                Arrays.asList("vegan", "gluten_free"), Arrays.asList(),
                550, 70, 25, 15,
                Arrays.asList("Tofu", "Brown rice", "Soy sauce", "Broccoli", "Carrots", "Ginger", "Garlic")));

        allRecipes.add(new Recipe("Mushroom Risotto", "dinner",
                Arrays.asList("vegetarian"), Arrays.asList(),
                600, 50, 15, 25,
                Arrays.asList("Arborio rice", "Mushrooms", "Vegetable broth", "Parmesan", "Olive oil", "Garlic")));

        allRecipes.add(new Recipe("Dinner Dish 1", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 564, 35.2, 70.5, 15.7, Arrays.asList("barley", "eggs", "kale", "spinach", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 2", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 368, 23.0, 46.0, 10.2, Arrays.asList("whole wheat bread", "lentils", "bell peppers", "tomatoes", "cottage cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 3", "breakfast", Arrays.asList("none"), Arrays.asList(), 459, 28.7, 57.4, 12.8, Arrays.asList("quinoa", "black beans", "spinach", "mushrooms", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 4", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 628, 39.2, 78.5, 17.4, Arrays.asList("quinoa", "eggs", "mushrooms", "carrots", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 5", "dinner", Arrays.asList("vegan"), Arrays.asList(), 458, 28.6, 57.2, 12.7, Arrays.asList("millet", "tempeh", "carrots", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 6", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 430, 26.9, 53.8, 11.9, Arrays.asList("brown rice", "turkey", "carrots", "broccoli", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 7", "dinner", Arrays.asList("vegan"), Arrays.asList(), 503, 31.4, 62.9, 14.0, Arrays.asList("whole wheat bread", "tempeh", "mushrooms", "carrots", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 8", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 677, 42.3, 84.6, 18.8, Arrays.asList("quinoa", "black beans", "broccoli", "bell peppers", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 9", "lunch", Arrays.asList("none"), Arrays.asList(), 502, 31.4, 62.8, 13.9, Arrays.asList("quinoa", "chickpeas", "zucchini", "tomatoes", "milk")));
        allRecipes.add(new Recipe("Breakfast Dish 10", "breakfast", Arrays.asList("none"), Arrays.asList(), 361, 22.6, 45.1, 10.0, Arrays.asList("oats", "tempeh", "mushrooms", "zucchini", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 11", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 429, 26.8, 53.6, 11.9, Arrays.asList("barley", "chickpeas", "mushrooms", "carrots", "cottage cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 12", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 598, 37.4, 74.8, 16.6, Arrays.asList("brown rice", "black beans", "zucchini", "tomatoes", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 13", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 360, 22.5, 45.0, 10.0, Arrays.asList("whole wheat bread", "tempeh", "kale", "bell peppers", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 14", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 404, 25.2, 50.5, 11.2, Arrays.asList("millet", "black beans", "mushrooms", "zucchini", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 15", "lunch", Arrays.asList("none"), Arrays.asList(), 454, 28.4, 56.8, 12.6, Arrays.asList("millet", "chicken breast", "broccoli", "bell peppers", "cheese")));
        allRecipes.add(new Recipe("Dinner Dish 16", "dinner", Arrays.asList("vegan"), Arrays.asList(), 658, 41.1, 82.2, 18.3, Arrays.asList("brown rice", "lentils", "tomatoes", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 17", "lunch", Arrays.asList("none"), Arrays.asList(), 610, 38.1, 76.2, 16.9, Arrays.asList("oats", "chicken breast", "tomatoes", "broccoli", "milk")));
        allRecipes.add(new Recipe("Dinner Dish 18", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 496, 31.0, 62.0, 13.8, Arrays.asList("quinoa", "turkey", "kale", "broccoli", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 19", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 544, 34.0, 68.0, 15.1, Arrays.asList("brown rice", "tofu", "kale", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 20", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 384, 24.0, 48.0, 10.7, Arrays.asList("brown rice", "chickpeas", "bell peppers", "carrots", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 21", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 579, 36.2, 72.4, 16.1, Arrays.asList("oats", "tempeh", "carrots", "kale", "yogurt")));
        allRecipes.add(new Recipe("Breakfast Dish 22", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 437, 27.3, 54.6, 12.1, Arrays.asList("quinoa", "lentils", "broccoli", "kale", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 23", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 592, 37.0, 74.0, 16.4, Arrays.asList("brown rice", "tofu", "spinach", "zucchini", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 24", "lunch", Arrays.asList("none"), Arrays.asList(), 497, 31.1, 62.1, 13.8, Arrays.asList("whole wheat bread", "black beans", "broccoli", "bell peppers", "yogurt")));
        allRecipes.add(new Recipe("Breakfast Dish 25", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 542, 33.9, 67.8, 15.1, Arrays.asList("brown rice", "black beans", "kale", "zucchini", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 26", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 403, 25.2, 50.4, 11.2, Arrays.asList("whole wheat pasta", "lentils", "carrots", "zucchini", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 27", "dinner", Arrays.asList("none"), Arrays.asList(), 410, 25.6, 51.2, 11.4, Arrays.asList("whole wheat bread", "turkey", "bell peppers", "mushrooms", "cottage cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 28", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 641, 40.1, 80.1, 17.8, Arrays.asList("brown rice", "tofu", "spinach", "bell peppers", "almonds")));
        allRecipes.add(new Recipe("Dinner Dish 29", "dinner", Arrays.asList("vegan"), Arrays.asList(), 607, 37.9, 75.9, 16.9, Arrays.asList("whole wheat bread", "lentils", "kale", "carrots", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 30", "lunch", Arrays.asList("vegan"), Arrays.asList(), 310, 19.4, 38.8, 8.6, Arrays.asList("quinoa", "black beans", "broccoli", "mushrooms", "avocado")));
        allRecipes.add(new Recipe("Dinner Dish 31", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 439, 27.4, 54.9, 12.2, Arrays.asList("brown rice", "tofu", "mushrooms", "carrots", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 32", "breakfast", Arrays.asList("none"), Arrays.asList(), 380, 23.8, 47.5, 10.6, Arrays.asList("whole wheat pasta", "eggs", "mushrooms", "tomatoes", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 33", "breakfast", Arrays.asList("none"), Arrays.asList(), 465, 29.1, 58.1, 12.9, Arrays.asList("brown rice", "lentils", "bell peppers", "tomatoes", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 34", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 500, 31.2, 62.5, 13.9, Arrays.asList("oats", "tofu", "broccoli", "kale", "cottage cheese")));
        allRecipes.add(new Recipe("Dinner Dish 35", "dinner", Arrays.asList("none"), Arrays.asList(), 514, 32.1, 64.2, 14.3, Arrays.asList("barley", "turkey", "mushrooms", "tomatoes", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 36", "lunch", Arrays.asList("vegan"), Arrays.asList(), 377, 23.6, 47.1, 10.5, Arrays.asList("millet", "lentils", "kale", "bell peppers", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 37", "lunch", Arrays.asList("none"), Arrays.asList(), 457, 28.6, 57.1, 12.7, Arrays.asList("quinoa", "black beans", "bell peppers", "carrots", "milk")));
        allRecipes.add(new Recipe("Breakfast Dish 38", "breakfast", Arrays.asList("none"), Arrays.asList(), 670, 41.9, 83.8, 18.6, Arrays.asList("quinoa", "chickpeas", "bell peppers", "kale", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 39", "dinner", Arrays.asList("vegan"), Arrays.asList(), 311, 19.4, 38.9, 8.6, Arrays.asList("whole wheat pasta", "tofu", "zucchini", "broccoli", "almonds")));
        allRecipes.add(new Recipe("Dinner Dish 40", "dinner", Arrays.asList("none"), Arrays.asList(), 632, 39.5, 79.0, 17.6, Arrays.asList("oats", "chicken breast", "broccoli", "kale", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 41", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 306, 19.1, 38.2, 8.5, Arrays.asList("quinoa", "black beans", "zucchini", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 42", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 634, 39.6, 79.2, 17.6, Arrays.asList("quinoa", "black beans", "tomatoes", "carrots", "milk")));
        allRecipes.add(new Recipe("Dinner Dish 43", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 463, 28.9, 57.9, 12.9, Arrays.asList("brown rice", "black beans", "mushrooms", "broccoli", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 44", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 371, 23.2, 46.4, 10.3, Arrays.asList("oats", "eggs", "broccoli", "carrots", "cheese")));
        allRecipes.add(new Recipe("Dinner Dish 45", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 345, 21.6, 43.1, 9.6, Arrays.asList("quinoa", "lentils", "kale", "tomatoes", "almonds")));
        allRecipes.add(new Recipe("Dinner Dish 46", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 460, 28.8, 57.5, 12.8, Arrays.asList("quinoa", "eggs", "kale", "zucchini", "milk")));
        allRecipes.add(new Recipe("Dinner Dish 47", "dinner", Arrays.asList("none"), Arrays.asList(), 686, 42.9, 85.8, 19.1, Arrays.asList("quinoa", "chicken breast", "mushrooms", "kale", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 48", "breakfast", Arrays.asList("none"), Arrays.asList(), 645, 40.3, 80.6, 17.9, Arrays.asList("millet", "chicken breast", "zucchini", "mushrooms", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 49", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 564, 35.2, 70.5, 15.7, Arrays.asList("millet", "lentils", "broccoli", "tomatoes", "cottage cheese")));
        allRecipes.add(new Recipe("Dinner Dish 50", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 337, 21.1, 42.1, 9.4, Arrays.asList("quinoa", "black beans", "kale", "mushrooms", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 51", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 498, 31.1, 62.2, 13.8, Arrays.asList("quinoa", "black beans", "spinach", "kale", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 52", "breakfast", Arrays.asList("none"), Arrays.asList(), 651, 40.7, 81.4, 18.1, Arrays.asList("barley", "black beans", "bell peppers", "broccoli", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 53", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 668, 41.8, 83.5, 18.6, Arrays.asList("oats", "lentils", "kale", "carrots", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 54", "lunch", Arrays.asList("vegan"), Arrays.asList(), 636, 39.8, 79.5, 17.7, Arrays.asList("barley", "tempeh", "zucchini", "mushrooms", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 55", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 521, 32.6, 65.1, 14.5, Arrays.asList("quinoa", "eggs", "broccoli", "tomatoes", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 56", "dinner", Arrays.asList("none"), Arrays.asList(), 533, 33.3, 66.6, 14.8, Arrays.asList("quinoa", "chicken breast", "spinach", "zucchini", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 57", "dinner", Arrays.asList("none"), Arrays.asList(), 603, 37.7, 75.4, 16.8, Arrays.asList("oats", "tempeh", "kale", "zucchini", "cheese")));
        allRecipes.add(new Recipe("Dinner Dish 58", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 586, 36.6, 73.2, 16.3, Arrays.asList("barley", "tempeh", "bell peppers", "broccoli", "yogurt")));
        allRecipes.add(new Recipe("Lunch Dish 59", "lunch", Arrays.asList("vegan"), Arrays.asList(), 694, 43.4, 86.8, 19.3, Arrays.asList("oats", "black beans", "carrots", "kale", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 60", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 554, 34.6, 69.2, 15.4, Arrays.asList("brown rice", "lentils", "carrots", "zucchini", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 61", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 604, 37.8, 75.5, 16.8, Arrays.asList("whole wheat pasta", "black beans", "tomatoes", "kale", "almonds")));
        allRecipes.add(new Recipe("Lunch Dish 62", "lunch", Arrays.asList("none"), Arrays.asList(), 628, 39.2, 78.5, 17.4, Arrays.asList("brown rice", "chicken breast", "mushrooms", "kale", "almonds")));
        allRecipes.add(new Recipe("Dinner Dish 63", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 385, 24.1, 48.1, 10.7, Arrays.asList("quinoa", "tofu", "zucchini", "carrots", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 64", "lunch", Arrays.asList("vegan"), Arrays.asList(), 667, 41.7, 83.4, 18.5, Arrays.asList("barley", "tempeh", "broccoli", "zucchini", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 65", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 423, 26.4, 52.9, 11.8, Arrays.asList("brown rice", "chicken breast", "carrots", "spinach", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 66", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 334, 20.9, 41.8, 9.3, Arrays.asList("quinoa", "chicken breast", "tomatoes", "mushrooms", "almonds")));
        allRecipes.add(new Recipe("Lunch Dish 67", "lunch", Arrays.asList("vegan"), Arrays.asList(), 611, 38.2, 76.4, 17.0, Arrays.asList("whole wheat bread", "black beans", "tomatoes", "bell peppers", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 68", "dinner", Arrays.asList("none"), Arrays.asList(), 623, 38.9, 77.9, 17.3, Arrays.asList("brown rice", "chicken breast", "zucchini", "bell peppers", "cheese")));
        allRecipes.add(new Recipe("Dinner Dish 69", "dinner", Arrays.asList("none"), Arrays.asList(), 553, 34.6, 69.1, 15.4, Arrays.asList("whole wheat pasta", "chicken breast", "spinach", "tomatoes", "milk")));
        allRecipes.add(new Recipe("Breakfast Dish 70", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 577, 36.1, 72.1, 16.0, Arrays.asList("whole wheat pasta", "black beans", "spinach", "bell peppers", "yogurt")));
        allRecipes.add(new Recipe("Lunch Dish 71", "lunch", Arrays.asList("none"), Arrays.asList(), 384, 24.0, 48.0, 10.7, Arrays.asList("quinoa", "black beans", "kale", "tomatoes", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 72", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 515, 32.2, 64.4, 14.3, Arrays.asList("barley", "black beans", "spinach", "broccoli", "yogurt")));
        allRecipes.add(new Recipe("Breakfast Dish 73", "breakfast", Arrays.asList("none"), Arrays.asList(), 300, 18.8, 37.5, 8.3, Arrays.asList("brown rice", "chicken breast", "spinach", "broccoli", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 74", "lunch", Arrays.asList("none"), Arrays.asList(), 650, 40.6, 81.2, 18.1, Arrays.asList("oats", "chicken breast", "bell peppers", "spinach", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 75", "lunch", Arrays.asList("vegan"), Arrays.asList(), 674, 42.1, 84.2, 18.7, Arrays.asList("quinoa", "black beans", "carrots", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 76", "lunch", Arrays.asList("none"), Arrays.asList(), 563, 35.2, 70.4, 15.6, Arrays.asList("quinoa", "chicken breast", "broccoli", "mushrooms", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 77", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 609, 38.1, 76.1, 16.9, Arrays.asList("brown rice", "lentils", "mushrooms", "tomatoes", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 78", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 463, 28.9, 57.9, 12.9, Arrays.asList("quinoa", "tofu", "broccoli", "spinach", "cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 79", "breakfast", Arrays.asList("none"), Arrays.asList(), 485, 30.3, 60.6, 13.5, Arrays.asList("whole wheat bread", "tofu", "spinach", "zucchini", "avocado")));
        allRecipes.add(new Recipe("Dinner Dish 80", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 606, 37.9, 75.8, 16.8, Arrays.asList("whole wheat bread", "turkey", "tomatoes", "spinach", "cottage cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 81", "breakfast", Arrays.asList("none"), Arrays.asList(), 646, 40.4, 80.8, 17.9, Arrays.asList("millet", "chicken breast", "carrots", "mushrooms", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 82", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 655, 40.9, 81.9, 18.2, Arrays.asList("brown rice", "chickpeas", "kale", "spinach", "yogurt")));
        allRecipes.add(new Recipe("Breakfast Dish 83", "breakfast", Arrays.asList("none"), Arrays.asList(), 603, 37.7, 75.4, 16.8, Arrays.asList("oats", "lentils", "zucchini", "kale", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 84", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 582, 36.4, 72.8, 16.2, Arrays.asList("quinoa", "turkey", "mushrooms", "tomatoes", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 85", "lunch", Arrays.asList("none"), Arrays.asList(), 476, 29.8, 59.5, 13.2, Arrays.asList("whole wheat bread", "chicken breast", "mushrooms", "carrots", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 86", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 540, 33.8, 67.5, 15.0, Arrays.asList("quinoa", "tofu", "zucchini", "carrots", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 87", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 420, 26.2, 52.5, 11.7, Arrays.asList("brown rice", "tofu", "kale", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 88", "dinner", Arrays.asList("none"), Arrays.asList(), 346, 21.6, 43.2, 9.6, Arrays.asList("millet", "black beans", "zucchini", "tomatoes", "yogurt")));
        allRecipes.add(new Recipe("Lunch Dish 89", "lunch", Arrays.asList("none"), Arrays.asList(), 663, 41.4, 82.9, 18.4, Arrays.asList("whole wheat pasta", "chickpeas", "zucchini", "kale", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 90", "breakfast", Arrays.asList("none"), Arrays.asList(), 398, 24.9, 49.8, 11.1, Arrays.asList("brown rice", "chickpeas", "mushrooms", "carrots", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 91", "lunch", Arrays.asList("none"), Arrays.asList(), 636, 39.8, 79.5, 17.7, Arrays.asList("quinoa", "chickpeas", "broccoli", "mushrooms", "cheese")));
        allRecipes.add(new Recipe("Dinner Dish 92", "dinner", Arrays.asList("vegan"), Arrays.asList(), 385, 24.1, 48.1, 10.7, Arrays.asList("quinoa", "tempeh", "carrots", "broccoli", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 93", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 592, 37.0, 74.0, 16.4, Arrays.asList("brown rice", "tempeh", "spinach", "mushrooms", "cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 94", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 390, 24.4, 48.8, 10.8, Arrays.asList("quinoa", "turkey", "kale", "bell peppers", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 95", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 367, 22.9, 45.9, 10.2, Arrays.asList("quinoa", "tofu", "zucchini", "kale", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 96", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 609, 38.1, 76.1, 16.9, Arrays.asList("quinoa", "lentils", "zucchini", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 97", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 647, 40.4, 80.9, 18.0, Arrays.asList("brown rice", "eggs", "carrots", "tomatoes", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 98", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 469, 29.3, 58.6, 13.0, Arrays.asList("quinoa", "chickpeas", "carrots", "mushrooms", "milk")));
        allRecipes.add(new Recipe("Breakfast Dish 99", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 594, 37.1, 74.2, 16.5, Arrays.asList("millet", "tempeh", "mushrooms", "tomatoes", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 100", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 463, 28.9, 57.9, 12.9, Arrays.asList("brown rice", "eggs", "zucchini", "carrots", "avocado")));
        allRecipes.add(new Recipe("Dinner Dish 101", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 579, 36.2, 72.4, 16.1, Arrays.asList("whole wheat bread", "chickpeas", "kale", "carrots", "yogurt")));
        allRecipes.add(new Recipe("Lunch Dish 102", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 600, 37.5, 75.0, 16.7, Arrays.asList("brown rice", "tempeh", "kale", "broccoli", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 103", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 319, 19.9, 39.9, 8.9, Arrays.asList("quinoa", "chickpeas", "bell peppers", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 104", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 618, 38.6, 77.2, 17.2, Arrays.asList("quinoa", "lentils", "tomatoes", "kale", "almonds")));
        allRecipes.add(new Recipe("Dinner Dish 105", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 542, 33.9, 67.8, 15.1, Arrays.asList("brown rice", "eggs", "bell peppers", "carrots", "milk")));
        allRecipes.add(new Recipe("Dinner Dish 106", "dinner", Arrays.asList("none"), Arrays.asList(), 356, 22.2, 44.5, 9.9, Arrays.asList("oats", "chickpeas", "carrots", "mushrooms", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 107", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 451, 28.2, 56.4, 12.5, Arrays.asList("oats", "chickpeas", "carrots", "spinach", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 108", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 338, 21.1, 42.2, 9.4, Arrays.asList("brown rice", "chicken breast", "mushrooms", "spinach", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 109", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 563, 35.2, 70.4, 15.6, Arrays.asList("brown rice", "tempeh", "kale", "spinach", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 110", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 379, 23.7, 47.4, 10.5, Arrays.asList("brown rice", "chickpeas", "zucchini", "bell peppers", "almonds")));
        allRecipes.add(new Recipe("Dinner Dish 111", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 368, 23.0, 46.0, 10.2, Arrays.asList("quinoa", "eggs", "tomatoes", "spinach", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 112", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 699, 43.7, 87.4, 19.4, Arrays.asList("barley", "chickpeas", "mushrooms", "kale", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 113", "lunch", Arrays.asList("none"), Arrays.asList(), 638, 39.9, 79.8, 17.7, Arrays.asList("barley", "black beans", "broccoli", "bell peppers", "cottage cheese")));
        allRecipes.add(new Recipe("Dinner Dish 114", "dinner", Arrays.asList("none"), Arrays.asList(), 486, 30.4, 60.8, 13.5, Arrays.asList("quinoa", "chicken breast", "zucchini", "spinach", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 115", "lunch", Arrays.asList("none"), Arrays.asList(), 655, 40.9, 81.9, 18.2, Arrays.asList("oats", "chicken breast", "mushrooms", "kale", "cheese")));
        allRecipes.add(new Recipe("Dinner Dish 116", "dinner", Arrays.asList("none"), Arrays.asList(), 683, 42.7, 85.4, 19.0, Arrays.asList("brown rice", "chicken breast", "zucchini", "spinach", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 117", "lunch", Arrays.asList("vegan"), Arrays.asList(), 315, 19.7, 39.4, 8.8, Arrays.asList("millet", "tempeh", "bell peppers", "spinach", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 118", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 506, 31.6, 63.2, 14.1, Arrays.asList("barley", "turkey", "mushrooms", "zucchini", "milk")));
        allRecipes.add(new Recipe("Lunch Dish 119", "lunch", Arrays.asList("none"), Arrays.asList(), 460, 28.8, 57.5, 12.8, Arrays.asList("whole wheat pasta", "chickpeas", "kale", "broccoli", "milk")));
        allRecipes.add(new Recipe("Lunch Dish 120", "lunch", Arrays.asList("vegan"), Arrays.asList(), 585, 36.6, 73.1, 16.2, Arrays.asList("brown rice", "black beans", "carrots", "broccoli", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 121", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 366, 22.9, 45.8, 10.2, Arrays.asList("brown rice", "tempeh", "carrots", "tomatoes", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 122", "dinner", Arrays.asList("none"), Arrays.asList(), 398, 24.9, 49.8, 11.1, Arrays.asList("barley", "lentils", "spinach", "kale", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 123", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 564, 35.2, 70.5, 15.7, Arrays.asList("barley", "tempeh", "mushrooms", "zucchini", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 124", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 666, 41.6, 83.2, 18.5, Arrays.asList("quinoa", "black beans", "carrots", "zucchini", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 125", "lunch", Arrays.asList("none"), Arrays.asList(), 351, 21.9, 43.9, 9.8, Arrays.asList("whole wheat bread", "chicken breast", "mushrooms", "broccoli", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 126", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 536, 33.5, 67.0, 14.9, Arrays.asList("oats", "lentils", "spinach", "mushrooms", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 127", "lunch", Arrays.asList("none"), Arrays.asList(), 616, 38.5, 77.0, 17.1, Arrays.asList("quinoa", "turkey", "spinach", "mushrooms", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 128", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 312, 19.5, 39.0, 8.7, Arrays.asList("quinoa", "tofu", "carrots", "zucchini", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 129", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 403, 25.2, 50.4, 11.2, Arrays.asList("barley", "lentils", "mushrooms", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 130", "breakfast", Arrays.asList("none"), Arrays.asList(), 581, 36.3, 72.6, 16.1, Arrays.asList("quinoa", "chickpeas", "carrots", "zucchini", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 131", "breakfast", Arrays.asList("none"), Arrays.asList(), 589, 36.8, 73.6, 16.4, Arrays.asList("oats", "tempeh", "broccoli", "mushrooms", "almonds")));
        allRecipes.add(new Recipe("Dinner Dish 132", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 346, 21.6, 43.2, 9.6, Arrays.asList("brown rice", "eggs", "broccoli", "tomatoes", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 133", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 615, 38.4, 76.9, 17.1, Arrays.asList("quinoa", "black beans", "spinach", "kale", "milk")));
        allRecipes.add(new Recipe("Breakfast Dish 134", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 341, 21.3, 42.6, 9.5, Arrays.asList("quinoa", "chicken breast", "bell peppers", "tomatoes", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 135", "dinner", Arrays.asList("none"), Arrays.asList(), 423, 26.4, 52.9, 11.8, Arrays.asList("oats", "turkey", "kale", "zucchini", "cottage cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 136", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 516, 32.2, 64.5, 14.3, Arrays.asList("quinoa", "chicken breast", "tomatoes", "bell peppers", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 137", "lunch", Arrays.asList("none"), Arrays.asList(), 522, 32.6, 65.2, 14.5, Arrays.asList("whole wheat bread", "chickpeas", "mushrooms", "zucchini", "almonds")));
        allRecipes.add(new Recipe("Lunch Dish 138", "lunch", Arrays.asList("none"), Arrays.asList(), 549, 34.3, 68.6, 15.2, Arrays.asList("whole wheat bread", "chicken breast", "carrots", "broccoli", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 139", "lunch", Arrays.asList("none"), Arrays.asList(), 545, 34.1, 68.1, 15.1, Arrays.asList("whole wheat bread", "eggs", "zucchini", "broccoli", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 140", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 590, 36.9, 73.8, 16.4, Arrays.asList("whole wheat pasta", "eggs", "tomatoes", "zucchini", "cottage cheese")));
        allRecipes.add(new Recipe("Dinner Dish 141", "dinner", Arrays.asList("vegan"), Arrays.asList(), 341, 21.3, 42.6, 9.5, Arrays.asList("barley", "black beans", "carrots", "mushrooms", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 142", "breakfast", Arrays.asList("none"), Arrays.asList(), 484, 30.2, 60.5, 13.4, Arrays.asList("whole wheat pasta", "chicken breast", "carrots", "bell peppers", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 143", "dinner", Arrays.asList("none"), Arrays.asList(), 534, 33.4, 66.8, 14.8, Arrays.asList("quinoa", "tofu", "bell peppers", "zucchini", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 144", "lunch", Arrays.asList("vegan"), Arrays.asList(), 623, 38.9, 77.9, 17.3, Arrays.asList("millet", "lentils", "carrots", "spinach", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 145", "lunch", Arrays.asList("none"), Arrays.asList(), 330, 20.6, 41.2, 9.2, Arrays.asList("brown rice", "lentils", "broccoli", "tomatoes", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 146", "lunch", Arrays.asList("vegan"), Arrays.asList(), 588, 36.8, 73.5, 16.3, Arrays.asList("whole wheat bread", "tofu", "bell peppers", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 147", "dinner", Arrays.asList("none"), Arrays.asList(), 609, 38.1, 76.1, 16.9, Arrays.asList("millet", "chicken breast", "broccoli", "bell peppers", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 148", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 612, 38.2, 76.5, 17.0, Arrays.asList("brown rice", "lentils", "zucchini", "spinach", "milk")));
        allRecipes.add(new Recipe("Lunch Dish 149", "lunch", Arrays.asList("none"), Arrays.asList(), 385, 24.1, 48.1, 10.7, Arrays.asList("oats", "eggs", "spinach", "zucchini", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 150", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 319, 19.9, 39.9, 8.9, Arrays.asList("whole wheat bread", "chickpeas", "tomatoes", "zucchini", "cottage cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 151", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 646, 40.4, 80.8, 17.9, Arrays.asList("quinoa", "black beans", "zucchini", "bell peppers", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 152", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 617, 38.6, 77.1, 17.1, Arrays.asList("oats", "black beans", "zucchini", "tomatoes", "cottage cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 153", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 682, 42.6, 85.2, 18.9, Arrays.asList("oats", "eggs", "spinach", "zucchini", "milk")));
        allRecipes.add(new Recipe("Lunch Dish 154", "lunch", Arrays.asList("vegan"), Arrays.asList(), 525, 32.8, 65.6, 14.6, Arrays.asList("brown rice", "lentils", "carrots", "zucchini", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 155", "lunch", Arrays.asList("none"), Arrays.asList(), 521, 32.6, 65.1, 14.5, Arrays.asList("millet", "chicken breast", "carrots", "broccoli", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 156", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 497, 31.1, 62.1, 13.8, Arrays.asList("brown rice", "tempeh", "kale", "bell peppers", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 157", "lunch", Arrays.asList("none"), Arrays.asList(), 409, 25.6, 51.1, 11.4, Arrays.asList("brown rice", "eggs", "spinach", "tomatoes", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 158", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 351, 21.9, 43.9, 9.8, Arrays.asList("oats", "tempeh", "tomatoes", "broccoli", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 159", "lunch", Arrays.asList("vegan"), Arrays.asList(), 476, 29.8, 59.5, 13.2, Arrays.asList("quinoa", "lentils", "zucchini", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 160", "breakfast", Arrays.asList("none"), Arrays.asList(), 682, 42.6, 85.2, 18.9, Arrays.asList("oats", "eggs", "carrots", "mushrooms", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 161", "dinner", Arrays.asList("vegan"), Arrays.asList(), 582, 36.4, 72.8, 16.2, Arrays.asList("millet", "black beans", "kale", "broccoli", "avocado")));
        allRecipes.add(new Recipe("Dinner Dish 162", "dinner", Arrays.asList("none"), Arrays.asList(), 376, 23.5, 47.0, 10.4, Arrays.asList("whole wheat pasta", "tempeh", "carrots", "zucchini", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 163", "dinner", Arrays.asList("none"), Arrays.asList(), 619, 38.7, 77.4, 17.2, Arrays.asList("whole wheat pasta", "chicken breast", "zucchini", "spinach", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 164", "dinner", Arrays.asList("none"), Arrays.asList(), 520, 32.5, 65.0, 14.4, Arrays.asList("quinoa", "eggs", "broccoli", "mushrooms", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 165", "lunch", Arrays.asList("vegan"), Arrays.asList(), 359, 22.4, 44.9, 10.0, Arrays.asList("whole wheat bread", "lentils", "bell peppers", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 166", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 444, 27.8, 55.5, 12.3, Arrays.asList("quinoa", "tempeh", "carrots", "spinach", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 167", "lunch", Arrays.asList("vegan"), Arrays.asList(), 485, 30.3, 60.6, 13.5, Arrays.asList("barley", "black beans", "carrots", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 168", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 627, 39.2, 78.4, 17.4, Arrays.asList("oats", "tofu", "spinach", "broccoli", "yogurt")));
        allRecipes.add(new Recipe("Breakfast Dish 169", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 556, 34.8, 69.5, 15.4, Arrays.asList("brown rice", "black beans", "bell peppers", "mushrooms", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 170", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 678, 42.4, 84.8, 18.8, Arrays.asList("brown rice", "lentils", "kale", "mushrooms", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 171", "lunch", Arrays.asList("none"), Arrays.asList(), 608, 38.0, 76.0, 16.9, Arrays.asList("millet", "chickpeas", "spinach", "zucchini", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 172", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 361, 22.6, 45.1, 10.0, Arrays.asList("oats", "tofu", "tomatoes", "spinach", "cheese")));
        allRecipes.add(new Recipe("Dinner Dish 173", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 594, 37.1, 74.2, 16.5, Arrays.asList("whole wheat bread", "lentils", "spinach", "zucchini", "cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 174", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 368, 23.0, 46.0, 10.2, Arrays.asList("oats", "lentils", "mushrooms", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 175", "lunch", Arrays.asList("none"), Arrays.asList(), 695, 43.4, 86.9, 19.3, Arrays.asList("whole wheat bread", "chickpeas", "mushrooms", "tomatoes", "almonds")));
        allRecipes.add(new Recipe("Lunch Dish 176", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 568, 35.5, 71.0, 15.8, Arrays.asList("whole wheat bread", "tofu", "carrots", "tomatoes", "cottage cheese")));
        allRecipes.add(new Recipe("Dinner Dish 177", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 392, 24.5, 49.0, 10.9, Arrays.asList("quinoa", "chickpeas", "broccoli", "carrots", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 178", "lunch", Arrays.asList("none"), Arrays.asList(), 379, 23.7, 47.4, 10.5, Arrays.asList("oats", "black beans", "carrots", "zucchini", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 179", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 599, 37.4, 74.9, 16.6, Arrays.asList("quinoa", "turkey", "broccoli", "zucchini", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 180", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 327, 20.4, 40.9, 9.1, Arrays.asList("quinoa", "chickpeas", "spinach", "broccoli", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 181", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 552, 34.5, 69.0, 15.3, Arrays.asList("quinoa", "lentils", "mushrooms", "tomatoes", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 182", "dinner", Arrays.asList("vegan"), Arrays.asList(), 485, 30.3, 60.6, 13.5, Arrays.asList("brown rice", "tofu", "bell peppers", "kale", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 183", "dinner", Arrays.asList("none"), Arrays.asList(), 431, 26.9, 53.9, 12.0, Arrays.asList("quinoa", "chicken breast", "tomatoes", "bell peppers", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 184", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 446, 27.9, 55.8, 12.4, Arrays.asList("quinoa", "black beans", "zucchini", "spinach", "milk")));
        allRecipes.add(new Recipe("Breakfast Dish 185", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 441, 27.6, 55.1, 12.2, Arrays.asList("quinoa", "tofu", "kale", "bell peppers", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 186", "lunch", Arrays.asList("vegan"), Arrays.asList(), 418, 26.1, 52.2, 11.6, Arrays.asList("brown rice", "tempeh", "zucchini", "spinach", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 187", "dinner", Arrays.asList("vegan"), Arrays.asList(), 648, 40.5, 81.0, 18.0, Arrays.asList("barley", "tempeh", "tomatoes", "bell peppers", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 188", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 345, 21.6, 43.1, 9.6, Arrays.asList("quinoa", "chickpeas", "broccoli", "carrots", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 189", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 648, 40.5, 81.0, 18.0, Arrays.asList("quinoa", "chicken breast", "broccoli", "spinach", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 190", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 444, 27.8, 55.5, 12.3, Arrays.asList("barley", "lentils", "mushrooms", "bell peppers", "milk")));
        allRecipes.add(new Recipe("Dinner Dish 191", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 387, 24.2, 48.4, 10.8, Arrays.asList("barley", "black beans", "broccoli", "carrots", "yogurt")));
        allRecipes.add(new Recipe("Dinner Dish 192", "dinner", Arrays.asList("vegan"), Arrays.asList(), 653, 40.8, 81.6, 18.1, Arrays.asList("brown rice", "tofu", "broccoli", "bell peppers", "avocado")));
        allRecipes.add(new Recipe("Dinner Dish 193", "dinner", Arrays.asList("none"), Arrays.asList(), 519, 32.4, 64.9, 14.4, Arrays.asList("barley", "tofu", "tomatoes", "carrots", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 194", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 525, 32.8, 65.6, 14.6, Arrays.asList("brown rice", "eggs", "tomatoes", "mushrooms", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 195", "breakfast", Arrays.asList("none"), Arrays.asList(), 511, 31.9, 63.9, 14.2, Arrays.asList("oats", "chicken breast", "zucchini", "tomatoes", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 196", "lunch", Arrays.asList("none"), Arrays.asList(), 306, 19.1, 38.2, 8.5, Arrays.asList("brown rice", "tempeh", "broccoli", "bell peppers", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 197", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 462, 28.9, 57.8, 12.8, Arrays.asList("quinoa", "black beans", "spinach", "tomatoes", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 198", "lunch", Arrays.asList("none"), Arrays.asList(), 437, 27.3, 54.6, 12.1, Arrays.asList("whole wheat pasta", "black beans", "spinach", "broccoli", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 199", "lunch", Arrays.asList("vegan"), Arrays.asList(), 398, 24.9, 49.8, 11.1, Arrays.asList("whole wheat bread", "black beans", "spinach", "carrots", "olive oil")));
        allRecipes.add(new Recipe("Lunch Dish 200", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 310, 19.4, 38.8, 8.6, Arrays.asList("quinoa", "lentils", "mushrooms", "bell peppers", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 201", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 351, 21.9, 43.9, 9.8, Arrays.asList("quinoa", "black beans", "bell peppers", "broccoli", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 202", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 593, 37.1, 74.1, 16.5, Arrays.asList("brown rice", "lentils", "broccoli", "kale", "peanut butter")));
        allRecipes.add(new Recipe("Breakfast Dish 203", "breakfast", Arrays.asList("none"), Arrays.asList(), 621, 38.8, 77.6, 17.2, Arrays.asList("oats", "lentils", "kale", "zucchini", "yogurt")));
        allRecipes.add(new Recipe("Lunch Dish 204", "lunch", Arrays.asList("vegan"), Arrays.asList(), 523, 32.7, 65.4, 14.5, Arrays.asList("whole wheat bread", "lentils", "kale", "broccoli", "almonds")));
        allRecipes.add(new Recipe("Lunch Dish 205", "lunch", Arrays.asList("none"), Arrays.asList(), 496, 31.0, 62.0, 13.8, Arrays.asList("oats", "chicken breast", "tomatoes", "kale", "almonds")));
        allRecipes.add(new Recipe("Lunch Dish 206", "lunch", Arrays.asList("vegan"), Arrays.asList(), 470, 29.4, 58.8, 13.1, Arrays.asList("barley", "lentils", "zucchini", "bell peppers", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 207", "breakfast", Arrays.asList("none"), Arrays.asList(), 371, 23.2, 46.4, 10.3, Arrays.asList("barley", "turkey", "tomatoes", "zucchini", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 208", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 631, 39.4, 78.9, 17.5, Arrays.asList("brown rice", "chicken breast", "carrots", "mushrooms", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 209", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 543, 33.9, 67.9, 15.1, Arrays.asList("brown rice", "tempeh", "tomatoes", "zucchini", "yogurt")));
        allRecipes.add(new Recipe("Breakfast Dish 210", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 304, 19.0, 38.0, 8.4, Arrays.asList("barley", "tofu", "kale", "bell peppers", "yogurt")));
        allRecipes.add(new Recipe("Breakfast Dish 211", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 632, 39.5, 79.0, 17.6, Arrays.asList("quinoa", "chicken breast", "tomatoes", "carrots", "avocado")));
        allRecipes.add(new Recipe("Dinner Dish 212", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 655, 40.9, 81.9, 18.2, Arrays.asList("brown rice", "tofu", "kale", "zucchini", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 213", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 332, 20.8, 41.5, 9.2, Arrays.asList("whole wheat pasta", "eggs", "zucchini", "spinach", "milk")));
        allRecipes.add(new Recipe("Lunch Dish 214", "lunch", Arrays.asList("vegan"), Arrays.asList(), 520, 32.5, 65.0, 14.4, Arrays.asList("millet", "tempeh", "bell peppers", "mushrooms", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 215", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 433, 27.1, 54.1, 12.0, Arrays.asList("brown rice", "tofu", "zucchini", "carrots", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 216", "lunch", Arrays.asList("none"), Arrays.asList(), 422, 26.4, 52.8, 11.7, Arrays.asList("oats", "lentils", "zucchini", "broccoli", "milk")));
        allRecipes.add(new Recipe("Dinner Dish 217", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 561, 35.1, 70.1, 15.6, Arrays.asList("brown rice", "black beans", "zucchini", "spinach", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 218", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 441, 27.6, 55.1, 12.2, Arrays.asList("barley", "turkey", "broccoli", "carrots", "milk")));
        allRecipes.add(new Recipe("Breakfast Dish 219", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 523, 32.7, 65.4, 14.5, Arrays.asList("brown rice", "tofu", "bell peppers", "tomatoes", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 220", "lunch", Arrays.asList("none"), Arrays.asList(), 695, 43.4, 86.9, 19.3, Arrays.asList("whole wheat pasta", "chickpeas", "carrots", "tomatoes", "milk")));
        allRecipes.add(new Recipe("Breakfast Dish 221", "breakfast", Arrays.asList("none"), Arrays.asList(), 670, 41.9, 83.8, 18.6, Arrays.asList("oats", "chickpeas", "spinach", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 222", "dinner", Arrays.asList("none"), Arrays.asList(), 675, 42.2, 84.4, 18.8, Arrays.asList("barley", "black beans", "bell peppers", "mushrooms", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 223", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 343, 21.4, 42.9, 9.5, Arrays.asList("quinoa", "tempeh", "mushrooms", "broccoli", "avocado")));
        allRecipes.add(new Recipe("Dinner Dish 224", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 528, 33.0, 66.0, 14.7, Arrays.asList("brown rice", "lentils", "zucchini", "bell peppers", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 225", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 391, 24.4, 48.9, 10.9, Arrays.asList("barley", "chickpeas", "kale", "spinach", "yogurt")));
        allRecipes.add(new Recipe("Breakfast Dish 226", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 677, 42.3, 84.6, 18.8, Arrays.asList("brown rice", "eggs", "tomatoes", "spinach", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 227", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 662, 41.4, 82.8, 18.4, Arrays.asList("quinoa", "chicken breast", "kale", "zucchini", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 228", "lunch", Arrays.asList("none"), Arrays.asList(), 507, 31.7, 63.4, 14.1, Arrays.asList("brown rice", "tempeh", "bell peppers", "spinach", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 229", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 609, 38.1, 76.1, 16.9, Arrays.asList("whole wheat bread", "tofu", "carrots", "spinach", "milk")));
        allRecipes.add(new Recipe("Lunch Dish 230", "lunch", Arrays.asList("none"), Arrays.asList(), 483, 30.2, 60.4, 13.4, Arrays.asList("whole wheat pasta", "lentils", "bell peppers", "carrots", "cottage cheese")));
        allRecipes.add(new Recipe("Lunch Dish 231", "lunch", Arrays.asList("vegetarian"), Arrays.asList(), 340, 21.2, 42.5, 9.4, Arrays.asList("brown rice", "chickpeas", "broccoli", "carrots", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 232", "lunch", Arrays.asList("none"), Arrays.asList(), 669, 41.8, 83.6, 18.6, Arrays.asList("brown rice", "eggs", "tomatoes", "spinach", "cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 233", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 651, 40.7, 81.4, 18.1, Arrays.asList("brown rice", "lentils", "spinach", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 234", "dinner", Arrays.asList("none"), Arrays.asList(), 520, 32.5, 65.0, 14.4, Arrays.asList("whole wheat bread", "chicken breast", "kale", "carrots", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 235", "dinner", Arrays.asList("none"), Arrays.asList(), 697, 43.6, 87.1, 19.4, Arrays.asList("whole wheat pasta", "chicken breast", "carrots", "tomatoes", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 236", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 637, 39.8, 79.6, 17.7, Arrays.asList("brown rice", "eggs", "tomatoes", "kale", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 237", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 486, 30.4, 60.8, 13.5, Arrays.asList("whole wheat pasta", "chickpeas", "tomatoes", "zucchini", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 238", "lunch", Arrays.asList("none"), Arrays.asList(), 529, 33.1, 66.1, 14.7, Arrays.asList("brown rice", "chicken breast", "spinach", "mushrooms", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 239", "lunch", Arrays.asList("none"), Arrays.asList(), 611, 38.2, 76.4, 17.0, Arrays.asList("millet", "lentils", "kale", "zucchini", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 240", "dinner", Arrays.asList("vegan"), Arrays.asList(), 548, 34.2, 68.5, 15.2, Arrays.asList("barley", "black beans", "kale", "broccoli", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 241", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 455, 28.4, 56.9, 12.6, Arrays.asList("brown rice", "chicken breast", "carrots", "mushrooms", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 242", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 528, 33.0, 66.0, 14.7, Arrays.asList("millet", "tempeh", "tomatoes", "kale", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 243", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 653, 40.8, 81.6, 18.1, Arrays.asList("quinoa", "lentils", "kale", "broccoli", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 244", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 448, 28.0, 56.0, 12.4, Arrays.asList("millet", "eggs", "mushrooms", "carrots", "cottage cheese")));
        allRecipes.add(new Recipe("Dinner Dish 245", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 479, 29.9, 59.9, 13.3, Arrays.asList("millet", "lentils", "carrots", "bell peppers", "cottage cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 246", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 588, 36.8, 73.5, 16.3, Arrays.asList("whole wheat pasta", "tofu", "broccoli", "kale", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 247", "dinner", Arrays.asList("none"), Arrays.asList(), 326, 20.4, 40.8, 9.1, Arrays.asList("millet", "turkey", "kale", "spinach", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 248", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 695, 43.4, 86.9, 19.3, Arrays.asList("millet", "black beans", "kale", "mushrooms", "olive oil")));
        allRecipes.add(new Recipe("Dinner Dish 249", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 514, 32.1, 64.2, 14.3, Arrays.asList("millet", "tofu", "kale", "spinach", "cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 250", "breakfast", Arrays.asList("none"), Arrays.asList(), 473, 29.6, 59.1, 13.1, Arrays.asList("whole wheat pasta", "chicken breast", "spinach", "carrots", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 251", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 635, 39.7, 79.4, 17.6, Arrays.asList("brown rice", "turkey", "bell peppers", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 252", "breakfast", Arrays.asList("none"), Arrays.asList(), 470, 29.4, 58.8, 13.1, Arrays.asList("barley", "lentils", "carrots", "mushrooms", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 253", "breakfast", Arrays.asList("gluten_free"), Arrays.asList(), 477, 29.8, 59.6, 13.2, Arrays.asList("brown rice", "tofu", "tomatoes", "carrots", "avocado")));
        allRecipes.add(new Recipe("Breakfast Dish 254", "breakfast", Arrays.asList("none"), Arrays.asList(), 685, 42.8, 85.6, 19.0, Arrays.asList("millet", "lentils", "mushrooms", "tomatoes", "favorite nuts")));
        allRecipes.add(new Recipe("Lunch Dish 255", "lunch", Arrays.asList("vegan"), Arrays.asList(), 301, 18.8, 37.6, 8.4, Arrays.asList("oats", "tempeh", "zucchini", "broccoli", "peanut butter")));
        allRecipes.add(new Recipe("Dinner Dish 256", "dinner", Arrays.asList("gluten_free"), Arrays.asList(), 604, 37.8, 75.5, 16.8, Arrays.asList("brown rice", "chickpeas", "zucchini", "spinach", "avocado")));
        allRecipes.add(new Recipe("Lunch Dish 257", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 301, 18.8, 37.6, 8.4, Arrays.asList("quinoa", "turkey", "zucchini", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Dinner Dish 258", "dinner", Arrays.asList("vegetarian"), Arrays.asList(), 589, 36.8, 73.6, 16.4, Arrays.asList("millet", "black beans", "spinach", "tomatoes", "cheese")));
        allRecipes.add(new Recipe("Breakfast Dish 259", "breakfast", Arrays.asList("none"), Arrays.asList(), 400, 25.0, 50.0, 11.1, Arrays.asList("barley", "chickpeas", "carrots", "broccoli", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 260", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 636, 39.8, 79.5, 17.7, Arrays.asList("barley", "lentils", "kale", "carrots", "cheese")));
        allRecipes.add(new Recipe("Lunch Dish 261", "lunch", Arrays.asList("vegan"), Arrays.asList(), 642, 40.1, 80.2, 17.8, Arrays.asList("brown rice", "lentils", "kale", "broccoli", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 262", "lunch", Arrays.asList("vegan"), Arrays.asList(), 563, 35.2, 70.4, 15.6, Arrays.asList("oats", "tempeh", "carrots", "kale", "favorite nuts")));
        allRecipes.add(new Recipe("Breakfast Dish 263", "breakfast", Arrays.asList("none"), Arrays.asList(), 423, 26.4, 52.9, 11.8, Arrays.asList("oats", "eggs", "kale", "mushrooms", "peanut butter")));
        allRecipes.add(new Recipe("Lunch Dish 264", "lunch", Arrays.asList("none"), Arrays.asList(), 402, 25.1, 50.2, 11.2, Arrays.asList("barley", "tofu", "bell peppers", "kale", "olive oil")));
        allRecipes.add(new Recipe("Breakfast Dish 265", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 504, 31.5, 63.0, 14.0, Arrays.asList("brown rice", "tempeh", "kale", "mushrooms", "milk")));
        allRecipes.add(new Recipe("Lunch Dish 266", "lunch", Arrays.asList("gluten_free"), Arrays.asList(), 337, 21.1, 42.1, 9.4, Arrays.asList("brown rice", "eggs", "mushrooms", "zucchini", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 267", "breakfast", Arrays.asList("vegetarian"), Arrays.asList(), 357, 22.3, 44.6, 9.9, Arrays.asList("whole wheat bread", "eggs", "mushrooms", "tomatoes", "milk")));
        allRecipes.add(new Recipe("Dinner Dish 268", "dinner", Arrays.asList("vegan"), Arrays.asList(), 439, 27.4, 54.9, 12.2, Arrays.asList("barley", "lentils", "bell peppers", "broccoli", "almonds")));
        allRecipes.add(new Recipe("Dinner Dish 269", "dinner", Arrays.asList("none"), Arrays.asList(), 612, 38.2, 76.5, 17.0, Arrays.asList("whole wheat bread", "chicken breast", "bell peppers", "kale", "almonds")));
        allRecipes.add(new Recipe("Breakfast Dish 270", "breakfast", Arrays.asList("vegan"), Arrays.asList(), 440, 27.5, 55.0, 12.2, Arrays.asList("millet", "black beans", "carrots", "broccoli", "favorite nuts")));
             // Continue adding more to reach 1000 allRecipes...
    }

    void initializeExercises() {
        allExercises.add(new Exercise("Push-ups", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dips", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Chest Press", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Incline Chest Press", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Decline Chest Press", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Bicep Curls", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Push-ups Dumbbells", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Pull-ups", "Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Shoulder Press", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Lat Raises", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Dips", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Chest Press", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Incline Chest Press", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Decline Chest Press", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Bicep Curls", "Dumbbells", "Medium", "upper_body"));

        allExercises.add(new Exercise("Squats", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Lunges", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Deadlifts", "None", "High", "lower_body"));
        allExercises.add(new Exercise("RDOs", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Squats With Dumbbells", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Lunges with Dumbbells", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Calf Raises", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Deadlifts", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("RDOs", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Squats With Dumbbells", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Lunges with Dumbbells", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Calf Raises", "Dumbbells", "Low", "lower_body"));

        allExercises.add(new Exercise("Plank", "None", "Medium", "core"));
        allExercises.add(new Exercise("Sit-ups", "Bar", "Low", "core"));
        allExercises.add(new Exercise("Russian Twists", "None", "Medium", "core"));
        allExercises.add(new Exercise("French Twists", "None", "Medium", "core"));
        allExercises.add(new Exercise("Indian Twists", "None", "Medium", "core"));
        allExercises.add(new Exercise("Leg Raises", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Russian Twists", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("French Twists", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Indian Twists", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Leg Raises", "Dumbbells", "Medium", "core"));

        allExercises.add(new Exercise("Jump Rope", "Bar", "High", "cardio"));
        allExercises.add(new Exercise("Burpees", "Dumbbells", "High", "cardio"));
        allExercises.add(new Exercise("Running", "Bike", "Medium", "cardio"));
        allExercises.add(new Exercise("Cycling", "Bike", "Medium", "cardio"));

        allExercises.add(new Exercise("Jog Twists", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Run Steps", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Hop Squats", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Dip Raises", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Crunch Lunges", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Squats", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Climb Raises", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Pull Raises", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Push Lunges", "Medicine Ball", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Downs", "Rope", "High", "core"));
        allExercises.add(new Exercise("Hop Burpees", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Press Rows", "Barbell", "Medium", "cardio"));
        allExercises.add(new Exercise("Pull Lunges", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Raise Jacks", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Lift Steps", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Press Lunges", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Run Jacks", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Jog Downs", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Raise Lunges", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Hop Lunges", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Plank", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Climb Downs", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Jog Raises", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Twists", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Rows", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Swing Squats", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Pull Burpees", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Push Steps", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Rows", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Hop Rows", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Climb Hops", "Dumbbells", "High", "cardio"));
        allExercises.add(new Exercise("Crunch Jacks", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Lunges", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Raises", "Medicine Ball", "Medium", "core"));
        allExercises.add(new Exercise("Swing Burpees", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Run Burpees", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Swing Lunges", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Lift Jacks", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Pull Ups", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Press Rows", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Jump Rows", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Press Ups", "None", "High", "cardio"));
        allExercises.add(new Exercise("Push Raises", "Rope", "High", "core"));
        allExercises.add(new Exercise("Raise Squats", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Swing Lunges", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Push Lunges", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Swing Plank", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Raise Hops", "Pull-up Bar", "Low", "upper_body"));
        allExercises.add(new Exercise("Lift Hops", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Lunges", "Resistance Bands", "Medium", "cardio"));
        allExercises.add(new Exercise("Climb Jacks", "Medicine Ball", "High", "upper_body"));
        allExercises.add(new Exercise("Raise Rows", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Downs", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Rows", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Dip Rows", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Pull Jacks", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Lunges", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Press Burpees", "Rope", "High", "core"));
        allExercises.add(new Exercise("Raise Squats", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Climb Squats", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Push Squats", "Rope", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Steps", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jump Downs", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Skip Plank", "Rope", "Medium", "cardio"));
        allExercises.add(new Exercise("Crunch Ups", "None", "High", "core"));
        allExercises.add(new Exercise("Press Hops", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Pull Lunges", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Crunch Plank", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Jump Raises", "Barbell", "Low", "lower_body"));
        allExercises.add(new Exercise("Press Plank", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Twists", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Run Ups", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Hops", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Jog Downs", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Hop Rows", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Raise Hops", "Medicine Ball", "Medium", "core"));
        allExercises.add(new Exercise("Lift Rows", "Pull-up Bar", "Medium", "core"));
        allExercises.add(new Exercise("Run Burpees", "Barbell", "Low", "lower_body"));
        allExercises.add(new Exercise("Jump Raises", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Raises", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Hop Steps", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Raise Plank", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Press Downs", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jump Ups", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Jacks", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Lift Jacks", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Push Lunges", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Swing Lunges", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Climb Raises", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Pull Steps", "Resistance Bands", "High", "upper_body"));
        allExercises.add(new Exercise("Crunch Downs", "Medicine Ball", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Downs", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Skip Hops", "Barbell", "Medium", "core"));
        allExercises.add(new Exercise("Swing Burpees", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Skip Lunges", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Squats", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Swing Plank", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Run Downs", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jog Raises", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Twists", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Raise Downs", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Dip Burpees", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Hop Ups", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Climb Plank", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Jump Hops", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Hop Twists", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Run Downs", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Lift Raises", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Swing Downs", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Crunch Steps", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Jump Jacks", "Medicine Ball", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Jacks", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Push Twists", "Rope", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Squats", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Press Downs", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Hop Ups", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Jump Rows", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Pull Steps", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Lift Squats", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Jog Rows", "Medicine Ball", "Low", "lower_body"));
        allExercises.add(new Exercise("Jump Jacks", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Hop Raises", "Pull-up Bar", "Medium", "core"));
        allExercises.add(new Exercise("Skip Jacks", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Jog Squats", "Rope", "Medium", "cardio"));
        allExercises.add(new Exercise("Lift Jacks", "None", "Low", "lower_body"));
        allExercises.add(new Exercise("Raise Ups", "Rope", "Medium", "cardio"));
        allExercises.add(new Exercise("Hop Hops", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Ups", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Jump Burpees", "Resistance Bands", "Medium", "lower_body"));
        allExercises.add(new Exercise("Lift Burpees", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Lunges", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Pull Rows", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Push Ups", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Climb Plank", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Lift Steps", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Jump Downs", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Jump Ups", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Hops", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Rows", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Press Twists", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Crunch Hops", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Swing Burpees", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Run Ups", "Dumbbells", "High", "cardio"));
        allExercises.add(new Exercise("Swing Lunges", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Raises", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Skip Ups", "Rope", "Medium", "cardio"));
        allExercises.add(new Exercise("Jog Squats", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Lift Raises", "Barbell", "Low", "lower_body"));
        allExercises.add(new Exercise("Swing Downs", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Push Plank", "Pull-up Bar", "Low", "upper_body"));
        allExercises.add(new Exercise("Skip Downs", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Burpees", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Skip Lunges", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Swing Downs", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Raise Twists", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Jog Lunges", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Pull Raises", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Crunch Ups", "Resistance Bands", "High", "upper_body"));
        allExercises.add(new Exercise("Crunch Ups", "Pull-up Bar", "Low", "core"));
        allExercises.add(new Exercise("Crunch Ups", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Lift Raises", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Downs", "Resistance Bands", "High", "upper_body"));
        allExercises.add(new Exercise("Raise Hops", "None", "Low", "lower_body"));
        allExercises.add(new Exercise("Skip Burpees", "Rope", "High", "core"));
        allExercises.add(new Exercise("Jog Twists", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Dip Twists", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Crunch Raises", "None", "High", "core"));
        allExercises.add(new Exercise("Run Hops", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Dip Downs", "Pull-up Bar", "Medium", "core"));
        allExercises.add(new Exercise("Raise Jacks", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Hop Hops", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Hop Downs", "Rope", "Low", "upper_body"));
        allExercises.add(new Exercise("Skip Downs", "None", "Low", "core"));
        allExercises.add(new Exercise("Skip Burpees", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Press Hops", "Resistance Bands", "Medium", "cardio"));
        allExercises.add(new Exercise("Crunch Jacks", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Crunch Downs", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Jog Squats", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Squats", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Twists", "Rope", "High", "core"));
        allExercises.add(new Exercise("Swing Squats", "Medicine Ball", "Medium", "core"));
        allExercises.add(new Exercise("Hop Rows", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Swing Raises", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Climb Downs", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Steps", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Rows", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Jog Plank", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Crunch Squats", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Swing Steps", "Barbell", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Raises", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Dip Twists", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Run Ups", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Climb Raises", "Rope", "Medium", "lower_body"));
        allExercises.add(new Exercise("Lift Raises", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Jog Raises", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Jacks", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Lift Downs", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Swing Downs", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Lift Raises", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Crunch Ups", "Dumbbells", "High", "cardio"));
        allExercises.add(new Exercise("Raise Lunges", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Burpees", "Rope", "High", "core"));
        allExercises.add(new Exercise("Hop Squats", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Push Ups", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Run Steps", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Steps", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Squats", "None", "High", "cardio"));
        allExercises.add(new Exercise("Pull Twists", "Pull-up Bar", "Low", "core"));
        allExercises.add(new Exercise("Crunch Steps", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Push Lunges", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Climb Squats", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Dip Twists", "None", "Low", "core"));
        allExercises.add(new Exercise("Press Lunges", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Crunch Lunges", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Hop Raises", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Jump Rows", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Jog Ups", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Climb Raises", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Jog Ups", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Push Ups", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Rows", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Pull Burpees", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Jog Steps", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Raises", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Crunch Twists", "Resistance Bands", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Downs", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Squats", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Rows", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Dip Burpees", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Run Downs", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Pull Rows", "Barbell", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Burpees", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Raise Twists", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Skip Raises", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Skip Ups", "None", "High", "cardio"));
        allExercises.add(new Exercise("Climb Burpees", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Lift Squats", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Run Hops", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Raise Steps", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Push Hops", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Dip Twists", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Run Raises", "Medicine Ball", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Lunges", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Skip Jacks", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Jump Jacks", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Raise Twists", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Raise Raises", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Dip Lunges", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Jump Plank", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Plank", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Press Plank", "Pull-up Bar", "Medium", "lower_body"));
        allExercises.add(new Exercise("Skip Twists", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Jog Squats", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Climb Squats", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Hop Lunges", "None", "Low", "lower_body"));
        allExercises.add(new Exercise("Swing Jacks", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Jacks", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Downs", "Pull-up Bar", "Low", "upper_body"));
        allExercises.add(new Exercise("Lift Burpees", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Press Rows", "Medicine Ball", "High", "core"));
        allExercises.add(new Exercise("Press Plank", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Lift Rows", "Medicine Ball", "High", "core"));
        allExercises.add(new Exercise("Jog Hops", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Raise Jacks", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Raise Steps", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Skip Burpees", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Climb Plank", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Climb Ups", "Medicine Ball", "Medium", "core"));
        allExercises.add(new Exercise("Lift Ups", "Rope", "High", "core"));
        allExercises.add(new Exercise("Lift Hops", "Barbell", "Medium", "cardio"));
        allExercises.add(new Exercise("Crunch Downs", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Press Downs", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Crunch Downs", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Push Rows", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Skip Downs", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Hop Burpees", "Rope", "High", "core"));
        allExercises.add(new Exercise("Jog Squats", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Skip Hops", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Raise Plank", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Steps", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Rows", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Hops", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Skip Lunges", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Hops", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Raise Burpees", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Climb Burpees", "Dumbbells", "High", "cardio"));
        allExercises.add(new Exercise("Run Burpees", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Push Burpees", "Pull-up Bar", "Low", "core"));
        allExercises.add(new Exercise("Hop Hops", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Hop Downs", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Crunch Rows", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Crunch Downs", "Pull-up Bar", "Medium", "core"));
        allExercises.add(new Exercise("Climb Downs", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Jump Raises", "None", "Medium", "core"));
        allExercises.add(new Exercise("Run Downs", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Lift Lunges", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Pull Twists", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Hop Hops", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Climb Steps", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Swing Hops", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Jump Raises", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Skip Raises", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Run Ups", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Plank", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Jump Downs", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Crunch Plank", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Skip Ups", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Raises", "None", "Low", "core"));
        allExercises.add(new Exercise("Climb Burpees", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Pull Hops", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Skip Downs", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Run Raises", "Barbell", "Medium", "cardio"));
        allExercises.add(new Exercise("Raise Lunges", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Swing Raises", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Downs", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Hop Ups", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Lift Raises", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Pull Ups", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Push Plank", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Run Steps", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Plank", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Dip Jacks", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Lift Burpees", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Crunch Squats", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Hops", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Jump Rows", "Resistance Bands", "Medium", "lower_body"));
        allExercises.add(new Exercise("Lift Hops", "Resistance Bands", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Ups", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Pull Rows", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Swing Plank", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Pull Downs", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Jump Burpees", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Crunch Burpees", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Push Ups", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Run Hops", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Downs", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Climb Rows", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Pull Ups", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Raise Twists", "Resistance Bands", "High", "upper_body"));
        allExercises.add(new Exercise("Dip Lunges", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Swing Ups", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Run Downs", "Resistance Bands", "Medium", "lower_body"));
        allExercises.add(new Exercise("Press Steps", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Push Raises", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Climb Lunges", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Climb Downs", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Ups", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Raises", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Burpees", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Jump Squats", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Lift Squats", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Squats", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Squats", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Jacks", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Plank", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Squats", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Plank", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Dip Rows", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Climb Plank", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Skip Plank", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Lift Burpees", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jog Burpees", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Push Ups", "Rope", "Medium", "cardio"));
        allExercises.add(new Exercise("Jump Lunges", "Rope", "High", "core"));
        allExercises.add(new Exercise("Crunch Ups", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Push Jacks", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Run Squats", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Ups", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Press Rows", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Jog Rows", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Downs", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Jump Steps", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Climb Twists", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Run Plank", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Jacks", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Jump Twists", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Push Burpees", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Lift Rows", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Press Lunges", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Jump Squats", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Jump Hops", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Run Burpees", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Jog Squats", "Resistance Bands", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jog Ups", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Burpees", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Jacks", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Dip Raises", "Pull-up Bar", "Medium", "core"));
        allExercises.add(new Exercise("Run Downs", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Skip Ups", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Plank", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Raise Hops", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Plank", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Crunch Lunges", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Jog Downs", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Ups", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Lunges", "Medicine Ball", "High", "upper_body"));
        allExercises.add(new Exercise("Swing Plank", "Rope", "High", "upper_body"));
        allExercises.add(new Exercise("Raise Burpees", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Lift Lunges", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jog Rows", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Crunch Plank", "Medicine Ball", "Medium", "upper_body"));
        allExercises.add(new Exercise("Swing Ups", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Run Jacks", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Swing Steps", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Jump Jacks", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Skip Steps", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Swing Ups", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Lunges", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Crunch Downs", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Downs", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Raises", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Lift Burpees", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Downs", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Jacks", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Run Jacks", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Skip Rows", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Press Downs", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Dip Rows", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Jog Ups", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Hop Burpees", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Plank", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Raises", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Plank", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Lift Lunges", "Resistance Bands", "Medium", "lower_body"));
        allExercises.add(new Exercise("Skip Downs", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Raise Plank", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Ups", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Pull Ups", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Raise Raises", "None", "Low", "core"));
        allExercises.add(new Exercise("Skip Ups", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Jump Ups", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Pull Rows", "None", "High", "core"));
        allExercises.add(new Exercise("Swing Squats", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Dip Rows", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Raises", "Rope", "Low", "upper_body"));
        allExercises.add(new Exercise("Skip Jacks", "Medicine Ball", "Low", "lower_body"));
        allExercises.add(new Exercise("Dip Rows", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Lift Steps", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Downs", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Pull Plank", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Run Squats", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Downs", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Raise Rows", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Pull Plank", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Hop Lunges", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Plank", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Swing Ups", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Lift Rows", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Press Raises", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Lift Steps", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Lift Downs", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Press Plank", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Push Jacks", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Pull Downs", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Climb Downs", "Barbell", "High", "upper_body"));
        allExercises.add(new Exercise("Crunch Squats", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Press Plank", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Crunch Hops", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Dip Jacks", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Press Lunges", "Medicine Ball", "Medium", "core"));
        allExercises.add(new Exercise("Dip Downs", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Skip Ups", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Press Hops", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Twists", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Push Raises", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Crunch Plank", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Pull Jacks", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Run Squats", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Jog Rows", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Crunch Lunges", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Ups", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Press Squats", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Run Squats", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Hop Squats", "Medicine Ball", "Low", "lower_body"));
        allExercises.add(new Exercise("Press Raises", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Raises", "Dumbbells", "High", "cardio"));
        allExercises.add(new Exercise("Swing Rows", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Jump Ups", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Skip Rows", "Medicine Ball", "Medium", "core"));
        allExercises.add(new Exercise("Raise Raises", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Lift Hops", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jump Rows", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Swing Hops", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Downs", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Run Burpees", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Skip Burpees", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Press Lunges", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Press Hops", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Press Hops", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Push Twists", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Push Rows", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Push Lunges", "Rope", "Low", "upper_body"));
        allExercises.add(new Exercise("Swing Squats", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Skip Rows", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Skip Ups", "Rope", "Low", "upper_body"));
        allExercises.add(new Exercise("Raise Ups", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Raise Plank", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Downs", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Raises", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Push Steps", "Medicine Ball", "High", "core"));
        allExercises.add(new Exercise("Dip Twists", "Barbell", "Medium", "cardio"));
        allExercises.add(new Exercise("Dip Plank", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Skip Rows", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Jog Steps", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Pull Jacks", "Medicine Ball", "High", "upper_body"));
        allExercises.add(new Exercise("Push Squats", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Push Hops", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Jump Raises", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Lift Burpees", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Swing Jacks", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Run Raises", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Swing Raises", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Climb Raises", "Medicine Ball", "Low", "lower_body"));
        allExercises.add(new Exercise("Pull Rows", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Pull Raises", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Ups", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Climb Ups", "None", "Low", "core"));
        allExercises.add(new Exercise("Dip Raises", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Twists", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Lift Burpees", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Press Jacks", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jump Downs", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Burpees", "Rope", "Medium", "lower_body"));
        allExercises.add(new Exercise("Push Plank", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Crunch Burpees", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Jump Ups", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Jog Hops", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Jump Rows", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Dip Twists", "Pull-up Bar", "Low", "core"));
        allExercises.add(new Exercise("Skip Raises", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Climb Rows", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Steps", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Press Burpees", "Barbell", "Low", "lower_body"));
        allExercises.add(new Exercise("Pull Lunges", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Press Burpees", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Lift Twists", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Press Downs", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jump Steps", "None", "High", "cardio"));
        allExercises.add(new Exercise("Jog Plank", "Rope", "High", "upper_body"));
        allExercises.add(new Exercise("Run Twists", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Jog Lunges", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Hop Burpees", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Press Rows", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Twists", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Crunch Ups", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Hop Lunges", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Pull Raises", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Climb Raises", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Climb Jacks", "Rope", "Medium", "lower_body"));
        allExercises.add(new Exercise("Press Plank", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Lift Burpees", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Lift Rows", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Dip Squats", "Resistance Bands", "High", "upper_body"));
        allExercises.add(new Exercise("Jump Hops", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Run Ups", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Hop Downs", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Steps", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Hop Hops", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Raise Rows", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Dip Jacks", "None", "Medium", "core"));
        allExercises.add(new Exercise("Swing Hops", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Crunch Raises", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Push Raises", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Run Lunges", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Raises", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Swing Plank", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Jog Hops", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Crunch Rows", "None", "Medium", "core"));
        allExercises.add(new Exercise("Lift Ups", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Climb Lunges", "Medicine Ball", "Medium", "core"));
        allExercises.add(new Exercise("Jog Jacks", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Jog Steps", "None", "Low", "core"));
        allExercises.add(new Exercise("Climb Raises", "Resistance Bands", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Downs", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Climb Plank", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Skip Raises", "None", "High", "core"));
        allExercises.add(new Exercise("Dip Raises", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Lift Downs", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Hop Downs", "None", "Low", "core"));
        allExercises.add(new Exercise("Push Hops", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Hop Plank", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Raise Steps", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Run Squats", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Jog Ups", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Raise Downs", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Run Lunges", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Lift Steps", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Press Downs", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Squats", "Pull-up Bar", "Low", "upper_body"));
        allExercises.add(new Exercise("Lift Lunges", "Medicine Ball", "Low", "lower_body"));
        allExercises.add(new Exercise("Dip Burpees", "None", "Medium", "core"));
        allExercises.add(new Exercise("Pull Downs", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Ups", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Climb Jacks", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Climb Steps", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Lunges", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Skip Burpees", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Jog Plank", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Climb Ups", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Jog Hops", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Raise Downs", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Climb Hops", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jump Raises", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Hop Lunges", "Medicine Ball", "High", "upper_body"));
        allExercises.add(new Exercise("Dip Steps", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Pull Downs", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Run Plank", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Push Lunges", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Jacks", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Squats", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Pull Twists", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Raise Lunges", "Resistance Bands", "Medium", "lower_body"));
        allExercises.add(new Exercise("Press Steps", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Raise Rows", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Dip Plank", "Medicine Ball", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Plank", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Climb Burpees", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Lift Rows", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Jump Lunges", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Climb Jacks", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Hops", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Dip Hops", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Dip Plank", "Pull-up Bar", "Low", "core"));
        allExercises.add(new Exercise("Raise Burpees", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Pull Raises", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Run Twists", "Pull-up Bar", "Low", "upper_body"));
        allExercises.add(new Exercise("Jog Downs", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Jog Steps", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Hop Downs", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Swing Raises", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Push Hops", "Resistance Bands", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Jacks", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Jog Raises", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Jump Raises", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Crunch Plank", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Jog Jacks", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Crunch Ups", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Skip Raises", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Lift Squats", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Swing Lunges", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Lift Hops", "Barbell", "High", "upper_body"));
        allExercises.add(new Exercise("Push Twists", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Jog Plank", "None", "Low", "core"));
        allExercises.add(new Exercise("Jog Twists", "Pull-up Bar", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Rows", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Dip Twists", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Climb Lunges", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Run Hops", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Lift Steps", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Dip Hops", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Run Steps", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Raise Plank", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Jog Burpees", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Swing Hops", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Raise Plank", "Resistance Bands", "High", "upper_body"));
        allExercises.add(new Exercise("Push Plank", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Raise Raises", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Jog Raises", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Lunges", "None", "High", "cardio"));
        allExercises.add(new Exercise("Press Hops", "Barbell", "Low", "lower_body"));
        allExercises.add(new Exercise("Swing Twists", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Lift Ups", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Raise Steps", "Barbell", "Medium", "core"));
        allExercises.add(new Exercise("Press Ups", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Skip Squats", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Plank", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Rows", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Twists", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Crunch Burpees", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Downs", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Hop Hops", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jump Twists", "None", "High", "cardio"));
        allExercises.add(new Exercise("Push Burpees", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Lunges", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Burpees", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Twists", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Hops", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Plank", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Press Lunges", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Jump Jacks", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Press Twists", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Skip Raises", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Dip Rows", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Run Plank", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Steps", "None", "High", "core"));
        allExercises.add(new Exercise("Hop Ups", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Lift Ups", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Hop Lunges", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Raise Downs", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Push Ups", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Lift Burpees", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Jump Ups", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Ups", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Twists", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Lift Lunges", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Jump Raises", "Rope", "Medium", "cardio"));
        allExercises.add(new Exercise("Raise Squats", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Push Burpees", "Medicine Ball", "High", "core"));
        allExercises.add(new Exercise("Skip Plank", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Lift Squats", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Lift Rows", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Plank", "Medicine Ball", "Low", "lower_body"));
        allExercises.add(new Exercise("Lift Ups", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Lunges", "Rope", "High", "upper_body"));
        allExercises.add(new Exercise("Raise Steps", "Rope", "Medium", "cardio"));
        allExercises.add(new Exercise("Hop Downs", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Plank", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Crunch Hops", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Skip Raises", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Jump Rows", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Pull Plank", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Jog Downs", "None", "High", "core"));
        allExercises.add(new Exercise("Skip Squats", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Pull Plank", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Lift Hops", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Raises", "Pull-up Bar", "Medium", "core"));
        allExercises.add(new Exercise("Run Hops", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Lift Steps", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Lift Rows", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Crunch Ups", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Jog Lunges", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Run Rows", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Lift Squats", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Lift Plank", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Raises", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Crunch Steps", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Rows", "None", "High", "cardio"));
        allExercises.add(new Exercise("Push Rows", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Run Downs", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Press Raises", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Run Ups", "Barbell", "Low", "lower_body"));
        allExercises.add(new Exercise("Climb Raises", "Rope", "Low", "core"));
        allExercises.add(new Exercise("Push Hops", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Jump Rows", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Crunch Downs", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Run Raises", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Pull Burpees", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Climb Downs", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Swing Jacks", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Swing Downs", "Barbell", "Medium", "core"));
        allExercises.add(new Exercise("Run Lunges", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Dip Ups", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Raise Rows", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Hop Lunges", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Push Twists", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Crunch Downs", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Dip Rows", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Climb Rows", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Jump Raises", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Crunch Burpees", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Press Rows", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Hop Downs", "Medicine Ball", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Steps", "Pull-up Bar", "Medium", "lower_body"));
        allExercises.add(new Exercise("Climb Jacks", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Climb Downs", "Barbell", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Hops", "Pull-up Bar", "High", "cardio"));
        allExercises.add(new Exercise("Climb Lunges", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jump Jacks", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Crunch Lunges", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Jump Lunges", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Crunch Rows", "None", "High", "upper_body"));
        allExercises.add(new Exercise("Skip Squats", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Dip Hops", "Barbell", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Twists", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Climb Hops", "None", "High", "cardio"));
        allExercises.add(new Exercise("Run Lunges", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Hop Lunges", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Jog Squats", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jump Hops", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Jump Ups", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Hop Ups", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Twists", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Run Burpees", "Barbell", "Medium", "core"));
        allExercises.add(new Exercise("Pull Lunges", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Climb Plank", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Hop Plank", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Skip Plank", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Lift Lunges", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Lift Ups", "Medicine Ball", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Squats", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Burpees", "Dumbbells", "High", "cardio"));
        allExercises.add(new Exercise("Jump Ups", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Climb Plank", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Hop Raises", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Climb Squats", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Raise Burpees", "Medicine Ball", "Medium", "core"));
        allExercises.add(new Exercise("Jog Twists", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Lift Hops", "Barbell", "Low", "core"));
        allExercises.add(new Exercise("Jog Downs", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Jog Jacks", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Raise Hops", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jump Twists", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Crunch Rows", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Twists", "None", "High", "core"));
        allExercises.add(new Exercise("Hop Steps", "Dumbbells", "Low", "lower_body"));
        allExercises.add(new Exercise("Lift Steps", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Ups", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Steps", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Lift Ups", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Dip Squats", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Jog Raises", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Jacks", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Swing Ups", "Medicine Ball", "High", "upper_body"));
        allExercises.add(new Exercise("Skip Plank", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Dip Twists", "None", "High", "cardio"));
        allExercises.add(new Exercise("Swing Rows", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Pull Jacks", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Twists", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Pull Lunges", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Climb Plank", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Lunges", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Hop Lunges", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Press Jacks", "None", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Lunges", "Rope", "Medium", "lower_body"));
        allExercises.add(new Exercise("Push Plank", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Dip Lunges", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Jump Burpees", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Pull Burpees", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Burpees", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Press Hops", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Run Squats", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jump Lunges", "Pull-up Bar", "Low", "upper_body"));
        allExercises.add(new Exercise("Jump Twists", "Medicine Ball", "Medium", "lower_body"));
        allExercises.add(new Exercise("Press Lunges", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Hop Hops", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Dip Burpees", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Twists", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Skip Raises", "Barbell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Run Twists", "None", "High", "core"));
        allExercises.add(new Exercise("Jog Raises", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Dip Plank", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Skip Jacks", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Hop Downs", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Jump Lunges", "Rope", "Low", "lower_body"));
        allExercises.add(new Exercise("Jog Rows", "Rope", "High", "upper_body"));
        allExercises.add(new Exercise("Press Burpees", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Run Lunges", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Climb Plank", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Burpees", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Crunch Ups", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Push Rows", "None", "Low", "upper_body"));
        allExercises.add(new Exercise("Press Lunges", "Kettlebell", "Medium", "core"));
        allExercises.add(new Exercise("Pull Downs", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Hop Ups", "Pull-up Bar", "Medium", "lower_body"));
        allExercises.add(new Exercise("Climb Hops", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Run Rows", "Kettlebell", "Low", "cardio"));
        allExercises.add(new Exercise("Jog Burpees", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Climb Raises", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Skip Hops", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Hop Raises", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Skip Burpees", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Dip Rows", "None", "High", "cardio"));
        allExercises.add(new Exercise("Push Ups", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Skip Plank", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Jog Twists", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Push Jacks", "Pull-up Bar", "Low", "cardio"));
        allExercises.add(new Exercise("Press Steps", "Dumbbells", "High", "cardio"));
        allExercises.add(new Exercise("Skip Steps", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Run Burpees", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Raise Ups", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Pull Jacks", "Barbell", "High", "upper_body"));
        allExercises.add(new Exercise("Hop Lunges", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Jump Steps", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Push Plank", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Lunges", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Jog Ups", "Pull-up Bar", "Low", "core"));
        allExercises.add(new Exercise("Press Rows", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Press Jacks", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Press Hops", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Run Burpees", "Medicine Ball", "High", "core"));
        allExercises.add(new Exercise("Climb Burpees", "Rope", "Medium", "lower_body"));
        allExercises.add(new Exercise("Hop Raises", "Pull-up Bar", "Low", "core"));
        allExercises.add(new Exercise("Climb Lunges", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Skip Steps", "Rope", "High", "cardio"));
        allExercises.add(new Exercise("Dip Jacks", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Swing Twists", "Pull-up Bar", "High", "core"));
        allExercises.add(new Exercise("Swing Twists", "Pull-up Bar", "High", "upper_body"));
        allExercises.add(new Exercise("Press Lunges", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Jump Burpees", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Hop Hops", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Run Rows", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Climb Lunges", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Swing Lunges", "None", "Low", "cardio"));
        allExercises.add(new Exercise("Pull Raises", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Crunch Burpees", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Climb Raises", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Jump Downs", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Press Jacks", "Kettlebell", "Low", "upper_body"));
        allExercises.add(new Exercise("Push Ups", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Pull Ups", "None", "Medium", "lower_body"));
        allExercises.add(new Exercise("Jog Hops", "Rope", "High", "upper_body"));
        allExercises.add(new Exercise("Pull Plank", "Rope", "Medium", "cardio"));
        allExercises.add(new Exercise("Hop Squats", "Resistance Bands", "Medium", "cardio"));
        allExercises.add(new Exercise("Swing Rows", "Resistance Bands", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Twists", "None", "High", "core"));
        allExercises.add(new Exercise("Dip Hops", "Pull-up Bar", "Medium", "cardio"));
        allExercises.add(new Exercise("Run Burpees", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Swing Downs", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Jog Lunges", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Run Hops", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Push Lunges", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Lift Squats", "Medicine Ball", "Low", "cardio"));
        allExercises.add(new Exercise("Push Steps", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Ups", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Raise Plank", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Jump Twists", "Dumbbells", "Medium", "core"));
        allExercises.add(new Exercise("Hop Raises", "Pull-up Bar", "Medium", "upper_body"));
        allExercises.add(new Exercise("Run Squats", "Rope", "Low", "cardio"));
        allExercises.add(new Exercise("Jog Rows", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Pull Burpees", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Push Hops", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Dip Twists", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Raise Hops", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Raise Twists", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Lunges", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Twists", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Skip Raises", "None", "Medium", "upper_body"));
        allExercises.add(new Exercise("Crunch Plank", "Dumbbells", "Medium", "lower_body"));
        allExercises.add(new Exercise("Swing Ups", "Rope", "High", "upper_body"));
        allExercises.add(new Exercise("Climb Raises", "None", "Low", "core"));
        allExercises.add(new Exercise("Pull Steps", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Push Burpees", "Pull-up Bar", "High", "lower_body"));
        allExercises.add(new Exercise("Hop Steps", "Medicine Ball", "Medium", "cardio"));
        allExercises.add(new Exercise("Pull Rows", "Dumbbells", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Jacks", "Medicine Ball", "Low", "upper_body"));
        allExercises.add(new Exercise("Raise Steps", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Dip Steps", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Skip Ups", "Rope", "Medium", "core"));
        allExercises.add(new Exercise("Hop Ups", "Medicine Ball", "Medium", "upper_body"));
        allExercises.add(new Exercise("Run Hops", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Swing Raises", "None", "Medium", "core"));
        allExercises.add(new Exercise("Pull Lunges", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Jump Steps", "Barbell", "High", "upper_body"));
        allExercises.add(new Exercise("Run Hops", "Rope", "High", "lower_body"));
        allExercises.add(new Exercise("Pull Raises", "Barbell", "High", "cardio"));
        allExercises.add(new Exercise("Push Raises", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Skip Burpees", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Hop Ups", "Dumbbells", "Medium", "cardio"));
        allExercises.add(new Exercise("Push Burpees", "Medicine Ball", "Low", "core"));
        allExercises.add(new Exercise("Jump Hops", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Push Jacks", "None", "Low", "core"));
        allExercises.add(new Exercise("Push Plank", "Kettlebell", "High", "upper_body"));
        allExercises.add(new Exercise("Climb Lunges", "None", "Medium", "cardio"));
        allExercises.add(new Exercise("Pull Raises", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jog Lunges", "None", "High", "cardio"));
        allExercises.add(new Exercise("Lift Rows", "Resistance Bands", "Medium", "cardio"));
        allExercises.add(new Exercise("Pull Rows", "Resistance Bands", "Medium", "lower_body"));
        allExercises.add(new Exercise("Run Rows", "Dumbbells", "Low", "upper_body"));
        allExercises.add(new Exercise("Run Hops", "Medicine Ball", "High", "lower_body"));
        allExercises.add(new Exercise("Lift Squats", "Kettlebell", "Low", "core"));
        allExercises.add(new Exercise("Lift Steps", "Barbell", "High", "core"));
        allExercises.add(new Exercise("Crunch Downs", "Medicine Ball", "High", "upper_body"));
        allExercises.add(new Exercise("Swing Steps", "Barbell", "High", "upper_body"));
        allExercises.add(new Exercise("Jog Twists", "Dumbbells", "High", "core"));
        allExercises.add(new Exercise("Raise Steps", "Kettlebell", "Low", "lower_body"));
        allExercises.add(new Exercise("Dip Rows", "None", "Low", "lower_body"));
        allExercises.add(new Exercise("Raise Squats", "Barbell", "High", "upper_body"));
        allExercises.add(new Exercise("Lift Lunges", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Climb Downs", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Dip Ups", "Resistance Bands", "Low", "upper_body"));
        allExercises.add(new Exercise("Hop Burpees", "Medicine Ball", "High", "upper_body"));
        allExercises.add(new Exercise("Hop Ups", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Climb Raises", "Kettlebell", "High", "cardio"));
        allExercises.add(new Exercise("Run Steps", "Dumbbells", "Low", "cardio"));
        allExercises.add(new Exercise("Jog Twists", "Kettlebell", "High", "core"));
        allExercises.add(new Exercise("Crunch Jacks", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Run Hops", "Dumbbells", "Medium", "upper_body"));
        allExercises.add(new Exercise("Dip Downs", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Run Raises", "Medicine Ball", "High", "cardio"));
        allExercises.add(new Exercise("Lift Downs", "Rope", "Low", "upper_body"));
        allExercises.add(new Exercise("Dip Twists", "Kettlebell", "High", "lower_body"));
        allExercises.add(new Exercise("Crunch Raises", "Barbell", "Medium", "core"));
        allExercises.add(new Exercise("Swing Rows", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Crunch Twists", "Resistance Bands", "High", "core"));
        allExercises.add(new Exercise("Press Burpees", "Resistance Bands", "Low", "core"));
        allExercises.add(new Exercise("Jump Downs", "Kettlebell", "Medium", "lower_body"));
        allExercises.add(new Exercise("Press Plank", "Barbell", "Low", "cardio"));
        allExercises.add(new Exercise("Dip Jacks", "Rope", "Medium", "upper_body"));
        allExercises.add(new Exercise("Push Ups", "Kettlebell", "Medium", "cardio"));
        allExercises.add(new Exercise("Raise Lunges", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Jump Plank", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Hop Downs", "Barbell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Run Lunges", "Pull-up Bar", "Low", "lower_body"));
        allExercises.add(new Exercise("Run Twists", "Pull-up Bar", "Low", "core"));
        allExercises.add(new Exercise("Jog Burpees", "Resistance Bands", "High", "lower_body"));
        allExercises.add(new Exercise("Run Rows", "None", "Low", "core"));
        allExercises.add(new Exercise("Dip Lunges", "Kettlebell", "Medium", "upper_body"));
        allExercises.add(new Exercise("Hop Rows", "Pull-up Bar", "Low", "upper_body"));
        allExercises.add(new Exercise("Press Burpees", "Barbell", "Low", "upper_body"));
        allExercises.add(new Exercise("Crunch Raises", "Dumbbells", "Low", "core"));
        allExercises.add(new Exercise("Jump Jacks", "None", "High", "lower_body"));
        allExercises.add(new Exercise("Swing Jacks", "Resistance Bands", "Low", "cardio"));
        allExercises.add(new Exercise("Raise Raises", "Resistance Bands", "Medium", "core"));
        allExercises.add(new Exercise("Raise Lunges", "Dumbbells", "High", "upper_body"));
        allExercises.add(new Exercise("Jog Jacks", "Barbell", "High", "lower_body"));
        allExercises.add(new Exercise("Lift Rows", "Resistance Bands", "Medium", "lower_body"));
        allExercises.add(new Exercise("Push Squats", "Rope", "High", "upper_body"));
        allExercises.add(new Exercise("Jog Hops", "Resistance Bands", "Low", "lower_body"));
        allExercises.add(new Exercise("Lift Jacks", "Resistance Bands", "High", "cardio"));
        allExercises.add(new Exercise("Hop Burpees", "Resistance Bands", "High", "upper_body"));
    }

}
