package ru.romanow.gateway.web;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.romanow.gateway.services.CrudService;

@RequiredArgsConstructor
public abstract class BaseController<ID, RESP> {

    private final CrudService<ID, RESP> service;

    @GetMapping("/{id}")
    @Operation(summary = "Получение элемента справочника по идентификатору")
    public RESP findById(@PathVariable ID id) {
        return service.findById(id);
    }

    @GetMapping
    @Operation(summary = "Получение элемента справочника по идентификатору")
    public List<RESP> findAll(@ParameterObject @PageableDefault Pageable pageable) {
        return service.findAll(pageable);
    }

}
