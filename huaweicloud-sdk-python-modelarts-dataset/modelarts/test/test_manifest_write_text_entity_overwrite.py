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

from modelarts import manifest, field_name
from modelarts.field_name import prefix_text, text_entity, property_start_index, property_end_index, label_separator
from modelarts.manifest import Annotation, Sample, DataSet


def create_manifest():
  size = 0
  sample_list = []
  for i in range(19):
    size = size + 1
    source = prefix_text + "raw data" + str(i)
    usage = "TRAIN"
    annotations_list = []

    for j in range(1):
      annotation_type = "modelarts/" + text_entity
      if 0 == i % 2:
        annotation_name = "name"
      else:
        annotation_name = "location"
      annotation_creation_time = "2019-04-28 08:23:06"
      annotation_format = "manifest"
      annotation_property = {property_start_index: 0, property_end_index: 5}
      annotation_confidence = 0.8
      annotated_by = "human"
      annotations_list.append(
        Annotation(name=annotation_name, type=annotation_type,
                   confidence=annotation_confidence,
                   creation_time=annotation_creation_time,
                   annotated_by=annotated_by, annotation_format=annotation_format, property=annotation_property))
    sample_list.append(
      Sample(source=source, usage=usage, annotations=annotations_list))
  return DataSet(sample=sample_list, size=size)


def main(argv):
  path = os.path.abspath('../../../') + "/resources/text_entity_write_1.manifest"
  dataset = create_manifest()
  if len(argv) < 2:
    dataset.save(path)
    para = []
    para.append(path)
    sample_list, label_type = manifest.get_sample_list(path, text_entity)
    assert (label_type == field_name.single_lable)
    assert len(sample_list) == 19
    for raw_data, label_list in sample_list:
      assert "raw data" in str(raw_data)
      for label in label_list:
        label, start_index, end_index = str.split(label, label_separator)
        if "name" == label or "location" == label:
          assert start_index in "0"
          assert 5 == int(end_index)
        else:
          assert False
      assert len(label_list) == 1
  else:
    path2 = argv[1]
    ak = argv[2]
    sk = argv[3]
    endpoint = argv[4]
    dataset.save(path2, ak, sk, endpoint)


if __name__ == '__main__':
  # If user want to test OBS, please input OBS path, ak, sk and endpoint.
  main(sys.argv)
  print("Success")
