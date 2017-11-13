package org.meveo.keycloak.client;

import javax.validation.constraints.NotNull;

/**
 * @author Edward P. Legaspi
 * @created 10 Nov 2017
 * 
 * All are required.
 **/
public class KeycloakUserAccount {
    
    @NotNull
    private String username;
    
    @NotNull
    private String firstName;
    
    @NotNull
    private String lastName;
    
    @NotNull
    private String email;
    
    @NotNull
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
