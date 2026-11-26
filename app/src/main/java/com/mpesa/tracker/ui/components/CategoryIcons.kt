package com.mpesa.tracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryIconOption(
    val key: String,
    val label: String,
    val icon: ImageVector
)

val categoryIconOptions = listOf(
    CategoryIconOption("shopping_cart", "Groceries", Icons.Filled.ShoppingCart),
    CategoryIconOption("bolt", "Utilities", Icons.Filled.Bolt),
    CategoryIconOption("directions_car", "Transport", Icons.Filled.DirectionsCar),
    CategoryIconOption("phone_android", "Airtime", Icons.Filled.PhoneAndroid),
    CategoryIconOption("home", "Housing", Icons.Filled.Home),
    CategoryIconOption("people", "People", Icons.Filled.People),
    CategoryIconOption("money_off", "Fees", Icons.AutoMirrored.Filled.ReceiptLong),
    CategoryIconOption("subscriptions", "Subscriptions", Icons.Filled.EventRepeat),
    CategoryIconOption("store", "Business", Icons.Filled.Storefront),
    CategoryIconOption("restaurant", "Dining", Icons.Filled.Restaurant),
    CategoryIconOption("local_hospital", "Health", Icons.Filled.LocalHospital),
    CategoryIconOption("school", "Education", Icons.Filled.School),
    CategoryIconOption("help_outline", "Other", Icons.Filled.Category)
)

fun categoryIconFor(iconName: String?): ImageVector =
    categoryIconOptions.firstOrNull { it.key == iconName }?.icon ?: Icons.Filled.Category

fun parseCategoryColor(hex: String?, fallback: Color): Color = try {
    if (hex.isNullOrBlank()) fallback else Color(android.graphics.Color.parseColor(hex))
} catch (_: Exception) {
    fallback
}
