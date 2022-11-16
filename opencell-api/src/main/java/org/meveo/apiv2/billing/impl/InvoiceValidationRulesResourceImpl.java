package org.meveo.apiv2.billing.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.billing.InvoiceValidationRuleDto;
import org.meveo.api.invoice.InvoiceValidationRulesApiService;
import org.meveo.apiv2.billing.resource.InvoiceValidationRulesResource;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.model.billing.ValidationRuleTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.InvoiceValidationRulesService;
import org.meveo.service.script.ScriptInstanceService;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import static java.util.Optional.ofNullable;

public class InvoiceValidationRulesResourceImpl implements InvoiceValidationRulesResource {

    @Inject
    private InvoiceValidationRulesApiService invoiceValidationRulesApiService;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private InvoiceValidationRulesService invoiceValidationRulesService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    private InvoiceValidationRuleMapper invoiceValidationRuleMapper = new InvoiceValidationRuleMapper();

    @Override
    public Response create(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceValidationRuleDto.getInvoiceType());

        checkMissingParameters(invoiceValidationRuleDto);
        checkInvoiceType(invoiceType);
        checkValidationSriptAndEL(invoiceValidationRuleDto);

        InvoiceValidationRule invoiceValidationRule = invoiceValidationRuleMapper.toEntity(invoiceValidationRuleDto);
        invoiceValidationRule.setInvoiceType(invoiceType);
        checkCodeAndDescription(invoiceValidationRule);
        invoiceValidationRulesApiService.create(invoiceValidationRule);

        ActionStatus responseStatus = new ActionStatus();
        responseStatus.setStatus(ActionStatusEnum.SUCCESS);
        responseStatus.setEntityId(invoiceValidationRule.getId());

        return Response.ok(responseStatus).build();

    }

    @Override
    public Response update(Long invoiceValidationRuleId, InvoiceValidationRuleDto invoiceValidationRuleDto) {

        InvoiceType invoiceType = checkInvoiceType(invoiceValidationRuleDto);

        checkValidationSriptAndEL(invoiceValidationRuleDto);

        InvoiceValidationRule invoiceValidationRule = ofNullable(invoiceValidationRulesService.findById(invoiceValidationRuleId))
                .orElseThrow(() -> new EntityDoesNotExistsException(InvoiceValidationRule.class, invoiceValidationRuleId));

        invoiceValidationRulesApiService.update(invoiceValidationRule.getId(),
                invoiceValidationRuleMapper.toEntity(invoiceValidationRuleDto, invoiceValidationRule, invoiceType));

        ActionStatus responseStatus = new ActionStatus();
        responseStatus.setStatus(ActionStatusEnum.SUCCESS);
        responseStatus.setEntityId(invoiceValidationRule.getId());

        return Response.ok(responseStatus).build();
    }

    private InvoiceType checkInvoiceType(InvoiceValidationRuleDto invoiceValidationRuleDto) {
        InvoiceType invoiceType = null;

        String invoiceTypeInput = invoiceValidationRuleDto.getInvoiceType();
        if(invoiceTypeInput != null){
            invoiceType = invoiceTypeService.findByCode(invoiceValidationRuleDto.getInvoiceType());
            checkInvoiceType(invoiceType);
        }
        return invoiceType;
    }

    private void checkMissingParameters(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        if (invoiceValidationRuleDto.getInvoiceType() == null) {
            throw new BadRequestException("Invoice Type is missing");
        }

        if (invoiceValidationRuleDto.getFailStatus() == null) {
            throw new BadRequestException("Fail status is missing");
        }

    }


    private void checkInvoiceType(InvoiceType invoiceType) {

        if (invoiceType == null) {
            throw new BusinessException("Invoice Type does not exist");
        }
    }


    private void checkCodeAndDescription(InvoiceValidationRule invoiceValidationRule) {

        if (invoiceValidationRule.getCode() == null || invoiceValidationRule.getCode().isEmpty()) {
             invoiceValidationRule.setCode(invoiceValidationRule.getInvoiceType() + "_" + invoiceValidationRule.getPriority());
        }

        if (invoiceValidationRule.getDescription() == null || invoiceValidationRule.getDescription().isEmpty()) {

             invoiceValidationRule.setDescription("Rule " + invoiceValidationRule.getPriority() + ":"
                 + invoiceValidationRule.getFailStatus().toString() + "if" + invoiceValidationRule.getType().toString()
                 + "fails");
        }
    }

    private void checkValidationSriptAndEL(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        ValidationRuleTypeEnum validationRuleType = invoiceValidationRuleDto.getType();

        boolean isValidationScriptAndValidationEL = invoiceValidationRuleDto.getValidationScript() != null && invoiceValidationRuleDto.getValidationEL() != null;
        boolean isValidationScriptAndTypeScript = invoiceValidationRuleDto.getValidationScript() != null && validationRuleType.equals(ValidationRuleTypeEnum.SCRIPT);
        boolean isValidationScriptAndTypeIsNotScript = invoiceValidationRuleDto.getValidationScript() != null && validationRuleType != ValidationRuleTypeEnum.SCRIPT;
        boolean isValidationELAndTypeisNotEL = invoiceValidationRuleDto.getValidationEL() != null && validationRuleType != ValidationRuleTypeEnum.EXPRESSION_LANGUAGE;


        if (isValidationScriptAndValidationEL) {
            throw new BusinessException("You cannot have both validation script and validation EL");
        }

        if (isValidationScriptAndTypeScript) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(invoiceValidationRuleDto.getValidationScript());
            if (scriptInstance == null) {
                throw new BusinessException("Script Instance does not exist");
            }
        }

        if (isValidationScriptAndTypeIsNotScript) {
            throw new BusinessException("Type is set to SCRIPT and validation EL is provided instead of validation script");

        }
        if (isValidationELAndTypeisNotEL) {
            throw new BusinessException("Type is set to EL and validation script is provided instead of validation EL");
        }
    }

}
