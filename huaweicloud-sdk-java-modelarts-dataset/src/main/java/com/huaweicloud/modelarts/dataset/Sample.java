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

import java.util.List;

/**
 * sample of manifest
 */
public class Sample {
  /**
   * source path of raw data
   * Mandatory field
   */
  private String source;

  /**
   * usage of this sample, like "TRAIN", "EVAL", "TEST", "inference"
   * Optional field
   */
  private String usage;

  /**
   * inference location
   * Optional field
   */
  private String inferenceLoc;

  /**
   * annotation list
   * Optional field
   */
  private List<Annotation> annotations;

  /**
   * sample id
   * Optional field
   */
  private String id;

  public Sample() {
  }

  public Sample(String source) {
    this.source = source;
  }

  public Sample(String source, String usage, String inferenceLoc, List<Annotation> annotations, String id) {
    this.source = source;
    this.usage = usage;
    this.inferenceLoc = inferenceLoc;
    this.annotations = annotations;
    this.id = id;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getUsage() {
    return usage;
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

  public String getInferenceLoc() {
    return inferenceLoc;
  }

  public void setInferenceLoc(String inferenceLoc) {
    this.inferenceLoc = inferenceLoc;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }

  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Sample{" +
        "source='" + source + '\'' +
        ", usage='" + usage + '\'' +
        ", inferenceLoc='" + inferenceLoc + '\'' +
        ", annotations=" + annotations +
        ", id='" + id + '\'' +
        '}';
  }
}
