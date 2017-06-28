package org.meveo.model.jobs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "ftp_imported_file")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "ftp_imported_file_seq"), })
public class FtpImportedFile extends BusinessEntity {
    private static final long serialVersionUID = 430457580612075457L;

    @Column(name = "uri", length = 2000, nullable = false)
    @Size(max = 2000)
    @NotNull
    private String uri;

    @Column(name = "size")
    private Long size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "import_date")
    private Date importDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification")
    private Date lastModification;


    public FtpImportedFile(){
    }



    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the importDate
     */
    public Date getImportDate() {
        return importDate;
    }

    /**
     * @param importDate the importDate to set
     */
    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    /**
     * @return the lastModification
     */
    public Date getLastModification() {
        return lastModification;
    }

    /**
     * @param lastModification the lastModification to set
     */
    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }



    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FtpImportedFile [uri=" + uri + ", size=" + size + ", importDate=" + importDate + ", lastModification=" + lastModification + ", code=" + code + ", description=" + description + ", appendGeneratedCode=" + appendGeneratedCode + ", id=" + id + "]";
    }


    }