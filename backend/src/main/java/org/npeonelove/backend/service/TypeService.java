package org.npeonelove.backend.service;

import lombok.RequiredArgsConstructor;
import org.npeonelove.backend.exception.type.TypeNotFoundException;
import org.npeonelove.backend.repository.TypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TypeService {

    private final TypeRepository typeRepository;

    public String getTypeTitle(UUID typeId) {
        return typeRepository.findTypeByTypeId(typeId).orElseThrow(
                () -> new TypeNotFoundException("Type with id " + typeId + " not exists")
        ).getTitle();
    }

}
