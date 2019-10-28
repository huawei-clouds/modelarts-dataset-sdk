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
from modelarts.voc_position import PositionType, Polyline, Point


def validate_voc_label_properties(pascal_voc):
    assert pascal_voc.get_folder() == "Images"
    assert pascal_voc.get_file_name() == "000000484951.jpg"
    assert pascal_voc.get_source().get_database() == "Unknown"
    assert pascal_voc.get_width() == 640
    assert pascal_voc.get_height() == 426
    assert pascal_voc.get_depth() == 3
    assert pascal_voc.get_segmented() == "0"

    voc_objects = pascal_voc.get_voc_objects()
    assert len(voc_objects) == 6
    for voc_object in voc_objects:
        if voc_object.get_name() == "labelProperties":
            properties = voc_object.get_properties()
            assert len(properties) == 5
            assert properties.get("color") == "green"
            assert properties.get("属性") == "绿色"
            assert properties.get("color2") == "blue"
            assert properties.get("颜色2") == "蓝色"
            assert properties.get("property") == "value"

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
            assert isinstance(voc_object.get_position(), Polyline) is True
            assert voc_object.get_position().get_type() == PositionType.POLYLINE.value
            points = voc_object.get_position().get_points()
            points_except_result = [Point(202, 293, "x1", "y1"), Point(199, 308, "x2", "y2"),
                                    Point(211, 319, "x3", "y3"), Point(230, 311, "x4", "y4"),
                                    Point(226, 293, "x5", "y5"), Point(210, 287, "x6", "y6"),
                                    Point(202, 293, "x7", "y7")]
            # TODO: compare python object
            for point_num in range(len(points)):
                assert (points[point_num].__dict__ == points_except_result[point_num].__dict__) is True

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

        elif voc_object.get_name() == "labelProperties4":
            properties = voc_object.get_properties()
            assert len(properties) == 1
            assert properties.get("color2") == "blue"
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
    path = os.path.abspath('../../../') + "/resources/voc/polyline.xml"
    pascal_voc = PascalVocIO.parse_xml(path)
    validate_voc_label_properties(pascal_voc)


if __name__ == '__main__':
    # If user want to test OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)
    print("Success")