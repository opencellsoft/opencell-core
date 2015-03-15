package org.meveo.model.jaxb.subscription;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "access"
})
@XmlRootElement(name = "accesses")
public class Accesses {

	protected List<Access> access;

	public Accesses(){}
	
	public Accesses(org.meveo.model.billing.Subscription sub,String dateFormat) {
		if(sub!=null && sub.getAccessPoints()!=null){
			access= new ArrayList<Access>(sub.getAccessPoints().size());
			for(org.meveo.model.mediation.Access acc:sub.getAccessPoints()){
				access.add(new Access(acc,dateFormat));
			}
		}
	}

	public List<Access> getAccess(){
		if(access==null){
			access=new ArrayList<Access>();
		}
		return access;
	}
}
