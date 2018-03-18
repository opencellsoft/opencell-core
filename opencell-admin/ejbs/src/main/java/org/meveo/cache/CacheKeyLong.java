/**
 * 
 */
package org.meveo.cache;

import java.io.Serializable;

/**
 * Represents a composite cache key distinguishing a provider
 * 
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
public class CacheKeyLong implements Serializable {

    private static final long serialVersionUID = 96958085546470864L;

    /**
     * Provider code
     */
    private String provider;

    /**
     * Key
     */
    private Long key;

    /**
     * @param provider Provider code
     * @param key Cache key
     */
    public CacheKeyLong(String provider, Long key) {
        super();
        this.provider = provider;
        this.key = key;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CacheKeyLong)) {
            return false;
        }
        CacheKeyLong other = (CacheKeyLong) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        if (provider == null) {
            if (other.provider != null) {
                return false;
            }
        } else if (!provider.equals(other.provider)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return (provider != null ? provider : "") + "/" + key;
    }
}