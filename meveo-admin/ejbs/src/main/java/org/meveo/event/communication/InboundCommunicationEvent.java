package org.meveo.event.communication;

import org.meveo.api.dto.communication.CommunicationRequestDto;


public class InboundCommunicationEvent {

	CommunicationRequestDto communicationRequestDto;
	
	public InboundCommunicationEvent(){
		
	}

	/**
	 * @return the communicationRequestDto
	 */
	public CommunicationRequestDto getCommunicationRequestDto() {
		return communicationRequestDto;
	}

	/**
	 * @param communicationRequestDto the communicationRequestDto to set
	 */
	public void setCommunicationRequestDto(CommunicationRequestDto communicationRequestDto) {
		this.communicationRequestDto = communicationRequestDto;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InboundCommunicationEvent [communicationRequestDto=" + communicationRequestDto + "]";
	}
	
	
}
