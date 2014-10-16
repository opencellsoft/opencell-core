/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.communication.email;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.communication.MessageSenderConfig;

@Entity
@DiscriminatorValue("EMAIL")
public class EmailSenderConfig extends MessageSenderConfig {

	private static final long serialVersionUID = -7349972726209816384L;

	@Column(name = "SMTP_HOST")
	private String SMTPHost;

	@Column(name = "SMTP_PORT")
	private Integer SMTPPort;

	@Column(name = "LOGIN", length = 50)
	private String login;

	@Column(name = "PASSWORD", length = 50)
	private String password;

	@Column(name = "USE_SSL")
	private Boolean useSSL;

	@Column(name = "DEFAULT_FROM_EMAIL")
	private String defaultFromEmail;

	@Column(name = "DEFAULT_REPLY_EMAIL")
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
		return "{host:" + SMTPHost + ";port:" + SMTPPort + ";login:" + login + ";passwordIsNull:"
				+ (password == null) + ";useSSL:" + useSSL + ";from:" + defaultFromEmail
				+ ";reply:" + defaultReplyEmail;
	}

}
