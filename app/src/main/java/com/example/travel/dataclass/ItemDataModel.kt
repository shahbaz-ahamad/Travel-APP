package com.example.travel.dataclass

data class ItemDataModel(
    var bed: Int = 0,
    var description: String = "",
    var guide: Boolean = false,
    var location: String = "",
    var pic: String = "",
    var price: Int = 0,
    var score: Long = 0L, // Update the data type to Long
    var title: String = "",
    var wifi: Boolean = false,
    var liked:Boolean=false
): java.io.Serializable
