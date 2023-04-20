package org.meveo.model.communication.sms;

import org.meveo.model.communication.MessageTemplate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("SMS")
public class SMSTemplate extends MessageTemplate {


}
