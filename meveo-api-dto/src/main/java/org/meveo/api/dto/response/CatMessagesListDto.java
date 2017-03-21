package org.meveo.api.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CatMessagesDto;

@XmlRootElement(name = "CatMessages")
@XmlAccessorType(XmlAccessType.FIELD)
public class CatMessagesListDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<CatMessagesDto> catMessage;

	public List<CatMessagesDto> getCatMessage() {
		if (catMessage == null) {
			catMessage = new ArrayList<CatMessagesDto>();
		}
		return catMessage;
	}

	public void setCatMessage(List<CatMessagesDto> catMessage) {
		this.catMessage = catMessage;
	}

	@Override
	public String toString() {
		return "CatMessagesListDto [catMessage=" + catMessage + "]";
	}

	
}
