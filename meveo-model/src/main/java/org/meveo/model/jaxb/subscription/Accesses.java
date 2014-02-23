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
	
	public List<Access> getAccess(){
		if(access==null){
			access=new ArrayList<Access>();
		}
		return access;
	}
}
