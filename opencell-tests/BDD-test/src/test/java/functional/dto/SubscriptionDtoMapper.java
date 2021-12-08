package functional.dto;

import functional.dto.annotation.FieldMapper;

public class SubscriptionDtoMapper {

    @FieldMapper("code")
    private String code;

    @FieldMapper("description")
    private String description;

    @FieldMapper("userAccount")
    private String userAccount;

    @FieldMapper("offerTemplate")
    private String offerTemplate;

}
