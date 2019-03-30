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
from Encoder import ManifestEncoder
from file import is_local, save
from file import read


def get_sample_list(manifest_path, task_type, exactly_match_type=False, *args):
  """
  get the sample list from manifest, support local and OBS path;
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
      if i > 0:
        label_type = field_name.multi_lable
      i = i + 1
      type = annotation.get_type()
      if not exactly_match_type:
        if str(type).endswith("/" + task_type):
          if (task_type == field_name.image_classification or task_type == field_name.sound_classification
                  or task_type == field_name.text_classification):
            label_list.append(annotation.get_name())
          if task_type == field_name.object_detection:
            label_list.append(annotation.get_loc())

      elif exactly_match_type:
        if type == task_type:
          if str(task_type).endswith("/" + field_name.image_classification) \
                  or str(task_type).endswith("/" + field_name.sound_classification) \
                  or str(task_type).endswith("/" + field_name.text_classification):
            label_list.append(annotation.get_name())
          if str(task_type).endswith("/" + field_name.object_detection):
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
    sample_list = []
    size = 0
    for line in lines:
      if line != '':
        size = size + 1
        text = json.loads(line)
        source = text.get(field_name.source)
        assert None != source
        usage = text.get(field_name.usage)
        annotations = text.get(field_name.annotation)
        inference_loc = text.get(field_name.inference_loc) or text.get(field_name.inference_loc2)
        annotations_list = []
        if annotations is not None:
          for annotation in annotations:
            annotation_type = annotation.get(field_name.annotation_type)
            annotation_name = annotation.get(field_name.annotation_name)
            annotation_loc = annotation.get(field_name.annotation_loc) or annotation.get(field_name.annotation_loc2)
            annotation_creation_time = annotation.get(field_name.annotation_creation_time) or annotation.get(
              field_name.annotation_creation_time2)
            annotation_property = annotation.get(field_name.annotation_property)
            annotation_format = annotation.get(field_name.annotation_format) or annotation.get(
              field_name.annotation_format2)
            annotation_confidence = annotation.get(field_name.annotation_confidence)
            annotated_by = annotation.get(field_name.annotation_annotated_by) or annotation.get(
              field_name.annotation_annotated_by2)
            annotations_list.append(
              Annotation(name=annotation_name, type=annotation_type, loc=annotation_loc,
                         property=annotation_property,
                         confidence=annotation_confidence,
                         creation_time=annotation_creation_time,
                         annotated_by=annotated_by, annotation_format=annotation_format))
          sample_list.append(
            Sample(source=source, usage=usage, annotations=annotations_list, inference_loc=inference_loc))
    return DataSet(sample=sample_list, size=size)

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

  def __init__(self, sample, size=None):
    self.sample = sample
    self.size = size

  def get_size(self):
    """
    :return size of the data set
    Optional field
    """
    return self.size

  def get_sample_list(self):
    """
    :return a list of sample
    Mandatory field
    """
    return self.sample

  def save(self, path, *args):
    if args.__len__() < 1:
      for sample in self.get_sample_list():
        with open(path, "a") as f_obj:
          json.dump(sample, f_obj, cls=ManifestEncoder)
          f_obj.write('\n')
    else:
      if (args.__len__() < 3):
        raise ValueError("please input OBS path, ak, sk and endpoint.")
      manifest_json = []
      for sample in self.get_sample_list():
        manifest_json.append(json.dumps(sample, cls=ManifestEncoder))
      save(manifest_json, path, args[0], args[1], args[2])


class Sample(object):
  def __init__(self, source, annotations=None, usage=None, inference_loc=None):
    self.source = source
    self.usage = usage
    self.annotation = annotations
    self.inference_loc = inference_loc

  def get_source(self):
    """
    :return "source" attribute
    Mandatory field
    """
    return self.source

  def get_usage(self):
    """
    :return "usage" attribute, one of
    Optional field
    """
    return self.usage

  def get_annotations(self):
    """
    :return a list of class Annotation
    Optional field
    """
    return self.annotation


class Annotation:

  def __init__(self, name=None, type=None, loc=None, property=None, confidence=None, creation_time=None,
               annotated_by=None,
               annotation_format=""):
    self.name = name
    self.type = type
    self.annotation_loc = loc
    self.property = property
    self.confidence = confidence
    self.creation_time = creation_time
    self.annotated_by = annotated_by
    self.annotation_format = annotation_format

  def get_type(self):
    """
    :return type of dataset: modelarts/image_classification, modelarts/object_detection
    Optional field
    """
    return self.type

  def get_name(self):
    """
    :return the name of this annotation, like "cat"
    Mandatory field if get_loc is None
    """
    return self.name

  def get_loc(self):
    """
    :return in case of object detection, this will return the annotation file,
    otherwise return null
    Mandatory field if get_name is None
    """
    return self.annotation_loc

  def get_property(self):
    """
    :return a KV pair list
    Optional field
    """
    return self.property

  def get_confidence(self):
    """
    :return confidence of label
    Optional field
    """
    return self.confidence

  def get_creation_time(self):
    """
    :return when this annotation is created
    Optional field
    """
    return self.creation_time

  def get_annotation_format(self):
    """
    :return when this annotation format
    Optional field
    """
    return self.annotation_format

  def get_annotated_by(self):
    """
    :return who this annotation is created by
    Optional field
    """
    return self.annotated_by
