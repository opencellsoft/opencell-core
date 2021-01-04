package org.meveo.model.cpq.commercial;

public enum CommercialOrderEnum {

	/*
	 * The order is being composed, it can be modified or even deleted
	 */
	DRAFT, 
	/*
	 * The order is taken into account and can no longer be modified. Its advancement rate drops to 0%. The command initiation script is started
	 */
	FINALIZED,
	/*
	 * The validated order was canceled before being fully completed. This status will stop the progress of the order. 
	 * On the other hand, no automatic customer regularization operation is planned
	 */
	CANCELED,
	/*
	 * The order has been completed completely. All the one-shot "Other" charges have been created and subscriptions 
	 * for the processing of subscriptions and the valuation of uses must be created
	 */
	COMPLETED,
	/*
	 * The order is completed and the "Subscriptions" created
	 */
	VALIDATED
}
