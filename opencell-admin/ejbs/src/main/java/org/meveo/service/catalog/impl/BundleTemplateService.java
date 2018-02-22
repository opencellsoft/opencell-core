package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.DatePeriod;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class BundleTemplateService extends GenericProductOfferingService<BundleTemplate> {

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
        String sqlQuery = "SELECT COUNT(*) FROM " + BundleTemplate.class.getName() + " p WHERE DATE_PART('day',p.validity.to - '"
                + DateUtils.formatDateWithPattern(new Date(), "yyyy-MM-dd hh:mm:ss") + "') <= 7";
        Query query = getEntityManager().createQuery(sqlQuery);
        result = (long) query.getSingleResult();
        return result;
    }

    /**
     * Create a shallow duplicate of an Bundle template (main Bundle template information and custom fields). A new Bundle template will have a code with suffix "- Copy"
     * 
     * @param bundle Bundle template to duplicate
     * @throws BusinessException business exception.
     */
    public synchronized void duplicate(BundleTemplate bundle) throws BusinessException {
        duplicate(bundle, true);

    }

    /**
     * Create a new version of an Bundle template. It is a shallow copy of an Bundle template (main Bundle template information and custom fields) with identical code and validity
     * start date matching latest version's validity end date or current date.
     * 
     * @param bundle Bundle template to create new version for
     * @return A not-persisted copy of Bundle template
     * @throws BusinessException business exception.
     */
    public synchronized BundleTemplate instantiateNewVersion(BundleTemplate bundle) throws BusinessException {

        // Find the latest version of an offer for duplication and to calculate a validity start date for a new offer
        BundleTemplate latestVersion = findTheLatestVersion(bundle.getCode());
        String code = latestVersion.getCode();
        Date startDate = null;
        Date endDate = null;
        if (latestVersion.getValidity() != null) {
            startDate = latestVersion.getValidity().getFrom();
            endDate = latestVersion.getValidity().getTo();
        }
        bundle = duplicate(latestVersion, false);

        bundle.setCode(code);

        Date from = endDate != null ? endDate : new Date();
        if (startDate != null && from.before(startDate)) {
            from = startDate;
        }
        bundle.setValidity(new DatePeriod(from, null));

        return bundle;
    }

    /**
     * Create a duplicate of a given Bundle template. It is a shallow copy of an Bundle template (main Bundle template information and custom fields)
     * 
     * @param bundle Bundle template to duplicate
     * @param persist Shall new entity be persisted
     * @return A copy of Bundle template
     * @throws BusinessException business exception.
     */
    public synchronized BundleTemplate duplicate(BundleTemplate bundle, boolean persist) throws BusinessException {

        bundle = refreshOrRetrieve(bundle);

        // Lazy load related values first
        bundle.getWalletTemplates().size();
        bundle.getBusinessAccountModels().size();
        bundle.getAttachments().size();
        bundle.getChannels().size();
        bundle.getOfferTemplateCategories().size();
        bundle.getBundleProducts().size();
        bundle.getProductChargeTemplates().size();
        bundle.getSellers().size();

        String code = findDuplicateCode(bundle);

        // Detach and clear ids of entity and related entities
        detach(bundle);
        bundle.setId(null);
        bundle.clearUuid();

        List<BusinessAccountModel> businessAccountModels = bundle.getBusinessAccountModels();
        bundle.setBusinessAccountModels(new ArrayList<BusinessAccountModel>());

        List<DigitalResource> attachments = bundle.getAttachments();
        bundle.setAttachments(new ArrayList<DigitalResource>());

        List<Channel> channels = bundle.getChannels();
        bundle.setChannels(new ArrayList<Channel>());

        List<OfferTemplateCategory> offerTemplateCategories = bundle.getOfferTemplateCategories();
        bundle.setOfferTemplateCategories(new ArrayList<OfferTemplateCategory>());

        List<WalletTemplate> walletTemplates = bundle.getWalletTemplates();
        bundle.setWalletTemplates(new ArrayList<WalletTemplate>());

        List<BundleProductTemplate> bundleProductTemplates = bundle.getBundleProducts();
        bundle.setBundleProducts(new ArrayList<BundleProductTemplate>());

        List<ProductChargeTemplate> chargeTemplates = bundle.getProductChargeTemplates();
        bundle.setProductChargeTemplates(new ArrayList<>());
        
        List<Seller> sellers = bundle.getSellers();
        bundle.setSellers(new ArrayList<>());

        bundle.setCode(code);

        if (businessAccountModels != null) {
            for (BusinessAccountModel bam : businessAccountModels) {
                bundle.getBusinessAccountModels().add(bam);
            }
        }

        if (attachments != null) {
            for (DigitalResource attachment : attachments) {
                bundle.addAttachment(attachment);
            }
        }

        if (channels != null) {
            for (Channel channel : channels) {
                bundle.getChannels().add(channel);
            }
        }

        if (offerTemplateCategories != null) {
            for (OfferTemplateCategory offerTemplateCategory : offerTemplateCategories) {
                bundle.getOfferTemplateCategories().add(offerTemplateCategory);
            }
        }

        if (walletTemplates != null) {
            for (WalletTemplate wt : walletTemplates) {
                bundle.getWalletTemplates().add(wt);
            }
        }

        if (bundleProductTemplates != null) {
            for (BundleProductTemplate bpt : bundleProductTemplates) {
                bpt.setId(null);
                bundle.addBundleProductTemplate(bpt);
            }
        }

        if (chargeTemplates != null) {
            for (ProductChargeTemplate chargeTemplate : chargeTemplates) {
                bundle.getProductChargeTemplates().add(chargeTemplate);
            }
        }

        if (sellers != null) {
            for (Seller seller : sellers) {
                bundle.getSellers().add(seller);
            }
        }
        
        if (persist) {
            create(bundle);
        }

        return bundle;
    }
}