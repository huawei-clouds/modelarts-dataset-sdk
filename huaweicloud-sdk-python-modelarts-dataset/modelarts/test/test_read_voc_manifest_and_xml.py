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
from modelarts import manifest, field_name
from modelarts.test import test_read_voc_xml_local, test_read_voc_xml_for_all_type


def check_data(sample_list):
    assert len(sample_list) == 8
    for raw_data, label_list in sample_list:
        assert str(raw_data).startswith("s3://obs-ma/test/label-0220/datafiles")
        assert len(label_list) > 0


def test_multi_default(path, *args):
    sample_list, label_type = manifest.get_sample_list(path, "object_detection", False, *args)
    assert (label_type == field_name.multi_lable)
    check_data(sample_list)
    print("Success: test_multi_default")


def test_multi_exactly_match_type(path, *args):
    sample_list, label_type = manifest.get_sample_list(path, "modelarts/object_detection", True, *args)
    assert (label_type == field_name.multi_lable)
    check_data(sample_list)
    print("Success: test_multi_exactly_match_type")


def validate_single_annotation_sample(sample):
    source = sample.get_source()
    assert source.startswith("s3://obs-ma/test/label-0220/datafiles")
    assert sample.get_usage() == "TRAIN"
    assert len(sample.get_annotations()) == 1
    annotation = sample.get_annotations()[0]
    assert annotation.get_annotation_format() == "PASCAL VOC"
    assert annotation.get_annotated_by() == "human"
    assert annotation.get_type() == "modelarts/object_detection"
    assert annotation.get_loc() == "./voc/2007_000027.xml"


def main(argv):
    if len(argv) < 2:
        path_base = os.path.abspath('../../../resources')
        path1 = path_base + "/detect-multi-local-voc.manifest"
        test_multi_default(path1)
        test_multi_exactly_match_type(path1)

        dataset = manifest.parse_manifest(path1)
        sample_num = dataset.get_size()
        for i in range(sample_num):
            sample = dataset.get_sample_list()[i]
            print("sample ", i)
            if len(sample.get_annotations()) == 1:
                validate_single_annotation_sample(sample)
                annotation = sample.get_annotations()[0]
                if "2007_000027" in annotation.get_loc():
                    path2 = path_base + annotation.get_loc()[1:]
                    pascal_voc = PascalVocIO.parse_xml(path2)
                    test_read_voc_xml_local.validate_uk(pascal_voc)
                    print("test read VOC xml from UK: Success")
                if "000000089955_1556180702627" in annotation.get_loc():
                    path2 = path_base + annotation.get_loc()[1:]
                    pascal_voc = PascalVocIO.parse_xml(path2)
                    test_read_voc_xml_local.validate(pascal_voc)
                    print("test read VOC xml: Success")
            else:
                annotations = sample.get_annotations()
                for annotation in annotations:
                    assert annotation.get_annotation_format() == "PASCAL VOC"
                    assert annotation.get_annotated_by() == "human"
                    assert annotation.get_type() == "modelarts/object_detection"
                    if "2007_000027" in annotation.get_loc():
                        path2 = path_base + annotation.get_loc()[1:]
                        pascal_voc = PascalVocIO.parse_xml(path2)
                        test_read_voc_xml_local.validate_uk(pascal_voc)
                        print("test read VOC xml from UK: Success")
                    if "000000089955_1556180702627" in annotation.get_loc():
                        path2 = path_base + annotation.get_loc()[1:]
                        pascal_voc = PascalVocIO.parse_xml(path2)
                        test_read_voc_xml_local.validate(pascal_voc)
                        print("test read VOC xml: Success")
        print("test local Success")
    elif len(argv) < 3:
        path1 = argv[1]
        path_base = path1[:path1.rfind('/')]
        test_multi_default(path1)
        test_multi_exactly_match_type(path1)

        dataset = manifest.parse_manifest(path1)
        sample_num = dataset.get_size()
        for i in range(sample_num):
            sample = dataset.get_sample_list()[i]
            print("sample ", i)
            if len(sample.get_annotations()) == 1:
                validate_single_annotation_sample(sample)
                annotation = sample.get_annotations()[0]
                if "2007_000027" in annotation.get_loc():
                    path2 = path_base + annotation.get_loc()[1:]
                    pascal_voc = PascalVocIO.parse_xml(path2)
                    test_read_voc_xml_local.validate_uk(pascal_voc)
                    print("test read VOC xml from UK: Success")
                if "000000089955_1556180702627" in annotation.get_loc():
                    path2 = path_base + annotation.get_loc()[1:]
                    pascal_voc = PascalVocIO.parse_xml(path2)
                    test_read_voc_xml_local.validate(pascal_voc)
                    print("test read VOC xml: Success")
            else:
                annotations = sample.get_annotations()
                for annotation in annotations:
                    assert annotation.get_annotation_format() == "PASCAL VOC"
                    assert annotation.get_annotated_by() == "human"
                    assert annotation.get_type() == "modelarts/object_detection"
                    if "2007_000027" in annotation.get_loc():
                        path2 = path_base + annotation.get_loc()[1:]
                        pascal_voc = PascalVocIO.parse_xml(path2)
                        test_read_voc_xml_local.validate_uk(pascal_voc)
                        print("test read VOC xml from UK: Success")
                    if "000000089955_1556180702627" in annotation.get_loc():
                        path2 = path_base + annotation.get_loc()[1:]
                        pascal_voc = PascalVocIO.parse_xml(path2)
                        test_read_voc_xml_local.validate(pascal_voc)
                        print("test read VOC xml: Success")
    else:
        path_base = "s3://obs-dataset-sjy"
        path1 = path_base + "/detect-multi-local-voc.manifest"
        ak = argv[1]
        sk = argv[2]
        endpoint = argv[3]
        test_multi_default(path1, ak, sk, endpoint)
        test_multi_exactly_match_type(path1, ak, sk, endpoint)

        dataset = manifest.parse_manifest(path1, ak, sk, endpoint)
        sample_num = dataset.get_size()
        for i in range(sample_num):
            sample = dataset.get_sample_list()[i]
            print("sample ", i)
            if len(sample.get_annotations()) == 1:
                validate_single_annotation_sample(sample)
                annotation = sample.get_annotations()[0]
                if "2007_000027" in annotation.get_loc():
                    path2 = path_base + annotation.get_loc()[1:]
                    pascal_voc = PascalVocIO.parse_xml(path2, ak, sk, endpoint)
                    test_read_voc_xml_local.validate_uk(pascal_voc)
                    print("test read VOC xml from UK: Success")
                if "000000089955_1556180702627" in annotation.get_loc():
                    path2 = path_base + annotation.get_loc()[1:]
                    pascal_voc = PascalVocIO.parse_xml(path2, ak, sk, endpoint)
                    test_read_voc_xml_local.validate(pascal_voc)
                    print("test read VOC xml: Success")
            else:
                annotations = sample.get_annotations()
                for annotation in annotations:
                    assert annotation.get_annotation_format() == "PASCAL VOC"
                    assert annotation.get_annotated_by() == "human"
                    assert annotation.get_type() == "modelarts/object_detection"
                    if "2007_000027" in annotation.get_loc():
                        path2 = path_base + annotation.get_loc()[1:]
                        pascal_voc = PascalVocIO.parse_xml(path2, ak, sk, endpoint)
                        test_read_voc_xml_local.validate_uk(pascal_voc)
                        print("test read VOC xml from UK: Success")
                    if "000000089955_1556180702627" in annotation.get_loc():
                        path2 = path_base + annotation.get_loc()[1:]
                        pascal_voc = PascalVocIO.parse_xml(path2, ak, sk, endpoint)
                        test_read_voc_xml_local.validate(pascal_voc)
                        print("test read VOC xml: Success")
        print("test OBS Success")


if __name__ == '__main__':
    # If user want to test OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)
    print("Success")
