package org.meveo.model.document;


import org.hibernate.annotations.Type;
import org.meveo.model.*;
import org.meveo.model.admin.FileType;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Document entity handles file storage
 *
 * @author Abdelkader Bouazza
 * @lastModifiedVersion 10.0.0
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "Document")
@Table(name = "document", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
public class Document extends BusinessCFEntity {
    /**
     * Translated descriptions in JSON format with language code as a key and
     * translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;
    /**
     * document type - document file type
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_type_id")
    @NotNull
    private FileType fileType;

    /**
     * creation date - document creation date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    @NotNull
    private Date creationDate;

    /**
     * file name - document file name
     */
    @Column(name = "file_name", nullable = false)
    @NotNull
    private String fileName;

    /**
     * List of tags linked to the document.
     */
    @ElementCollection//meveo_filter_selector_ignore_fields
    @CollectionTable(name = "document_tags", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "tags")
    private List<String> tags = new ArrayList<>();

    /**
     * account entity - document related account entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_account_entity_id")
    private AccountEntity linkedAccountEntity;

    /**
     * account entity - document related account entity.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false ,targetEntity = DocumentCategory.class)
    @JoinColumn(name = "document_category_id")
    private DocumentCategory category;

    /**
     * document current status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "document_status", length = 25, nullable = false)
    @NotNull
    private DocumentStatus documentStatus = DocumentStatus.ACTIVE;

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public org.meveo.model.AccountEntity getLinkedAccountEntity() {
        return linkedAccountEntity;
    }

    public void setLinkedAccountEntity(org.meveo.model.AccountEntity linkedAccountEntity) {
        this.linkedAccountEntity = linkedAccountEntity;
    }

    public DocumentCategory getCategory() {
        return category;
    }

    public void setCategory(DocumentCategory category) {
        this.category = category;
    }

    public DocumentStatus getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(DocumentStatus documentStatus) {
        this.documentStatus = documentStatus;
    }
}
