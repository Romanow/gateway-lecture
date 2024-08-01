package ru.romanow.dictionary.domain

import jakarta.persistence.*
import ru.romanow.dictionary.domain.enums.SeriesComplexity
import ru.romanow.dictionary.domain.enums.SeriesType

@Entity
@Table(name = "series")
data class SeriesEntity(

    @Id
    @Column(name = "name", length = 80, nullable = false, updatable = false, unique = true)
    var name: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: SeriesType? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "complexity", nullable = false)
    var complexity: SeriesComplexity? = null,

    @Column(name = "age")
    var age: Int? = null,

    @OneToMany(mappedBy = "series")
    var sets: Set<LegoSetEntity>? = null
) : Identity<String?> {
    override fun getId() = name
}
