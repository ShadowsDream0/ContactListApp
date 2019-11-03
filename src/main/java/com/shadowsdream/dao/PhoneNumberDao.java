package com.shadowsdream.dao;

import com.shadowsdream.model.PhoneNumber;

import java.util.List;

public interface PhoneNumberDao {
    Long save(PhoneNumber phoneNumber);

    List<PhoneNumber> findAll();

    PhoneNumber findById(Long id);

    void update(PhoneNumber phoneNumber);

    void remove(Long id);
}
