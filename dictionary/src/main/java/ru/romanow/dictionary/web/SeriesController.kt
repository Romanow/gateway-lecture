package ru.romanow.dictionary.web

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.romanow.dictionary.models.Series
import ru.romanow.dictionary.services.SeriesService

@Tag(name = "Lego Series")
@RestController
@RequestMapping("/api/v1/series")
class SeriesController(seriesService: SeriesService) : BaseController<String, Series>(seriesService) {
    override fun findAll(pageable: Pageable): List<Series> {
        Thread.sleep(5000)
        return super.findAll(pageable)
    }
}
