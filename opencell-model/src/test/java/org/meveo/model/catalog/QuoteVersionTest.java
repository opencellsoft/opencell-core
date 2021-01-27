package org.meveo.model.catalog;

import org.junit.Test;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.Quote;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.valueOf;

public class QuoteVersionTest {

    @Test
    public void createQuoteVersion() {
        BillingAccount billingAccount = createBillingAccount();

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
        BillingAccount billingAccount = createBillingAccount();

        CpqQuote quote = new CpqQuote();
        quote.setApplicantAccount(billingAccount);
        quote.setBillableAccount(null);

        QuoteVersion quoteVersion = new QuoteVersion();
        quoteVersion.setQuote(quote);

        //new QuoteMapper().map(quoteVersion);
    }

    private BillingAccount createBillingAccount() {
        BillingAccount billableAccount = new BillingAccount();
        billableAccount.setCode("BA");
        return billableAccount;
    }
}
