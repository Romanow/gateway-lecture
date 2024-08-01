package ru.romanow.dictionary.mappers

import ru.romanow.dictionary.domain.Identity

interface ModelMapper<ID, T : Identity<ID?>, RESP> {
    fun toModel(entity: T): RESP
}
