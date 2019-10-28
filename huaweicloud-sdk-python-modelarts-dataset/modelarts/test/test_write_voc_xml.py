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
    size_list = [640, 321, 3]
    file_name = "000000089955.jpg"
    voc_object_tags = ["trafficlight", "trafficlight"]
    voc_object_properties = [{"@modelarts:color": "#FFFFF0", "@modelarts:shortcut": "C",
                              "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[347, 186], [382, 249]]},
                             {"@modelarts:color": "#FFFFE0", "@modelarts:shortcut": "D",
                              "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[544, 50], [591, 149]]}]
    voc_objects = []
    for i in range(len(voc_object_tags)):
        object_tag = voc_object_tags[i]
        object_properties = voc_object_properties[i]
        voc_objects.append(VocObject(name=object_tag, properties=object_properties))

    return PascalVocIO(file_name=file_name, width=size_list[0], height=size_list[1], depth=size_list[2],
                       voc_objects=voc_objects)


def main(argv):
    path = os.path.abspath('../../../') + "/resources/voc/000000089955_1556180702627_2.xml"
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
