package org.meveo.service.quote.script;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.cpq.PriceDTO;
import org.meveo.api.dto.cpq.xml.BillableAccount;
import org.meveo.api.dto.cpq.xml.BillingAccount;
import org.meveo.api.dto.cpq.xml.Category;
import org.meveo.api.dto.cpq.xml.Contract;
import org.meveo.api.dto.cpq.xml.Details;
import org.meveo.api.dto.cpq.xml.Header;
import org.meveo.api.dto.cpq.xml.PaymentMethod;
import org.meveo.api.dto.cpq.xml.Product;
import org.meveo.api.dto.cpq.xml.Quote;
import org.meveo.api.dto.cpq.xml.QuoteLine;
import org.meveo.api.dto.cpq.xml.QuoteXmlDto;
import org.meveo.api.dto.cpq.xml.SubCategory;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.PriceLevelEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;
import org.meveo.model.quote.QuoteArticleLine;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.api.EntityToDtoConverter;
import org.meveo.service.cpq.CpqQuoteService;
import org.meveo.service.cpq.QuoteMapper;
import org.meveo.service.cpq.XmlQuoteFormatter;
import org.meveo.service.script.Script;
import org.meveo.service.script.module.ModuleScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteToXmlScript extends ModuleScript {

    private QuoteMapper quoteMapper = (QuoteMapper) getServiceInterface(QuoteMapper.class.getSimpleName());
    private XmlQuoteFormatter quoteFormatter = (XmlQuoteFormatter) getServiceInterface(XmlQuoteFormatter.class.getSimpleName());
    private CpqQuoteService cpqQuoteService = (CpqQuoteService) getServiceInterface(CpqQuoteService.class.getSimpleName());
    protected ParamBeanFactory paramBeanFactory = (ParamBeanFactory) getServiceInterface(ParamBeanFactory.class.getSimpleName());
    private EntityToDtoConverter entityToDtoConverter = (EntityToDtoConverter) getServiceInterface(EntityToDtoConverter.class.getSimpleName());
    private TradingCurrencyService currencyService = (TradingCurrencyService) getServiceInterface(TradingCurrencyService.class.getSimpleName());

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        QuoteVersion quoteVersion= (QuoteVersion) methodContext.get("quoteVersion");
        if (quoteVersion == null) {
            throw new BusinessException("No quote version is found");
        }
        byte[] xmlContent = null;

        String quoteXml = null;
        try {
            quoteXml = quoteFormatter.format(map(quoteVersion));
        } catch (JAXBException e) {
            log.error("Can not format QuoteXmlDto object");
            return;
        }
        String meveoDir = paramBeanFactory.getChrootDir() + File.separator;

        File quoteXmlDir = new File(meveoDir + "quotes" + File.separator + "xml");
        if (!quoteXmlDir.exists()) {
            quoteXmlDir.mkdirs();
        }
        xmlContent = quoteXml.getBytes();
        String fileName = cpqQuoteService.generateFileName(quoteVersion);
        quoteVersion.setXmlFilename(fileName);
        String xmlFilename = quoteXmlDir.getAbsolutePath() + File.separator + fileName + ".xml";
        try {
            Files.write(Paths.get(xmlFilename), quoteXml.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error("Can not wrie quote xml to the input/output media");
        }

        methodContext.put(Script.RESULT_VALUE, xmlContent);
    }

    public QuoteXmlDto map(QuoteVersion quoteVersion) {
    	
    	 

        CpqQuote quote = quoteVersion.getQuote();
        org.meveo.model.billing.BillingAccount bac=quote.getBillableAccount() == null ? quote.getApplicantAccount() : quote.getBillableAccount();
        
        PaymentMethod paymentMethod=new PaymentMethod(bac.getPaymentMethod(),entityToDtoConverter.getCustomFieldsDTO(bac.getPaymentMethod()));
        
        BillingAccount billingAccount = new BillingAccount(bac,paymentMethod,entityToDtoConverter.getCustomFieldsDTO(bac));
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
        
        Header header = new Header(billingAccount,ctr,quoteVersion.getQuoteVersion(),quote.getCode(),startDate,duration,
        		quote.getQuoteLotDuration(),quote.getCustomerRef(),quote.getRegisterNumber(),startDate,endDate,quoteVersion.getComment());

        Map<org.meveo.model.billing.BillingAccount, List<QuoteArticleLine>> linesByBillingAccount = getAllOffersQuoteLineStream(quoteVersion)
                .collect(groupingBy(QuoteArticleLine::getBillableAccount));


        List<BillableAccount> billableAccounts = linesByBillingAccount.keySet()
                .stream()
                .map(ba -> mapToBillableAccount(ba, linesByBillingAccount.get(ba)))
                .collect(Collectors.toList());

        List<QuotePrice> allQuotesPrice = getAllOffersQuoteLineStream(quoteVersion).map(p -> p.getQuotePrices().stream()).flatMap(identity()).collect(toList());
        Details details = new Details(new Quote(billableAccounts, quote.getQuoteNumber(), quote.getQuoteDate(),entityToDtoConverter.getCustomFieldsDTO(quoteVersion)), aggregatePricesPerType(allQuotesPrice));

        return new QuoteXmlDto(header, details);
    }

    protected Stream<QuoteArticleLine> getAllOffersQuoteLineStream(QuoteVersion quoteVersion) {
    	List<QuoteArticleLine> QuoteArticleLines=quoteVersion.getQuoteArticleLines();
    	log.info("-----------------getAllOffersQuoteLineStream---------- size={}",QuoteArticleLines.size());
    	return QuoteArticleLines.stream();
//        return quoteVersion.getQuoteOffers()
//                    .stream()
//                    .map(offer -> offer.getQuoteProduct().stream())
//                    .flatMap(identity())
//                    .map(quoteProduct -> quoteProduct.getQuoteArticleLines().stream())
//                    .flatMap(identity());
    }

    protected org.meveo.api.dto.cpq.xml.BillableAccount mapToBillableAccount(org.meveo.model.billing.BillingAccount ba, List<QuoteArticleLine> lines){

        Map<QuoteLot, List<QuoteArticleLine>> linesByLot = lines.stream()
                .filter(line -> line.getQuoteLot() != null)
                .collect(groupingBy(line -> line.getQuoteLot()));

        List<QuoteArticleLine> linesWithoutLot = lines.stream()
                .filter(line -> line.getQuoteLot() == null)
                .collect(toList());

        linesByLot.put(new QuoteLot(), linesWithoutLot);

        List<org.meveo.api.dto.cpq.xml.QuoteLot> quoteLots = linesByLot.keySet().stream()
                .map(lot -> mapToLot(lot, linesByLot.get(lot), ba))
                .collect(Collectors.toList());

        List<QuotePrice> baPrices = lines.stream()
                .map(line -> line.getQuotePrices().stream())
                .flatMap(identity())
                .collect(toList());

        return new BillableAccount(ba.getCode(), quoteLots, aggregatePricesPerType(baPrices));

    }

    protected org.meveo.api.dto.cpq.xml.QuoteLot mapToLot(QuoteLot lot, List<QuoteArticleLine> quoteArticleLines, org.meveo.model.billing.BillingAccount ba) {
        Map<InvoiceCategory, List<QuoteArticleLine>> linesByCategory = quoteArticleLines.stream()
                .collect(groupingBy(quoteArticleLine -> quoteArticleLine.getAccountingArticle().getInvoiceSubCategory().getInvoiceCategory()));
        List<Category> categories = linesByCategory.keySet().stream()
                .map(category -> mapToCategory(category, linesByCategory.get(category), ba))
                .collect(toList());
        return new org.meveo.api.dto.cpq.xml.QuoteLot(lot, categories);
    }

    protected Category mapToCategory(InvoiceCategory category, List<QuoteArticleLine> quoteArticleLines, org.meveo.model.billing.BillingAccount ba) {
        Map<InvoiceSubCategory, List<QuoteArticleLine>> linesBySubCategory = quoteArticleLines.stream()
                .collect(groupingBy(line -> line.getAccountingArticle().getInvoiceSubCategory()));

        List<SubCategory> subCategories = linesBySubCategory.keySet().stream()
                .map(subCategory -> mapToSubCategory(subCategory, linesBySubCategory.get(subCategory), ba))
                .collect(toList());
        return new Category(category, subCategories, getTradingLanguage(ba));
    }

    protected SubCategory mapToSubCategory(InvoiceSubCategory subCategory, List<QuoteArticleLine> quoteArticleLines, org.meveo.model.billing.BillingAccount ba) {
        Map<AccountingArticle, List<QuoteArticleLine>> linesByAccountingArticle = quoteArticleLines.stream()
                .collect(groupingBy(line -> line.getAccountingArticle()));
        List<org.meveo.api.dto.cpq.xml.AccountingArticle> articleLines = linesByAccountingArticle.keySet().stream()
                .map(accountingArticle -> mapToArticleLine(accountingArticle, linesByAccountingArticle.get(accountingArticle), ba))
                .collect(toList());
        return new SubCategory(subCategory, articleLines, getTradingLanguage(ba));
    }

    protected org.meveo.api.dto.cpq.xml.AccountingArticle mapToArticleLine(AccountingArticle accountingArticle, List<QuoteArticleLine> quoteArticleLines, org.meveo.model.billing.BillingAccount ba) {
    	org.meveo.api.dto.cpq.xml.AccountingArticle accountingArticleDto = new  org.meveo.api.dto.cpq.xml.AccountingArticle(accountingArticle, quoteArticleLines, getTradingLanguage(ba));

    	accountingArticleDto.setQuoteLines(quoteArticleLines.stream().filter(line -> line.getQuoteProduct() != null)
    			.map(line -> {
                    // build currency details
                    Map<String, TradingCurrency> currencies = new HashMap<>();
                    line.getQuotePrices().forEach(quotePrice -> {
                        if(StringUtils.isNotBlank(quotePrice.getCurrencyCode())){
                            currencies.put(quotePrice.getCurrencyCode(), currencyService.findByTradingCurrencyCode(quotePrice.getCurrencyCode()));
                        }
                    });
                    return new QuoteLine(line,mapToOffer(line.getQuoteProduct()), currencies);
                })
    			.collect(Collectors.toList()));
    	return accountingArticleDto; 
    }
    
    protected org.meveo.api.dto.cpq.xml.Offer mapToOffer(QuoteProduct quoteProduct) {
        if(quoteProduct==null) {
            return null;
        }
        org.meveo.api.dto.cpq.xml.Offer quoteOfferDto = new  org.meveo.api.dto.cpq.xml.Offer(quoteProduct.getQuoteOffer(),entityToDtoConverter.getCustomFieldsDTO(quoteProduct.getQuoteOffer()));
        List<Product> products=new ArrayList<Product>();
        products.add(mapToProduct(quoteProduct));
        quoteOfferDto.setProducts(products);
        
        quoteOfferDto.setAttributes(quoteProduct.getQuoteOffer().getQuoteAttributes().stream()
                .map(product ->  mapToAttribute(product))
                .collect(Collectors.toList()));
        
        return quoteOfferDto;
    }
    
    
    protected org.meveo.api.dto.cpq.xml.Product mapToProduct(QuoteProduct quoteProduct) {

        List<QuotePrice> prices = quoteProduct.getQuoteArticleLines().stream()
                .map(line -> line.getQuotePrices().stream())
                .flatMap(identity())
                .collect(toList());
        
    	org.meveo.api.dto.cpq.xml.Product quoteProductDto = new  org.meveo.api.dto.cpq.xml.Product(quoteProduct,entityToDtoConverter.getCustomFieldsDTO(quoteProduct), aggregatePricesPerType(prices));

    	quoteProductDto.setAttributes(quoteProduct.getQuoteAttributes().stream()
    			.map(product ->  mapToAttribute(product))
    			.collect(Collectors.toList()));
    	return quoteProductDto;  
    }
    
    protected org.meveo.api.dto.cpq.xml.Attribute mapToAttribute(QuoteAttribute quoteAttribute) {
    	org.meveo.api.dto.cpq.xml.Attribute quoteAttributeDto = new  org.meveo.api.dto.cpq.xml.Attribute(quoteAttribute,entityToDtoConverter.getCustomFieldsDTO(quoteAttribute)); 
    	return quoteAttributeDto; 
    }


    protected String getTradingLanguage(org.meveo.model.billing.BillingAccount ba) {
        return ba.getTradingLanguage().getLanguageCode();
    }

    protected List<PriceDTO> aggregatePricesPerType(List<QuotePrice> baPrices) {
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

    protected Optional<QuotePrice> reducePrices(PriceTypeEnum key, Map<PriceTypeEnum, List<QuotePrice>> pricesPerType) {
        return pricesPerType.get(key).stream().reduce((a, b) -> {
            QuotePrice quotePrice = new QuotePrice();
            quotePrice.setPriceTypeEnum(key);
            quotePrice.setPriceLevelEnum(PriceLevelEnum.QUOTE);
            quotePrice.setTaxAmount(a.getTaxAmount().add(b.getTaxAmount()));
            quotePrice.setAmountWithTax(a.getAmountWithTax().add(b.getAmountWithTax()));
            quotePrice.setAmountWithoutTax(a.getAmountWithoutTax().add(b.getAmountWithoutTax()));
            quotePrice.setUnitPriceWithoutTax(a.getUnitPriceWithoutTax());
            quotePrice.setTaxRate(a.getTaxRate());
            quotePrice.setTax(a.getTax());
            return quotePrice;
        });
    }
}
