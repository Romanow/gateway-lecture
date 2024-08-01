package ru.romanow.dictionary.services

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import ru.romanow.dictionary.domain.LegoSetEntity
import ru.romanow.dictionary.models.LegoSet

@Service
class LegoSetService(repository: JpaRepository<LegoSetEntity, String>) :
    BaseCrudService<String, LegoSetEntity, LegoSet>(repository) {

    override fun toModel(entity: LegoSetEntity) =
        LegoSet(
            number = entity.number,
            name = entity.name,
            age = entity.age,
            partsCount = entity.partsCount,
            suggestedPrice = entity.suggestedPrice,
            seriesName = entity.seriesName
        )
}
