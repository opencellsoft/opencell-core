package org.meveo.security;

/**
 * Access to application entities - actions a user can perform
 * 
 * @author Andrius Karpavicius
 */
public enum AccessScopeEnum {

    /**
     * Create
     */
    CREATE("POST"),

    /**
     * Update
     */
    UPDATE("PUT"),

    /**
     * Find/search
     */
    LIST("GET"),

    /**
     * Delete
     */
    DELETE("DELETE");

    private String httpMethod;

    AccessScopeEnum(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * Return a corresponding HTTP method
     */
    public String getHttpMethod() {
        return httpMethod;
    }

    public String getLabel() {
        return this.getClass().getSimpleName() + "." + this.name();
    }
}