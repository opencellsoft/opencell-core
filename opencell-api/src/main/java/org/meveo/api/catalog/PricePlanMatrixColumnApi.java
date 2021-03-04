package org.meveo.api.catalog;

import org.elasticsearch.common.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.catalog.PricePlanMatrixColumnDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ProductService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.function.BiFunction;

@Stateless
public class PricePlanMatrixColumnApi extends BaseCrudApi<PricePlanMatrixColumn, PricePlanMatrixColumnDto> {

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

    @Override
    public PricePlanMatrixColumn create(PricePlanMatrixColumnDto dtoData) throws MeveoApiException, BusinessException {

        checkMissingParameters(dtoData);

        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(dtoData.getPricePlanMatrixCode(), dtoData.getPricePlanMatrixVersion());
        
        if(!pricePlanMatrixColumnService.findByCodeAndPlanMaptrixVersion(dtoData.getCode(), pricePlanMatrixVersion).isEmpty()) {
            throw new EntityAlreadyExistsException(PricePlanMatrixColumn.class, "(" + dtoData.getCode() + ", " + dtoData.getPricePlanMatrixVersion() + ")");
        }

        PricePlanMatrixColumn pricePlanMatrixColumn = new PricePlanMatrixColumn();
        Attribute attribute = loadEntityByCode(attributeService, dtoData.getAttributeCode(), Attribute.class);
        pricePlanMatrixColumn.setAttribute(attribute);
        pricePlanMatrixColumn.setRange(dtoData.getRange());
        pricePlanMatrixColumn.setType(attribute.getAttributeType().getColumnType(dtoData.getRange()));
        populatePricePlanMatrixColumn(dtoData, pricePlanMatrixColumn, pricePlanMatrixVersion);


        pricePlanMatrixColumnService.create(pricePlanMatrixColumn);

        return pricePlanMatrixColumn;
    }

    @Override
    public PricePlanMatrixColumn update(PricePlanMatrixColumnDto dtoData) throws MeveoApiException, BusinessException {
        checkMissingParameters(dtoData);
        PricePlanMatrixVersion pricePlanMatrixVersion = getPricePlanMatrixVersion(dtoData.getPricePlanMatrixCode(), dtoData.getPricePlanMatrixVersion());
        PricePlanMatrixColumn pricePlanMatrixColumn = loadEntityByCode(pricePlanMatrixColumnService, dtoData.getCode(), PricePlanMatrixColumn.class);
        populatePricePlanMatrixColumn(dtoData, pricePlanMatrixColumn, pricePlanMatrixVersion);
        Attribute attribute = loadEntityByCode(attributeService, dtoData.getAttributeCode(), Attribute.class);
        pricePlanMatrixColumn.setAttribute(attribute);
        return pricePlanMatrixColumnService.update(pricePlanMatrixColumn);
    }

    public void removePricePlanColumn(String code){
        pricePlanMatrixColumnService.removePricePlanColumn(code);
    }

    @Override
    protected BiFunction<PricePlanMatrixColumn, CustomFieldsDto, PricePlanMatrixColumnDto> getEntityToDtoFunction() {
        return (a, b) -> new PricePlanMatrixColumnDto(a);
    }

    private void checkMissingParameters(PricePlanMatrixColumnDto dtoData) {
       
        if (StringUtils.isBlank(dtoData.getPricePlanMatrixVersion())) {
            missingParameters.add("pricePlanMatrixVersion");
        }
        if (StringUtils.isBlank(dtoData.getAttributeCode())) {
            missingParameters.add("attributeCode");
        }
        if (StringUtils.isBlank(dtoData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(dtoData);
    }

    private void populatePricePlanMatrixColumn(PricePlanMatrixColumnDto dtoData, PricePlanMatrixColumn pricePlanMatrixColumn, PricePlanMatrixVersion pricePlanMatrixVersion) {
        pricePlanMatrixColumn.setPricePlanMatrixVersion(pricePlanMatrixVersion);
        if(!Strings.isEmpty(dtoData.getProductCode()))
        	pricePlanMatrixColumn.setProduct(loadEntityByCode(productService, dtoData.getProductCode(), Product.class));
        if(!Strings.isEmpty(dtoData.getOfferTemplateCode()))
        	pricePlanMatrixColumn.setOfferTemplate(loadEntityByCode(offerTemplateService, dtoData.getOfferTemplateCode(), OfferTemplate.class));
        pricePlanMatrixColumn.setCode(dtoData.getCode());
        pricePlanMatrixColumn.setElValue(dtoData.getElValue());
        pricePlanMatrixColumn.setPosition(dtoData.getPosition());
        if(dtoData.getRange() != null)
        	pricePlanMatrixColumn.setRange(dtoData.getRange());
    }
    
    private PricePlanMatrixVersion getPricePlanMatrixVersion(String plnaMatrixCode, int currentPricePlanMatrixVersion) {
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(plnaMatrixCode, currentPricePlanMatrixVersion);
        if(pricePlanMatrixVersion == null){
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, plnaMatrixCode, "pricePlanMatrixCode", ""+currentPricePlanMatrixVersion, "pricePlanMatrixVersion");
        }
        return pricePlanMatrixVersion;
    }
}
