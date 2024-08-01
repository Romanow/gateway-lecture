package ru.romanow.dictionary.services

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import ru.romanow.dictionary.domain.LegoSetEntity
import ru.romanow.dictionary.mappers.ModelMapper
import ru.romanow.dictionary.models.LegoSet

@Service
class LegoSetService(
    repository: JpaRepository<LegoSetEntity, String>,
    mapper: ModelMapper<String, LegoSetEntity, LegoSet>
) : BaseCrudService<String, LegoSetEntity, LegoSet>(repository, mapper)
