package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SubscriptionRenewalDto;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionStatusEnum;

public class SubscriptionMassImportScript extends GenericMassImportScript {

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "SUBSCRIPTION";
    private static final String ENTITY_NAME = "Subscription";

    public enum SubscriptionActionEnum {
        CREATE, UPDATE, SUSPEND, RESUME, ACTIVATE, TERMINATE
    }

    private final SubscriptionApi subscriptionApi = (SubscriptionApi) getServiceInterface(SubscriptionApi.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
            if (recordMap != null && !recordMap.isEmpty()) {
                // VALIDATE ENTITY
                String OC_ENTITY = (String) recordMap.get("OC_ENTITY");
                if (!ENTITY.equals(OC_ENTITY)) {
                    throw new ValidationException("value of OC_ENTITY is not correct: " + OC_ENTITY);
                }
                // VALIDATE ACTION
                String OC_ACTION = (String) recordMap.get("OC_ACTION");
                if (Stream.of(SubscriptionActionEnum.values()).noneMatch(e -> e.toString().equals(OC_ACTION))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
                }

                SubscriptionActionEnum action = SubscriptionActionEnum.valueOf(OC_ACTION);

                SubscriptionDto subscriptionDto = validateAndGetSubscription(action, recordMap);

                if (SubscriptionActionEnum.CREATE.equals(action)) {
                    setSubscriptionValues(recordMap, subscriptionDto);
                    Subscription subscription = subscriptionApi.create(subscriptionDto);
                    this.setCFValues(recordMap, subscription, ENTITY_NAME);
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private SubscriptionDto validateAndGetSubscription(SubscriptionActionEnum action, Map<String, Object> recordMap) {
        SubscriptionDto subscriptionDto = null;

        String subscriptionCode = (String) recordMap.get("OC_SUBSCRIPTION_CODE");
        if (subscriptionCode.isEmpty()) {
            throw new ValidationException("subscription_code is required");
        }

        String useraccountCode = (String) recordMap.get("OC_USERACCOUNT_CODE");
        if (useraccountCode.isEmpty()) {
            throw new ValidationException("useraccount_code is required");
        }

        subscriptionDto = new SubscriptionDto();

        return subscriptionDto;
    }

    private void setSubscriptionValues(Map<String, Object> recordMap, SubscriptionDto subscriptionDto) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        dateFormat.setLenient(false);

        String OC_subscription_date = (String) recordMap.get("OC_SUBSCRIPTION_DATE");
        String OC_endagreement_date = (String) recordMap.get("OC_ENDAGREEMENTDATE");
        String OC_termination_date = (String) recordMap.get("OC_TERMINATIONDATE");

        String OC_sub_inittermtype = (String) recordMap.get("OC_SUB_INITTERMTYPE");
        String OC_sub_durationtype = (String) recordMap.get("OC_SUB_DURATIONTYPE");
        Integer OC_sub_inittermlength = ((String) recordMap.get("OC_SUB_INITTERMLENGTH")).isEmpty() ? null : Integer.parseInt((String) recordMap.get("OC_SUB_INITTERMLENGTH"));

        String OC_sub_inittermunit = (String) recordMap.get("OC_SUB_INITTERMUNIT");
        String OC_sub_endtermaction = (String) recordMap.get("OC_SUB_ENDTERMACTION");

        try {
            Date subscriptionDate = StringUtils.isEmpty(OC_subscription_date) ? null : dateFormat.parse(OC_subscription_date);
            subscriptionDto.setSubscriptionDate(subscriptionDate);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect date format for subscription date. Please use 'dd/MM/yyyy'");
        }
        try {
            Date endagreementDate = StringUtils.isEmpty(OC_endagreement_date) ? null : dateFormat.parse(OC_endagreement_date);
            subscriptionDto.setEndAgreementDate(endagreementDate);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect date format for end agreement date. Please use 'dd/MM/yyyy'");
        }
        try {
            Date terminationDate = StringUtils.isEmpty(OC_termination_date) ? null : dateFormat.parse(OC_termination_date);
            subscriptionDto.setTerminationDate(terminationDate);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect date format for termination date. Please use 'dd/MM/yyyy'");
        }

        SubscriptionRenewal.InitialTermTypeEnum initialTermTypeEnum = (!EnumUtils.isValidEnum(SubscriptionRenewal.InitialTermTypeEnum.class, OC_sub_inittermtype)) ? null
                : SubscriptionRenewal.InitialTermTypeEnum.valueOf(OC_sub_inittermtype);
        SubscriptionRenewal.RenewalPeriodUnitEnum renewalPeriodUnit = (!EnumUtils.isValidEnum(SubscriptionRenewal.RenewalPeriodUnitEnum.class, OC_sub_durationtype)) ? null
                : SubscriptionRenewal.RenewalPeriodUnitEnum.valueOf(OC_sub_durationtype);

        SubscriptionRenewal.RenewalPeriodUnitEnum initialyActiveForUnit = (!EnumUtils.isValidEnum(SubscriptionRenewal.RenewalPeriodUnitEnum.class, OC_sub_inittermunit)) ? null
                : SubscriptionRenewal.RenewalPeriodUnitEnum.valueOf(OC_sub_inittermunit);
        SubscriptionRenewal.EndOfTermActionEnum endOfTermAction = (!EnumUtils.isValidEnum(SubscriptionRenewal.EndOfTermActionEnum.class, OC_sub_endtermaction)) ? null
                : SubscriptionRenewal.EndOfTermActionEnum.valueOf(OC_sub_endtermaction);
        String terminationReason = StringUtils.isEmpty((String) recordMap.get("OC_SUB_TERMINATIONREASON")) ? null : ((String) recordMap.get("OC_SUB_TERMINATIONREASON"));

        SubscriptionRenewalDto subscriptionRenewalDto = new SubscriptionRenewalDto();
        subscriptionRenewalDto.setInitialTermType(initialTermTypeEnum);
        subscriptionRenewalDto.setRenewForUnit(renewalPeriodUnit);
        subscriptionRenewalDto.setInitialyActiveFor(OC_sub_inittermlength);
        subscriptionRenewalDto.setInitialyActiveForUnit(initialyActiveForUnit);
        subscriptionRenewalDto.setEndOfTermAction(endOfTermAction);
        subscriptionRenewalDto.setTerminationReasonCode(terminationReason);

        subscriptionDto.setUserAccount((String) recordMap.get("OC_USERACCOUNT_CODE"));
        subscriptionDto.setCode((String) recordMap.get("OC_SUBSCRIPTION_CODE"));
        subscriptionDto.setStatus(SubscriptionStatusEnum.valueOf((String) recordMap.get("OC_STATUS")));
        subscriptionDto.setDescription((String) recordMap.get("OC_SUBSCRIPTION_DESCRIPTION"));
        subscriptionDto.setOfferTemplate((String) recordMap.get("OC_OFFER_CODE"));
        subscriptionDto.setSeller((String) recordMap.get("OC_SELLER_CODE"));
        subscriptionDto.setTerminationReason(terminationReason);
        subscriptionDto.setBillingCycle((String) recordMap.get("OC_SUB_BC"));

        subscriptionDto.setRenewalRule(subscriptionRenewalDto);
        subscriptionDto.setElectronicBilling(recordMap.get("OC_SUB_EBILL").toString().equals("X"));
        subscriptionDto.setEmail((String) recordMap.get("OC_SUB_EBILLEMAIL"));
        subscriptionDto.setCcedEmails((String) recordMap.get("OC_SUB_EBILLEMAILS"));

    }
}