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
from modelarts.voc_position import PositionType, Point


def validate_voc_multiple_object(pascal_voc):
    assert pascal_voc.get_folder() == "Images"
    assert pascal_voc.get_file_name() == "000000115967.jpg"
    assert pascal_voc.get_source().get_database() == "Unknown"
    assert pascal_voc.get_width() == 640
    assert pascal_voc.get_height() == 480
    assert pascal_voc.get_depth() == 3
    assert pascal_voc.get_segmented() == "0"

    voc_objects = pascal_voc.get_voc_objects()
    assert len(voc_objects) == 11
    for voc_object in voc_objects:
        if voc_object.get_name() == "car":
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_occluded() is None
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_confidence() == "0.8"
            assert voc_object.get_position().get_type() == PositionType.POLYGON.value
            points = voc_object.get_position().get_points()
            points_except_result1 = [Point(0, 292, "x1", "y1"), Point(12, 279, "x2", "y2"),
                                     Point(102, 280, "x3", "y3"), Point(122, 294, "x4", "y4"),
                                     Point(134, 326, "x5", "y5"), Point(137, 321, "x6", "y6"),
                                     Point(138, 312, "x7", "y7"), Point(148, 314, "x8", "y8"),
                                     Point(152, 319, "x9", "y9"), Point(150, 326, "x10", "y10"),
                                     Point(136, 331, "x11", "y11"), Point(149, 349, "x12", "y12"),
                                     Point(147, 414, "x13", "y13"), Point(128, 422, "x14", "y14"),
                                     Point(111, 422, "x15", "y15"), Point(104, 403, "x16", "y16"),
                                     Point(26, 403, "x17", "y17"), Point(24, 415, "x18", "y18"),
                                     Point(7, 419, "x19", "y19"), Point(0, 424, "x20", "y20"),
                                     Point(1, 413, "x21", "y21")]
            points_except_result2 = [Point(259, 340, "x1", "y1"), Point(258, 328, "x2", "y2"),
                                     Point(263, 322, "x3", "y3"), Point(278, 322, "x4", "y4"),
                                     Point(281, 329, "x5", "y5"), Point(282, 333, "x6", "y6"),
                                     Point(282, 341, "x7", "y7"), Point(280, 342, "x8", "y8")]
            # TODO: compare python object
            if points[0].__dict__ == points_except_result1[0].__dict__:
                for point_num in range(len(points)):
                    assert (points[point_num].__dict__ == points_except_result1[point_num].__dict__) is True
            elif points[0].__dict__ == points_except_result2[0].__dict__:
                for point_num in range(len(points)):
                    assert (points[point_num].__dict__ == points_except_result2[point_num].__dict__) is True
            else:
                assert False
        elif voc_object.get_name() == "DottedLine":
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_occluded() is None
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_position().get_type() == PositionType.DASHED.value
            dashed = voc_object.get_position()
            if dashed.get_x1() == 474:
                assert dashed.get_y1() == 456
                assert dashed.get_x2() == 370
                assert dashed.get_y2() == 339
            elif dashed.get_x1() == 160:
                assert dashed.get_y1() == 445
                assert dashed.get_x2() == 195
                assert dashed.get_y2() == 368
            else:
                assert False
        elif voc_object.get_name() == "line":
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_occluded() is None
            assert voc_object.get_position().get_type() == PositionType.LINE.value
            line = voc_object.get_position()
            if line.get_x1() == 154:
                assert line.get_y1() == 449
                assert line.get_x2() == 466
                assert line.get_y2() == 457
            else:
                assert False
        elif voc_object.get_name() == "trafficlight":
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_occluded() is None
            assert voc_object.get_position().get_type() == PositionType.BNDBOX.value
            bndbox = voc_object.get_position()
            if bndbox.get_x_min() == 255:
                assert bndbox.get_y_min() == 205
                assert bndbox.get_x_max() == 293
                assert bndbox.get_y_max() == 220
            elif bndbox.get_x_min() == 374:
                assert bndbox.get_y_min() == 209
                assert bndbox.get_x_max() == 405
                assert bndbox.get_y_max() == 224
            else:
                assert False
        elif voc_object.get_name() == "greenLight":
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "1"
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_occluded() is None
            assert voc_object.get_position().get_type() == PositionType.CIRCLE.value
            circle = voc_object.get_position()
            if circle.get_cx() == 61:
                assert circle.get_cy() == 204
                assert circle.get_r() == 5
            else:
                assert False
        elif voc_object.get_name() == "redPoint":
            assert voc_object.get_pose() == "0"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "0"
            assert voc_object.get_difficult_coefficient() is None
            assert voc_object.get_occluded() is None
            assert voc_object.get_position().get_type() == PositionType.POINT.value
            point = voc_object.get_position()
            if point.get_x_value() == 259:
                assert point.get_y_value() == 282 or point.get_y_value() == 290
            elif point.get_x_value() == 226:
                assert point.get_y_value() == 281
            else:
                assert False
        else:
            assert False


def main(argv):
    path = os.path.abspath('../../../') + "/resources/voc/000000115967_1556247179208.xml"
    pascal_voc = PascalVocIO.parse_xml(path)
    validate_voc_multiple_object(pascal_voc)

if __name__ == '__main__':
    # If user want to test OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)
    print("Success")