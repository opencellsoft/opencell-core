package org.meveo.api.dto.util;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.message.exception.InvalidDTOException;

/**
 * @author Edward P. Legaspi
 * @since Oct 29, 2013
 **/
public class BaseDtoHelper {

	public static BaseDto deserialize(ObjectMessage m) throws JMSException,
			InvalidDTOException {
		BaseDto msgDTO = (BaseDto) m.getObject();
		msgDTO.validate();
		return msgDTO;
	}

	public static ObjectMessage serialize(Session session, BaseDto dto)
			throws JMSException, InvalidDTOException {
		dto.validate();
		ObjectMessage msg = session.createObjectMessage(dto);
		return msg;
	}

}
