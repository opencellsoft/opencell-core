package org.meveo.service.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CompareOfferAmountScript  extends Script{

    private static final Logger LOG = LoggerFactory.getLogger(CompareOfferAmountScript.class);
    
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
        methodContext.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            LOG.info("{}={}", entry.getKey(), entry.getValue());
        });
        
        checkScriptParams(methodContext, new String[] {OFFERS, WITH_OR_WITHOUT_TAX, OPERATOR, VALUE, INVOICE});
        Long invoiceId = ((Invoice) methodContext.get(INVOICE)).getId();
        String finalQuery = query.replaceAll("AMOUNT",  "amount" + StringUtils.camelcase((String)methodContext.get(WITH_OR_WITHOUT_TAX)))
                                 .replace("OPERATOR", ScriptUtils.buildOperator(String.valueOf(methodContext.get(OPERATOR)), true));
        
        List<OfferTemplate> offers = (List<OfferTemplate>) methodContext.get(OFFERS);
        
        List<Object[]> result = invoiceLineService.getEntityManager().createQuery(finalQuery)
                                                    .setParameter("invoiceId", invoiceId)
                                                    .setParameter("offers", offers.stream().map(OfferTemplate::getCode).collect(Collectors.toList()))
                                                    .setParameter("value", methodContext.get(VALUE))
                                                    .getResultList();
        
        methodContext.put(Script.INVOICE_VALIDATION_STATUS, CollectionUtils.isEmpty(result) ? InvoiceValidationStatusEnum.VALID : (InvoiceValidationStatusEnum) methodContext.get(Script.RESULT_VALUE));
    }
    
    @SuppressWarnings("unchecked")
    private void checkScriptParams(Map<String, Object> context, String ...params) {
        List<String> errors = new ArrayList<>();
        if(params.length == 0) {
            throw new BusinessException("params : invoice, offers, withOrWithoutTax, operator, value are mandatory" );
        }
        for(String param: params) {
            if(context.get(param) == null) {
                errors.add(param);
            }else if(context.get(param) != null && context.get(param) instanceof List) {
                if(CollectionUtils.isEmpty((List<OfferTemplate>)context.get(param))){
                    errors.add(param);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(errors)) {
            if(errors.size() > 1)
                throw new BusinessException("param : " + errors + " is mandatory");
            else
                throw new BusinessException("params : " + errors + " are mandatory");
        }
    }
}
