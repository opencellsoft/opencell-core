package org.meveo.service.communication.impl;

import org.meveo.api.dto.communication.TranslatedHtmlContentDto;
import org.meveo.api.dto.communication.TranslatedSubjectDto;
import org.meveo.api.dto.communication.TranslatedTextContentDto;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.service.billing.impl.TradingLanguageService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationsUtils {

    public static final String IS_NOT_SUPPORTED_BY_THE_PROVIDER = " is not supported by the provider.";
    public static final String LANGUAGE = "Language ";

    @Inject
    private TradingLanguageService tradingLanguageService;

    public Map<String, String> convertSubjectsToMap(List<TranslatedSubjectDto> translatedSubjects) {

        List<String> supportedLanguages = tradingLanguageService.listLanguageCodes();

        if (translatedSubjects == null || translatedSubjects.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> values = new HashMap<>();

        for (TranslatedSubjectDto translatedSubjectDto : translatedSubjects) {
            if (!supportedLanguages.contains(translatedSubjectDto.getLanguageCode())) {
                throw new InvalidParameterException(LANGUAGE + translatedSubjectDto.getLanguageCode() + IS_NOT_SUPPORTED_BY_THE_PROVIDER);
            }
            if (StringUtils.isBlank(translatedSubjectDto.getSubject())) {
                values.remove(translatedSubjectDto.getLanguageCode());
            } else {
                values.put(translatedSubjectDto.getLanguageCode(), translatedSubjectDto.getSubject());
            }
        }

        return values;
    }

    public Map<String, String> convertHtmlTranslationsToMap(List<TranslatedHtmlContentDto> htmlTranslations) {

        if (htmlTranslations == null || htmlTranslations.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> supportedLanguages = tradingLanguageService.listLanguageCodes();

        Map<String, String> values = new HashMap<>();

        for (TranslatedHtmlContentDto translatedHtmlContentDto : htmlTranslations) {
            if (!supportedLanguages.contains(translatedHtmlContentDto.getLanguageCode())) {
                throw new InvalidParameterException(LANGUAGE + translatedHtmlContentDto.getLanguageCode() + IS_NOT_SUPPORTED_BY_THE_PROVIDER);
            }
            if (StringUtils.isBlank(translatedHtmlContentDto.getHtmlContent())) {
                values.remove(translatedHtmlContentDto.getLanguageCode());
            } else {
                values.put(translatedHtmlContentDto.getLanguageCode(), translatedHtmlContentDto.getHtmlContent());
            }
        }

        return values;
    }

    public Map<String, String> convertTextTranslationsToMap(List<TranslatedTextContentDto> textTranslations) {

        if (textTranslations == null || textTranslations.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> values = new HashMap<>();

        List<String> supportedLanguages = tradingLanguageService.listLanguageCodes();

        for (TranslatedTextContentDto translatedTextContentDto : textTranslations) {
            if (!supportedLanguages.contains(translatedTextContentDto.getLanguageCode())) {
                throw new InvalidParameterException(LANGUAGE + translatedTextContentDto.getLanguageCode() + IS_NOT_SUPPORTED_BY_THE_PROVIDER);
            }
            if (StringUtils.isBlank(translatedTextContentDto.getTextContent())) {
                values.remove(translatedTextContentDto.getLanguageCode());
            } else {
                values.put(translatedTextContentDto.getLanguageCode(), translatedTextContentDto.getTextContent());
            }
        }

        return values;
    }


    public static List<TranslatedTextContentDto> convertToTranslatedTextDto(Map<String, String> textTranslationsMap) {

        List<TranslatedTextContentDto> textTranslations = new ArrayList<>();
        for (Map.Entry<String, String> languageTranslation : textTranslationsMap.entrySet()) {
            TranslatedTextContentDto translatedTextContentDto = new TranslatedTextContentDto(languageTranslation.getKey(), languageTranslation.getValue());
            textTranslations.add(translatedTextContentDto);
        }

        return textTranslations;
    }

    public static List<TranslatedSubjectDto> convertToTranslatedSubjectDto(Map<String, String> subjectTranslationsMap) {

        List<TranslatedSubjectDto> subjectTranslations = new ArrayList<>();

        for (Map.Entry<String, String> subjectTranslation : subjectTranslationsMap.entrySet()) {
            TranslatedSubjectDto translatedSubjectDto = new TranslatedSubjectDto(subjectTranslation.getKey(), subjectTranslation.getValue());
            subjectTranslations.add(translatedSubjectDto);
        }

        return subjectTranslations;
    }

    public static List<TranslatedHtmlContentDto> convertToTranslatedHtmlDto(Map<String, String> htmlTranslationsMap) {

        List<TranslatedHtmlContentDto> htmlTranslations = new ArrayList<>();

        for (Map.Entry<String, String> htmlTranslation : htmlTranslationsMap.entrySet()) {
            TranslatedHtmlContentDto translatedHtmlContentDto = new TranslatedHtmlContentDto(htmlTranslation.getKey(), htmlTranslation.getValue());
            htmlTranslations.add(translatedHtmlContentDto);
        }

        return htmlTranslations;
    }
}
