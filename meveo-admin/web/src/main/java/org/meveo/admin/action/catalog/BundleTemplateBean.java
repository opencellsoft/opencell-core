package org.meveo.admin.action.catalog;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BundleProductTemplateService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.meveo.service.catalog.impl.ChannelService;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class BundleTemplateBean extends CustomFieldBean<BundleTemplate> {

	private static final long serialVersionUID = -2076286547281668406L;

	@Inject
	protected BundleTemplateService bundleTemplateService;

	@Inject
	protected BundleProductTemplateService bundleProductTemplateService;

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

	@Inject
	private ChannelService channelService;

	@Inject
	private DigitalResourceService digitalResourceService;

	private PricePlanMatrix entityPricePlan;
	private BigDecimal catalogPrice;
	private BigDecimal discountedAmount;
	private CustomFieldInstance catalogPriceCF;

	private String editMode;

	private List<ProductTemplate> productTemplatesToAdd;

	private DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM;
	private DualListModel<DigitalResource> attachmentsDM;
	private DualListModel<BusinessAccountModel> bamDM;
	private DualListModel<Channel> channelDM;

	private UploadedFile uploadedFile;

	public BundleTemplateBean() {
		super(BundleTemplate.class);
	}

	@Override
	protected IPersistenceService<BundleTemplate> getPersistenceService() {
		return bundleTemplateService;
	}

	@Override
	public BundleTemplate initEntity() {
		BundleTemplate result = super.initEntity();

		initPricePlan();

		return result;
	}

	private void initPricePlan() {
		boolean exists = false;
		if (entity.getProductChargeTemplate() == null)
			return;

		List<PricePlanMatrix> pricePlanMatrixes = pricePlanMatrixService.listByEventCodeWithOrder(entity.getProductChargeTemplate().getCode(), currentUser.getProvider(),
				"priority");
		if (pricePlanMatrixes != null && pricePlanMatrixes.size() > 0) {
			entityPricePlan = pricePlanMatrixes.get(0);
			log.debug("IPIEL: found price plan {}", entityPricePlan);
			exists = true;
		}

		if (!exists) {
			log.debug("IPIEL: no priceplan found creating...");
			// create new price plan
			entityPricePlan = new PricePlanMatrix();
			entityPricePlan.setAmountWithoutTax(new BigDecimal(0));
		}

		List<CustomFieldInstance> cfInstances = getCustomFieldInstances(entity).get(ProductTemplate.CF_CATALOG_PRICE);

		CustomFieldInstance cfInstance = null;
		if (cfInstances != null && cfInstances.size() > 0) {
			cfInstance = cfInstances.get(0);
			if (cfInstance != null) {
				log.debug("IPIEL: no CFI found");
				catalogPriceCF = cfInstance;
			}
		}

		Map<String, CustomFieldTemplate> entityCFTs = getCustomFieldTemplates(entity);
		CustomFieldTemplate cft = entityCFTs.get(ProductTemplate.CF_CATALOG_PRICE);

		if (cft != null && cfInstance == null) {
			log.debug("IPIEL: creating cf instance from template");
			catalogPriceCF = CustomFieldInstance.fromTemplate(cft, entity);
			catalogPriceCF.setCode(ProductTemplate.CF_CATALOG_PRICE);
		}

		if (catalogPriceCF == null) {
			log.debug("IPIEL: no CF found");
			messages.info(new BundleKey("messages", "message.marketingManager.product.catalogPrice.missing"));
		} else {
			if (catalogPriceCF.getCfValue() == null || catalogPriceCF.getCfValue().getDoubleValue() == null) {
				catalogPriceCF.setDoubleValue(Double.valueOf(0));
			}

			catalogPrice = new BigDecimal(catalogPriceCF.getCfValue().getDoubleValue());
		}
	}

	public boolean isDiscountComputable() {
		if (entity.getProductChargeTemplate() == null)
			return false;

		return catalogPrice != null && entityPricePlan != null && entityPricePlan.getAmountWithoutTax() != null;
	}

	public BigDecimal computeDiscountAmount() {
		BigDecimal result = new BigDecimal(0);

		if (entity.getProductChargeTemplate() == null)
			return result;

		if (entityPricePlan != null && entityPricePlan.getAmountWithoutTax() != null && catalogPriceCF != null && catalogPriceCF.getCfValue() != null
				&& catalogPriceCF.getCfValue().getDoubleValue() != null) {
			if (catalogPrice != null && catalogPrice.compareTo(BigDecimal.ZERO) != 0) {
				result = (entityPricePlan.getAmountWithoutTax().subtract(catalogPrice).multiply(new BigDecimal(100))).divide(catalogPrice);
			}

			result = NumberUtils.round(result, currentUser.getProvider().getRounding() != null ? currentUser.getProvider().getRounding() : 2);
		}

		return result;
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		String outcome = super.saveOrUpdate(killConversation);

		savePricePlanMatrix();

		if (editMode != null && editMode.length() > 0) {
			outcome = "mm_productTemplates";
		}

		return outcome;
	}

	public void handleFileUpload(FileUploadEvent event) throws BusinessException {
		uploadedFile = event.getFile();

		if (uploadedFile != null) {
			byte[] contents = uploadedFile.getContents();
			try {
				entity.setImage(new SerialBlob(contents));
			} catch (SQLException e) {
				entity.setImage(null);
			}
			entity.setImageContentType(uploadedFile.getContentType());

			saveOrUpdate(entity);

			initEntity();

			FacesMessage message = new FacesMessage("Succesful", uploadedFile.getFileName() + " is uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void removeProductTemplateFromBundle(BundleProductTemplate bundleProductTemplate) throws BusinessException {
		entity.getBundleProducts().remove(bundleProductTemplate);
		entity = getPersistenceService().update(entity, getCurrentUser());

		bundleTemplateService.remove(bundleProductTemplate.getId());

		messages.info(new BundleKey("messages", "delete.successful"));
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

			try {
				bundleProductTemplateService.create(bpt, currentUser);
				entity.addBundleProductTemplate(bpt);
				entity = getPersistenceService().update(entity, currentUser);
			} catch (BusinessException e) {
				log.error("IPIEL: fail creating bpt {}", e.getMessage());
				messages.error(e.getMessage());
			}
		}
	}

	private void savePricePlanMatrix() throws BusinessException {
		if (entity.getProductChargeTemplate() == null)
			return;

		if (entityPricePlan.isTransient()) {
			entityPricePlan.setCode(entity.getProductChargeTemplate().getCode());
			entityPricePlan.setEventCode(entity.getProductChargeTemplate().getCode());
			pricePlanMatrixService.create(entityPricePlan, getCurrentUser());
		} else {
			pricePlanMatrixService.update(entityPricePlan, getCurrentUser());
		}

		if (catalogPriceCF != null) {
			catalogPriceCF.setDoubleValue(catalogPrice.doubleValue());
			if (catalogPriceCF.isTransient()) {
				customFieldInstanceService.create(catalogPriceCF, getCustomFieldTemplates(entity).get(catalogPriceCF.getCode()), entity, getCurrentUser());
			} else {
				customFieldInstanceService.update(catalogPriceCF, getCustomFieldTemplates(entity).get(catalogPriceCF.getCode()), entity, getCurrentUser());
			}
		}
	}

	public void computePricePlan() {
		if (catalogPriceCF.getCfValue().getDoubleValue() != null && entityPricePlan.getAmountWithoutTax() != null) {
			catalogPrice = new BigDecimal(catalogPriceCF.getCfValue().getDoubleValue().toString());
			discountedAmount = (entityPricePlan.getAmountWithoutTax().subtract(catalogPrice).multiply(new BigDecimal("100"))).divide(catalogPrice);
		}
	}

	public PricePlanMatrix getEntityPricePlan() {
		return entityPricePlan;
	}

	public void setEntityPricePlan(PricePlanMatrix entityPricePlan) {
		this.entityPricePlan = entityPricePlan;
	}

	public BigDecimal getCatalogPrice() {
		return catalogPrice;
	}

	public void setCatalogPrice(BigDecimal catalogPrice) {
		this.catalogPrice = catalogPrice;
	}

	public BigDecimal getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(BigDecimal discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public CustomFieldInstance getCatalogPriceCF() {
		return catalogPriceCF;
	}

	public void setCatalogPriceCF(CustomFieldInstance catalogPriceCF) {
		this.catalogPriceCF = catalogPriceCF;
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
			List<OfferTemplateCategory> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = offerTemplateCategoryService.list(entity.getProvider(), true);
			} else {
				perksSource = offerTemplateCategoryService.listActive();
			}

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
			List<BusinessAccountModel> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = businessAccountModelService.list(entity.getProvider(), true);
			} else {
				perksSource = businessAccountModelService.listActive();
			}

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
			List<Channel> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = channelService.list(entity.getProvider(), true);
			} else {
				perksSource = channelService.listActive();
			}

			List<Channel> perksTarget = new ArrayList<Channel>();
			if (entity.getChannels() != null) {
				perksTarget.addAll(entity.getChannels());
			}
			perksSource.removeAll(perksTarget);

			channelDM = new DualListModel<Channel>(perksSource, perksTarget);
		}
		return channelDM;
	}

    public void updateDefaultCode() {
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(entity.getName());
        }
    }

	public void setChannelDM(DualListModel<Channel> channelDM) {
		this.channelDM = channelDM;
	}

	public DualListModel<DigitalResource> getAttachmentsDM() {
		if (attachmentsDM == null) {
			List<DigitalResource> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = digitalResourceService.list(entity.getProvider(), true);
			} else {
				perksSource = digitalResourceService.list(currentUser.getProvider(), true);
			}

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
				bundleTemplateService.duplicate(entity, getCurrentUser());
				messages.info(new BundleKey("messages", "save.successful"));
			} catch (BusinessException e) {
				log.error("Error encountered persisting product template entity: {}: {}", entity.getCode(), e);
				messages.error(new BundleKey("messages", "save.unsuccessful"));
			}
		}
	}

}
