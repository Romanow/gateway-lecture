package ru.romanow.dictionary.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.romanow.dictionary.models.Series;
import ru.romanow.dictionary.services.SeriesService;

@Tag(name = "Lego Series")
@RestController
@RequestMapping("/api/v1/series")
public class SeriesController
        extends BaseController<String, Series> {

    public SeriesController(SeriesService seriesService) {
        super(seriesService);
    }

    @Override
    @SneakyThrows
    public List<Series> findAll(Pageable pageable) {
        Thread.sleep(5000);
        return super.findAll(pageable);
    }

}
