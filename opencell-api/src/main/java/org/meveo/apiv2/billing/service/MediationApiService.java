package org.meveo.apiv2.billing.service;

import static org.meveo.apiv2.billing.RegisterCdrListModeEnum.PROCESS_ALL;
import static org.meveo.apiv2.billing.RegisterCdrListModeEnum.ROLLBACK_ON_ERROR;
import static org.meveo.apiv2.billing.RegisterCdrListModeEnum.STOP_ON_FIRST_FAIL;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.admin.job.MediationJobBean;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.CdrListResult;
import org.meveo.apiv2.billing.CdrListResult.CdrError;
import org.meveo.apiv2.billing.ChargeCdrListInput;
import org.meveo.apiv2.billing.ChargeCdrListResult;
import org.meveo.apiv2.billing.RegisterCdrListModeEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.ICdrCsvReader;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;
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

    @Inject
    @RejectedCDR
    private Event<Serializable> rejectededCdrEventProducer;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/job_executor")
    protected ManagedExecutorService executor;

    @Inject
    protected CurrentUserProvider currentUserProvider;

    @Inject
    private UsageRatingService usageRatingService;

    @EJB
    private MediationApiService thisNewTX;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public CdrListResult registerCdrList(CdrListInput postData, String ipAddress) {

        validate(postData);

        List<String> cdrLines = postData.getCdrs();
        RegisterCdrListModeEnum mode = postData.getMode();

        CdrListResult cdrListResult = new CdrListResult(mode, cdrLines.size());

        final ICdrCsvReader cdrReader = cdrParsingService.getCDRReader(currentUser.getUserName(), ipAddress);

        final ICdrParser cdrParser = cdrParsingService.getCDRParser(null);

        boolean isDuplicateCheckOn = cdrParser.isDuplicateCheckOn();

        int nbThreads = mode == PROCESS_ALL ? Runtime.getRuntime().availableProcessors() : 1;

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads);
        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        final SynchronizedIterator<String> cdrLineIterator = new SynchronizedIterator<String>(cdrLines);

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                Thread.currentThread().setName("MediationApi" + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);
                thisNewTX.registerCDRsInTx(cdrLineIterator, cdrReader, cdrParser, isDuplicateCheckOn, cdrListResult);

            });
        }

        for (Runnable task : tasks) {
            futures.add(executor.submit(task));
        }

        boolean wasKilled = false;

        // Wait for all async methods to finish
        for (Future future : futures) {
            try {
                future.get();

            } catch (InterruptedException | CancellationException e) {
                wasKilled = true;

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                log.error("Failed to execute Mediation API async method", cause);
            }
        }

        return cdrListResult;
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registerCDRsInTx(Iterator<String> cdrLineIterator, ICdrReader cdrReader, ICdrParser cdrParser, boolean isDuplicateCheckOn, CdrListResult cdrProcessingResult) {

        while (true) {

            String cdrLine = cdrLineIterator.next();
            if (cdrLine == null) {
                break;
            }

            CDR cdr = cdrReader.getRecord(cdrParser, cdrLine);
            if (cdr == null) {
                break;
            }

            if (cdr.getRejectReason() == null) {
                try {
                    if (isDuplicateCheckOn) {
                        cdrParser.deduplicate(cdr);
                    }
                    List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
                    List<EDR> edrs = cdrParser.convertCdrToEdr(cdr, accessPoints);

                    cdrParsingService.createEdrs(edrs, cdr);

                    addEdrIds(cdrProcessingResult, edrs);

                } catch (Exception e) {
                    cdr.setRejectReasonException(e);
                }
            }

            if (cdr.getStatus() == CDRStatusEnum.ERROR) {

                String errorReason = cdr.getRejectReason();
                if (cdr.getRejectReasonException() != null) {
                    Exception e = cdr.getRejectReasonException();
                    final Throwable rootCause = MediationJobBean.getRootCause(e);
                    if (e instanceof EJBTransactionRolledbackException && rootCause instanceof ConstraintViolationException) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Invalid values passed: ");
                        for (ConstraintViolation<?> violation : ((ConstraintViolationException) rootCause).getConstraintViolations()) {
                            builder.append(
                                String.format(" %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()));
                        }
                        errorReason = builder.toString();
                        cdr.setRejectReason(errorReason);
                        log.error("Failed to process a CDR line: {} from api. Reason: {}", cdr.getLine(), errorReason);
                    } else if (e instanceof CDRParsingException) {
                        log.error("Failed to process a CDR line: {} from api. Reason: {}", cdr.getLine(), errorReason);
                    } else {
                        log.error("Failed to process a CDR line: {} from api. Reason: {}", cdr.getLine(), errorReason, e);
                    }
                } else {
                    log.error("Failed to process a CDR line: {} from api. Reason: {}", cdr.getLine(), errorReason);
                }

                if (cdrProcessingResult.getMode() == ROLLBACK_ON_ERROR) {
                    cdrProcessingResult.getEdrIds().clear();
                }

                cdrProcessingResult.getStatistics().addFail();
                cdrProcessingResult.getErrors().add(new CdrError(cdr.getRejectReasonException() != null ? cdr.getRejectReasonException().getClass().getSimpleName() : null, cdr.getRejectReason(), cdr.getLine()));

                if (cdrProcessingResult.getMode() == ROLLBACK_ON_ERROR) {
                    if (cdr.getRejectReasonException() != null && cdr.getRejectReasonException() instanceof BusinessException) {
                        throw (BusinessException) cdr.getRejectReasonException();
                    } else {
                        throw new BusinessException(cdr.getRejectReason());
                    }
                }

                rejectededCdrEventProducer.fire(cdr);
                cdrService.createOrUpdateCdr(cdr);

                if (cdrProcessingResult.getMode() == PROCESS_ALL) {
                    continue;
                }
                if (cdrProcessingResult.getMode() == STOP_ON_FIRST_FAIL) {
                    break;
                }

            } else {
                cdrProcessingResult.getStatistics().addSuccess();
            }
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void chargeCDRsInTx(SynchronizedIterator<String> cdrLineIterator, ICdrReader cdrReader, ICdrParser cdrParser, boolean isDuplicateCheckOn, boolean isVirtual, boolean isRateTriggeredEdr, Integer maxDepth,
            boolean isReturnWalletOperations, ChargeCdrListResult cdrProcessingResult) {

        while (true) {

            SynchronizedIterator<String>.NextItem<String> nextCDR = cdrLineIterator.nextWPosition();
            if (nextCDR == null) {
                break;
            }
            int position = nextCDR.getPosition();
            String cdrLine = nextCDR.getValue();

            CDR cdr = cdrReader.getRecord(cdrParser, cdrLine);
            if (cdr == null) {
                break;
            }

            if (cdr.getRejectReason() == null) {
                try {
                    if (isDuplicateCheckOn) {
                        cdrParser.deduplicate(cdr);
                    }
                    List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
                    List<EDR> edrs = cdrParser.convertCdrToEdr(cdr, accessPoints);
                    if (!isVirtual) {
                        cdrParsingService.createEdrs(edrs, cdr);
                    }

                    List<WalletOperation> walletOperations = new ArrayList<>();
                    for (EDR edr : edrs) {
                        List<WalletOperation> wo = usageRatingService.rateUsageWithinTransaction(edr, isVirtual, isRateTriggeredEdr, maxDepth, 0);

                        if (wo != null) {
                            walletOperations.addAll(wo);
                        }
                    }

                    cdrProcessingResult.addChargedCdr(position, createChargeCDRResultDto(walletOperations, isReturnWalletOperations));

                } catch (Exception e) {
                    cdr.setRejectReasonException(e);
                }
            }

            if (cdr.getStatus() == CDRStatusEnum.ERROR) {

                String errorReason = cdr.getRejectReason();
                if (cdr.getRejectReasonException() != null) {
                    Exception e = cdr.getRejectReasonException();
                    final Throwable rootCause = MediationJobBean.getRootCause(e);
                    if (e instanceof EJBTransactionRolledbackException && rootCause instanceof ConstraintViolationException) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("Invalid values passed: ");
                        for (ConstraintViolation<?> violation : ((ConstraintViolationException) rootCause).getConstraintViolations()) {
                            builder.append(
                                String.format(" %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()));
                        }
                        errorReason = builder.toString();
                        cdr.setRejectReason(errorReason);
                        log.error("Failed to process a CDR line: {} from api. Reason: {}", cdr.getLine(), errorReason);
                    } else if (e instanceof CDRParsingException) {
                        log.error("Failed to process a CDR line: {} from api. Reason: {}", cdr.getLine(), errorReason);
                    } else {
                        log.error("Failed to process a CDR line: {} from api. Reason: {}", cdr.getLine(), errorReason, e);
                    }
                } else {
                    log.error("Failed to process a CDR line: {} from api. Reason: {}", cdr.getLine(), errorReason);
                }

                if (cdrProcessingResult.getMode() == ROLLBACK_ON_ERROR) {
                    cdrProcessingResult.setChargedCDRs(new ChargeCDRResponseDto[1]);
                }

                cdrProcessingResult.getStatistics().addFail();
                cdrProcessingResult.addChargedCdr(position,
                    new ChargeCDRResponseDto(new CdrError(cdr.getRejectReasonException() != null ? cdr.getRejectReasonException().getClass().getSimpleName() : null, cdr.getRejectReason(), cdr.getLine())));

                if (cdrProcessingResult.getMode() == ROLLBACK_ON_ERROR) {
                    if (cdr.getRejectReasonException() != null && cdr.getRejectReasonException() instanceof BusinessException) {
                        throw (BusinessException) cdr.getRejectReasonException();
                    } else {
                        throw new BusinessException(cdr.getRejectReason());
                    }
                }

                rejectededCdrEventProducer.fire(cdr);
                cdrService.createOrUpdateCdr(cdr);

                if (cdrProcessingResult.getMode() == PROCESS_ALL) {
                    continue;
                }
                if (cdrProcessingResult.getMode() == STOP_ON_FIRST_FAIL) {
                    break;
                }

            } else {
                cdrProcessingResult.getStatistics().addSuccess();
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ChargeCdrListResult chargeCdrList(ChargeCdrListInput postData, String ipAddress) {

        validate(postData);

        List<String> cdrLines = postData.getCdrs();
        RegisterCdrListModeEnum mode = postData.getMode();

        ChargeCdrListResult cdrListResult = new ChargeCdrListResult(mode, cdrLines.size());

        final ICdrCsvReader cdrReader = cdrParsingService.getCDRReader(currentUser.getUserName(), ipAddress);

        final ICdrParser cdrParser = cdrParsingService.getCDRParser(null);

        boolean isDuplicateCheckOn = cdrParser.isDuplicateCheckOn();

        int nbThreads = mode == PROCESS_ALL ? Runtime.getRuntime().availableProcessors() : 1;

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads);
        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        final SynchronizedIterator<String> cdrLineIterator = new SynchronizedIterator<String>(cdrLines);

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                Thread.currentThread().setName("MediationApi" + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);
                thisNewTX.chargeCDRsInTx(cdrLineIterator, cdrReader, cdrParser, isDuplicateCheckOn, postData.isVirtual(), postData.isRateTriggeredEdr(), postData.getMaxDepth(), postData.isReturnWalletOperations(),
                    cdrListResult);

            });
        }

        for (Runnable task : tasks) {
            futures.add(executor.submit(task));
        }

        boolean wasKilled = false;

        // Wait for all async methods to finish
        for (Future future : futures) {
            try {
                future.get();

            } catch (InterruptedException | CancellationException e) {
                wasKilled = true;

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                log.error("Failed to execute Mediation API async method", cause);
            }
        }

        return cdrListResult;
    }

    private void addEdrIds(CdrListResult cdrListResult, List<EDR> edrs) {
        for (EDR edr : edrs) {
            cdrListResult.getEdrIds().add(edr.getId());
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

    private List<WalletOperation> rateUsage(EDR edr, ChargeCDRDto chargeCDRDto) throws MeveoApiException {

        List<WalletOperation> walletOperations = null;
        try {
            walletOperations = usageRatingService.rateUsageWithinTransaction(edr, chargeCDRDto.isVirtual(), chargeCDRDto.isRateTriggeredEdr(), chargeCDRDto.getMaxDepth(), 0);

        } catch (InsufficientBalanceException e) {
            log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());
            throw new MeveoApiException(MeveoApiErrorCodeEnum.INSUFFICIENT_BALANCE, e.getMessage());

        } catch (RatingException e) {
            log.trace("Failed to rate EDR {}: {}", edr, e.getRejectionReason());
            throw new MeveoApiException(MeveoApiErrorCodeEnum.RATING_REJECT, e.getMessage());

        } catch (BusinessException e) {
            log.error("Failed to rate EDR {}: {}", edr, e.getMessage(), e);
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, e.getMessage());
        }

        return walletOperations;
    }

    private ChargeCDRResponseDto createChargeCDRResultDto(List<WalletOperation> walletOperations, boolean returnWalletOperations) {

        ChargeCDRResponseDto result = new ChargeCDRResponseDto();
        BigDecimal amountWithTax = BigDecimal.ZERO;
        BigDecimal amountWithoutTax = BigDecimal.ZERO;
        BigDecimal amountTax = BigDecimal.ZERO;
        for (WalletOperation walletOperation : walletOperations) {
            if (returnWalletOperations) {
                WalletOperationDto walletOperationDto = new WalletOperationDto(walletOperation);
                result.getWalletOperations().add(walletOperationDto);
            }
            amountWithTax = amountWithTax.add(walletOperation.getAmountWithTax() != null ? walletOperation.getAmountWithTax() : BigDecimal.ZERO);
            amountWithoutTax = amountWithoutTax.add(walletOperation.getAmountWithoutTax() != null ? walletOperation.getAmountWithoutTax() : BigDecimal.ZERO);
            amountTax = amountTax.add(walletOperation.getAmountTax() != null ? walletOperation.getAmountTax() : BigDecimal.ZERO);
        }
        if (returnWalletOperations) {
            result.setWalletOperationCount(result.getWalletOperations().size());
        }
        result.setAmountTax(amountTax);
        result.setAmountWithoutTax(amountWithoutTax);
        result.setAmountWithTax(amountWithTax);
        return result;
    }
}