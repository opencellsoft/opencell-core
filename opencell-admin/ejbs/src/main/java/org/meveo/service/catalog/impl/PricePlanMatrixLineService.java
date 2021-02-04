package org.meveo.service.catalog.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            return getEntityManager().createNamedQuery("PricePlanMatrixLine.findByPricePlanMatrixVersion", PricePlanMatrixLine.class)
                    .setParameter("pricePlanMatrixVersion", pricePlanMatrixVersion)
                    .getResultList();
        } catch (NoResultException exp) {
            return new ArrayList<>();
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PricePlanMatrixLineDto createPricePlanMatrixLine(PricePlanMatrixLineDto dtoData) {
        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(dtoData);

        PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
        pricePlanMatrixLine.setPricetWithoutTax(dtoData.getPricetWithoutTax());
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

        PricePlanMatrixLine pricePlanMatrixLine = findById(pricePlanMatrixLineDto.getPpmLineId());


        if (pricePlanMatrixLine == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, pricePlanMatrixLineDto.getPricePlanMatrixCode(), "pricePlanMatrixVersion.pricePlanMatrixCode", "" + pricePlanMatrixLineDto.getPricePlanMatrixVersion(), "pricePlanMatrixVersion.currentVersion");
        }

        pricePlanMatrixLine.setPricetWithoutTax(pricePlanMatrixLineDto.getPricetWithoutTax());
        pricePlanMatrixLine.setPriority(pricePlanMatrixLineDto.getPriority());
        Set<PricePlanMatrixValue> pricePlanMatrixValues = getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
        pricePlanMatrixLine.getPricePlanMatrixValues().clear();
        pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);
        pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);
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

    private Set<PricePlanMatrixValue> getPricePlanMatrixValues(PricePlanMatrixLineDto dtoData, PricePlanMatrixLine pricePlanMatrixLine) {
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
                    PricePlanMatrixColumn pricePlanMatrixColumn = pricePlanMatrixColumnService.findByCode(value.getPpmColumnCode());
                    if (pricePlanMatrixColumn == null)
                        throw new EntityDoesNotExistsException(PricePlanMatrixColumn.class, value.getPpmColumnCode());
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

    public List<PricePlanMatrixLine> loadMatchedLinesForProductQuote(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues, Long productQuoteId) {
        List<PricePlanMatrixLine> matchedPrices = getMatchedPriceLines(pricePlanMatrixVersion, attributeValues);
        if (matchedPrices.isEmpty()) {
            throw new BusinessApiException("No price match with quote product id: " + productQuoteId + " using price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")");
        }else if(matchedPrices.size() >= 2 && matchedPrices.get(0).getPriority() == matchedPrices.get(1).getPriority())
            throw new BusinessException("Many prices lines with the same priority match with quote product id: "+ productQuoteId + " using price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")");
        return List.of(matchedPrices.get(0));
    }

    public PricePlanMatrixLine loadMatchedLinesForServiceInstance(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues, String serviceInstanceCode) {
        List<PricePlanMatrixLine> matchedPrices = getMatchedPriceLines(pricePlanMatrixVersion, attributeValues);
        if (matchedPrices.isEmpty()) {
            throw new BusinessApiException("No price match with service instance code: " + serviceInstanceCode + " using price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")");
        }else if(matchedPrices.size() >= 2 && matchedPrices.get(0).getPriority() == matchedPrices.get(1).getPriority())
            throw new BusinessException("Many prices lines with the same priority match with service instance code: "+ serviceInstanceCode + " using price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")");
        return matchedPrices.get(0);
    }

    private List<PricePlanMatrixLine> getMatchedPriceLines(PricePlanMatrixVersion pricePlanMatrixVersion, Set<AttributeValue> attributeValues) {
        List<PricePlanMatrixLine> priceLines = findByPricePlanMatrixVersion(pricePlanMatrixVersion);
        return priceLines.stream()
                .filter(line -> line.match(attributeValues))
                .sorted(Comparator.comparing(PricePlanMatrixLine::getPriority))
                .collect(Collectors.toList());
    }

    public void removeAll(Set<PricePlanMatrixLine> linesToRemove) {
        for (PricePlanMatrixLine l : linesToRemove) {
            remove(findById(l.getId()));
        }
    }
}
