package ru.romanow.dictionary.web

import io.swagger.v3.oas.annotations.Operation
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.romanow.dictionary.services.CrudService

abstract class BaseController<ID, RESP>(
    private val service: CrudService<ID, RESP>
) {
    @GetMapping("/{id}")
    @Operation(summary = "Получение элемента справочника по идентификатору")
    open fun findById(@PathVariable id: ID) = service.findById(id)

    @GetMapping
    @Operation(summary = "Получение элемента справочника по идентификатору")
    open fun findAll(@ParameterObject @PageableDefault pageable: Pageable) = service.findAll(pageable)
}
