/*
 * Copyright 2018 Deep Learning Service of Huawei Cloud. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.modelarts.dataset;

import org.json.JSONObject;

/**
 * Annotation, including name for classification, annotationLoc for object detection and so on.
 */
public class Annotation {
  /**
   * object name in Annotation, like "Cat", "Dog"
   * Mandatory field if annotationLoc is null, otherwise is optional
   */
  private String name;

  /**
   * annotation type, such as: modelarts/image_classification, modelarts/object_detection,
   * modelarts/sound_classification, modelarts/test_classification
   * modelarts is product name, image_classification is the algorithm name.
   * Optional field
   */
  private String type;

  /**
   * annotation location, for example, it's a xml path for object_detection
   * Mandatory field if name is null, otherwise is optional
   */
  private String annotationLoc;

  /**
   * annotation property, like the color of cat
   * Optional field
   */
  private JSONObject property;

  /**
   * whether is hard annotation
   * Optional field
   */
  private boolean hard;

  /**
   * Confidence for annotation that was annotated by machine
   * the value: 0 <= Confidence < 1
   * Optional field
   */
  private double confidence;

  /**
   * the time of creating annotation
   * Optional field
   */
  private String creationTime;

  /**
   * who did annotated annotation? like human, modelarts/active-learning, modelarts/auto-label.
   * Optional field
   */
  private String annotatedBy;

  /**
   * annotation format, like "PASCAL VOC"
   * Optional field
   */
  private String annotationFormat;

  public Annotation() {
  }

  /**
   * constructor with annotation name
   *
   * @param name annotation name
   */
  public Annotation(String name) {
    this.name = name;
  }

  public Annotation(String name, String type, String annotationLoc, JSONObject property, double confidence, String creationTime, String annotatedBy, String annotationFormat) {
    this.name = name;
    this.type = type;
    this.annotationLoc = annotationLoc;
    this.property = property;
    this.confidence = confidence;
    this.creationTime = creationTime;
    this.annotatedBy = annotatedBy;
    this.annotationFormat = annotationFormat;
  }

  public Annotation(String name, String type, String annotationLoc, JSONObject property, double confidence, String creationTime, String annotatedBy, String annotationFormat, boolean hard) {
    this.name = name;
    this.type = type;
    this.annotationLoc = annotationLoc;
    this.property = property;
    this.confidence = confidence;
    this.creationTime = creationTime;
    this.annotatedBy = annotatedBy;
    this.annotationFormat = annotationFormat;
    this.hard = hard;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAnnotationLoc() {
    return annotationLoc;
  }

  public void setAnnotationLoc(String annotationLoc) {
    this.annotationLoc = annotationLoc;
  }

  public JSONObject getProperty() {
    return property;
  }

  public void setProperty(JSONObject property) {
    this.property = property;
  }

  public double getConfidence() {
    return confidence;
  }

  public void setConfidence(double confidence) {
    this.confidence = confidence;
  }

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  public String getAnnotatedBy() {
    return annotatedBy;
  }

  public void setAnnotatedBy(String annotatedBy) {
    this.annotatedBy = annotatedBy;
  }

  public String getAnnotationFormat() {
    return annotationFormat;
  }

  public void setAnnotationFormat(String annotationFormat) {
    this.annotationFormat = annotationFormat;
  }

  public boolean isHard() {
    return hard;
  }

  public void setHard(boolean hard) {
    this.hard = hard;
  }

  @Override
  public String toString() {
    return "Annotation{" +
        "name='" + name + '\'' +
        ", type='" + type + '\'' +
        ", annotationLoc='" + annotationLoc + '\'' +
        ", property=" + property +
        ", hard=" + hard +
        ", confidence=" + confidence +
        ", creationTime='" + creationTime + '\'' +
        ", annotatedBy='" + annotatedBy + '\'' +
        ", annotationFormat='" + annotationFormat + '\'' +
        '}';
  }
}
