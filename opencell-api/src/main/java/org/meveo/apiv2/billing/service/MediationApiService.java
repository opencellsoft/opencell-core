package org.meveo.apiv2.billing.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.CdrListResult;
import org.meveo.apiv2.billing.CdrListResult.CdrError;
import org.meveo.apiv2.billing.CdrListResult.Statistics;
import org.meveo.apiv2.billing.RegisterCdrListModeEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class MediationApiService {

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private CDRParsingService cdrParsingService;

    @Inject
    private CDRService cdrService;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CdrListResult registerCdrList(CdrListInput postData, String ipAddress) {
        if (postData == null) {
            throw new BadRequestException("The input params are required");
        }

        List<String> cdrLines = postData.getCdrs();
        if (cdrLines == null || cdrLines.isEmpty()) {
            throw new BadRequestException("The cdrs list are required");
        }

        ParamBean param = ParamBeanFactory.getAppScopeInstance();
        int maxCdrSizeViaAPI = param.getPropertyAsInteger("mediation.maxCdrSizeViaAPI", 1000);
        if (cdrLines.size() > maxCdrSizeViaAPI) {
            throw new BadRequestException("You cannot inject more than " + maxCdrSizeViaAPI + " CDR in one call");
        }

        RegisterCdrListModeEnum mode = postData.getMode();
        ICdrParser cdrParser = cdrParsingService.getCDRParser(null);
        CDR cdr = null;

        CdrListResult cdrListResult = new CdrListResult();
        cdrListResult.setMode(mode);
        int total = cdrLines.size(), success = 0, fail = 0;

        for (String cdrLine : cdrLines) {
            try {
                cdr = cdrParser.parseByApi(cdrLine, currentUser.getUserName(), ipAddress);
                if (cdr == null) {
                    throw new BusinessException("Failed to process a CDR line: " + cdrLine);
                }
                if (cdr.getRejectReason() != null) {
                    if (mode == RegisterCdrListModeEnum.rollbackOnError) {
                        throw cdr.getRejectReasonException();
                    }

                    fail++;
                    log.error("Failed to process a CDR line: {} error {}", cdr.getLine(), cdr.getRejectReason());
                    cdr.setStatus(CDRStatusEnum.ERROR);
                    cdrService.createOrUpdateCdr(cdr);
                    cdrListResult.getErrors().add(new CdrError(cdr.getRejectReasonException().getClass().getSimpleName(), cdr.getRejectReason(), cdr.getLine()));
                    if (mode == RegisterCdrListModeEnum.stopOnFirstFail) {
                        break;
                    }
                    if (mode == RegisterCdrListModeEnum.processAll) {
                        continue;
                    }
                } else {
                    List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
                    List<EDR> edrs = cdrParser.convertCdrToEdr(cdr, accessPoints);
                    cdrParsingService.createEdrs(edrs, cdr);
                    for (EDR edr : edrs) {
                        cdrListResult.getEdrIds().add(edr.getId());
                    }
                    success++;
                }
            } catch (Exception e) {

                if (mode == RegisterCdrListModeEnum.rollbackOnError) {
                    if (e instanceof BusinessException) {
                        throw (BusinessException) e;
                    } else {
                        throw new BusinessException(e);
                    }
                }

                if (e instanceof InvalidAccessException) {
                    fail++;
                    cdr.setRejectReasonException(e);
                }

                cdrListResult.getErrors().add(new CdrError(cdr.getRejectReasonException().getClass().getSimpleName(), cdr.getRejectReason(), cdr.getLine()));
                String errorReason = e.getMessage();
                log.error("Failed to process a CDR line: {} error {}", cdr != null ? cdr.getLine() : null, errorReason);
                cdr.setStatus(CDRStatusEnum.ERROR);
                cdrService.createOrUpdateCdr(cdr);
            }
        }

        Statistics statistics = new Statistics(total, success, fail);
        cdrListResult.setStatistics(statistics);
        return cdrListResult;
    }
}
