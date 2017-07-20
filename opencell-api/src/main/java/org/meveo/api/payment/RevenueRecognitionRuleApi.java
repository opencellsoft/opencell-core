package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.finance.RevenueRecognitionRuleService;
import org.meveo.service.script.ScriptInstanceService;

@Stateless
public class RevenueRecognitionRuleApi extends BaseApi {

    @Inject
    private RevenueRecognitionRuleService revenueRecognitionRuleService;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void create(RevenueRecognitionRuleDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        if (StringUtils.isBlank(postData.getScript())) {
            missingParameters.add("script");

        } else {
            // If script was passed, code is needed if script source was not passed.
            if (StringUtils.isBlank(postData.getScript().getCode()) && StringUtils.isBlank(postData.getScript().getScript())) {
                missingParameters.add("script.code");

                // Otherwise code is calculated from script source by combining package and classname
            } else if (!StringUtils.isBlank(postData.getScript().getScript())) {
                String fullClassname = ScriptInstanceService.getFullClassname(postData.getScript().getScript());
                if (!StringUtils.isBlank(postData.getScript().getCode()) && !postData.getScript().getCode().equals(fullClassname)) {
                    throw new BusinessApiException("The code and the canonical script class name must be identical");
                }
                postData.getScript().setCode(fullClassname);
            }
        }
        handleMissingParameters();

        if (revenueRecognitionRuleService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(RevenueRecognitionRule.class, postData.getCode());
        }

        RevenueRecognitionRule rrr = new RevenueRecognitionRule();
        revenueRecognitionRuleFromDTO(postData, rrr);

        revenueRecognitionRuleService.create(rrr);
    }

    public void update(RevenueRecognitionRuleDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getScript())) {
            missingParameters.add("script");

        } else {
            // If script was passed, code is needed if script source was not passed.
            if (StringUtils.isBlank(postData.getScript().getCode()) && StringUtils.isBlank(postData.getScript().getScript())) {
                missingParameters.add("script.code");

                // Otherwise code is calculated from script source by combining package and classname
            } else if (!StringUtils.isBlank(postData.getScript().getScript())) {
                String fullClassname = ScriptInstanceService.getFullClassname(postData.getScript().getScript());
                if (!StringUtils.isBlank(postData.getScript().getCode()) && !postData.getScript().getCode().equals(fullClassname)) {
                    throw new BusinessApiException("The code and the canonical script class name must be identical");
                }
                postData.getScript().setCode(fullClassname);
            }
        }
        handleMissingParameters();

        RevenueRecognitionRule rrr = revenueRecognitionRuleService.findByCode(postData.getCode());
        if (rrr == null) {
            throw new EntityDoesNotExistsException(RevenueRecognitionRule.class, postData.getCode());
        }

        revenueRecognitionRuleFromDTO(postData, rrr);
        revenueRecognitionRuleService.update(rrr);
    }

    public RevenueRecognitionRuleDto find(String revenueRecognitionRuleCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(revenueRecognitionRuleCode)) {
            missingParameters.add("revenueRecognitionRuleCode");
        }
        handleMissingParameters();

        RevenueRecognitionRule rrr = revenueRecognitionRuleService.findByCode(revenueRecognitionRuleCode);
        if (rrr == null) {
            throw new EntityDoesNotExistsException(RevenueRecognitionRule.class, revenueRecognitionRuleCode);
        }
        RevenueRecognitionRuleDto result = new RevenueRecognitionRuleDto(rrr);
        return result;
    }

    public void remove(String revenueRecognitionRuleCode) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(revenueRecognitionRuleCode)) {
            missingParameters.add("revenueRecognitionRuleCode");
        }

        handleMissingParameters();
        RevenueRecognitionRule rrr = revenueRecognitionRuleService.findByCode(revenueRecognitionRuleCode);
        if (rrr == null) {
            throw new EntityDoesNotExistsException(RevenueRecognitionRule.class, revenueRecognitionRuleCode);
        }

        revenueRecognitionRuleService.remove(rrr);
    }

    public void createOrUpdate(RevenueRecognitionRuleDto postData) throws MeveoApiException, BusinessException {
        RevenueRecognitionRule rrr = revenueRecognitionRuleService.findByCode(postData.getCode());
        if (rrr == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    public List<RevenueRecognitionRuleDto> list() {
        List<RevenueRecognitionRuleDto> result = new ArrayList<RevenueRecognitionRuleDto>();
        List<RevenueRecognitionRule> rules = revenueRecognitionRuleService.list();
        for (RevenueRecognitionRule rule : rules) {
            result.add(new RevenueRecognitionRuleDto(rule));
        }
        return result;
    }

    /**
     * Convert RevenueRecognitionRuleDto to a RevenueRecognitionRule instance.
     * 
     * @param dto RevenueRecognitionRuleDto object to convert
     * @param action RevenueRecognitionRule to update with values from dto
     * @return A new or updated RevenueRecognitionRule object
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    private void revenueRecognitionRuleFromDTO(RevenueRecognitionRuleDto dto, RevenueRecognitionRule rrr) throws MeveoApiException, BusinessException {

        rrr.setCode(dto.getCode());
        rrr.setDescription(dto.getDescription());
        rrr.setDisabled(dto.isDisabled());
        rrr.setStartDelay(dto.getStartDelay());
        rrr.setStartUnit(dto.getStartUnit());
        rrr.setStartEvent(dto.getStartEvent());
        rrr.setStopDelay(dto.getStopDelay());
        rrr.setStopUnit(dto.getStopUnit());
        rrr.setStopEvent(dto.getStopEvent());

        // Extract script associated with an action
        ScriptInstance scriptInstance = null;

        // Should create it or update script only if it has full information only
        if (!dto.getScript().isCodeOnly()) {
            scriptInstanceApi.createOrUpdate(dto.getScript());
        }

        scriptInstance = scriptInstanceService.findByCode(dto.getScript().getCode());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getScript().getCode());
        }
        rrr.setScript(scriptInstance);
    }
}