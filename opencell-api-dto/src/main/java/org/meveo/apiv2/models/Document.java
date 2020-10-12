package org.meveo.apiv2.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.immutables.value.Value;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.FileType;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.document.DocumentCategory;
import org.meveo.model.document.DocumentStatus;
import org.meveo.model.payments.CustomerAccount;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = ImmutableDocument.class)
public interface  Document {

    static Object from(org.meveo.model.document.Document documentEntity, String encodedFileContent) {
        return ImmutableDocument.builder()
                .id(documentEntity.getId())
                .code(documentEntity.getCode())
                .description(documentEntity.getDescription())
                .descriptionI18n(documentEntity.getDescriptionI18n())
                .fileName(documentEntity.getFileName())
                .creationDate(documentEntity.getCreationDate())
                .category(documentEntity.getCategory())
                .fileType(documentEntity.getFileType())
                .tags(documentEntity.getTags())
                .status(documentEntity.getDocumentStatus())
                .linkedAccountEntity(documentEntity.getLinkedAccountEntity())
                .encodedFile(encodedFileContent)
                .build();
    }

    @Nullable
    Long getId();

    @Nullable
    String getCode();

    @Nullable
    String getDescription();

    @Nullable
    Map<String, String> getDescriptionI18n();

    @Nullable
    FileType getFileType();

    @Nullable
    String getFileName();

    @Nullable
    Date getCreationDate();

    @Nullable
    List<String> getTags();

    @Nullable
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Customer.class, name = "Customer"),
            @JsonSubTypes.Type(value = BillingAccount.class, name = "BillingAccount"),
            @JsonSubTypes.Type(value = Contact.class, name = "Contact"),
            @JsonSubTypes.Type(value = Seller.class, name = "Seller"),
            @JsonSubTypes.Type(value = UserAccount.class, name = "UserAccount"),
            @JsonSubTypes.Type(value = CustomerAccount.class, name = "CustomerAccount")
    })
    @JsonSerialize(using = AccountEntitySerializer.class)
    AccountEntity getLinkedAccountEntity();

    @Nullable
    DocumentCategory getCategory();

    @Nullable
    DocumentStatus getStatus();

    @Nullable
    String getEncodedFile();

    default org.meveo.model.document.Document toEntity(){
        final org.meveo.model.document.Document documentEntity = new org.meveo.model.document.Document();
        documentEntity.setId(getId());
        documentEntity.setCode(getCode());
        documentEntity.setDescription(getDescription());
        documentEntity.setDescriptionI18n(getDescriptionI18n());
        documentEntity.setFileType(getFileType());
        documentEntity.setFileName(getFileName());
        documentEntity.setCreationDate(getCreationDate());
        documentEntity.setTags(getTags());
        documentEntity.setLinkedAccountEntity(getLinkedAccountEntity());
        documentEntity.setCategory(getCategory());
        documentEntity.setDocumentStatus(getStatus());
        return documentEntity;
    }

    class AccountEntitySerializer extends JsonSerializer<AccountEntity> {
        @Override
        public void serialize(AccountEntity value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException
        {
            if(value != null && value.getId() != null)
            {
                gen.writeFieldName("id");
                gen.writeNumber(value.getId());
                gen.writeEndObject();
            }
        }

        @Override
        public void serializeWithType(AccountEntity value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
            typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
            serialize(value, gen, provider);
            typeSer.writeTypeSuffix(gen, typeSer.typeId(value, JsonToken.END_OBJECT));
        }
    }
}
