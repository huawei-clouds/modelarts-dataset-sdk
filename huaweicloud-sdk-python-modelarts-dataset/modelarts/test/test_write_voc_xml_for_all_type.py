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
    size_list = [640, 480, 3]
    file_name = "000000115967.jpg"
    voc_object_tags = ["car", "car", "DottedLine", "DottedLine", "line", "trafficlight", "trafficlight", "greenLight",
                       "redPoint", "redPoint", "redPoint"]
    voc_object_properties = [{"pose": "0", "truncated": "0", "difficult": "0", "confidence": "0.8",
                              "@modelarts:shape": "polygon", "@modelarts:feature": [[0, 292], [12, 279],
                                                                                    [102, 280], [122, 294],
                                                                                    [134, 326], [137, 321],
                                                                                    [138, 312], [148, 314],
                                                                                    [152, 319], [150, 326],
                                                                                    [136, 331], [149, 349],
                                                                                    [147, 414], [128, 422],
                                                                                    [111, 422], [104, 403],
                                                                                    [26, 403], [24, 415],
                                                                                    [7, 419], [0, 424],
                                                                                    [1, 413]]},
                             {"pose": "0", "truncated": "0", "difficult": "0", "confidence": "0.8",
                              "@modelarts:shape": "polygon", "@modelarts:feature": [[259, 340], [258, 328],
                                                                                    [263, 322], [278, 322],
                                                                                    [281, 329], [282, 333],
                                                                                    [282, 341], [280, 342]]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "dashed", "@modelarts:feature": [[474, 456], [370, 339]]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "dashed", "@modelarts:feature": [[160, 445], [195, 368]]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "line", "@modelarts:feature": [[154, 449], [466, 457]]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[374, 209], [405, 224]]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[255, 205], [293, 220]]},
                             {"pose": "0", "truncated": "0", "difficult": "1",
                              "@modelarts:shape": "circle", "@modelarts:feature": [61, 204, 5]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "point", "@modelarts:feature": [259, 282]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "point", "@modelarts:feature": [226, 281]},
                             {"pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "point", "@modelarts:feature": [259, 290]}]
    voc_objects = []
    for i in range(len(voc_object_tags)):
        object_tag = voc_object_tags[i]
        object_properties = voc_object_properties[i]
        voc_objects.append(VocObject(name=object_tag, properties=object_properties))

    return PascalVocIO(file_name=file_name, width=size_list[0], height=size_list[1], depth=size_list[2],
                       voc_objects=voc_objects)


def main(argv):
    path = os.path.abspath('../../../') + "/resources/voc/000000115967_1556247179208_2.xml"
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
