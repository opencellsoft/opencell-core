package org.meveo.apiv2.dunning.service;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.dunning.DunningPolicyLevel;
import org.meveo.model.dunning.InvoiceDunningStatuses;
import org.meveo.service.dunning.DunningLevelService;
import org.meveo.service.dunning.DunningPolicyLevelService;
import org.meveo.service.payments.impl.InvoiceDunningStatusesService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Optional;

public class DunningPolicyLevelApiService implements ApiService<DunningPolicyLevel> {

    @Inject
    private DunningPolicyLevelService dunningPolicyLevelService;

    @Inject
    private DunningLevelService dunningLevelService;

    @Inject
    private InvoiceDunningStatusesService invoiceDunningStatusesService;

    @Override
    public List<DunningPolicyLevel> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<DunningPolicyLevel> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public DunningPolicyLevel create(DunningPolicyLevel dunningPolicyLevel) {
        try {
            dunningPolicyLevelService.create(dunningPolicyLevel);
            return dunningPolicyLevel;
        } catch (Exception exception) {
            throw new BadRequestException("Dunning policy level creation failed");
        }
    }

    @Override
    public Optional<DunningPolicyLevel> update(Long id, DunningPolicyLevel baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<DunningPolicyLevel> patch(Long id, DunningPolicyLevel baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<DunningPolicyLevel> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<DunningPolicyLevel> findByCode(String code) {
        return Optional.empty();
    }
}