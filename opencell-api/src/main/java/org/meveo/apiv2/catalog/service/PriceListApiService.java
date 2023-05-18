package org.meveo.apiv2.catalog.service;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ListUtils;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.service.catalog.impl.PriceListService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.util.Optional;

@Stateless
public class PriceListApiService {

    @Inject
    private PriceListService priceListService; 

    @TransactionAttribute
    public void updateStatus(String priceListCode, PriceListStatusEnum newStatus) {

        PriceList priceListToUpdate = priceListService.findByCode(priceListCode);
        if(priceListToUpdate == null) {
            throw new EntityDoesNotExistsException(PriceList.class, priceListCode);
        }

        switch (newStatus) {
            case ACTIVE:
                if(priceListToUpdate.getStatus() != PriceListStatusEnum.DRAFT) {
                    throw new BusinessApiException("Only DRAFT PriceList are eligible to ACTIVE status");
                }

                if(ListUtils.isEmtyCollection(priceListToUpdate.getLines())) {
                    throw new BusinessApiException("Cannot activate PriceList without lines");
                }

                priceListToUpdate.getLines()
                                 .stream()
                                 .filter(pll -> pll.getRate() != null || (pll.getPricePlan()!= null && !pll.getPricePlan().getVersions().isEmpty()  && pll.getPricePlan()
                                                                                                          .getVersions()
                                                                                                          .stream()
                                                                                                          .anyMatch(ppv -> ppv.getStatus().equals(VersionStatusEnum.PUBLISHED))))
                                 .findAny()
                                 .orElseThrow(() -> new BusinessApiException("Cannot activate PriceList without lines having a price or active PricePlan"));
                if (ListUtils.isEmtyCollection(priceListToUpdate.getBrands())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getCustomerCategories())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getCreditCategories())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getCountries())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getCurrencies())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getLegalEntities())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getPaymentMethods())
                    && ListUtils.isEmtyCollection(priceListToUpdate.getSellers())
                ) {
                    throw new BusinessApiException("Cannot activate PriceList without application rules");
                }
                break;
            case CLOSED:
                if(priceListToUpdate.getStatus() != PriceListStatusEnum.ACTIVE) {
                    throw new BusinessApiException("Only ACTIVE PriceList are eligible to CLOSED status");
                }
                break;
            case ARCHIVED:
                if(priceListToUpdate.getStatus() != PriceListStatusEnum.DRAFT) {
                    throw new BusinessApiException("Only DRAFT PriceList are eligible to ARCHIVED status");
                }
                break;

            default:
                throw new BusinessApiException("Unsupported status");

        }

        priceListToUpdate.setStatus(newStatus);
        priceListService.update(priceListToUpdate);

    }

}
