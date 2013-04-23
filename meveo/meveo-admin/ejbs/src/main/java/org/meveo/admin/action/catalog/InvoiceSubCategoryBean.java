/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.catalog;

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * Standard backing bean for {@link InvoiceSubCategory} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas
 * @created Dec 15, 2010
 */
@Named
@ConversationScoped
public class InvoiceSubCategoryBean extends BaseBean<InvoiceSubCategory> {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link InvoiceSubCategory} service. Extends {@link PersistenceService}.
     */
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private CatMessagesService catMessagesService;

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    /**
     * Inject InvoiceCategory service, that is used to load default category if its id was passed in parameters.
     */
    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private Messages messages;

    /**
     * InvoiceCategory Id passed as a parameter. Used when creating new InvoiceSubCategory from InvoiceCategory window, so default InvoiceCategory will be set on newly created
     * InvoiceSubCategory.
     */
    @Inject
    @RequestParam
    private Instance<Long> invoiceCategoryId;

    private String[] accountingCodeFields = new String[7];
    private String separator;

    @Produces
    @Named
    private InvoiceSubcategoryCountry invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();

    public void newInvoiceSubcategoryCountryInstance() {
        this.invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
    }

    public void saveInvoiceSubCategoryCountry() {
        log.info("saveOneShotChargeIns getObjectId=#0", getObjectId());

        try {
            if (invoiceSubcategoryCountry != null) {
                for (InvoiceSubcategoryCountry inc : entity.getInvoiceSubcategoryCountries()) {
                    if (inc.getTradingCountry().getCountry().getCountryCode().equalsIgnoreCase(invoiceSubcategoryCountry.getTradingCountry().getCountry().getCountryCode())
                            && !inc.getId().equals(invoiceSubcategoryCountry.getId())) {
                        throw new Exception();
                    }
                }
                if (invoiceSubcategoryCountry.getId() != null) {
                    invoiceSubCategoryCountryService.update(invoiceSubcategoryCountry);
                    messages.info(new BundleKey("messages", "update.successful"));
                } else {
                    invoiceSubcategoryCountry.setInvoiceSubCategory(entity);
                    invoiceSubCategoryCountryService.create(invoiceSubcategoryCountry);
                    entity.getInvoiceSubcategoryCountries().add(invoiceSubcategoryCountry);
                    messages.info(new BundleKey("messages", "save.successful"));
                }
            }
        } catch (Exception e) {
            log.error("exception when applying one invoiceSubCategoryCountry !", e);
            messages.error(new BundleKey("messages", "invoiceSubCategory.uniqueTaxFlied"));
        }
        invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
    }

    
    public void deleteInvoiceSubcategoryCountry(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
    	invoiceSubCategoryCountryService.remove(invoiceSubcategoryCountry);
    	entity.getInvoiceSubcategoryCountries().remove(invoiceSubcategoryCountry);
    }
    
    public void editInvoiceSubcategoryCountry(InvoiceSubcategoryCountry invoiceSubcategoryCountry) {
        this.invoiceSubcategoryCountry = invoiceSubcategoryCountry;
    }

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public InvoiceSubCategoryBean() {
        super(InvoiceSubCategory.class);
        ParamBean param = ParamBean.getInstance("meveo-admin.properties");
        separator = param.getProperty("reporting.accountingCode.separator", ",");
        accountingCodeFields[4] = "ZONE";
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public InvoiceSubCategory initEntity() {
        InvoiceSubCategory invoiceCatSub = super.initEntity();
        languageMessagesMap.clear();
        if (invoiceCatSub.getId() != null) {
            for (CatMessages msg : catMessagesService.getCatMessagesList(InvoiceSubCategory.class.getSimpleName() + "_" + invoiceCatSub.getId())) {
                languageMessagesMap.put(msg.getLanguageCode(), msg.getDescription());
            }
        }
        if (invoiceCategoryId.get() != null) {
            entity.setInvoiceCategory(invoiceCategoryService.findById(invoiceCategoryId.get()));
        }
        parseAccountingCode();
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
    public String saveOrUpdate(boolean killConversation) {
        String back = null;
        if (entity.getId() != null) {
            for (String msgKey : languageMessagesMap.keySet()) {
                String description = languageMessagesMap.get(msgKey);
                CatMessages catMsg = catMessagesService.getCatMessages(entity.getClass().getSimpleName() + "_" + entity.getId(), msgKey);
                if (catMsg != null) {
                    catMsg.setDescription(description);
                    catMessagesService.update(catMsg);
                } else {
                    CatMessages catMessages = new CatMessages(entity.getClass().getSimpleName() + "_" + entity.getId(), msgKey, description);
                    catMessagesService.create(catMessages);
                }
            }
            entity.setAccountingCode(generateAccountingCode());
            super.saveOrUpdate(killConversation);

        } else {
            entity.setAccountingCode(generateAccountingCode());
            entity.setAccountingCode(generateAccountingCode());
            getPersistenceService().create(entity);
            messages.info(new BundleKey("messages", "invoiceSubCaterogy.AddTax"));
            if (killConversation) {
                endConversation();
            }
            for (String msgKey : languageMessagesMap.keySet()) {
                String description = languageMessagesMap.get(msgKey);
                CatMessages catMessages = new CatMessages(entity.getClass().getSimpleName() + "_" + entity.getId(), msgKey, description);
                catMessagesService.create(catMessages);
            }
            
        }

        return back;
    }
    
    @Override
    protected String getListViewName() {
    	 return "invoiceSubCategories";
    }

    /**
     * Constructs cost accounting code
     */
    public String generateAccountingCode() {
        return accountingCodeFields[0] + separator + accountingCodeFields[1] + separator + accountingCodeFields[2] + separator + accountingCodeFields[3] + separator
                + accountingCodeFields[4] + separator + accountingCodeFields[5] + separator + accountingCodeFields[6];
    }

    /**
     * Parses cost accounting code
     * 
     */
    public void parseAccountingCode() {
        if (entity.getAccountingCode() != null) {
            String[] accountingCodeValues = entity.getAccountingCode().split(separator);
            if (accountingCodeValues != null) {
                for (int i = 0; i < accountingCodeFields.length; i++) {
                    if (i < accountingCodeValues.length) {
                        accountingCodeFields[i] = accountingCodeValues[i];
                    }
                }
            }
        }
    }

    /**
     * Override default list view name. (By default its class name starting lower case + 's').
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
     */
    protected String getDefaultViewName() {
        return "invoiceSubCategories";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<InvoiceSubCategory> getPersistenceService() {
        return invoiceSubCategoryService;
    }

    public String getAccountingCodeField1() {
        return accountingCodeFields[0];
    }

    public void setAccountingCodeField1(String accountingCodeField1) {
        this.accountingCodeFields[0] = accountingCodeField1;
    }

    public String getAccountingCodeField2() {
        return accountingCodeFields[1];
    }

    public void setAccountingCodeField2(String accountingCodeField2) {
        this.accountingCodeFields[1] = accountingCodeField2;
    }

    public String getAccountingCodeField3() {
        return accountingCodeFields[2];
    }

    public void setAccountingCodeField3(String accountingCodeField3) {
        this.accountingCodeFields[2] = accountingCodeField3;
    }

    public String getAccountingCodeField4() {
        return accountingCodeFields[3];
    }

    public void setAccountingCodeField4(String accountingCodeField4) {
        this.accountingCodeFields[3] = accountingCodeField4;
    }

    public String getAccountingCodeField5() {
        return accountingCodeFields[4];
    }

    public void setAccountingCodeField5(String accountingCodeField5) {
        this.accountingCodeFields[4] = accountingCodeField5;
    }

    public String getAccountingCodeField6() {
        return accountingCodeFields[5];
    }

    public void setAccountingCodeField6(String accountingCodeField6) {
        this.accountingCodeFields[5] = accountingCodeField6;
    }

    public String getAccountingCodeField7() {
        return accountingCodeFields[6];
    }

    public void setAccountingCodeField7(String accountingCodeField7) {
        this.accountingCodeFields[6] = accountingCodeField7;
    }
}