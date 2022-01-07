package org.meveo.apiv2.securityDeposit.impl;

import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.securityDeposit.ImmutableSecurityDepositInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.Subscription;
import org.meveo.model.cpq.Product;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;

public class SecurityDepositMapper extends ResourceMapper<SecurityDepositInput, SecurityDeposit> {

    @Override
    protected SecurityDepositInput toResource(SecurityDeposit entity) {
        return ImmutableSecurityDepositInput.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .template(createResource(entity.getTemplate()))
                .currency(createResource(entity.getCurrency()))
                .customerAccount(createResource(entity.getCustomerAccount()))
                .validityDate(entity.getValidityDate())
                .validityPeriod(entity.getValidityPeriod())
                .validityPeriodUnit(entity.getValidityPeriodUnit())
                .amount(entity.getAmount())
                .currentBalance(entity.getCurrentBalance())
                .status(entity.getStatus())
                .productInstance(entity.getProductInstance())
                .subscription(createResource(entity.getSubscription()))
                .product(createResource(entity.getProduct()))
                .externalReference(entity.getExternalReference())
                .build();
    }

    @Override
    protected SecurityDeposit toEntity(SecurityDepositInput resource) {
        return toEntity(new SecurityDeposit(), resource);
    }

    private SecurityDeposit toEntity(SecurityDeposit securityDeposit, SecurityDepositInput resource) {
        securityDeposit.setId(resource.getId());
        securityDeposit.setCode(resource.getCode());
        securityDeposit.setDescription(resource.getDescription());
        if (resource.getTemplate() != null) {
            SecurityDepositTemplate template = new SecurityDepositTemplate();
            template.setId(resource.getTemplate().getId());
            template.setCode(resource.getTemplate().getCode());
            securityDeposit.setTemplate(template);
        }
        if (resource.getCurrency() != null) {
            Currency currency = new Currency();
            currency.setId(resource.getCurrency().getId());
            securityDeposit.setCurrency(currency);
        }
        if (resource.getCustomerAccount() != null) {
            CustomerAccount customerAccount = new CustomerAccount();
            customerAccount.setId(resource.getCustomerAccount().getId());
            customerAccount.setCode(resource.getCustomerAccount().getCode());
            securityDeposit.setCustomerAccount(customerAccount);
        }
        securityDeposit.setValidityDate(resource.getValidityDate());
        securityDeposit.setValidityPeriod(resource.getValidityPeriod());
        securityDeposit.setValidityPeriodUnit(resource.getValidityPeriodUnit());
        securityDeposit.setAmount(resource.getAmount());
        securityDeposit.setCurrentBalance(resource.getCurrentBalance());
        securityDeposit.setStatus(resource.getStatus());
        securityDeposit.setProductInstance(resource.getProductInstance());
        if (resource.getSubscription() != null) {
            Subscription subscription = new Subscription();
            subscription.setId(resource.getSubscription().getId());
            subscription.setCode(resource.getSubscription().getCode());
            securityDeposit.setSubscription(subscription);
        }
        if (resource.getProduct() != null) {
            Product product = new Product();
            product.setId(resource.getProduct().getId());
            product.setCode(resource.getProduct().getCode());
            securityDeposit.setProduct(product);
        }
        securityDeposit.setExternalReference(resource.getExternalReference());
        return securityDeposit;
    }

    private Resource createResource(BaseEntity baseEntity) {
        return baseEntity != null ? ImmutableResource.builder().id(baseEntity.getId()).build() : null;
    }
    
    private Resource createResource(BusinessEntity businessEntity) {
        return businessEntity != null ? ImmutableResource.builder().id(businessEntity.getId()).code(businessEntity.getCode()).build() : null;
    }
}
