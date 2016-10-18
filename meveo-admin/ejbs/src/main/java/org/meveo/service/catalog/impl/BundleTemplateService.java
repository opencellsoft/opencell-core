package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class BundleTemplateService extends BusinessService<BundleTemplate> {

	@Inject
	private CustomFieldInstanceService customFieldInstanceService;

	public long bundleTemplateActiveCount(boolean status) {
		long result = 0;

		Query query;

		if (status) {
			query = getEntityManager().createNamedQuery("BundleTemplate.countActive");
		} else {
			query = getEntityManager().createNamedQuery("BundleTemplate.countDisabled");
		}

		result = (long) query.getSingleResult();
		return result;
	}

	public long productTemplateAlmostExpiredCount() {
		long result = 0;
		String sqlQuery = "SELECT COUNT(*) FROM " + BundleTemplate.class.getName() + " p WHERE DATE_PART('day',p.validTo - '"
				+ DateUtils.formatDateWithPattern(new Date(), "yyyy-MM-dd hh:mm:ss") + "') <= 7";
		Query query = getEntityManager().createQuery(sqlQuery);
		result = (long) query.getSingleResult();
		return result;
	}

	public synchronized void duplicate(BundleTemplate entity, User currentUser) throws BusinessException {
		entity = refreshOrRetrieve(entity);

		// Lazy load related values first
		entity.getWalletTemplates().size();
		entity.getBusinessAccountModels().size();
		entity.getAttachments().size();
		entity.getChannels().size();
		entity.getOfferTemplateCategories().size();
		entity.getBundleProducts().size();

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

		Set<BundleProductTemplate> bundleProductTemplates = entity.getBundleProducts();
		entity.setBundleProducts(new HashSet<BundleProductTemplate>());

		entity.setCode(code);

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

		if (bundleProductTemplates != null) {
			for (BundleProductTemplate bpt : bundleProductTemplates) {
				entity.getBundleProducts().add(bpt);
			}
			for (BundleProductTemplate bpt : bundleProductTemplates) {
				bpt.setId(null);
				bpt.setBundleTemplate(entity);
				entity.addBundleProductTemplate(bpt);
			}
		}


        create(entity, getCurrentUser());
		customFieldInstanceService.duplicateCfValues(sourceAppliesToEntity, entity, getCurrentUser());
	}
}
