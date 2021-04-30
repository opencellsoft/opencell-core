package com.matooma.script;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionRenewal;
import org.meveo.model.billing.SubscriptionRenewal.EndOfTermActionEnum;
import org.meveo.model.billing.SubscriptionRenewal.RenewalPeriodUnitEnum;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.script.Script;

/**
 * API de changement statut de carte SIM
 */
public class ChangementStatutCarteSIMApi extends Script {

    private static final long serialVersionUID = 7140592725623295745L;

    private static final String CET_CODE = "CE_SIM_CARD";
    private static final String CF_COMMITMENT = "Commitment";
    private static final String CF_FREE_MONTHS = "freeMonths";

    private AccessService accessService = (AccessService) getServiceInterface(AccessService.class.getSimpleName());
    private SellerService sellerService = (SellerService) getServiceInterface(SellerService.class.getSimpleName());
    private UserAccountService userAccountService = (UserAccountService) getServiceInterface(UserAccountService.class.getSimpleName());
    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface(SubscriptionService.class.getSimpleName());
    private OfferTemplateService offerTemplateService = (OfferTemplateService) getServiceInterface(OfferTemplateService.class.getSimpleName());
    private ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface(ServiceInstanceService.class.getSimpleName());
    private TerminationReasonService terminationReasonService = (TerminationReasonService) getServiceInterface(TerminationReasonService.class.getSimpleName());
    private CustomFieldInstanceService cfiService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());
    private CustomEntityInstanceService customEntityInstanceService = (CustomEntityInstanceService) getServiceInterface(CustomEntityInstanceService.class.getSimpleName());

    /**
     * Entry Point
     */
    public void execute(Map<String, Object> context) throws BusinessException {

        InboundRequest inboundRequest = (InboundRequest) context.get(Script.CONTEXT_ENTITY);

        try {

            String body = (String) context.get("body");

            inboundRequest.setResponseContentType("application/json");
            inboundRequest.setResponseBody("{\"status\": \"SUCCESS\",\"message\": \"\"}");
            inboundRequest.setResponseStatus(200);

            // parse body content
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(body);

            Date date = getDate((String) json.get("createdAt"));
            String status = (String) json.get("status");
            String iccid = (String) json.get("iccid");

            switch (status) {

            case "ACTIVATE":
                String userAccountCode = (String) json.get("userAccount");
                String seller = (String) json.get("seller");
                JSONArray iccidList = (JSONArray) json.get("iccids");
                String profil = json.get("offerCode").toString(); // change by thomas because Integer sent from parameters

                EntityManager em = subscriptionService.getEntityManager();
                Session session = em.unwrap(Session.class);
                Transaction tx = session.beginTransaction();
                try {
                    tx.begin();
                    activerCarteSIM(inboundRequest, iccidList, userAccountCode, profil, seller, date);
                    tx.commit();
                } catch (Exception e) {
                    tx.rollback();
                    throw e;
                }
                break; // activate SIM Card

            case "SUSPENDED":
                suspendreCarteSIM(inboundRequest, iccid, date);
                break; // suspend SIM Card

            case "TERMINATE":
                terminateCarteSIM(inboundRequest, iccid, date);
                break; // terminate SIM Card

            case "REACTIVATE":
                reactivateCarteSIM(inboundRequest, iccid, date);
                break; // reactivate SIM Card

            case "PAIRED":
                userAccountCode = (String) json.get("userAccount");
                appairerCarteSIM(inboundRequest, iccid, date, userAccountCode);
                break; // pair SIM Card

            default:
                log.error("Status {} not found ", status);
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"STATUS_NOT_FOUND\"}");
                inboundRequest.setResponseStatus(500);
                break;
            }

        } catch (Exception e) {
            log.error("Failed to execute ChangementStatutCarteSIM script", e);
            inboundRequest.setResponseStatus(500);

            if (e instanceof BusinessException) {
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"BUSINESS_EXCEPTION - " + e.getMessage() + "\"}");
            } else {
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"" + e.getClass().getSimpleName() + " - " + e.getMessage() + "\"}");
            }
        }
    }

    /**
     * Activer Carte SIM
     * 
     * @param offerCode
     * @param sellerCode
     * @param date
     * 
     * @throws Exception
     */
    private void activerCarteSIM(InboundRequest inboundRequest, JSONArray iccidList, String userAccountCode, String offerCode, String sellerCode, Date date) throws Exception {
        log.debug("Activate SIM Card...");

        // Vérifier que l'agence existe
        UserAccount userAccount = userAccountService.findByCode(userAccountCode);
        if (userAccount == null) {
            throw new BusinessException("AGENCE_NOT_FOUND : " + userAccountCode);
        }

        // Vérifier que l'offre existe
        OfferTemplate offer = offerTemplateService.findByCode(offerCode);
        if (offer == null) {
            throw new BusinessException("OFFER_NOT_FOUND : " + offerCode);
        }

        // Vérifier que le seller existe
        Seller seller = sellerService.findByCode(sellerCode);
        if (seller == null) {
            throw new BusinessException("SELLER_NOT_FOUND : " + sellerCode);
        }

        // For each iccidList
        for (Object iccidJSON : iccidList) {
            JSONObject json = (JSONObject) iccidJSON;
            String iccid = (String) json.get("iccid");
            String msisdn = (String) json.get("msisdn");
            String deviceModelId = json.get("deviceModelId").toString(); // changed by Thomas because Integer sent from parameters
            String deviceModelName = (String) json.get("deviceModelName");

            // Chercher la custom entity "Carte SIM" avec l'ICCID = customEntityInstance.code
            CustomEntityInstance carteSIM = customEntityInstanceService.findByCodeByCet(CET_CODE, iccid);
            if (carteSIM == null) {
                throw new BusinessException("SIM_CARD_NOT_FOUND : " + iccid);
            }

            // Check status
            String status = (String) carteSIM.getCfValue("status");
            if (!Arrays.asList("PAIRED", "ORDERED").contains(status)) {
                throw new BusinessException("INVALID_STATUS : " + iccid + " -> current status [" + status + "]");
            }

            // Check if ordering customer is the same who will activate
            EntityReferenceWrapper customerCodeInfo = (EntityReferenceWrapper) carteSIM.getCfValue("customerCode");
            if (customerCodeInfo != null) {
                UserAccount userWhoOrdered = userAccountService.findByCode(customerCodeInfo.getCode());
                if (userWhoOrdered == null || !userWhoOrdered.getCode().equals(userAccountCode)) {
                    throw new BusinessException("NOT_SAME_ORDERED_USER : " + iccid + " -> User who ordered the SIM card : " + userWhoOrdered);
                }
            }

            Subscription subscription = new Subscription();
            subscription.setCode(iccid);
            subscription.setOffer(offer);
            subscription.setUserAccount(userAccount);
            subscription.setSeller(seller);
            subscription.setSubscriptionDate(date);

            // Manage Renewal
            Double commitment = (Double) offer.getCfValue(CF_COMMITMENT);
            SubscriptionRenewal subscriptionRenewal = new SubscriptionRenewal();
            subscriptionRenewal.setInitialyActiveFor(commitment.intValue());
            subscriptionRenewal.setInitialyActiveForUnit(RenewalPeriodUnitEnum.MONTH);
            subscriptionRenewal.setRenewFor(10000);
            subscriptionRenewal.setRenewForUnit(RenewalPeriodUnitEnum.MONTH);
            subscriptionRenewal.setAutoRenew(true);
            subscriptionRenewal.setEndOfTermAction(EndOfTermActionEnum.TERMINATE);
            subscriptionRenewal.setTerminationReason(terminationReasonService.findByCode("TR_NONE"));
            subscription.setSubscriptionRenewal(subscriptionRenewal);

            // Renseigner le CF simCard de la souscription créé avec la ref vers la simCard
            subscription.setCfValue("simCard", carteSIM);

            // Renseigner les CF simCard, boxModelId, boxModelName sur la souscription
            subscription.setCfValue("boxModelId", deviceModelId);
            subscription.setCfValue("boxModelName", deviceModelName);

            subscriptionService.create(subscription);

            // Renseigner l'accès point avec le champ "msisdn"
            Access access = accessService.findByUserIdAndSubscription(iccid, subscription);
            access.setAccessUserId(msisdn);
            accessService.update(access);

            // Instantiate and Activate Services
            log.info("Activating services");

            Date subscriptionDate = date;

            // Check if subscription with free months (franchise)
            Double freeMonths = (Double) cfiService.getInheritedCFValue(subscription, CF_FREE_MONTHS);
            boolean offerWithFranchise = freeMonths != null && freeMonths > 0;

            if (offerWithFranchise) {

                Date exemptionEndDate = DateUtils.addMonthsToDate(subscriptionDate, freeMonths.intValue());

                // Save provisional exemptionEndDate
                subscription.setCfValue("exemptionEndDate", exemptionEndDate);

                for (OfferServiceTemplate offerServiceTemplate : offer.getOfferServiceTemplates()) {

                    ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
                    String serviceTemplateCode = serviceTemplate.getCode();

                    // Instantiate only recurring service
                    if (serviceTemplateCode.contains("ONESHOT") || serviceTemplateCode.contains("SUBSCRIPTION")) {
                        ServiceInstance serviceInstance = new ServiceInstance();
                        serviceInstance.setServiceTemplate(serviceTemplate);
                        serviceInstance.setStatus(InstanceStatusEnum.INACTIVE);
                        serviceInstance.setSubscription(subscription);
                        serviceInstance.setCode(serviceTemplate.getCode());
                        serviceInstance.setSubscriptionDate(exemptionEndDate);

                        serviceInstanceService.serviceInstanciation(serviceInstance, null);
                    } else {
                        // Activate Usages and one shoot
                        instantiateAndActivateService(subscription, serviceTemplate, subscriptionDate);
                    }
                }
            } else {
                for (OfferServiceTemplate offerServiceTemplate : offer.getOfferServiceTemplates()) {
                    ServiceTemplate serviceTemplate = offerServiceTemplate.getServiceTemplate();
                    instantiateAndActivateService(subscription, serviceTemplate, subscriptionDate);
                }
            }

            // Modifier le statut de la carteSIM à ACTIVE
            carteSIM.setCfValue("status", "ACTIVE");
            carteSIM.setCfValue("dateActive", date);
            // Renseigner le "customerCode" de la carte sim avec l'UA
            carteSIM.setCfValue("customerCode", userAccount);
            customEntityInstanceService.update(carteSIM);

            log.info("Activated SIM Card : " + iccid);
        }
    }

    /**
     * Suspend CARTE SIM. Modify SIM card status to SUSPENDED.
     * 
     * @throws Exception
     */
    private void suspendreCarteSIM(InboundRequest inboundRequest, String iccidList, Date date) throws Exception {
        log.info("Suspended SIM Card...");
        for (String iccid : iccidList.split(",")) {

            Subscription subscription = subscriptionService.findByCode(iccid);
            if (subscription == null) {
                log.error("Subscription not found " + iccid);
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"SUBSCRIPTION_NOT_FOUND : " + iccid + "\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }

            // Suspendre souscription
            // Ticket #210
            // subscriptionService.subscriptionSuspension(subscription, date);

            // chercher la custom entity instance "Carte SIM" avec l'ICCID = customEntityInstance.code
            CustomEntityInstance carteSIM = customEntityInstanceService.findByCodeByCet(CET_CODE, iccid);

            if (carteSIM == null) {
                log.error("Carte SIM non existante : " + iccidList);
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"SIM_CARD_NOT_FOUND : " + iccid + "\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }

            log.info("CarteSIM Found : " + carteSIM.getCode());

            carteSIM.setCfValue("status", "SUSPENDED");
            carteSIM.setCfValue("dateSuspended", date);
            customEntityInstanceService.update(carteSIM);

            log.info("Suspended SIM Card : " + iccid);
        }
    }

    /**
     * Terminer Carte SIM. Modify SIM card status to TERMINATED.
     * 
     * @throws Exception
     */
    private void terminateCarteSIM(InboundRequest inboundRequest, String iccidList, Date date) throws Exception {
        log.info("Terminate SIM Card...");
        for (String iccid : iccidList.split(",")) {

            Subscription subscription = subscriptionService.findByCode(iccid);
            if (subscription == null) {
                log.error("Subscription not found " + iccid);
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"SUBSCRIPTION_NOT_FOUND : " + iccid + "\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }

            if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
                log.error("Subscription is already resilied");
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"ALREADY_RESILIATED_SUBSCRIPTION : " + iccid + "\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }

            if (subscription.getTerminationDate() != null) {
                log.error("Subscription already has a termination date");
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"SUBSCRIPTION_ALREADY_HAS_A_TERMINATION_DATE : " + iccid + "\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }

            // chercher la custom entity instance "Carte SIM" avec l'ICCID = customEntityInstance.code
            CustomEntityInstance carteSIM = customEntityInstanceService.findByCodeByCet(CET_CODE, iccid);
            if (carteSIM == null) {
                log.error("Carte SIM non existante : " + iccidList);
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"SIM_CARD_NOT_FOUND : " + iccid + "\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }
            log.info("CarteSIM Found : " + carteSIM.getCode());

            // terminate subscription
            subscription.setCfValue("waitingTermination", "false");
            subscription.setCfValue("dateTerminated", date);
            subscription.setStatus(SubscriptionStatusEnum.RESILIATED);
            subscriptionService.update(subscription);

            carteSIM.setCfValue("status", "TERMINATED");
            carteSIM.setCfValue("dateTerminated", date);
            customEntityInstanceService.update(carteSIM);

            log.info("Terminated SIM Card " + iccid);
        }
    }

    /**
     * Reactivate CARTE SIM. Modify SIM card status to REACTIVATED.
     * 
     * @throws Exception
     */
    private void reactivateCarteSIM(InboundRequest inboundRequest, String iccidList, Date date) throws Exception {
        log.info("Reactivate SIM Card...");

        for (String iccid : iccidList.split(",")) {
            Subscription subscription = subscriptionService.findByCode(iccid);
            if (subscription == null) {
                log.error("Subscription not found " + iccid);
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"SUBSCRIPTION_NOT_FOUND : " + iccid + "\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }

            // Reactivate subscription
            // Ticket #210
            // subscriptionService.subscriptionReactivation(subscription, date);

            // chercher la custom entity instance "Carte SIM" avec l'ICCID = customEntityInstance.code
            CustomEntityInstance carteSIM = customEntityInstanceService.findByCodeByCet(CET_CODE, iccid);

            carteSIM.setCfValue("status", "ACTIVE");
            carteSIM.setCfValue("dateActive", date);
            customEntityInstanceService.update(carteSIM);

            log.info("Reactivated SIM Card : " + iccid);
        }
    }

    /**
     * Pair CARTE SIM. Modify SIM card status to PAIRED.
     * 
     * @throws Exception
     */
    private void appairerCarteSIM(InboundRequest inboundRequest, String iccidList, Date date, String userAccountCode) throws Exception {
        log.info("Appairer SIM Card...");

        // vérifier que l’agence existe
        UserAccount userAccount = null;
        if (!StringUtils.isBlank(userAccountCode)) {
            userAccount = userAccountService.findByCode(userAccountCode);
            if (userAccount == null) {
                log.error("Agence not found " + userAccountCode);
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"AGENCE_NOT_FOUND\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }
        }

        for (String iccid : iccidList.split(",")) {

            // chercher la custom entity instance "Carte SIM" avec l'ICCID = customEntityInstance.code
            CustomEntityInstance carteSIM = customEntityInstanceService.findByCodeByCet(CET_CODE, iccid);

            if (carteSIM == null) {
                log.error("Carte SIM non existante : " + iccidList);
                inboundRequest.setResponseBody("{\"status\": \"FAILURE\",\"message\": \"SIM_CARD_NOT_FOUND : " + iccid + "\"}");
                inboundRequest.setResponseStatus(500);
                return;
            }

            log.info("CarteSIM Found : " + carteSIM.getCode());

            carteSIM.setCfValue("status", "PAIRED");
            carteSIM.setCfValue("datePaired", date);
            if (userAccount != null) {
                carteSIM.setCfValue("customerCode", userAccount);
            }
            customEntityInstanceService.update(carteSIM);

            log.info("SIM Card Paired: " + iccid);
        }
    }

    private void instantiateAndActivateService(Subscription subscription, ServiceTemplate serviceTemplate, Date subscriptionDate) {
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceTemplate(serviceTemplate);
        serviceInstance.setStatus(InstanceStatusEnum.INACTIVE);
        serviceInstance.setSubscription(subscription);
        serviceInstance.setCode(serviceTemplate.getCode());
        serviceInstance.setSubscriptionDate(subscriptionDate);

        serviceInstanceService.serviceInstanciation(serviceInstance, null);
        serviceInstanceService.serviceActivation(serviceInstance, true, null, null);
    }

    /**
     * Convert Date String to a Date Object
     * 
     * @param date
     * @return Parsed Date
     * @throws ParseException thrown when problem in parsing date.
     */
    private Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return format.parse(date);
    }
}