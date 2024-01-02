package org.meveo.service.payments.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.payments.CustomerBalance;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

/**
 * Service implementation to manage CustomerBalance entity.
 * It extends {@link PersistenceService} class
 * 
 * @author zelmeliani
 * @version 15.0.0
 *
 */
@Stateless
public class CustomerBalanceService extends BusinessService<CustomerBalance> {

    @Inject
    private OCCTemplateService occTemplateService;

	/**
	 * Get the default CustomerBalance
	 * @return
	 */
	public CustomerBalance getDefaultOne() throws NoResultException, NonUniqueResultException {
		try {
			return getEntityManager().createNamedQuery("CustomerBalance.findDefaultOne", CustomerBalance.class)
			.setParameter("default", true)
			.getSingleResult();
		} catch (NoResultException e) {
	        return null;
	    } catch (NonUniqueResultException e) {
	        throw new BusinessException("there are multiple customer balance as default");
	    }
	}

    /**
     * Find default customer balance
     *
     * @return optional customer balance, empty if no result found
     */
    public Optional<CustomerBalance> findDefaultCustomerBalance() {
        try {
            return of((CustomerBalance) getEntityManager()
                    .createNamedQuery("CustomerBalance.findDefaultCustomerBalance")
                    .getSingleResult());
        } catch (NoResultException exception) {
            return empty();
        }
    }

    @Override
    public void create(CustomerBalance entity) {
        final int maxLimitCustomerBalance = paramBeanFactory
                .getInstance()
                .getPropertyAsInteger("max.customer.balance", 6);
        if(entity.getDescription() == null) {
            throw new BusinessException("Customer balance description is mandatory");
        }
        if (entity.isDefaultBalance() && findDefaultCustomerBalance().isPresent()) {
            throw new BusinessException("One default balance already exists");
        }
        if (count() >= maxLimitCustomerBalance) {
            throw new BusinessException("Customer balance number reached limit, max balance allowed : "
                    + maxLimitCustomerBalance);
        }
        if (entity.getOccTemplates() != null) {
            entity.setOccTemplates(validateAndAttachTemplates(entity.getOccTemplates()));
        }
        super.create(entity);
    }

    private List<OCCTemplate> validateAndAttachTemplates(List<OCCTemplate> templates) {
        int credit = 0;
        int debit = 0;
        List<OCCTemplate> attachedTemplates = new ArrayList<>();
        long id;
        for (OCCTemplate template : templates) {
            id = template.getId();
            template = occTemplateService.findById(id);
            if(template == null) {
                throw new NotFoundException("Occ template with id " + id + " does not exists");
            }
            if(OperationCategoryEnum.DEBIT == template.getOccCategory()) {
                debit++;
            }
            if(OperationCategoryEnum.CREDIT == template.getOccCategory()) {
                credit++;
            }
            attachedTemplates.add(template);
        }
        if(credit == 0) {
            throw new ValidationException("Credit line should not be empty");
        }
        if(debit == 0) {
            throw new ValidationException("Debit line should not be empty");
        }
        return attachedTemplates;
    }

    @Override
    public CustomerBalance update(CustomerBalance entity) {
        CustomerBalance toUpdate = ofNullable(findById(entity.getId(), asList("occTemplates")))
                .orElseThrow(() -> new NotFoundException("Customer balance with id "
                        + entity.getId() + " and code " + entity.getCode() + " does not exists"));
        ofNullable(entity.getDescription()).ifPresent(toUpdate::setDescription);
        if(entity.getOccTemplates() != null && !entity.getOccTemplates().isEmpty()) {
            List<OCCTemplate> templates = validateAndAttachTemplates(entity.getOccTemplates());
            toUpdate.getOccTemplates().clear();
            toUpdate.setOccTemplates(templates);
        }
        return super.update(toUpdate);
    }
	
}
