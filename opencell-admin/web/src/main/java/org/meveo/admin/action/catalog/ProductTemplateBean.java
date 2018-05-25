package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.LifeCycleStatusEnum;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.BusinessProductModelService;
import org.meveo.service.catalog.impl.ChannelService;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductChargeTemplateService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class ProductTemplateBean extends CustomFieldBean<ProductTemplate> {

    private static final long serialVersionUID = -7002455215420815747L;

    @Inject
    protected ProductTemplateService productTemplateService;

    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Inject
    private DigitalResourceService digitalResourceService;

    @Inject
    private WalletTemplateService walletTemplateService;

    @Inject
    private BusinessAccountModelService businessAccountModelService;

    @Inject
    private ChannelService channelService;

    @Inject
    private ProductChargeTemplateService productChargeTemplateService;

    @Produces
    @Named
    private ProductChargeTemplate productChargeTemplate = new ProductChargeTemplate();

    @Inject
    protected BusinessProductModelService businessProductModelService;
    
    private DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM;
    private DualListModel<DigitalResource> attachmentsDM;
    private DualListModel<WalletTemplate> walletTemplatesDM;
    private DualListModel<BusinessAccountModel> bamDM;
    private DualListModel<Channel> channelDM;

    private BusinessProductModel businessProductModel;

    private String editMode;
    private boolean newVersion;
    private Long bpmId;
    private boolean duplicateProduct;

    public ProductTemplateBean() {
        super(ProductTemplate.class);
    }

    @Override
    public ProductTemplate initEntity() {
        if (bpmId != null) {
            duplicateFromBPM();
            entity.setValidity(new DatePeriod());
            bpmId = null;
        } else {
            super.initEntity();

            if (newVersion) {
                instantiateNewVersion();
                setObjectId(entity.getId());
                newVersion = false;
            } else if (duplicateProduct) {
                duplicateWithoutSave();
            }

            if (entity.getValidity() == null) {
                entity.setValidity(new DatePeriod());
            }
        }

        return entity;
    }

    private void duplicateFromBPM() {
        try {

            businessProductModel = businessProductModelService.findById(bpmId);
            ProductTemplate product = businessProductModel.getProductTemplate();

            businessProductModelService.detach(businessProductModel);

            String code = product.getCode();

            entity = productTemplateService.duplicate(product, false);
            // Preserve the offer template original code
            entity.setCode(code);

            setObjectId(null);

        } catch (BusinessException e) {
            log.error("Error encountered while duplicating product template from BPM: {}", bpmId, e);
        }
    }

    @Override
    protected IPersistenceService<ProductTemplate> getPersistenceService() {
        return productTemplateService;
    }

    @ActionMethod
    public void duplicate() {
        if (entity != null && entity.getId() != null) {
            try {
                productTemplateService.duplicate(entity);
                messages.info(new BundleKey("messages", "duplicate.successfull"));
            } catch (BusinessException e) {
                log.error("Error encountered duplicating product template entity: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.duplicate.unexpected"));
            }
        }
    }

    @ActionMethod
    public void duplicateWithoutSave() {
        if (entity != null && entity.getId() != null) {
            try {
                productTemplateService.duplicate(entity, false);
            } catch (BusinessException e) {
                log.error("Error encountered duplicating product template entity: {}", entity.getCode(), e);
            }
        }
    }

    @ActionMethod
    public void instantiateNewVersion() {
        if (entity != null && entity.getId() != null) {
            try {
                entity = productTemplateService.instantiateNewVersion(entity);
                messages.info(new BundleKey("messages", "newVersion.successful"));
            } catch (BusinessException e) {
                log.error("Error encountered instantiating new offer template entity version: {}", entity.getCode(), e);
                messages.error(new BundleKey("messages", "error.newVersion.unsuccessful"));
            }
        }
    }

    public String activateProduct() throws BusinessException {
        if (entity.getValidity() == null || entity.getValidity().isValid()) {
            entity.setActive(true);
            return saveOrUpdate(false);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valid From cannot be greater than Valid To.", "Valid From cannot be greater than Valid To."));
        }
        return "";
    }

    public String saveAsDraft() throws BusinessException {
        if (entity.getValidity() == null || entity.getValidity().isValid()) {
            entity.setActive(false);
            return saveOrUpdate(false);
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valid From cannot be greater than Valid To.", "Valid From cannot be greater than Valid To."));
        }
        return "";
    }

    public String discardChanges() {
        return "mm_productTemplates";
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        if (businessProductModel != null) {
            businessProductModel = businessProductModelService.refreshOrRetrieve(businessProductModel);
            businessProductModelService.instantiateBPM(entity, businessProductModel);
            return back();

        } else {
            if (!entity.isTransient()) {
                productTemplateService.refreshOrRetrieve(entity);
            }
            if (offerTemplateCategoriesDM != null && (offerTemplateCategoriesDM.getSource() != null || offerTemplateCategoriesDM.getTarget() != null)) {
                entity.getOfferTemplateCategories().clear();
                entity.getOfferTemplateCategories().addAll(offerTemplateCategoryService.retrieveIfNotManaged(offerTemplateCategoriesDM.getTarget()));
            }

            if (attachmentsDM != null && (attachmentsDM.getSource() != null || attachmentsDM.getTarget() != null)) {
                entity.getAttachments().clear();
                entity.getAttachments().addAll(digitalResourceService.refreshOrRetrieve(attachmentsDM.getTarget()));
            }

            if (walletTemplatesDM != null && (walletTemplatesDM.getSource() != null || walletTemplatesDM.getTarget() != null)) {
                entity.getWalletTemplates().clear();
                entity.getWalletTemplates().addAll(walletTemplateService.refreshOrRetrieve(walletTemplatesDM.getTarget()));
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

    public DualListModel<DigitalResource> getAttachmentsDM() {
        if (attachmentsDM == null) {
            List<DigitalResource> perksSource = digitalResourceService.listActive();

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

    public DualListModel<WalletTemplate> getWalletTemplatesDM() {
        if (walletTemplatesDM == null) {
            List<WalletTemplate> perksSource = walletTemplateService.listActive();

            List<WalletTemplate> perksTarget = new ArrayList<WalletTemplate>();
            if (entity.getWalletTemplates() != null) {
                perksTarget.addAll(entity.getWalletTemplates());
            }
            perksSource.removeAll(perksTarget);

            walletTemplatesDM = new DualListModel<WalletTemplate>(perksSource, perksTarget);
        }

        return walletTemplatesDM;
    }

    public void setWalletTemplatesDM(DualListModel<WalletTemplate> walletTemplatesDM) {
        this.walletTemplatesDM = walletTemplatesDM;
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
            if (entity.getBusinessAccountModels() != null) {
                perksTarget.addAll(entity.getChannels());
            }
            perksSource.removeAll(perksTarget);

            channelDM = new DualListModel<Channel>(perksSource, perksTarget);
        }

        return channelDM;
    }

    public void onNameChange() {
        if (StringUtils.isBlank(entity.getCode())) {
            entity.setCode(entity.getName());
        }
    }

    public void setChannelDM(DualListModel<Channel> channelDM) {
        this.channelDM = channelDM;
    }

    public boolean isBundleTemplate(ProductTemplate pt) {
        return pt instanceof BundleTemplate;
    }

    public String getEditMode() {
        return editMode;
    }

    public void setEditMode(String editMode) {
        this.editMode = editMode;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public boolean displayStatus(ProductTemplate product) {

        if ((Arrays.asList(LifeCycleStatusEnum.ACTIVE, LifeCycleStatusEnum.LAUNCHED, LifeCycleStatusEnum.IN_TEST).contains(product.getLifeCycleStatus()))) {
            return product.getValidity() == null || product.getValidity().isCorrespondsToPeriod(new Date());
        }

        return false;
    }

    public void newProductChargeTemplate() {
        productChargeTemplate = new ProductChargeTemplate();
    }

    public void editProductChargeTemplate(ProductChargeTemplate productChargeTemplate) {
        this.productChargeTemplate = productChargeTemplate;
    }

    public void deleteProductChargeTemplate(Long id) throws BusinessException {
        ProductChargeTemplate productCharge = productChargeTemplateService.findById(id);
        entity.getProductChargeTemplates().remove(productCharge);
        productCharge.getProductTemplates().remove(entity);
        entity = getPersistenceService().update(entity);
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void saveProductChargeTemplate() {
        log.info("saveProductChargeTemplate getObjectId=" + getObjectId());

        try {
            if (productChargeTemplate == null) {
                return;
            }
            // TODO this line might cause an issue when after update of charge
            // template service template can not be saved
            entity = productTemplateService.findById(entity.getId());
            productChargeTemplate = productChargeTemplateService.findById(productChargeTemplate.getId());
            if (!productChargeTemplate.getProductTemplates().contains(entity)) {
                productChargeTemplate.getProductTemplates().add(entity);
                entity.getProductChargeTemplates().add(productChargeTemplate);
                productChargeTemplateService.update(productChargeTemplate);
            }
            messages.info(new BundleKey("messages", "save.successful"));
            newProductChargeTemplate();
            setActiveTab(1);
            if (entity.getValidity() == null) {
                entity.setValidity(new DatePeriod());
            }
        } catch (Exception e) {
            log.error("error when saving productCharge", e);
            messages.error("error when creating product charge:" + e.getMessage());
        }
    }

    public ProductChargeTemplate getProductChargeTemplate() {
        return productChargeTemplate;
    }

    public void setProductChargeTemplate(ProductChargeTemplate productChargeTemplate) {
        this.productChargeTemplate = productChargeTemplate;
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

        List<ProductOffering> matchedVersions = productTemplateService.getMatchingVersions(code, from, to, entity.getId(), true);

        if (!matchedVersions.isEmpty()) {
            messages.error(new BundleKey("messages", "productTemplate.version.exists"),
                matchedVersions.get(0).getValidity() == null ? " / " : matchedVersions.get(0).getValidity().toString(paramBeanFactory.getInstance().getDateFormat()));
            return false;
        }

        return true;
    }

    public List<ProductTemplate> listActiveByDate(Date date) {
        return productTemplateService.listActiveByDate(date);
    }

    public LazyDataModel<ProductTemplate> listProductsForBundle(BundleTemplate bt, List<BundleProductTemplate> bundleProductTemplates) {
        filters.clear();

        List<Long> ids = new ArrayList<>();
        for (BundleProductTemplate bpt : bundleProductTemplates) {
            ids.add(bpt.getProductTemplate().getId());
        }
        filters.put("ne code", bt.getCode());
        if (!ids.isEmpty()) {
            filters.put("ne id", ids);
        }

        @SuppressWarnings("rawtypes")
        List<Class> types = new ArrayList<>();
        types.add(ProductTemplate.class);
        types.add(BundleTemplate.class);
        filters.put(PersistenceService.SEARCH_ATTR_TYPE_CLASS, types);

        return getLazyDataModel();
    }

    public Long getBpmId() {
        return bpmId;
    }

    public void setBpmId(Long bpmId) {
        this.bpmId = bpmId;
    }

    public boolean isDuplicateProduct() {
        return duplicateProduct;
    }

    public void setDuplicateProduct(boolean duplicateProduct) {
        this.duplicateProduct = duplicateProduct;
    }
}