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


def test_read_voc_xml_with_empty_folder():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/2007_000027_folder_error.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, folder can't be empty in VOC file!; The file is" in str(e)) is True


def test_read_voc_xml_with_empty_file_name():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/2007_000027_filename_error.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, filename can't be empty in VOC file!; The file is" in str(e)) is True


def test_read_voc_xml_with_empty_annotation():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/emptyAnnotation.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
        assert True
    except Exception as e:
        print(e)
        assert False


def test_read_voc_xml_with_empty_xml():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/empty.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, no element found" in str(e)) is True


def test_read_voc_xml_with_empty_source():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/2007_000027_source_database_empty.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
        assert True
    except Exception as e:
        print(e)
        assert False


def test_read_voc_xml_with_empty_width():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/2007_000027_width_empty.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, size width can't be empty in VOC file!; The file is" in str(e)) is True


def test_read_voc_xml_with_empty_segmented():
    path = os.path.abspath('../../../') + "/resources/voc/errorFiles/2007_000027_segmented_empty.xml"
    try:
        pascal_voc = PascalVocIO.parse_xml(path)
    except Exception as e:
        print(e)
        assert ("Can't parse the XML file, segmented can't be empty in VOC file!; The file is" in str(e)) is True


if __name__ == '__main__':
    test_read_voc_xml_with_empty_folder()
    print("Test empty folder: Success")

    test_read_voc_xml_with_empty_file_name()
    print("Test empty filename: Success")

    test_read_voc_xml_with_empty_annotation()
    print("Test empty annotation: Success")

    test_read_voc_xml_with_empty_xml()
    print("Test empty xml: Success")

    test_read_voc_xml_with_empty_source()
    print("Test empty source: Success")

    test_read_voc_xml_with_empty_width()
    print("Test empty width: Success")

    test_read_voc_xml_with_empty_segmented()
    print("Test empty segmented: Success")

