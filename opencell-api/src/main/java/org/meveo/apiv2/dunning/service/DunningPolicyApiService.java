package org.meveo.apiv2.dunning.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningPolicy;
import org.meveo.service.dunning.DunningPolicyService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

public class DunningPolicyApiService implements ApiService<DunningPolicy> {

    @Inject
    private DunningPolicyService dunningPolicyService;

    @Override
    public List<DunningPolicy> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<DunningPolicy> findById(Long id) {
        return empty();
    }

    @Override
    public DunningPolicy create(DunningPolicy dunningPolicy) {
        dunningPolicyService.create(dunningPolicy);
        return findByName(dunningPolicy.getPolicyName()).get();
    }

    @Override
    public Optional<DunningPolicy> update(Long id, DunningPolicy dunningPolicy) {
        return empty();
    }

    @Override
    public Optional<DunningPolicy> patch(Long id, DunningPolicy dunningPolicy) {
        return empty();
    }

    @Override
    public Optional<DunningPolicy> delete(Long id) {
        return empty();
    }

    @Override
    public Optional<DunningPolicy> findByCode(String code) {
        return empty();
    }

    public Optional<DunningPolicy> findByName(String policyName) {
        try {
            return of(dunningPolicyService.findByName(policyName));
        } catch (Exception exception) {
            throw new BadRequestException(exception.getMessage());
        }
    }

    public Optional<DunningPolicy> updateTotalLevels(DunningPolicy dunningPolicy) {
        dunningPolicy = dunningPolicyService.refreshOrRetrieve(dunningPolicy);
        return of(dunningPolicyService.update(dunningPolicy));
    }
}