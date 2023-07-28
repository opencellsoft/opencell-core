package org.meveo.service.catalog.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.ws.rs.core.Response;

import org.hibernate.criterion.MatchMode;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixValueDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class PricePlanMatrixLineService extends PersistenceService<PricePlanMatrixLine> {

    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;

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
        pricePlanMatrixLine.setRatingAccuracy(pricePlanMatrixLine.getPricePlanMatrixValues().size());
        BigDecimal value = dtoData.getValue() != null? dtoData.getValue():dtoData.getPriceWithoutTax();
        pricePlanMatrixLine.setValue(value);

        create(pricePlanMatrixLine);
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
        pricePlanMatrixLine.setRatingAccuracy(pricePlanMatrixLine.getPricePlanMatrixValues().size());
        pricePlanMatrixLine.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        pricePlanMatrixLine.setDescription(pricePlanMatrixLineDto.getDescription());
        BigDecimal value = pricePlanMatrixLineDto.getValue() != null? pricePlanMatrixLineDto.getValue():pricePlanMatrixLineDto.getPriceWithoutTax();
        pricePlanMatrixLine.setValue(value);
        PricePlanMatrixLine update = update(pricePlanMatrixLine);
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
            pricePlanMatrixLine.setPricePlanMatrixValues(getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine));
            pricePlanMatrixLine.setRatingAccuracy(pricePlanMatrixLine.getPricePlanMatrixValues().size());
            create(pricePlanMatrixLine);
            
            lines.add(pricePlanMatrixLine);
        }
        ppmVersion.getLines().clear();
        ppmVersion.getLines().addAll(lines);
    }

    public void updateWithoutDeletePricePlanMatrixLines(PricePlanMatrixVersion ppmVersion, PricePlanMatrixLinesDto dtoData) throws MeveoApiException, BusinessException {        
        checkDuplicatePricePlanMatrixValues(dtoData.getPricePlanMatrixLines());
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
                pricePlanMatrixLine.setRatingAccuracy(pricePlanMatrixLine.getPricePlanMatrixValues().size());
                update(pricePlanMatrixLine);
            }
            else {                
                converterPricePlanMatrixLineFromDto(ppmVersion, pricePlanMatrixLineDto, pricePlanMatrixLine);               
                pricePlanMatrixLine.setPricePlanMatrixValues(getPricePlanMatrixValues(pricePlanMatrixLineDto, pricePlanMatrixLine));
                pricePlanMatrixLine.setRatingAccuracy(pricePlanMatrixLine.getPricePlanMatrixValues().size());
                create(pricePlanMatrixLine);                
                
                pricePlanMatrixLineDto.setPpmLineId(pricePlanMatrixLine.getId());         
                ppmVersion.getLines().add(pricePlanMatrixLine);
            }
        }
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
    public List<PricePlanMatrixLine> search(Map<String, Object> searchInfo) {
        Query query = getEntityManager().createQuery(buildQuery(searchInfo), PricePlanMatrixLine.class);
        injectParamsIntoQuery(searchInfo, query);
        return  query.getResultList();
    }

    private String buildQuery(Map<String, Object> searchInfo) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT distinct ppml FROM PricePlanMatrixLine ppml");
        queryString.append(" LEFT JOIN FETCH ppml.pricePlanMatrixValues ppmvs ");
        queryString.append(" WHERE (LOWER(ppml.description) LIKE :description OR ppml.description is null) ");
        if(searchInfo.containsKey("pricePlanMatrixVersion") && ((Map) searchInfo.get("pricePlanMatrixVersion")).containsKey("id")){
            queryString.append(" AND ppml.pricePlanMatrixVersion.id = :pricePlanMatrixVersionId ");
        }
        if(searchInfo.containsKey("priceWithoutTax")){
            queryString.append(" AND ppml.priceWithoutTax = :priceWithoutTax ");
        }
        if(searchInfo.containsKey("attributes") && !((List)searchInfo.get("attributes")).isEmpty()){
            queryString.append(" AND EXISTS ");
            queryString.append(appendAttributesToQuery((List<Map<String, Object>>) searchInfo.getOrDefault("attributes", Collections.EMPTY_LIST)));
        }
        queryString.append(" ORDER BY ppml." + searchInfo.getOrDefault("sortBy","id"));
        queryString.append(" ");
        queryString.append(searchInfo.getOrDefault("order","ASC"));

        return queryString.toString();
    }

    private String appendAttributesToQuery(List<Map<String, Object>> attributesSearch) {
        return attributesSearch.stream()
                .map(stringObjectMap ->
                        "(SELECT ppmv.id FROM PricePlanMatrixValue ppmv"+
                                " JOIN PricePlanMatrixColumn ppmc ON ppmv.pricePlanMatrixColumn=ppmc"+
                                " WHERE (LOWER(ppmc.code)='"
                                + stringObjectMap.get("column").toString().toLowerCase()
                                + "' AND "
                                + resolveType((String) stringObjectMap.get("type"), stringObjectMap.get("value"), (String) stringObjectMap.getOrDefault("operator", "="))
                                +"AND ppmv.id in elements(ppmvs)))")
                .collect(Collectors.joining(" AND EXISTS "));
    }

    private String resolveType(String type, Object value, String operator) {

        String rangeType = "";
        switch(type.toLowerCase()){
            case "string":
                return "(LOWER(ppmv.stringValue) " + formattedOperation(operator, value.toString().toLowerCase()) + " OR ppmv.stringValue IS NULL)";
            case "long":
                return "(ppmv.longValue " + formattedOperation(operator, value) + " OR ppmv.long_value IS NULL)";
            case "double":
                if("=".equals(operator)){
                    rangeType = "(ppmc.isRange = true and ppmv.fromDoubleValue <=" + Double.valueOf(value.toString())+ "  and ppmv.toDoubleValue >="+ Double.valueOf(value.toString());
                    rangeType +=  " OR (ppmv.doubleValue " + formattedOperation(operator, Double.valueOf(value.toString()))+ " OR ppmv.doubleValue IS NULL))";
                    return rangeType;
                }else if("!=".equals(operator)){
                    rangeType = "(ppmc.isRange = true and ppmv.toDoubleValue <" + Double.valueOf(value.toString())+ "  and ppmv.fromDoubleValue >"+ Double.valueOf(value.toString());
                    rangeType +=  " OR (ppmv.doubleValue " + formattedOperation(operator, Double.valueOf(value.toString()))+ " OR ppmv.doubleValue IS NULL))";
                    return rangeType;
                }
                if(operator.contentEquals("BETWEEN")){
                    return "(ppmv.doubleValue " + formattedOperation(operator, value.toString())+ " OR ppmv.doubleValue IS NULL)";
                }
                return "(ppmv.doubleValue " + formattedOperation(operator, Double.valueOf(value.toString()))+ " OR ppmv.doubleValue IS NULL)";
            case "boolean":
                return "(ppmv.booleanValue " + formattedOperation(operator, Boolean.valueOf(value.toString()))+ " OR ppmv.booleanValue IS NULL)";
            case "date":
                if("=".equals(operator)){
                    rangeType = "(ppmc.isRange = true and ppmv.fromDateValue <='" + new java.sql.Date(parseDate(value).getTime())+ "'  and ppmv.toDateValue >='"+ new java.sql.Date(parseDate(value).getTime())+"'";
                    rangeType +=  " OR (ppmc.isRange = false and (ppmv.dateValue " + formattedOperation(operator, new java.sql.Date(parseDate(value).getTime()))+ " OR ppmv.dateValue IS NULL)))";
                    return rangeType;
                }else if("!=".equals(operator)){
                    rangeType = "(ppmc.isRange = true and ppmv.toDateValue <'" +  new java.sql.Date(parseDate(value).getTime())+ "'  or ppmv.fromDateValue >'"+ new java.sql.Date(parseDate(value).getTime()) + "'";
                    rangeType +=  " OR (ppmc.isRange = false and (ppmv.dateValue " + formattedOperation(operator, new java.sql.Date(parseDate(value).getTime()))+ " OR ppmv.dateValue IS NULL)))";
                    return rangeType;
                }
                return rangeType + "(ppmc.isRange = false and (ppmv.dateValue " + formattedOperation(operator, new java.sql.Date(parseDate(value).getTime()))+ " OR ppmv.dateValue IS NULL))";
            default:
                return "stringValue = ''";
        }
    }

    private String formattedOperation(String operator, Object value) {
        String operand = "";

        switch(operator) {
            case "=":
            case "!=":
            case ">":
            case ">=":
            case "<":
            case "<=": {
                if(value instanceof String || value instanceof java.sql.Date) {
                    operand = operator + " '" + value + "'";
                } else {
                    operand = operator + " " + value;
                }

                break;
            }
            case "like": {
                operand = "like '%" + value + "%'";
                break;
            }
            case "in": {
                operand = "in (" + value + ")";
                break;
            }
            case "BETWEEN" : {
                if(Objects.isNull(value)){
                    throw  new BusinessException("The operator BETWEEN can n ot have a null value");
                }
                String[] values = value.toString().split(";");
                if(values.length < 2 ){
                    throw  new BusinessException("The operator BETWEEN must have 2 values");
                }
                operand = "between " + values[0] + " AND " + values[1];
                break;
            }
            default:
                operand = "= " + value;
        }
        return operand;
    }

    private Date parseDate(Object value) {
        if(value instanceof String) {
            try {
                return ((String) value).matches("^\\d{4}-\\d{2}-\\d{2}$") ? new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(value))
                        : new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(value));
            } catch (ParseException e) {
                throw new IllegalArgumentException("date attribute has not a valid filter value, hint : yyyy-MM-dd or dd/MM/yyyy");
            }
        }
        return new Date((Long) value);
    }

    private void injectParamsIntoQuery(Map<String, Object> searchInfo, Query query) {
        query.setParameter("description", MatchMode.ANYWHERE.toMatchString(((String) searchInfo.getOrDefault("description", "")).toLowerCase()));
        if(searchInfo.containsKey("pricePlanMatrixVersion") && ((Map) searchInfo.get("pricePlanMatrixVersion")).containsKey("id")){
            query.setParameter("pricePlanMatrixVersionId", Long.valueOf(((Map) searchInfo.get("pricePlanMatrixVersion")).getOrDefault("id", 1l)+""));
        }
        if(searchInfo.containsKey("priceWithoutTax")){
            query.setParameter("priceWithoutTax", BigDecimal.valueOf(Double.valueOf(searchInfo.getOrDefault("priceWithoutTax", 0.0)+"")));
        }
    }
}
