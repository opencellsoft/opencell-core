package org.meveo.service.index;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 * Tracks pending request to be submitted to Elastic search
 * 
 * @author Andrius Karpavicius
 */
@RequestScoped
public class ElasticClientQueuedChanges {

    private Map<String, ElasticSearchChangeset> queuedChanges = new HashMap<>();

    public void addChange(ElasticSearchChangeset change) {
        if (queuedChanges.containsKey(change.getFullId())) {
            queuedChanges.get(change.getFullId()).mergeUpdates(change.getSource());
        } else {
            queuedChanges.put(change.getFullId(), change);
        }
    }

    public Map<String, ElasticSearchChangeset> getQueuedChanges() {
        return queuedChanges;
    }

    public boolean isNoChange() {
        return queuedChanges.isEmpty();
    }

    public void clear() {
        queuedChanges.clear();
    }
}
