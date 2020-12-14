package org.meveo.model.article;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BusinessEntity;
import org.meveo.model.scripts.ScriptInstance;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "billing_article_mapping")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_article_mapping_seq"), })
public class ArticleMapping extends BusinessEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mapping_script_id")
    private ScriptInstance mappingScript;

    public ArticleMapping(){}

    public ArticleMapping(Long id) {
        this.id = id;
    }

    public ScriptInstance getMappingScript() {
        return mappingScript;
    }

    public void setMappingScript(ScriptInstance mappingScript) {
        this.mappingScript = mappingScript;
    }
}
