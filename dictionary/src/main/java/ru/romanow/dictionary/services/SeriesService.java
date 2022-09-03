package ru.romanow.dictionary.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.romanow.dictionary.domain.SeriesEntity;
import ru.romanow.dictionary.mappers.ModelMapper;
import ru.romanow.dictionary.models.Series;

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
