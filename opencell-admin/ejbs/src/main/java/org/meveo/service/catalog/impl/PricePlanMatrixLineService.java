package org.meveo.service.catalog.impl;

import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixValueDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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

        if(dtoData.isDefault()) {
            checkMatrixVersionHasNoDefaultLine(pricePlanMatrixVersion);
        }

        PricePlanMatrixLine pricePlanMatrixLine = new PricePlanMatrixLine();
        pricePlanMatrixLine.setIsDefault(dtoData.isDefault());
        pricePlanMatrixLine.setPricetWithoutTax(dtoData.getPricetWithoutTax());
        pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        pricePlanMatrixLine.setPricePlanMatrixValues(getPricePlanMatrixValues(dtoData, pricePlanMatrixLine));
        super.create(pricePlanMatrixLine);
        return new PricePlanMatrixLineDto(pricePlanMatrixLine);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PricePlanMatrixLineDto updatePricePlanMatrixLine(PricePlanMatrixLineDto pricePlanMatrixLineDto) {
        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(pricePlanMatrixLineDto);

        PricePlanMatrixLine pricePlanMatrixLine = findById(pricePlanMatrixLineDto.getPpmLineId());


        if (pricePlanMatrixLine == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixLine.class, pricePlanMatrixLineDto.getPricePlanMatrixCode(), "pricePlanMatrixVersion.pricePlanMatrixCode", "" + pricePlanMatrixLineDto.getPricePlanMatrixVersion(), "pricePlanMatrixVersion.currentVersion");
        }

        if(pricePlanMatrixLineDto.isDefault() && !pricePlanMatrixLine.getIsDefault())
            checkMatrixVersionHasNoDefaultLine(pricePlanMatrixVersion);

        pricePlanMatrixLine.setPricetWithoutTax(pricePlanMatrixLineDto.getPricetWithoutTax());
        pricePlanMatrixLine.setIsDefault(pricePlanMatrixLineDto.isDefault());
        Set<PricePlanMatrixValue> pricePlanMatrixValues = getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine);
        pricePlanMatrixLine.getPricePlanMatrixValues().clear();
        pricePlanMatrixLine.getPricePlanMatrixValues().addAll(pricePlanMatrixValues);
        pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        PricePlanMatrixLine update = super.update(pricePlanMatrixLine);
        return new PricePlanMatrixLineDto(update);
    }

    private void checkMatrixVersionHasNoDefaultLine(PricePlanMatrixVersion pricePlanMatrixVersion) {
        Optional<PricePlanMatrixLine> defaultLine = pricePlanMatrixVersion.getLines()
                .stream()
                .filter(PricePlanMatrixLine::getIsDefault)
                .findAny();
        if(defaultLine.isPresent())
            throw new InvalidParameterException(String.format("Matrix version: (code: %s, version: %d), already has a default price line: (PpmLineId: %d)", pricePlanMatrixVersion.getPricePlanMatrix().getCode(), pricePlanMatrixVersion.getVersion(), defaultLine.get().getId()));
    }

    private PricePlanMatrixVersion getPricePlanMatrixVersion(PricePlanMatrixLineDto dtoData) {
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(dtoData.getPricePlanMatrixCode(), dtoData.getPricePlanMatrixVersion());
        if (pricePlanMatrixVersion == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, dtoData.getPricePlanMatrixCode(), "pricePlanMatrixCode", "" + dtoData.getPricePlanMatrixVersion(), "currentVersion");
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

    public List<PricePlanMatrixLine> loadMatchedLines(PricePlanMatrixVersion pricePlanMatrixVersion, Set<QuoteAttribute> quoteAttributes) {
        List<PricePlanMatrixLine> priceLines = findByPricePlanMatrixVersion(pricePlanMatrixVersion);
        List<PricePlanMatrixLine> matchedPrices = getMatchedPriceLines(quoteAttributes, priceLines);
        if (matchedPrices.isEmpty()) {
                return List.of(priceLines.stream()
                        .filter(PricePlanMatrixLine::getIsDefault)
                        .findAny()
                        .orElseThrow(
                                () -> new BusinessApiException("No price match with quote product id: " + quoteAttributes.stream().findAny().get().getQuoteProduct().getId() + " using price plan matrix: (code : " + pricePlanMatrixVersion.getPricePlanMatrix().getCode() + ", version: " + pricePlanMatrixVersion.getCurrentVersion() + ")")
                        ));

        }

        return List.of(matchedPrices.get(0));
    }

    private List<PricePlanMatrixLine> getMatchedPriceLines(Set<QuoteAttribute> quoteAttributes, List<PricePlanMatrixLine> priceLines) {
        return priceLines.stream()
                    .filter(line -> line.match(quoteAttributes))
                    .sorted(Comparator.comparing(line -> line.getMatchingTypeEnum().getPriority()))
                    .collect(Collectors.toList());
    }

}
