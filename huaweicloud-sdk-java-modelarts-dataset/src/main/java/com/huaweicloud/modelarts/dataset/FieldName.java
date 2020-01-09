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
public class FieldName
{
    public static final String SOURCE = "source";
    
    public static final String SOURCE_TYPE = "source-type";
    
    public static final String USAGE = "usage";
    
    public static final String INFERENCE_LOC = "inference-loc";
    
    public static final String INFERENCE_LOC2 = "inference_loc";
    
    public static final String ID = "id";
    
    public static final String SOURCE_PROPERTY = "property";
    
    public static final String ANNOTATIONS = "annotation";
    
    public static final String NAME = "name";
    
    public static final String ANNOTATION_TYPE = "type";
    
    public static final String ANNOTATION_LOC = "annotation-loc";
    
    public static final String ANNOTATION_LOC2 = "annotation_loc";
    
    public static final String ANNOTATION_FORMAT = "annotation-format";
    
    public static final String ANNOTATION_FORMAT2 = "annotation_format";
    
    public static final String ANNOTATION_NAME = "name";
    
    public static final String ANNOTATION_NAMES = "annotation_names";
    
    public static final String ANNOTATION_PROPERTY = "property";
    
    public static final String ANNOTATION_HARD = "hard";
    
    public static final String ANNOTATION_HARD_COEFFICIENT = "hard-coefficient";
    
    public static final String ANNOTATION_CONFIDENCE = "confidence";
    
    public static final String ANNOTATION_CREATION_TIME = "creation-time";
    
    public static final String ANNOTATION_CREATION_TIME2 = "creation_time";
    
    public static final String ANNOTATION_ANNOTATED_BY = "annotated-by";
    
    public static final String ANNOTATION_ANNOTATED_BY2 = "annotated_by";
    
    public static final String SIZE = "size";
    
    public static final String IMAGE_CLASSIFICATION = "image_classification";
    
    public static final String AUDIO_CLASSIFICATION = "audio_classification";
    
    public static final String TEXT_CLASSIFICATION = "text_classification";
    
    public static final String OBJECT_DETECTION = "object_detection";
    
    // PASCAL VOC FORMAT
    public static final String FOLDER_NAME = "folder";
    
    public static final String FILE_NAME = "filename";
    
    public static final String DATABASE = "database";
    
    public static final String IMAGE = "image";
    
    public static final String WIDTH = "width";
    
    public static final String HEIGHT = "height";
    
    public static final String DEPTH = "depth";
    
    public static final String SEGMENTED = "segmented";
    
    public static final String OBJECT = "object";
    
    public static final String VOC_PROPERTIES = "properties";
    
    public static final String VOC_PROPERTY_KEY = "key";
    
    public static final String VOC_PROPERTY_VALUE = "value";
    
    public static final String POSE = "pose";
    
    public static final String TRUNCATED = "truncated";
    
    public static final String OCCLUDED = "occluded";
    
    public static final String DIFFICULT = "difficult";
    
    public static final String DIFFICULT_COEFFICIENT = "difficult-coefficient";
    
    public static final String CONFIDENCE = "confidence";
    
    public static final String XMIN = "xmin";
    
    public static final String YMIN = "ymin";
    
    public static final String XMAX = "xmax";
    
    public static final String YMAX = "ymax";
    
    public static final String X1 = "x1";
    
    public static final String Y1 = "y1";
    
    public static final String X2 = "x2";
    
    public static final String Y2 = "y2";
    
    public static final String CX = "cx";
    
    public static final String CY = "cy";
    
    public static final String R = "r";
    
    public static final String X = "x";
    
    public static final String Y = "y";
    
    public static final String PART = "part";
    
    public static final String PROPERTY_COLOR = "@modelarts:color";
    
    public static final String PROPERTY_START_INDEX = "@modelarts:start_index";
    
    public static final String PROPERTY_END_INDEX = "@modelarts:end_index";
    
    public static final String PROPERTY_CONTENT = "@modelarts:content";
    
    public static final String PROPERTY_SCHEMA = "@modelarts:schema";
    
    public static final String PARSE_PASCAL_VOC = "parsePascalVOC";
    
    public static final String RELATIVE_PATH = "RelativePath";
    
    public static final String RELATIVE_PATH_PREFIX = "./";
    
}
