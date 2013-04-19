package org.meveo.model.billing;

public enum WalletOperationStatusEnum {
	    OPEN(1, "walletOperationStatus.open"),
	    TREATED(1, "walletOperationStatus.treated"),
	    CANCELED(2, "walletOperationStatus.canceled"),
	    RESERVED(3, "walletOperationStatus.reserved"), 
	    TO_RERATE(4, "walletOperationStatus.to_rerate"), ;

	    private Integer id;
	    private String label;

	    private WalletOperationStatusEnum(Integer id, String label) {
	        this.id = id;
	        this.label = label;
	    }

	    public String getLabel() {
	        return label;
	    }

	    public Integer getId() {
	        return id;
	    }

	    public static WalletOperationStatusEnum getValue(Integer id) {
	        if (id != null) {
	            for (WalletOperationStatusEnum status : values()) {
	                if (id.equals(status.getId())) {
	                    return status;
	                }
	            }
	        }
	        return null;
	    }

	    public String toString() {
	        return label.toString();
	    }
}
