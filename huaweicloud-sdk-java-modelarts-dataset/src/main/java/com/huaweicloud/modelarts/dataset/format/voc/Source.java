package com.huaweicloud.modelarts.dataset.format.voc;

public class Source {
  private String database;
  private String annotation;
  private String image;

  public Source(String database) {
    this.database = database;
  }

  public Source(String database, String annotation, String image) {
    this.database = database;
    this.annotation = annotation;
    this.image = image;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public void setAnnotation(String annotation) {
    this.annotation = annotation;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getDatabase() {
    return database;
  }

  public String getAnnotation() {
    return annotation;
  }

  public String getImage() {
    return image;
  }
}
