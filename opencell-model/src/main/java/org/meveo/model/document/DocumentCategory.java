package org.meveo.model.document;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

/**
 *
 * @author Abdelkader Bouazza
 * @lastModifiedVersion 10.0.0
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "DocumentCategory")
@Table(name = "document_category", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
public class DocumentCategory extends BusinessCFEntity {
    /**
     * Translated descriptions in JSON format with language code as a key and
     * translated description as a value
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;
    /**
     * a text EL (has access to variables "document" and "entity" containing the document category)
     * returning a subdirectory for the document category (relative to "document.rootDir")
     */
    @Column(name = "relative_path", length = 2000, nullable = false)
    @Size(max = 2000)
    private String relativePath;

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
}
