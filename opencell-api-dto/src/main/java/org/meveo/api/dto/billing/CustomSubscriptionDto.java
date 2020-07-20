package org.meveo.api.dto.billing;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;

import javax.xml.bind.annotation.*;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(FIELD)
public class CustomSubscriptionDto {

    private static final long serialVersionUID = -6021918810749866649L;

    @XmlAttribute(required = true)
    private String code;

    @XmlElement(required = true)
    private String userAccount;

    @XmlElement(required = true)
    private Date subscriptionDate;

    @XmlElement(required = true)
    private Date terminationDate;

    private SubscriptionStatusEnum status;

    private boolean renewed;

    @XmlElement
    private CustomServiceInstancesDto services = new CustomServiceInstancesDto();

    public CustomSubscriptionDto() {
        super();
    }

    public CustomSubscriptionDto(Subscription subscription) {
        code = subscription.getCode();
        status = subscription.getStatus();

        if (subscription.getUserAccount() != null) {
            userAccount = subscription.getUserAccount().getCode();
        }
        renewed = subscription.isRenewed();

        subscriptionDate = subscription.getSubscriptionDate();
        terminationDate = subscription.getTerminationDate();
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public boolean isRenewed() {
        return renewed;
    }

    public void setRenewed(boolean renewed) {
        this.renewed = renewed;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public SubscriptionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatusEnum status) {
        this.status = status;
    }

    public CustomServiceInstancesDto getServices() {
        return services;
    }

    public void setServices(CustomServiceInstancesDto services) {
        this.services = services;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}