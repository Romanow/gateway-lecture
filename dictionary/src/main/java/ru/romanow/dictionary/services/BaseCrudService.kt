package ru.romanow.dictionary.services

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.romanow.dictionary.domain.Identity

abstract class BaseCrudService<ID, T : Identity<ID?>, RESP>(
    private val repository: JpaRepository<T, ID>,
) : CrudService<ID, RESP> {

    @Transactional(readOnly = true)
    override fun findById(id: ID): RESP {
        return repository
            .findById(id!!)
            .map { toModel(it) }
            .orElseThrow { EntityNotFoundException("Entity not found for id '$id'") }
    }

    @Transactional
    override fun findAll(pageable: Pageable): List<RESP> {
        return repository.findAll().map { toModel(it) }
    }

    protected abstract fun toModel(entity: T): RESP
}
