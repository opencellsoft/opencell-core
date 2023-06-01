package org.meveo.service.script;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.billing.impl.InvoiceLineService;

@SuppressWarnings("serial")
public class CompareOfferAmountScript extends Script {

	private final String OFFERS = "offers";
	private final String WITH_OR_WITHOUT_TAX = "withOrWithoutTax";
	private final String OPERATOR = "operator";
	private final String VALUE = "value";
	private final String INVOICE = "CONTEXT_ENTITY";

	private final String query = "select sum(AMOUNT), offerTemplate.id from InvoiceLine where invoice.id = :invoiceId and offerTemplate.code in (:offers) group by offerTemplate.id having not(sum(AMOUNT) OPERATOR :value)";

	private InvoiceLineService invoiceLineService = (InvoiceLineService) getServiceInterface("InvoiceLineService");

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		Long invoiceId = ((Invoice) methodContext.get(INVOICE)).getId();
		String finalQuery = query
				.replace("AMOUNT", "amount" + StringUtils.camelcase((String) methodContext.get(WITH_OR_WITHOUT_TAX)))
				.replace("OPERATOR", ScriptUtils.buildOperator(String.valueOf(methodContext.get(OPERATOR)), true));

		List<OfferTemplate> offers = (List<OfferTemplate>) methodContext.get(OFFERS);

		if (CollectionUtils.isEmpty(offers)) {
			methodContext.put(Script.INVOICE_VALIDATION_STATUS, null);
		} else {
			List<Object[]> result = invoiceLineService.getEntityManager().createQuery(finalQuery)
					.setParameter("invoiceId", invoiceId)
					.setParameter("offers", offers.stream().map(OfferTemplate::getCode).collect(Collectors.toList()))
					.setParameter("value", methodContext.get(VALUE)).getResultList();

			methodContext.put(Script.INVOICE_VALIDATION_STATUS, CollectionUtils.isEmpty(result) ? true : false);
		}
	}

}