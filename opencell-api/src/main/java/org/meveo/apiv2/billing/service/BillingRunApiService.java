package org.meveo.apiv2.billing.service;

import static java.util.Optional.empty;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.billing.BillingRun;
import org.meveo.service.billing.impl.BillingRunService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class BillingRunApiService implements ApiService<BillingRun> {

    @Inject
    private BillingRunService billingRunService;

    @Override
    public BillingRun create(BillingRun billingRun) {
        billingRunService.create(billingRun);
        return billingRun;
    }

    @Override
    public List<BillingRun> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<BillingRun> findById(Long id) {
        return empty();
    }

    @Override
    public Optional<BillingRun> update(Long id, BillingRun baseEntity) {
        return empty();
    }

    @Override
    public Optional<BillingRun> patch(Long id, BillingRun baseEntity) {
        return empty();
    }

    @Override
    public Optional<BillingRun> delete(Long id) {
        return empty();
    }
}
