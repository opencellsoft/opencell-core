package org.meveo.commons.encryption;

public class EncryptionException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 5175511780611978989L;
    
    
    public EncryptionException() {
        super();
    }

    public EncryptionException(Throwable cause) {
        super(cause);
    }

    public EncryptionException(String message) {
        super(message);
    }

}
