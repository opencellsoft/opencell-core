package org.meveo.service.script;

import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.cpq.QuoteVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RatingScript extends Script {

    private final static Logger LOGGER = LoggerFactory.getLogger(QuoteVersionService.class);

    @Override
    public void execute(Map<String, Object> context) {

        WalletOperation walletOperation = (WalletOperation) context.get(Script.CONTEXT_ENTITY);

        ServiceInstance serviceInstance = walletOperation.getServiceInstance();

        List<AttributeInstance> attributeInstances = serviceInstance.getAttributeInstances(); // Attribute values: attribute + value

        ProductVersion productVersion = serviceInstance.getProductVersion();

        QuoteProduct quoteProduct = serviceInstance.getQuoteProduct();

        QuoteOffer quoteOffer = quoteProduct.getQuoteOffer();

        QuoteVersion quoteVersion = quoteOffer.getQuoteVersion();

        CpqQuote quote = quoteVersion.getQuote();


        for (QuoteAttribute attributeInstance : quoteOffer.getQuoteAttributes()) {
            if (attributeInstance.getAttribute().getCode().equals("ATTR-PRD-NV-VOLUME")) {
                log.error("about to set price on wallet operation, price: " + attributeInstance.getStringValue());
                walletOperation.setUnitAmountWithoutTax(BigDecimal.valueOf(Double.parseDouble(attributeInstance.getStringValue())));
            }
        }

    }

}
