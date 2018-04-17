package org.meveo.api.finance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
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
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
@Stateless
public class ReportExtractApi extends BaseApi {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ReportExtractService reportExtractService;

    public void create(ReportExtractDto postData) throws MeveoApiException, BusinessException {
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

        ReportExtract reportExtract = toReportExtract(postData, null);
        reportExtractService.create(reportExtract);
    }

    public void update(ReportExtractDto postData) throws MeveoApiException, BusinessException {
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

        reportExtract = toReportExtract(postData, reportExtract);
        reportExtractService.update(reportExtract);
    }

    public void createOrUpdate(ReportExtractDto postData) throws MeveoApiException, BusinessException {
        if (reportExtractService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    public List<ReportExtractDto> list() {
        List<ReportExtract> reportExtracts = reportExtractService.list();
        return (reportExtracts == null || reportExtracts.isEmpty()) ? new ArrayList<>() : reportExtracts.stream().map(p -> fromReportExtract(p)).collect(Collectors.toList());
    }

    public ReportExtractDto find(String code) throws EntityDoesNotExistsException {
        ReportExtract reportExtract = reportExtractService.findByCode(code);
        if (reportExtract == null) {
            throw new EntityDoesNotExistsException(ReportExtract.class, code);
        }

        return fromReportExtract(reportExtract);
    }

    public void remove(String code) throws MeveoApiException, BusinessException {
        ReportExtract reportExtract = reportExtractService.findByCode(code);
        if (reportExtract == null) {
            throw new EntityDoesNotExistsException(ReportExtract.class, code);
        }

        reportExtractService.remove(reportExtract);
    }

    public ReportExtractDto fromReportExtract(ReportExtract source) {
        ReportExtractDto target = new ReportExtractDto();

        target.setCategory(source.getCategory());
        target.setCode(source.getCode());
        target.setDescription(source.getDescription());
        target.setEndDate(source.getEndDate());
        target.setFilenameFormat(source.getFilenameFormat());
        target.setParams(source.getParams());
        target.setStartDate(source.getStartDate());
        target.setScriptType(source.getScriptType());
        if (source.getScriptType().equals(ReportExtractScriptTypeEnum.JAVA)) {
            if (source.getScriptInstance() != null) {
                target.setScriptInstanceCode(source.getScriptInstance().getCode());
            }
        } else {
            target.setSqlQuery(source.getSqlQuery());
        }

        return target;
    }

    public ReportExtract toReportExtract(ReportExtractDto source, ReportExtract target) throws EntityDoesNotExistsException {
        if (target == null) {
            target = new ReportExtract();
        }

        target.setCategory(source.getCategory());
        target.setCode(source.getCode());
        target.setDescription(source.getDescription());
        target.setEndDate(source.getEndDate());
        target.setFilenameFormat(source.getFilenameFormat());
        target.setParams(source.getParams());
        target.setStartDate(source.getStartDate());
        target.setScriptType(source.getScriptType());
        if (source.getScriptType().equals(ReportExtractScriptTypeEnum.JAVA)) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(source.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, source.getScriptInstanceCode());
            }
            target.setScriptInstance(scriptInstance);
            target.setSqlQuery(null);
        } else {
            target.setSqlQuery(source.getSqlQuery());
            target.setScriptInstance(null);
        }

        return target;
    }

    public void runReportExtract(RunReportExtractDto postData) throws BusinessException {
        ReportExtract reportExtract = reportExtractService.findByCode(postData.getCode());
        reportExtractService.runReport(reportExtract, postData.getParams());
    }
}
