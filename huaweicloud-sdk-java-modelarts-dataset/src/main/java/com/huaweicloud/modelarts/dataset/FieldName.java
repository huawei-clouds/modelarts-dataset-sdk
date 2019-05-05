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

/**
 * Constants for annotation/sample/dataset
 */
public class FieldName {
  public static String SOURCE = "source";
  public static String USAGE = "usage";
  public static String INFERENCE_LOC = "inference-loc";
  public static String INFERENCE_LOC2 = "inference_loc";
  public static String ID = "id";
  public static String ANNOTATIONS = "annotation";
  public static String NAME = "name";

  public static String ANNOTATION_TYPE = "type";
  public static String ANNOTATION_LOC = "annotation-loc";
  public static String ANNOTATION_LOC2 = "annotation_loc";
  public static String ANNOTATION_FORMAT = "annotation-format";
  public static String ANNOTATION_FORMAT2 = "annotation_format";
  public static String ANNOTATION_NAME = "name";
  public static String ANNOTATION_PROPERTY = "property";
  public static String ANNOTATION_HARD = "hard";
  public static String ANNOTATION_CONFIDENCE = "confidence";
  public static String ANNOTATION_CREATION_TIME = "creation-time";
  public static String ANNOTATION_CREATION_TIME2 = "creation_time";
  public static String ANNOTATION_ANNOTATED_BY = "annotated-by";
  public static String ANNOTATION_ANNOTATED_BY2 = "annotated_by";

  public static String SIZE = "size";

  public static String IMAGE_CLASSIFICATION = "image_classification";
  public static String AUDIO_CLASSIFICATION = "audio_classification";
  public static String TEXT_CLASSIFICATION = "text_classification";
  public static String OBJECT_DETECTION = "object_detection";

  // PASCAL VOC FORMAT
  public static String FOLDER_NAME = "folder";
  public static String FILE_NAME = "filename";
  public static String DATABASE = "database";
  public static String IMAGE = "image";
  public static String WIDTH = "width";
  public static String HEIGHT = "height";
  public static String DEPTH = "depth";
  public static String SEGMENTED = "segmented";
  public static String OBJECT = "object";
  public static String POSE = "pose";
  public static String TRUNCATED = "truncated";
  public static String OCCLUDED = "occluded";
  public static String DIFFICULT = "difficult";
  public static String XMIN = "xmin";
  public static String YMIN = "ymin";
  public static String XMAX = "xmax";
  public static String YMAX = "ymax";
  public static String X1 = "x1";
  public static String Y1 = "y1";
  public static String X2 = "x2";
  public static String Y2 = "y2";
  public static String CX = "cx";
  public static String CY = "cy";
  public static String R = "r";

  public static String X = "x";
  public static String Y = "y";
  public static String PART = "part";

  public static String PROPERTY_COLOR = "@modelarts:color";
  public static String PROPERTY_START_INDEX = "@modelarts:start_index";
  public static String PROPERTY_END_INDEX = "@modelarts:end_index";

  public static String PROPERTY_CONTENT = "@modelarts:content";

}
