package org.meveo.admin.action.index;

import java.io.Serializable;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.service.index.ElasticClient;

@Named
@ViewScoped
public class IndexingBean implements Serializable {

    private static final long serialVersionUID = 7051728474316387375L;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    protected Messages messages;

    public void clearAndReindex() {

        if (!elasticClient.isEnabled()) {
            messages.error(new BundleKey("messages", "indexing.notEnabled"));
            return;
        }

        elasticClient.clearAndReindex();
        messages.info(new BundleKey("messages", "indexing.started"));
    }
}