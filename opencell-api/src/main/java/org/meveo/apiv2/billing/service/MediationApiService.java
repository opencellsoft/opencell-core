package org.meveo.apiv2.billing.service;

import static org.meveo.apiv2.billing.ProcessCdrListModeEnum.PROCESS_ALL;
import static org.meveo.apiv2.billing.ProcessCdrListModeEnum.ROLLBACK_ON_ERROR;
import static org.meveo.apiv2.billing.ProcessCdrListModeEnum.STOP_ON_FIRST_FAIL;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.BadRequestException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.MediationJobBean;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto.CdrError;
import org.meveo.api.dto.billing.CounterPeriodDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.ChargeCdrListInput;
import org.meveo.apiv2.billing.ProcessCdrListModeEnum;
import org.meveo.apiv2.billing.ProcessCdrListResult;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.Reservation;
import org.meveo.model.billing.ReservationStatus;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.mediation.MediationsettingService;
import org.meveo.service.medina.impl.CDRAlreadyProcessedException;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.DuplicateException;
import org.meveo.service.medina.impl.ICdrCsvReader;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;
import org.meveo.util.ApplicationProvider;
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
    private CounterInstanceService counterInstanceService;

    @Inject
    @RejectedCDR
    private Event<Serializable> rejectededCdrEventProducer;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/job_executor")
    protected ManagedExecutorService executor;

    @Inject
    protected CurrentUserProvider currentUserProvider;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private MethodCallingUtils methodCallingUtils;

    @Inject
    private EdrService edrService;

    @Resource
    private TimerService timerService;

    @EJB
    private MediationApiService thisNewTX;

    @Inject
    private MediationsettingService mediationsettingService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ProcessCdrListResult registerCdrList(CdrListInput postData, String ipAddress) {

        validate(postData);
        return processCdrList(postData.getCdrs(), postData.getMode(), false, false, false, false, null, false, false, true, false, ipAddress, false);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<CDR> processCdrList(List<Long> cdrIds) throws CDRAlreadyProcessedException {
        List<CDR> cdrs = cdrService.findByIds(cdrIds);
        for (CDR cdr : cdrs) {
            if (!StringUtils.isBlank(cdr.getRejectReason())) {
                cdr.setStatus(CDRStatusEnum.ERROR);
                rejectededCdrEventProducer.fire(cdr);
                cdrService.createOrUpdateCdr(cdr);
            } else if (cdr.getStatus() == CDRStatusEnum.PROCESSED) {
                throw new CDRAlreadyProcessedException("CDR id="+cdr.getId()+" is already processed");
            } else {
                try {
                    List<Access> accessPoints = cdrParsingService.accessPointLookup(cdr);
                    List<EDR> edrs = cdrParsingService.convertCdrToEdr(cdr, accessPoints);

                    if (EdrService.isDuplicateCheckOn() && edrService.isDuplicateFound(cdr.getOriginRecord())) {
                         throw new DuplicateException(cdr);
                    }

                    for (EDR edr : edrs) {
                        edrService.create(edr);
                        cdr.setHeaderEDR(edr);
                        cdr.setStatus(CDRStatusEnum.PROCESSED);
                        cdrService.update(cdr);
                    }

                    mediationsettingService.applyEdrVersioningRule(edrs, cdr, false);
                    if (!StringUtils.isBlank(cdr.getRejectReason())) {
                        cdr.setStatus(cdr.getStatus());
                        rejectededCdrEventProducer.fire(cdr);
                        cdrService.createOrUpdateCdr(cdr);
                    }
                } catch(Exception ex) {
                    cdr.setStatus(CDRStatusEnum.ERROR);
                    cdr.setRejectReason(ex.getMessage());
                    rejectededCdrEventProducer.fire(cdr);
                    cdrService.createOrUpdateCdr(cdr);
                }
            }
        }
        return cdrs;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ProcessCdrListResult reserveCdrList(CdrListInput postData, String ipAddress) {

        validate(postData);
        return processCdrList(postData.getCdrs(), postData.getMode(), false, false, true, false, null, false, false, true, false, ipAddress, false);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ProcessCdrListResult chargeCdrList(ChargeCdrListInput postData, String ipAddress) {

        validate(postData);
        return processCdrList(postData.getCdrs(), postData.getMode(), postData.isVirtual(), true, false, postData.isRateTriggeredEdr(), postData.getMaxDepth(), postData.isReturnWalletOperations(),
            postData.isReturnWalletOperationDetails(), postData.isReturnEDRs(), postData.isReturnCounters(), ipAddress, postData.isGenerateRTs());
    }

    /**
     * Process CDR lines: create EDRs and rate them
     * 
     * @param cdrLines CDR lines to process
     * @param mode Process interruption mode
     * @param isVirtual Are operations virtual
     * @param rate Should EDRs be rated
     * @param reserve Is this a reservation for prepaid charges
     * @param rateTriggeredEdr Should triggered EDRs be rated
     * @param maxDepth The max deep used in triggered EDR
     * @param returnWalletOperations Return wallet operation ids
     * @param returnWalletOperationDetails Return wallet operation details
     * @param returnEDRs Return EDR ids
     * @param returnCounters Return counter updates
     * @param ipAddress IP address from the request
     * @param generateRTs Generate automatically RTs
     * @return CDR processing result
     */
    @SuppressWarnings("rawtypes")
    private ProcessCdrListResult processCdrList(List<String> cdrLines, ProcessCdrListModeEnum mode, boolean isVirtual, boolean rate, boolean reserve, boolean rateTriggeredEdr, Integer maxDepth,
            boolean returnWalletOperations, boolean returnWalletOperationDetails, boolean returnEDRs, boolean returnCounters, String ipAddress, boolean generateRTs) {

        ProcessCdrListResult cdrListResult = new ProcessCdrListResult(mode, cdrLines.size());

        final ICdrCsvReader cdrReader = cdrParsingService.getCDRReader(currentUser.getUserName(), ipAddress);

        final ICdrParser cdrParser = cdrParsingService.getCDRParser(null);

        boolean isDuplicateCheckOn = cdrParser.isDuplicateCheckOn();

        int nbThreads = mode == PROCESS_ALL ? Runtime.getRuntime().availableProcessors() : 1;
        if (nbThreads > cdrLines.size()) {
            nbThreads = cdrLines.size();
        }

        List<Runnable> tasks = new ArrayList<Runnable>(nbThreads);
        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        final SynchronizedIterator<String> cdrLineIterator = new SynchronizedIterator<String>(cdrLines);

        final Map<String, List<CounterPeriod>> virtualCounters = new HashMap<String, List<CounterPeriod>>();
        final Map<String, List<CounterPeriod>> counterUpdates = new HashMap<String, List<CounterPeriod>>();

        counterInstanceService.reestablishCounterTracking(virtualCounters, counterUpdates);

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                Thread.currentThread().setName("MediationApi" + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);
                thisNewTX.processCDRs(cdrLineIterator, cdrReader, cdrParser, isDuplicateCheckOn, isVirtual, rate, reserve, rateTriggeredEdr, maxDepth, returnWalletOperations, returnWalletOperationDetails, returnEDRs,
                    cdrListResult, virtualCounters, counterUpdates, generateRTs);

            });
        }

        for (Runnable task : tasks) {
            futures.add(executor.submit(task));
        }

        // Wait for all async methods to finish
        for (Future future : futures) {
            try {
                future.get();

            } catch (InterruptedException | CancellationException e) {
//                wasKilled = true;

            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                log.error("Failed to execute Mediation API async method", cause);
            }
        }

        // Gather counter update summary information
        if (returnCounters) {
            List<CounterPeriod> counterPeriods = counterInstanceService.getCounterUpdates();
            if (counterPeriods != null) {
                counterPeriods.sort(Comparator.comparing(CounterPeriod::getPeriodStartDate));
                cdrListResult.setCounterPeriods(counterPeriods.stream().map(CounterPeriodDto::new).collect(Collectors.toList()));
            }
        }

        return cdrListResult;
    }

    /**
     * Process CDRs in a new transaction
     * 
     * @param cdrLineIterator Iterator over CDRs to process
     * @param cdrReader CDR reader
     * @param cdrParser CDR parser
     * @param isDuplicateCheckOn Is duplication check required
     * @param isVirtual Is this is virtual charging - neither CDR, EDR nor wallet operations will be persisted
     * @param rate Shall EDRs be rated - wallet operations be created
     * @param reserve Is this a reservation
     * @param rateTriggeredEdrs In case of rating, shall Triggered EDRs be rated as well
     * @param maxDepth Max depth to rate of triggered EDRs
     * @param returnWalletOperations Shall wallet operation details be returned
     * @param cdrProcessingResult CDR processing result tracking
     * @param virtualCounters Virtual counters
     * @param counterUpdates Counter update tracking
     * @param generateRTs generate automatically RTs
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processCDRs(SynchronizedIterator<String> cdrLineIterator, ICdrReader cdrReader, ICdrParser cdrParser, boolean isDuplicateCheckOn, boolean isVirtual, boolean rate, boolean reserve,
            boolean rateTriggeredEdrs, Integer maxDepth, boolean returnWalletOperations, boolean returnWalletOperationDetails, boolean returnEDRs, ProcessCdrListResult cdrProcessingResult,
            Map<String, List<CounterPeriod>> virtualCounters, Map<String, List<CounterPeriod>> counterUpdates, boolean generateRTs) {

        counterInstanceService.reestablishCounterTracking(virtualCounters, counterUpdates);

        // In case of no need to rollback, an error will be recorded directly in EDR
        boolean noNeedToRollback = false;

        String originRecordEL = appProvider.getCdrDeduplicationKeyEL();

        while (true) {

            SynchronizedIterator<String>.NextItem<String> nextCDR = cdrLineIterator.nextWPosition();
            if (nextCDR == null) {
                break;
            }
            int position = nextCDR.getPosition();
            String cdrLine = nextCDR.getValue();

            CDR cdr = cdrReader.getRecord(cdrParser, cdrLine, originRecordEL);
            if (cdr == null) {
                break;
            }

            List<EDR> edrs = null;
            List<WalletOperation> walletOperations = new ArrayList<>();
            List<Reservation> reservations = new ArrayList<>();

            if (cdr.getRejectReason() == null) {
                try {

                    List<Access> accessPoints = cdrParser.accessPointLookup(cdr);
                    edrs = cdrParser.convertCdrToEdr(cdr, accessPoints);
                    if (!isVirtual) {
                        if (isDuplicateCheckOn) {
                            cdrParser.deduplicate(cdr);
                        }
                        cdrParsingService.createEdrs(edrs, cdr);
                    }
                   final boolean isVirtualTemp = mediationsettingService.applyEdrVersioningRule(edrs, cdr, isVirtual);
                    // Convert CDR to EDR and create a reservation
                    if (reserve) {

                        // TODO this could be a problem if one CDR results in multiple EDRs and some fail to rate, while others are rated successfully
                        for (EDR edr : edrs) {
                            Reservation reservation = null;
                            // For ROLLBACK_ON_ERROR mode, processing is called within TX, so when error is thrown up, everything will rollback
                            if (cdrProcessingResult.getMode() == ROLLBACK_ON_ERROR) {
                                reservation = usageRatingService.reserveUsageWithinTransaction(edr);
                                // For other cases, rate each EDR in a separate TX
                            } else {
                                reservation = methodCallingUtils.callCallableInNewTx(() -> usageRatingService.reserveUsageWithinTransaction(edr));
                            }

                            if (edr.getRejectReason() != null) {
                                cdr.setRejectReason(edr.getRejectReason());
                                cdr.setStatus(CDRStatusEnum.ERROR);
                            } else {
                                reservations.add(reservation);

                            }
                            // schedule cancellation at expire
                            TimerConfig timerConfig = new TimerConfig();
                            Object[] objs = { reservation.getId(), currentUser };
                            timerConfig.setInfo(objs);
                            Timer timer = timerService.createSingleActionTimer(appProvider.getPrepaidReservationExpirationDelayinMillisec(), timerConfig);
//                            timers.put(reservation.getId(), timer);

                        }

                        cdrProcessingResult.addChargedCdr(position, createChargeCDRResultDto(edrs, null, false, false, returnEDRs, reservations, null));

                        // Convert CDR to EDR and rate them
                    } else if (rate) {
                        log.debug("usageRatingService : " + usageRatingService + ", isVirtual : " + isVirtual + ", rateTriggeredEdrs : " + rateTriggeredEdrs + ", maxDepth : " + maxDepth);
                        for (EDR edr : edrs) {
                            RatingResult ratingResult = null;
                            // For ROLLBACK_ON_ERROR mode, processing is called within TX, so when error is thrown up, everything will rollback
                            if (cdrProcessingResult.getMode() == ROLLBACK_ON_ERROR) {
                                ratingResult = usageRatingService.rateUsage(edr, isVirtual, rateTriggeredEdrs, maxDepth, 0, null, true);
                                if (ratingResult.getRatingException() != null) {
                                    throw ratingResult.getRatingException();
                                }
                                if (ratingResult.getWalletOperations() != null) {
                                    walletOperations.addAll(ratingResult.getWalletOperations());
                                }
                                // For STOP_ON_FIRST_FAIL or PROCESS_ALL model if no rollback is needed (no additional unforeseen data can be created/updated during rating)
                                // when rating fails, error is not thrown but is simply handled
                            } else if (noNeedToRollback) {
                                ratingResult = usageRatingService.rateUsage(edr, isVirtual, rateTriggeredEdrs, maxDepth, 0, null, noNeedToRollback);
                                if (ratingResult.getRatingException() != null) {
                                    throw ratingResult.getRatingException();
                                }
                                if (ratingResult.getWalletOperations() != null) {
                                    walletOperations.addAll(ratingResult.getWalletOperations());
                                }

                                // For STOP_ON_FIRST_FAIL or PROCESS_ALL model if rollback is needed, rating is called in a new TX and will rollback
                            } else {
                                ratingResult = methodCallingUtils.callCallableInNewTx(() -> usageRatingService.rateUsage(edr, isVirtualTemp, rateTriggeredEdrs, maxDepth, 0, null, false));

                                if (ratingResult.getWalletOperations() != null) {
                                    walletOperations.addAll(ratingResult.getWalletOperations());
                                }
                                
                            }
                        }
                        cdrProcessingResult.addChargedCdr(position, createChargeCDRResultDto(edrs, walletOperations, returnWalletOperations, returnWalletOperationDetails, returnEDRs, null, null));

                        // Just convert CDR to EDR - applies to non-virtual requests only
                    } else if (!isVirtual) {
                        cdrProcessingResult.addChargedCdr(position, createChargeCDRResultDto(edrs, null, false, false, returnEDRs, null, null));
                    }

                } catch (Exception e) {
                    cdr.setRejectReasonException(e);
                }
            }

            if (cdr.getStatus() == CDRStatusEnum.ERROR || cdr.getStatus() == CDRStatusEnum.DISCARDED) {

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

                // In case of rollback only an error will be reported, irrelevant of how many CDRs were processed already
                if (cdrProcessingResult.getMode() == ROLLBACK_ON_ERROR) {
                    cdrProcessingResult.setChargedCDRs(new ChargeCDRResponseDto[1]);
                    position = 0;
                }

                cdrProcessingResult.getStatistics().addFail();
                cdrProcessingResult.addChargedCdr(position, createChargeCDRResultDto(edrs, walletOperations, returnWalletOperations, returnWalletOperationDetails, returnEDRs, reservations,
                    new CdrError(cdr.getRejectReasonException() != null ? cdr.getRejectReasonException().getClass().getSimpleName() : null, cdr.getRejectReason(), cdr.getLine())));

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

                // Trim array to the last populated position, so not to return null elemenst in array more than needed
                if (cdrProcessingResult.getMode() == STOP_ON_FIRST_FAIL) {
                    cdrProcessingResult.setChargedCDRs(Arrays.copyOf(cdrProcessingResult.getChargedCDRs(), position + 1));
                    break;
                }

            } else {
                cdrProcessingResult.getStatistics().addSuccess();
            }

            // Generate automatically RTs
            if (generateRTs && !walletOperations.isEmpty()) {
                for (WalletOperation walletOperation : walletOperations) {
                    cdrParsingService.getEntityManager().persist(walletOperation.getEdr());
                    ratedTransactionService.createRatedTransaction(walletOperation, false);
                }
            }
            cdrProcessingResult.setAmountWithTax(BigDecimal.ZERO);
            cdrProcessingResult.setAmountWithoutTax(BigDecimal.ZERO);
            cdrProcessingResult.setAmountTax(BigDecimal.ZERO);
            cdrProcessingResult.setWalletOperationCount(0);
            Arrays.stream(cdrProcessingResult.getChargedCDRs()).forEach( cdrCharge -> {
                if (cdrCharge != null) {
                    cdrProcessingResult.setAmountWithTax(cdrProcessingResult.getAmountWithTax().add(cdrCharge.getAmountWithTax() != null
                            ? cdrCharge.getAmountWithTax() : BigDecimal.ZERO));
                    cdrProcessingResult.setAmountWithoutTax(cdrProcessingResult.getAmountWithoutTax().add(cdrCharge.getAmountWithoutTax() != null
                            ? cdrCharge.getAmountWithoutTax() : BigDecimal.ZERO));
                    cdrProcessingResult.setAmountTax(cdrProcessingResult.getAmountTax().add(cdrCharge.getAmountTax() != null
                            ? cdrCharge.getAmountTax() : BigDecimal.ZERO));
                    cdrProcessingResult.setWalletOperationCount(cdrProcessingResult.getWalletOperationCount() + (cdrCharge.getWalletOperationCount() != null
                            ? cdrCharge.getWalletOperationCount() : 0));
                } else {
                    log.warn("cdrProcessingResult amouts and WOCount will have default 0 value, due to cdrCharge null");
                }
            });
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

    private ChargeCDRResponseDto createChargeCDRResultDto(List<EDR> edrs, List<WalletOperation> walletOperations, boolean returnWalletOperations, boolean returnWalletOperationDetails, boolean returnEDRs,
            List<Reservation> reservations, CdrError cdrError) {

        ChargeCDRResponseDto result = new ChargeCDRResponseDto();

        result.setError(cdrError);

        if (returnEDRs && CollectionUtils.isNotEmpty(edrs) && edrs.get(0).getId() != null) {
            result.setEdrIds(new ArrayList<Long>(edrs.size()));
            for (EDR edr : edrs) {
                result.getEdrIds().add(edr.getId());
            }
        }
        if (walletOperations != null && !walletOperations.isEmpty()) {
            BigDecimal amountWithTax = BigDecimal.ZERO;
            BigDecimal amountWithoutTax = BigDecimal.ZERO;
            BigDecimal amountTax = BigDecimal.ZERO;
            for (WalletOperation walletOperation : walletOperations) {
                if (returnWalletOperationDetails) {
                    WalletOperationDto walletOperationDto = new WalletOperationDto(walletOperation);
                    result.getWalletOperations().add(walletOperationDto);

                } else if (returnWalletOperations && walletOperation.getId() != null) {
                    WalletOperationDto walletOperationDto = new WalletOperationDto();
                    walletOperationDto.setId(walletOperation.getId());
                    result.getWalletOperations().add(walletOperationDto);
                }
                amountWithTax = amountWithTax.add(walletOperation.getAmountWithTax() != null ? walletOperation.getAmountWithTax() : BigDecimal.ZERO);
                amountWithoutTax = amountWithoutTax.add(walletOperation.getAmountWithoutTax() != null ? walletOperation.getAmountWithoutTax() : BigDecimal.ZERO);
                amountTax = amountTax.add(walletOperation.getAmountTax() != null ? walletOperation.getAmountTax() : BigDecimal.ZERO);
            }
            result.setWalletOperationCount(walletOperations.size());
            result.setAmountTax(amountTax);
            result.setAmountWithoutTax(amountWithoutTax);
            result.setAmountWithTax(amountWithTax);
        }

        if (reservations != null) {
            result.setReservationIds(new ArrayList<Long>(reservations.size()));
            for (Reservation reservation : reservations) {
                result.getReservationIds().add(reservation.getId());
            }
        }

        return result;
    }

    @Timeout
    private void reservationExpired(Timer timer) {
        Object[] objs = (Object[]) timer.getInfo();
        try {
            Reservation reservation = reservationService.findById((Long) objs[0]);
            if (!ReservationStatus.CONFIRMED.equals(reservation.getStatus())) {
                reservationService.cancelPrepaidReservationInNewTransaction(reservation);
            }

        } catch (BusinessException e) {
            log.error("Failed to cancel Prepaid Reservation In New Transaction", e);
        }
    }
}