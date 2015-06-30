package org.meveo.model.crm;


public enum CustomFieldStorageTypeEnum {
  SINGLE(1, "customFieldTemplate.storageSingle"),
  LIST(2, "customFieldTemplate.storageList"),
  MAP(3, "customFieldTemplate.storageMap");
  

  private Integer id;
  private String label;

  CustomFieldStorageTypeEnum(Integer id, String label) {
      this.id = id;
      this.label = label;
  }

  public Integer getId() {
      return id;
  }

  public String getLabel() {
      return this.label;
  }

  public static CustomFieldStorageTypeEnum getValue(Integer id) {
      if (id != null) {
          for (CustomFieldStorageTypeEnum type : values()) {
              if (id.equals(type.getId())) {
                  return type;
              }
          }
      }
      return null;
  }
}
