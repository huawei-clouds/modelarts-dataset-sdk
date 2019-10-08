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
from modelarts import field_name
from modelarts.pascal_voc import PascalVocIO
from modelarts.voc_position import PositionType


def validate(pascal_voc):
    assert pascal_voc.get_folder() == "Images"
    assert pascal_voc.get_file_name() == "000000089955.jpg"
    assert pascal_voc.get_source().get_database() == "Unknown"
    assert pascal_voc.get_width() == 640
    assert pascal_voc.get_height() == 321
    assert pascal_voc.get_depth() == 3
    assert pascal_voc.get_segmented() == "0"

    voc_objects = pascal_voc.get_voc_objects()
    assert len(voc_objects) == 2
    for voc_object in voc_objects:
        assert voc_object.get_name() == "trafficlight"
        assert voc_object.get_pose() == "0"
        assert voc_object.get_difficult() == "0"
        assert voc_object.get_difficult_coefficient() is None
        assert voc_object.get_occluded() is None
        assert voc_object.get_truncated() == "0"
        assert voc_object.get_position().get_type() == PositionType.BNDBOX.value
        position = voc_object.get_position()
        if position.get_x_min() == 347:
            assert position.get_y_min() == 186
            assert position.get_x_max() == 382
            assert position.get_y_max() == 249
        elif position.get_x_min() == 544:
            assert position.get_y_min() == 50
            assert position.get_x_max() == 591
            assert position.get_y_max() == 149


def main(argv):
    if len(argv) < 2:
        path = os.path.abspath('../../../') + "/resources/voc/000000089955_1556180702627.xml"
        pascal_voc = PascalVocIO.parse_xml(path)
        validate(pascal_voc)
        print("test read local xml: Success")
    elif len(argv) < 3:
        path = argv[1]
        pascal_voc = PascalVocIO.parse_xml(path)
        validate(pascal_voc)
        print("test read local xml: Success")
    else:
        path = argv[1]
        ak = argv[2]
        sk = argv[3]
        endpoint = argv[4]
        pascal_voc = PascalVocIO.parse_xml(path, ak, sk, endpoint)
        validate(pascal_voc)
        print("test read OBS xml: Success")


if __name__ == '__main__':
    # If user want to test OBS, please input ak, sk and endpoint.
    main(sys.argv)
    print("Success")
