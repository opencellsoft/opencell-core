package org.meveo.model.document;

import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import java.util.Map;

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
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
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
