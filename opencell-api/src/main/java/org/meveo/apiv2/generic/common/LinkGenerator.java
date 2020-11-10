package org.meveo.apiv2.generic.common;

import org.assertj.core.util.Arrays;
import org.meveo.commons.utils.StringUtils;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

        public LinkGenerator.SelfLinkGenerator withGetAction() {
            this.actions.add("GET");
            return this;
        }

        public LinkGenerator.SelfLinkGenerator withPostAction() {
            this.actions.add("POST");
            return this;
        }

        public LinkGenerator.SelfLinkGenerator withPutAction() {
            this.actions.add("PUT");
            return this;
        }

        public LinkGenerator.SelfLinkGenerator withPatchAction() {
            this.actions.add("PATCH");
            return this;
        }

        public LinkGenerator.SelfLinkGenerator withDeleteAction() {
            this.actions.add("DELETE");
            return this;
        }

        public LinkGenerator.SelfLinkGenerator withId(Long resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Link build() {
            UriBuilder uriBuilder = getUriBuilderFromResource(this.resourceClass, this.resourceId);
            return getLink(uriBuilder);
        }

        public Link build(String... params) {
            UriBuilder uriBuilder = getUriBuilderFromResource(this.resourceClass, params);
            return getLink(uriBuilder);
        }

        private Link getLink(UriBuilder uriBuilder) {
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

        public LinkGenerator.PaginationLinkGenerator offset(Long offset) {
            params.put("offset", offset);
            return this;
        }

        public LinkGenerator.PaginationLinkGenerator limit(Long limit) {
            params.put("limit", limit);
            return this;
        }

        public LinkGenerator.PaginationLinkGenerator total(Long total) {
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
        return getUriBuilderFromResource(resourceClass, String.valueOf(resourceId));
    }
    public static UriBuilder getUriBuilderFromResource(Class resourceClass, String... params) {
        UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);
        if (Arrays.isNullOrEmpty(params)) {
            throw new IllegalArgumentException("Path params should not be null");
        }
        return Stream.of(params).filter(StringUtils::isNotBlank).map(uriBuilder::path).findFirst().orElseThrow(() -> new IllegalArgumentException("Path params should not be "
                + "empty"));
    }
}