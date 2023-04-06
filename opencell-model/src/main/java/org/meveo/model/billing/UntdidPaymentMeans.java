package org.meveo.model.billing;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@Cacheable
@Table(name = "untdid_4461_payment_means")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "untdid_4461_payment_means_seq"), })
public class UntdidPaymentMeans extends AuditableEntity {

	private static final long serialVersionUID = -1024979001287985755L;

	@Column(name = "code", length = 500)
	@Size(max = 20)
	private String code;
	
	@Column(name = "code_name", length = 500)
	@Size(max = 20)
	private String codeName;
	  
	@Column(name = "usage_in_en16931", length = 500)
	@Size(max = 20)
	private String usageEN16931;

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getUsageEN16931() {
		return usageEN16931;
	}

    public void setUsageEN16931(String usageEN16931) {
        this.usageEN16931 = usageEN16931;
    }
	
}
