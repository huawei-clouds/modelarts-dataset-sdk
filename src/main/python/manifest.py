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


import json

import field_name
from file import is_local
from file import read


def get_sample_list(manifest_path, task_type, exactly_match_type=False, *args):
    """
    get the sample list from masnifest, support local and OBS path;
    If the exactly_match_type is True, then it will match the task type exactly;
    If the exactly_match_type is False, then it will not match the task type exactly and match the suffix of the type
    """
    data_set = parse_manifest(manifest_path, *args)
    sample_list = data_set.get_sample_list()
    data_list = []
    label_type = field_name.single_lable

    for sample in sample_list:
        annotations = sample.get_annotations()
        label_list = []
        i = 0
        for annotation in annotations:
            if (i > 0):
                label_type = field_name.multi_lable
            i = i + 1
            type = annotation.get_type()
            if (exactly_match_type == False):
                if (str(type).endswith("/" + task_type)):
                    if (task_type == field_name.image_classification or task_type == field_name.sound_classification
                            or task_type == field_name.text_classification):
                        label_list.append(annotation.get_name())
                    if (task_type == field_name.object_detection):
                        label_list.append(annotation.get_loc())

            elif (exactly_match_type == True):
                if (type == task_type):
                    if (str(task_type).endswith("/" + field_name.image_classification) or str(task_type).endswith(
                            "/" + field_name.sound_classification) or str(task_type).endswith(
                        "/" + field_name.text_classification)):
                        label_list.append(annotation.get_name())
                    if (str(task_type).endswith("/" + field_name.object_detection)):
                        label_list.append(annotation.get_loc())
        data_list.append([sample.get_source(), label_list])
    return data_list, label_type


def parse_manifest(manifest_path, *args):
    """
    user give the path of manifest file, it will return the dataset,
    including data object list, annotation list and so on after the manifest was parsed.
    :param manifest_path: path of manifest file
    :return: data set
    """

    def __getDataSet(lines):
        sampleList = []
        size = 0
        for line in lines:
            size = size + 1
            text = json.loads(line)
            source = text.get(field_name.source)
            assert None != source
            usage = text.get(field_name.usage)
            annotations = text.get(field_name.annotation)
            inference_loc = text.get(field_name.inference_loc)
            annotationsList = []
            if annotations != None:
                for annotation in annotations:
                    annotation_type = annotation.get(field_name.annotation_type)
                    annotation_name = annotation.get(field_name.annotation_name)
                    annotation_loc = annotation.get(field_name.annotation_loc)
                    annotation_creation_time = annotation.get(field_name.annotation_creation_time)
                    annotation_property = annotation.get(field_name.annotation_property)
                    annotation_format = annotation.get(field_name.annotation_format)
                    annotation_confidence = annotation.get(field_name.annotation_confidence)
                    annotated_by = annotation.get(field_name.annotation_annotated_by)
                    annotationsList.append(
                        Annotation(type=annotation_type, name=annotation_name, loc=annotation_loc,
                                   property=annotation_property,
                                   confidence=annotation_confidence,
                                   creation_time=annotation_creation_time,
                                   annotated_by=annotated_by, annotation_format=annotation_format))
                sampleList.append(
                    Sample(source=source, usage=usage, annotation=annotationsList, inference_loc=inference_loc))
        return DataSet(size=size, sample=sampleList)

    local = is_local(manifest_path)

    if local:
        with open(manifest_path) as f_obj:
            lines = f_obj.readlines()
            return __getDataSet(lines)
    else:
        if args.__len__() < 3:
            raise ValueError("Please input ak, sk and endpoint")
        ak = args[0]
        sk = args[1]
        endpoint = args[2]
        data = read(manifest_path, ak, sk, endpoint)
        result = __getDataSet(data.decode().split("\n"))
        return result


class DataSet(object):

    def __init__(self, size, sample):
        self.__size = size
        self.__sample = sample

    def get_size(self):
        """
        return size of the data set
        """
        return self.__size

    def get_sample_list(self):
        """
        :return a list of sample
        """
        return self.__sample


class Sample(object):
    def __init__(self, source, usage, annotation, inference_loc):
        self.__source = source
        self.__usage = usage
        self.__annotation = annotation
        self.__inference_loc = inference_loc

    def get_source(self):
        """
        return "source" attribute
        """
        return self.__source

    def get_usage(self):
        """
        return "usage" attribute, one of
        """
        return self.__usage

    def get_annotations(self):
        """
        return a list of class Annotation
        """
        return self.__annotation


class Annotation:
    def __init__(self, type, name, loc, property, confidence, creation_time, annotated_by, annotation_format):
        self.__type = type
        self.__name = name
        self.__loc = loc
        self.__property = property
        self.__confidence = confidence
        self.__creation_time = creation_time
        self.__annotated_by = annotated_by
        self.__annotation_format = annotation_format

    def get_type(self):
        """
        return type of dataset: image_classification, object_detection
        """
        return self.__type

    def get_name(self):
        """
        return the name of this annotation, like "cat"
        """
        return self.__name

    def get_loc(self):
        """
        in case of object detection, this will return the annotation file,
        otherwise return null
        """
        return self.__loc

    def get_property(self):
        """
        return a KV pair list
        """
        return self.__property

    def get_confidence(self):
        """
        return confidence of label
        """
        return self.__confidence

    def get_creation_time(self):
        """
        return when this annotation is created
        """
        return self.__creation_time

    def get_annotation_format(self):
        """
        return when this annotation format
        """
        return self.__annotation_format

    def get_annotated_by(self):
        """
        return who this annotation is created by
        """
        return self.__annotated_by
