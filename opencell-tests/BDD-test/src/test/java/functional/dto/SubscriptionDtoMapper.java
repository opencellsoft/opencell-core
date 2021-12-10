package functional.dto;

import functional.dto.annotation.EntityDtoMapper;
import functional.dto.annotation.FieldMapper;

@EntityDtoMapper("SubscriptionDto")
public class SubscriptionDtoMapper {

    @FieldMapper("/code")
    private String code;

    @FieldMapper("/description")
    private String description;

    @FieldMapper("/userAccount")
    private String userAccount;

    @FieldMapper("/offerTemplate")
    private String offerTemplate;

}
