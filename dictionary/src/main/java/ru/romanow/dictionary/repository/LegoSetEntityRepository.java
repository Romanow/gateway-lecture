package ru.romanow.dictionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanow.dictionary.domain.SeriesEntity;

public interface LegoSetEntityRepository
        extends JpaRepository<SeriesEntity, String> {

}
