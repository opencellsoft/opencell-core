package org.meveo.admin.action.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.BundleProductTemplate;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BundleTemplateService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class BundleTemplateBean extends CustomFieldBean<BundleTemplate> {

	private static final long serialVersionUID = -2076286547281668406L;

	@Inject
	protected BundleTemplateService bundleTemplateService;

	private PricePlanMatrix entityPricePlan;
	private BigDecimal catalogPrice;
	private BigDecimal discountedAmount;
	private CustomFieldInstance catalogPriceCF;

	private String editMode;

	private List<ProductTemplate> productTemplatesToAdd;

	public BundleTemplateBean() {
		super(BundleTemplate.class);
	}

	@Override
	protected IPersistenceService<BundleTemplate> getPersistenceService() {
		return bundleTemplateService;
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {

		String outcome = super.saveOrUpdate(killConversation);

		if (editMode != null && editMode.length() > 0) {
			outcome = "mmProductTemplates";
		}

		return outcome;
	}

	public String addProductTemplateToBundle(ProductTemplate prod) {
		if (entity != null) {
			BundleProductTemplate bpt = new BundleProductTemplate();
			bpt.setProductTemplate(prod);
			bpt.setQuantity(1);
			if (entity.getBundleProducts() == null) {
				entity.setBundleProducts(new ArrayList<BundleProductTemplate>());
			}
			entity.getBundleProducts().add(bpt);
		}
		return "mmBundleTemplateDetail";
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

}
