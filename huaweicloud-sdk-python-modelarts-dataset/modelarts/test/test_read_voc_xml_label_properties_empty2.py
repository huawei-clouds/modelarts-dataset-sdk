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


def test_read_voc_xml_label_properties_empty2():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/labelPropertiesEmpty2.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, object properties 颜色 can't be empty in VOC file!; The file is" in str(e)) is True


def test_read_voc_xml_label_properties_empty3():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/labelPropertiesEmpty3.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, object properties color can't be empty in VOC file!; The file is" in str(e)) is True


if __name__ == '__main__':
    test_read_voc_xml_label_properties_empty2()
    print("Success")
    test_read_voc_xml_label_properties_empty3()
    print("Success")