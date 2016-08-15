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
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BundleProductTemplateService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
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

	private PricePlanMatrix entityPricePlan;
	private BigDecimal catalogPrice;
	private BigDecimal discountedAmount;
	private CustomFieldInstance catalogPriceCF;

	private List<BundleProductTemplate> bundleProductTemplatesToAdd;

	private String editMode;

	private List<ProductTemplate> productTemplatesToAdd;

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
		if (bundleProductTemplatesToAdd == null) {
			bundleProductTemplatesToAdd = new ArrayList<BundleProductTemplate>();
		}
		bundleProductTemplatesToAdd.addAll(entity.getBundleProducts());
		return entity;
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {

		entity.getBundleProducts().clear();
		entity.getBundleProducts().addAll(bundleProductTemplatesToAdd);

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

	public void addProductTemplateToBundle(ProductTemplate prod) {
		BundleProductTemplate bpt = new BundleProductTemplate();
		bpt.setProductTemplate(prod);

		if (bundleProductTemplatesToAdd == null) {
			bundleProductTemplatesToAdd = new ArrayList<BundleProductTemplate>();
		}
		bundleProductTemplatesToAdd.add(bpt);
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

}
