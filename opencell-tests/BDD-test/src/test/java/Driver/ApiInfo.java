package Driver;

public class ApiInfo {
    private String apiVer;
    private String businessDomainPath;
    private String entity;
    private String codeOrId;

    private String requestBody;

    public ApiInfo(String apiVer, String businessDomainPath, String entity, String codeOrId, String requestBody){
        this.apiVer = apiVer;
        this.businessDomainPath = businessDomainPath;
        this.entity = entity;
        this.codeOrId = codeOrId;
        this.requestBody = requestBody;
    }

    public String getApiVer() {
        return apiVer;
    }

    public String getBusinessDomainPath() {
        return businessDomainPath;
    }

    public String getEntity() {
        return entity;
    }

    public String getCodeOrId() {
        return codeOrId;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
