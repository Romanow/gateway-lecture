package ru.romanow.gateway.mappers;

import org.mapstruct.Mapper;
import ru.romanow.gateway.domain.SeriesEntity;
import ru.romanow.gateway.mappers.config.MappingConfig;
import ru.romanow.gateway.models.Series;

@Mapper(config = MappingConfig.class)
public interface SeriesMapper
        extends ModelMapper<String, SeriesEntity, Series> {

}
