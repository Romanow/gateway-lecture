package ru.romanow.gateway.models

import java.math.BigDecimal

data class LegoSet(
    var number: String? = null,
    var name: String? = null,
    var age: Int? = null,
    var partsCount: Int? = null,
    var suggestedPrice: BigDecimal? = null,
    var seriesName: String? = null
)
