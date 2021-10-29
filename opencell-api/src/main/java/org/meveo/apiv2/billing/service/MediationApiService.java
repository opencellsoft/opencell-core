package org.meveo.apiv2.billing.service;

import static org.meveo.apiv2.billing.RegisterCdrListModeEnum.PROCESS_ALL;
import static org.meveo.apiv2.billing.RegisterCdrListModeEnum.ROLLBACK_ON_ERROR;
import static org.meveo.apiv2.billing.RegisterCdrListModeEnum.STOP_ON_FIRST_FAIL;

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
    private ParamBeanFactory paramBeanFactory;

    @Inject
    private CDRParsingService cdrParsingService;

    @Inject
    private CDRService cdrService;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CdrListResult registerCdrList(CdrListInput postData, String ipAddress) {

        validate(postData);

        List<String> cdrLines = postData.getCdrs();
        RegisterCdrListModeEnum mode = postData.getMode();

        CdrListResult cdrListResult = new CdrListResult();
        cdrListResult.setMode(mode);
        int total = cdrLines.size();
        int success = 0;
        int fail = 0;

        ICdrParser cdrParser = cdrParsingService.getCDRParser(null);
        CDR cdr = null;

        for (String cdrLine : cdrLines) {
            try {
                cdr = cdrParser.parseByApi(cdrLine, currentUser.getUserName(), ipAddress);
                if (cdr == null) {
                    throw new BusinessException("Failed to process a CDR line: " + cdrLine);
                }
                if (cdr.getRejectReason() != null) {
                    log.error("Failed to process a CDR line: {} error {}", cdr.getLine(), cdr.getRejectReason());

                    fail++;
                    cdrService.createOrUpdateCdr(cdr);
                    cdrListResult.getErrors().add(new CdrError(cdr.getRejectReasonException().getClass().getSimpleName(), cdr.getRejectReason(), cdr.getLine()));

                    if (mode == PROCESS_ALL) {
                        continue;
                    }
                    if (mode == STOP_ON_FIRST_FAIL) {
                        break;
                    }
                    if (mode == ROLLBACK_ON_ERROR) {
                        throw cdr.getRejectReasonException();
                    }
                }

                List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
                List<EDR> edrs = cdrParser.convertCdrToEdr(cdr, accessPoints);
                cdrParsingService.createEdrs(edrs, cdr);
                addEdrIds(cdrListResult, edrs);
                success++;

            } catch (Exception e) {

                checkRollBackMode(mode, e);

                fail = checkInvalidAccess(fail, cdr, e);

                CdrError cdrError = createCdrError(cdr, cdrLine, e);
                cdrListResult.getErrors().add(cdrError);
                cdrService.createOrUpdateCdr(cdr);

                if (mode == STOP_ON_FIRST_FAIL) {
                    break;
                }
            }
        }

        Statistics statistics = new Statistics(total, success, fail);
        cdrListResult.setStatistics(statistics);
        return cdrListResult;
    }

    private void addEdrIds(CdrListResult cdrListResult, List<EDR> edrs) {
        for (EDR edr : edrs) {
            cdrListResult.getEdrIds().add(edr.getId());
        }
    }

    private CdrError createCdrError(CDR cdr, String cdrLine, Exception e) {
        CdrError cdrError = null;
        if (cdr != null) {
            cdrError = new CdrError(cdr.getRejectReasonException().getClass().getSimpleName(), cdr.getRejectReason(), cdr.getLine());
        } else {
            cdrError = new CdrError(e.getClass().getSimpleName(), e.getMessage(), cdrLine);
        }
        return cdrError;
    }

    private int checkInvalidAccess(int fail, CDR cdr, Exception e) {
        if (e instanceof InvalidAccessException) {
            fail++;
            if (cdr != null) {
                cdr.setRejectReasonException(e);
            }
        }
        return fail;
    }

    private void checkRollBackMode(RegisterCdrListModeEnum mode, Exception e) {
        if (mode == ROLLBACK_ON_ERROR) {
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            } else {
                throw new BusinessException(e);
            }
        }
    }

    private void validate(CdrListInput postData) {
        if (postData == null) {
            throw new BadRequestException("The input params are required");
        }

        List<String> cdrLines = postData.getCdrs();
        if (cdrLines == null || cdrLines.isEmpty()) {
            throw new BadRequestException("The cdrs list are required");
        }

        ParamBean param = paramBeanFactory.getInstance();
        int maxCdrSizeViaAPI = param.getPropertyAsInteger("mediation.maxCdrSizeViaAPI", 1000);
        if (cdrLines.size() > maxCdrSizeViaAPI) {
            throw new BadRequestException("You cannot inject more than " + maxCdrSizeViaAPI + " CDR in one call");
        }
    }
}
