package org.meveo.model.crm;


public enum CustomFieldTypeEnum {
  STRING(1, "customFieldTypeEnum.string"),
  DATE(2, "customFieldTypeEnum.date"),
  LONG(3, "customFieldTypeEnum.long"),
  DOUBLE(4, "customFieldTypeEnum.double"),
  LIST(5, "customFieldTypeEnum.list");
  

  private Integer id;
  private String label;

  CustomFieldTypeEnum(Integer id, String label) {
      this.id = id;
      this.label = label;
  }

  public Integer getId() {
      return id;
  }

  public String getLabel() {
      return this.label;
  }

  public static CustomFieldTypeEnum getValue(Integer id) {
      if (id != null) {
          for (CustomFieldTypeEnum type : values()) {
              if (id.equals(type.getId())) {
                  return type;
              }
          }
      }
      return null;
  }
}
