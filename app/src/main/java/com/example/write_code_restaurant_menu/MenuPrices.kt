package com.example.write_code_restaurant_menu

enum class MenuPrices(val id: String, val price: Double) {
    DISH_1("1001", 10.00),
    DISH_3("1003", 14.00),
    DISH_4("1004", 22.00),
    DISH_6("1006", 21.00),
    DISH_7("1007", 42.00),
    DISH_8("1008", 48.00),
    DISH_9("1009", 34.00),
    DISH_10("1010", 22.00),
    DISH_11("1011", 12.00),
    DISH_12("1012", 8.00),
    DISH_13("1013", 6.00),
    DISH_14("1014", 10.00),
    DISH_15("1015", 2.00),
    DISH_16("1016", 4.00);
    companion object {
        fun getPriceById(id: String): Double {
            return values().firstOrNull { it.id == id }?.price ?: 0.0
        }

        fun getByIndex(index: Int): MenuPrices? {
            return if (index in 0 until values().size) {
                values()[index]
            } else {
                null
            }
        }
    }
}