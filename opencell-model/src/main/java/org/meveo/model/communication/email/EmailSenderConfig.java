/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.communication.email;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.communication.MessageSenderConfig;

@Entity
@DiscriminatorValue("EMAIL")
public class EmailSenderConfig extends MessageSenderConfig {

    private static final long serialVersionUID = -7349972726209816384L;

    @Column(name = "smtp_host", length = 255)
    @Size(max = 255)
    private String SMTPHost;

    @Column(name = "smtp_port")
    private Integer SMTPPort;

    @Column(name = "login", length = 50)
    @Size(max = 50)
    private String login;

    @Column(name = "password", length = 50)
    @Size(max = 50)
    private String password;

    @Type(type = "numeric_boolean")
    @Column(name = "use_ssl")
    private Boolean useSSL;

    @Column(name = "default_from_email", length = 255)
    @Size(max = 255)
    private String defaultFromEmail;

    @Column(name = "default_reply_email", length = 255)
    @Size(max = 255)
    private String defaultReplyEmail;

    public String getSMTPHost() {
        return SMTPHost;
    }

    public void setSMTPHost(String sMTPHost) {
        SMTPHost = sMTPHost;
    }

    public Integer getSMTPPort() {
        return SMTPPort;
    }

    public void setSMTPPort(Integer sMTPPort) {
        SMTPPort = sMTPPort;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getDefaultFromEmail() {
        return defaultFromEmail;
    }

    public void setDefaultFromEmail(String defaultFromEmail) {
        this.defaultFromEmail = defaultFromEmail;
    }

    public String getDefaultReplyEmail() {
        return defaultReplyEmail;
    }

    public void setDefaultReplyEmail(String defaultReplyEmail) {
        this.defaultReplyEmail = defaultReplyEmail;
    }

    public String toString() {
        return "{host:" + SMTPHost + ";port:" + SMTPPort + ";login:" + login + ";passwordIsNull:" + (password == null) + ";useSSL:" + useSSL + ";from:" + defaultFromEmail
                + ";reply:" + defaultReplyEmail;
    }

}
