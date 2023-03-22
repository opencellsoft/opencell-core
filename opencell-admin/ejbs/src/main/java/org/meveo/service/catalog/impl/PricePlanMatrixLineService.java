package org.meveo.service.catalog.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.api.dto.catalog.ConvertedPricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixValueDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ConvertedPricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeCategoryEnum;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.ProviderService;

import com.paypal.api.payments.Currency;

@Stateless
public class PricePlanMatrixLineService extends PersistenceService<PricePlanMatrixLine> {

    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;
    
    @Inject
    private ProviderService providerService;
    
    @Inject
    private ConvertedPricePlanMatrixLineService convertedPricePlanMatrixLineService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    public List<PricePlanMatrixLine> findByPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
         return getEntityManager().createNamedQuery("PricePlanMatrixLine.findByPricePlanMatrixVersion", entityClass)
                .setParameter("pricePlanMatrixVersionId", pricePlanMatrixVersion.getId())
                .getResultList();
    }
    
    public List<PricePlanMatrixLine> findByPricePlanMatrixVersionIds(List<Long> ppmvIds) {
        return getEntityManager().createNamedQuery("PricePlanMatrixLine.findByPricePlanMatrixVersionIds", entityClass)
                .setParameter("ppmvIds", ppmvIds)
                .getResultList();
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PricePlanMatrixLineDto createPricePlanMatrixLine(PricePlanMatrixLineDto dtoData) {
        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(dtoData);
        
        if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())) {
            log.warn("The status of the price plan matrix code={} and current version={}, is PUBLISHED, it can not be updated", pricePlanMatrixVersion.getPricePlanMatrix().getCode(),pricePlanMatrixVersion.getCurrentVersion());
            throw new MeveoApiException(String.format("status of the price plan matrix version id=%d is %s, it can not be updated",pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        if(dtoData.getPriceWithoutTax() != null && dtoData.getValue() != null){
            log.warn("Property priceWithoutTax is deprecated, please use only property value");
            throw new MeveoApiException("Property priceWithoutTax is deprecated, please use only property value");
        }
        if(dtoData.getPriceWithoutTax() == null && dtoData.getValue() == null){
            log.warn("Property value in lines should not be null");
            throw new MeveoApiException("Property value in lines should not be null");
        }

        if(dtoData.getPriceEL() != null && dtoData.getValueEL() != null){
            log.warn("Property “priceEl” is deprecated, please use only property “valueEl");
            throw new MeveoApiException("Property “priceEl” is deprecated, please use only property “valueEl”");
        }

        PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
        pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        pricePlanMatrixLine.setValueEL(dtoData.getValueEL() != null ? dtoData.getValueEL() : dtoData.getPriceEL());
        pricePlanMatrixLine.setPriority(dtoData.getPriority());
        pricePlanMatrixLine.setDescription(dtoData.getDescription());
        pricePlanMatrixLine.setPricePlanMatrixValues(getPricePlanMatrixValues(dtoData, pricePlanMatrixLine));
        BigDecimal value = dtoData.getValue() != null? dtoData.getValue():dtoData.getPriceWithoutTax();
        pricePlanMatrixLine.setValue(value);

        super.create(pricePlanMatrixLine);
        return new PricePlanMatrixLineDto(pricePlanMatrixLine);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<PricePlanMatrixLineDto> createOrUpdateLines(List<PricePlanMatrixLineDto> lines) {
        return lines.stream()
                .map(l -> l.getPpmLineId() != null ? updatePricePlanMatrixLine(l) : createPricePlanMatrixLine(l))
                .collect(Collectors.toList());
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PricePlanMatrixLineDto updatePricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto) {
        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(pricePlanMatrixLineDto);
        
        if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())) {
            log.warn("The status of the price plan matrix code={} and current version={}, is PUBLISHED, it can not be updated", pricePlanMatrixVersion.getPricePlanMatrix().getCode(),pricePlanMatrixVersion.getCurrentVersion());
            throw new MeveoApiException(String.format("status of the price plan matrix version id=%d is %s, it can not be updated",pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        if(pricePlanMatrixLineDto.getPriceWithoutTax() != null && pricePlanMatrixLineDto.getValue() != null){
            log.warn("Property priceWithoutTax is deprecated, please use only property value");
            throw new MeveoApiException("Property priceWithoutTax is deprecated, please use only property value");
        }
        if(pricePlanMatrixLineDto.getPriceWithoutTax() == null && pricePlanMatrixLineDto.getValue() == null){
            log.warn("Property value in lines should not be null");
            throw new MeveoApiException("Property value in lines should not be null");
        }
        PricePlanMatrixLine pricePlanMatrixLine = findById(pricePlanMatrixLineDto.getPpmLineId());
        if (pricePlanMatrixLine == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, pricePlanMatrixLineDto.getPricePlanMatrixCode(), "pricePlanMatrixVersion.pricePlanMatrixCode", "" + pricePlanMatrixLineDto.getPricePlanMatrixVersion(), "pricePlanMatrixVersion.currentVersion");
        }

        pricePlanMatrixLine.setPriceWithoutTax(pricePlanMatrixLineDto.getPriceWithoutTax());
        pricePlanMatrixLine.setPriority(pricePlanMatrixLineDto.getPriority());
        Set<PricePlanMatrixValue> pricePlanMatrixValues = getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
        pricePlanMatrixLine.getPricePlanMatrixValues().clear();
        pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);
        pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        pricePlanMatrixLine.setDescription(pricePlanMatrixLineDto.getDescription());
        BigDecimal value = pricePlanMatrixLineDto.getValue() != null? pricePlanMatrixLineDto.getValue():pricePlanMatrixLineDto.getPriceWithoutTax();
        pricePlanMatrixLine.setValue(value);
        PricePlanMatrixLine update = super.update(pricePlanMatrixLine);
        return new PricePlanMatrixLineDto(update);
    }

    private PricePlanMatrixVersion getPricePlanMatrixVersion(PricePlanMatrixLineDto dtoData) {
        return getPricePlanMatrixVersion(dtoData.getPricePlanMatrixCode(), dtoData.getPricePlanMatrixVersion());
    }
    
    public PricePlanMatrixVersion getPricePlanMatrixVersion( String pricePlanMatrixCode,int version) {
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, version);
        if (pricePlanMatrixVersion == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + version, "currentVersion");
        }
        return pricePlanMatrixVersion;
    }

    public Set<PricePlanMatrixValue> getPricePlanMatrixValues(PricePlanMatrixLineDto dtoData, PricePlanMatrixLine pricePlanMatrixLine) {
    	
        return dtoData.getPricePlanMatrixValues()
                .stream()
                .map(value -> {
                    PricePlanMatrixValue pricePlanMatrixValue;
                    if (value.getPpmValueId() != null) {
                        pricePlanMatrixValue = pricePlanMatrixValueService.findById(value.getPpmValueId());
                        if (pricePlanMatrixValue == null)
                            throw new EntityDoesNotExistsException(PricePlanMatrixValue.class, value.getPpmValueId());
                    } else {
                        pricePlanMatrixValue = new PricePlanMatrixValue();
                    }
                    
                    PricePlanMatrixColumn pricePlanMatrixColumn = null;
                    var columnSet = pricePlanMatrixLine.getPricePlanMatrixVersion().getColumns();
                    if (!columnSet.isEmpty()) {
                        pricePlanMatrixColumn = columnSet.stream().filter(c -> c.getCode().equals(value.getPpmColumnCode())).findAny().orElseThrow();
                    }
                    else {
                        var columnList = pricePlanMatrixColumnService.findByCodeAndPricePlanMatrixVersion(value.getPpmColumnCode(), pricePlanMatrixLine.getPricePlanMatrixVersion());
                        if (columnList.isEmpty()) {
                            throw new EntityDoesNotExistsException(PricePlanMatrixColumn.class, value.getPpmColumnCode());
                        }
                        pricePlanMatrixColumn = columnList.get(0);
                    }
                    pricePlanMatrixValue.setPricePlanMatrixColumn(pricePlanMatrixColumn);
                    pricePlanMatrixValue.setDoubleValue(value.getDoubleValue());
                    pricePlanMatrixValue.setLongValue(value.getLongValue());
                    pricePlanMatrixValue.setStringValue(value.getStringValue());
                    pricePlanMatrixValue.setDateValue(value.getDateValue());
                    pricePlanMatrixValue.setFromDoubleValue(value.getFromDoubleValue());
                    pricePlanMatrixValue.setFromDateValue(value.getFromDateValue());
                    pricePlanMatrixValue.setToDoubleValue(value.getToDoubleValue());
                    pricePlanMatrixValue.setToDateValue(value.getToDateValue());
                    pricePlanMatrixValue.setPricePlanMatrixLine(pricePlanMatrixLine);
                    pricePlanMatrixValue.setBooleanValue(value.getBooleanValue());
                    return pricePlanMatrixValue;
                }).collect(Collectors.toSet());
    }

    public PricePlanMatrixLineDto load(Long ppmLineId) {
        PricePlanMatrixLine ppmLine = findById(ppmLineId);
        if (ppmLine == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, ppmLineId);
        }
        return new PricePlanMatrixLineDto(ppmLine);
    }

    public List<PricePlanMatrixLine> loadMatchedLinesForProductQuote(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues, Long productQuoteId) throws BusinessException{
        List<PricePlanMatrixLine> matchedPrices = getMatchedPriceLines(pricePlanMatrixVersion, attributeValues, null);
        if (matchedPrices.isEmpty()) {
            throw new BusinessApiException("No price match with quote product id: " + productQuoteId + " using price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")");
        }else if(matchedPrices.size() >= 2 && matchedPrices.get(0).getPriority() == matchedPrices.get(1).getPriority())
            throw new BusinessException("Many prices lines with the same priority match with quote product id: "+ productQuoteId + " using price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")");
        return List.of(matchedPrices.get(0));
    }
    
    public PricePlanMatrixLine loadMatchedLinesForServiceInstance(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues, WalletOperation walletOperation)
            throws NoPricePlanException {
        List<PricePlanMatrixLine> matchedPrices = getMatchedPriceLines(pricePlanMatrixVersion, attributeValues, walletOperation);
       
        if (matchedPrices.isEmpty()) {
            throw new NoPricePlanException("No price match with price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ") using attribute : " + attributeValues.stream().map(AttributeValue::getValue));
        
        } else if (matchedPrices.size() >= 2 && matchedPrices.get(0).getPriority().equals(matchedPrices.get(1).getPriority())) {
            throw new NoPricePlanException("Many prices lines with the same priority match with price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ") using attribute : " + attributeValues.stream().map(AttributeValue::getValue));
        }
        return matchedPrices.get(0);
    }

    private List<PricePlanMatrixLine> getMatchedPriceLines(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues, WalletOperation walletOperation) {
        List<PricePlanMatrixLine> priceLines = findByPricePlanMatrixVersion(pricePlanMatrixVersion);
//        List<PricePlanMatrixLine> priceLinesSorted = priceLines.stream()
//                .sorted(Comparator.comparing(PricePlanMatrixLine::getId))
//                .collect(Collectors.toList());
//        int i = 0;
//        for (PricePlanMatrixLine ppml : priceLinesSorted) {
//            ppml.setPriority(i++);
//        }
            
        addBusinessAttributeValues(pricePlanMatrixVersion.getColumns().stream().filter(column->AttributeCategoryEnum.BUSINESS.equals(column.getAttribute().getAttributeCategory())).map(column->column.getAttribute()).collect(Collectors.toList()),attributeValues, walletOperation);
        if(attributeValues.isEmpty()) {
            return priceLines.stream()
                    .filter(PricePlanMatrixLine::isDefaultLine)
                    .collect(Collectors.toList());
        }
        else {
            List<PricePlanMatrixLine> results = priceLines.stream()
                    .filter(line -> line.match(attributeValues))
                    .sorted(Comparator.comparing(PricePlanMatrixLine::getPriority))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(results) && results.size() > 1) {
                // in case we have more than one result, remove the default one (without values)
                results.removeIf(pricePlanMatrixLine -> CollectionUtils.isEmpty(pricePlanMatrixLine.getPricePlanMatrixValues()));
                // if we still have more than one value, get the one with biggest values : values are the number of matched values with passed attributes
                if (results.size() > 1) {
                    return filterByMostMatchedResultValues(results);
                    // if we have more than result, the called method shall be use priority as already developped
                }
            }
            return results;
        }
    }

    /**
	 * @param businessAttributes 
     * @param attributeValues
     * @param walletOperation 
	 */
	private void addBusinessAttributeValues(List<Attribute> businessAttributes, Set<AttributeValue> attributeValues, WalletOperation walletOperation) {
		businessAttributes.stream().forEach(attribute->attributeValues.add(getBusinessAttributeValue(attribute, walletOperation)));
	}

	/**
	 * @param attribute
	 * @return
	 */
	private AttributeValue getBusinessAttributeValue(Attribute attribute, WalletOperation op) {
		Object value=ValueExpressionWrapper.evaluateExpression(attribute.getElValue(), Object.class, op);
		AttributeValue<AttributeValue> attributeValue= new AttributeValue<AttributeValue>(attribute, value);
		return attributeValue;
	}

	public void removeAll(Set<PricePlanMatrixLine> linesToRemove) {
        for (PricePlanMatrixLine l : linesToRemove) {
            remove(findById(l.getId()));
        }
    }
    
    @SuppressWarnings("unchecked")
	public List<PricePlanMatrixLine> findByPriority(Integer priority, Integer currentVersion) {
    	QueryBuilder builder = new QueryBuilder(PricePlanMatrixLine.class, "ppml", Arrays.asList("pricePlanMatrixVersion"));
    	builder.addCriterion("ppml.priority", "=", priority, false);
    	builder.addCriterion("ppml.pricePlanMatrixVersion.currentVersion", "=", currentVersion, false);
    	return builder.getQuery(this.getEntityManager()).getResultList();
    }
    
    public void updatePricePlanMatrixLines(PricePlanMatrixVersion ppmVersion, PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {
        
        Set<PricePlanMatrixLine> lines = new HashSet<>();
        checkDuplicatePricePlanMatrixValues(dtoData.getPricePlanMatrixLines());
        for (PricePlanMatrixLineDto pricePlanMatrixLineDto : dtoData.getPricePlanMatrixLines()) {
            if(pricePlanMatrixLineDto.getPriceWithoutTax() != null && pricePlanMatrixLineDto.getValue() != null){
                log.warn("Property priceWithoutTax is deprecated, please use only property value");
                throw new MeveoApiException("Property priceWithoutTax is deprecated, please use only property value");
            }
            if(pricePlanMatrixLineDto.getPriceWithoutTax() == null && pricePlanMatrixLineDto.getValue() == null){
                log.warn("Property value in lines should not be null");
                throw new MeveoApiException("Property value in lines should not be null");
            }
            if(pricePlanMatrixLineDto.getPriceEL() != null && pricePlanMatrixLineDto.getValueEL() != null){
                log.warn("Property “priceEl” is deprecated, please use only property “valueEl");
                throw new MeveoApiException("Property “priceEl” is deprecated, please use only property “valueEl”");
            }

            PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
            pricePlanMatrixLine.setPriceWithoutTax(pricePlanMatrixLineDto.getPriceWithoutTax());
            pricePlanMatrixLine.setPriority(pricePlanMatrixLineDto.getPriority());
            pricePlanMatrixLine.setValueEL(pricePlanMatrixLineDto.getValueEL() != null ? pricePlanMatrixLineDto.getValueEL() : pricePlanMatrixLineDto.getPriceEL());
            pricePlanMatrixLine.setPricePlanMatrixVersion(ppmVersion);
            pricePlanMatrixLine.setDescription(pricePlanMatrixLineDto.getDescription());
            BigDecimal value = pricePlanMatrixLineDto.getValue() != null? pricePlanMatrixLineDto.getValue():pricePlanMatrixLineDto.getPriceWithoutTax();
            pricePlanMatrixLine.setValue(value);
            create(pricePlanMatrixLine);
            
            Set<PricePlanMatrixValue> pricePlanMatrixValues = getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
            pricePlanMatrixValues.stream().forEach(ppmv -> pricePlanMatrixValueService.create(ppmv));
            pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);
            lines.add(pricePlanMatrixLine);
        }
        ppmVersion.getLines().clear();
        ppmVersion.getLines().addAll(lines);
    }
    
    public void updateWithoutDeletePricePlanMatrixLines(PricePlanMatrixVersion ppmVersion, PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {  
    	
        checkDuplicatePricePlanMatrixValues(dtoData.getPricePlanMatrixLines());
        Provider provider = providerService.getProvider();
        for (PricePlanMatrixLineDto pricePlanMatrixLineDto : dtoData.getPricePlanMatrixLines()) {
        	
        	
            PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
            if(pricePlanMatrixLineDto.getPpmLineId() != null){
                pricePlanMatrixLine = findById(pricePlanMatrixLineDto.getPpmLineId());
                if (pricePlanMatrixLine == null) {
                    throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, pricePlanMatrixLineDto.getPpmLineId());
                }
                converterPricePlanMatrixLineFromDto(ppmVersion, pricePlanMatrixLineDto, pricePlanMatrixLine);                
                Set<PricePlanMatrixValue> pricePlanMatrixValues = getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
                pricePlanMatrixValues.stream().forEach(ppmv -> pricePlanMatrixValueService.create(ppmv));
                pricePlanMatrixLine.getPricePlanMatrixValues().clear();
                pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);
                Set<ConvertedPricePlanMatrixLine> convertedPricePlanMatrixLines = getConvertedPricePlanMatrixLine(pricePlanMatrixLineDto, pricePlanMatrixLine, provider);
                pricePlanMatrixLine.getConvertedPricePlanMatrixLines().addAll(convertedPricePlanMatrixLines);
                update(pricePlanMatrixLine);
            }
            else {                
                converterPricePlanMatrixLineFromDto(ppmVersion, pricePlanMatrixLineDto, pricePlanMatrixLine);                
                create(pricePlanMatrixLine);                
                pricePlanMatrixLineDto.setPpmLineId(pricePlanMatrixLine.getId());
                Set<PricePlanMatrixValue> pricePlanMatrixValues = getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
                pricePlanMatrixValues.stream().forEach(ppmv -> pricePlanMatrixValueService.create(ppmv));
                pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);      
                Set<ConvertedPricePlanMatrixLine> convertedPricePlanMatrixLines = getConvertedPricePlanMatrixLine(pricePlanMatrixLineDto, pricePlanMatrixLine, provider);
                pricePlanMatrixLine.getConvertedPricePlanMatrixLines().addAll(convertedPricePlanMatrixLines);          
                ppmVersion.getLines().add(pricePlanMatrixLine);
            }

        }
    }

    private Set<ConvertedPricePlanMatrixLine> getConvertedPricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto, PricePlanMatrixLine pricePlanMatrixLine,  Provider provider){
        Set<ConvertedPricePlanMatrixLine> convertedPricePlanMatrixLines = new HashSet<>();
        List<String> checkDuplicateTradingCurrency = new ArrayList<>();
        for (ConvertedPricePlanMatrixLineDto convertedPPML : pricePlanMatrixLineDto.getConvertedPricePlanMatrixLines()) {
            if(convertedPPML.getTradingCurrency() == null) {
                throw new MissingParameterException("tradingCurrency");
            }
            TradingCurrency tradingCurrencyToAdd = tradingCurrencyService.findByTradingCurrencyCodeOrId(convertedPPML.getTradingCurrency().getCode(), convertedPPML.getTradingCurrency().getId()); 
            if(tradingCurrencyToAdd == null) {
                throw new MeveoApiException("Trading currency doesn't exist for  ( code : " +  convertedPPML.getTradingCurrency().getCode() + " , id : " + convertedPPML.getTradingCurrency().getId() + " )" );
            }
            
            if( tradingCurrencyToAdd.getCurrency() != null && provider.getCurrency() != null && 
                    tradingCurrencyToAdd.getCurrency().getId().equals(provider.getCurrency().getId())) {
                throw new MeveoApiException("The trading currency must not be the same as functional currency");
            }
            if(checkDuplicateTradingCurrency.contains(convertedPPML.getTradingCurrency().getCode())) {
                throw new MeveoApiException(" User should not be able to add an already added TradingCurrency");
            }else {
                checkDuplicateTradingCurrency.add(tradingCurrencyToAdd.getCurrencyCode());
            }
            ConvertedPricePlanMatrixLine convPPML =  new ConvertedPricePlanMatrixLine(convertedPPML.getConvertedValue(), tradingCurrencyToAdd, convertedPPML.getRate(), convertedPPML.getUseForBillingAccounts(), pricePlanMatrixLine);
            convertedPricePlanMatrixLineService.create(convPPML);
            convertedPricePlanMatrixLines.add(convPPML);
        }
        
      pricePlanMatrixLine.getConvertedPricePlanMatrixLines().clear();
        return convertedPricePlanMatrixLines;
    }
    
    private void converterPricePlanMatrixLineFromDto(PricePlanMatrixVersion ppmVersion, PricePlanMatrixLineDto pricePlanMatrixLineDto,
            PricePlanMatrixLine pricePlanMatrixLineUpdate) {

        if(pricePlanMatrixLineDto.getPriceWithoutTax() != null && pricePlanMatrixLineDto.getValue() != null){
            log.warn("Property priceWithoutTax is deprecated, please use only property value");
            throw new MeveoApiException("Property priceWithoutTax is deprecated, please use only property value");
        }
        if(pricePlanMatrixLineDto.getPriceWithoutTax() == null && pricePlanMatrixLineDto.getValue() == null){
            log.warn("Property value in lines should not be null");
            throw new MeveoApiException("Property value in lines should not be null");
        }

        if(pricePlanMatrixLineDto.getPriceEL() != null && pricePlanMatrixLineDto.getValueEL() != null){
            log.warn("Property “priceEl” is deprecated, please use only property “valueEl");
            throw new MeveoApiException("Property “priceEl” is deprecated, please use only property “valueEl”");
        }

        pricePlanMatrixLineUpdate.setPriceWithoutTax(pricePlanMatrixLineDto.getPriceWithoutTax());
        pricePlanMatrixLineUpdate.setPriority(pricePlanMatrixLineDto.getPriority());
        pricePlanMatrixLineUpdate.setValueEL(pricePlanMatrixLineDto.getValueEL() != null ? pricePlanMatrixLineDto.getValueEL() : pricePlanMatrixLineDto.getPriceEL());
        pricePlanMatrixLineUpdate.setPricePlanMatrixVersion(ppmVersion);
        pricePlanMatrixLineUpdate.setDescription(pricePlanMatrixLineDto.getDescription());
        BigDecimal value = pricePlanMatrixLineDto.getValue() != null? pricePlanMatrixLineDto.getValue():pricePlanMatrixLineDto.getPriceWithoutTax();
        pricePlanMatrixLineUpdate.setValue(value);
    }
    
    public void checkDuplicatePricePlanMatrixValues(List<PricePlanMatrixLineDto> list) {
        for (int i = 0; i < list.size(); i++) {
            var values = list.get(i).getPricePlanMatrixValues(); 
            for (int k = i + 1; k < list.size(); k++) {
                var valTobeCompared = list.get(k).getPricePlanMatrixValues();
                if(!values.isEmpty() 
                        && !valTobeCompared.isEmpty() && Arrays.deepEquals(values.toArray(new PricePlanMatrixValueDto[] {}), valTobeCompared.toArray(new PricePlanMatrixValueDto[] {})))
                    throw new MeveoApiException("A line having similar values already exists!.");
            }
        }
    }

    private List<PricePlanMatrixLine> filterByMostMatchedResultValues(List<PricePlanMatrixLine> ppmls) {
        if (CollectionUtils.isEmpty(ppmls)) {
            return Collections.emptyList();
        }
        // Create a map with COUNT of value, List on PricePlanMatrixLines
        Map<Integer, List<PricePlanMatrixLine>> groupByCountValue = new HashMap<>();
        // Put the result in map
        ppmls.forEach(p -> {
            if (CollectionUtils.isEmpty(p.getPricePlanMatrixValues())) {
                return;
            }
            List<PricePlanMatrixLine> list = groupByCountValue.get(p.getPricePlanMatrixValues().size());
            if (list == null) {
                List<PricePlanMatrixLine> newList = new ArrayList<>();
                newList.add(p);
                groupByCountValue.put(p.getPricePlanMatrixValues().size(), newList);
            } else {
                list.add(p);
            }
        });
        // Reverse order to get the result with the most matched values
        Map<Integer, List<PricePlanMatrixLine>> reverdeOrderResults = groupByCountValue.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        // return list with the most values : 1 or more
        return reverdeOrderResults.get(Collections.max(reverdeOrderResults.entrySet(),
                Comparator.comparingInt(Map.Entry::getKey)).getKey());
    }

}
