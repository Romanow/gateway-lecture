package ru.romanow.dictionary.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.romanow.dictionary.domain.LegoSetEntity;
import ru.romanow.dictionary.mappers.ModelMapper;
import ru.romanow.dictionary.models.LegoSet;

@Service
public class LegoSetService
        extends BaseCrudService<String, LegoSetEntity, LegoSet> {

    public LegoSetService(
            JpaRepository<LegoSetEntity, String> repository,
            ModelMapper<String, LegoSetEntity, LegoSet> mapper
    ) {
        super(repository, mapper);
    }

}
