package org.meveo.service.billing.impl.article;

import org.meveo.model.article.ArticleMappingLine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeMappingLineMatch {

    private List<ArticleMappingLine> fullMatches = new ArrayList<>();

    private List<PartialMatchMappingLine> partialMatchMappingLines = new ArrayList<>();

    public void addFullMatch(ArticleMappingLine aml) {
        fullMatches.add(aml);
    }

    public void addPartialMatch(ArticleMappingLine articleMappingLine, int numberOfMatchedAttribute) {
        partialMatchMappingLines.add(new PartialMatchMappingLine(numberOfMatchedAttribute, articleMappingLine));
    }

    public List<ArticleMappingLine> getFullMatchs() {
        return fullMatches;
    }

    public ArticleMappingLine getBestMatch() {
        List<PartialMatchMappingLine> matches = partialMatchMappingLines.stream()
                .sorted(Comparator.comparing(a -> a.numberOfMatchedAttribute))
                .collect(Collectors.toList());
        return matches.isEmpty() ? null : matches.get(matches.size() - 1).articleMappingLine;
    }

    public class PartialMatchMappingLine {
        private Integer numberOfMatchedAttribute;
        private ArticleMappingLine articleMappingLine;


        public PartialMatchMappingLine(Integer numberOfMatchedAttribute, ArticleMappingLine articleMappingLine) {
            this.numberOfMatchedAttribute = numberOfMatchedAttribute;
            this.articleMappingLine = articleMappingLine;
        }
    }
}
