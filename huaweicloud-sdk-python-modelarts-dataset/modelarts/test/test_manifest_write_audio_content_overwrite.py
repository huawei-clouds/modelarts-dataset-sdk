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
from modelarts.field_name import audio_content, prefix_s3, property_content
from modelarts.manifest import Annotation, Sample, DataSet


def create_manifest():
  size = 0
  sample_list = []
  for i in range(19):
    size = size + 1
    source = prefix_s3 + "audio" + str(i) + ".wav"
    usage = "TRAIN"
    annotations_list = []

    for j in range(1):
      annotation_type = "modelarts/" + audio_content
      annotation_creation_time = "2019-04-28 08:23:06"
      annotation_format = "manifest"
      annotation_property = {property_content: "Hello world!"}
      annotation_confidence = 0.8
      annotated_by = "human"
      annotations_list.append(
        Annotation(type=annotation_type,
                   confidence=annotation_confidence,
                   creation_time=annotation_creation_time,
                   annotated_by=annotated_by, annotation_format=annotation_format, property=annotation_property))
    sample_list.append(
      Sample(source=source, usage=usage, annotations=annotations_list))
  return DataSet(sample=sample_list, size=size)


def main(argv):
  path = os.path.abspath('../../../') + "/resources/audio_content_write_2.manifest"
  dataset = create_manifest()
  if len(argv) < 2:
    dataset.save(path)
    para = []
    para.append(path)
    sample_list, label_type = manifest.get_sample_list(path, audio_content)
    assert (label_type == field_name.single_lable)
    assert len(sample_list) == 19
    for raw_data, label_list in sample_list:
      assert prefix_s3 + "audio" in str(raw_data)
      for label in label_list:
        assert "Hello world!" == label
      assert len(label_list) == 1
    print("Local")
  else:
    path1 = "s3a://carbonsouth/manifest/audio_content_write_1.manifest"
    ak = argv[1]
    sk = argv[2]
    endpoint = argv[3]
    dataset.save(path1, ak, sk, endpoint)
    print("OBS")


if __name__ == '__main__':
  # If user want to test OBS, please input OBS path, ak, sk and endpoint.
  main(sys.argv)
  print("Success")
