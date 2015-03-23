/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.infinispan.api.BasicCache;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.cache.NotificationCacheContainerProvider;
import org.meveo.cache.RatingCacheContainerProvider;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@Named
@ViewScoped
public class CacheBean implements Serializable {

    private static final long serialVersionUID = -8072659867697109888L;

    @Inject
    private WalletCacheContainerProvider walletCacheContainerProvider;

    @Inject
    private CdrEdrProcessingCacheContainerProvider cdrEdrProcessingCacheContainerProvider;

    @Inject
    private NotificationCacheContainerProvider notificationCacheContainerProvider;

    @Inject
    private RatingCacheContainerProvider ratingCacheContainerProvider;

    /** Logger. */
    @Inject
    protected org.slf4j.Logger log;

    /**
     * Request parameter. Should form be displayed in create/edit or view mode
     */
    @Inject
    @RequestParam()
    private Instance<String> cacheName;

    /**
     * Selected cache to display details of
     */
    @SuppressWarnings("rawtypes")
    private BasicCache cache;

    /**
     * Datamodel for lazy dataloading in cached contents.
     */
    @SuppressWarnings("rawtypes")
    protected LazyDataModel cacheContents;

    @SuppressWarnings("rawtypes")
    public void preRenderView() {

        if (cacheName.get() != null) {
            log.error("AKK cache is " + cacheName.get());
            Map<String, BasicCache> caches = walletCacheContainerProvider.getCaches();
            caches.putAll(cdrEdrProcessingCacheContainerProvider.getCaches());
            caches.putAll(notificationCacheContainerProvider.getCaches());
            caches.putAll(ratingCacheContainerProvider.getCaches());

            cache = caches.get(cacheName.get());
            log.error("AKK cache is " + cache);
        }
    }

    /**
     * Get a summary of cached information
     * 
     * @return A list of a map containing cache information with the following cache keys: name, count
     */
    @SuppressWarnings("rawtypes")
    public List<Map<String, String>> getSummaryOfCaches() {
        List<Map<String, String>> cacheSummary = new ArrayList<Map<String, String>>();

        Map<String, BasicCache> caches = walletCacheContainerProvider.getCaches();
        caches.putAll(cdrEdrProcessingCacheContainerProvider.getCaches());
        caches.putAll(notificationCacheContainerProvider.getCaches());
        caches.putAll(ratingCacheContainerProvider.getCaches());
        caches = new TreeMap<String, BasicCache>(caches);

        for (Entry<String, BasicCache> cache : caches.entrySet()) {
            Map<String, String> cacheInfo = new HashMap<String, String>();
            cacheInfo.put("name", cache.getKey());
            cacheInfo.put("count", Integer.toString(cache.getValue().size()));
            cacheSummary.add(cacheInfo);
        }
        return cacheSummary;
    }

    /**
     * Refresh cache
     * 
     * @param cacheName Cache name
     */
    public void refresh(String cacheName) {
        walletCacheContainerProvider.refreshCache(cacheName);
        cdrEdrProcessingCacheContainerProvider.refreshCache(cacheName);
        notificationCacheContainerProvider.refreshCache(cacheName);
        ratingCacheContainerProvider.refreshCache(cacheName);
    }

    @SuppressWarnings("rawtypes")
    public LazyDataModel getCacheContents() {
        return getCacheContents(null, false);
    }

    @SuppressWarnings("rawtypes")
    public LazyDataModel getCacheContents(Map<String, Object> inputFilters, boolean forceReload) {
        if (cacheContents == null || forceReload) {

            // final Map<String, Object> filters = inputFilters;

            cacheContents = new LazyDataModel() {

                private static final long serialVersionUID = -5796910936316457321L;

                private Integer rowCount;

                @SuppressWarnings("unchecked")
                @Override
                public List load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {
                    setRowCount(cache.size());

                    if (getRowCount() > 0) {
                        int toNr = ((first + 1) * pageSize) - 1;
                        return new LinkedList(cache.entrySet()).subList(first * pageSize, rowCount - 1 <= toNr ? rowCount : toNr);

                    } else {
                        return new ArrayList();
                    }
                }

                @Override
                public void setRowCount(int rowCount) {
                    this.rowCount = rowCount;
                }

                @Override
                public int getRowCount() {
                    return rowCount;
                }

            };
        }
        return cacheContents;
    }

    public String getCacheName() {
        return cache.getName();
    }

    /**
     * Extract values of cached object to show in a list. In case of list of items, show only the first 10 items
     * 
     * @param cachedObject Cached object to display
     * @return A string representation
     */
    public String getExtractOfValuesForList(Object cachedObject) {
        StringBuilder builder = new StringBuilder();

        if (cachedObject instanceof List) {
            List listObject = (List) cachedObject;
            for (int i = 0; i < 10 && i < listObject.size(); i++) {
                Object item = listObject.get(i);
                if (item instanceof BusinessEntity) {
                    builder.append(builder.length() == 0 ? "" : ", ").append(((BusinessEntity) item).getCode());

                } else if (item instanceof IEntity) {
                    builder.append(builder.length() == 0 ? "" : ", ").append(((IEntity) item).getId());

                } else {
                    builder.append(builder.length() == 0 ? "" : ", ").append(item);
                }
            }

            if (listObject.size() > 10) {
                builder.append(", ...");
            }
        } else if (cachedObject instanceof BusinessEntity) {
            builder.append(builder.length() == 0 ? "" : ", ").append(((BusinessEntity) cachedObject).getCode());

        } else if (cachedObject instanceof IEntity) {
            builder.append(builder.length() == 0 ? "" : ", ").append(((IEntity) cachedObject).getId());

        } else {
            builder.append(builder.length() == 0 ? "" : ", ").append(cachedObject);
        }

        return builder.toString();
    }
}