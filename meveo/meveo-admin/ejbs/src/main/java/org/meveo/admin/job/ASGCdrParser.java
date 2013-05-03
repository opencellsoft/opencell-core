package org.meveo.admin.job;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Named;

import org.meveo.service.medina.impl.CSVCDRParser;
import org.meveo.service.medina.impl.EDRDAO;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.meveo.service.medina.impl.InvalidFormatException;

@Named
public class ASGCdrParser implements CSVCDRParser{

	class CDR implements Serializable{
		private static final long serialVersionUID = -536798105625877375L;
		public long quantity;
		public String user_id;
		public String service_id;
		public String id_type;
		public String unit;
		public long timestamp; 	
	}
	
	String batchName;

	@Override
	public void init(String CDRFileName){
		batchName="ASG_"+CDRFileName;
	}
	
	@Override
	public String getOriginBatch() {
		return batchName==null?"ASG_CONS_CSV":batchName;
	}

	@Override
	public Serializable getCDR(String line) throws InvalidFormatException {
		CDR cdr=new CDR();
		
		return cdr;
	}

	@Override
	public String getOriginRecord(Serializable object) {
		CDR cdr = (CDR)object;
		return cdr.user_id+"_"+cdr.service_id+"_"+cdr.timestamp;
	}

	@Override
	public String getAccessUserId(Serializable cdr)
			throws InvalidAccessException {
		String result=((CDR)cdr).user_id;
		if(result==null || result.trim().length()==0){
			throw new InvalidAccessException();
		}
		if(((CDR)cdr).service_id!=null && (((CDR)cdr).service_id.length()>0) ){
			result+="_"+((CDR)cdr).service_id;
		}
		return null;
	}

	@Override
	public EDRDAO getEDR(Serializable object) {
		CDR cdr=(CDR)object;
		EDRDAO result =  new EDRDAO();
		result.setEventDate(new Date(cdr.timestamp));
		return result;
	}
	
}


