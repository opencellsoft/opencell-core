package org.meveo.service.script;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.billing.impl.InvoiceLineService;



@SuppressWarnings("serial")
public class CompareOfferLinesAmountScript extends Script{

    private final String OFFERS = "offers";
    private final String WITH_OR_WITHOUT_TAX = "withOrWithoutTax";
    private final String OPERATOR = "operator";
    private final String VALUE = "value";
    private final String INVOICE = "CONTEXT_ENTITY";
    
    private final String queryWithOffer = "select id from InvoiceLine where invoice.id = :invoiceId and offerTemplate.id in (:offers) and not (AMOUNT OPERATOR :value)";
    private final String queryWithoutOffer = "select id from InvoiceLine where invoice.id = :invoiceId and not (AMOUNT OPERATOR :value)";

    private InvoiceLineService invoiceLineService = (InvoiceLineService) getServiceInterface("InvoiceLineService");
    
    @SuppressWarnings("unchecked")
    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        Long invoiceId = ((Invoice) methodContext.get(INVOICE)).getId();
        List<OfferTemplate> offers = (List<OfferTemplate>) methodContext.get(OFFERS);

		String finalQuery = ((offers == null || offers.isEmpty()) ? queryWithoutOffer : queryWithOffer)
				.replace("AMOUNT", "amount" + StringUtils.camelcase((String) methodContext.get(WITH_OR_WITHOUT_TAX)))
				.replace("OPERATOR", ScriptUtils.buildOperator(String.valueOf(methodContext.get(OPERATOR)), true));
        
        Query query = invoiceLineService.getEntityManager().createQuery(finalQuery).setParameter("invoiceId", invoiceId).setParameter("value", methodContext.get(VALUE));
        if (!(offers == null || offers.isEmpty())) query.setParameter("offers", offers.stream().map(OfferTemplate::getId).collect(Collectors.toList()));
        List<Object[]> result = query.getResultList();
       
        methodContext.put(Script.INVOICE_VALIDATION_STATUS, CollectionUtils.isEmpty(result) ? true : false);
    }
    
}