package org.meveo.apiv2.services;

import org.meveo.model.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface ApiService<T extends BaseEntity> {
    List<T> list(Long offset, Long limit, String sort, String orderBy, String filter);
    Long getCount(String filter);
    Optional<T> findById(Long id);
    T create(T baseEntity);
    Optional<T> update(Long id, T baseEntity);
    Optional<T> patch(Long id, T baseEntity);
    Optional<T> delete(Long id);
}
