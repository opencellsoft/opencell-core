package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.MultilanguageEntityService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

@Stateless
public class ProductTemplateService extends MultilanguageEntityService<ProductTemplate> {

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	public long countProductTemplateActive(boolean status, Provider provider) {
		long result = 0;

		Query query;

		if (status) {
			query = getEntityManager().createNamedQuery("ProductTemplate.countActive").setParameter("provider", provider);
		} else {
			query = getEntityManager().createNamedQuery("ProductTemplate.countDisabled").setParameter("provider", provider);
		}

		result = (long) query.getSingleResult();
		return result;
	}

	public long countProductTemplateExpiring(Provider provider) {	
        Long result = 0L;
        Query query = getEntityManager().createNamedQuery("ProductTemplate.countExpiring").setParameter("provider", provider);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        query.setParameter("nowMinus1Day", c.getTime());

        try {
            result = (long) query.getSingleResult();
        } catch (NoResultException e) {

        }
        return result;		
	}

	public synchronized void duplicate(ProductTemplate entity, User currentUser) throws BusinessException {
		entity = refreshOrRetrieve(entity);

		// Lazy load related values first
		entity.getWalletTemplates().size();
		entity.getBusinessAccountModels().size();
		entity.getAttachments().size();
		entity.getChannels().size();
		entity.getOfferTemplateCategories().size();

		String code = findDuplicateCode(entity, currentUser);

		// Detach and clear ids of entity and related entities
		detach(entity);
		entity.setId(null);
		String sourceAppliesToEntity = entity.clearUuid();

		List<BusinessAccountModel> businessAccountModels = entity.getBusinessAccountModels();
		entity.setBusinessAccountModels(new ArrayList<BusinessAccountModel>());

		List<DigitalResource> attachments = entity.getAttachments();
		entity.setAttachments(new ArrayList<DigitalResource>());

		List<Channel> channels = entity.getChannels();
		entity.setChannels(new ArrayList<Channel>());

		List<OfferTemplateCategory> offerTemplateCategories = entity.getOfferTemplateCategories();
		entity.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());

		List<WalletTemplate> walletTemplates = entity.getWalletTemplates();
		entity.setWalletTemplates(new ArrayList<WalletTemplate>());

		entity.setCode(code);
		create(entity, getCurrentUser());

		if (businessAccountModels != null) {
			for (BusinessAccountModel bam : businessAccountModels) {
				entity.getBusinessAccountModels().add(bam);
			}
		}

		if (attachments != null) {
			for (DigitalResource attachment : attachments) {
				entity.addAttachment(attachment);
			}
		}

		if (channels != null) {
			for (Channel channel : channels) {
				entity.getChannels().add(channel);
			}
		}

		if (offerTemplateCategories != null) {
			for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
				entity.getOfferTemplateCategories().add(offerTemplateCategory);
			}
		}

		if (walletTemplates != null) {
			for (WalletTemplate wt : walletTemplates) {
				entity.getWalletTemplates().add(wt);
			}
		}

		update(entity, currentUser);
		customFieldInstanceService.duplicateCfValues(sourceAppliesToEntity, entity, getCurrentUser());
	}

}
