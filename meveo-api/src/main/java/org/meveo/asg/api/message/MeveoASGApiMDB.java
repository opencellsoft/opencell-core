package org.meveo.asg.api.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CountryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Oct 29, 2013
 **/
@MessageDriven(name = "MeveoASGApiMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/meveoApi"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class MeveoASGApiMDB implements MessageListener {

	private Logger log = LoggerFactory.getLogger(MeveoASGApiMDB.class);

	@Override
	public void onMessage(Message msg) {
		try {
			log.debug("onMessage: afterBegin. msg={}, content={}", msg,
					((TextMessage) msg).getText().toString());
		} catch (JMSException e1) {
			e1.printStackTrace();
		}

		try {
			// BaseDto baseDto = BaseDtoHelper.deserialize((TextMessage) msg);
			// processAction(baseDto);
		} catch (Exception e) {
			log.error("onMessage: {}. msg was : {}", e.getMessage(), msg);
		}
	}

	@SuppressWarnings("unused")
	private void processAction(BaseDto msg) {
		log.debug("processAction: afterDeserialize. baseDto={}", msg);

		if (msg instanceof CountryDto) {

		}
	}

}
