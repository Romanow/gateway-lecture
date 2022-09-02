package ru.romanow.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.romanow.gateway.domain.SeriesEntity;

public interface LegoSetEntityRepository
        extends JpaRepository<SeriesEntity, String> {

}
