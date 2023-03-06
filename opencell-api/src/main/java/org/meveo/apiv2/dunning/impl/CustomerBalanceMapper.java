package org.meveo.apiv2.dunning.impl;

import org.meveo.apiv2.dunning.CustomerBalance;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.payments.OCCTemplate;

import java.util.ArrayList;
import java.util.List;

public class CustomerBalanceMapper extends ResourceMapper<CustomerBalance, org.meveo.model.dunning.CustomerBalance> {

    @Override
    protected CustomerBalance toResource(org.meveo.model.dunning.CustomerBalance entity) {
        return null;
    }

    @Override
    protected org.meveo.model.dunning.CustomerBalance toEntity(CustomerBalance resource) {
        org.meveo.model.dunning.CustomerBalance customerBalance = new org.meveo.model.dunning.CustomerBalance();
        customerBalance.setCode(resource.getCode());
        customerBalance.setDescription(resource.getLabel());
        customerBalance.setDefaultBalance(resource.getDefaultBalance());
        if(resource.getOccTemplates() != null) {
            List<OCCTemplate> templates = new ArrayList<>();
            resource.getOccTemplates().forEach(input -> {
                OCCTemplate template = new OCCTemplate();
                template.setId(input.getId());
                templates.add(template);
            });
            customerBalance.setOccTemplates(templates);
        }
        return customerBalance;
    }
}