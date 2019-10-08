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

from modelarts.pascal_voc import PascalVocIO
from modelarts.voc_position import PositionType


def validate_voc_label_properties_empty(pascal_voc):
    assert pascal_voc.get_folder() == "Images"
    assert pascal_voc.get_file_name() == "000000484951.jpg"
    assert pascal_voc.get_source().get_database() == "Unknown"
    assert pascal_voc.get_width() == 640
    assert pascal_voc.get_height() == 426
    assert pascal_voc.get_depth() == 3
    assert pascal_voc.get_segmented() == "0"

    voc_objects = pascal_voc.get_voc_objects()
    assert len(voc_objects) == 5
    for voc_object in voc_objects:
        if voc_object.get_name() == "labelProperties":
            properties = voc_object.get_properties()
            assert len(properties) == 0

            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_occluded() is None
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_confidence() is None

            assert voc_object.get_position().get_type() == PositionType.BNDBOX.value
            assert voc_object.get_position().get_x_min() == 199
            assert voc_object.get_position().get_y_min() == 356
            assert voc_object.get_position().get_x_max() == 230
            assert voc_object.get_position().get_y_max() == 385

        elif voc_object.get_name() == "big":
            assert len(voc_object.get_properties()) == 0
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_occluded() is None
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_confidence() is None

            assert voc_object.get_position().get_type() == PositionType.BNDBOX.value
            assert voc_object.get_position().get_x_min() == 248
            assert voc_object.get_position().get_y_min() == 59
            assert voc_object.get_position().get_x_max() == 638
            assert voc_object.get_position().get_y_max() == 292

        elif voc_object.get_name() == "labelProperties3":
            properties = voc_object.get_properties()
            assert len(properties) == 1
            assert properties.get("颜色") == "红色"
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_occluded() is None
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_confidence() is None

        elif voc_object.get_name() == "labelProperties2":
            properties = voc_object.get_properties()
            assert len(properties) == 1
            assert properties.get("color") == "yellow"
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_occluded() is None
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_confidence() is None

        elif voc_object.get_name() == "small":
            assert len(voc_object.get_properties()) == 0
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_occluded() is None
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_confidence() is None

            assert voc_object.get_position().get_type() == PositionType.BNDBOX.value
            assert voc_object.get_position().get_x_min() == 317
            assert voc_object.get_position().get_y_min() == 104
            assert voc_object.get_position().get_x_max() == 461
            assert voc_object.get_position().get_y_max() == 222

        else:
            assert False


def main(argv):
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/labelPropertiesEmpty.xml"
    pascal_voc = PascalVocIO.parse_xml(path)
    validate_voc_label_properties_empty(pascal_voc)


if __name__ == '__main__':
    main(sys.argv)
    print("Success")