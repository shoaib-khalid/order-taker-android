package com.symplified.ordertaker

import com.symplified.ordertaker.models.CartItem
import com.symplified.ordertaker.models.Category
import com.symplified.ordertaker.models.Item
import com.symplified.ordertaker.models.Zone

class SampleData {
    companion object {
        fun zones(): List<Zone> {
            return listOf(
                Zone("Indoor"),
                Zone("Outdoor"),
                Zone("Upstairs"),
                Zone("Rooftop")
            )
        }

        fun categories(): List<Category> {
            return listOf(
                Category(0, "Best Seller"),
                Category(0, "Appetizer"),
                Category(0, "Main Course"),
                Category(0, "Soup"),
                Category(0, "Drinks"),
                Category(0, "BBQ"),
                Category(0, "Dessert"),
                Category(0, "Salad"),
                Category(0, "Pasta"),
                Category(0, "Noodles")
            )
        }

        fun items(): List<Item> {
            return listOf(
                Item("Hawaiian Pizza", 17.00),
                Item("Seafood Onion Pizza", 27.00),
                Item("Beef Lasagna", 23.00),
                Item("Aglio Oglio with Prawns", 29.00),
                Item("Beef Bolognese Pasta", 29.00),
                Item("Chicken Hotdog", 29.00),
                Item("Latte", 9.00),
                Item("Fruit Juice", 9.00),
                Item("Black Forest Cake", 9.00),
                Item("Earl Grey Tea", 9.00),
                Item("Green Tea", 9.00),
                Item("Fruits Platter", 9.00),
                Item("Jasmine Tea", 9.00),
                Item("Lotus Tea", 9.00),
                Item("Pu Er Tea", 9.00)
            )
        }

//        fun cartItems(): List<CartItem> {
//            val items = items()
//            return listOf(
//                CartItem(items[0], 1),
//                CartItem(items[1], 2),
//                CartItem(items[2], 3),
//                CartItem(items[3], 4),
//                CartItem(items[4], 5),
//                CartItem(items[5], 6),
//                CartItem(items[6], 7),
//                CartItem(items[7], 8),
//                CartItem(items[8], 9)
//            )
//        }
    }
}