package org.meveo.service.cpq;

import org.junit.Before;
import org.junit.Test;
import org.meveo.api.dto.cpq.xml.ArticleLine;
import org.meveo.api.dto.cpq.xml.QuoteLine;
import org.meveo.api.dto.cpq.xml.BillableAccount;
import org.meveo.api.dto.cpq.xml.Category;
import org.meveo.api.dto.cpq.xml.Header;
import org.meveo.api.dto.cpq.xml.Quote;
import org.meveo.api.dto.cpq.xml.QuoteXmlDto;
import org.meveo.api.dto.cpq.xml.SubCategory;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.enums.PriceTypeEnum;
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


    private QuoteMapper quoteMapper;
    private XmlQuoteFormatter xmlQuoteFormatter;

    @Before
    public void setUp() throws Exception {
        quoteMapper = new QuoteMapper();
        xmlQuoteFormatter = new XmlQuoteFormatter();
    }

    @Test
    public void createQuoteVersion() {
        BillingAccount billableAccount = new BillingAccount();
        billableAccount.setCode("BA");
        BillingAccount billingAccount = billableAccount;

        CpqQuote quote = new CpqQuote();
        quote.setApplicantAccount(billingAccount);

        QuoteLot quoteLot = createQuoteLot();

        QuotePrice price = new QuotePrice();
        price.setUnitPriceWithoutTax(valueOf(10));
        price.setTaxRate(valueOf(3));
        price.setAmountWithTax(valueOf(5));
        price.setAmountWithoutTax(valueOf(15));
        price.setTaxAmount(valueOf(3));

        InvoiceCategory invoiceCategory = createInvoiceCategory();

        InvoiceSubCategory invoiceSubCategory = createInvoiceSubCategory(invoiceCategory);

        AccountingArticle accountingArticle = createAccountingArticle(invoiceSubCategory);

        QuoteArticleLine line = createQuoteArticleLine(accountingArticle, quoteLot, List.of(price));

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
        BillingAccount billableAccount = createBillingAccount("BA");

        CpqQuote quote = createQuote(billableAccount);

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

    @Test
    public void can_serialize_quote() throws Exception {
        BillingAccount billableAccount = createBillingAccount("BA");

        CpqQuote quote = createQuote(billableAccount);

        QuoteVersion quoteVersion = new QuoteVersion();
        quoteVersion.setQuote(quote);

        QuoteXmlDto quoteXmlDto = quoteMapper.map(quoteVersion);

        String xmlQuoteRepresentation = xmlQuoteFormatter.format(quoteXmlDto);
        System.out.println(xmlQuoteRepresentation);
    }

    @Test
    public void quote_xml_has_details() throws JAXBException {

        BillingAccount billingAccount = createBillingAccount("BA");

        CpqQuote quote = createQuote(billingAccount);

        QuoteLot quoteLot = createQuoteLot();

        QuotePrice recurring = createQuotePrice(PriceTypeEnum.RECURRING);
        QuotePrice oneShot = createQuotePrice(PriceTypeEnum.ONE_SHOT);

        InvoiceCategory invoiceCategory = createInvoiceCategory();

        InvoiceSubCategory invoiceSubCategory = createInvoiceSubCategory(invoiceCategory);

        AccountingArticle accountingArticle = createAccountingArticle(invoiceSubCategory);

        QuoteArticleLine line = createQuoteArticleLine(accountingArticle, quoteLot, List.of(recurring, oneShot));

        QuoteProduct quoteProduct = new QuoteProduct();
        quoteProduct.setQuoteArticleLines(List.of(line));

        QuoteOffer quoteOffer = new QuoteOffer();
        quoteOffer.setBillableAccount(billingAccount);
        quoteOffer.setQuoteLot(quoteLot);
        quoteOffer.setQuoteProduct(List.of(quoteProduct));

        QuoteVersion quoteVersion = new QuoteVersion();
        quoteVersion.setQuote(quote);
        quoteVersion.setQuoteOffers(List.of(quoteOffer));

        QuoteXmlDto quoteXmlDto = quoteMapper.map(quoteVersion);

        assertThat(quoteXmlDto.getDetails()).isNotNull();
        Quote quoteXml = quoteXmlDto.getDetails().getQuote();
        assertThat(quoteXml).isNotNull();
        assertThat(quoteXml.getQuoteNumber()).isEqualTo("10");
        assertThat(quoteXml.getQuoteDate()).isEqualTo(Date.valueOf(LocalDate.of(2020, 1, 2)));
        List<BillableAccount> billableAccounts = quoteXmlDto.getDetails().getQuote().getBillableAccounts();
        assertThat(billableAccounts).isNotEmpty();
        BillableAccount billableAccount = billableAccounts.get(0);
        assertThat(billableAccount.getBillingAccountCode()).isEqualTo("LINE_BA");
        assertThat(billableAccount.getQuoteLots()).isNotEmpty();
        org.meveo.api.dto.cpq.xml.QuoteLot quoteLotXml = billableAccount.getQuoteLots().get(0);
        assertThat(quoteLotXml.getCode()).isEqualTo("QL_CODE");
        assertThat(quoteLotXml.getDuration()).isEqualTo(11);
        assertThat(quoteLotXml.getName()).isEqualTo("LOT1");
        assertThat(quoteLotXml.getExecutionDate()).isEqualTo(Date.valueOf(LocalDate.of(2020, 1, 1)));
        Category category = quoteLotXml.getCategories().get(0);
        assertThat(category).isNotNull();
        assertThat(category.getCode()).isEqualTo("INV_CAT");
        assertThat(category.getLabel()).isEqualTo("Abonnement");
        assertThat(category.getSortIndex()).isNull();
        SubCategory subCategory = category.getSubCategories().get(0);
        assertThat(subCategory).isNotNull();
        assertThat(subCategory.getCode()).isEqualTo("INV_SUB_CAT");
        assertThat(subCategory.getLabel()).isEqualTo("Abonnement et services");
        assertThat(subCategory.getSortIndex()).isNull();
        assertThat(subCategory.getArticleLines()).isNotEmpty();
        ArticleLine articleLine = subCategory.getArticleLines().get(0);
        assertThat(articleLine.getCode()).isEqualTo("ACC_CODE");
        assertThat(articleLine.getLabel()).isEqualTo("ART_LABEL");
        assertThat(articleLine.getArticleCode()).isEqualTo("ACC_CODE");
        assertThat(articleLine.getArticleLabel()).isEqualTo("ART_LABEL");
        QuoteLine quoteLine = articleLine.getQuoteLines().get(0);
        assertThat(quoteLine.getQuantity()).isEqualTo(valueOf(10));
        assertThat(quoteLine.getPrices()).isNotEmpty();
        assertThat(quoteLine.getPrices().get(0).getPriceType()).isEqualTo(PriceTypeEnum.RECURRING);
        assertThat(quoteLine.getPrices().get(1).getPriceType()).isEqualTo(PriceTypeEnum.ONE_SHOT);

        String formattedQuote = xmlQuoteFormatter.format(quoteXmlDto);
        assertThat(formattedQuote).isEqualTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<quote>\n" +
                "    <header>\n" +
                "        <billingAccount id=\"1\" billingCycleCode=\"BC_CODE\" code=\"BA\" description=\"billing account description\" externalRef1=\"external ref1\" externalRef2=\"external ref2\" jobTitle=\"jobTitle\" registrationNo=\"123456\" vatNo=\"67890\">\n" +
                "            <name>\n" +
                "                <name>firstName lastName</name>\n" +
                "                <quality>Society</quality>\n" +
                "            </name>\n" +
                "            <address>\n" +
                "                <address1>address1</address1>\n" +
                "                <city>Paris</city>\n" +
                "                <country>Fr</country>\n" +
                "                <countryName>France</countryName>\n" +
                "            </address>\n" +
                "        </billingAccount>\n" +
                "    </header>\n" +
                "    <details>\n" +
                "        <quote>\n" +
                "            <billableAccounts billingAccountCode=\"LINE_BA\">\n" +
                "                <quoteLots>\n" +
                "                    <quoteLot code=\"QL_CODE\" duration=\"11\" name=\"LOT1\" executionDate=\"2020-01-01T00:00:00+01:00\">\n" +
                "                        <categories>\n" +
                "                            <category code=\"INV_CAT\" label=\"Abonnement\">\n" +
                "                                <subCategories>\n" +
                "                                    <subCategory code=\"INV_SUB_CAT\" label=\"Abonnement et services\">\n" +
                "                                        <articleLines>\n" +
                "                                            <articleLine code=\"ACC_CODE\" label=\"ART_LABEL\">\n" +
                "                                                <articleCode>ACC_CODE</articleCode>\n" +
                "                                                <articleLabel>ART_LABEL</articleLabel>\n" +
                "                                                <quoteLines>\n" +
                "                                                    <quoteLine>\n" +
                "                                                        <quantity>10</quantity>\n" +
                "                                                        <prices>\n" +
                "                                                            <price priceType=\"RECURRING\">\n" +
                "                                                                <amountWithtax>5</amountWithtax>\n" +
                "                                                                <unitPriceWithoutTax>10</unitPriceWithoutTax>\n" +
                "                                                                <amountWithoutTax>15</amountWithoutTax>\n" +
                "                                                                <taxAmount>3</taxAmount>\n" +
                "                                                                <taxRate>3</taxRate>\n" +
                "                                                                <priceOverCharged>false</priceOverCharged>\n" +
                "                                                                <currencyCode>EUR</currencyCode>\n" +
                "                                                            </price>\n" +
                "                                                            <price priceType=\"ONE_SHOT\">\n" +
                "                                                                <amountWithtax>5</amountWithtax>\n" +
                "                                                                <unitPriceWithoutTax>10</unitPriceWithoutTax>\n" +
                "                                                                <amountWithoutTax>15</amountWithoutTax>\n" +
                "                                                                <taxAmount>3</taxAmount>\n" +
                "                                                                <taxRate>3</taxRate>\n" +
                "                                                                <priceOverCharged>false</priceOverCharged>\n" +
                "                                                                <currencyCode>EUR</currencyCode>\n" +
                "                                                            </price>\n" +
                "                                                        </prices>\n" +
                "                                                    </quoteLine>\n" +
                "                                                </quoteLines>\n" +
                "                                            </articleLine>\n" +
                "                                        </articleLines>\n" +
                "                                    </subCategory>\n" +
                "                                </subCategories>\n" +
                "                            </category>\n" +
                "                        </categories>\n" +
                "                    </quoteLot>\n" +
                "                </quoteLots>\n" +
                "                <billingAccountPrices>\n" +
                "                    <price priceType=\"ONE_SHOT\">\n" +
                "                        <amountWithtax>5</amountWithtax>\n" +
                "                        <unitPriceWithoutTax>10</unitPriceWithoutTax>\n" +
                "                        <amountWithoutTax>15</amountWithoutTax>\n" +
                "                        <taxAmount>3</taxAmount>\n" +
                "                        <taxRate>3</taxRate>\n" +
                "                        <priceOverCharged>false</priceOverCharged>\n" +
                "                        <currencyCode>EUR</currencyCode>\n" +
                "                    </price>\n" +
                "                    <price priceType=\"RECURRING\">\n" +
                "                        <amountWithtax>5</amountWithtax>\n" +
                "                        <unitPriceWithoutTax>10</unitPriceWithoutTax>\n" +
                "                        <amountWithoutTax>15</amountWithoutTax>\n" +
                "                        <taxAmount>3</taxAmount>\n" +
                "                        <taxRate>3</taxRate>\n" +
                "                        <priceOverCharged>false</priceOverCharged>\n" +
                "                        <currencyCode>EUR</currencyCode>\n" +
                "                    </price>\n" +
                "                </billingAccountPrices>\n" +
                "            </billableAccounts>\n" +
                "            <quoteDate>2020-01-02T00:00:00+01:00</quoteDate>\n" +
                "            <quoteNumber>10</quoteNumber>\n" +
                "        </quote>\n" +
                "        <quotePrices>\n" +
                "            <price priceType=\"ONE_SHOT\">\n" +
                "                <amountWithtax>5</amountWithtax>\n" +
                "                <unitPriceWithoutTax>10</unitPriceWithoutTax>\n" +
                "                <amountWithoutTax>15</amountWithoutTax>\n" +
                "                <taxAmount>3</taxAmount>\n" +
                "                <taxRate>3</taxRate>\n" +
                "                <priceOverCharged>false</priceOverCharged>\n" +
                "                <currencyCode>EUR</currencyCode>\n" +
                "            </price>\n" +
                "            <price priceType=\"RECURRING\">\n" +
                "                <amountWithtax>5</amountWithtax>\n" +
                "                <unitPriceWithoutTax>10</unitPriceWithoutTax>\n" +
                "                <amountWithoutTax>15</amountWithoutTax>\n" +
                "                <taxAmount>3</taxAmount>\n" +
                "                <taxRate>3</taxRate>\n" +
                "                <priceOverCharged>false</priceOverCharged>\n" +
                "                <currencyCode>EUR</currencyCode>\n" +
                "            </price>\n" +
                "        </quotePrices>\n" +
                "    </details>\n" +
                "</quote>\n");
    }

    private QuotePrice createQuotePrice(PriceTypeEnum type) {
        QuotePrice price = new QuotePrice();
        price.setPriceTypeEnum(type);
        price.setUnitPriceWithoutTax(valueOf(10));
        price.setTaxRate(valueOf(3));
        price.setAmountWithTax(valueOf(5));
        price.setAmountWithoutTax(valueOf(15));
        price.setTaxAmount(valueOf(3));
        price.setCurrencyCode("EUR");
        price.setPriceOverCharged(false);
        return price;
    }

    private BillingAccount createBillingAccount(String code) {
        BillingCycle billingCycle = new BillingCycle();
        billingCycle.setCode("BC_CODE");
        BillingAccount billableAccount = new BillingAccount();
        billableAccount.setId(1L);
        billableAccount.setJobTitle("jobTitle");
        billableAccount.setCode(code);
        billableAccount.setDescription("billing account description");
        billableAccount.setExternalRef1("external ref1");
        billableAccount.setExternalRef2("external ref2");
        billableAccount.setBillingCycle(billingCycle);
        billableAccount.setRegistrationNo("123456");
        billableAccount.setVatNo("67890");
        TradingLanguage tradingLanguage = new TradingLanguage();
        Language language = new Language();
        language.setLanguageCode("Fr");
        tradingLanguage.setLanguage(language);
        billableAccount.setTradingLanguage(tradingLanguage);
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

    private CpqQuote createQuote(BillingAccount billableAccount) {
        CpqQuote quote = new CpqQuote();
        quote.setApplicantAccount(billableAccount);
        quote.setBillableAccount(null);
        quote.setQuoteNumber("10");
        quote.setSendDate(Date.valueOf(LocalDate.of(2020, 1, 2)));
        return quote;
    }

    private AccountingArticle createAccountingArticle(InvoiceSubCategory invoiceSubCategory) {
        AccountingArticle accountingArticle = new AccountingArticle();
        accountingArticle.setCode("ACC_CODE");
        accountingArticle.getDescriptionI18nNotNull().put("Fr", "ART_LABEL");
        accountingArticle.setInvoiceSubCategory(invoiceSubCategory);
        return accountingArticle;
    }

    private InvoiceSubCategory createInvoiceSubCategory(InvoiceCategory invoiceCategory) {
        InvoiceSubCategory invoiceSubCategory = new InvoiceSubCategory();
        invoiceSubCategory.setInvoiceCategory(invoiceCategory);
        invoiceSubCategory.setCode("INV_SUB_CAT");
        invoiceSubCategory.getDescriptionI18nNullSafe().put("Fr", "Abonnement et services");
        invoiceSubCategory.setSortIndex(null);
        return invoiceSubCategory;
    }

    private InvoiceCategory createInvoiceCategory() {
        //getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode()
        InvoiceCategory invoiceCategory = new InvoiceCategory();
        invoiceCategory.setCode("INV_CAT");
        invoiceCategory.getDescriptionI18nNullSafe().put("Fr", "Abonnement");
        invoiceCategory.setSortIndex(null);
        return invoiceCategory;
    }

    private QuoteLot createQuoteLot() {
        QuoteLot quoteLot = new QuoteLot();
        quoteLot.setCode("QL_CODE");
        quoteLot.setName("LOT1");
        quoteLot.setExecutionDate(Date.valueOf(LocalDate.of(2020, 1, 1)));
        quoteLot.setDuration(11);
        return quoteLot;
    }

    private QuoteArticleLine createQuoteArticleLine(AccountingArticle accountingArticle, QuoteLot quoteLot, List<QuotePrice> prices) {
        QuoteArticleLine line = new QuoteArticleLine();
        line.setQuotePrices(prices);
        line.setAccountingArticle(accountingArticle);
        line.setBillableAccount(createBillingAccount("LINE_BA"));
        line.setQuantity(valueOf(10));
        line.setQuoteLot(quoteLot);
        return line;
    }
}
