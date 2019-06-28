/**
 * 
 */
package org.meveo.model.payments;

/**
 * @author anasseh
 *
 */
public enum PaymentOrRefundEnum {
  PAYMENT(OperationCategoryEnum.DEBIT),REFUND((OperationCategoryEnum.CREDIT));
    
    private OperationCategoryEnum operationCategoryToProcess;
    
    PaymentOrRefundEnum(OperationCategoryEnum operationCategoryToProcess) {
        this.operationCategoryToProcess = operationCategoryToProcess;
    }

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
    
    /**
     * @return the operationCategoryToProcess
     */
    public OperationCategoryEnum getOperationCategoryToProcess() {
        return operationCategoryToProcess;
    }

    /**
     * @param operationCategoryToProcess the operationCategoryToProcess to set
     */
    public void setOperationCategoryToProcess(OperationCategoryEnum operationCategoryToProcess) {
        this.operationCategoryToProcess = operationCategoryToProcess;
    }
    
    
}
