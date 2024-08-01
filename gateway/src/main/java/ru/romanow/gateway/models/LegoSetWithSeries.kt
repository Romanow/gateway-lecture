package ru.romanow.gateway.models

import java.math.BigDecimal

data class LegoSetWithSeries(
    var number: String? = null,
    var name: String? = null,
    var age: Int? = null,
    var partsCount: Int? = null,
    var suggestedPrice: BigDecimal? = null,
    var series: Series? = null
)
