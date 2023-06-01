package org.meveo.service.script;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMatrixColumn;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;

public class SubscriptionImportScript extends Script {

    private static final long serialVersionUID = -2757843316687901385L;

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String RECORD_VARIABLE_NAME = "record";
    private SubscriptionTerminationReasonService reasonService = (SubscriptionTerminationReasonService) getServiceInterface("SubscriptionTerminationReasonService");
    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
    private UserAccountService userAccountService = (UserAccountService) getServiceInterface("UserAccountService");
    private SellerService sellerService = (SellerService) getServiceInterface("SellerService");
    private OfferTemplateService offerService = (OfferTemplateService) getServiceInterface("OfferTemplateService");
    private CustomFieldTemplateService customFieldTemplateService = (CustomFieldTemplateService) getServiceInterface("CustomFieldTemplateService");

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        try {
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
            if (recordMap != null && !recordMap.isEmpty()) {
                String OC_ENTITY = (String) recordMap.get("OC_ENTITY");
                if (!"SUBSCRIPTION".equals(OC_ENTITY)) {
                    throw new ValidationException("value of OC_ENTITY is not correct: " + OC_ENTITY);
                }
                String OC_ACTION = (String) recordMap.get("OC_ACTION");
                if (!Stream.of(SubscriptionActionEnum.values()).anyMatch(e -> e.toString().equals(OC_ACTION))) {
                    throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
                }
                SubscriptionActionEnum action = SubscriptionActionEnum.valueOf(OC_ACTION);

                String OC_subscription_code = (String) recordMap.get("OC_subscription_code");
                Subscription subscription = subscriptionService.findByCode(OC_subscription_code);
                if (subscription == null && !SubscriptionActionEnum.CREATE.equals(action)) {
                    throw new ValidationException("no Subscription Found for subscriptionCode: '" + OC_subscription_code + "'");
                }
                if (subscription != null && SubscriptionActionEnum.CREATE.equals(action)) {
                    throw new ValidationException("subscription already exists with code: '" + OC_subscription_code + "'");
                }

                switch (action) {
                case CREATE:
                    subscription = new Subscription();
                    subscription.setCode(OC_subscription_code);
                    mapSubscriptionValues(recordMap, subscription);
                    subscriptionService.create(subscription);
                    break;
                case ACTIVATE:
                    subscriptionService.activateInstantiatedService(subscription);
                    break;
                case RESUME:
                    subscriptionService.subscriptionReactivation(subscription, new Date());
                    break;
                case SUSPEND:
                    subscriptionService.subscriptionSuspension(subscription, new Date());
                    break;
                case TERMINATE:
                    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
                    Date OC_terminationDate = extractDate(recordMap, dateFormat, "OC_terminationDate");
                    String terminationCode = (String) recordMap.get("OC_subscriptionTerminationReason_code");
                    if (terminationCode == null) {
                        throw new ValidationException("OC_subscriptionTerminationReason_code is mandatory to terminate subscription");
                    }
                    SubscriptionTerminationReason terminationReason = reasonService.findByCodeReason(terminationCode);
                    if (terminationReason == null) {
                        throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, terminationCode);
                    }
                    subscription.setSubscriptionTerminationReason(terminationReason);
                    subscriptionService.terminateSubscription(subscription, OC_terminationDate, terminationReason, null);
                    break;
                case UPDATE:
                    mapSubscriptionValues(recordMap, subscription);
                    subscriptionService.update(subscription);
                    break;
                default:
                    break;
                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private void mapSubscriptionValues(Map<String, Object> recordMap, Subscription subscription) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        String OC_seller_code = (String) recordMap.get("OC_seller_code");
        String OC_offer_code = (String) recordMap.get("OC_offer_code");
        String OC_userAccount_code = (String) recordMap.get("OC_userAccount_code");
        Date subscriptionDate = extractDate(recordMap, dateFormat, "OC_subscriptionDate");
        Date endAgreementDate = extractDate(recordMap, dateFormat, "OC_endAgreementDate");
        Date OC_terminationDate = extractDate(recordMap, dateFormat, "OC_terminationDate");

        String OC_subscription_description = (String) recordMap.get("OC_subscription_description");

        UserAccount userAccount = userAccountService.findByCode(OC_userAccount_code);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, OC_userAccount_code);
        }
        Seller seller = sellerService.findByCode(OC_seller_code);
        if (seller == null) {
            throw new EntityDoesNotExistsException(Seller.class, OC_seller_code);
        }
        OfferTemplate offer = offerService.findByCode(OC_offer_code);
        if (offer == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class, OC_offer_code);
        }
        subscription.setUserAccount(userAccount);
        subscription.setSeller(seller);
        subscription.setOffer(offer);
        subscription.setTerminationDate(OC_terminationDate);
        subscription.setSubscriptionDate(subscriptionDate);
        subscription.setEndAgreementDate(endAgreementDate);
        subscription.setDescription(OC_subscription_description);
        recordMap.keySet().stream().filter(key -> key.startsWith("CF_")).forEach(key -> subscription.setCfValue(key.substring(3), parseStringCf(key.substring(3), (String) recordMap.get(key))));
    }

    public enum SubscriptionActionEnum {
        CREATE, UPDATE, SUSPEND, RESUME, ACTIVATE, TERMINATE
    }

    private Date extractDate(Map<String, Object> recordMap, DateFormat dateFormat, final String key) throws ParseException {
        final String str = (String) recordMap.get(key);
        return StringUtils.isEmpty(str) ? null : dateFormat.parse(str);
    }

    public Object parseStringCf(String cftCode, String stringCF) {
        if (StringUtils.isEmpty(stringCF)) {
            return stringCF;
        }
        CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(cftCode, "Subscription");
        if (cft == null) {
            throw new BusinessException("No Custom Field exist on Subscription with code " + cftCode);
        }
        CustomFieldStorageTypeEnum storageType = cft.getStorageType();

        switch (storageType) {
        case SINGLE:
            return parseSingleValue(cft, stringCF);
        case MATRIX:
            Map<String, Object> matrix = new HashMap<>();
            final List<CustomFieldMatrixColumn> matrixKeys = cft.getMatrixKeyColumns();
            final List<CustomFieldMatrixColumn> matrixValues = cft.getMatrixValueColumns();
            if (cft.getFieldType() == CustomFieldTypeEnum.MULTI_VALUE) {
                List<String> stringCFLines = stringCF.contains("\n") ? Arrays.asList(stringCF.split("\n")) : Arrays.asList(stringCF);
                for (String stringCFLine : stringCFLines) {
                    List<String> list = Arrays.asList(stringCFLine.split("\\|"));

                    final int keySize = matrixKeys.size();
                    if (list == null || list.size() != (keySize + matrixValues.size())) {
                        throw new ValidationException("Not valid String representation of MATRIX Custom Field : " + cft.getCode() + "/" + stringCF);
                    }
                    String key = "";
                    String value = "";
                    for (String s : list.subList(0, keySize)) {
                        key = key != "" ? key + "|" + s : s;
                    }
                    for (String s : list.subList(keySize, list.size())) {
                        value = value != "" ? value + "|" + s : s;
                    }
                    matrix.put(key, value);
                }
            } else {
                List<String> stringCFLines = stringCF.contains("\n") ? Arrays.asList(stringCF.split("\n")) : Arrays.asList(stringCF);
                for (String stringCFLine : stringCFLines) {
                    List<String> list = Arrays.asList(stringCFLine.split("\\|"));
                    final int keySize = matrixKeys.size();
                    if (list == null || list.size() != (keySize + 1)) {
                        throw new ValidationException("Not valid String representation of MATRIX Custom Field : " + cft.getCode() + "/" + stringCF);
                    }
                    String key = "";
                    for (String s : list.subList(0, keySize)) {
                        key = key != "" ? key + "|" + s : s;
                    }
                    matrix.put(key, parseSingleValue(cft, list.get(list.size() - 1)));
                }
            }
            return matrix;
        case MAP:
            Map<String, Object> map = new HashMap<>();
            if (stringCF.isEmpty()) {
                return map;
            }
            List<String> stringCFLines = stringCF.contains("\n") ? Arrays.asList(stringCF.split("\n")) : Arrays.asList(stringCF);

            for (String stringCFLine : stringCFLines) {
                List<String> list = Arrays.asList(stringCFLine.split("\\|"));
                if (list == null || list.size() != 2) {
                    throw new ValidationException("Not valid String representation of MAP Custom Field : " + cft.getCode() + "/" + stringCF);
                }
                String key = list.get(0);
                map.put(key, parseSingleValue(cft, list.get(1)));
            }
            return map;
        case LIST:
            // TODO
            return stringCF;
        default:
            return stringCF;
        }
    }

    private static Object parseSingleValue(CustomFieldTemplate cft, String stringCF) {
        if (cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
            return Double.parseDouble(stringCF);
        } else if (cft.getFieldType() == CustomFieldTypeEnum.BOOLEAN) {
            return Boolean.parseBoolean(stringCF);
        } else if (cft.getFieldType() == CustomFieldTypeEnum.LONG) {
            return Long.parseLong(stringCF);
        } else if (cft.getFieldType() == CustomFieldTypeEnum.STRING || cft.getFieldType() == CustomFieldTypeEnum.LIST || cft.getFieldType() == CustomFieldTypeEnum.CHECKBOX_LIST
                || cft.getFieldType() == CustomFieldTypeEnum.TEXT_AREA) {
            return stringCF;
        } else if (cft.getFieldType() == CustomFieldTypeEnum.DATE) {
            return DateUtils.parseDate(stringCF);
        } else {
            throw new ValidationException("NOT YET IMPLEMENTED");
        }
    }
}