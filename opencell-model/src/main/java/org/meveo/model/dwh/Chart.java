package org.meveo.model.dwh;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;

@Entity
@Cacheable
@ModuleItem
@ExportIdentifier({ "code"})
@Table(name = "dwh_chart", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@Inheritance(strategy = InheritanceType.JOINED)
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "dwh_chart_seq"), })
public class Chart extends BusinessEntity {

    private static final long serialVersionUID = 7127515648757614672L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "msr_qty_id")
    private MeasurableQuantity measurableQuantity;

    @Column(name = "width", length = 10)
    @Size(max = 10)
    private String width = "500px";

    @Column(name = "height", length = 10)
    @Size(max = 10)
    private String height = "300px";

    @Column(name = "css_style", length = 1000)
    @Size(max = 1000)
    private String style;

    @Column(name = "css_style_class", length = 255)
    @Size(max = 255)
    private String styleClass;

    @Column(name = "extender", length = 255)
    @Size(max = 255)
    private String extender;

    @Type(type="numeric_boolean")
    @Column(name = "visible")
    private Boolean visible = false;

    @Transient
    private User user;

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

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
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
        return visible;
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

}
