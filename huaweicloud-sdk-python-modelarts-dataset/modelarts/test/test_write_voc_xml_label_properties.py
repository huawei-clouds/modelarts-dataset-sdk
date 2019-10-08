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

from modelarts.test import test_read_voc_xml_from_obs
from modelarts.pascal_voc import PascalVocIO, VocObject


def create_pascal_voc():
    size_list = [640, 426, 3]
    file_name = "000000484951.jpg"
    voc_object_tags = ["labelProperties", "big", "labelProperties3", "labelProperties2", "labelProperties4", "small"]
    voc_object_properties = [{"color": "green", "属性": "绿色", "property": "value", "color2": "blue", "颜色2": "蓝色",
                              "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[199, 356], [230, 385]]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[248, 59], [638, 292]]},
                             {"颜色": "红色", "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "polygon", "@modelarts:feature": [[202, 293], [199, 308],
                                                                                    [211, 319], [230, 311],
                                                                                    [226, 293], [210, 287],
                                                                                    [202, 293]]},
                             {"color": "yellow", "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[197, 324], [227, 348]]},
                             {"color2": "blue", "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[197, 324], [227, 348]]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[317, 104], [461, 222]]}]
    voc_objects = []
    for i in range(len(voc_object_tags)):
        object_tag = voc_object_tags[i]
        object_properties = voc_object_properties[i]
        voc_objects.append(VocObject(name=object_tag, properties=object_properties))

    return PascalVocIO(file_name=file_name, width=size_list[0], height=size_list[1], depth=size_list[2],
                       voc_objects=voc_objects)


def main(argv):
    path = os.path.abspath('../../../') + "/resources/voc/labelProperties_2.xml"
    pascal_voc = create_pascal_voc()
    if len(argv) < 2:
        pascal_voc.save_xml(path)
        print("test write local xml: Success")
        para = []
        para.append(path)
        test_read_voc_xml_from_obs.main(para)
    else:
        path2 = argv[1]
        ak = argv[2]
        sk = argv[3]
        endpoint = argv[4]
        pascal_voc.save_xml(path2, ak, sk, endpoint)
        print("test write OBS xml: Success")


if __name__ == '__main__':
    # If user want to test OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)
    print("Success")
