package ru.romanow.dictionary.services

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.romanow.dictionary.domain.Identity
import ru.romanow.dictionary.mappers.ModelMapper

open class BaseCrudService<ID, T : Identity<ID?>, RESP>(
    private val repository: JpaRepository<T, ID>,
    private val mapper: ModelMapper<ID, T, RESP>
) : CrudService<ID, RESP> {

    @Transactional(readOnly = true)
    override fun findById(id: ID): RESP {
        return repository
            .findById(id)
            .map { mapper.toModel(it) }
            .orElseThrow { EntityNotFoundException("Entity not found for id '$id'") }
    }

    @Transactional
    override fun findAll(pageable: Pageable): List<RESP> {
        return repository.findAll().map { mapper.toModel(it) }
    }
}
