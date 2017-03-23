package org.meveo.model.order;

/**
 * Action requested on a product or product offer
 * 
 * @author Andrius Karpavicius
 * 
 */
public enum OrderItemActionEnum {

    /**
     * Order new product
     */
    ADD,

    /**
     * Modify existing ordered product
     */
    MODIFY,

//    /**
//     * Perform no change or ordered product
//     */
//    NO_CHANGE,

    /**
     * Delete ordered product
     */
    DELETE;

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}
