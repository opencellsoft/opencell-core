package org.meveo.admin.parse.csv;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.meveo.model.NotifiableEntity;

/**
 * A CDR record
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 */
@NotifiableEntity
public class CDR implements Serializable {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static final long serialVersionUID = 1L;
    private Date timestamp;
    private String access_id;
    private BigDecimal quantity;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private String param5;
    private String param6;
    private String param7;
    private String param8;
    private String param9;
    private Date dateParam1;
    private Date dateParam2;
    private Date dateParam3;
    private Date dateParam4;
    private Date dateParam5;
    private BigDecimal decimalParam1;
    private BigDecimal decimalParam2;
    private BigDecimal decimalParam3;
    private BigDecimal decimalParam4;
    private BigDecimal decimalParam5;
    private String extraParam;
    private String originBatch;
    private String originRecord;

    private String line;

    /**
     * @return Event date
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp Event date
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return Access user id
     */
    public String getAccess_id() {
        return access_id;
    }

    /**
     * @param access_id Access user id
     */
    public void setAccess_id(String access_id) {
        this.access_id = access_id;
    }

    /**
     * @return Quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * @param quantity Quantity
     */
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    /**
     * @return Parameter 1
     */
    public String getParam1() {
        return param1;
    }

    /**
     * @param param1 Parameter 1
     */
    public void setParam1(String param1) {
        this.param1 = param1;
    }

    /**
     * @return Parameter 2
     */
    public String getParam2() {
        return param2;
    }

    /**
     * @param param2 Parameter 2
     */
    public void setParam2(String param2) {
        this.param2 = param2;
    }

    /**
     * @return Parameter 3
     */
    public String getParam3() {
        return param3;
    }

    /**
     * @param param3 Parameter 3
     */
    public void setParam3(String param3) {
        this.param3 = param3;
    }

    /**
     * @return Parameter 4
     */
    public String getParam4() {
        return param4;
    }

    /**
     * @param param4 Parameter 4
     */
    public void setParam4(String param4) {
        this.param4 = param4;
    }

    /**
     * @return Parameter 5
     */
    public String getParam5() {
        return param5;
    }

    /**
     * @param param5 Parameter 5
     */
    public void setParam5(String param5) {
        this.param5 = param5;
    }

    /**
     * @return Parameter 6
     */
    public String getParam6() {
        return param6;
    }

    /**
     * @param param6 Parameter 6
     */
    public void setParam6(String param6) {
        this.param6 = param6;
    }

    /**
     * @return Parameter 7
     */
    public String getParam7() {
        return param7;
    }

    /**
     * @param param7 Parameter 7
     */
    public void setParam7(String param7) {
        this.param7 = param7;
    }

    /**
     * @return Parameter 8
     */
    public String getParam8() {
        return param8;
    }

    /**
     * @param param8 Parameter 8
     */
    public void setParam8(String param8) {
        this.param8 = param8;
    }

    /**
     * @return Parameter 9
     */
    public String getParam9() {
        return param9;
    }

    /**
     * @param param9 Parameter 9
     */
    public void setParam9(String param9) {
        this.param9 = param9;
    }

    /**
     * @return Date parameter 1
     */
    public Date getDateParam1() {
        return dateParam1;
    }

    /**
     * @param dateParam1 Date parameter 1
     */
    public void setDateParam1(Date dateParam1) {
        this.dateParam1 = dateParam1;
    }

    /**
     * @return Date parameter 2
     */
    public Date getDateParam2() {
        return dateParam2;
    }

    /**
     * @param dateParam2 Date parameter 2
     */
    public void setDateParam2(Date dateParam2) {
        this.dateParam2 = dateParam2;
    }

    /**
     * @return Date parameter 3
     */
    public Date getDateParam3() {
        return dateParam3;
    }

    /**
     * @param dateParam3 Date parameter 3
     */
    public void setDateParam3(Date dateParam3) {
        this.dateParam3 = dateParam3;
    }

    /**
     * @return Date parameter 4
     */
    public Date getDateParam4() {
        return dateParam4;
    }

    /**
     * @param dateParam4 Date parameter 4
     */
    public void setDateParam4(Date dateParam4) {
        this.dateParam4 = dateParam4;
    }

    /**
     * @return Date parameter 5
     */
    public Date getDateParam5() {
        return dateParam5;
    }

    /**
     * @param dateParam5 Date parameter 5
     */
    public void setDateParam5(Date dateParam5) {
        this.dateParam5 = dateParam5;
    }

    /**
     * @return Decimal parameter 1
     */
    public BigDecimal getDecimalParam1() {
        return decimalParam1;
    }

    /**
     * @param decimalParam1 Decimal parameter 1
     */
    public void setDecimalParam1(BigDecimal decimalParam1) {
        this.decimalParam1 = decimalParam1;
    }

    /**
     * @return Decimal parameter 2
     */
    public BigDecimal getDecimalParam2() {
        return decimalParam2;
    }

    /**
     * @param decimalParam2 Decimal parameter 2
     */
    public void setDecimalParam2(BigDecimal decimalParam2) {
        this.decimalParam2 = decimalParam2;
    }

    /**
     * @return Decimal parameter 3
     */
    public BigDecimal getDecimalParam3() {
        return decimalParam3;
    }

    /**
     * @param decimalParam3 Decimal parameter 3
     */
    public void setDecimalParam3(BigDecimal decimalParam3) {
        this.decimalParam3 = decimalParam3;
    }

    /**
     * @return Decimal parameter 4
     */
    public BigDecimal getDecimalParam4() {
        return decimalParam4;
    }

    /**
     * @param decimalParam4 Decimal parameter 4
     */
    public void setDecimalParam4(BigDecimal decimalParam4) {
        this.decimalParam4 = decimalParam4;
    }

    /**
     * @return Decimal parameter 5
     */
    public BigDecimal getDecimalParam5() {
        return decimalParam5;
    }

    /**
     * @param decimalParam5 Decimal parameter 5
     */
    public void setDecimalParam5(BigDecimal decimalParam5) {
        this.decimalParam5 = decimalParam5;
    }

    /**
     * @return the extraParam
     */
    public String getExtraParam() {
        return extraParam;
    }

    /**
     * @param extraParam the extraParam
     */
    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam;
    }

    /**
     * @return A source of CDR: file or api
     */
    public String getOriginBatch() {
        return originBatch;
    }

    /**
     * @param originBatch A source of CDR: file or api
     */
    public void setOriginBatch(String originBatch) {
        this.originBatch = originBatch;
    }

    /**
     * @return CDR line digest for duplicate comparison
     */
    public String getOriginRecord() {
        return originRecord;
    }

    /**
     * @param originRecord CDR line digest for duplicate comparison
     */
    public void setOriginRecord(String originRecord) {
        this.originRecord = originRecord;
    }

    /**
     * @return An original parsed line
     */
    public String getLine() {
        return line;
    }

    /**
     * 
     * @param line An original parsed line
     */
    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public String toString() {
        if (timestamp == null) {
            return "CDR [line=" + line + "]";
        } else {
            return "CDR [timestamp=" + timestamp + ", access_id=" + access_id + ", quantity=" + quantity + ", param1=" + param1 + ", param2=" + param2 + ", param3=" + param3
                    + ", param4=" + param4 + ", param5=" + param5 + ", param6=" + param6 + ", param7=" + param7 + ", param8=" + param8 + ", param9=" + param9 + ", dateParam1="
                    + dateParam1 + ", dateParam2=" + dateParam2 + ", dateParam3=" + dateParam3 + ", dateParam4=" + dateParam4 + ", dateParam5=" + dateParam5 + ", decimalParam1="
                    + decimalParam1 + ", decimalParam2=" + decimalParam2 + ", decimalParam3=" + decimalParam3 + ", decimalParam4=" + decimalParam4 + ", decimalParam5="
                    + decimalParam5 + ", extraParam=" + extraParam + ", originBatch=" + originBatch + ", originRecord=" + originRecord + "]";
        }
    }

    /**
     * Convert to a CSV-like line with ";" as field separator
     * 
     * @return CSV-line line string
     */
    public String toCsv() {
        return getCsvValue(timestamp) + ";" + getCsvValue(quantity) + ";" + getCsvValue(access_id) + ";" + getCsvValue(param1) + ";" + getCsvValue(param2) + ";"
                + getCsvValue(param3) + ";" + getCsvValue(param4) + ";" + getCsvValue(param5) + ";" + getCsvValue(param6) + ";" + getCsvValue(param7) + ";" + getCsvValue(param8)
                + ";" + getCsvValue(param9) + ";" + getCsvValue(dateParam1) + ";" + getCsvValue(dateParam2) + ";" + getCsvValue(dateParam3) + ";" + getCsvValue(dateParam4) + ";"
                + getCsvValue(dateParam5) + ";" + getCsvValue(decimalParam1) + ";" + getCsvValue(decimalParam2) + ";" + getCsvValue(decimalParam3) + ";"
                + getCsvValue(decimalParam4) + ";" + getCsvValue(decimalParam5) + ";" + getCsvValue(extraParam);
    }

    private String getCsvValue(Object o) {
        String result = "";
        if (o == null) {
            result = "";
        } else if (o instanceof Date) {
            result = sdf.format((Date) o);
        } else {
            result = o.toString();
        }
        return result;
    }
}