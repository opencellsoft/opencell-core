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

import java.util.Arrays;
import java.util.List;

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
 * Standard backing bean for {@link InvoiceSubCategory} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class InvoiceSubCategoryBean extends CustomFieldBean<InvoiceSubCategory> {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link InvoiceSubCategory} service. Extends {@link PersistenceService}.
     */
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    /**
     * Inject InvoiceCategory service, that is used to load default category if its id was passed in parameters.
     */
    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    /**
     * InvoiceCategory Id passed as a parameter. Used when creating new InvoiceSubCategory from InvoiceCategory window, so default InvoiceCategory will be set on newly created
     * InvoiceSubCategory.
     */
    @Inject
    @Param
    private Long invoiceCategoryId;

    /** paramBeanFactory */
    private InvoiceSubcategoryCountry invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();

    public void newInvoiceSubcategoryCountryInstance() {
        invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
    }

    public String saveInvoiceSubCategoryCountry() {
        log.info("saveInvoiceSubCategoryCountry getObjectId={}", getObjectId());

        try {
            if (invoiceSubcategoryCountry != null) {
                ParamBean paramBean = paramBeanFactory.getInstance();
                String datePattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");

                if (invoiceSubcategoryCountry.getId() != null) {
                    invoiceSubCategoryCountryService.update(invoiceSubcategoryCountry);
                    entity = invoiceSubCategoryService.refreshOrRetrieve(entity);
                    messages.info(new BundleKey("messages", "update.successful"));
                } else {
                    invoiceSubcategoryCountry.setInvoiceSubCategory(entity);
                    try {
                        invoiceSubCategoryCountryService.create(invoiceSubcategoryCountry);
                    } catch (BusinessException e1) {
                        if (invoiceSubcategoryCountry.isStrictMatch()) {
                            messages.error(new BundleKey("messages", "invoiceSubCategoryCountry.validityDates.matchingFound.strict"),
                                invoiceSubcategoryCountry.getStartValidityDateMatch() == null ? "null"
                                        : DateUtils.formatDateWithPattern(invoiceSubcategoryCountry.getStartValidityDateMatch(), datePattern),
                                invoiceSubcategoryCountry.getEndValidityDateMatch() == null ? "null"
                                        : DateUtils.formatDateWithPattern(invoiceSubcategoryCountry.getEndValidityDateMatch(), datePattern));
                            return null;
                        }
                    }

                    if (invoiceSubcategoryCountry.isStrictMatch() != null && !invoiceSubcategoryCountry.isStrictMatch()) {
                        messages.warn(new BundleKey("messages", "invoiceSubCategoryCountry.validityDates.matchingFound"),
                            invoiceSubcategoryCountry.getStartValidityDateMatch() == null ? "null"
                                    : DateUtils.formatDateWithPattern(invoiceSubcategoryCountry.getStartValidityDateMatch(), datePattern),
                            invoiceSubcategoryCountry.getEndValidityDateMatch() == null ? "null"
                                    : DateUtils.formatDateWithPattern(invoiceSubcategoryCountry.getEndValidityDateMatch(), datePattern));
                    }

                    entity.getInvoiceSubcategoryCountries().add(invoiceSubcategoryCountry);
                    messages.info(new BundleKey("messages", "save.successful"));
                }

                invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
                return null;
            }
        } catch (Exception e) {
            log.error("exception when applying one invoiceSubCategoryCountry !", e);
            messages.error(new BundleKey("messages", "invoiceSubCategory.uniqueTaxFlied"));

            return null;
        }

        invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();

        return null;
    }

    @ActionMethod
    public void deleteInvoiceSubcategoryCountry(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
        try {
            invoiceSubCategoryCountryService.remove(invoiceSubcategoryCountry.getId());
            entity = invoiceSubCategoryService.refreshOrRetrieve(entity);

            /* these two lines serve for nothing but to solve a tricky problem with JSF and object fields lazily-loaded (org.hibernate.LazyInitializationException)*/
            entity.getInvoiceCategory().getInvoiceSubCategories().size();
            entity.getAccountingCode().getNotes();
        } catch (Exception e) {
            messages.error(new BundleKey("messages", "error.delete.unexpected"));
        }
    }

    public void editInvoiceSubcategoryCountry(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
        this.invoiceSubcategoryCountry = invoiceSubCategoryCountryService.refreshOrRetrieve(invoiceSubcategoryCountry);
    }

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public InvoiceSubCategoryBean() {
        super(InvoiceSubCategory.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return invoice sub category
     */
    @Override
    public InvoiceSubCategory initEntity() {
        InvoiceSubCategory invoiceCatSub = super.initEntity();

        if (invoiceCategoryId != null) {
            entity.setInvoiceCategory(invoiceCategoryService.findById(invoiceCategoryId));
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
