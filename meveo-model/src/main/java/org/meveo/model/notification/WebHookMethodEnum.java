package org.meveo.model.notification;


public enum WebHookMethodEnum {
	HTTP_GET(1,"enum.webHookMethodEnum.HTTP_GET"),
	HTTP_POST(2,"enum.webHookMethodEnum.HTTP_POST"),
	HTTP_PUT(3,"enum.webHookMethodEnum.HTTP_PUT"),
	HTTP_DELETE(4,"enum.webHookMethodEnum.HTTP_DELETE");

    private Integer id;
    private String label;
    
    WebHookMethodEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }
    

    public static WebHookMethodEnum getValue(Integer id) {
        if (id != null) {
            for (WebHookMethodEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
}
