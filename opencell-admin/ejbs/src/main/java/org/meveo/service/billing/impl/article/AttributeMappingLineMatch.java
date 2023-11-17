package org.meveo.service.billing.impl.article;

import org.apache.commons.collections4.MapUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMappingLine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
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

    public int getFullMatchesCount() {
        return fullMatches.size();
    }
    
    public Set<AccountingArticle> getFullMatchsArticle() {
        return fullMatches.stream()
                .map(ArticleMappingLine::getAccountingArticle)
                .collect(toSet());
    }

    public ArticleMappingLine getBestMatch() {
        checkDuplicatePartiallyMatchedMapping(partialMatchMappingLines);

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

        public Integer getNumberOfMatchedAttribute() {
            return numberOfMatchedAttribute;
        }
    }

    /**
     * If we have same number of matchedAttribut in partialMatchMappingLines, than means that we have two matched Article matched, and this is not permited
     *
     * @param lines partially matched mapping lines
     */
    private void checkDuplicatePartiallyMatchedMapping(List<PartialMatchMappingLine> lines) {
        Map<Integer, List<PartialMatchMappingLine>> groupByNBMatchedAttr = lines.stream()
                .collect(groupingBy(PartialMatchMappingLine::getNumberOfMatchedAttribute));

        if (MapUtils.isNotEmpty(groupByNBMatchedAttr)) {
            groupByNBMatchedAttr.forEach((nbMatched, mappings) -> {
                if (mappings.size() > 1) {
                    throw new BusinessException("More than one article found");
                }
            });
        }
    }
}
