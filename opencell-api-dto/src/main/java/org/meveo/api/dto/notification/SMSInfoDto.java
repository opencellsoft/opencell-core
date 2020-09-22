package org.meveo.api.dto.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SMSInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMSInfoDto {

    /**
     *  targetType String NUMBER|S|C|CA|BA|UA
     *  NUMBER : phone number
     *  S : Seller
     *  C : Customer
     *  CA : Customer Account
     *  BA : Billing Account
     *  UA : User Account
     * */
    private String targetType;

    /**
     *  target represent the code of the entity
     *  if targetType set to +XXXXX-XXXX|S
     *  the target will be the seller code
     */
    @XmlElement(name = "target")
    private String code;

    private String body;

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}