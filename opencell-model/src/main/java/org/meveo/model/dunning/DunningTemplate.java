package org.meveo.model.dunning;

import static javax.persistence.FetchType.LAZY;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.communication.MessageTemplateTypeEnum;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.payments.ActionChannelEnum;


@Entity
@DiscriminatorValue("DUNNING_MEDIA")
public class DunningTemplate extends EmailTemplate {

	private static final long serialVersionUID = 5950324976559109922L;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private ActionChannelEnum channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_language_id")
    private TradingLanguage language;
	
	@Type(type = "numeric_boolean")
    @Column(name = "is_active")
    protected boolean isActive;

    @OneToMany(mappedBy = "actionNotificationTemplate", fetch = LAZY)
    private List<DunningAction> dunningActions;

    
	public DunningTemplate() {
		setType(MessageTemplateTypeEnum.DUNNING); 
	}

	public ActionChannelEnum getChannel() {
		return channel;
	}

	public void setChannel(ActionChannelEnum channel) {
		this.channel = channel;
	}

	public TradingLanguage getLanguage() {
		return language;
	}

	public void setLanguage(TradingLanguage language) {
		this.language = language;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<DunningAction> getDunningActions() {
		return dunningActions;
	}

	public void setDunningActions(List<DunningAction> dunningActions) {
		this.dunningActions = dunningActions;
	}
	
    @Override
    public int hashCode() {
        return 961 + ("DunningTemplate" + code).hashCode();
    }
    
	@Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof DunningTemplate)) {
            return false;
        }

        DunningTemplate other = (DunningTemplate) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (code == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!code.equals(other.getCode())) {
            return false;
        }
        return true;
    }

}
