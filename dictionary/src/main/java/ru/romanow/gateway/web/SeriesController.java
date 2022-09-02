package ru.romanow.gateway.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.romanow.gateway.models.Series;
import ru.romanow.gateway.services.SeriesService;

@Tag(name = "Lego Series")
@RestController
@RequestMapping("/api/v1/series")
public class SeriesController
        extends BaseController<String, Series> {

    public SeriesController(SeriesService seriesService) {
        super(seriesService);
    }

}
