package org.meveo.api.dto.payment;

import org.meveo.api.dto.response.SearchResponse;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class PaymentScheduleInstanceItemsDto.
 *
 * @author K.horri
 */
@XmlRootElement(name = "PaymentScheduleInstanceItemsDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleInstanceItemsDto extends SearchResponse {

    /**
     * The PaymentScheduleInstances Dtos.
     */
    @XmlElementWrapper
    @XmlElement(name = "paymentScheduleInstanceItems")
    private List<PaymentScheduleInstanceItemDto> paymentScheduleInstanceItems = new ArrayList<>();

    public List<PaymentScheduleInstanceItemDto> getPaymentScheduleInstanceItems() {
        return paymentScheduleInstanceItems;
    }

    public void setPaymentScheduleInstanceItems(List<PaymentScheduleInstanceItemDto> paymentScheduleInstanceItems) {
        this.paymentScheduleInstanceItems = paymentScheduleInstanceItems;
    }
}