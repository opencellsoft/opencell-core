package org.meveo.api.finance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.finance.ReportExtractService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class ReportExtractApi extends BaseCrudApi<ReportExtract, ReportExtractDto> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ReportExtractService reportExtractService;

    @Override
    public ReportExtract create(ReportExtractDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getScriptType())) {
            missingParameters.add("scriptType");
        }
        if (StringUtils.isBlank(postData.getFilenameFormat())) {
            missingParameters.add("filenameFormat");
        }
        handleMissingParameters();

        if (reportExtractService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(ReportExtract.class, postData.getCode());
        }

        ReportExtract reportExtract = convertReportExtractFromDto(postData, null);
        reportExtractService.create(reportExtract);
        return reportExtract;
    }

    @Override
    public ReportExtract update(ReportExtractDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getScriptType())) {
            missingParameters.add("scriptType");
        }
        if (StringUtils.isBlank(postData.getFilenameFormat())) {
            missingParameters.add("filenameFormat");
        }
        handleMissingParameters();

        ReportExtract reportExtract = reportExtractService.findByCode(postData.getCode());
        if (reportExtract == null) {
            throw new EntityDoesNotExistsException(ReportExtract.class, postData.getCode());
        }

        reportExtract = convertReportExtractFromDto(postData, reportExtract);
        reportExtract = reportExtractService.update(reportExtract);
        return reportExtract;
    }

    public List<ReportExtractDto> list() {
        List<ReportExtract> reportExtracts = reportExtractService.list();
        return (reportExtracts == null || reportExtracts.isEmpty()) ? new ArrayList<>() : reportExtracts.stream().map(p -> new ReportExtractDto(p)).collect(Collectors.toList());
    }

    @Override
    public ReportExtractDto find(String code) throws EntityDoesNotExistsException {
        ReportExtract reportExtract = reportExtractService.findByCode(code);
        if (reportExtract == null) {
            throw new EntityDoesNotExistsException(ReportExtract.class, code);
        }

        return new ReportExtractDto(reportExtract);
    }

    private ReportExtract convertReportExtractFromDto(ReportExtractDto dto, ReportExtract reportExtractToUpdate) throws EntityDoesNotExistsException {

        ReportExtract reportExtract = reportExtractToUpdate;
        if (reportExtract == null) {
            reportExtract = new ReportExtract();
            if (dto.isDisabled() != null) {
                reportExtract.setDisabled(dto.isDisabled());
            }
        }

        reportExtract.setCategory(dto.getCategory());
        reportExtract.setCode(dto.getCode());
        reportExtract.setDescription(dto.getDescription());
        reportExtract.setEndDate(dto.getEndDate());
        reportExtract.setFilenameFormat(dto.getFilenameFormat());
        reportExtract.setParams(dto.getParams());
        reportExtract.setStartDate(dto.getStartDate());
        reportExtract.setScriptType(dto.getScriptType());
        if (dto.getScriptType().equals(ReportExtractScriptTypeEnum.JAVA)) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(dto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getScriptInstanceCode());
            }
            reportExtract.setScriptInstance(scriptInstance);
            reportExtract.setSqlQuery(null);
        } else {
            reportExtract.setSqlQuery(dto.getSqlQuery());
            reportExtract.setScriptInstance(null);
        }

        return reportExtract;
    }

    public void runReportExtract(RunReportExtractDto postData) throws BusinessException {
        ReportExtract reportExtract = reportExtractService.findByCode(postData.getCode());
        reportExtractService.runReport(reportExtract, postData.getParams());
    }
}
