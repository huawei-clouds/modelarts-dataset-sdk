# Copyright 2018 Deep Learning Service of Huawei Cloud. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


annotation = "annotation"
annotation_type = "type"
annotation_loc = "annotation-loc"
annotation_loc2 = "annotation_loc"
annotation_format = "annotation-format"
annotation_format2 = "annotation_format"
annotation_name = "name"
annotation_id = "id"
annotation_property = "property"
annotation_hard = "hard"
annotation_hard_coefficient = "hard-coefficient"
annotation_confidence = "confidence"
annotation_creation_time = "creation-time"
annotation_creation_time2 = "creation_time"
annotation_annotated_by = "annotated-by"
annotation_annotated_by2 = "annotated_by"

source = "source"
source_type = "source-type"
source_property = "property"
size = "size"
usage = "usage"
id = "id"
inference_loc = "inference-loc"
inference_loc2 = "inference_loc"

image_classification = "image_classification"
audio_classification = "audio_classification"
sound_classification = "sound_classification"
audio_content = "audio_content"
text_classification = "text_classification"
text_entity = "text_entity"
text_triplet = "text_triplet"
object_detection = "object_detection"
prefix_text = "content://"

single_lable = "single"
multi_lable = "multi"

s3 = "s3:"
prefix_s3 = "s3://"
prefix_s3_upper = "S3://"

s3a = "s3a:"
prefix_s3a = "s3a://"
prefix_s3a_upper = "S3a://"

separator = "/"
newline_character = "\n"

# Usage
default_usage = "all"
usage_train = "train"
usage_eval = "eval"
usage_test = "test"
usage_inference = "inference"

label_separator = '\u0001'
property_start_index = "@modelarts:start_index"
property_end_index = "@modelarts:end_index"
property_from = "@modelarts:from"
property_to = "@modelarts:to"

property_content = "@modelarts:content"

CARBON = "carbon"
CARBONDATA = "carbondata"

# PASCAL VOC
ANNOTATIONS = "annotation"
FOLDER_NAME = "folder"
FILE_NAME = "filename"
SOURCE = "source"
DATABASE = "database"
IMAGE = "image"
SIZE = "size"
WIDTH = "width"
HEIGHT = "height"
DEPTH = "depth"
SEGMENTED = "segmented"
OBJECT = "object"
NAME = "name"
VOC_PROPERTIES = "properties"
VOC_PROPERTY = "property"
VOC_PROPERTY_KEY = "key"
VOC_PROPERTY_VALUE = "value"
POSE = "pose"
TRUNCATED = "truncated"
OCCLUDED = "occluded"
DIFFICULT = "difficult"
DIFFICULT_COEFFICIENT = "difficult-coefficient"
CONFIDENCE = "confidence"
X_MIN = "xmin"
X_MAX = "xmax"
Y_MIN = "ymin"
Y_MAX = "ymax"
X1 = "x1"
Y1 = "y1"
X2 = "x2"
Y2 = "y2"
CX = "cx"
CY = "cy"
R = "r"
X = "x"
Y = "y"
PART = "part"
PARSE_PASCAL_VOC = "parsePascalVOC"

SECONDARY_LABELS = "secondary_labels"
SECONDARY_LABELS_NAME = "name"
SECONDARY_LABELS_VALUE = "value"

# built-in properties
INTERNAL_PROPERTY_KEY_PREFIX = "@modelarts:"

SHORTCUT = INTERNAL_PROPERTY_KEY_PREFIX + "shortcut"
COLOR = INTERNAL_PROPERTY_KEY_PREFIX + "color"

# label properties
SHAPE = INTERNAL_PROPERTY_KEY_PREFIX + "shape"
FEATURE = INTERNAL_PROPERTY_KEY_PREFIX + "feature"
CLOCKWISE_ANGLE = INTERNAL_PROPERTY_KEY_PREFIX + "clockwise_angle"
ROTATE_BACKGROUND = INTERNAL_PROPERTY_KEY_PREFIX + "rotate_background"
