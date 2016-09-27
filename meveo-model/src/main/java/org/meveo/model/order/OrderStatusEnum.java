package org.meveo.model.order;

/**
 * Order status lifecycle
 * 
 * @author Andrius Karpavicius
 * 
 */
public enum OrderStatusEnum {

    /**
     * Order being prepared - used for new order entry via GUI
     */
    IN_CREATION(""),

    /**
     * The Acknowledged state is where an order has been received and has passed message and basic business validations.
     */
    ACKNOWLEDGED("Acknowledged"),
    /**
     * The In Progress state is where an order has passed the Order Feasibility check successfully and service delivery has started.
     */
    IN_PROGRESS("InProgress"),
    /**
     * The Cancelled state is where an In-Flight Order has been successfully cancelled.
     */
    CANCELLED("Cancelled"),
    /**
     * The Completed state is where an order has complete provision and the service is now active.
     */
    COMPLETED("Completed"),
    /**
     * The Rejected state is where: - An order failed the Order Feasibility check - Invalid information is provided through the order request - The order request fails to meet
     * business rules for ordering.
     */
    REJECTED("Rejected"),

    /**
     * The Pending state is used when an order is currently in a waiting stage for an action/activity to be completed before the order can progress further, pending order amend or
     * cancel assessment. In situations where Access Seeker action is required, an “information required” notification will be issued on transition into this state.
     * 
     * A pending stage can lead into auto cancellation of an order, if no action is taken within the defined timeframes to be described under the Agreement.
     */
    PENDING("Pending"),
    /**
     * The Held state is used when an order cannot be progressed due to an issue. SP has temporarily delayed completing an order to resolve an infrastructure shortfall to
     * facilitate supply of order. Upon resolution of the issue, the order will continue to progress.
     */
    HELD("Held"),
    /**
     * All Order items have failed which results in the entire Order has Failed.
     */
    FAILED("Failed"),
    /**
     * Some Order items have failed and some have succeeded so the entire Order is in a Partial state. This provides support for partial Failure of an Order
     */
    PARTIAL("Partial");

    private String apiState;

    private OrderStatusEnum(String apiState) {
        this.apiState = apiState;
    }

    public String getApiState() {
        return apiState;
    }

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }

    public static OrderStatusEnum valueByApiState(String apiState) {
        for (OrderStatusEnum enumValue : values()) {
            if (enumValue.getApiState().equals(apiState)) {
                return enumValue;
            }
        }
        return null;
    }
}