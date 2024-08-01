package ru.romanow.dictionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanow.dictionary.domain.LegoSetEntity;

public interface SeriesEntityRepository
        extends JpaRepository<LegoSetEntity, String> {

}
