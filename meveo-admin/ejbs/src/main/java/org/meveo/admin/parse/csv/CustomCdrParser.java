package org.meveo.admin.parse.csv;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.IProvider;
import org.meveo.model.crm.Provider;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.meveo.service.medina.impl.EDRDAO;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.meveo.service.medina.impl.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi To test CdrParserProducer uncomment @CdrParser
 *         annotation.
 **/
@Named
public class CustomCdrParser implements CSVCDRParser {

	private static Logger log = LoggerFactory.getLogger(MEVEOCdrParser.class);

	DateTimeFormatter formatter1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	static MessageDigest messageDigest = null;
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		    log.error("No message digest of type MD5", e);
		}
	}

	class CDR implements Serializable, IProvider {
		private static final long serialVersionUID = -536798105625877375L;
		public long timestamp;
		public String access_id;
		public BigDecimal quantity;
		public String param1;
		public String param2;
		public String param3;
		public String param4;
		public transient Provider provider;
		
		public String toString() {
			return (new Date(timestamp)) + ";" + quantity + ";" + access_id + ";" + param1 + ";" + param2
					+ ";" + param3 + ";" + param4;
		}

        @Override
        public Provider getProvider() {
            return provider;
        }

        @Override
        public void setProvider(Provider provider) {
            this.provider = provider;            
        }
	}

	private String batchName;
	private String originBatch;
	private String username;

	@Override
	public void init(File CDRFile) {
		batchName = "CDR_" + CDRFile.getName();
	}

	@Override
	public void initByApi(String username, String ip) {
		originBatch = "API_" + ip;
		this.username = username;
	}

	@Override
	public String getOriginBatch() {
		if (StringUtils.isBlank(originBatch)) {
			return batchName == null ? "CDR_CONS_CSV" : batchName;
		} else {
			return originBatch;
		}
	}

	@Override
	public Serializable getCDR(String line) throws InvalidFormatException {
		CDR cdr = new CDR();
		try {
			String[] fields = line.split(";");
			if (fields.length == 0) {
				throw new InvalidFormatException(line, "record empty");
			} else if (fields.length < 4) {
				throw new InvalidFormatException(line, "only " + fields.length + " in the record");
			} else {
				try {
					DateTime dt = formatter1.parseDateTime(fields[0]);
					cdr.timestamp = dt.getMillis();
				} catch (Exception e1) {
					DateTime dt = formatter2.parseDateTime(fields[0]);
					cdr.timestamp = dt.getMillis();
				}
				cdr.quantity = new BigDecimal(fields[1]);

				cdr.access_id = fields[2];
				if (cdr.access_id == null) {
					throw new InvalidAccessException(line, "userId is empty");
				}
				cdr.param1 = fields[3];
				if (fields.length <= 4) {
					cdr.param2 = null;
				} else {
					cdr.param2 = fields[4];
				}
				if (fields.length <= 5) {
					cdr.param3 = null;
				} else {
					cdr.param3 = fields[5];
				}
				if (fields.length <= 6) {
					cdr.param4 = null;
				} else {
					cdr.param4 = fields[6];
				}
			}
		} catch (Exception e) {
			throw new InvalidFormatException(line, e.getMessage());
		}
		return cdr;
	}

	@Override
	public String getOriginRecord(Serializable object) {
		String result = null;
		if (StringUtils.isBlank(username)) {
			CDR cdr = (CDR) object;
			result = cdr.toString();

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
		} else {
			return username + "_" + new Date().getTime();
		}

		return result;
	}

	@Override
	public String getAccessUserId(Serializable cdr) throws InvalidAccessException {
		String result = ((CDR) cdr).access_id;
		if (result == null || result.trim().length() == 0) {
			throw new InvalidAccessException(cdr);
		}
		/*
		 * if(((CDR)cdr).service_id!=null && (((CDR)cdr).service_id.length()>0)
		 * ){ result+="_"+((CDR)cdr).service_id; }
		 */
		return result;
	}

	@Override
	public EDRDAO getEDR(Serializable object) {
		CDR cdr = (CDR) object;
		EDRDAO result = new EDRDAO();
		result.setEventDate(new Date(cdr.timestamp));
		result.setOriginBatch(getOriginBatch());
		result.setOriginRecord(getOriginRecord(object));
		result.setQuantity(cdr.quantity.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
		result.setParameter1(cdr.param1);
		result.setParameter2(cdr.param2);
		result.setParameter3(cdr.param3);
		result.setParameter4(cdr.param4);
		return result;
	}

	@Override
	public String getCDRLine(Serializable cdr, String reason) {
		return ((CDR) cdr).toString() + ";" + reason;
	}

}
