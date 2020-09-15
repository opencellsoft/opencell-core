package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.UserAccountService;
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

    private static final String SERVICE_TEMPLATE_QUERY = "select ost.serviceTemplate "
            + "from OfferServiceTemplate ost \n"
            + "where ost.serviceTemplate.code like '%USAGE' and ost.offerTemplate.id=:offerId";


    @Inject
    private Logger log;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private CustomFieldInstanceService cfiService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @SuppressWarnings({"unchecked"})
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long offerId, Long userAccountId, BigDecimal subCount, Date counterStartDate) {

        log.info("Start pool counters initialization for offerId={}, userAccountId={}", offerId, userAccountId);

        try {
            String userAccountCode = null;

            List<ServiceTemplate> serviceTemplates = emWrapper.getEntityManager().createQuery(SERVICE_TEMPLATE_QUERY, ServiceTemplate.class)
                    .setParameter("offerId", offerId)
                    .getResultList();

            if(!serviceTemplates.isEmpty()) {
                UserAccount userAccount = userAccountService.findById(userAccountId);
                userAccountCode = userAccount.getCode();
            }

            for (ServiceTemplate serviceTemplate : serviceTemplates) {

                Map<String, Double> poolPerOfferMap = (Map<String, Double>) serviceTemplate.getCfValue(CF_POOL_PER_OFFER_MAP, counterStartDate);
                if (poolPerOfferMap == null) {
                    poolPerOfferMap = new HashMap<>();
                }

                if (poolPerOfferMap.get(userAccountCode + "_initial") == null) {
                    BigDecimal volumePerCard = getVolumePerCard(serviceTemplate);
                    BigDecimal totalPool = volumePerCard.multiply(subCount);

                    poolPerOfferMap.put(userAccountCode + "_initial", totalPool.doubleValue());
                    poolPerOfferMap.put(userAccountCode + "_value", totalPool.doubleValue());
                    cfiService.setCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, poolPerOfferMap, counterStartDate);
                    serviceTemplateService.update(serviceTemplate);
                }
            }

            result.registerSucces();

        } catch (Exception e) {
            log.error("Error on initializing counters for offerId={}, uaId={}", offerId, userAccountId, e);
            result.registerError("Error on initializing counters for offerId="+offerId+", uaId="+userAccountId+": " +e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private BigDecimal getVolumePerCard(ServiceTemplate serviceTemplate) {

        Double volume = (Double) serviceTemplate.getCfValue("volume");
        String volumeUnit = (String) serviceTemplate.getCfValue("volumeUnit");
        Double multiplier = ((Map<String, Double>) cfiService
                .getCFValueByKey(appProvider, "CF_P_USAGE_UNITS", volumeUnit))
                .get("multiplier");

        return BigDecimal.valueOf(volume).multiply(BigDecimal.valueOf(multiplier));
    }
}