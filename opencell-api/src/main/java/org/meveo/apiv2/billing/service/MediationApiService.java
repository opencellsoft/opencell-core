package org.meveo.apiv2.billing.service;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.admin.exception.RatingException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRListResponseDto;
import org.meveo.api.dto.billing.ChargeCDRResponseDto;
import org.meveo.api.dto.billing.CounterPeriodDto;
import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.EDR;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.ICdrParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Stateless
public class MediationApiService {

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private CDRParsingService cdrParsingService;

    @Inject
    protected CurrentUserProvider currentUserProvider;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    private CounterInstanceService counterInstanceService;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/job_executor")
    protected ManagedExecutorService executor;

    @EJB
    private MediationApiService thisNewTX;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ChargeCDRListResponseDto processCdrList(ChargeCDRDto chargeCDRDto) {
        ChargeCDRListResponseDto cdrListResult = new ChargeCDRListResponseDto(chargeCDRDto.getCdrs().size());

        ICdrParser cdrParser = cdrParsingService.getCDRParser(null);

        int nbThreads = Runtime.getRuntime().availableProcessors();
//        int nbThreads = 1;

        List<Runnable> tasks = new ArrayList<>(nbThreads);
        List<Future> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();

        final SynchronizedIterator<String> cdrLineIterator = new SynchronizedIterator<>(chargeCDRDto.getCdrs());

        final Map<String, List<CounterPeriod>> virtualCounters = new HashMap<>();
        final Map<String, List<CounterPeriod>> counterUpdates = new HashMap<>();

        counterInstanceService.reestablishCounterTracking(virtualCounters, counterUpdates);

        for (int k = 0; k < nbThreads; k++) {

            int finalK = k;
            tasks.add(() -> {

                Thread.currentThread().setName("MediationApi" + "-" + finalK);

                currentUserProvider.reestablishAuthentication(lastCurrentUser);
                processCDRs(cdrLineIterator, cdrParser, chargeCDRDto.isVirtual(), chargeCDRDto.isRateTriggeredEdr(),
                        chargeCDRDto.getMaxDepth(), chargeCDRDto.isReturnWalletOperationDetails(), chargeCDRDto.isReturnWalletOperations(),
                        chargeCDRDto.isReturnEDRs(), cdrListResult, virtualCounters, counterUpdates);

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
        if (chargeCDRDto.isReturnCounters()) {
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
     * @param cdrParser CDR parser
     * @param isVirtual Is this is virtual charging - neither CDR, EDR nor wallet operations will be persisted
     * @param rateTriggeredEdrs In case of rating, shall Triggered EDRs be rated as well
     * @param maxDepth Max depth to rate of triggered EDRs
     * @param returnWalletOperationDetails Shall wallet operation details be returned
     * @param returnWalletOperations Shall wallet operation id be returned
     * @param returnEDRs Shall edr ids be returned
     * @param virtualCounters Virtual counters
     * @param counterUpdates Counter udpate tracking
     * @param cdrProcessingResult CDR processing result tracking
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void processCDRs(SynchronizedIterator<String> cdrLineIterator, ICdrParser cdrParser, boolean isVirtual,
                                boolean rateTriggeredEdrs, Integer maxDepth, boolean returnWalletOperationDetails,
                                boolean returnWalletOperations, boolean returnEDRs, ChargeCDRListResponseDto cdrProcessingResult,
                                Map<String, List<CounterPeriod>> virtualCounters, Map<String, List<CounterPeriod>> counterUpdates) {

        while (true) {

            SynchronizedIterator<String>.NextItem<String> nextCDR = cdrLineIterator.nextWPosition();
            if (nextCDR == null) {
                break;
            }
            int position = nextCDR.getPosition();
            String cdrLine = nextCDR.getValue();

            thisNewTX.processEachCDRInTx(cdrLine, cdrParser, position, isVirtual,
                    rateTriggeredEdrs, maxDepth, returnWalletOperationDetails,
                    returnWalletOperations, returnEDRs, cdrProcessingResult,
                    virtualCounters, counterUpdates);
        }
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processEachCDRInTx(String cdrLine, ICdrParser cdrParser, int position, boolean isVirtual,
                                   boolean rateTriggeredEdrs, Integer maxDepth, boolean returnWalletOperationDetails,
                                   boolean returnWalletOperations, boolean returnEDRs, ChargeCDRListResponseDto cdrProcessingResult,
                                   Map<String, List<CounterPeriod>> virtualCounters, Map<String, List<CounterPeriod>> counterUpdates) {

        counterInstanceService.reestablishCounterTracking(virtualCounters, counterUpdates);

        try {
//                CDR cdr = cdrParser.parse(cdrLine);
            CDR cdr = cdrParser.parseByApi(cdrLine, currentUser.getUserName(), null);
//                List<EDR> edrs = cdrParser.convertCdrToEdr(cdr, accessPoints);
            List<EDR> edrs = cdrParsingService.getEDRList(cdr);
            List<WalletOperation> walletOperations = new ArrayList<>();
            for (EDR edr : edrs) {
                log.debug("edr={}", edr);
                edr.setSubscription(edr.getSubscription());
                if (!isVirtual) {
                    edrService.create(edr);
                }
                List<WalletOperation> wo = rateUsage(edr, isVirtual, rateTriggeredEdrs, maxDepth);
                if (wo != null) {
                    walletOperations.addAll(wo);
                }
            }
            cdrProcessingResult.addChargedCdr(position, createChargeCDRResultDto(edrs, walletOperations, returnWalletOperationDetails, returnWalletOperations, returnEDRs));
            cdrProcessingResult.getStatistics().addSuccess();

        } catch (CDRParsingException e) {
            log.error("Error parsing cdr={}", e.getRejectionCause());

            cdrProcessingResult.getStatistics().addFail();
            cdrProcessingResult.addChargedCdr(position,
                    new ChargeCDRResponseDto(new ChargeCDRResponseDto.CdrError(e.getClass().getSimpleName(), e.getRejectionCause() != null ? e.getRejectionCause().toString() : e.getMessage(), cdrLine)));
        } catch (RatingException e) {
            log.error("Error rating cdr: {}", e.getRejectionReason(), e);

            cdrProcessingResult.getStatistics().addFail();
            cdrProcessingResult.addChargedCdr(position,
                    new ChargeCDRResponseDto(new ChargeCDRResponseDto.CdrError(e.getClass().getSimpleName(), e.getRejectionReason() != null ? e.getRejectionReason().toString() : e.getMessage(), cdrLine)));

        } catch (MeveoApiException e) {
            log.error("Error Code Meveo Api={}", e.getErrorCode());

            cdrProcessingResult.getStatistics().addFail();
            cdrProcessingResult.addChargedCdr(position,
                    new ChargeCDRResponseDto(new ChargeCDRResponseDto.CdrError(e.getErrorCode().toString(), e.getCause() != null ? e.getCause().toString() : e.getMessage(), cdrLine)));

        } catch (Exception e) {
            log.error("Error parsing cdr", e);

            cdrProcessingResult.getStatistics().addFail();
            cdrProcessingResult.addChargedCdr(position,
                    new ChargeCDRResponseDto(new ChargeCDRResponseDto.CdrError(e.getClass().getSimpleName(), e.getMessage(), cdrLine)));
        }
    }

    private List<WalletOperation> rateUsage(EDR edr, boolean isVirtual, boolean rateTriggeredEdrs, Integer maxDepth) throws MeveoApiException {

        List<WalletOperation> walletOperations = null;
        try {
            walletOperations = usageRatingService.rateUsageWithinTransaction(edr, isVirtual, rateTriggeredEdrs, maxDepth, 0);

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

    private ChargeCDRResponseDto createChargeCDRResultDto(List<EDR> edrs, List<WalletOperation> walletOperations,
                                                          boolean returnWalletOperationDetails, boolean returnWalletOperations, boolean returnEDRs) {

        ChargeCDRResponseDto result = new ChargeCDRResponseDto();

        if (edrs.get(0).getId() != null) {
            result.setEdrIds(new ArrayList<Long>(edrs.size()));
            if (returnEDRs) {
                for (EDR edr : edrs) {
                    result.getEdrIds().add(edr.getId());
                }
            }
        }
        if (walletOperations != null) {
            BigDecimal amountWithTax = BigDecimal.ZERO;
            BigDecimal amountWithoutTax = BigDecimal.ZERO;
            BigDecimal amountTax = BigDecimal.ZERO;
            for (WalletOperation walletOperation : walletOperations) {
                if (returnWalletOperationDetails) {
                    WalletOperationDto walletOperationDto = new WalletOperationDto(walletOperation);
                    result.getWalletOperations().add(walletOperationDto);
                }
                else {
                    if (returnWalletOperations) {
                        WalletOperationDto walletOperationDto = new WalletOperationDto(walletOperation);
                        result.getWalletOperations().add(new WalletOperationDto(walletOperationDto.getWalletId()));
                    }
                }
                amountWithTax = amountWithTax.add(walletOperation.getAmountWithTax() != null ? walletOperation.getAmountWithTax() : BigDecimal.ZERO);
                amountWithoutTax = amountWithoutTax.add(walletOperation.getAmountWithoutTax() != null ? walletOperation.getAmountWithoutTax() : BigDecimal.ZERO);
                amountTax = amountTax.add(walletOperation.getAmountTax() != null ? walletOperation.getAmountTax() : BigDecimal.ZERO);
            }
            if (returnWalletOperationDetails) {
                result.setWalletOperationCount(result.getWalletOperations().size());
            }
            result.setAmountTax(amountTax);
            result.setAmountWithoutTax(amountWithoutTax);
            result.setAmountWithTax(amountWithTax);
        }
        return result;
    }

}
