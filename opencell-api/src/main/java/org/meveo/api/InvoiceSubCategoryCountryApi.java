package org.meveo.api;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.InvoiceSubCategoryCountryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.TaxService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class InvoiceSubCategoryCountryApi extends BaseApi {

    @Inject
    private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private TaxService taxService;

    public void create(InvoiceSubCategoryCountryDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (StringUtils.isBlank(postData.getCountry())) {
            missingParameters.add("country");
        }

        if (StringUtils.isBlank(postData.getTax()) && StringUtils.isBlank(postData.getTaxCodeEL())) {
            missingParameters.add("tax");
        }

        handleMissingParameters();

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory());
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getInvoiceSubCategory());
        }

        TradingCountry tradingCountry = null;
        if (!StringUtils.isBlank(postData.getCountry())) {
            tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry());
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
            }
            if (invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory, tradingCountry, postData.getStartValidityDate(),
                postData.getEndValidityDate()) != null) {
                throw new EntityAlreadyExistsException(
                    "InvoiceSubCategoryCountry with invoiceSubCategory=" + postData.getInvoiceSubCategory() + ", tradingCountry=" + postData.getCountry() + " already exists.");
            }
        }

        Tax tax = null;
        if (!StringUtils.isBlank(postData.getTax())) {
            tax = taxService.findByCode(postData.getTax());
            if (tax == null) {
                throw new EntityDoesNotExistsException(Tax.class, postData.getTax());
            }
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = new InvoiceSubcategoryCountry();
        invoiceSubcategoryCountry.setInvoiceSubCategory(invoiceSubCategory);
        invoiceSubcategoryCountry.setTax(tax);
        invoiceSubcategoryCountry.setTradingCountry(tradingCountry);
        invoiceSubcategoryCountry.setFilterEL(postData.getFilterEL());
        invoiceSubcategoryCountry.setTaxCodeEL(postData.getTaxCodeEL());
        invoiceSubcategoryCountry.setStartValidityDate(postData.getStartValidityDate());
        invoiceSubcategoryCountry.setEndValidityDate(postData.getEndValidityDate());
        invoiceSubcategoryCountry.setPriority(postData.getPriority());

        invoiceSubCategoryCountryService.create(invoiceSubcategoryCountry);
    }

    public void update(InvoiceSubCategoryCountryDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getInvoiceSubCategory())) {
            missingParameters.add("invoiceSubCategory");
        }
        if (StringUtils.isBlank(postData.getCountry())) {
            missingParameters.add("country");
        }
        if (StringUtils.isBlank(postData.getTax()) && StringUtils.isBlank(postData.getTaxCodeEL())) {
            missingParameters.add("tax");
        }

        handleMissingParameters();

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry());
        if (tradingCountry == null) {
            throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory());
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getInvoiceSubCategory());
        }

        Tax tax = null;
        if (!StringUtils.isBlank(postData.getTax())) {
            tax = taxService.findByCode(postData.getTax());
            if (tax == null) {
                throw new EntityDoesNotExistsException(Tax.class, postData.getTax());
            }
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory,
            tradingCountry, postData.getStartValidityDate(), postData.getEndValidityDate());

        if (invoiceSubcategoryCountry == null) {
            throw new EntityDoesNotExistsException(
                "InvoiceSubCategoryCountry with invoiceSubCategory=" + postData.getInvoiceSubCategory() + ", tradingCountry=" + postData.getCountry() + " does not exists.");
        }

        if (postData.getStartValidityDate() != null) {
            invoiceSubcategoryCountry.setStartValidityDate(postData.getStartValidityDate());
        }
        if (postData.getEndValidityDate() != null) {
            invoiceSubcategoryCountry.setEndValidityDate(postData.getEndValidityDate());
        }
        if (postData.getPriority() != 0) {
            invoiceSubcategoryCountry.setPriority(postData.getPriority());
        }

        invoiceSubcategoryCountry.setTax(tax);
        invoiceSubcategoryCountry.setTaxCodeEL(postData.getTaxCodeEL());

        invoiceSubCategoryCountryService.update(invoiceSubcategoryCountry);
    }

    public InvoiceSubCategoryCountryDto find(String invoiceSubCategoryCode, String countryCode) throws MeveoApiException {

        if (StringUtils.isBlank(invoiceSubCategoryCode)) {
            missingParameters.add("invoiceSubCategoryCode");
        }
        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("country");
        }

        handleMissingParameters();

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode);
        if (tradingCountry == null) {
            throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(invoiceSubCategoryCode);
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, invoiceSubCategoryCode);
        }

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory,
            tradingCountry, Arrays.asList("invoiceSubCategory", "tradingCountry", "tax"), null, null);
        if (invoiceSubcategoryCountry == null) {
            throw new EntityDoesNotExistsException(
                "InvoiceSubCategoryCountry with invoiceSubCategory=" + invoiceSubCategoryCode + ", tradingCountry=" + countryCode + " already exists.");
        }

        return new InvoiceSubCategoryCountryDto(invoiceSubcategoryCountry);
    }

    public void remove(String invoiceSubCategoryCode, String countryCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(invoiceSubCategoryCode)) {
            missingParameters.add("invoiceSubCategoryCode");
        }
        if (StringUtils.isBlank(countryCode)) {
            missingParameters.add("country");
        }

        handleMissingParameters();

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(countryCode);
        if (tradingCountry == null) {
            throw new EntityDoesNotExistsException(TradingCountry.class, countryCode);
        }

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(invoiceSubCategoryCode);
        if (invoiceSubCategory == null) {
            throw new EntityDoesNotExistsException(InvoiceSubCategory.class, invoiceSubCategoryCode);
        }

        List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = invoiceSubCategoryCountryService.listByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory,
            tradingCountry, Arrays.asList("invoiceSubCategory", "tradingCountry", "tax"), null, null);
        if (invoiceSubcategoryCountries == null) {
            throw new EntityDoesNotExistsException(
                "InvoiceSubCategoryCountry with invoiceSubCategory=" + invoiceSubCategoryCode + ", tradingCountry=" + countryCode + " does not exists.");
        } else {
            for (InvoiceSubcategoryCountry invoiceSubcategoryCountry : invoiceSubcategoryCountries) {
                invoiceSubCategoryCountryService.remove(invoiceSubcategoryCountry);
            }
        }
    }

    /**
     * Create or update InvoiceSubCategoryCountry based on the invoice sub-category and country attached.
     * 
     * @param postData instance of InvoiceSubCategoryCountryDto
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void createOrUpdate(InvoiceSubCategoryCountryDto postData) throws MeveoApiException, BusinessException {

        TradingCountry tradingCountry = tradingCountryService.findByTradingCountryCode(postData.getCountry());

        InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategory());

        InvoiceSubcategoryCountry invoiceSubcategoryCountry = invoiceSubCategoryCountryService.findByInvoiceSubCategoryAndCountryWithValidityDates(invoiceSubCategory,
            tradingCountry, postData.getStartValidityDate(), postData.getEndValidityDate());

        if (invoiceSubcategoryCountry == null) {
            create(postData);
        } else {
            update(postData);
        }

    }
}
