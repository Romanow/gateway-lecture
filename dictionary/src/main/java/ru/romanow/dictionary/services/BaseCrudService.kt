package ru.romanow.dictionary.services;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.romanow.dictionary.domain.Identity;
import ru.romanow.dictionary.mappers.ModelMapper;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class BaseCrudService<ID, T extends Identity<ID>, RESP>
        implements CrudService<ID, RESP> {

    private final JpaRepository<T, ID> repository;
    private final ModelMapper<ID, T, RESP> mapper;

    @Nullable
    @Override
    @Transactional(readOnly = true)
    public RESP findById(@NotNull ID id) {
        return repository
                .findById(id)
                .map(mapper::toModel)
                .orElse(null);
    }

    @NotNull
    @Override
    @Transactional
    public List<RESP> findAll(@NotNull Pageable pageable) {
        return repository
                .findAll()
                .stream()
                .map(mapper::toModel)
                .collect(toList());
    }

}
