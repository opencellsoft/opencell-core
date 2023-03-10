package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.account.AccessApi;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.service.billing.impl.SubscriptionService;

public class AccessPointImportScript extends GenericMassImportScript {

    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy";
    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "ACCESS_POINT";
    private static final String ENTITY_NAME = "Access";

    public enum AccessPointActionEnum {
        CREATE, UPDATE, DELETE
    }

    private final SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
    private final AccessApi accessApi = (AccessApi) getServiceInterface(AccessApi.class.getSimpleName());

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
                if (Stream.of(AccessPointActionEnum.values()).noneMatch(e -> e.toString().equals(OC_ACTION))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
                }

                AccessPointActionEnum action = AccessPointActionEnum.valueOf(OC_ACTION);

                AccessDto accessDto = validateAndGetAccessPoint(action, recordMap);

                if (AccessPointActionEnum.CREATE.equals(action)) {
                    setAccessValues(recordMap, accessDto);
                    Access access = accessApi.create(accessDto);
                    this.setCFValues(recordMap, access, ENTITY_NAME);
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private AccessDto validateAndGetAccessPoint(AccessPointActionEnum action, Map<String, Object> recordMap) {
        AccessDto accessDto = null;

        String OC_subscription_code = (String) recordMap.get("OC_SUBSCRIPTION_CODE");
        if (OC_subscription_code.isEmpty()) {
            throw new ValidationException("subscription_code is required");
        }

        accessDto = new AccessDto();
        Subscription subscription = subscriptionService.findByCode(OC_subscription_code);
        if (subscription == null) {
            throw new ValidationException("no subscription found for code: '" + OC_subscription_code + "'");
        }

        return accessDto;
    }

    private void setAccessValues(Map<String, Object> recordMap, AccessDto accessDto) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        dateFormat.setLenient(false);
        String OC_accessPoint_startDate = (String) recordMap.get("OC_ACCESSPOINT_STARTDATE");
        String OC_accessPoint_endDate = (String) recordMap.get("OC_ACCESSPOINT_ENDDATE");
        try {
            Date startDate = StringUtils.isEmpty(OC_accessPoint_startDate) ? null : dateFormat.parse(OC_accessPoint_startDate);
            accessDto.setStartDate(startDate);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect date format for start date. Please use dd/MM/yyyy");
        }

        try {
            Date endDate = StringUtils.isEmpty(OC_accessPoint_endDate) ? null : dateFormat.parse(OC_accessPoint_endDate);
            accessDto.setEndDate(endDate);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect date format for end date. Please use dd/MM/yyyy");
        }

        accessDto.setSubscription((String) recordMap.get("OC_SUBSCRIPTION_CODE"));
        accessDto.setCode((String) recordMap.get("OC_ACCESSPOINT_CODE"));
    }
}