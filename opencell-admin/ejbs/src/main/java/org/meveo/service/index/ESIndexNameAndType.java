package org.meveo.service.index;

import java.io.Serializable;

/**
 * A mapping between providerCode, classname, custom entity code (if applicable) and index name and type (if applicable)
 * 
 * A cache value: index name and type (if applicable)
 * 
 * @author Andrius Karpavicius
 */
public class ESIndexNameAndType implements Serializable {

    private static final long serialVersionUID = 2183194936642780997L;

    /**
     * Index name
     */
    private String indexName;

    /**
     * Entity type
     */
    private String type;

    /**
     * Constructor
     */
    public ESIndexNameAndType() {
    }

    public ESIndexNameAndType(String indexName, String type) {
        super();
        this.indexName = indexName;
        this.type = type;
    }

    @Override
    public String toString() {
        return indexName + ", " + type;
    }

    @Override
    public boolean equals(Object obj) {
        ESIndexNameAndType other = (ESIndexNameAndType) obj;

        if (other == null) {
            return false;
        }

        return (indexName + "_" + type).equals(other.getIndexName() + "_" + other.getType());
    }

    @Override
    public int hashCode() {
        return (indexName + "_" + type).hashCode();
    }

    public String getIndexName() {
        return indexName;
    }

    public String getType() {
        return type;
    }

    /**
     * Does the index and type match
     * 
     * @param matchIndexName Full index name to match
     * @param matchType Type to match (optional)
     * @return True if index and type match
     */
    public boolean isMatchIndexNameAndType(String matchIndexName, String matchType) {

        return (this.indexName.equals(matchIndexName) || matchIndexName.startsWith(this.indexName))
                && ((this.type == null && matchType == null) || (this.type != null && this.type.equals(matchType)));
    }
}