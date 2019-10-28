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


def read_more_than_one_position_in_voc():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/twoPosition.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, The number of object position type can't more than one "
              "in one object or one part, there are bndbox and polygon in the object; The file is" in str(e)) is True


def read_more_than_one_position_in_voc_part():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/twoPosition_2007_000027.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, The number of object position type can't more than one "
              "in one object or one part, there are bndbox and circle in the object; The file is" in str(e)) is True


if __name__ == '__main__':
    read_more_than_one_position_in_voc()
    print("Success")
    read_more_than_one_position_in_voc_part()
    print("Success")
