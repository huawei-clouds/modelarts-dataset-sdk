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

from modelarts.test import test_manifest_image_classification
from modelarts.manifest import Annotation, Sample, DataSet


def create_manifest():
  size = 0;
  sample_list = []
  for i in range(19):
    size = size + 1
    source = "s3://obs-ma/test/classification/datafiles/1_1550650984970_" + str(i) + ".jpg"
    usage = "TRAIN"
    inference_loc = "s3://obs-ma/test/classification/datafiles/1_1550650984970_" + str(i) + ".txt"
    annotations_list = []

    for j in range(1):
      annotation_type = "modelarts/image_classification"
      if 0 == i % 2:
        annotation_name = "Cat"
      else:
        annotation_name = "Dog"
      annotation_creation_time = "2019-02-20 08:23:06"
      annotation_format = "manifest"
      annotation_property = {"color": "black"}
      annotation_confidence = 0.8
      annotated_by = "human"
      annotations_list.append(
        Annotation(name=annotation_name, type=annotation_type,
                   confidence=annotation_confidence,
                   creation_time=annotation_creation_time,
                   annotated_by=annotated_by, annotation_format=annotation_format, property=annotation_property))
    sample_list.append(
      Sample(source=source, usage=usage, annotations=annotations_list, inference_loc=inference_loc))
  return DataSet(sample=sample_list, size=size)


def main(argv):
  path = os.path.abspath('../../../') + "/resources/classification-xy-V201902220937263726_2.manifest"
  dataset = create_manifest()
  if len(argv) < 2:
    dataset.save(path)
    para = []
    para.append(path)
    test_manifest_image_classification.main(para)
  else:
    path2 = argv[1]
    ak = argv[2]
    sk = argv[3]
    endpoint = argv[4]
    dataset.save(path2, ak, sk, endpoint)
    para = []
    para.append(path2)
    para.append(path2)
    para.append(ak)
    para.append(sk)
    para.append(endpoint)
    para.append(endpoint)
    test_manifest_image_classification.main(para)


if __name__ == '__main__':
  # If user want to test OBS, please input OBS path, ak, sk and endpoint.
  main(sys.argv)
  print("Success")
