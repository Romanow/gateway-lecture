package ru.romanow.dictionary.mappers;

import ru.romanow.dictionary.domain.Identity;

public interface ModelMapper<ID, T extends Identity<ID>, RESP> {

    RESP toModel(T entity);

}
