package org.meveo.apiv2.billing.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.billing.InvoiceValidationRuleDto;
import org.meveo.api.invoice.InvoiceValidationRulesApiService;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.billing.resource.InvoiceValidationRulesResource;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceValidationRule;
import org.meveo.model.billing.ValidationRuleTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.InvoiceValidationRulesService;
import org.meveo.service.script.ScriptInstanceService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import java.util.Objects;

import static java.util.Optional.ofNullable;

@Interceptors({ WsRestApiInterceptor.class })
public class InvoiceValidationRulesResourceImpl implements InvoiceValidationRulesResource {

    @Inject
    private InvoiceValidationRulesApiService invoiceValidationRulesApiService;

    @Inject
    private InvoiceTypeService invoiceTypeService;
    
    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoiceValidationRulesService invoiceValidationRulesService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    private final InvoiceValidationRuleMapper invoiceValidationRuleMapper = new InvoiceValidationRuleMapper();

    @Override
    public Response create(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        checkValidationRuleDto(invoiceValidationRuleDto);
        InvoiceValidationRule invoiceValidationRule = invoiceValidationRuleMapper.toEntity(invoiceValidationRuleDto);
        postValidationRuleMapper(invoiceValidationRuleDto, invoiceValidationRule);
        
        invoiceValidationRulesApiService.create(invoiceValidationRule);

        return Response.ok(buildSucessResponse(invoiceValidationRule.getId())).build();
    }

    @Override
    public Response update(Long invoiceValidationRuleId, InvoiceValidationRuleDto invoiceValidationRuleDto) {

        InvoiceType invoiceType = checkInvoiceType(invoiceValidationRuleDto);
        checkValidationRuleDto(invoiceValidationRuleDto);
        ScriptInstance scriptInstance = checkScriptInstance(invoiceValidationRuleDto);

        InvoiceValidationRule invoiceValidationRule = ofNullable(invoiceValidationRulesService.findById(invoiceValidationRuleId))
                .orElseThrow(() -> new EntityDoesNotExistsException(InvoiceValidationRule.class, invoiceValidationRuleId));
        
        invoiceValidationRule = invoiceValidationRuleMapper.toEntity(invoiceValidationRuleDto, invoiceValidationRule, invoiceType, scriptInstance);
        postValidationRuleMapper(invoiceValidationRuleDto, invoiceValidationRule);

        invoiceValidationRulesApiService.update(invoiceValidationRule.getId(), invoiceValidationRule);

        return Response.ok(buildSucessResponse(invoiceValidationRule.getId())).build();
    }

    @Override
    public Response delete(Long id) {
    	checkRuleAlreadyReferenced(id);
        invoiceValidationRulesApiService.delete(id);
        return Response.ok(buildSucessResponse(id)).build();
    }
    
    private ValidationRuleTypeEnum getValidationRuleType(InvoiceValidationRuleDto invoiceValidationRuleDto) {
        if (invoiceValidationRuleDto.getType() == null) {
            throw new MissingParameterException("Validation Rule Type is Missing");
        }
        try {
            return ValidationRuleTypeEnum.valueOf(invoiceValidationRuleDto.getType());
        } catch (Exception exception) {
            throw new InvalidParameterException("Type value must be either SCRIPT, EXPRESSION_LANGUAGE OR RULE_SET");
        }
    }

    private static ActionStatus buildSucessResponse(Long invoiceValidationRuleId) {
        ActionStatus responseStatus = new ActionStatus();
        responseStatus.setStatus(ActionStatusEnum.SUCCESS);
        responseStatus.setEntityId(invoiceValidationRuleId);
        return responseStatus;
    }

    private void checkFailStatus(InvoiceValidationRule invoiceValidationRule) {
        if (invoiceValidationRule.getFailStatus() == null && invoiceValidationRule.getParentRule() == null) {
            throw new BadRequestException("Fail status is missing");
        }
    }

    private InvoiceType checkInvoiceType(InvoiceValidationRuleDto invoiceValidationRuleDto) {
        InvoiceType invoiceType = null;

        String invoiceTypeInput = invoiceValidationRuleDto.getInvoiceType();
        if(invoiceTypeInput != null){
            invoiceType = invoiceTypeService.findByCode(invoiceValidationRuleDto.getInvoiceType());
            if (invoiceType == null) {
                throw new BusinessException("Invoice Type does not exist");
            }
        }
        return invoiceType;
    }
    
    private ScriptInstance checkScriptInstance(InvoiceValidationRuleDto invoiceValidationRuleDto) {
    	ScriptInstance scriptInstance = null;
        if(invoiceValidationRuleDto.getValidationScript() != null) {
        	scriptInstance = scriptInstanceService.findByCode(invoiceValidationRuleDto.getValidationScript());
        	 if (scriptInstance == null) {
                 throw new BusinessException("Script validation does not exist");
             }
        }
        return scriptInstance;
    }
    
    private void checkRuleAlreadyReferenced(Long id) {
        if (invoiceService.countInvoicesByValidationRule(id) > 0) {
            throw new BadRequestException("Rule [id= " + id + "] cannot be deleted as it is suspecting or rejecting some invoices. "
            		+ "Please, validate, correct, or cancel these invoices before deleting the rule.");
        }
    }

	private void postValidationRuleMapper(InvoiceValidationRuleDto invoiceValidationRuleDto, InvoiceValidationRule invoiceValidationRule) {
		checkFailStatus(invoiceValidationRule);
		invoiceValidationRule.setInvoiceType(checkInvoiceType(invoiceValidationRuleDto));
        invoiceValidationRule.setValidationScript(checkScriptInstance(invoiceValidationRuleDto));
        buildCodeAndDescription(invoiceValidationRule);
        if (ValidationRuleTypeEnum.RULE_SET.equals(ValidationRuleTypeEnum.valueOf(invoiceValidationRuleDto.getType()))) {
        	for (int i = 0;  i < invoiceValidationRule.getSubRules().size(); i++) {
        		postValidationRuleMapper(invoiceValidationRuleDto.getSubRules().get(i), invoiceValidationRule.getSubRules().get(i));
        	}
        }
	}
	
    private void buildCodeAndDescription(InvoiceValidationRule invoiceValidationRule) {
        Long ruleId = invoiceValidationRulesService.nextSequenceId();

        if (invoiceValidationRule.getCode() == null || invoiceValidationRule.getCode().isEmpty()) {
            invoiceValidationRule.setCode("RULE_" + ruleId);
        }

        if ((invoiceValidationRule.getDescription() == null || invoiceValidationRule.getDescription().isEmpty()) && Objects.isNull(invoiceValidationRule.getParentRule())) {

            String scriptCodeOrEL = invoiceValidationRule.getType().equals(ValidationRuleTypeEnum.SCRIPT) ? getScriptValidationShortCode(invoiceValidationRule.getValidationScript().getCode())
                    : ((invoiceValidationRule.getValidationEL() == null)? "" : invoiceValidationRule.getValidationEL()) ;

            invoiceValidationRule.setDescription(String.format("rule %s: %s if %s %s %s" ,
                    ruleId,
                    invoiceValidationRule.getFailStatus().toString(),
                    invoiceValidationRule.getType().toString(),
                    scriptCodeOrEL,
                    invoiceValidationRule.getEvaluationMode().toString()
                    )
            );
        }
    }
    
    private String getScriptValidationShortCode(String code) {
    	return code.substring(code.lastIndexOf('.') + 1);
    }

	private void checkValidationRuleDto(InvoiceValidationRuleDto invoiceValidationRuleDto) {

        if (invoiceValidationRuleDto.getInvoiceType() == null) {
            throw new BadRequestException("Invoice Type is missing");
        }
        if (invoiceValidationRuleDto.getType() == null) {
            throw new BadRequestException("Validation Rule Type is missing");
        }
		
        ValidationRuleTypeEnum validationRuleType = getValidationRuleType(invoiceValidationRuleDto);
        boolean isTypeScriptAndScriptValidationNotProvided = Objects.equals(validationRuleType, ValidationRuleTypeEnum.SCRIPT) && StringUtils.isBlank(invoiceValidationRuleDto.getValidationScript());
        boolean isTypeELAndValidationELNotProvided = Objects.equals(validationRuleType, ValidationRuleTypeEnum.EXPRESSION_LANGUAGE) && StringUtils.isBlank(invoiceValidationRuleDto.getValidationEL());
        boolean isValidationScriptAndValidationEL = invoiceValidationRuleDto.getValidationScript() != null && invoiceValidationRuleDto.getValidationEL() != null;
        boolean isValidationScriptAndTypeScript = invoiceValidationRuleDto.getValidationScript() != null && Objects.equals(validationRuleType, ValidationRuleTypeEnum.SCRIPT);
        boolean isTypeScriptAndValidationELProvided = Objects.equals(validationRuleType, ValidationRuleTypeEnum.SCRIPT) && invoiceValidationRuleDto.getValidationEL() != null;
        boolean isTypeELAndValidationScriptProvided = Objects.equals(validationRuleType, ValidationRuleTypeEnum.EXPRESSION_LANGUAGE) && invoiceValidationRuleDto.getValidationScript() != null;
        
        if (isTypeScriptAndScriptValidationNotProvided) {
            throw new InvalidParameterException("Validation script is mandatory for type=SCRIPT");
        }
        if (isTypeELAndValidationELNotProvided) {
            throw new InvalidParameterException("Validation EL is mandatory for type=EL");
        }
        if (isValidationScriptAndValidationEL) {
            throw new InvalidParameterException("You cannot have both validation script and validation EL");
        }
        if (isValidationScriptAndTypeScript) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(invoiceValidationRuleDto.getValidationScript());
            if (scriptInstance == null) {
                throw new InvalidParameterException("Script Instance does not exist");
            }
        }
        if (isTypeScriptAndValidationELProvided) {
            throw new InvalidParameterException("Type is set to SCRIPT and validation EL is provided instead of validation script");
        }
        if (isTypeELAndValidationScriptProvided) {
            throw new InvalidParameterException("Type is set to EL and validation script is provided instead of validation EL");
        }
        
        if (ValidationRuleTypeEnum.RULE_SET.equals(ValidationRuleTypeEnum.valueOf(invoiceValidationRuleDto.getType()))) {
        	if (invoiceValidationRuleDto.getSubRules() == null || invoiceValidationRuleDto.getSubRules().size() < 2) {
        		throw new InvalidParameterException("Invoice validation rule type RULE_SET must have more than 1 sub-rules");
        	}
        	invoiceValidationRuleDto.getSubRules().forEach(this::checkValidationRuleDto);
        }
       
    }

}
