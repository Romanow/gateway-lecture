package ru.romanow.dictionary.web

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.romanow.dictionary.models.LegoSet
import ru.romanow.dictionary.services.LegoSetService

@Tag(name = "Lego Sets")
@RestController
@RequestMapping("/api/v1/lego-sets")
class LegoSetController(legoSetService: LegoSetService) : BaseController<String, LegoSet>(legoSetService)
