package com.symplified.ordertaker

import com.symplified.ordertaker.models.categories.Category
import com.symplified.ordertaker.models.MenuItem
import com.symplified.ordertaker.models.zones.Zone
import com.symplified.ordertaker.models.zones.ZoneWithTables

class SampleData {
    companion object {

        fun zones(): List<ZoneWithTables> {
            return listOf(
                ZoneWithTables(Zone(1, 1, "Nigga1"), listOf()),
                ZoneWithTables(Zone(2, 2, "Nigga2"), listOf())
            )
        }

        fun categories(): List<Category> {
            return listOf(
//                Category(name = "Best Seller"),
//                Category(name = "Appetizer"),
//                Category(name = "Main Course"),
//                Category(name = "Soup"),
//                Category(name = "Drinks"),
//                Category(name = "BBQ"),
//                Category(name = "Dessert"),
//                Category(name = "Salad"),
//                Category(name = "Pasta"),
//                Category(name = "Noodles")
            )
        }

        fun items(): List<MenuItem> {
            return listOf(
                MenuItem(name = "Hawaiian Pizza", price = 17.00),
                MenuItem(name = "Seafood Onion Pizza", price = 27.00),
                MenuItem(name = "Beef Lasagna", price = 23.00),
                MenuItem(name = "Aglio Oglio with Prawns", price = 29.00),
                MenuItem(name = "Beef Bolognese Pasta", price = 29.00),
                MenuItem(name = "Chicken Hotdog", price = 29.00),
                MenuItem(name = "Latte", price = 9.00),
                MenuItem(name = "Fruit Juice", price = 9.00),
                MenuItem(name = "Black Forest Cake", price = 9.00),
                MenuItem(name = "Earl Grey Tea", price = 9.00),
                MenuItem(name = "Green Tea", price = 9.00),
                MenuItem(name = "Fruits Platter", price = 9.00),
                MenuItem(name = "Jasmine Tea", price = 9.00),
                MenuItem(name = "Lotus Tea", price = 9.00),
                MenuItem(name = "Pu Er Tea", price = 9.00)
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