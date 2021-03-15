package org.meveo.api.dto.response;

import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.LanguagesDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class TradingLanguagesResponseDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "TradingLanguagesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TradingLanguagesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The languages DTO. */
    private LanguagesDto languagesDto = new LanguagesDto();

    /**
     * Constructor of TradingLanguagesResponseDto.
     */
    public TradingLanguagesResponseDto() {
    }

    public TradingLanguagesResponseDto(GenericSearchResponse<LanguageDto> searchResponse) {
        super(searchResponse.getPaging());
        this.languagesDto.setLanguage(searchResponse.getSearchResults());
    }

    /**
     * Get the languages DTO.
     *
     * @return the languages DTO
     */
    public LanguagesDto getTradingLanguages() {
        return languagesDto;
    }

    /**
     * Sets the languages DTO.
     *
     * @param languagesDto the languages DTO
     */
    public void setTradingLanguages(LanguagesDto languagesDto) {
        this.languagesDto = languagesDto;
    }

    @Override
    public String toString() {
        return "ListLanguagesResponseDto [languages=" + languagesDto + ", toString()=" + super.toString() + "]";
    }
}
