package org.meveo.service.script.todelete;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mediation_DuplicatedCheck extends Script {

	private static final Logger log = LoggerFactory.getLogger(Mediation_DuplicatedCheck.class);
	static MessageDigest messageDigest = null;
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			
		}
	}
	EdrService edrService = (EdrService) getServiceInterface("EdrService");
	
	public void execute(Map<String, Object> initContext,Provider provider)throws BusinessException{
		log.info("Execute...");
		String originBatch = (String) initContext.get("origine_filename");
		String originRecord = getOriginRecord(initContext.get("record"));
		boolean result = edrService.duplicateFound(provider, originBatch, originRecord);
		initContext.put("duplicateFound", Boolean.valueOf(result));
		log.info("Execute result:"+result);
	}
	
	public String getOriginRecord(Object object) {
		String result = object.toString();
        if (messageDigest != null) {
			synchronized (messageDigest) {
					messageDigest.reset();
					messageDigest.update(result.getBytes(Charset.forName("UTF8")));
					final byte[] resultByte = messageDigest.digest();
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < resultByte.length; ++i) {
						sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
					}
					result = sb.toString();
			}
		}
		return result;
	}
}

