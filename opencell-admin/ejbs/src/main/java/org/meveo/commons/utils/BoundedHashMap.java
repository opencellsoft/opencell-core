package org.meveo.commons.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class BoundedHashMap<K,V> extends LinkedHashMap<K,V> {
    
	private static final long serialVersionUID = -1431359474927137743L;
	
	private int maxSize;

    public BoundedHashMap(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}