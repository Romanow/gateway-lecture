package ru.romanow.gateway.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.romanow.gateway.domain.LegoSetEntity;
import ru.romanow.gateway.mappers.ModelMapper;
import ru.romanow.gateway.models.LegoSet;

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
