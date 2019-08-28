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

import os
import sys

from modelarts import manifest

import sys

reload(sys)
sys.setdefaultencoding('utf-8')

def validate(data_set):
  assert data_set.get_size() > 18
  data_objects = data_set.get_sample_list()
  for data_object in data_objects:
    assert "s3://obs-ma/test/classification/datafiles/" in data_object.get_source()
    assert ".jpg" in data_object.get_source()
    assert data_object.get_usage() == "TRAIN"

    annotations = data_object.get_annotations()
    inference_loc = data_object.get_inference_loc()
    id = data_object.get_id()
    assert id is None or str(id).startswith("XGDVG")
    for annotation in annotations:
      annotation_type = annotation.get_type()
      assert annotation_type == "modelarts/image_classification"

      annotation_name = annotation.get_name()
      assert (annotation_name == "Cat" or annotation_name == "Dog")

      annotation_loc = annotation.get_loc()
      assert annotation_loc is None

      confidence = annotation.get_confidence()
      assert confidence == 0.8

      annotation_property = annotation.get_property()
      assert annotation_property is not None

      annotation_create_time = annotation.get_creation_time()
      assert "2019-02-20 08:2" in annotation_create_time

      annotation_annotated_by = annotation.get_annotated_by()
      assert annotation_annotated_by == "human"

      print(annotation_type + "\t" + annotation_name + "\t" + str(annotation_loc) + "\t" + str(
        annotation_property) + "\t" + str(confidence) + "\t" + str(annotation_create_time) + "\t" + str(
        annotation_annotated_by + "\t" + str(inference_loc)))


def main(argv):
  if len(argv) < 2:
    if str(argv[0]).endswith(".manifest"):
      path = argv[0]
    else:
      path = os.path.abspath('../../../') + "/resources/V010.manifest"
    data_set = manifest.parse_manifest(path)
    chinese = False
    for sample in data_set.get_sample_list():
      for annotation in sample.get_annotations():
        print(annotation.get_name())
        if ("向日葵" == annotation.get_name()):
          chinese = True
    assert chinese;
  elif len(argv) < 3:
    data_set = manifest.parse_manifest(argv[1])
    validate(data_set)
  else:
    path = argv[1]
    ak = argv[2]
    sk = argv[3]
    endpoint = argv[4]
    data_set = manifest.parse_manifest(manifest_path=path, access_key=ak, secret_key=sk, end_point=endpoint)
    chinese = False
    for sample in data_set.get_sample_list():
      for annotation in sample.get_annotations():
        print(annotation.get_name())
        if ("向日葵" == annotation.get_name()):
          chinese = True
    assert chinese;


if __name__ == '__main__':
  # If user want to test OBS, please input OBS path, ak, sk and endpoint.
  main(sys.argv)
  print("Success")
