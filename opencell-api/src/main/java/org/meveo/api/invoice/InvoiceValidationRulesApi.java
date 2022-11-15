package org.meveo.api.invoice;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.invoice.InvoiceValidationRuleDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.model.billing.ValidationRuleTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.InvoiceValidationRulesService;
import org.meveo.service.script.ScriptInstanceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import static java.util.Optional.ofNullable;


@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class InvoiceValidationRulesApi extends BaseApi {

    @Inject
    InvoiceValidationRulesService invoiceValidationRulesService;

    @Inject
    InvoiceTypeService invoiceTypeService;

    @Inject
    ScriptInstanceService scriptInstanceService;

    public void createInvoiceValidationRule(InvoiceValidationRuleDto invoiceValidationRuleDto) {


        checkMissingParameters(invoiceValidationRuleDto);
        checkBusinessValidationOnParameters(invoiceValidationRuleDto);

        InvoiceValidationRule invoiceValidationRule = invoiceValidationRulesService.createInvoiceValidationOrUpdateFromDto(invoiceValidationRuleDto,null);

        updateInvoiceTypePriority(invoiceValidationRuleDto.getPriority(),invoiceValidationRule);

        invoiceValidationRulesService.create(invoiceValidationRule);

    }


    private void checkMissingParameters(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        if (invoiceValidationRuleDto.getInvoiceTypeDto() == null) {
            missingParameters.add("invoiceType");
        }

        if (invoiceValidationRuleDto.getFailStatus() == null) {
            missingParameters.add("failStatus");
        }

        handleMissingParameters();
    }

    private void checkBusinessValidationOnParameters(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        checkInvoiceType(invoiceValidationRuleDto.getInvoiceTypeDto().getCode());
        checkCodeAndDescription(invoiceValidationRuleDto);
        checkValidationSriptAndEL(invoiceValidationRuleDto);

    }

    private void checkInvoiceType(String invoiceTypeCode) {

        InvoiceType invoiceType = invoiceTypeService.findByCode(invoiceTypeCode);

        if (invoiceType == null) {
            throw new BusinessException("Invoice Type does not exist");
        }
    }


    private void checkCodeAndDescription(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        if (invoiceValidationRuleDto.getCode() == null || invoiceValidationRuleDto.getCode().isEmpty()) {
            invoiceValidationRuleDto.setCode(invoiceValidationRuleDto.getInvoiceTypeDto().getCode() + "_" + invoiceValidationRuleDto.getPriority());
        }

        if (invoiceValidationRuleDto.getDescription() == null || invoiceValidationRuleDto.getDescription().isEmpty()) {

            ValidationRuleTypeEnum invoiceValidationRuleType = invoiceValidationRuleDto.getType();
            String descriptionSuffix = "";

            if (invoiceValidationRuleType.equals(ValidationRuleTypeEnum.SCRIPT)) {
                descriptionSuffix = invoiceValidationRuleDto.getCode();
            }
            if (invoiceValidationRuleType.equals(ValidationRuleTypeEnum.EXPRESSION_LANGUAGE)) {
                descriptionSuffix = invoiceValidationRuleDto.getValidationEL();
            }

            invoiceValidationRuleDto.setDescription("Rule " + invoiceValidationRuleDto.getPriority() + ":"
                    + invoiceValidationRuleDto.getFailStatus().toString() + descriptionSuffix);
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

    private void updateInvoiceTypePriority(Integer priority, InvoiceValidationRule invoiceValidationRule) {

        InvoiceType invoiceType = invoiceValidationRule.getInvoiceType();

        if (priority == null) {
            invoiceValidationRule.setPriority(invoiceType.getInvoiceValidationRules() != null ? invoiceType.getInvoiceValidationRules().size() + 1 : null);
        } else {
            InvoiceType updatedInvoiceType = invoiceValidationRulesService.reorderInvoiceValidationRules(invoiceType, invoiceValidationRule, false);
            invoiceTypeService.update(updatedInvoiceType);
        }
    }


    public void updateInvoiceValidationRule(Long invoiceValidationRuleId, InvoiceValidationRuleDto invoiceValidationRuleDto) {

        checkInvoiceType(invoiceValidationRuleDto.getInvoiceTypeDto().getCode());
        checkValidationSriptAndEL(invoiceValidationRuleDto);

        InvoiceValidationRule invoiceValidationRule =  ofNullable(invoiceValidationRulesService.findById(invoiceValidationRuleId))
                .orElseThrow(() -> new EntityDoesNotExistsException(InvoiceValidationRule.class, invoiceValidationRuleId));

        invoiceValidationRule = invoiceValidationRulesService.createInvoiceValidationOrUpdateFromDto(invoiceValidationRuleDto,invoiceValidationRule);

        invoiceValidationRulesService.update(invoiceValidationRule);
    }
}
