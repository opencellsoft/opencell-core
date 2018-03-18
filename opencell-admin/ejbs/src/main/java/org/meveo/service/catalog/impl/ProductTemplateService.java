package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.BusinessAccountModel;

@Stateless
public class ProductTemplateService extends GenericProductOfferingService<ProductTemplate> {

    public long countProductTemplateActive(boolean status) {
        long result = 0;

        Query query;

        if (status) {
            query = getEntityManager().createNamedQuery("ProductTemplate.countActive");
        } else {
            query = getEntityManager().createNamedQuery("ProductTemplate.countDisabled");
        }

        result = (long) query.getSingleResult();
        return result;
    }

    public long countProductTemplateExpiring() {
        int beforeExpiration = Integer.parseInt(paramBeanFactory.getInstance().getProperty("offer.expiration.before", "30"));

        Long result = 0L;
        Query query = getEntityManager().createNamedQuery("ProductTemplate.countExpiring");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -beforeExpiration);
        query.setParameter("nowMinusXDay", c.getTime());

        try {
            result = (long) query.getSingleResult();
        } catch (NoResultException e) {

        }
        return result;
    }

    /**
     * Create a shallow duplicate of an Product template (main Product template information and custom fields). A new Product template will have a code with suffix "- Copy"
     * 
     * @param product Product template to duplicate
     * @throws BusinessException business exception.
     */
    public synchronized void duplicate(ProductTemplate product) throws BusinessException {
        duplicate(product, true);
    }

    /**
     * Create a new version of an Product template. It is a shallow copy of an Product template (main Product template information and custom fields) with identical code and
     * validity start date matching latest version's validity end date or current date.
     * 
     * @param product Product template to create new version for
     * @return A not-persisted copy of Product template
     * @throws BusinessException business exception.
     */
    public synchronized ProductTemplate instantiateNewVersion(ProductTemplate product) throws BusinessException {

        // Find the latest version of an offer for duplication and to calculate a validity start date for a new offer
        ProductTemplate latestVersion = findTheLatestVersion(product.getCode());
        String code = latestVersion.getCode();
        Date startDate = null;
        Date endDate = null;
        if (latestVersion.getValidity() != null) {
            startDate = latestVersion.getValidity().getFrom();
            endDate = latestVersion.getValidity().getTo();
        }

        product = duplicate(latestVersion, false);

        product.setCode(code);

        Date from = endDate != null ? endDate : new Date();
        if (startDate != null && from.before(startDate)) {
            from = startDate;
        }
        product.setValidity(new DatePeriod(from, null));

        return product;
    }

    /**
     * Create a duplicate of a given Product template. It is a shallow copy of an Product template (main Product template information and custom fields)
     * 
     * @param product Product template to duplicate
     * @param persist Shall new entity be persisted
     * @return A copy of Product template
     * @throws BusinessException business exception.
     */
    public synchronized ProductTemplate duplicate(ProductTemplate product, boolean persist) throws BusinessException {

        product = refreshOrRetrieve(product);

        // Lazy load related values first
        product.getWalletTemplates().size();
        product.getBusinessAccountModels().size();
        product.getAttachments().size();
        product.getChannels().size();
        product.getOfferTemplateCategories().size();
        product.getProductChargeTemplates().size();
        product.getSellers().size();

        String code = findDuplicateCode(product);

        // Detach and clear ids of entity and related entities
        detach(product);
        product.setId(null);
        product.clearUuid();

        List<BusinessAccountModel> businessAccountModels = product.getBusinessAccountModels();
        product.setBusinessAccountModels(new ArrayList<BusinessAccountModel>());

        List<DigitalResource> attachments = product.getAttachments();
        product.setAttachments(new ArrayList<DigitalResource>());

        List<Channel> channels = product.getChannels();
        product.setChannels(new ArrayList<Channel>());

        List<OfferTemplateCategory> offerTemplateCategories = product.getOfferTemplateCategories();
        product.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());

        List<WalletTemplate> walletTemplates = product.getWalletTemplates();
        product.setWalletTemplates(new ArrayList<WalletTemplate>());

        List<ProductChargeTemplate> chargeTemplates = product.getProductChargeTemplates();
        product.setProductChargeTemplates(new ArrayList<>());

        List<Seller> sellers = product.getSellers();
        product.setSellers(new ArrayList<>());

        product.setCode(code);

        if (businessAccountModels != null) {
            for (BusinessAccountModel bam : businessAccountModels) {
                product.getBusinessAccountModels().add(bam);
            }
        }

        if (attachments != null) {
            for (DigitalResource attachment : attachments) {
                product.addAttachment(attachment);
            }
        }

        if (channels != null) {
            for (Channel channel : channels) {
                product.getChannels().add(channel);
            }
        }

        if (offerTemplateCategories != null) {
            for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
                product.getOfferTemplateCategories().add(offerTemplateCategory);
            }
        }

        if (walletTemplates != null) {
            for (WalletTemplate wt : walletTemplates) {
                product.getWalletTemplates().add(wt);
            }
        }

        if (chargeTemplates != null) {
            for (ProductChargeTemplate chargeTemplate : chargeTemplates) {
                product.getProductChargeTemplates().add(chargeTemplate);
            }
        }

        if (sellers != null) {
            for (Seller seller : sellers) {
                product.getSellers().add(seller);
            }
        }

        if (persist) {
            create(product);
        }

        return product;
    }

    @SuppressWarnings("unchecked")
    public List<ProductTemplate> listByLifeCycleStatus(LifeCycleStatusEnum... statuses) {
        QueryBuilder qb = new QueryBuilder(ProductTemplate.class, "p");
        qb.startOrClause();
        for (LifeCycleStatusEnum status : statuses) {
            qb.addCriterionEnum("lifeCycleStatus", status);
        }
        qb.endOrClause();
        return qb.getQuery(getEntityManager()).getResultList();
    }
}