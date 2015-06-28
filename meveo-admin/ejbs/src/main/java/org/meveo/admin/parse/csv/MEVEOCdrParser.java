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

@Named
public class MEVEOCdrParser implements CSVCDRParser {

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
		private String param5;
		private String param6;
		private String param7;
		private String param8;
		private String param9;
		private long dateParam1;
		private long dateParam2;
		private long dateParam3; 
		private long dateParam4; 
		private long dateParam5; 
		private BigDecimal decimalParam1; 
		private BigDecimal decimalParam2; 
		private BigDecimal decimalParam3; 
		private BigDecimal decimalParam4; 
		private BigDecimal decimalParam5;
		public transient Provider provider;

		public String toString() {
			return (new Date(timestamp)) + ";" + quantity + ";" + access_id + ";" + param1 + ";" + param2
					+ ";" + param3 + ";" + param4+ ";" +param5+ ";"+ param6+ ";" + param7+ ";" + param8+ ";" + param9 +";"
					 + dateParam1+ ";" + dateParam2+ ";" + dateParam3+ ";" + dateParam4+ ";" + dateParam5+ ";" + decimalParam1
					 +";" + decimalParam2+";" + decimalParam3+";" + decimalParam4+";" + decimalParam5;
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
				if (fields.length <= 7) {
					cdr.param5 = null;
				} else {
					cdr.param5 = fields[7];
				}
				if (fields.length <= 8) {
					cdr.param6 = null;
				} else {
					cdr.param6 = fields[8];
				}
				if (fields.length <= 9) {
					cdr.param7 = null;
				} else {
					cdr.param7 = fields[9];
				}
				if (fields.length <= 10) {
					cdr.param8 = null;
				} else {
					cdr.param8 = fields[10];
				}
				if (fields.length <= 11) {
					cdr.param9 = null;
				} else {
					cdr.param9 = fields[11];
				}
				
				if (fields.length <= 12 ||"".equals(fields[12])) {
					cdr.dateParam1=0;
				}else{
				try {
					DateTime dt = formatter1.parseDateTime(fields[12]);
					cdr.dateParam1 = dt.getMillis();
				} catch (Exception e1) {
					DateTime dt = formatter2.parseDateTime(fields[12]);
					cdr.dateParam1 = dt.getMillis();
				}}
				if (fields.length <= 13||"".equals(fields[13])) {
					cdr.dateParam2=0;
				}else{
				try {
					DateTime dt = formatter1.parseDateTime(fields[13]);
					cdr.dateParam2 = dt.getMillis();
				} catch (Exception e1) {
					DateTime dt = formatter2.parseDateTime(fields[13]);
					cdr.dateParam2 = dt.getMillis();
				}}
				if (fields.length <= 14||"".equals(fields[14])) {
					cdr.dateParam3=0;
				}else{
				try {
					DateTime dt = formatter1.parseDateTime(fields[14]);
					cdr.dateParam3 = dt.getMillis();
				} catch (Exception e1) {
					DateTime dt = formatter2.parseDateTime(fields[14]);
					cdr.dateParam3 = dt.getMillis();
				}}
				if (fields.length <= 15||"".equals(fields[15])) {
					cdr.dateParam4=0;
				}else{
				try {
					DateTime dt = formatter1.parseDateTime(fields[15]);
					cdr.dateParam4 = dt.getMillis();
				} catch (Exception e1) {
					DateTime dt = formatter2.parseDateTime(fields[15]);
					cdr.dateParam4 = dt.getMillis();
				}}
				if (fields.length <= 16 || "".equals(fields[16])) {
					cdr.dateParam5=0;
				}else{
				try {
					DateTime dt = formatter1.parseDateTime(fields[16]);
					cdr.dateParam5 = dt.getMillis();
				} catch (Exception e1) {
					DateTime dt = formatter2.parseDateTime(fields[16]);
					cdr.dateParam5 = dt.getMillis();
				}}
				if (fields.length <= 17 || "".equals(fields[17])) {
					cdr.decimalParam1=null;
				}else{
				   cdr.decimalParam1 = new BigDecimal(fields[17]);
				}if (fields.length <= 18 || "".equals(fields[18])) {
					cdr.decimalParam2=null;
				}else{
					cdr.decimalParam2 = new BigDecimal(fields[18]);
				}if (fields.length <= 19 || "".equals(fields[19])) {
					cdr.decimalParam3=null;
				}else{
					cdr.decimalParam3 = new BigDecimal(fields[19]);
				}if (fields.length <= 20 || "".equals(fields[20])) {
					cdr.decimalParam4=null;
				}else{
					cdr.decimalParam4 = new BigDecimal(fields[20]);
				}if (fields.length <= 21 || "".equals(fields[21])) {
					cdr.decimalParam5=null;
				}else{
				cdr.decimalParam5 = new BigDecimal(fields[21]);
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
		result.setParameter5(cdr.param5);
		result.setParameter6(cdr.param6);
		result.setParameter7(cdr.param7);
		result.setParameter8(cdr.param8);
		result.setParameter9(cdr.param9);
		result.setDateParam1(cdr.dateParam1!=0?new Date(cdr.dateParam1):null);
		result.setDateParam2(cdr.dateParam2!=0?new Date(cdr.dateParam2):null);
		result.setDateParam3(cdr.dateParam3!=0?new Date(cdr.dateParam3):null);
		result.setDateParam4(cdr.dateParam4!=0?new Date(cdr.dateParam4):null);
		result.setDateParam5(cdr.dateParam5!=0?new Date(cdr.dateParam5):null);
		result.setDecimalParam1(cdr.decimalParam1!=null ? cdr.decimalParam1.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
		result.setDecimalParam2(cdr.decimalParam2!=null ? cdr.decimalParam2.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
		result.setDecimalParam3(cdr.decimalParam3!=null ? cdr.decimalParam3.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
		result.setDecimalParam4(cdr.decimalParam4!=null ? cdr.decimalParam4.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
		result.setDecimalParam5(cdr.decimalParam5!=null ? cdr.decimalParam5.setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP):null);
		
		return result;
	}

	@Override
	public String getCDRLine(Serializable cdr, String reason) {
		return ((CDR) cdr).toString() + ";" + reason;
	}

}
