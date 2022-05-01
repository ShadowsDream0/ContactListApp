package com.shadowsdream.facade;

import java.util.UUID;

public interface Facade<T, U> {
    T save(U object);
    T findById(UUID id);
    T update(T object);
    T deleteById(Long id);
}
