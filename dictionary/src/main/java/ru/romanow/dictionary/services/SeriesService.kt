package ru.romanow.dictionary.services

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import ru.romanow.dictionary.domain.SeriesEntity
import ru.romanow.dictionary.models.Series

@Service
class SeriesService(repository: JpaRepository<SeriesEntity, String>) :
    BaseCrudService<String, SeriesEntity, Series>(repository) {

    override fun toModel(entity: SeriesEntity) =
        Series(
            name = entity.name,
            type = entity.type?.name,
            complexity = entity.complexity?.name,
            age = entity.age
        )
}
