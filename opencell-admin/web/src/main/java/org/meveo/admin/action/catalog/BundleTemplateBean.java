package org.meveo.admin.action.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.meveo.service.catalog.impl.ChannelService;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.primefaces.model.DualListModel;

/**
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Named
@ViewScoped
public class BundleTemplateBean extends CustomFieldBean<BundleTemplate> {

    private static final long serialVersionUID = -2076286547281668406L;

    @Inject
    protected BundleTemplateService bundleTemplateService;

    @Inject
    private BusinessAccountModelService businessAccountModelService;

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Inject
    private ChannelService channelService;

    @Inject
    private DigitalResourceService digitalResourceService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    private BigDecimal salesPrice;
    private BigDecimal catalogPrice;
    private BigDecimal discountedAmount;

    private String editMode;

    private List<ProductTemplate> productTemplatesToAdd;

    private DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM;
    private DualListModel<DigitalResource> attachmentsDM;
    private DualListModel<BusinessAccountModel> bamDM;
    private DualListModel<Channel> channelDM;

    private boolean newVersion;
    private boolean duplicateBundle;

    public BundleTemplateBean() {
        super(BundleTemplate.class);
    }

    @Override
    protected IPersistenceService<BundleTemplate> getPersistenceService() {
        return bundleTemplateService;
    }

    @Override
    public BundleTemplate initEntity() {
        super.initEntity();

        if (newVersion) {
            instantiateNewVersion();
            setObjectId(entity.getId());
            newVersion = false;
        } else if (duplicateBundle) {
            duplicateWithoutSave();
        }

        if (entity.getValidity() == null) {
            entity.setValidity(new DatePeriod());
        }

        return entity;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        if (offerTemplateCategoriesDM != null && (offerTemplateCategoriesDM.getSource() != null || offerTemplateCategoriesDM.getTarget() != null)) {
            entity.getOfferTemplateCategories().clear();
            entity.getOfferTemplateCategories().addAll(offerTemplateCategoryService.refreshOrRetrieve(offerTemplateCategoriesDM.getTarget()));
        }

        if (attachmentsDM != null && (attachmentsDM.getSource() != null || attachmentsDM.getTarget() != null)) {
            entity.getAttachments().clear();
            entity.getAttachments().addAll(digitalResourceService.refreshOrRetrieve(attachmentsDM.getTarget()));
        }

        if (bamDM != null && (bamDM.getSource() != null || bamDM.getTarget() != null)) {
            entity.getBusinessAccountModels().clear();
            entity.getBusinessAccountModels().addAll(businessAccountModelService.refreshOrRetrieve(bamDM.getTarget()));
        }

        if (channelDM != null && (channelDM.getSource() != null || channelDM.getTarget() != null)) {
            entity.getChannels().clear();
            entity.getChannels().addAll(channelService.refreshOrRetrieve(channelDM.getTarget()));
        }

        String outcome = super.saveOrUpdate(killConversation);

        if (editMode != null && editMode.length() > 0) {
            outcome = "mm_products";
        }

        return outcome;
    }

    public void addProductTemplateToBundle(ProductTemplate prodTemplate) {
        boolean found = false;
        for (BundleProductTemplate bpt : entity.getBundleProducts()) {
            if (prodTemplate.equals(bpt.getProductTemplate())) {
                found = true;
            }
        }

        if (!found) {
            BundleProductTemplate bpt = new BundleProductTemplate();
            bpt.setProductTemplate(prodTemplate);
            bpt.setBundleTemplate(entity);

            entity.addBundleProductTemplate(bpt);

            messages.info(new BundleKey("messages", "bundleTemplate.productTemplate.create.successful"));
        }
    }

    public void removeProductTemplateFromBundle(BundleProductTemplate bundleProductTemplate) throws BusinessException {
        try {
            entity.getBundleProducts().remove(bundleProductTemplate);

            messages.info(new BundleKey("messages", "bundleTemplate.productTemplate.delete.successful"));
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
    }

    public BigDecimal getCatalogPrice() {
        return catalogPrice;
    }

    public void setCatalogPrice(BigDecimal catalogPrice) {
        this.catalogPrice = catalogPrice;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public BigDecimal getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(BigDecimal discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public String getEditMode() {
        return editMode;
    }

    public void setEditMode(String editMode) {
        this.editMode = editMode;
    }

    public List<ProductTemplate> getProductTemplatesToAdd() {
        return productTemplatesToAdd;
    }

    public void setProductTemplatesToAdd(List<ProductTemplate> productTemplatesToAdd) {
        this.productTemplatesToAdd = productTemplatesToAdd;
    }

    public DualListModel<OfferTemplateCategory> getOfferTemplateCategoriesDM() {
        if (offerTemplateCategoriesDM == null) {
            List<OfferTemplateCategory> perksSource = offerTemplateCategoryService.listActive();

            List<OfferTemplateCategory> perksTarget = new ArrayList<OfferTemplateCategory>();
            if (entity.getOfferTemplateCategories() != null) {
                perksTarget.addAll(entity.getOfferTemplateCategories());
            }
            perksSource.removeAll(perksTarget);

            offerTemplateCategoriesDM = new DualListModel<OfferTemplateCategory>(perksSource, perksTarget);
        }

        return offerTemplateCategoriesDM;
    }

    public void setOfferTemplateCategoriesDM(DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM) {
        this.offerTemplateCategoriesDM = offerTemplateCategoriesDM;
    }

    public DualListModel<BusinessAccountModel> getBamDM() {
        if (bamDM == null) {
            List<BusinessAccountModel> perksSource = businessAccountModelService.listActive();

            List<BusinessAccountModel> perksTarget = new ArrayList<BusinessAccountModel>();
            if (entity.getBusinessAccountModels() != null) {
                perksTarget.addAll(entity.getBusinessAccountModels());
            }
            perksSource.removeAll(perksTarget);

            bamDM = new DualListModel<BusinessAccountModel>(perksSource, perksTarget);
        }

        return bamDM;
    }

    public void setBamDM(DualListModel<BusinessAccountModel> bamDM) {
        this.bamDM = bamDM;
    }

    public DualListModel<Channel> getChannelDM() {
        if (channelDM == null) {
            List<Channel> perksSource = channelService.listActive();

            List<Channel> perksTarget = new ArrayList<Channel>();
            if (entity.getChannels() != null) {
                perksTarget.addAll(entity.getChannels());
            }
            perksSource.removeAll(perksTarget);

            channelDM = new DualListModel<Channel>(perksSource, perksTarget);
        }
        return channelDM;
    }

    public void onNameChange() {
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(entity.getName());
        }
    }

    public void setChannelDM(DualListModel<Channel> channelDM) {
        this.channelDM = channelDM;
    }

    public DualListModel<DigitalResource> getAttachmentsDM() {
        if (attachmentsDM == null) {
            List<DigitalResource> perksSource = digitalResourceService.list(true);

            List<DigitalResource> perksTarget = new ArrayList<DigitalResource>();
            if (entity.getAttachments() != null) {
                perksTarget.addAll(entity.getAttachments());
            }
            perksSource.removeAll(perksTarget);

            attachmentsDM = new DualListModel<DigitalResource>(perksSource, perksTarget);
        }

        return attachmentsDM;
    }

    public void setAttachmentsDM(DualListModel<DigitalResource> attachmentsDM) {
        this.attachmentsDM = attachmentsDM;
    }

    @ActionMethod
    public void duplicate() {
        if (entity != null && entity.getId() != null) {
            try {
                bundleTemplateService.duplicate(entity);
                messages.info(new BundleKey("messages", "duplicate.successfull"));
            } catch (BusinessException e) {
                log.error("Error encountered duplicating product bundle template entity: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
            }
        }
    }

    @ActionMethod
    public void duplicateWithoutSave() {
        if (entity != null && entity.getId() != null) {
            try {
                bundleTemplateService.duplicate(entity, false);
            } catch (BusinessException e) {
                log.error("Error encountered duplicating product bundle template entity: {}", entity.getCode(), e);
            }
        }
    }

    @ActionMethod
    public void instantiateNewVersion() {
        if (entity != null && entity.getId() != null) {
            try {
                entity = bundleTemplateService.instantiateNewVersion(entity);
                messages.info(new BundleKey("messages", "newVersion.successful"));
            } catch (BusinessException e) {
                log.error("Error encountered instantiating new offer template entity version: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.newVersion.unsuccessful"));
            }
        }
    }

    public void setNewVersion(boolean newVersion) {
        this.newVersion = newVersion;
    }

    public boolean isNewVersion() {
        return newVersion;
    }

    public boolean validateUniqueVersion(FacesContext context, List<UIInput> components, List<Object> values) {

        if (values.size() != 3) {
            throw new RuntimeException("Please bind validator to two components in the following order: offer/product/bundle template code, dateFrom, dateTo");
        }

        String code = (String) values.get(0);
        Date from = (Date) values.get(1);
        Date to = (Date) values.get(2);

        List<ProductOffering> matchedVersions = bundleTemplateService.getMatchingVersions(code, from, to, entity.getId(), true);

        if (!matchedVersions.isEmpty()) {
            messages.error(new BundleKey("messages", "bundleTemplate.version.exists"),
                matchedVersions.get(0).getValidity() == null ? " / " : matchedVersions.get(0).getValidity().toString(paramBeanFactory.getInstance().getDateFormat()));
            return false;
        }

        return true;
    }

    public List<BundleTemplate> listActiveByDate(Date date) {
        return bundleTemplateService.listActiveByDate(date);
    }

    public boolean isDuplicateBundle() {
        return duplicateBundle;
    }

    public void setDuplicateBundle(boolean duplicateBundle) {
        this.duplicateBundle = duplicateBundle;
    }
}