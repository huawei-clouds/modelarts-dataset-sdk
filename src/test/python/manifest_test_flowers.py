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

import manifest


def validate(data_set):
  assert data_set.get_size() > 18
  data_objects = data_set.get_sample_list()
  for data_object in data_objects:
    assert str(data_object.get_source()).__contains__("s3://obs-ma/test/flowers/datafiles/")
    assert str(data_object.get_source()).__contains__(".jpg")
    assert data_object.get_usage() == "TRAIN"

    annotations = data_object.get_annotations()
    for annotation in annotations:
      annotation_type = annotation.get_type()
      assert annotation_type == "modelarts/image_classification"

      annotation_name = annotation.get_name()
      assert (annotation_name == "Cat" or annotation_name == "Dog")

      annotation_loc = annotation.get_loc()
      assert annotation_loc is None or str(annotation_loc).__contains__()

      confidence = annotation.get_confidence()
      assert confidence == 0.8

      annotation_property = annotation.get_property()
      assert annotation_property == "[{}]" or annotation_property is None

      annotation_create_time = annotation.get_creation_time()
      assert str(annotation_create_time).__contains__("2019-02-20 08:2")

      annotation_annotated_by = annotation.get_annotated_by()
      assert annotation_annotated_by == "human"

      print(annotation_type + "\t" + annotation_name + "\t" + str(annotation_loc) + "\t" + str(
        annotation_property) + "\t" + str(confidence) + "\t" + str(annotation_create_time) + "\t" + str(
        annotation_annotated_by))


def main(argv):
  if argv.__len__() < 2:
    if str(argv[0]).endswith(".manifest"):
      path = argv[0]
    else:
      path = os.path.abspath('../') + "/resources/flowers-xy-V201902220937263726.manifest"
    data_set = manifest.parse_manifest(path)
    validate(data_set)
  elif argv.__len__() < 3:
    data_set = manifest.parse_manifest(argv[1])
    validate(data_set)
  else:
    path = argv[1]
    ak = argv[2]
    sk = argv[3]
    endpoint = argv[4]
    data_set = manifest.parse_manifest(path, ak, sk, endpoint)
    validate(data_set)


if __name__ == '__main__':
  # If user want to test OBS, please input OBS path, ak, sk and endpoint.
  main(sys.argv)
  print("Success")
