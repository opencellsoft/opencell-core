package org.meveo.service.cpq;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.cpq.CurrencyDetailDto;
import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.api.dto.cpq.TaxDTO;
import org.meveo.api.dto.cpq.TaxDetailDTO;
import org.meveo.api.dto.cpq.xml.BillableAccount;
import org.meveo.api.dto.cpq.xml.BillingAccount;
import org.meveo.api.dto.cpq.xml.Category;
import org.meveo.api.dto.cpq.xml.Contract;
import org.meveo.api.dto.cpq.xml.Customer;
import org.meveo.api.dto.cpq.xml.Details;
import org.meveo.api.dto.cpq.xml.PaymentMethod;
import org.meveo.api.dto.cpq.xml.Quote;
import org.meveo.api.dto.cpq.xml.QuoteLine;
import org.meveo.api.dto.cpq.xml.QuoteXMLHeader;
import org.meveo.api.dto.cpq.xml.QuoteXmlDto;
import org.meveo.api.dto.cpq.xml.Seller;
import org.meveo.api.dto.cpq.xml.SubCategory;
import org.meveo.common.UtilsDto;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.crm.Provider;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.order.OpenOrderService;
import org.meveo.util.ApplicationProvider;

@Stateless
public class QuoteMapper {

    private static final String DISCOUNT_ALLOWANCE_CODE = "95";

    @Inject
    public EntityToDtoConverter entityToDtoConverter;

   	@Inject
    private OpenOrderService openOrderService;

    @Inject
    private TradingCurrencyService currencyService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;
   	
    
    public QuoteXmlDto map(QuoteVersion quoteVersion, Map<String, TaxDTO> mapTaxIndexes, TaxDetailDTO taxDetail) {
    	CpqQuote quote = quoteVersion.getQuote();
        org.meveo.model.billing.BillingAccount bac=quote.getBillableAccount() == null ? quote.getApplicantAccount() : quote.getBillableAccount();
        
        PaymentMethod paymentMethod=new PaymentMethod(bac.getPaymentMethod(),entityToDtoConverter.getCustomFieldsDTO(bac.getPaymentMethod()));
        
        BillingAccount billingAccount = new BillingAccount(bac,paymentMethod,entityToDtoConverter.getCustomFieldsDTO(bac));
        Customer customer = new Customer(bac.getCustomerAccount().getCustomer(),
                bac.getCustomerAccount().getTradingCurrency(),
                bac.getCustomerAccount().getTradingLanguage());
        Seller seller = new Seller(quote.getSeller());

        org.meveo.model.cpq.contract.Contract contract = quoteVersion.getContract();
        Contract ctr=null;
        if(contract!=null) {
         ctr = new Contract(contract,entityToDtoConverter.getCustomFieldsDTO(contract));
        }
        Date startDate=null; Date endDate=null;Long duration=0L;
       if(quote.getValidity()!=null) {
         startDate =quote.getValidity().getFrom();
         endDate=quote.getValidity().getTo();
         duration = startDate.getTime()-endDate.getTime();
       }

       CurrencyDetailDto currencyDetailDto = new CurrencyDetailDto();
       if (bac.getTradingCurrency()!=null) {
           currencyDetailDto.setBaCode(bac.getTradingCurrency().getCurrencyCode());
           currencyDetailDto.setBaSymbol(bac.getTradingCurrency().getSymbol());
           currencyDetailDto.setBaRate(bac.getTradingCurrency().getCurrentRate());
       }

        if (appProvider != null && appProvider.getCurrency() != null) {
            currencyDetailDto.setCode(appProvider.getCurrency().getCurrencyCode());
            currencyDetailDto.setSymbol(appProvider.getCurrency().getSymbol());
            currencyDetailDto.setRate(BigDecimal.ONE); // Functional currency is 1, waiting to apply transactionalCurrency on Quote
        }
        QuoteXMLHeader header = new QuoteXMLHeader(billingAccount,ctr,quoteVersion,quote.getCode(),startDate,duration,
        		quote.getQuoteLotDuration(),quote.getCustomerRef(),quote.getRegisterNumber(),startDate,endDate,quoteVersion.getComment(),
                customer, seller, currencyDetailDto, taxDetail);

        Map<org.meveo.model.billing.BillingAccount, List<QuoteArticleLine>> linesByBillingAccount = quoteVersion.getQuoteArticleLines().stream()
                .collect(groupingBy(QuoteArticleLine::getBillableAccount));


        List<BillableAccount> billableAccounts = linesByBillingAccount.keySet()
                .stream()
                .map(ba -> mapToBillableAccount(ba, linesByBillingAccount.get(ba), mapTaxIndexes))
                .collect(Collectors.toList());

        List<QuotePrice> allQuotesPrice = quoteVersion.getQuoteArticleLines().stream().flatMap(e -> e.getQuotePrices().stream()).filter(e -> PriceLevelEnum.QUOTE.equals(e.getPriceLevelEnum())).collect(toList());
        String defaultConsumer = quoteVersion.getQuote().getUserAccount() != null ? quoteVersion.getQuote().getUserAccount().getCode() : null;
        Details details = new Details(new Quote(billableAccounts, quote.getQuoteNumber(), quote.getQuoteDate(),
                entityToDtoConverter.getCustomFieldsDTO(quoteVersion), defaultConsumer), aggregatePricesPerType(allQuotesPrice, mapTaxIndexes));

        return new QuoteXmlDto(header, details);
    }

    private org.meveo.api.dto.cpq.xml.BillableAccount mapToBillableAccount(org.meveo.model.billing.BillingAccount ba,
                                                                           List<QuoteArticleLine> lines,
                                                                           Map<String, TaxDTO> mapTaxIndexes){

        Map<QuoteLot, List<QuoteArticleLine>> linesByLot = lines.stream()
                .filter(line -> line.getQuoteLot() != null)
                .collect(groupingBy(line -> line.getQuoteLot()));

        List<QuoteArticleLine> linesWithoutLot = lines.stream()
                .filter(line -> line.getQuoteLot() == null)
                .collect(toList());

        linesByLot.put(new QuoteLot(), linesWithoutLot);

        List<org.meveo.api.dto.cpq.xml.QuoteLot> quoteLots = linesByLot.keySet().stream()
                .map(lot -> mapToLot(lot, linesByLot.get(lot), ba, mapTaxIndexes))
                .collect(Collectors.toList());

        List<QuotePrice> baPrices = lines.stream()
                                         .flatMap(line -> line.getQuotePrices().stream())
                                         .filter(e -> PriceLevelEnum.QUOTE.equals(e.getPriceLevelEnum()))
                                         .collect(toList());

        return new BillableAccount(ba.getCode(), quoteLots, aggregatePricesPerType(baPrices, mapTaxIndexes));

    }

    private org.meveo.api.dto.cpq.xml.QuoteLot mapToLot(QuoteLot lot, List<QuoteArticleLine> quoteArticleLines,
                                                        org.meveo.model.billing.BillingAccount ba,
                                                        Map<String, TaxDTO> mapTaxIndexes) {
        Map<InvoiceCategory, List<QuoteArticleLine>> linesByCategory = quoteArticleLines.stream()
                .collect(groupingBy(quoteArticleLine -> quoteArticleLine.getAccountingArticle().getInvoiceSubCategory().getInvoiceCategory()));
        List<Category> categories = linesByCategory.keySet().stream()
                .map(category -> mapToCategory(category, linesByCategory.get(category), ba, mapTaxIndexes))
                .collect(toList());
        return new org.meveo.api.dto.cpq.xml.QuoteLot(lot, categories);
    }

    private Category mapToCategory(InvoiceCategory category, List<QuoteArticleLine> quoteArticleLines,
                                   org.meveo.model.billing.BillingAccount ba,
                                   Map<String, TaxDTO> mapTaxIndexes) {
        Map<InvoiceSubCategory, List<QuoteArticleLine>> linesBySubCategory = quoteArticleLines.stream()
                .collect(groupingBy(line -> line.getAccountingArticle().getInvoiceSubCategory()));

        List<SubCategory> subCategories = linesBySubCategory.keySet().stream()
                .map(subCategory -> mapToSubCategory(subCategory, linesBySubCategory.get(subCategory), ba, mapTaxIndexes))
                .collect(toList());
        return new Category(category, subCategories, getTradingLanguage(ba));
    }

    private SubCategory mapToSubCategory(InvoiceSubCategory subCategory, List<QuoteArticleLine> quoteArticleLines,
                                         org.meveo.model.billing.BillingAccount ba,
                                         Map<String, TaxDTO> mapTaxIndexes) {
        Map<AccountingArticle, List<QuoteArticleLine>> linesByAccountingArticle = quoteArticleLines.stream()
                .collect(groupingBy(line -> line.getAccountingArticle()));
        List<org.meveo.api.dto.cpq.xml.AccountingArticle> articleLines = linesByAccountingArticle.keySet().stream()
                .map(accountingArticle -> mapToArticleLine(accountingArticle, linesByAccountingArticle.get(accountingArticle), ba, mapTaxIndexes))
                .collect(toList());
        List<org.meveo.api.dto.cpq.xml.AccountingArticle> articleLinesDiscounts = linesByAccountingArticle.keySet().stream()
                .map(accountingArticle -> mapToArticleLineDiscount(accountingArticle, linesByAccountingArticle.get(accountingArticle), ba, mapTaxIndexes))
                .collect(toList());
        return new SubCategory(subCategory, articleLines, articleLinesDiscounts, getTradingLanguage(ba));
    }

    private org.meveo.api.dto.cpq.xml.AccountingArticle mapToArticleLine(AccountingArticle accountingArticle,
                                                                         List<QuoteArticleLine> quoteArticleLines,
                                                                         org.meveo.model.billing.BillingAccount ba,
                                                                         Map<String, TaxDTO> mapTaxIndexes) {

        if (accountingArticle.getAllowanceCode() != null && DISCOUNT_ALLOWANCE_CODE.equals(accountingArticle.getAllowanceCode().getCode())) {
           return null;
        }

    	Optional<OpenOrder> openOrder = openOrderService.checkAvailableOpenOrderForArticle(ba, accountingArticle, new Date());
    	org.meveo.api.dto.cpq.xml.AccountingArticle accountingArticleDto = new  org.meveo.api.dto.cpq.xml.AccountingArticle(
    																												accountingArticle, 
    																												quoteArticleLines,
    																												getTradingLanguage(ba), 
    																												openOrder.map(OpenOrder::getOpenOrderNumber).orElse(null), 
    																												openOrder.map(OpenOrder::getExternalReference).orElse(null), 
    																												openOrder.map(OpenOrder::getActivationDate).orElse(null));

    	accountingArticleDto.setQuoteLines(quoteArticleLines.stream()
    			.map(line -> {
                    // build currency details
                    Map<String, TradingCurrency> currencies = new HashMap<>();
                    line.getQuotePrices().stream().filter(e -> PriceLevelEnum.QUOTE.equals(e.getPriceLevelEnum())).forEach(quotePrice -> {
                        if(StringUtils.isNotBlank(quotePrice.getCurrencyCode())){
                            currencies.put(quotePrice.getCurrencyCode(), currencyService.findByTradingCurrencyCode(quotePrice.getCurrencyCode()));
                        }
                    });
                    return new QuoteLine(line,mapToOffer(line.getQuoteProduct() != null?line.getQuoteProduct().getQuoteOffer():null, mapTaxIndexes), currencies, mapTaxIndexes);
                })
    			.collect(Collectors.toList()));
    	return accountingArticleDto; 
    }

    private org.meveo.api.dto.cpq.xml.AccountingArticle mapToArticleLineDiscount(AccountingArticle accountingArticle,
                                                                         List<QuoteArticleLine> quoteArticleLines,
                                                                         org.meveo.model.billing.BillingAccount ba,
                                                                         Map<String, TaxDTO> mapTaxIndexes) {
        if (accountingArticle.getAllowanceCode() != null && DISCOUNT_ALLOWANCE_CODE.equals(accountingArticle.getAllowanceCode().getCode())) {
            Optional<OpenOrder> openOrder = openOrderService.checkAvailableOpenOrderForArticle(ba, accountingArticle, new Date());
            org.meveo.api.dto.cpq.xml.AccountingArticle accountingArticleDto = new org.meveo.api.dto.cpq.xml.AccountingArticle(
                    accountingArticle,
                    quoteArticleLines,
                    getTradingLanguage(ba),
                    openOrder.map(OpenOrder::getOpenOrderNumber).orElse(null),
                    openOrder.map(OpenOrder::getExternalReference).orElse(null),
                    openOrder.map(OpenOrder::getActivationDate).orElse(null));


            accountingArticleDto.setQuoteLines(quoteArticleLines.stream()
                    .map(line -> {
                        // build currency details
                        Map<String, TradingCurrency> currencies = new HashMap<>();
                        line.getQuotePrices().forEach(quotePrice -> {
                            if (StringUtils.isNotBlank(quotePrice.getCurrencyCode())) {
                                currencies.put(quotePrice.getCurrencyCode(), currencyService.findByTradingCurrencyCode(quotePrice.getCurrencyCode()));
                            }
                        });
                        return new QuoteLine(line, mapToOffer(line.getQuoteProduct() != null ? line.getQuoteProduct().getQuoteOffer() : null, mapTaxIndexes), currencies, mapTaxIndexes);
                    })
                    .collect(Collectors.toList()));
            return accountingArticleDto;
        }
        return null;
    }

    private org.meveo.api.dto.cpq.xml.Offer mapToOffer(QuoteOffer quoteOffer, Map<String, TaxDTO> mapTaxIndexes) {
    	if(quoteOffer==null) {
    		return null;
    	}
    	org.meveo.api.dto.cpq.xml.Offer quoteOfferDto = new  org.meveo.api.dto.cpq.xml.Offer(quoteOffer,entityToDtoConverter.getCustomFieldsDTO(quoteOffer));

    	quoteOfferDto.setProducts(quoteOffer.getQuoteProduct().stream()
    			.map(product ->  mapToProduct(product, mapTaxIndexes))
    			.collect(Collectors.toList()));
    	
    	quoteOfferDto.setAttributes(quoteOffer.getQuoteAttributes().stream()
    			.map(product ->  mapToAttribute(product))
    			.collect(Collectors.toList()));
    	
    	return quoteOfferDto; 
    }
    
    
    private org.meveo.api.dto.cpq.xml.Product mapToProduct(QuoteProduct quoteProduct, Map<String, TaxDTO> mapTaxIndexes) {

        List<QuotePrice> price = quoteProduct.getQuoteArticleLines()
                                             .stream()
                                             .flatMap(line -> line.getQuotePrices().stream())
                                             .filter(e -> PriceLevelEnum.QUOTE.equals(e.getPriceLevelEnum()))
                                             .collect(toList());
        
    	org.meveo.api.dto.cpq.xml.Product quoteProductDto = new  org.meveo.api.dto.cpq.xml.Product(quoteProduct,entityToDtoConverter.getCustomFieldsDTO(quoteProduct), aggregatePricesPerType(price, mapTaxIndexes));

    	quoteProductDto.setAttributes(quoteProduct.getQuoteAttributes().stream()
    			.map(product ->  mapToAttribute(product))
    			.collect(Collectors.toList()));
    	return quoteProductDto;  
    }
    
    private org.meveo.api.dto.cpq.xml.Attribute mapToAttribute(QuoteAttribute quoteAttribute) {
    	org.meveo.api.dto.cpq.xml.Attribute quoteAttributeDto = new  org.meveo.api.dto.cpq.xml.Attribute(quoteAttribute,entityToDtoConverter.getCustomFieldsDTO(quoteAttribute)); 
    	return quoteAttributeDto; 
    }


    private String getTradingLanguage(org.meveo.model.billing.BillingAccount ba) {
        return ba.getTradingLanguage().getLanguageCode();
    }

    private List<PriceDTO> aggregatePricesPerType(List<QuotePrice> baPrices, Map<String, TaxDTO> mapTaxIndexes) {
        Map<PriceTypeEnum, List<QuotePrice>> pricesPerType = baPrices.stream()
                .collect(Collectors.groupingBy(QuotePrice::getPriceTypeEnum));

        return pricesPerType
                .keySet()
                .stream()
                .map(key -> UtilsDto.reducePrices(key, pricesPerType, PriceLevelEnum.QUOTE, null, null))
                .filter(Optional::isPresent)
                .map(price -> new PriceDTO(price.get(), currencyService.findByTradingCurrencyCode(price.get().getCurrencyCode()), mapTaxIndexes)).collect(Collectors.toList());
    }

}
