package org.meveo.apiv2.custom.query.service;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.custom.query.CustomQuery;
import org.meveo.service.custom.CustomQueryService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class CustomQueryApiService implements ApiService<CustomQuery> {

    @Inject
    private CustomQueryService customQueryService;

    private List<String> fetchFields = asList("fields");

    @Override
    public List<CustomQuery> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<CustomQuery> findById(Long id) {
        return ofNullable(customQueryService.findById(id, fetchFields));
    }

    @Override
    public CustomQuery create(CustomQuery baseEntity) {
        return null;
    }

    @Override
    public Optional<CustomQuery> update(Long id, CustomQuery baseEntity) {
        return empty();
    }

    @Override
    public Optional<CustomQuery> patch(Long id, CustomQuery baseEntity) {
        return empty();
    }

    @Override
    public Optional<CustomQuery> delete(Long id) {
        CustomQuery customQuery = customQueryService.findById(id);
        if (customQuery != null) {
            customQueryService.remove(customQuery);
            return of(customQuery);
        }
        return empty();
    }

    @Override
    public Optional<CustomQuery> findByCode(String code) {
        return of(customQueryService.findByCode(code, fetchFields));
    }
}