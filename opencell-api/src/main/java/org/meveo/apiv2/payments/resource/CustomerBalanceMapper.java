package org.meveo.apiv2.payments.resource;

import org.meveo.apiv2.payments.CustomerBalance;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.payments.OCCTemplate;

import java.util.ArrayList;
import java.util.List;

public class CustomerBalanceMapper extends ResourceMapper<CustomerBalance, org.meveo.model.payments.CustomerBalance> {

    @Override
    protected CustomerBalance toResource(org.meveo.model.payments.CustomerBalance entity) {
        return null;
    }

    @Override
    public org.meveo.model.payments.CustomerBalance toEntity(CustomerBalance resource) {
        org.meveo.model.payments.CustomerBalance customerBalance = new org.meveo.model.payments.CustomerBalance();
        customerBalance.setCode(resource.getCode());
        if(resource.getLabel() != null) {
            customerBalance.setDescription(resource.getLabel());
        }
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