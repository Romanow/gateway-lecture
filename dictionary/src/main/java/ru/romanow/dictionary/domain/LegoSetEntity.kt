package ru.romanow.dictionary.domain

import jakarta.persistence.*
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import java.math.BigDecimal

@Entity
@Table(name = "lego_set")
data class LegoSetEntity(

    @Id
    @Column(name = "number", length = 20, nullable = false, updatable = false, unique = true)
    override var id: String? = null,

    @Column(name = "name", length = 120, nullable = false)
    var name: String? = null,

    @Column(name = "age")
    var age: Int? = null,

    @Column(name = "parts_count", nullable = false)
    var partsCount: Int? = null,

    @Column(name = "suggested_price", precision = 8, scale = 2, nullable = false)
    var suggestedPrice: BigDecimal? = null,

    @Column(name = "series_id", updatable = false, insertable = false)
    var seriesName: String? = null,

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false, foreignKey = ForeignKey(name = "fk_lego_set_series_id"))
    var series: SeriesEntity? = null
) : Identity<String?>
