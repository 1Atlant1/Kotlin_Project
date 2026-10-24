package com.example.kotlin_project.data.db

object DatabaseSeeder {
    val initialDishes = listOf(
        // Soups
        Dish(name = "Lentil soup", nameRu = "Чечевичный суп", category = "Soups", carbsPer100g = 12.0, proteinPer100g = 4.0, fatPer100g = 3.0, caloriesPer100g = 91.0),
        Dish(name = "Tomato soup", nameRu = "Томатный суп", category = "Soups", carbsPer100g = 6.0, proteinPer100g = 1.0, fatPer100g = 2.0, caloriesPer100g = 50.0),
        Dish(name = "Chicken soup", nameRu = "Куриный суп", category = "Soups", carbsPer100g = 3.0, proteinPer100g = 3.0, fatPer100g = 1.0, caloriesPer100g = 35.0),
        Dish(name = "Mushroom soup", nameRu = "Грибной суп", category = "Soups", carbsPer100g = 4.0, proteinPer100g = 2.0, fatPer100g = 2.0, caloriesPer100g = 45.0),
        Dish(name = "Pumpkin soup", nameRu = "Тыквенный суп", category = "Soups", carbsPer100g = 9.0, proteinPer100g = 1.0, fatPer100g = 2.0, caloriesPer100g = 60.0),

        // Hot dishes
        Dish(name = "Chicken cutlet", nameRu = "Куриная котлета", category = "Hot dishes", carbsPer100g = 10.0, proteinPer100g = 15.0, fatPer100g = 8.0, caloriesPer100g = 180.0),
        Dish(name = "Roasted chicken", nameRu = "Жареная курица", category = "Hot dishes", carbsPer100g = 0.0, proteinPer100g = 25.0, fatPer100g = 12.0, caloriesPer100g = 220.0),
        Dish(name = "Baked fish", nameRu = "Рыба запечённая", category = "Hot dishes", carbsPer100g = 0.0, proteinPer100g = 20.0, fatPer100g = 8.0, caloriesPer100g = 160.0),

        // Sweets
        Dish(name = "Chocolate", nameRu = "Шоколад", category = "Sweets", carbsPer100g = 55.0, proteinPer100g = 5.0, fatPer100g = 30.0, caloriesPer100g = 520.0),
        Dish(name = "Cookies", nameRu = "Печенье", category = "Sweets", carbsPer100g = 70.0, proteinPer100g = 6.0, fatPer100g = 15.0, caloriesPer100g = 450.0),
        Dish(name = "Ice cream", nameRu = "Мороженое", category = "Sweets", carbsPer100g = 20.0, proteinPer100g = 3.0, fatPer100g = 10.0, caloriesPer100g = 200.0),

        // Drinks
        Dish(name = "Cola", nameRu = "Кола", category = "Drinks", carbsPer100g = 10.6, proteinPer100g = 0.0, fatPer100g = 0.0, caloriesPer100g = 42.0, isDrink = true),
        Dish(name = "Orange juice", nameRu = "Апельсиновый сок", category = "Drinks", carbsPer100g = 10.0, proteinPer100g = 0.5, fatPer100g = 0.0, caloriesPer100g = 45.0, isDrink = true),
        Dish(name = "Milk", nameRu = "Молоко", category = "Drinks", carbsPer100g = 4.7, proteinPer100g = 3.2, fatPer100g = 2.5, caloriesPer100g = 52.0, isDrink = true),

        // Fruits
        Dish(name = "Apple", nameRu = "Яблоко", category = "Fruits", carbsPer100g = 10.0, proteinPer100g = 0.3, fatPer100g = 0.2, caloriesPer100g = 52.0),
        Dish(name = "Banana", nameRu = "Банан", category = "Fruits", carbsPer100g = 22.0, proteinPer100g = 1.1, fatPer100g = 0.3, caloriesPer100g = 95.0),
        Dish(name = "Watermelon", nameRu = "Арбуз", category = "Fruits", carbsPer100g = 5.7, proteinPer100g = 0.6, fatPer100g = 0.2, caloriesPer100g = 30.0),

        // Vegetables
        Dish(name = "Boiled potato", nameRu = "Картофель варёный", category = "Vegetables", carbsPer100g = 17.0, proteinPer100g = 2.0, fatPer100g = 0.1, caloriesPer100g = 87.0),
        Dish(name = "Carrot", nameRu = "Морковь", category = "Vegetables", carbsPer100g = 7.0, proteinPer100g = 0.9, fatPer100g = 0.2, caloriesPer100g = 35.0),
        Dish(name = "Tomato", nameRu = "Помидор", category = "Vegetables", carbsPer100g = 3.0, proteinPer100g = 1.0, fatPer100g = 0.2, caloriesPer100g = 20.0),

        // Dairy
        Dish(name = "Cottage cheese 5%", nameRu = "Творог 5%", category = "Dairy", carbsPer100g = 2.0, proteinPer100g = 12.0, fatPer100g = 5.0, caloriesPer100g = 100.0),
        Dish(name = "Hard cheese", nameRu = "Сыр твёрдый", category = "Dairy", carbsPer100g = 1.0, proteinPer100g = 25.0, fatPer100g = 30.0, caloriesPer100g = 380.0),
        Dish(name = "Yogurt", nameRu = "Йогурт", category = "Dairy", carbsPer100g = 8.0, proteinPer100g = 4.0, fatPer100g = 3.0, caloriesPer100g = 75.0),

        // Bread & grains
        Dish(name = "White bread", nameRu = "Хлеб белый", category = "Bread & grains", carbsPer100g = 50.0, proteinPer100g = 8.0, fatPer100g = 3.0, caloriesPer100g = 260.0),
        Dish(name = "Boiled rice", nameRu = "Рис варёный", category = "Bread & grains", carbsPer100g = 28.0, proteinPer100g = 2.5, fatPer100g = 0.3, caloriesPer100g = 130.0),
        Dish(name = "Boiled buckwheat", nameRu = "Гречка варёная", category = "Bread & grains", carbsPer100g = 20.0, proteinPer100g = 4.0, fatPer100g = 1.0, caloriesPer100g = 110.0)
    )

    val initialInsulins = listOf(
        InsulinProfile(name = "NovoRapid", type = "bolus"),
        InsulinProfile(name = "Humalog", type = "bolus"),
        InsulinProfile(name = "Apidra", type = "bolus")
    )
}