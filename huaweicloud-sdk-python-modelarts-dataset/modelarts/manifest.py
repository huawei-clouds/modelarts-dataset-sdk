# -*- coding: utf-8 -*-
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

from modelarts import field_name
from modelarts.field_name import prefix_text, label_separator, property_start_index, property_end_index, \
  property_content, sound_classification, audio_classification
from modelarts.file_util import __is_local, save
from modelarts.file_util import __read
from obs import ObsClient

import collections
import sys

reload(sys)
sys.setdefaultencoding('utf-8')
def get_sample_list(manifest_path, task_type, exactly_match_type=False, access_key=None,
                    secret_key=None, end_point=None, usage=field_name.default_usage, ssl_verify=False,
                    max_retry_count=3, timeout=60):
  """
  get the sample list from manifest, support local and OBS path;
  If the exactly_match_type is True, then it will match the task type exactly;
  If the exactly_match_type is False, then it will not match the task type exactly and match the suffix of the type
  default_usage is all. Users can use usage="train" if users want to get train sample.
  default task type is all.

  :param manifest_path:  manifest file path
  :param task_type:  task type, like: image_classification, object_detection, audio_classification/sound_classification,
  audio_content, text_classification, text_entity
  :param exactly_match_type: whether exactly match task type. Users can set True if users want to match exactly,
        like "modelarts/image_classification; Users can set False if users don't want to match exactly,
        like "image_classification;
  :param access_key: access key of OBS
  :param secret_key: secret key of OBS
  :param end_point: end point of OBS
  :param ssl_verify: whether use ssl, set True if user want to verify certification, otherwise set False; default is False
  :param max_retry_count: max retry count, default is 3
  :param timeout: timeout [10,60], default is 60
  :param usage: usage of the sample, like "TRAIN", "EVAL", "TEST", "inference", "all", default value is all
  :return: data_list, label_type
  """
  data_set = parse_manifest(manifest_path, access_key=access_key, secret_key=secret_key, end_point=end_point,
                            ssl_verify=ssl_verify, max_retry_count=max_retry_count, timeout=timeout)
  if sound_classification is task_type:
    task_type = audio_classification
  if str(task_type).endswith("/" + sound_classification):
    task_type = str(task_type).replace(sound_classification, audio_classification)
  sample_list = data_set.get_sample_list()
  data_list = []
  label_type = field_name.single_lable
  for sample in sample_list:
    flag = False
    annotations = sample.get_annotations()
    sample_usage = sample.get_usage()
    sample_source = sample.get_source()
    if str(sample_usage).lower().__eq__(str(usage).lower()) or str(usage).lower().__eq__(field_name.default_usage):
      label_list = []
      i = 0
      if str(usage).lower().__eq__(field_name.usage_inference):
        flag = True
      for annotation in annotations:
        if i > 0:
          label_type = field_name.multi_lable
        i = i + 1
        type = annotation.get_type()
        if not exactly_match_type:
          if str(type).endswith("/" + task_type):
            flag = True
            if (task_type == field_name.image_classification or task_type == field_name.audio_classification
                    or task_type == field_name.text_classification):
              label_list.append(str(annotation.get_name()))
            elif task_type == field_name.text_entity:
              annotation_property = annotation.get_property()
              label_list.append(str(annotation.get_name()
                                    + label_separator + str(annotation_property[property_start_index])
                                    + label_separator + str(annotation_property[property_end_index])))
            elif task_type == field_name.audio_content:
              annotation_property = annotation.get_property()
              label_list.append(str(annotation_property[property_content]))
            elif task_type == field_name.object_detection:
              label_list.append(str(annotation.get_loc()))
            else:
              raise Exception("Don't support the task type:" + task_type)

        elif exactly_match_type:
          if type == task_type:
            flag = True
            if str(task_type).endswith("/" + field_name.image_classification) \
                    or str(task_type).endswith("/" + field_name.audio_classification) \
                    or str(task_type).endswith("/" + field_name.text_classification):
              label_list.append(str(annotation.get_name()))
            elif str(task_type).endswith("/" + field_name.text_entity):
              annotation_property = annotation.get_property()
              label_list.append(str(annotation.get_name()
                                    + label_separator + str(annotation_property[property_start_index])
                                    + label_separator + str(annotation_property[property_end_index])))

            elif str(task_type).endswith("/" + field_name.audio_content):
              annotation_property = annotation.get_property()
              label_list.append(str(annotation_property[property_content]))
            elif str(task_type).endswith("/" + field_name.object_detection):
              label_list.append(str(annotation.get_loc()))
            else:
              raise Exception("Don't support the task type:" + task_type)

    else:
      continue
    if str(task_type).endswith(field_name.text_classification) \
            or str(task_type).endswith(field_name.text_entity):
      assert str(sample_source).startswith(prefix_text)
      sample_source = str(sample_source)[len(prefix_text):]
    if flag:
      data_list.append([sample_source, label_list])
  return data_list, label_type


def getSources(manifest_path, source_type, obs_client=None):
  sources = []
  data_set = parse_manifest(manifest_path, obs_client=obs_client)
  sample_list = data_set.get_sample_list()
  for sample in sample_list:
    if str(source_type).lower() == str(sample.get_source_type()).lower():
      sources.append(sample.get_source())
  return sources


def getAnnotations(value):
  text = json.loads(value)
  return __getAnnotationsInternal(text)


def __getAnnotationsInternal(text):
  annotations = text.get(field_name.annotation)
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
      annotation_hard = annotation.get(field_name.annotation_hard)
      annotation_hard_coefficient = annotation.get(field_name.annotation_hard_coefficient)
      annotations_list.append(
        Annotation(name=annotation_name, type=annotation_type, loc=annotation_loc,
                   property=annotation_property,
                   confidence=annotation_confidence,
                   creation_time=annotation_creation_time,
                   annotated_by=annotated_by, annotation_format=annotation_format,
                   hard=annotation_hard,
                   hard_coefficient=annotation_hard_coefficient))
  return annotations_list

def __getDataSet(lines):
  sample_list = []
  size = 0
  for line in lines:
    if line != '':
      size = size + 1
      text = json.loads(line)
      source = text.get(field_name.source)
      assert source is not None
      usage = text.get(field_name.usage)
      source_type = text.get(field_name.source_type)
      source_property = text.get(field_name.source_property)
      id = text.get(field_name.id)
      inference_loc = text.get(field_name.inference_loc) or text.get(field_name.inference_loc2)
      annotations_list = __getAnnotationsInternal(text)
      sample_list.append(
        Sample(source=source, usage=usage, annotations=annotations_list, inference_loc=inference_loc, id=id,
               source_type=source_type, source_property=source_property))
  return DataSet(sample=sample_list, size=size)


def parse_manifest(manifest_path, obs_client):
  local = __is_local(manifest_path)

  if local:
    with open(manifest_path) as f_obj:
      lines = f_obj.readlines()
      return __getDataSet(lines)
  else:
    if obs_client is None:
      raise ValueError("Please input obs_client.")

    data = __read(manifest_path, obs_client)
    result = __getDataSet(data.decode().split("\n"))
    return result


def parse_manifest(manifest_path, access_key=None, secret_key=None, end_point=None, obs_client=None, ssl_verify=False,
                   max_retry_count=3, timeout=60):
  """
  user give the path of manifest file, it will return the dataset,
  including data object list, annotation list and so on after the manifest was parsed.

  :param manifest_path:  path of manifest file
  :param access_key: access key of OBS
  :param secret_key: secret key of OBS
  :param end_point: end point of OBS
  :param ssl_verify: whether use ssl, set True if user want to verify certification, otherwise set False; default is False
  :param max_retry_count: max retry count, default is 3
  :param timeout: timeout [10,60], default is 60
  :return: data set of manifest
  """
  local = __is_local(manifest_path)

  if local:
    with open(manifest_path) as f_obj:
      lines = f_obj.readlines()
      return __getDataSet(lines)
  else:
    if (access_key is None or secret_key is None or end_point is None) and obs_client is None:
      raise ValueError("Please input ak, sk and endpoint or obs_client")
    if obs_client is not None:
      obs_client=obs_client
    else:
      obs_client = ObsClient(
        access_key_id=access_key,
        secret_access_key=secret_key,
        server=end_point,
        long_conn_mode=True,
        ssl_verify=ssl_verify,
        max_retry_count=max_retry_count,
        timeout=timeout
      )
    data = __read(manifest_path, obs_client)
    result = __getDataSet(data.decode().split("\n"))
    return result


class DataSet(object):
  """
  dataset for manifest
  dataset architecture:
    --size
    --sample list
      --sample 1
      --sample 2
          --source
          ...
          --annotation list
            --annotation 1
            --annotation 2
              --name
              --annotation_loc
              --type
              ...
              annotated_by
  """

  def __init__(self, sample, size=None):
    self._sample = sample
    self._size = size

  def get_size(self):
    """
    :return size of the data set
    Optional field
    """
    return self._size

  def get_sample_list(self):
    """
    :return a list of sample
    Mandatory field
    """
    return self._sample

  def __put(self, sample_json, key, value):
    """
    put key and value to sample_json if value is not None
    :param sample_json: sample json
    :param key: key
    :param value: value
    :return: sample json
    """
    if value is not None:
      sample_json[key] = value
    return sample_json

  def __annotations_to_json(self, annotations):
    """
    convert annotations to json
    :return annotation json
    """
    annotations_json = []
    annotation_json = collections.OrderedDict()
    if annotations is None:
      return None
    for annotation in annotations:
      self.__put(annotation_json, field_name.annotation_name, annotation.get_name())
      self.__put(annotation_json, field_name.annotation_loc, annotation.get_loc())
      self.__put(annotation_json, field_name.annotation_type, annotation.get_type())
      self.__put(annotation_json, field_name.annotation_format, annotation.get_annotation_format())
      self.__put(annotation_json, field_name.annotation_confidence, annotation.get_confidence())
      self.__put(annotation_json, field_name.annotation_hard, annotation.get_hard())
      self.__put(annotation_json, field_name.annotation_hard_coefficient, annotation.get_hard_coefficient())
      self.__put(annotation_json, field_name.annotation_property, annotation.get_property())
      self.__put(annotation_json, field_name.annotation_annotated_by, annotation.get_annotated_by())
      self.__put(annotation_json, field_name.annotation_creation_time, annotation.get_creation_time())
      annotations_json.append(annotation_json)
    return annotations_json

  def __toJSON(self, sample):
    """
    convert sample to json
    :param sample: sample object of dataset
    :return: sample json
    """
    sample_json = {}
    self.__put(sample_json, field_name.id, sample.get_id())
    self.__put(sample_json, field_name.source, sample.get_source())
    self.__put(sample_json, field_name.usage, sample.get_usage())
    self.__put(sample_json, field_name.inference_loc, sample.get_inference_loc())
    self.__put(sample_json, field_name.annotation, self.__annotations_to_json(sample.get_annotations()))
    return sample_json

  def save(self, path, access_key=None, secret_key=None, end_point=None, saveMode="w", ssl_verify=False,
           max_retry_count=3, timeout=60):
    """
    save dataset to local or OBS
    It will overwrite if the file path already exists.
    Please check the file path before invoking this method

    :param path: manifest output path
    :param access_key: access key of OBS
    :param secret_key: secret key of OBS
    :param end_point: end point of OBS
    :param saveMode: default is "w", it will overwrite if file already exists.
        User can set "a" if user want to append content to one file.
        Can not append to a normal object.
    :param ssl_verify: whether use ssl, set True if user want to verify certification, otherwise set False; default is False
    :param max_retry_count: max retry count, default is 3
    :param timeout: timeout [10,60], default is 60
    :return: None
    """
    if access_key is None and secret_key is None and end_point is None:
      with open(path, saveMode) as f_obj:
        for sample in self.get_sample_list():
          value = self.__toJSON(sample)
          json.dump(value, f_obj, separators=(",", ":"))
          f_obj.write('\n')
    elif access_key is None:
      raise Exception("access_key is None")
    elif secret_key is None:
      raise Exception("secret_key is None")
    elif end_point is None:
      raise Exception("end_point is None")
    else:
      manifest_json = []
      for sample in self.get_sample_list():
        value = self.__toJSON(sample)
        manifest_json.append(json.dumps(value, separators=(",", ":")))
      save(manifest_json, path, access_key=access_key, secret_key=secret_key, end_point=end_point,
           saveMode=saveMode, ssl_verify=ssl_verify, max_retry_count=max_retry_count, timeout=timeout)


class Sample(object):
  def __init__(self, source, annotations=None, usage=None, inference_loc=None, id=None, source_type=None,
               source_property=None):
    self._source = source
    self._source_type = source_type
    self._source_property = source_property
    self._usage = usage
    self._annotation = annotations
    self._inference_loc = inference_loc
    self._id = id

  def get_source(self):
    """
    :return "source" attribute
    Mandatory field
    """
    return self._source

  def get_source_type(self):
    """
    :return "source_type" attribute
    Optional field
    """
    return self._source_type

  def get_id(self):
    """
    :return "id" attribute, one of
    Optional field
    """
    return self._id

  def get_usage(self):
    """
    :return "usage" attribute, one of
    Optional field
    """
    return self._usage

  def get_inference_loc(self):
    """
    :return "inference_loc" attribute, one of Optional field
    """
    return self._inference_loc

  def get_annotations(self):
    """
    :return a list of class Annotation
    Optional field
    """
    return self._annotation


class Annotation:

  def __init__(self, name=None, type=None, loc=None, property=None, confidence=None, creation_time=None,
               annotated_by=None, annotation_format=None, hard=None, hard_coefficient=None):
    self._name = name
    self._type = type
    self._annotation_loc = loc
    self._property = property
    self._hard = hard
    self._hard_coefficient = hard_coefficient
    self._confidence = confidence
    self._creation_time = creation_time
    self._annotated_by = annotated_by
    self._annotation_format = annotation_format

  def get_type(self):
    """
    :return type of dataset: modelarts/image_classification, modelarts/object_detection
    Optional field
    """
    return self._type

  def get_name(self):
    """
    :return the name of this annotation, like "cat"
    Mandatory field if get_loc is None
    """
    return self._name

  def get_loc(self):
    """
    :return in case of object detection, this will return the annotation file,
    otherwise return null
    Mandatory field if get_name is None
    """
    return self._annotation_loc

  def get_property(self):
    """
    :return a KV pair list
    Optional field
    """
    return self._property

  def get_hard(self):
    """
    :return set true if it's hard annotation, set false  if it's not hard annotation
    Optional field
    """
    return self._hard

  def get_hard_coefficient(self):
    """
    :return set the coefficient of hard
    Optional field
    """
    return self._hard_coefficient

  def get_confidence(self):
    """
    :return confidence of label
    Optional field
    """
    return self._confidence

  def get_creation_time(self):
    """
    :return when this annotation is created
    Optional field
    """
    return self._creation_time

  def get_annotation_format(self):
    """
    :return when this annotation format
    Optional field
    """
    return self._annotation_format

  def get_annotated_by(self):
    """
    :return who this annotation is created by
    Optional field
    """
    return self._annotated_by
