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

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BundleProductTemplateService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.meveo.service.catalog.impl.ChannelService;
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

	private PricePlanMatrix entityPricePlan;
	private BigDecimal catalogPrice;
	private BigDecimal discountedAmount;
	private CustomFieldInstance catalogPriceCF;

	private List<BundleProductTemplate> bundleProductTemplatesToAdd;

	private String editMode;

	private List<ProductTemplate> productTemplatesToAdd;

	private DualListModel<OfferTemplateCategory> offerTemplateCategoriesDM;
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
		super.initEntity();

		if (entity != null) {
			bundleProductTemplatesToAdd = new ArrayList<BundleProductTemplate>();
			bundleProductTemplatesToAdd.addAll(entity.getBundleProducts());
		}

		setPricePlan();

		return entity;
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {

		if (entity.getBundleProducts() != null) {
			entity.getBundleProducts().clear();
			entity.getBundleProducts().addAll(bundleProductTemplatesToAdd);
		}

		String outcome = super.saveOrUpdate(killConversation);

		savePricePlanMatrix();

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

	public void addProductTemplateToBundle(ProductTemplate prod) {
		BundleProductTemplate bpt = new BundleProductTemplate();
		bpt.setProductTemplate(prod);

		if (bundleProductTemplatesToAdd != null) {
			bundleProductTemplatesToAdd.add(bpt);
		} else {
			bundleProductTemplatesToAdd = new ArrayList<BundleProductTemplate>();
			bundleProductTemplatesToAdd.add(bpt);
		}

	}

	private void savePricePlanMatrix() throws BusinessException {
		entityPricePlan.setCode(entity.getCode());
		entityPricePlan.setEventCode(entity.getCode());
		if (entityPricePlan.isTransient()) {
			pricePlanMatrixService.create(entityPricePlan, getCurrentUser());
		} else {
			pricePlanMatrixService.update(entityPricePlan, getCurrentUser());
		}
	}

	public void setPricePlan() {
		if (entity != null && entity.getCode() != null && entity.getCode().length() > 0) {
			entityPricePlan = pricePlanMatrixService.findByCode(entity.getCode(), getCurrentProvider());
		}
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

	public List<BundleProductTemplate> getBundleProductTemplatesToAdd() {
		return bundleProductTemplatesToAdd;
	}

	public void setBundleProductTemplatesToAdd(List<BundleProductTemplate> bundleProductTemplatesToAdd) {
		this.bundleProductTemplatesToAdd = bundleProductTemplatesToAdd;
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

	public DualListModel<Channel> getChannelDM() {
		if (channelDM == null) {
			List<Channel> perksSource = null;
			if (entity != null && entity.getProvider() != null) {
				perksSource = channelService.list(entity.getProvider(), true);
			} else {
				perksSource = channelService.listActive();
			}

			List<Channel> perksTarget = new ArrayList<Channel>();
			if (getEntity().getChannels() != null) {
				perksTarget.addAll(getEntity().getChannels());
			}
			perksSource.removeAll(perksTarget);

			channelDM = new DualListModel<Channel>(perksSource, perksTarget);
		}
		return channelDM;
	}

	public void setChannelDM(DualListModel<Channel> channelDM) {
		this.channelDM = channelDM;
	}

}
