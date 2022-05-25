package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixValueDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeCategoryEnum;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;

@Stateless
public class PricePlanMatrixLineService extends PersistenceService<PricePlanMatrixLine> {

    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;


    public List<PricePlanMatrixLine> findByPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        try {
            return getEntityManager().createNamedQuery("PricePlanMatrixLine.findByPricePlanMatrixVersion", entityClass)
                    .setParameter("pricePlanMatrixVersion", pricePlanMatrixVersion)
                    .getResultList();
        } catch (NoResultException exp) {
            return new ArrayList<>();
        }
    }
    
    public List<PricePlanMatrixLine> findByPricePlanMatrixVersionIds(List<Long> ppmvIds) {
        try {
            return getEntityManager().createNamedQuery("PricePlanMatrixLine.findByPricePlanMatrixVersionIds", entityClass)
                    .setParameter("ppmvIds", ppmvIds)
                    .getResultList();
        } catch (NoResultException exp) {
            return new ArrayList<>();
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PricePlanMatrixLineDto createPricePlanMatrixLine(PricePlanMatrixLineDto dtoData) {
        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(dtoData);
        
        if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())) {
            log.warn("The status of the price plan matrix code={} and current version={}, is PUBLISHED, it can not be updated", pricePlanMatrixVersion.getPricePlanMatrix().getCode(),pricePlanMatrixVersion.getCurrentVersion());
            throw new MeveoApiException(String.format("status of the price plan matrix version id=%d is %s, it can not be updated",pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
        pricePlanMatrixLine.setPriceWithoutTax(dtoData.getPriceWithoutTax());
        pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        pricePlanMatrixLine.setPriority(dtoData.getPriority());
        pricePlanMatrixLine.setDescription(dtoData.getDescription());
        pricePlanMatrixLine.setPricePlanMatrixValues(getPricePlanMatrixValues(dtoData, pricePlanMatrixLine));
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
                    var pricePlanMatrixColumns = pricePlanMatrixColumnService.findByCodeAndPricePlanMatrixVersion(value.getPpmColumnCode(), pricePlanMatrixLine.getPricePlanMatrixVersion());
                    if (pricePlanMatrixColumns.isEmpty())
                        throw new EntityDoesNotExistsException(PricePlanMatrixColumn.class, value.getPpmColumnCode());
                    pricePlanMatrixValue.setPricePlanMatrixColumn(pricePlanMatrixColumns.get(0));
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
    
    public PricePlanMatrixLine loadMatchedLinesForServiceInstance(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues, String serviceInstanceCode, WalletOperation walletOperation)
            throws NoPricePlanException {
        List<PricePlanMatrixLine> matchedPrices = getMatchedPriceLines(pricePlanMatrixVersion, attributeValues, walletOperation);
       
        if (matchedPrices.isEmpty()) {
            throw new NoPricePlanException("No price match with service instance code: " + serviceInstanceCode + " using price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode()
                    + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")");
        
        } else if (matchedPrices.size() >= 2 && matchedPrices.get(0).getPriority() == matchedPrices.get(1).getPriority()) {
            throw new NoPricePlanException("Many prices lines with the same priority match with service instance code: " + serviceInstanceCode + " using price plan matrix: (code : "
                    + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")");
        }
        return matchedPrices.get(0);
    }

    private List<PricePlanMatrixLine> getMatchedPriceLines(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues, WalletOperation walletOperation) {
        List<PricePlanMatrixLine> priceLines = findByPricePlanMatrixVersion(pricePlanMatrixVersion);
    	addBusinessAttributeValues(pricePlanMatrixVersion.getColumns().stream().filter(column->AttributeCategoryEnum.BUSINESS.equals(column.getAttribute().getAttributeCategory())).map(column->column.getAttribute()).collect(Collectors.toList()),attributeValues, walletOperation);
        if(attributeValues.isEmpty()) {
            return priceLines.stream()
                    .filter(PricePlanMatrixLine::isDefaultLine)
                    .collect(Collectors.toList());
        }
        else {
            return priceLines.stream()
                    .filter(line -> line.match(attributeValues))
                    .sorted(Comparator.comparing(PricePlanMatrixLine::getPriority))
                    .collect(Collectors.toList());
        }
    }

    /**
	 * @param businessAttributes 
     * @param attributeValues
     * @param walletOperation 
	 * @param product 
	 */
	private void addBusinessAttributeValues(List<Attribute> businessAttributes, Set<AttributeValue> attributeValues, WalletOperation walletOperation) {
		businessAttributes.stream().forEach(attribute->attributeValues.add(getBusinessAttributeValue(attribute, walletOperation)));
	}

	/**
	 * @param attribute
	 * @param walletOperation 
	 * @param product 
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
        for (PricePlanMatrixLineDto pricePlanMatrixLineDto:dtoData.getPricePlanMatrixLines()) {
            PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
            pricePlanMatrixLine.setPriceWithoutTax(pricePlanMatrixLineDto.getPriceWithoutTax());
            pricePlanMatrixLine.setPriority(pricePlanMatrixLineDto.getPriority());
            pricePlanMatrixLine.setPricePlanMatrixVersion(ppmVersion);
            pricePlanMatrixLine.setDescription(pricePlanMatrixLineDto.getDescription());
            create(pricePlanMatrixLine);
            
            Set<PricePlanMatrixValue> pricePlanMatrixValues = getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
            pricePlanMatrixValues.stream().forEach(ppmv -> pricePlanMatrixValueService.create(ppmv));
            pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);
            lines.add(pricePlanMatrixLine);
        }

        ppmVersion.setLines(lines);
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
}
