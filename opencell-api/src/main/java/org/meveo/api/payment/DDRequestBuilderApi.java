/**
 * 
 */
package org.meveo.api.payment;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.payment.DDRequestBuilderDto;
import org.meveo.api.dto.payment.DDRequestBuilderResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestBuilderTypeEnum;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.payments.impl.DDRequestBuilderService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.model.SortOrder;

/**
 * DDRequestBuilderDto CRUD.
 * 
 * @author anasseh
 *
 */
@Stateless
public class DDRequestBuilderApi extends BaseCrudApi<DDRequestBuilder, DDRequestBuilderDto> {

   
    @Inject
    private DDRequestBuilderService ddRequestBuilderService;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

  
    @Override
    public DDRequestBuilder create(DDRequestBuilderDto ddRequestBuilderDto) throws MeveoApiException, BusinessException {
        String code = null;
        if (ddRequestBuilderDto == null) {
            missingParameters.add("ddRequestBuilderDto");
            handleMissingParameters();
            return null;
        } else if (ddRequestBuilderDto != null) {
            code = ddRequestBuilderDto.getCode();
            if (StringUtils.isBlank(code)) {
                missingParameters.add("code");
            }
            
            if (ddRequestBuilderDto.getType() == null) {
                missingParameters.add("type");
            }
            if (ddRequestBuilderDto.getPaymentLevel() == null) {
                missingParameters.add("paymentLevel");
            }            

            if (ddRequestBuilderDto.getType() == DDRequestBuilderTypeEnum.CUSTOM && StringUtils.isBlank(ddRequestBuilderDto.getScriptInstanceCode())) {
                missingParameters.add("scriptInstanceCode");
            }
        }

        handleMissingParameters();

        if (ddRequestBuilderDto != null && ddRequestBuilderDto.getType() == DDRequestBuilderTypeEnum.NATIF) {
            throw new BusinessException("Cant add Natif DDRequestBuilder");
        }

        DDRequestBuilder ddRequestBuilder = ddRequestBuilderService.findByCode(code);
        if (ddRequestBuilder != null) {
            throw new EntityAlreadyExistsException(DDRequestBuilder.class, code);
        }

        ScriptInstance scriptInstance = scriptInstanceService.findByCode(ddRequestBuilderDto.getScriptInstanceCode());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, ddRequestBuilderDto.getScriptInstanceCode());
        }

        ddRequestBuilder = new DDRequestBuilder();
        ddRequestBuilder.setType(ddRequestBuilderDto.getType());
        ddRequestBuilder.setCode(code);
        ddRequestBuilder.setDescription(ddRequestBuilderDto.getDescription());
        ddRequestBuilder.setScriptInstance(scriptInstance);
        ddRequestBuilder.setMaxSizeFile(ddRequestBuilderDto.getMaxSizeFile());
        ddRequestBuilder.setNbOperationPerFile(ddRequestBuilderDto.getNbOperationPerFile());
        ddRequestBuilder.setPaymentLevel(ddRequestBuilderDto.getPaymentLevel());

        if (ddRequestBuilderDto.isDisabled() != null) {
            ddRequestBuilder.setDisabled(ddRequestBuilderDto.isDisabled());
        }

        try {
            populateCustomFields(ddRequestBuilderDto.getCustomFields(), ddRequestBuilder, true, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        ddRequestBuilderService.create(ddRequestBuilder);
        return ddRequestBuilder;
    }

    @Override
    public DDRequestBuilder update(DDRequestBuilderDto ddRequestBuilderDto) throws BusinessException, MeveoApiException {
        String code = null;
        if (ddRequestBuilderDto == null) {
            missingParameters.add("ddRequestBuilderDto");
            handleMissingParameters();
            return null;
        }

        if (StringUtils.isBlank(ddRequestBuilderDto.getCode())) {
            code = ddRequestBuilderDto.getCode();
            missingParameters.add("code");
        }
        handleMissingParameters();
        DDRequestBuilder ddRequestBuilder = null;
        ddRequestBuilder = ddRequestBuilderService.findByCode(code);
        if (ddRequestBuilder == null) {
            throw new EntityDoesNotExistsException(DDRequestBuilder.class, code);
        }

        
        if (ddRequestBuilderDto.getNbOperationPerFile() != null && ddRequestBuilderDto.getNbOperationPerFile()  != 0L ) {
            ddRequestBuilder.setNbOperationPerFile(ddRequestBuilderDto.getNbOperationPerFile());
        }
        if (ddRequestBuilderDto.getMaxSizeFile() != null && ddRequestBuilderDto.getMaxSizeFile()  != 0L ) {
            ddRequestBuilder.setMaxSizeFile(ddRequestBuilderDto.getMaxSizeFile());
        }
        if (ddRequestBuilderDto.getPaymentLevel() != null ) {
            ddRequestBuilder.setPaymentLevel(ddRequestBuilderDto.getPaymentLevel());
        }
        if (!StringUtils.isBlank(ddRequestBuilderDto.getScriptInstanceCode())) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(ddRequestBuilderDto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, ddRequestBuilderDto.getScriptInstanceCode());
            }
            ddRequestBuilder.setScriptInstance(scriptInstance);
        }
        if (!StringUtils.isBlank(ddRequestBuilderDto.getDescription())) {
            ddRequestBuilder.setDescription(ddRequestBuilderDto.getDescription());
        }
       
        ddRequestBuilder.setCode(StringUtils.isBlank(ddRequestBuilderDto.getUpdatedCode()) ? code : ddRequestBuilderDto.getUpdatedCode());

        try {
            populateCustomFields(ddRequestBuilderDto.getCustomFields(), ddRequestBuilder, true, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new BusinessException(e.getMessage());
        }

        ddRequestBuilder = ddRequestBuilderService.update(ddRequestBuilder);
        return ddRequestBuilder;
    }

    @Override
    public DDRequestBuilderDto find(String ddRequestBuilderCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        if (StringUtils.isBlank(ddRequestBuilderCode)) {
            missingParameters.add("ddRequestBuilderCode");
        }

        handleMissingParameters();

        DDRequestBuilder ddRequestBuilder = null;
        ddRequestBuilder = ddRequestBuilderService.findByCode(ddRequestBuilderCode);
        if (ddRequestBuilder == null) {
            throw new EntityDoesNotExistsException(DDRequestBuilder.class, ddRequestBuilderCode);
        }
        return new DDRequestBuilderDto(ddRequestBuilder);
    }
    
 
    /**
     * List the DDRequestBuilders for given criteria.
     *
     * @param pagingAndFiltering the paging and filtering
     * @return the dd request builder list dto
     * @throws InvalidParameterException the invalid parameter exception
     */
    public DDRequestBuilderResponseDto list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
        DDRequestBuilderResponseDto result = new DDRequestBuilderResponseDto();
        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.DESCENDING, null, pagingAndFiltering, PaymentMethod.class);
        Long totalCount = ddRequestBuilderService.count(paginationConfig);
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        if (totalCount > 0) {
            List<DDRequestBuilder> ddRequestBuilders = ddRequestBuilderService.list(paginationConfig);
            for (DDRequestBuilder ddRequestBuilder : ddRequestBuilders) {
                result.getDdRequestBuilders().add(new DDRequestBuilderDto(ddRequestBuilder));
            }
        }
        return result;
    }
}