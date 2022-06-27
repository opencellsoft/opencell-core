package org.meveo.apiv2.ordering.services;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.ordering.OpenOrderQuote;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.order.OpenOrderQuoteService;
import org.meveo.service.settings.impl.OpenOrderSettingService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.DRAFT;
import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.REJECTED;
import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.SENT;
import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.VALIDATED;
import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.WAITING_VALIDATION;
import static org.meveo.model.ordering.OpenOrderTypeEnum.ARTICLES;
import static org.meveo.model.ordering.OpenOrderTypeEnum.PRODUCTS;

@Stateless
public class OpenOrderQuoteApi {

    @Inject
    private OpenOrderQuoteService openOrderQuoteService;

    @Inject
    private OpenOrderSettingService openOrderSettingService;

    public OpenOrderQuote changeStatus(String code, OpenOrderQuoteStatusEnum newStatus) {
        // Check existence
        OpenOrderQuote ooq = openOrderQuoteService.findByCode(code);

        if (ooq == null) {
            throw new EntityDoesNotExistsException("No Open Order Quote found with code '" + code + "'");
        }

        // Find General Settings
        List<OpenOrderSetting> settings = openOrderSettingService.list();

        if (CollectionUtils.isEmpty(settings) || settings.size() == 0) {
            throw new BusinessApiException("No Open Order setting found");
        }

        OpenOrderSetting setting = openOrderSettingService.list().get(0);

        // check by new status
        switch (newStatus) {
            case DRAFT:
                if (ooq.getStatus() != DRAFT) {
                    throw new BusinessApiException("Cannot change status '" + ooq.getStatus() + "' to DRAFT");
                }

                break;

            case WAITING_VALIDATION:
                // ASK VALIDATION feature activated and status in(DRAFT, REJECTED)
                // check that Product/Article list is not empty(depending on type)

                if (!setting.getUseManagmentValidationForOOQuotation()) {
                    throw new BusinessApiException("ASK VALIDATION feature is not activated");
                }

                if (!(ooq.getStatus() == DRAFT || ooq.getStatus() == REJECTED)) {
                    throw new BusinessApiException("Open Order Quote status must be DRAFT or REJECTED");
                }

                if (ooq.getOpenOrderType() == ARTICLES && CollectionUtils.isEmpty(ooq.getArticles())) {
                    throw new BusinessApiException("Cannot ask validation for Open Order Quote without Articles");
                }

                if (ooq.getOpenOrderType() == PRODUCTS && CollectionUtils.isEmpty(ooq.getProducts())) {
                    throw new BusinessApiException("Cannot ask validation for Open Order Quote without Products");
                }

                break;

            case ACCEPTED:
                if (ooq.getStatus() != SENT) {
                    throw new BusinessApiException("Open Order Quote status must be SENT");
                }

                break;

            case SENT:
                // ASK VALIDATION feature NOT activated and status in(DRAFT)
                // status in (VALIDATED, SENT)
                // check that Product/Article list is not empty(depending on type)

                if (setting.getUseManagmentValidationForOOQuotation()) {
                    throw new BusinessApiException("ASK VALIDATION feature shall not be activated");
                }

                if (ooq.getStatus() != VALIDATED) {
                    throw new BusinessApiException("Open Order Quote status must be VALIDATED");
                }

                break;
            case VALIDATED:
            case REJECTED:
                if (!setting.getUseManagmentValidationForOOQuotation()) {
                    throw new BusinessApiException("ASK VALIDATION feature is not activated");
                }

                if (ooq.getStatus() != WAITING_VALIDATION) {
                    throw new BusinessApiException("Open Order Quote status must be WAITING_VALIDATION");
                }

                break;

            case CANCELED:
                // Possible for all status : DRAFT, WAITING VALIDATION, REJECTED, VALIDATED, ACCEPTED, SENT
                break;

            default:
        }

        return openOrderQuoteService.changeStatus(ooq, newStatus);
    }

}