package org.meveo.admin.action.catalog;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.DigitalResource;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.meveo.service.catalog.impl.DigitalResourceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.meveo.service.crm.impl.BusinessAccountModelService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

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
	private BundleTemplateService bundleTemplateService;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Inject
	private DigitalResourceService digitalResourceService;

	@Inject
	private WalletTemplateService walletTemplateService;

	@Inject
	private BusinessAccountModelService businessAccountModelService;

	@Inject
	private PricePlanMatrixService pricePlanMatrixService;

	@Inject
	CustomFieldInstanceService customFieldInstanceService;

	private DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM;
	private DualListModel<DigitalResource> attachmentsDM;
	private DualListModel<WalletTemplate> walletTemplatesDM;
	private DualListModel<BusinessAccountModel> bamDM;
	private UploadedFile uploadedFile;

	private String editMode;

	private PricePlanMatrix entityPricePlan;
	private BigDecimal catalogPrice;
	private BigDecimal discountedAmount;
	private CustomFieldInstance catalogPriceCF;

	public ProductTemplateBean() {
		super(ProductTemplate.class);
	}

	@Override
	public ProductTemplate initEntity() {
		super.initEntity();
		getOfferTemplateCategoriesDM();
		getAttachmentsDM();
		getWalletTemplatesDM();
		getBamDM();
		setPricePlan();
		return entity;
	}

	@Override
	protected IPersistenceService<ProductTemplate> getPersistenceService() {
		return productTemplateService;
	}

	public String duplicate() {

		if (entity != null && entity.getId() != null) {

			// entity = productTemplateService.refreshOrRetrieve(entity);

			// Detach and clear ids of entity and related entities
			productTemplateService.detach(entity);
			entity.setId(null);
			String sourceAppliesToEntity = entity.clearUuid();

			// entity.setCode(entity.getCode() + "_copy");

			try {
				productTemplateService.create(entity, getCurrentUser());
				customFieldInstanceService.duplicateCfValues(sourceAppliesToEntity, entity, getCurrentUser());
				messages.info(new BundleKey("messages", "save.successful"));
			} catch (BusinessException e) {
				log.error("Error encountered persisting offer template entity: #{0}:#{1}", entity.getCode(), e);
				messages.error(new BundleKey("messages", "save.unsuccessful"));
			}
		}

		return "mmProductTemplates";
	}

	public String activateProduct() throws BusinessException {
		if (entity.getValidFrom().before(entity.getValidTo())) {
			entity.setActive(true);
			savePricePlanMatrix();
			return saveOrUpdate(false);
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valid From cannot be greater than Valid To.",
							"Valid From cannot be greater than Valid To."));
		}
		return "";
	}

	public String saveAsDraft() throws BusinessException {
		if (entity.getValidFrom().before(entity.getValidTo())) {
			entity.setActive(false);
			savePricePlanMatrix();
			return saveOrUpdate(false);
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valid From cannot be greater than Valid To.",
							"Valid From cannot be greater than Valid To."));
		}
		return "";
	}

	private void savePricePlanMatrix() throws BusinessException {
		if (entityPricePlan.isTransient()) {
			pricePlanMatrixService.create(entityPricePlan, getCurrentUser());
		} else {
			pricePlanMatrixService.update(entityPricePlan, getCurrentUser());
		}
	}

	public String discardChanges() {
		return "mmProductTemplates";
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {

		if (entity.getOfferTemplateCategories() != null) {
			entity.getOfferTemplateCategories().clear();
			entity.getOfferTemplateCategories().addAll(
					offerTemplateCategoryService.refreshOrRetrieve(offerTemplateCategoriesDM.getTarget()));
		}

		if (entity.getAttachments() != null) {
			entity.getAttachments().clear();
			entity.getAttachments().addAll(digitalResourceService.refreshOrRetrieve(attachmentsDM.getTarget()));
		}

		if (entity.getWalletTemplates() != null) {
			entity.getWalletTemplates().clear();
			entity.getWalletTemplates().addAll(walletTemplateService.refreshOrRetrieve(walletTemplatesDM.getTarget()));
		}

		if (entity.getBusinessAccountModels() != null) {
			entity.getBusinessAccountModels().clear();
			entity.getBusinessAccountModels().addAll(businessAccountModelService.refreshOrRetrieve(bamDM.getTarget()));
		}

		String outcome = super.saveOrUpdate(killConversation);

		if (editMode != null && editMode.length() > 0) {
			outcome = "mmProductTemplates";
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

	public DualListModel<OfferTemplateCategory> getOfferTemplateCategoriesDM() {
		if (offerTemplateCategoriesDM == null) {
			List<OfferTemplateCategory> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = offerTemplateCategoryService.list(entity.getProvider(), true);
			} else {
				perksSource = offerTemplateCategoryService.listActive();
			}

			List<OfferTemplateCategory> perksTarget = new ArrayList<OfferTemplateCategory>();
			if (getEntity().getOfferTemplateCategories() != null) {
				perksTarget.addAll(getEntity().getOfferTemplateCategories());
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
			List<DigitalResource> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = digitalResourceService.list(entity.getProvider(), true);
			} else {
				perksSource = digitalResourceService.listActive();
			}

			List<DigitalResource> perksTarget = new ArrayList<DigitalResource>();
			if (getEntity().getAttachments() != null) {
				perksTarget.addAll(getEntity().getAttachments());
			}
			perksSource.removeAll(perksTarget);

			attachmentsDM = new DualListModel<DigitalResource>(perksSource, perksTarget);
		}

		return attachmentsDM;
	}

	public void setAttachmentsDM(DualListModel<DigitalResource> attachmentsDM) {
		this.attachmentsDM = attachmentsDM;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public DualListModel<WalletTemplate> getWalletTemplatesDM() {
		if (walletTemplatesDM == null) {
			List<WalletTemplate> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = walletTemplateService.list(entity.getProvider(), true);
			} else {
				perksSource = walletTemplateService.listActive();
			}

			List<WalletTemplate> perksTarget = new ArrayList<WalletTemplate>();
			if (getEntity().getWalletTemplates() != null) {
				perksTarget.addAll(getEntity().getWalletTemplates());
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
			List<BusinessAccountModel> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = businessAccountModelService.list(entity.getProvider(), true);
			} else {
				perksSource = businessAccountModelService.listActive();
			}

			List<BusinessAccountModel> perksTarget = new ArrayList<BusinessAccountModel>();
			if (getEntity().getBusinessAccountModels() != null) {
				perksTarget.addAll(getEntity().getBusinessAccountModels());
			}
			perksSource.removeAll(perksTarget);

			bamDM = new DualListModel<BusinessAccountModel>(perksSource, perksTarget);
		}

		return bamDM;
	}

	public void setBamDM(DualListModel<BusinessAccountModel> bamDM) {
		this.bamDM = bamDM;
	}

	public void setPricePlan() {
		if (entity != null && entity.getCode() != null && entity.getCode().length() > 0) {
			entityPricePlan = pricePlanMatrixService.findByCode(entity.getCode(), getCurrentProvider());
			if (entityPricePlan == null) {
				entityPricePlan = new PricePlanMatrix();
			}

			String catalogPriceCode = "CATALOG_PRICE";
			List<CustomFieldInstance> cfInstances = customFieldInstanceService.findByCodeLike(catalogPriceCode,
					getCurrentProvider());
			if (cfInstances != null && cfInstances.size() > 0) {
				catalogPriceCF = cfInstances.get(0);
			} else {
				catalogPriceCF = new CustomFieldInstance();
				catalogPriceCF.setCode(catalogPriceCode);
			}
		}
	}

	public boolean isBundleTemplate(ProductTemplate pt) {

		/*
		 * if (bundleTemplateService.findById(pt.getId()) != null) { return
		 * true; } return false;
		 */
		return pt instanceof BundleTemplate;
	}

	public String getEditMode() {
		return editMode;
	}

	public void setEditMode(String editMode) {
		this.editMode = editMode;
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

}
