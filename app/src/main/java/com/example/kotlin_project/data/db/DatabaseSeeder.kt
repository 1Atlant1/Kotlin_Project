package com.example.kotlin_project.data.db

object DatabaseSeeder {
    val initialDishes = listOf(
        Dish(
            name = "Чечевичный суп",
            category = "Soups",
            carbsPer100g = 12.0,
            proteinPer100g = 4.0,
            fatPer100g = 3.0,
            caloriesPer100g = 91.0
        ),
        Dish(
            name = "Томатный суп",
            category = "Soups",
            carbsPer100g = 6.0,
            proteinPer100g = 1.0,
            fatPer100g = 2.0,
            caloriesPer100g = 50.0
        ),
        Dish(
            name = "Куриный суп",
            category = "Soups",
            carbsPer100g = 3.0,
            proteinPer100g = 3.0,
            fatPer100g = 1.0,
            caloriesPer100g = 35.0
        ),
        Dish(
            name = "Грибной суп",
            category = "Soups",
            carbsPer100g = 4.0,
            proteinPer100g = 2.0,
            fatPer100g = 2.0,
            caloriesPer100g = 45.0
        ),
        Dish(
            name = "Тыквенный суп",
            category = "Soups",
            carbsPer100g = 9.0,
            proteinPer100g = 1.0,
            fatPer100g = 2.0,
            caloriesPer100g = 60.0
        ),
        Dish(
            name = "Куриная котлета",
            category = "Hot dishes",
            carbsPer100g = 10.0,
            proteinPer100g = 15.0,
            fatPer100g = 8.0,
            caloriesPer100g = 180.0
        ),
        Dish(
            name = "Жареная курица",
            category = "Hot dishes",
            carbsPer100g = 0.0,
            proteinPer100g = 25.0,
            fatPer100g = 12.0,
            caloriesPer100g = 220.0
        ),
        Dish(
            name = "Рыба запечённая",
            category = "Hot dishes",
            carbsPer100g = 0.0,
            proteinPer100g = 20.0,
            fatPer100g = 8.0,
            caloriesPer100g = 160.0
        ),
        Dish(
            name = "Шоколад",
            category = "Sweets",
            carbsPer100g = 55.0,
            proteinPer100g = 5.0,
            fatPer100g = 30.0,
            caloriesPer100g = 520.0
        ),
        Dish(
            name = "Печенье",
            category = "Sweets",
            carbsPer100g = 70.0,
            proteinPer100g = 6.0,
            fatPer100g = 15.0,
            caloriesPer100g = 450.0
        ),
        Dish(
            name = "Мороженое",
            category = "Sweets",
            carbsPer100g = 20.0,
            proteinPer100g = 3.0,
            fatPer100g = 10.0,
            caloriesPer100g = 200.0
        ),
        Dish(
            name = "Кола",
            category = "Drinks",
            carbsPer100g = 10.6,
            proteinPer100g = 0.0,
            fatPer100g = 0.0,
            caloriesPer100g = 42.0,
            isDrink = true
        ),
        Dish(
            name = "Сок апельсиновый",
            category = "Drinks",
            carbsPer100g = 10.0,
            proteinPer100g = 0.5,
            fatPer100g = 0.0,
            caloriesPer100g = 45.0,
            isDrink = true
        ),
        Dish(
            name = "Молоко",
            category = "Drinks",
            carbsPer100g = 4.7,
            proteinPer100g = 3.2,
            fatPer100g = 2.5,
            caloriesPer100g = 52.0,
            isDrink = true
        ),
        Dish(
            name = "Яблоко",
            category = "Fruits",
            carbsPer100g = 10.0,
            proteinPer100g = 0.3,
            fatPer100g = 0.2,
            caloriesPer100g = 52.0
        ),
        Dish(
            name = "Банан",
            category = "Fruits",
            carbsPer100g = 22.0,
            proteinPer100g = 1.1,
            fatPer100g = 0.3,
            caloriesPer100g = 95.0
        ),
        Dish(
            name = "Арбуз",
            category = "Fruits",
            carbsPer100g = 5.7,
            proteinPer100g = 0.6,
            fatPer100g = 0.2,
            caloriesPer100g = 30.0
        ),
        Dish(
            name = "Картофель варёный",
            category = "Vegetables",
            carbsPer100g = 17.0,
            proteinPer100g = 2.0,
            fatPer100g = 0.1,
            caloriesPer100g = 87.0
        ),
        Dish(
            name = "Морковь",
            category = "Vegetables",
            carbsPer100g = 7.0,
            proteinPer100g = 0.9,
            fatPer100g = 0.2,
            caloriesPer100g = 35.0
        ),
        Dish(
            name = "Помидор",
            category = "Vegetables",
            carbsPer100g = 3.0,
            proteinPer100g = 1.0,
            fatPer100g = 0.2,
            caloriesPer100g = 20.0
        ),
        Dish(
            name = "Творог 5%",
            category = "Dairy",
            carbsPer100g = 2.0,
            proteinPer100g = 12.0,
            fatPer100g = 5.0,
            caloriesPer100g = 100.0
        ),
        Dish(
            name = "Сыр твёрдый",
            category = "Dairy",
            carbsPer100g = 1.0,
            proteinPer100g = 25.0,
            fatPer100g = 30.0,
            caloriesPer100g = 380.0
        ),
        Dish(
            name = "Йогурт",
            category = "Dairy",
            carbsPer100g = 8.0,
            proteinPer100g = 4.0,
            fatPer100g = 3.0,
            caloriesPer100g = 75.0
        ),
        Dish(
            name = "Хлеб белый",
            category = "Bread & grains",
            carbsPer100g = 50.0,
            proteinPer100g = 8.0,
            fatPer100g = 3.0,
            caloriesPer100g = 260.0
        ),
        Dish(
            name = "Рис варёный",
            category = "Bread & grains",
            carbsPer100g = 28.0,
            proteinPer100g = 2.5,
            fatPer100g = 0.3,
            caloriesPer100g = 130.0
        ),
        Dish(
            name = "Гречка варёная",
            category = "Bread & grains",
            carbsPer100g = 20.0,
            proteinPer100g = 4.0,
            fatPer100g = 1.0,
            caloriesPer100g = 110.0
        )
    )

    val initialInsulins = listOf(
        InsulinProfile(name = "NovoRapid", type = "bolus"),
        InsulinProfile(name = "Humalog", type = "bolus"),
        InsulinProfile(name = "Apidra", type = "bolus")
    )
}