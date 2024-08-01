package ru.romanow.dictionary.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.romanow.dictionary.domain.SeriesEntity

interface LegoSetEntityRepository : JpaRepository<SeriesEntity, String>
