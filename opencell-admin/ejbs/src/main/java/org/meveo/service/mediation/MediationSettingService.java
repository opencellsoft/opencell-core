package org.meveo.service.mediation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.ReratingService;

@Stateless
public class MediationSettingService extends PersistenceService<MediationSetting> {

    @Inject
    private ReratingService reratingService;

    // Use MEDIATION_SETTING_ID to remember the mediation setting and look it up by ID next time.
    private static Long MEDIATION_SETTING_ID = null;

    public String getEventKeyFromEdrVersionRule(EDR edr) {
        MediationSetting mediationSetting = getMediationSetting();
        if (mediationSetting == null || !mediationSetting.isEnableEdrVersioning()) {
            return null;
        }
        Comparator<EdrVersioningRule> sortByPriority = (EdrVersioningRule edrV1, EdrVersioningRule edrV2) -> edrV1.getPriority().compareTo(edrV2.getPriority());
        return Optional.ofNullable(mediationSetting.getRules().stream().sorted(sortByPriority).filter(edrVersion -> {
            try {
                return ValueExpressionWrapper.evaluateExpression(edrVersion.getCriteriaEL(), Boolean.class, edr);
            } catch (Exception e) {
                log.warn("cant evaluate expression : " + edrVersion.getCriteriaEL(), e);
            }
            return false;
        }).findFirst()).map(edrVersion -> {
            try {
                return edrVersion != null ? ValueExpressionWrapper.evaluateExpression(edrVersion.get().getKeyEL(), String.class, edr) : null;
            } catch (Exception e) {
                log.warn("cant evaluate expression : " + edrVersion.get().getKeyEL(), e);
            }
            return null;
        }).get();
    }

    public void applyEdrVersioningRule(List<EDR> edrs, CDR cdr, boolean isTriggeredEdr) {

        MediationSetting mediationSetting = getMediationSetting();
        if (mediationSetting == null || !mediationSetting.isEnableEdrVersioning()) {
            return;
        }

        Iterator<EDR> edrIterate = edrs.iterator();
        while (edrIterate.hasNext()) {
            EDR edr = edrIterate.next();

            String errorMessage = "Error evaluating %s  [id= %d, \"%s\"] for CDR: [%s] : %s";
            Optional<EdrVersioningRule> edrVersionRuleOption = mediationSetting.getRules().stream().filter(edrVersion -> {
                String errorMsg = String.format(errorMessage, "criteriaEL", edrVersion.getId(), edrVersion.getCriteriaEL(), isTriggeredEdr ? "FROM_TRIGGERED_EDR : " + edr : cdr, "%s");
                Boolean eval = (Boolean) evaluateEdrVersion(edrVersion.getId(), edrVersion.getCriteriaEL(), edr, cdr, errorMsg, Boolean.class, edrIterate);
                return eval == null ? false : eval;
            }).findFirst();

            if (edrVersionRuleOption.isPresent()) {
                EdrVersioningRule edrVersionRule = edrVersionRuleOption.get();
                String errorMsg = String.format(errorMessage, "eventKeyEl", edrVersionRule.getId(), edrVersionRule.getCriteriaEL(), isTriggeredEdr ? "FROM_TRIGGERED_EDR : " + edr : cdr, "%s");
                String keyEvent = (String) evaluateEdrVersion(edrVersionRule.getId(), edrVersionRule.getKeyEL(), edr, cdr, errorMsg, String.class, edrIterate);

                if (StringUtils.isNotEmpty(keyEvent) && edr.getRejectReason() == null) { // test si cdr est rejete
                    edr.setEventKey(keyEvent);
                    errorMsg = String.format(errorMessage, "isNewVersionEL", edrVersionRule.getId(), edrVersionRule.getCriteriaEL(), cdr, "%s");

                    List<EDR> previousEdrs = this.findByEventKey(keyEvent);

                    if (CollectionUtils.isEmpty(previousEdrs)) {
                        edr.setEventVersion(1);
                        continue;
                    }

                    EDR previousEdr = previousEdrs.get(0);
                    if (previousEdr.getStatus() == EDRStatusEnum.CANCELLED) {
                        edr.setEventVersion(previousEdr.getEventVersion() + 1);
                        continue;
                    }
                    boolean isNewVersion = (boolean) evaluateEdrVersion(edrVersionRule.getId(), edrVersionRule.getIsNewVersionEL(), edr, cdr, errorMsg, Boolean.class, previousEdr, edrIterate);

                    if (isNewVersion) {

                        // When previous EDR with status : OPEN, REJECTED
                        if (previousEdr.getStatus() != EDRStatusEnum.RATED) { // all status : OPEN, REJECTED
                            previousEdr.setStatus(EDRStatusEnum.CANCELLED);
                            previousEdr.setRejectReason("Received new version EDR[id=" + edr.getId() + "]");
                            edr.setEventVersion(previousEdr.getEventVersion() + 1);

                            // When previous EDR with status RATED
                        } else {

                            // Validate if Wallet operations, derived from a given EDR, contain any billed Rated Transactions and if not - cancel all triggered EDRs, WO, discount WO, RTs and adjust Invoice line amounts
                            // and quantities
                            boolean derivedWOwasCanceled = reratingService.validateAndCancelDerivedWosEdrsAndRts(previousEdr);

                            if (derivedWOwasCanceled) {

                                previousEdr.setStatus(EDRStatusEnum.CANCELLED);
                                previousEdr.setRejectReason("Received new version EDR[id=" + edr.getId() + "]");
                                edr.setEventVersion(previousEdr.getEventVersion() + 1);

                            } else {

                                if (cdr != null) {
                                    cdr.setStatus(CDRStatusEnum.DISCARDED);
                                    cdr.setRejectReason("EDR[id=" + previousEdr.getId() + ", eventKey=" + keyEvent + "] has already been invoiced");
                                }
                                edr.setStatus(EDRStatusEnum.CANCELLED);
                                edr.setRejectReason("EDR[id=" + previousEdr.getId() + ", eventKey=" + keyEvent + "] has already been invoiced");

                                if (edr.getId() != null) {
                                    edrService.remove(edr);
                                }
                                edrIterate.remove();
                            }
                        }
                    } else {
                        if (cdr != null) {
                            cdr.setStatus(CDRStatusEnum.DISCARDED);
                            String msgError = "Newer version already exists EDR[id=" + previousEdrs.get(0).getId() + "]";
                            cdr.setRejectReason(msgError);
                        }

                        edr.setStatus(EDRStatusEnum.CANCELLED);
                        edr.setRejectReason("EDR[id=" + previousEdr.getId() + ", eventKey=" + keyEvent + "] has already been invoiced");

                        if (edr.getId() != null) {
                            edrService.remove(edr);
                        }
                        edrIterate.remove();
                    }
                }
            }
        }
    }

    private Object evaluateEdrVersion(Long idEdrVersion, String expression, EDR edr, CDR cdr, String msg, Class<?> result, EDR previousEdr, Iterator<EDR> edrIterate) {
        Object evaluted = null;
        Map<Object, Object> context = new HashMap<>();
        context.put("edr", edr);
        if (previousEdr != null)
            context.put("previous", previousEdr);
        try {
            evaluted = ValueExpressionWrapper.evaluateExpression(expression, context, result);
        } catch (Exception e) {
            msg = String.format(msg, e.getMessage());
            if (cdr != null) {
                cdr.setRejectReason(msg);
                cdr.setStatus(CDRStatusEnum.ERROR);
            }
            if (edr.getId() != null) {
                edrService.remove(edr);
            }
            edrIterate.remove();
        }
        return evaluted;
    }

    @Inject
    private EdrService edrService;

    private Object evaluateEdrVersion(Long idEdrVersion, String expression, EDR edr, CDR cdr, String msg, Class<?> result, Iterator<EDR> edrIterate) {
        return evaluateEdrVersion(idEdrVersion, expression, edr, cdr, msg, result, null, edrIterate);
    }

    @SuppressWarnings("unchecked")
    public List<EDR> findByEventKey(String eventKey) {
        return getEntityManager().createNamedQuery("EDR.findEDREventVersioning").setParameter("eventKey", eventKey).setMaxResults(1).getResultList();
    }

    /**
     * Get a Mediation Setting - there should be only one in the system. Use MEDIATION_SETTING_ID to remember the mediation setting and look it up by ID next time.
     * 
     * @return Mediation setting
     */
    private MediationSetting getMediationSetting() {
        MediationSetting mediationSetting = null;

        // No mediations setting was looked up yet
        if (MEDIATION_SETTING_ID == null) {
            List<MediationSetting> mediationSettings = this.list();
            if (mediationSettings.isEmpty()) {
                return null;
            }
            mediationSetting = mediationSettings.get(0);
            MEDIATION_SETTING_ID = mediationSetting.getId();
        } else {
            mediationSetting = findById(MEDIATION_SETTING_ID);
        }
        return mediationSetting;
    }
}
