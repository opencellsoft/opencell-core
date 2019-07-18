package org.meveo.api.dto.response.generic;

public class SimpleGenericValue<T> {
    
    private T value;
    
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
}
