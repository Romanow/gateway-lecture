package ru.romanow.gateway.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.romanow.gateway.domain.SeriesEntity;
import ru.romanow.gateway.mappers.ModelMapper;
import ru.romanow.gateway.models.Series;

@Service
public class SeriesService
        extends BaseCrudService<String, SeriesEntity, Series> {

    public SeriesService(
            JpaRepository<SeriesEntity, String> repository,
            ModelMapper<String, SeriesEntity, Series> mapper
    ) {
        super(repository, mapper);
    }

}
