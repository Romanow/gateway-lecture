package ru.romanow.dictionary.services

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import ru.romanow.dictionary.domain.SeriesEntity
import ru.romanow.dictionary.mappers.ModelMapper
import ru.romanow.dictionary.models.Series

@Service
class SeriesService(
    repository: JpaRepository<SeriesEntity, String>,
    mapper: ModelMapper<String, SeriesEntity, Series>
) : BaseCrudService<String, SeriesEntity, Series>(repository, mapper)
