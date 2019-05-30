package org.meveo.apiv2.common;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LinkGenerator {


    public static class SelfLinkGenerator {

        private final List<String> actions;
        private Long resourceId;
        private String rel;
        private Class resourceClass;

        public SelfLinkGenerator(Class resourceClass) {
            this.rel = "self";
            this.resourceClass=resourceClass;
            actions = new ArrayList<>();
        }

        public SelfLinkGenerator withGetAction() {
            this.actions.add("GET");
            return this;
        }

        public SelfLinkGenerator withPostAction() {
            this.actions.add("POST");
            return this;
        }

        public SelfLinkGenerator withPutAction() {
            this.actions.add("PUT");
            return this;
        }

        public SelfLinkGenerator withPatchAction() {
            this.actions.add("PATCH");
            return this;
        }

        public SelfLinkGenerator withDeleteAction() {
            this.actions.add("DELETE");
            return this;
        }

        public SelfLinkGenerator withId(Long resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Link build() {
            UriBuilder uriBuilder = getUriBuilderFromResource(this.resourceClass, this.resourceId);
            Link.Builder builder = Link.fromUriBuilder(uriBuilder).rel(rel);
            StringBuilder stringBuilder = new StringBuilder();
            actions.forEach(value -> stringBuilder.append(value).append(" "));
            builder.param("actions",stringBuilder.toString().trim());
            return builder.build();
        }
    }

    public static class PaginationLinkGenerator {
        private final Map<String, Long> params;
        private Class resourceClass;

        public PaginationLinkGenerator(Class resourceClass) {
            this.params = new HashMap<>();
            this.resourceClass=resourceClass;
        }

        public PaginationLinkGenerator offset(Long offset) {
            params.put("offset", offset);
            return this;
        }

        public PaginationLinkGenerator limit(Long limit) {
            params.put("limit", limit);
            return this;
        }

        public PaginationLinkGenerator total(Long total) {
            params.put("total", total);
            return this;
        }

        public List<Link> build() {
            List<Link> links = new ArrayList();
            Link previousLinks = getPreviousLinks();
            Link nextLinks = getNextLinks();
            if(previousLinks != null) {
                links.add(previousLinks);
            }
            if(nextLinks != null) {
                links.add(nextLinks);
            }
            return links;
        }

        private Link getNextLinks() {
            Long offset = this.params.getOrDefault("offset", 0L);
            Long limit = this.params.getOrDefault("limit", 1L);
            Long total = this.params.getOrDefault("total", 0L);
            if(offset + limit < total){
                Map<String,Long> newQueryParams = new HashMap<>();
                long nextOffset = offset + limit;
                newQueryParams.put("offset", nextOffset <= total ? nextOffset : offset+limit - total);
                newQueryParams.put("limit", limit);
                return buildFrom("next", newQueryParams);
            }
            return null;
        }

        private Link getPreviousLinks() {
            Long offset = this.params.getOrDefault("offset", 0L);
            Long limit = this.params.getOrDefault("limit", 1L);
            Long total = this.params.getOrDefault("total", 0L);
            if(offset > 0 && total > 0){
                Map<String,Long> newQueryParams = new HashMap<>();
                long previousOffset = offset - limit;
                newQueryParams.put("offset", previousOffset >= 0 ? previousOffset : 0);
                newQueryParams.put("limit", limit);
                return buildFrom("previous", newQueryParams);
            }
            return null;
        }

        private Link buildFrom(String rel, Map<String,Long> params) {
            UriBuilder uriBuilder = UriBuilder.fromResource(this.resourceClass);
            params.forEach(uriBuilder::queryParam);
            Link.Builder builder = Link.fromUriBuilder(uriBuilder).rel(rel);
            builder.param("action", "GET");
            return builder.build();
        }
    }
    public static UriBuilder getUriBuilderFromResource(Class resourceClass, Long resourceId) {
        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);
        if(resourceId != null){
            uriBuilder.path(String.valueOf(resourceId));
        }
        return uriBuilder;
    }
}
