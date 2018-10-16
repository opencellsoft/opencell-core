package org.meveo.admin.job;

import java.util.Date;

public class DateRange {
    
    private Date from;
    private Date to;
    
    public DateRange () {
    }
    
    public DateRange (Date from, Date to) {
        this.from = from;
        this.to = to;
    }
    
    /**
     * @return the from
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @return the to
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Date from) {
        this.from = from;
    }

    /**
     * @param to the to to set
     */
    public void setTo(Date to) {
        this.to = to;
    }
}
