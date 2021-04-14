/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.payment;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.RevenueRecognitionRule;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.finance.RevenueRecognitionRuleService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptUtils;
/**
 * @author Abdellatif BARI
 * @author melyoussoufi
 * @lastModifiedVersion 7.2.0
 */
@Stateless
public class RevenueRecognitionRuleApi extends BaseCrudApi<RevenueRecognitionRule, RevenueRecognitionRuleDto> {

    @Inject
    private RevenueRecognitionRuleService revenueRecognitionRuleService;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Override
    public RevenueRecognitionRule create(RevenueRecognitionRuleDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getScript())) {
            missingParameters.add("script");

        } else {
            // If script was passed, code is needed if script source was not passed.
            if (StringUtils.isBlank(postData.getScript().getCode()) && StringUtils.isBlank(postData.getScript().getScript())) {
                missingParameters.add("script.code");

                // Otherwise code is calculated from script source by combining package and classname
            } else if (!StringUtils.isBlank(postData.getScript().getScript())) {
                String fullClassname = ScriptUtils.getFullClassname(postData.getScript().getScript());
                if (!StringUtils.isBlank(postData.getScript().getCode()) && !postData.getScript().getCode().equals(fullClassname)) {
                    throw new BusinessApiException("The code and the canonical script class name must be identical");
                }
                postData.getScript().setCode(fullClassname);
            }
        }
        if(StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(RevenueRecognitionRule.class.getName(), postData);
        }
        handleMissingParameters();

        if (revenueRecognitionRuleService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(RevenueRecognitionRule.class, postData.getCode());
        }

        RevenueRecognitionRule rrr = revenueRecognitionRuleFromDTO(postData, null);

        revenueRecognitionRuleService.create(rrr);
        return rrr;
    }

    @Override
    public RevenueRecognitionRule update(RevenueRecognitionRuleDto postData) throws MeveoApiException, BusinessException {
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
                String fullClassname = ScriptUtils.getFullClassname(postData.getScript().getScript());
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

        rrr = revenueRecognitionRuleFromDTO(postData, rrr);
        rrr = revenueRecognitionRuleService.update(rrr);
        return rrr;
    }

    @Override
    public RevenueRecognitionRuleDto find(String revenueRecognitionRuleCode)
            throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
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
     * @param rrrToUpdate RevenueRecognitionRule to update with values from dto
     * @return A new or updated RevenueRecognitionRule object
     * @throws MeveoApiException
     * @throws BusinessException
     */
    private RevenueRecognitionRule revenueRecognitionRuleFromDTO(RevenueRecognitionRuleDto dto, RevenueRecognitionRule rrrToUpdate) throws MeveoApiException, BusinessException {

        RevenueRecognitionRule rrr = rrrToUpdate;
        if (rrr == null) {
            rrr = new RevenueRecognitionRule();
            if (dto.isDisabled() != null) {
                rrr.setDisabled(dto.isDisabled());
            }
        }

        rrr.setCode(dto.getCode());
        rrr.setDescription(dto.getDescription());
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

        return rrr;
    }
}