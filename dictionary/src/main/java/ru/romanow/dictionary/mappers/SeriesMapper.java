package ru.romanow.dictionary.mappers;

import org.mapstruct.Mapper;
import ru.romanow.dictionary.domain.SeriesEntity;
import ru.romanow.dictionary.mappers.config.MappingConfig;
import ru.romanow.dictionary.models.Series;

@Mapper(config = MappingConfig.class)
public interface SeriesMapper
        extends ModelMapper<String, SeriesEntity, Series> {

}
