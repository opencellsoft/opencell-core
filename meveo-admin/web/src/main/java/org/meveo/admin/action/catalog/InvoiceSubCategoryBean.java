/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.component.tabview.TabView;

/**
 * Standard backing bean for {@link InvoiceSubCategory} (extends
 * {@link BaseBean} that provides almost all common methods to handle entities
 * filtering/sorting in datatable, their create, edit, view, delete operations).
 * It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class InvoiceSubCategoryBean extends
		BaseBean<InvoiceSubCategory> {
	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link InvoiceSubCategory} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

	/**
	 * Inject InvoiceCategory service, that is used to load default category if
	 * its id was passed in parameters.
	 */
	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	@Inject
	private Messages messages;

	/**
	 * InvoiceCategory Id passed as a parameter. Used when creating new
	 * InvoiceSubCategory from InvoiceCategory window, so default
	 * InvoiceCategory will be set on newly created InvoiceSubCategory.
	 */
	@Inject
	@RequestParam
	private Instance<Long> invoiceCategoryId;

	private InvoiceSubcategoryCountry invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();

	private TabView tabView = null;

	public void newInvoiceSubcategoryCountryInstance() {
		invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
	}

	public String saveInvoiceSubCategoryCountry() {
		log.info("saveOneShotChargeIns getObjectId={}", getObjectId());

		try {
			if (invoiceSubcategoryCountry != null) {
				for (InvoiceSubcategoryCountry inc : entity
						.getInvoiceSubcategoryCountries()) {
					if (inc.getTradingCountry()
							.getCountry()
							.getCountryCode()
							.equalsIgnoreCase(
									invoiceSubcategoryCountry
											.getTradingCountry().getCountry()
											.getCountryCode())
							&& !inc.getId().equals(
									invoiceSubcategoryCountry.getId())) {
						throw new Exception();
					}
				}

				if (invoiceSubcategoryCountry.getId() != null) {
					invoiceSubCategoryCountryService
							.update(invoiceSubcategoryCountry);
					messages.info(new BundleKey("messages", "update.successful"));
				} else {
					invoiceSubcategoryCountry.setInvoiceSubCategory(entity);
					invoiceSubCategoryCountryService
							.create(invoiceSubcategoryCountry);
					entity.getInvoiceSubcategoryCountries().add(
							invoiceSubcategoryCountry);
					messages.info(new BundleKey("messages", "save.successful"));
				}

				invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
				return null;
			}
		} catch (Exception e) {
			log.error(
					"exception when applying one invoiceSubCategoryCountry !",
					e);
			messages.error(new BundleKey("messages",
					"invoiceSubCategory.uniqueTaxFlied"));

			return null;
		}

		invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();

		return null;
	}

	public void deleteInvoiceSubcategoryCountry(
			InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		invoiceSubcategoryCountry = (InvoiceSubcategoryCountry) invoiceSubCategoryCountryService
				.attach(invoiceSubcategoryCountry);
		invoiceSubCategoryCountryService.remove(invoiceSubcategoryCountry);
		entity.getInvoiceSubcategoryCountries().remove(
				invoiceSubcategoryCountry);
	}

	public void editInvoiceSubcategoryCountry(
			InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		this.invoiceSubcategoryCountry = invoiceSubcategoryCountry;
	}

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceSubCategoryBean() {
		super(InvoiceSubCategory.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public InvoiceSubCategory initEntity() {
		InvoiceSubCategory invoiceCatSub = super.initEntity();

		if (invoiceCategoryId.get() != null) {
			entity.setInvoiceCategory(invoiceCategoryService
					.findById(invoiceCategoryId.get()));
		}
		return invoiceCatSub;
	}

	public List<InvoiceSubCategory> listAll() {
		getFilters();
		if (filters.containsKey("languageCode")) {
			filters.put("language.languageCode", filters.get("languageCode"));
			filters.remove("languageCode");
		} else if (filters.containsKey("language.languageCode")) {
			filters.remove("language.languageCode");
		}
		return super.listAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {

		if (entity.getId() != null) {
			super.saveOrUpdate(killConversation);
	        return getListViewName();

		} else {
		    super.saveOrUpdate(killConversation);
			messages.info(new BundleKey("messages", "invoiceSubCaterogy.AddTax"));
			if (killConversation) {
				endConversation();
			}
			return null;
		}
	}

	@Override
	protected String getListViewName() {
		return "invoiceSubCategories";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<InvoiceSubCategory> getPersistenceService() {
		return invoiceSubCategoryService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "invoiceSubcategoryCountries");
	}

	@Named
	@Produces
	public InvoiceSubcategoryCountry getInvoiceSubcategoryCountry() {
		return invoiceSubcategoryCountry;
	}

	public void setInvoiceSubcategoryCountry(
			InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		this.invoiceSubcategoryCountry = invoiceSubcategoryCountry;
	}

	public TabView getTabView() {
		return tabView;
	}

	public void setTabView(TabView tabView) {
		this.tabView = tabView;
	}

	public void setIndex() {
		tabView.setActiveIndex(0);
	}
}