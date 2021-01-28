package org.meveo.service.cpq;

import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.api.dto.cpq.xml.ArticleLine;
import org.meveo.api.dto.cpq.xml.BillableAccount;
import org.meveo.api.dto.cpq.xml.BillingAccount;
import org.meveo.api.dto.cpq.xml.Category;
import org.meveo.api.dto.cpq.xml.Details;
import org.meveo.api.dto.cpq.xml.Header;
import org.meveo.api.dto.cpq.xml.Quote;
import org.meveo.api.dto.cpq.xml.QuoteXmlDto;
import org.meveo.api.dto.cpq.xml.SubCategory;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteVersion;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class QuoteMapper {
    public QuoteXmlDto map(QuoteVersion quoteVersion) {

        CpqQuote quote = quoteVersion.getQuote();
        BillingAccount billingAccount = new BillingAccount(quote.getBillableAccount() == null ? quote.getApplicantAccount() : quote.getBillableAccount());
        Header header = new Header(billingAccount);

        Map<org.meveo.model.billing.BillingAccount, List<QuoteArticleLine>> linesByBillingAccount = getAllOffersQuoteLineStream(quoteVersion)
                .collect(groupingBy(QuoteArticleLine::getBillableAccount));


        List<BillableAccount> billableAccounts = linesByBillingAccount.keySet()
                .stream()
                .map(ba -> mapToBillableAccount(ba, linesByBillingAccount.get(ba)))
                .collect(Collectors.toList());

        List<QuotePrice> allQuotesPrice = getAllOffersQuoteLineStream(quoteVersion).map(p -> p.getQuotePrices().stream()).flatMap(identity()).collect(toList());
        Details details = new Details(new Quote(billableAccounts, quote.getQuoteNumber(), quote.getSendDate()), aggregatePricesPerType(allQuotesPrice));

        return new QuoteXmlDto(header, details);
    }

    private Stream<QuoteArticleLine> getAllOffersQuoteLineStream(QuoteVersion quoteVersion) {
        return quoteVersion.getQuoteOffers()
                    .stream()
                    .map(offer -> offer.getQuoteProduct().stream())
                    .flatMap(identity())
                    .map(quoteProduct -> quoteProduct.getQuoteArticleLines().stream())
                    .flatMap(identity());
    }

    private org.meveo.api.dto.cpq.xml.BillableAccount mapToBillableAccount(org.meveo.model.billing.BillingAccount ba, List<QuoteArticleLine> lines){

        Map<QuoteLot, List<QuoteArticleLine>> linesByLot = lines.stream()
                .collect(groupingBy(line -> line.getQuoteLot()));

        List<org.meveo.api.dto.cpq.xml.QuoteLot> quoteLots = linesByLot.keySet().stream()
                .map(lot -> mapToLot(lot, linesByLot.get(lot), ba))
                .collect(Collectors.toList());

        List<QuotePrice> baPrices = lines.stream()
                .map(line -> line.getQuotePrices().stream())
                .flatMap(identity())
                .collect(toList());

        return new BillableAccount(ba.getCode(), quoteLots, aggregatePricesPerType(baPrices));

    }

    private org.meveo.api.dto.cpq.xml.QuoteLot mapToLot(QuoteLot lot, List<QuoteArticleLine> quoteArticleLines, org.meveo.model.billing.BillingAccount ba) {
        Map<InvoiceCategory, List<QuoteArticleLine>> linesByCategory = quoteArticleLines.stream()
                .collect(groupingBy(quoteArticleLine -> quoteArticleLine.getAccountingArticle().getInvoiceSubCategory().getInvoiceCategory()));
        List<Category> categories = linesByCategory.keySet().stream()
                .map(category -> mapToCategory(category, linesByCategory.get(category), ba))
                .collect(toList());
        return new org.meveo.api.dto.cpq.xml.QuoteLot(lot, categories);
    }

    private Category mapToCategory(InvoiceCategory category, List<QuoteArticleLine> quoteArticleLines, org.meveo.model.billing.BillingAccount ba) {
        Map<InvoiceSubCategory, List<QuoteArticleLine>> linesBySubCategory = quoteArticleLines.stream()
                .collect(groupingBy(line -> line.getAccountingArticle().getInvoiceSubCategory()));

        List<SubCategory> subCategories = linesBySubCategory.keySet().stream()
                .map(subCategory -> mapToSubCategory(subCategory, linesBySubCategory.get(subCategory), ba))
                .collect(toList());
        return new Category(category, subCategories, getTradingLanguage(ba));
    }

    private SubCategory mapToSubCategory(InvoiceSubCategory subCategory, List<QuoteArticleLine> quoteArticleLines, org.meveo.model.billing.BillingAccount ba) {
        Map<AccountingArticle, List<QuoteArticleLine>> linesByArticleLine = quoteArticleLines.stream()
                .collect(groupingBy(line -> line.getAccountingArticle()));
        List<ArticleLine> articleLines = linesByArticleLine.keySet().stream()
                .map(accountingArticle -> mapToArticleLine(accountingArticle, linesByArticleLine.get(accountingArticle), ba))
                .collect(toList());
        return new SubCategory(subCategory, articleLines, getTradingLanguage(ba));
    }

    private ArticleLine mapToArticleLine(AccountingArticle accountingArticle, List<QuoteArticleLine> quoteArticleLines, org.meveo.model.billing.BillingAccount ba) {
        return new ArticleLine(accountingArticle, quoteArticleLines, getTradingLanguage(ba));
    }


    private String getTradingLanguage(org.meveo.model.billing.BillingAccount ba) {
        return ba.getTradingLanguage().getLanguageCode();
    }

    private List<PriceDTO> aggregatePricesPerType(List<QuotePrice> baPrices) {
        Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = baPrices.stream()
                .collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

        return pricesPerType
                .keySet()
                .stream()
                .map(key -> reducePrices(key, pricesPerType))
                .filter(Optional::isPresent)
                .map(price -> new PriceDTO(price.get()))
                .collect(Collectors.toList());
    }

    private Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType) {
        return pricesPerType.get(key).stream().reduce((a, b) -> {
            QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(key);
            quotePrice.setPriceLevelEnum(PriceLevelEnum.QUOTE);
            quotePrice.setTaxAmount(a.getTaxAmount().add(b.getTaxAmount()));
            quotePrice.setAmountWithTax(a.getAmountWithTax().add(b.getAmountWithTax()));
            quotePrice.setAmountWithoutTax(a.getAmountWithoutTax().add(b.getAmountWithoutTax()));
            quotePrice.setUnitPriceWithoutTax(a.getUnitPriceWithoutTax().add(b.getUnitPriceWithoutTax()));
            quotePrice.setTaxRate(a.getTaxRate().add(b.getTaxRate()));
            return quotePrice;
        });
    }

}
