package org.meveocrm.model.dwh;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;

@Entity
@Table(name = "DWH_CHART", uniqueConstraints = @UniqueConstraint(columnNames = {
		"CODE", "PROVIDER_ID" }))
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_BILLING_RUN_SEQ")
public class Chart extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7127515648757614672L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROLE_ID")
	Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MSR_QTY_ID")
	MeasurableQuantity measurableQuantity;

	@Column(name = "CSS_STYLE", length = 1000)
	String style;

	@Column(name = "CSS_STYLE_CLASS")
	String styleClass;

	@Column(name = "EXTENDER")
	String extender;

	@Column(name = "ISVISIBLE")
	Boolean isVisible = false;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public MeasurableQuantity getMeasurableQuantity() {
		return measurableQuantity;
	}

	public void setMeasurableQuantity(MeasurableQuantity measurableQuantity) {
		this.measurableQuantity = measurableQuantity;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getExtender() {
		return extender;
	}

	public void setExtender(String extender) {
		this.extender = extender;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

}
