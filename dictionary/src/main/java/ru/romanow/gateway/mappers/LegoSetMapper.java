package ru.romanow.gateway.mappers;

import org.mapstruct.Mapper;
import ru.romanow.gateway.domain.LegoSetEntity;
import ru.romanow.gateway.mappers.config.MappingConfig;
import ru.romanow.gateway.models.LegoSet;

@Mapper(config = MappingConfig.class)
public interface LegoSetMapper
        extends ModelMapper<String, LegoSetEntity, LegoSet> {

}
