package org.meveo.api.dto.payment;

import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
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