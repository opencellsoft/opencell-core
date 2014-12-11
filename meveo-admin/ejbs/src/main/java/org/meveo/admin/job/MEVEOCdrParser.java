package org.meveo.admin.job;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Named;

import org.meveo.model.BaseEntity;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.meveo.service.medina.impl.EDRDAO;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.meveo.service.medina.impl.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class MEVEOCdrParser implements CSVCDRParser {

	private static Logger log = LoggerFactory.getLogger(MEVEOCdrParser.class);

	static SimpleDateFormat sdf1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static SimpleDateFormat sdf2 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	static MessageDigest messageDigest = null;
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}
	}

	class CDR implements Serializable {
		private static final long serialVersionUID = -536798105625877375L;
		public long timestamp;
		public String access_id;
		public BigDecimal quantity;
		public String param1;
		public String param2;
		public String param3;
		public String param4;

		public String toString() {
			return sdf1.format(new Date(timestamp)) + ";" + quantity + ";"
					+ access_id + ";" + param1 + ";" + param2 + ";" + param3
					+ ";" + param4;

		}
	}

	String batchName;
	long fileDate;

	@Override
	public void init(File CDRFile) {
		batchName = "CDR_" + CDRFile.getName();
		fileDate = CDRFile.lastModified();
	}

	@Override
	public String getOriginBatch() {
		return batchName == null ? "CDR_CONS_CSV" : batchName;
	}

	@Override
	public Serializable getCDR(String line) throws InvalidFormatException {
		CDR cdr = new CDR();
		try {
			String[] fields = line.split(";");
			if (fields.length == 0) {
				throw new InvalidFormatException(line, "record empty");
			} else if (fields.length < 4) {
				throw new InvalidFormatException(line, "only " + fields.length
						+ " in the record");
			} else {
				try {
					cdr.timestamp = sdf1.parse(fields[0]).getTime();
				} catch (Exception e1) {
					cdr.timestamp = sdf2.parse(fields[0]).getTime();
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
		CDR cdr = (CDR) object;
		String result = cdr.toString();
		if (messageDigest != null) {
			synchronized (messageDigest) {
				messageDigest.reset();
				messageDigest.update(result.getBytes(Charset.forName("UTF8")));
				final byte[] resultByte = messageDigest.digest();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < resultByte.length; ++i) {
					sb.append(Integer.toHexString(
							(resultByte[i] & 0xFF) | 0x100).substring(1, 3));
				}
				result = sb.toString();
			}
		}
		return result;
	}

	@Override
	public String getAccessUserId(Serializable cdr)
			throws InvalidAccessException {
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
		result.setQuantity(cdr.quantity.setScale(BaseEntity.NB_DECIMALS,
				RoundingMode.HALF_UP));
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
