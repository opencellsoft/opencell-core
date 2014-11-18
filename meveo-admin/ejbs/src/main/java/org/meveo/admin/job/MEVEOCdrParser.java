package org.meveo.admin.job;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Named;

import org.meveo.model.BaseEntity;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.meveo.service.medina.impl.EDRDAO;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.meveo.service.medina.impl.InvalidFormatException;

@Named
public class MEVEOCdrParser implements CSVCDRParser {

	static SimpleDateFormat sdf1 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static SimpleDateFormat sdf2 = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	class CDR implements Serializable {
		private static final long serialVersionUID = -536798105625877375L;
		public BigDecimal quantity;
		public String user_id;
		public String service_id;
		public String id_type;
		public String unit;
		public long timestamp;

		public String toString() {
			return quantity + "\t" + user_id + "\t" + service_id + "\t"
					+ id_type + "\t" + unit + "\t"
					+ sdf1.format(new Date(timestamp));

		}
	}

	String batchName;
	long fileDate;

	@Override
	public void init(File CDRFile) {
		batchName = "ASG_" + CDRFile.getName();
		fileDate = CDRFile.lastModified();
	}

	@Override
	public String getOriginBatch() {
		return batchName == null ? "ASG_CONS_CSV" : batchName;
	}

	@Override
	public Serializable getCDR(String line) throws InvalidFormatException {
		CDR cdr = new CDR();
		try {
			String[] fields = line.split("\t");
			if (fields.length == 0) {
				throw new InvalidFormatException(line, "record empty");
			} else if (fields.length < 3) {
				throw new InvalidFormatException(line, "only " + fields.length
						+ " in the record");
			} else {
				cdr.quantity = new BigDecimal(fields[0]);
				cdr.user_id = fields[1];
				if (cdr.user_id == null) {
					throw new InvalidAccessException(line, "userId is empty");
				}
				cdr.service_id = fields[2];
				if (fields.length <= 3) {
					cdr.id_type = "ID";
				} else {
					cdr.id_type = fields[3];
				}
				if (fields.length <= 4) {
					cdr.unit = "BYTE";
				} else {
					cdr.unit = fields[4];
				}
				if (fields.length <= 5) {
					cdr.timestamp = fileDate;
				} else {
					try {
						cdr.timestamp = sdf1.parse(fields[5]).getTime();
					} catch (Exception e1) {
						cdr.timestamp = sdf2.parse(fields[5]).getTime();
					}
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
		return cdr.user_id + "_" + cdr.service_id + "_" + cdr.id_type + "_"
				+ cdr.timestamp;
	}

	@Override
	public String getAccessUserId(Serializable cdr)
			throws InvalidAccessException {
		String result = ((CDR) cdr).user_id;
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
		result.setParameter1(cdr.service_id);
		result.setParameter2(cdr.user_id);
		result.setParameter3(cdr.id_type);
		result.setParameter4(cdr.unit);
		return result;
	}

	@Override
	public String getCDRLine(Serializable cdr, String reason) {
		return ((CDR) cdr).toString() + "\t" + reason;
	}

}
