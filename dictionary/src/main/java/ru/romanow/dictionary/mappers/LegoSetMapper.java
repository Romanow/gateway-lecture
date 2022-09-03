package ru.romanow.dictionary.mappers;

import org.mapstruct.Mapper;
import ru.romanow.dictionary.domain.LegoSetEntity;
import ru.romanow.dictionary.mappers.config.MappingConfig;
import ru.romanow.dictionary.models.LegoSet;

@Mapper(config = MappingConfig.class)
public interface LegoSetMapper
        extends ModelMapper<String, LegoSetEntity, LegoSet> {

}
