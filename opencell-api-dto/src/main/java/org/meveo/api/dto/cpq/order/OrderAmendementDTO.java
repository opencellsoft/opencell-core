package org.meveo.api.dto.cpq.order;

import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.billing.ServiceToUpdateDto;

import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("serial") 
public class OrderAmendementDTO extends BaseEntityDto {
	
	/** The subscription code. */
	@Schema(description = "The subscription code") 
    private String subscriptionCode;
	
	/** The consumer. */
	@Schema(description = "The consumer") 
    private String consumer;
 
    private List<ServiceToUpdateDto> productsToSuspend = new ArrayList<ServiceToUpdateDto>();
    
    private List<ServiceToUpdateDto> productsToReactivate = new ArrayList<ServiceToUpdateDto>();
    
    private List<ServiceToUpdateDto> productsToTerminate = new ArrayList<ServiceToUpdateDto>();
    
    private List<ServiceToUpdateDto> productsToActivate = new ArrayList<ServiceToUpdateDto>();
    
    private List<ServiceToUpdateDto> productsToRestart = new ArrayList<ServiceToUpdateDto>();
     

    public OrderAmendementDTO() {
	}
 
	public OrderAmendementDTO(String subscriptionCode, String consumer, List<ServiceToUpdateDto> productsToSuspend,
			List<ServiceToUpdateDto> productsToReactivate, List<ServiceToUpdateDto> productsToTerminate,
			List<ServiceToUpdateDto> productsToActivate, List<ServiceToUpdateDto> productsToRestart) {
		super();
		this.subscriptionCode = subscriptionCode;
		this.consumer = consumer;
		this.productsToSuspend = productsToSuspend;
		this.productsToReactivate = productsToReactivate;
		this.productsToTerminate = productsToTerminate;
		this.productsToActivate = productsToActivate;
		this.productsToRestart = productsToRestart;
	}

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public List<ServiceToUpdateDto> getProductsToSuspend() {
		return productsToSuspend;
	}

	public void setProductsToSuspend(List<ServiceToUpdateDto> productsToSuspend) {
		this.productsToSuspend = productsToSuspend;
	}

	public List<ServiceToUpdateDto> getProductsToReactivate() {
		return productsToReactivate;
	}

	public void setProductsToReactivate(List<ServiceToUpdateDto> productsToReactivate) {
		this.productsToReactivate = productsToReactivate;
	}

	public List<ServiceToUpdateDto> getProductsToTerminate() {
		return productsToTerminate;
	}

	public void setProductsToTerminate(List<ServiceToUpdateDto> productsToTerminate) {
		this.productsToTerminate = productsToTerminate;
	}

	public List<ServiceToUpdateDto> getProductsToActivate() {
		return productsToActivate;
	}

	public void setProductsToActivate(List<ServiceToUpdateDto> productsToActivate) {
		this.productsToActivate = productsToActivate;
	}

	public List<ServiceToUpdateDto> getProductsToRestart() {
		return productsToRestart;
	}

	public void setProductsToRestart(List<ServiceToUpdateDto> productsToRestart) {
		this.productsToRestart = productsToRestart;
	}
 
}
