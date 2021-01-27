package org.meveo.service.cpq;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.meveo.api.dto.cpq.xml.Header;
import org.meveo.api.dto.cpq.xml.QuoteXmlDto;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;

import javax.xml.bind.JAXBException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

public class QuoteMapperTest {


    @Test
    public void createQuoteVersion() {
        BillingAccount billableAccount = new BillingAccount();
        billableAccount.setCode("BA");
        BillingAccount billingAccount = billableAccount;

        CpqQuote quote = new CpqQuote();
        quote.setApplicantAccount(billingAccount);

        QuoteLot quoteLot = new QuoteLot();
        quoteLot.setName("LOT1");
        quoteLot.setExecutionDate(Date.valueOf(LocalDate.of(2020, 1, 1)));

        QuotePrice price = new QuotePrice();
        price.setUnitPriceWithoutTax(valueOf(10));
        price.setTaxRate(valueOf(3));
        price.setAmountWithTax(valueOf(5));
        price.setAmountWithoutTax(valueOf(15));
        price.setTaxAmount(valueOf(3));

        InvoiceCategory invoiceCategory = new InvoiceCategory();
        invoiceCategory.setCode("INV_CAT");

        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setInvoiceCategory(invoiceCategory);
        invoiceSubCategory.setCode("INV_SUB_CAT");

        AccountingArticle accountingArticle = new AccountingArticle();
        accountingArticle.setCode("ACC_CODE");
        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);

        QuoteArticleLine line = new QuoteArticleLine();
        line.setQuotePrices(List.of(price));
        line.setAccountingArticle(accountingArticle);

        QuoteProduct quoteProduct = new QuoteProduct();
        quoteProduct.setQuoteArticleLines(List.of(line));

        QuoteOffer quoteOffer = new QuoteOffer();
        quoteOffer.setBillableAccount(billingAccount);
        quoteOffer.setQuoteLot(quoteLot);
        quoteOffer.setQuoteProduct(List.of(quoteProduct));

        QuoteVersion quoteVersion = new QuoteVersion();
        quoteVersion.setQuote(quote);
        quoteVersion.setQuoteOffers(List.of(quoteOffer));
    }

    @Test
    public void can_map_a_quote_version_with_billable_account() {
        BillingAccount billableAccount = createBillingAccount();

        CpqQuote quote = new CpqQuote();
        quote.setApplicantAccount(billableAccount);
        quote.setBillableAccount(null);

        QuoteVersion quoteVersion = new QuoteVersion();
        quoteVersion.setQuote(quote);

        QuoteXmlDto quoteXmlDto = new QuoteMapper().map(quoteVersion);

        Header header = quoteXmlDto.getHeader();
        assertThat(header).isNotNull();
        org.meveo.api.dto.cpq.xml.BillingAccount billingAccount = header.getBillingAccount();
        assertThat(billingAccount).isNotNull();
        assertThat(billingAccount.getBillingCycleCode()).isEqualTo("BC_CODE");
        assertThat(billingAccount.getCode()).isEqualTo("BA");
        assertThat(billingAccount.getDescription()).isEqualTo("billing account description");
        assertThat(billingAccount.getExternalRef1()).isEqualTo("external ref1");
        assertThat(billingAccount.getExternalRef2()).isEqualTo("external ref2");
        assertThat(billingAccount.getId()).isEqualTo(1L);
        assertThat(billingAccount.getJobTitle()).isEqualTo("jobTitle");
        assertThat(billingAccount.getRegistrationNo()).isEqualTo("123456");
        assertThat(billingAccount.getVatNo()).isEqualTo("67890");

        assertThat(billingAccount.getName()).isNotNull();
        assertThat(billingAccount.getName().getQuality()).isEqualTo("Society");
        assertThat(billingAccount.getName().getName()).isEqualTo("firstName lastName");

        assertThat(billingAccount.getAddress()).isNotNull();
        assertThat(billingAccount.getAddress().getAddress1()).isEqualTo("address1");
        assertThat(billingAccount.getAddress().getAddress2()).isNull();
        assertThat(billingAccount.getAddress().getAddress3()).isNull();
        assertThat(billingAccount.getAddress().getCity()).isEqualTo("Paris");
        assertThat(billingAccount.getAddress().getCountry()).isEqualTo("Fr");
        assertThat(billingAccount.getAddress().getCountryName()).isEqualTo("France");
        assertThat(billingAccount.getAddress().getState()).isNull();
    }

    private BillingAccount createBillingAccount() {
        BillingCycle billingCycle = new BillingCycle();
        billingCycle.setCode("BC_CODE");
        BillingAccount billableAccount = new BillingAccount();
        billableAccount.setId(1L);
        billableAccount.setJobTitle("jobTitle");
        billableAccount.setCode("BA");
        billableAccount.setDescription("billing account description");
        billableAccount.setExternalRef1("external ref1");
        billableAccount.setExternalRef2("external ref2");
        billableAccount.setBillingCycle(billingCycle);
        billableAccount.setRegistrationNo("123456");
        billableAccount.setVatNo("67890");
        Name name = new Name();
        Title title = new Title();
        title.setIsCompany(true);
        name.setTitle(title);
        name.setFirstName("firstName");
        name.setLastName("lastName");
        billableAccount.setName(name);
        Address address = new Address();
        address.setAddress1("address1");
        address.setCity("Paris");
        address.setZipCode("75010");
        Country country = new Country();
        country.setCountryCode("Fr");
        country.setDescription("France");
        address.setCountry(country);
        billableAccount.setAddress(address);
        return billableAccount;
    }

    @Test
    public void can_serialize_quote() throws Exception {
        BillingAccount billableAccount = createBillingAccount();

        CpqQuote quote = new CpqQuote();
        quote.setApplicantAccount(billableAccount);
        quote.setBillableAccount(null);

        QuoteVersion quoteVersion = new QuoteVersion();
        quoteVersion.setQuote(quote);

        QuoteXmlDto quoteXmlDto = new QuoteMapper().map(quoteVersion);

        String xmlQuoteRepresentation = new XmlQuoteFormatter().format(quoteXmlDto);
        System.out.println(xmlQuoteRepresentation);
    }
}
