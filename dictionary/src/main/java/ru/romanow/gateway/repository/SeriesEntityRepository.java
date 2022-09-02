package ru.romanow.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanow.gateway.domain.LegoSetEntity;

public interface SeriesEntityRepository
        extends JpaRepository<LegoSetEntity, String> {

}
