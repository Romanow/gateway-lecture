package ru.romanow.gateway.mappers;

import ru.romanow.gateway.domain.Identity;

public interface ModelMapper<ID, T extends Identity<ID>, RESP> {

    RESP toModel(T entity);

}
