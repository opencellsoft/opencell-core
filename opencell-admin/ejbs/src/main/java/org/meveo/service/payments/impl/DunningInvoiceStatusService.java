package org.meveo.service.payments.impl;
import javax.ejb.Stateless;

import org.meveo.model.dunning.DunningInvoiceStatus;
import org.meveo.service.base.PersistenceService;

import java.util.List;

/**
 * Service implementation to manage InvoiceDunningStatuses entity.
 * It extends {@link PersistenceService} class
 * 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Stateless
public class DunningInvoiceStatusService extends PersistenceService<DunningInvoiceStatus> {

    /**
     * Search a dunning invoice status based on dunning setting code and status.
     *
     * @param dunningSettingsCode dunning setting code
     * @param status              invoice status
     * @return Dunning invoice status
     */
    public DunningInvoiceStatus findByCodeAndDunningSettingCode(String dunningSettingsCode, String status) {
        return getEntityManager().createNamedQuery("DunningInvoiceStatus.findByCodeAndDunningSettingCode", DunningInvoiceStatus.class).setParameter("status", status)
                .setParameter("dunningSettingsCode", dunningSettingsCode).getSingleResult();
    }

    public List<DunningInvoiceStatus> findByStatusAndLanguage(DunningInvoiceStatus dunningInvoiceStatus) {
        return getEntityManager().createNamedQuery("DunningInvoiceStatus.findByStatusAndLanguage", DunningInvoiceStatus.class)
                .setParameter("status", dunningInvoiceStatus.getStatus()).setParameter("languageId", dunningInvoiceStatus.getLanguage().getId()).getResultList();
    }
}
