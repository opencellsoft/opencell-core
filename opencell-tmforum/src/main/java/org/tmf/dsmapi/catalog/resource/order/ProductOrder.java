package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.tmf.dsmapi.catalog.resource.RelatedParty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@XmlRootElement(name = "ProductOrder", namespace = "http://www.tmforum.org")
@XmlType(name = "ProductOrder", namespace = "http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class ProductOrder implements Serializable {

    private static final long serialVersionUID = -4883520016795545598L;

    private String id;
    private String href;
    private String externalId;
    private String priority;
    private String description;
    private String category;
    private String state;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date orderDate;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date completionDate;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date requestedStartDate;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date requestedCompletionDate;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date expectedCompletionDate;
    private String notificationContact;

    private List<Note> note;
    private List<RelatedParty> relatedParty;
    private List<ProductOrderItem> orderItem;

    private CustomFieldsDto customFields = new CustomFieldsDto();

    /**
     * Payment methods requested. Note: only the first one will be used.
     */
    @XmlElementWrapper(name = "paymentMethods")
    @XmlElement(name = "methodOfPayment")
    private List<PaymentMethodDto> paymentMethods;

    private String dueDateDelayEL;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Date getRequestedStartDate() {
        return requestedStartDate;
    }

    public void setRequestedStartDate(Date requestedStartDate) {
        this.requestedStartDate = requestedStartDate;
    }

    public Date getRequestedCompletionDate() {
        return requestedCompletionDate;
    }

    public void setRequestedCompletionDate(Date requestedCompletionDate) {
        this.requestedCompletionDate = requestedCompletionDate;
    }

    public Date getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Date expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public String getNotificationContact() {
        return notificationContact;
    }

    public void setNotificationContact(String notificationContact) {
        this.notificationContact = notificationContact;
    }

    public List<Note> getNote() {
        return note;
    }

    public void setNote(List<Note> note) {
        this.note = note;
    }

    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public List<ProductOrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<ProductOrderItem> orderItem) {
        this.orderItem = orderItem;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public List<PaymentMethodDto> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethodDto> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getDueDateDelayEL() {
        return dueDateDelayEL;
    }

    public void setDueDateDelayEL(String dueDateDelayEL) {
        this.dueDateDelayEL = dueDateDelayEL;
    }
}