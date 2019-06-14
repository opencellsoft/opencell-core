/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.omnifaces.cdi.Param;

/**
 * Standard backing bean for {@link InvoiceSubCategory} (extends
 * {@link BaseBean} that provides almost all common methods to handle entities
 * filtering/sorting in datatable, their create, edit, view, delete operations).
 * It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class InvoiceSubCategoryBean extends CustomFieldBean<InvoiceSubCategory> {
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
	 * Inject InvoiceCategory service, that is used to load default category if its
	 * id was passed in parameters.
	 */
	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	/**
	 * InvoiceCategory Id passed as a parameter. Used when creating new
	 * InvoiceSubCategory from InvoiceCategory window, so default InvoiceCategory
	 * will be set on newly created InvoiceSubCategory.
	 */
	@Inject
	@Param
	private Long invoiceCategoryId;

	/** paramBeanFactory */
	private InvoiceSubcategoryCountry invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();

	private enum CrudAction {
		ADD, REMOVE, UPDATE
	};

	private Map<CrudAction, List<InvoiceSubcategoryCountry>> invoiceSubcategoryCountryUpdates = new TreeMap<>();

	public void newInvoiceSubcategoryCountryInstance() {
		invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
	}

	public String saveInvoiceSubCategoryCountry() {
		log.info("saveInvoiceSubCategoryCountry getObjectId={}", getObjectId());

		try {
			if (invoiceSubcategoryCountry != null) {
				validateDates();
				if (invoiceSubcategoryCountry.getId() != null) {
					if (invoiceSubcategoryCountry.getId() > 0) {
						invoiceSubcategoryCountryUpdates.get(CrudAction.UPDATE)
								.removeIf(x -> x.equals(invoiceSubcategoryCountry));
						invoiceSubcategoryCountryUpdates.get(CrudAction.UPDATE).add(invoiceSubcategoryCountry);
					} else {
						invoiceSubcategoryCountryUpdates.get(CrudAction.ADD).remove(invoiceSubcategoryCountry);
						invoiceSubcategoryCountryUpdates.get(CrudAction.ADD).add(invoiceSubcategoryCountry);
					}
					entity.setInvoiceSubcategoryCountries(entity.getInvoiceSubcategoryCountries().stream()
							.map(x -> x.equals(invoiceSubcategoryCountry) ? invoiceSubcategoryCountry : x)
							.collect(Collectors.toList()));
					messages.info(new BundleKey("messages", "customFieldInstance.childEntity.update.successful"));
				} else {
					invoiceSubcategoryCountry
							.setId((long) (-1 - invoiceSubcategoryCountryUpdates.get(CrudAction.ADD).size()));
					entity.getInvoiceSubcategoryCountries().add(invoiceSubcategoryCountry);
					invoiceSubcategoryCountryUpdates.get(CrudAction.ADD).add(invoiceSubcategoryCountry);
					messages.info(new BundleKey("messages", "customFieldInstance.childEntity.save.successful"));
				}
				this.invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
				return null;
			}
		} catch (Exception e) {
			log.error("exception when applying one invoiceSubCategoryCountry !", e);
			messages.error(new BundleKey("messages", "invoiceSubCategory.uniqueTaxFlied"));
			return null;
		}
		return null;
	}

	private void validateDates() {
		ParamBean paramBean = paramBeanFactory.getInstance();
		String datePattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");
		try {
			invoiceSubCategoryCountryService.checkValidityDateFromList(invoiceSubcategoryCountry,
					entity.getInvoiceSubcategoryCountries());
		} catch (BusinessException e) {
			if (invoiceSubcategoryCountry.isStrictMatch()) {
				messages.error(
						new BundleKey("messages", "invoiceSubCategoryCountry.validityDates.matchingFound.strict"),
						invoiceSubcategoryCountry.getStartValidityDateMatch() == null ? "null"
								: DateUtils.formatDateWithPattern(invoiceSubcategoryCountry.getStartValidityDateMatch(),
										datePattern),
						invoiceSubcategoryCountry.getEndValidityDateMatch() == null ? "null"
								: DateUtils.formatDateWithPattern(invoiceSubcategoryCountry.getEndValidityDateMatch(),
										datePattern));
			}
		}
		if (invoiceSubcategoryCountry.isStrictMatch() != null && !invoiceSubcategoryCountry.isStrictMatch()) {
			messages.warn(new BundleKey("messages", "invoiceSubCategoryCountry.validityDates.matchingFound"),
					invoiceSubcategoryCountry.getStartValidityDateMatch() == null ? "null"
							: DateUtils.formatDateWithPattern(invoiceSubcategoryCountry.getStartValidityDateMatch(),
									datePattern),
					invoiceSubcategoryCountry.getEndValidityDateMatch() == null ? "null"
							: DateUtils.formatDateWithPattern(invoiceSubcategoryCountry.getEndValidityDateMatch(),
									datePattern));
		}
	}

	@ActionMethod
	public void deleteInvoiceSubcategoryCountry(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		invoiceSubcategoryCountryUpdates.get(CrudAction.REMOVE).add(invoiceSubcategoryCountry);
		invoiceSubcategoryCountryUpdates.get(CrudAction.UPDATE).removeIf(x -> x.equals(invoiceSubcategoryCountry));
		invoiceSubcategoryCountryUpdates.get(CrudAction.ADD).removeIf(x -> x.equals(invoiceSubcategoryCountry));
		entity.getInvoiceSubcategoryCountries().remove(invoiceSubcategoryCountry);
		messages.info(new BundleKey("messages", "customFieldInstance.childEntity.delete.successful"));
	}

	public void editInvoiceSubcategoryCountry(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		this.invoiceSubcategoryCountry = invoiceSubcategoryCountry;
	}

	/**
	 * Constructor. Invokes super constructor and provides class type of this bean
	 * for {@link BaseBean}.
	 */
	public InvoiceSubCategoryBean() {
		super(InvoiceSubCategory.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @return invoice sub category
	 */
	@Override
	public InvoiceSubCategory initEntity() {
		InvoiceSubCategory invoiceCatSub = super.initEntity();

		if (invoiceCategoryId != null) {
			entity.setInvoiceCategory(invoiceCategoryService.findById(invoiceCategoryId));
		}

		for (CrudAction action : CrudAction.values()) {
			invoiceSubcategoryCountryUpdates.put(action, new ArrayList<InvoiceSubcategoryCountry>());
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
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {

		for (CrudAction action : invoiceSubcategoryCountryUpdates.keySet()) {
			List<InvoiceSubcategoryCountry> ISCset = invoiceSubcategoryCountryUpdates.get(action);
			if (action == CrudAction.REMOVE) {
				for (InvoiceSubcategoryCountry isc : ISCset) {
					invoiceSubCategoryCountryService.remove(isc);
				}
			} else if (action == CrudAction.ADD) {
				for (InvoiceSubcategoryCountry isc : ISCset) {
					isc.setInvoiceSubCategory(entity);
					isc.setId(null);
					invoiceSubCategoryCountryService.create(isc);
				}
			} else if (action == CrudAction.UPDATE) {
				for (InvoiceSubcategoryCountry isc : ISCset) {
					isc.setInvoiceSubCategory(entity);
					invoiceSubCategoryCountryService.update(isc);
				}
			}
		}
		entity = invoiceSubCategoryService.refreshOrRetrieve(entity);
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
		return Arrays.asList("invoiceSubcategoryCountries");
	}

	public InvoiceSubcategoryCountry getInvoiceSubcategoryCountry() {
		return invoiceSubcategoryCountry;
	}

	public void setInvoiceSubcategoryCountry(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
		this.invoiceSubcategoryCountry = invoiceSubcategoryCountry;
	}
}
