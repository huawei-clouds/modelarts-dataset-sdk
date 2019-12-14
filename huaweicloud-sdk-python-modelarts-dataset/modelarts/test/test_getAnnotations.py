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


import sys

from modelarts import manifest

# reload(sys)
# sys.setdefaultencoding('utf-8')

def main(argv):
    line = "{\"annotation\":[{\"name\":\"cat\",\"type\":\"modelarts/image_classification\",\"creation-time\":\"2019-06-28 16:07:52\",\"annotated-by\":\"human/ei_modelarts_y00218826_01/ei_modelarts_y00218826_01\"}],\"usage\":\"train\",\"source\":\"s3://obs-lzy/train_data/data05/data0509/data4/input/猫/CAT_04/00000990_004.jpg\",\"id\":\"0001842d9eb2a3078d835f0d18cf58c1\"}"
    annotation_list = manifest.getAnnotations(line)
    assert (1==len(annotation_list))
    for annotation in annotation_list:
      assert "cat" == annotation.get_name()
      assert "modelarts/image_classification" == annotation.get_type()
      assert "2019-06-28 16:07:52" == annotation.get_creation_time()
      assert "human/ei_modelarts_y00218826_01/ei_modelarts_y00218826_01" == annotation.get_annotated_by()


if __name__ == '__main__':
  # If user want to test OBS, please input OBS path, ak, sk and endpoint.
  main(sys.argv)
  print("Success")
