package org.meveo.service.billing.impl.article;

import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class AttributeMappingLineMatch {

    private List<ArticleMappingLine> fullMatches = new ArrayList<>();

    private List<PartialMatchMappingLine> partialMatchMappingLines = new ArrayList<>();

    public void addFullMatch(ArticleMappingLine aml) {
        fullMatches.add(aml);
    }

    public void addPartialMatch(ArticleMappingLine articleMappingLine, int numberOfMatchedAttribute) {
        partialMatchMappingLines.add(new PartialMatchMappingLine(numberOfMatchedAttribute, articleMappingLine));
    }

    public Set<AccountingArticle> getFullMatchsArticle() {
        return fullMatches.stream()
                .map(ArticleMappingLine::getAccountingArticle)
                .collect(toSet());
    }

    public ArticleMappingLine getBestMatch() {
        List<PartialMatchMappingLine> matches = partialMatchMappingLines.stream()
                .filter(partialMatchMappingLine -> partialMatchMappingLine.numberOfMatchedAttribute > 0)
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
