package org.meveo.api.catalog;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.PricePlanMatrixColumnDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ProductService;

@Stateless
public class PricePlanMatrixColumnApi extends BaseApi {

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;
    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;
    @Inject
    private ProductService productService;
    @Inject
    private OfferTemplateService offerTemplateService;
    @Inject
    private AttributeService attributeService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    public PricePlanMatrixColumnDto create(String pricePlanMatrixCode, int version, PricePlanMatrixColumnDto dtoData) throws MeveoApiException, BusinessException {

        checkMissingParameters(pricePlanMatrixCode, version, dtoData);

        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(pricePlanMatrixCode, version);
        
        if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())) {
            log.warn("The status of the price plan matrix code={} and current version={}, is PUBLISHED, it can not be updated", pricePlanMatrixCode,version);
            throw new MeveoApiException(String.format("status of the price plan matrix version id=%d is %s, it can not be updated",pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        
        if(!pricePlanMatrixColumnService.findByCodeAndPricePlanMatrixVersion(dtoData.getCode(), pricePlanMatrixVersion).isEmpty()) {
            throw new EntityAlreadyExistsException(PricePlanMatrixColumn.class, "(" + dtoData.getCode().toLowerCase() + ", " + version + ")");
        }

        PricePlanMatrixColumn pricePlanMatrixColumn = new PricePlanMatrixColumn();
        Attribute attribute = loadEntityByCode(attributeService, dtoData.getAttributeCode(), Attribute.class);
        pricePlanMatrixColumn.setAttribute(attribute);
        pricePlanMatrixColumn.setType(attribute.getAttributeType().getColumnType(dtoData.getRange()));
        populatePricePlanMatrixColumn(dtoData, pricePlanMatrixColumn, pricePlanMatrixVersion);

        pricePlanMatrixColumnService.create(pricePlanMatrixColumn);
        return new PricePlanMatrixColumnDto(pricePlanMatrixColumn);
    }

    public PricePlanMatrixColumn update(String pricePlanMatrixCode, int version, PricePlanMatrixColumnDto dtoData) throws MeveoApiException, BusinessException {
        checkMissingParameters(pricePlanMatrixCode, version,dtoData);
        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(pricePlanMatrixCode, version);
        
        if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())) {
            log.warn("The status of the price plan matrix code={} and current version={}, is PUBLISHED, it can not be updated", pricePlanMatrixCode,version);
            throw new MeveoApiException(String.format("status of the price plan matrix version id=%d is %s, it can not be updated",pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        List<PricePlanMatrixColumn> pricePlanMatrixColumns = pricePlanMatrixColumnService.findByCodeAndPricePlanMatrixVersion(dtoData.getCode(), pricePlanMatrixVersion);
        PricePlanMatrixColumn pricePlanMatrixColumn = null;
        if (!pricePlanMatrixColumns.isEmpty()) {
        	pricePlanMatrixColumn = pricePlanMatrixColumns.get(0);
		}else {
			throw new EntityAlreadyExistsException(PricePlanMatrixColumn.class, "(" + dtoData.getCode().toLowerCase() + ", " + version + ")");
		}
        populatePricePlanMatrixColumn(dtoData, pricePlanMatrixColumn,pricePlanMatrixVersion);
        Attribute attribute = loadEntityByCode(attributeService, dtoData.getAttributeCode(), Attribute.class);
        pricePlanMatrixColumn.setAttribute(attribute);
        pricePlanMatrixColumnService.update(pricePlanMatrixColumn);
        pricePlanMatrixColumn.getPricePlanMatrixVersion().setPricePlanMatrix(pricePlanMatrixService.findByCode(pricePlanMatrixCode));
        return pricePlanMatrixColumn;
    }

    public void removePricePlanColumn(String pricePlanMatrixCode, int version, String code){
    	
    	PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(pricePlanMatrixCode, version);
        
        if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())) {
            log.warn("The status of the price plan matrix code={} and current version={}, is PUBLISHED, it can not be updated", pricePlanMatrixCode,version);
            throw new MeveoApiException(String.format("status of the price plan matrix version id=%d is %s, it can not be updated",pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        List<PricePlanMatrixColumn> pricePlanMatrixColumns = pricePlanMatrixColumnService.findByCodeAndPricePlanMatrixVersion(code, pricePlanMatrixVersion);
        PricePlanMatrixColumn pricePlanMatrixColumn = null;
        if (!pricePlanMatrixColumns.isEmpty()) {
        	pricePlanMatrixColumn = pricePlanMatrixColumns.get(0);
		}else {
			throw new EntityAlreadyExistsException(PricePlanMatrixColumn.class, "(" + code.toLowerCase() + ", " + version + ")");
		}
        pricePlanMatrixColumnService.removePricePlanColumn(pricePlanMatrixColumn.getId());
    }

    private void checkMissingParameters(String pricePlanMatrixCode, Integer version, PricePlanMatrixColumnDto dtoData) {


        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }
        if (version == null) {
            missingParameters.add("pricePlanMatrixVersion");
        }

        if (StringUtils.isBlank(dtoData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(dtoData.getAttributeCode())) {
            missingParameters.add("attributeCode");
        }

        handleMissingParametersAndValidate(dtoData);
    }

    private void populatePricePlanMatrixColumn(PricePlanMatrixColumnDto dtoData, PricePlanMatrixColumn pricePlanMatrixColumn, PricePlanMatrixVersion pricePlanMatrixVersion) {
        pricePlanMatrixColumn.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        if(!StringUtils.isEmpty(dtoData.getProductCode()))
        	pricePlanMatrixColumn.setProduct(loadEntityByCode(productService, dtoData.getProductCode(), Product.class));
        if(!StringUtils.isEmpty(dtoData.getOfferTemplateCode()))
        	pricePlanMatrixColumn.setOfferTemplate(loadEntityByCode(offerTemplateService, dtoData.getOfferTemplateCode(), OfferTemplate.class));
        pricePlanMatrixColumn.setCode(dtoData.getCode());
        pricePlanMatrixColumn.setElValue(dtoData.getElValue());
        pricePlanMatrixColumn.setPosition(dtoData.getPosition());
        Attribute attribute = pricePlanMatrixColumn.getAttribute();
        if (attribute != null && attribute.getAttributeType() != AttributeTypeEnum.DATE && attribute.getAttributeType() != AttributeTypeEnum.NUMERIC 
                && attribute.getAttributeType() != AttributeTypeEnum.INTEGER && attribute.getAttributeType() != AttributeTypeEnum.EXPRESSION_LANGUAGE) {
            pricePlanMatrixColumn.setRange(false);
        } else {
            pricePlanMatrixColumn.setRange(dtoData.getRange());
        }
    }
    
    private PricePlanMatrixVersion getPricePlanMatrixVersion(String planMatrixCode, int currentPricePlanMatrixVersion) {
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(planMatrixCode, currentPricePlanMatrixVersion);
        if(pricePlanMatrixVersion == null){
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, planMatrixCode, "pricePlanMatrixCode", ""+currentPricePlanMatrixVersion, "pricePlanMatrixVersion");
        }
        return pricePlanMatrixVersion;
    }

    public PricePlanMatrixColumnDto find(String code) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        PricePlanMatrixColumn pricePlanMatrixColumn = loadEntityByCode(pricePlanMatrixColumnService, code, PricePlanMatrixColumn.class);
        return new PricePlanMatrixColumnDto(pricePlanMatrixColumn);
    }
}
