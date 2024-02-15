package com.example.write_code_restaurant_menu

data class CardItem(
    val textIdDish: Int,
    val textQuantity: Int,
    val textPrice: Int,
    val checkboxCard: Int,
    val buttonAddComment: Int,
    val buttonIncrease: Int,
    val buttonDecrease: Int
)
data class ShoppingBagItem (
    val id: String,
    val price: Double,
    val comment: String,
)
