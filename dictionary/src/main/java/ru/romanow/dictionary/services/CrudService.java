package ru.romanow.dictionary.services;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;

public interface CrudService<ID, RESP> {

    @Nullable
    RESP findById(@NotNull ID id);

    @NotNull
    List<RESP> findAll(@NotNull Pageable pageable);

}
