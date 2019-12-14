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


def validate_python(pascal_voc):
    assert pascal_voc.get_folder() == "tests"
    assert pascal_voc.get_file_name() == "test"
    assert pascal_voc.get_source().get_database() == "Unknown"
    assert pascal_voc.get_width() == 512
    assert pascal_voc.get_height() == 512
    assert pascal_voc.get_depth() == 1
    assert pascal_voc.get_segmented() == "0"

    voc_objects = pascal_voc.get_voc_objects()
    assert len(voc_objects) == 2
    for voc_object in voc_objects:
        if voc_object.get_name() == "person":
            assert voc_object.get_pose() == "Unspecified"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "1"
            assert voc_object.get_occluded() is None
            assert voc_object.get_position().get_type() == PositionType.BNDBOX.value
            position = voc_object.get_position()
            assert position.get_x_min() == 60
            assert position.get_y_min() == 40
            assert position.get_x_max() == 430
            assert position.get_y_max() == 504
        elif voc_object.get_name() == "face":
            assert voc_object.get_pose() == "Unspecified"
            assert voc_object.get_truncated() == "0"
            assert voc_object.get_difficult() == "1"
            assert voc_object.get_occluded() is None
            assert voc_object.get_position().get_type() == PositionType.BNDBOX.value
            position = voc_object.get_position()
            assert position.get_x_min() == 113
            assert position.get_y_min() == 40
            assert position.get_x_max() == 450
            assert position.get_y_max() == 403


def validate_uk(pascal_voc):
    assert pascal_voc.get_folder() == "VOC2012"
    assert pascal_voc.get_file_name() == "2007_000027.jpg"
    assert pascal_voc.get_source().get_database() == "The VOC2007 Database"
    assert pascal_voc.get_source().get_annotation() == "PASCAL VOC2007"
    assert pascal_voc.get_source().get_image() == "flickr"
    assert pascal_voc.get_width() == 486
    assert pascal_voc.get_height() == 500
    assert pascal_voc.get_depth() == 3
    assert pascal_voc.get_segmented() == "0"

    voc_objects = pascal_voc.get_voc_objects()
    assert len(voc_objects) == 1
    for voc_object in voc_objects:
        assert voc_object.get_name() == "person"
        assert voc_object.get_pose() == "Unspecified"
        assert voc_object.get_difficult() == "0"
        assert voc_object.get_occluded() is None
        assert voc_object.get_truncated() == "0"
        assert voc_object.get_confidence() == "0.8"
        assert voc_object.get_position().get_type() == PositionType.BNDBOX.value
        position = voc_object.get_position()
        if position.get_x_min() == 174:
            assert position.get_y_min() == 101
            assert position.get_x_max() == 349
            assert position.get_y_max() == 351
        else:
            assert False
        parts = voc_object.get_parts()
        assert len(parts) == 4
        for part in parts:
            if part.get_name() == "head":
                assert part.get_confidence() == "0.8"
                assert part.get_position().get_type() == PositionType.BNDBOX.value
                assert part.get_position().get_x_min() == 169
                assert part.get_position().get_y_min() == 104
                assert part.get_position().get_x_max() == 209
                assert part.get_position().get_y_max() == 146
            elif part.get_name() == "hand":
                assert part.get_confidence() is None
                assert part.get_position().get_type() == PositionType.BNDBOX.value
                assert part.get_position().get_x_min() == 278
                assert part.get_position().get_y_min() == 210
                assert part.get_position().get_x_max() == 297
                assert part.get_position().get_y_max() == 233
            elif part.get_name() == "foot":
                if part.get_position().get_x_min() == 273:
                    assert part.get_position().get_y_min() == 333
                    assert part.get_position().get_x_max() == 297
                    assert part.get_position().get_y_max() == 354
                elif part.get_position().get_x_min() == 319:
                    assert part.get_position().get_y_min() == 307
                    assert part.get_position().get_x_max() == 340
                    assert part.get_position().get_y_max() == 326


def main(argv):
    path = os.path.abspath('../../../') + "/resources/voc/000000089955_1556180702627.xml"
    pascal_voc = PascalVocIO.parse_xml(path)
    validate(pascal_voc)
    print("test read VOC xml from cloud: Success")

    path = os.path.abspath('../../../') + "/resources/voc/test.xml"
    pascal_voc = PascalVocIO.parse_xml(path)
    validate_python(pascal_voc)
    print("test read VOC xml from python: Success")

    path = os.path.abspath('../../../') + "/resources/voc/2007_000027.xml"
    pascal_voc = PascalVocIO.parse_xml(path)
    validate_uk(pascal_voc)
    print("test read VOC xml from UK: Success")


if __name__ == '__main__':
    # If user want to test OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)
    print("Success")