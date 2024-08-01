package ru.romanow.dictionary.services

import org.springframework.data.domain.Pageable

interface CrudService<ID, RESP> {
    fun findById(id: ID): RESP
    fun findAll(pageable: Pageable): List<RESP>
}
