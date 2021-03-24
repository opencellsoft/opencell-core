package org.meveo.admin.job;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * Calculate the overrun by Agency-Offer-ChargeType and create WalletOperation
 *
 * @author BEN AICHA Amine
 * @author BOUKAYOUA Mounir
 */
@Stateless
public class OfferPoolInitializerUnitJobBean {

    private static final String CF_POOL_PER_OFFER_MAP = "POOL_PER_OFFER_MAP";

    private static final String SERVICE_TEMPLATE_QUERY = "select ost.serviceTemplate " + "from OfferServiceTemplate ost \n"
            + "where ost.serviceTemplate.code like '%USAGE' and ost.offerTemplate.id=:offerId";

    @Inject
    private Logger log;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private CustomFieldInstanceService cfiService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @SuppressWarnings({ "unchecked" })
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, BigInteger offerId, String userAccountCode, BigInteger subCount, Date counterStartDate) {

        log.info("Start pool counters initialization for offerId={}, userAccountCode={}", offerId, userAccountCode);

        try {
            List<ServiceTemplate> serviceTemplates = emWrapper.getEntityManager().createQuery(SERVICE_TEMPLATE_QUERY, ServiceTemplate.class)
                .setParameter("offerId", offerId.longValue()).getResultList();

            for (ServiceTemplate serviceTemplate : serviceTemplates) {

                try {

                    Map<String, Double> poolPerOfferMap = (Map<String, Double>) serviceTemplate.getCfValue(CF_POOL_PER_OFFER_MAP, counterStartDate);
                    if (poolPerOfferMap == null) {
                        poolPerOfferMap = new HashMap<>();
                    }

                    if (poolPerOfferMap.get(userAccountCode + "_initial") == null) {
                        BigDecimal volumePerCard = getVolumePerCard(serviceTemplate);
                        BigDecimal totalPool = volumePerCard.multiply(new BigDecimal(subCount));

                        poolPerOfferMap.put(userAccountCode + "_number_of_cards", subCount.doubleValue());
                        poolPerOfferMap.put(userAccountCode + "_initial", totalPool.doubleValue());
                        poolPerOfferMap.put(userAccountCode + "_value", totalPool.doubleValue());
                        cfiService.setCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, poolPerOfferMap, counterStartDate);
                        serviceTemplateService.update(serviceTemplate);
                    }
                } catch (Exception e) {
                    new BusinessException("Service=" + serviceTemplate.getCode(), e);
                }
            }

            result.registerSucces();

        } catch (Exception e) {
            log.error("Error on initializing counters for offerId={}, userAccountCode={}", offerId, userAccountCode, e);
            result.registerError("Error on initializing counters for offerId=" + offerId + ", userAccountCode=" + userAccountCode + " => " + e.getCause() + ":" + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private BigDecimal getVolumePerCard(ServiceTemplate serviceTemplate) {

        Double volume = (Double) serviceTemplate.getCfValue("volume");
        String volumeUnit = (String) serviceTemplate.getCfValue("volumeUnit");
        Double multiplier = ((Map<String, Double>) cfiService.getCFValueByKey(appProvider, "CF_P_USAGE_UNITS", volumeUnit)).get("multiplier");

        return BigDecimal.valueOf(volume).multiply(BigDecimal.valueOf(multiplier));
    }
}