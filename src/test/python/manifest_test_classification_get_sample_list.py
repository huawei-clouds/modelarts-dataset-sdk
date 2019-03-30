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

import field_name
import manifest


def check_data(sample_list):
  assert sample_list.__len__() == 19
  for raw_data, label_list in sample_list:
    assert str(raw_data).startswith("s3://obs-ma/test/flowers/datafiles")
    assert label_list.__len__() > 0


def check_data_without_label(sample_list):
  assert sample_list.__len__() == 19
  for raw_data, label_list in sample_list:
    assert str(raw_data).startswith("s3://obs-ma/test/flowers/datafiles")
    assert label_list.__len__() == 0


def test_single_default(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "image_classification", False, *args)
  assert (label_type == field_name.single_lable)
  check_data(sample_list)
  print("Success: test_single_default")


def test_multi_default(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "image_classification", False, *args)
  assert (label_type == field_name.multi_lable)
  check_data(sample_list)
  print("Success: test_multi_default")


def test_single_exactly_match_type(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "modelarts/image_classification", True, *args)
  assert (label_type == field_name.single_lable)
  check_data(sample_list)
  print("Success: test_single_exactly_match_type")


def test_multi_exactly_match_type(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "modelarts/image_classification", True, *args)
  assert (label_type == field_name.multi_lable)
  check_data(sample_list)
  print("Success: test_multi_exactly_match_type")


def test_multi_exactly_match_type_error(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "modelarts/object_detection", True, *args)
  assert (label_type == field_name.multi_lable)
  check_data_without_label(sample_list)
  print("Success: test_multi_exactly_match_type_error")


def main(argv):
  if argv.__len__() < 2:
    path1 = os.path.abspath("../") + "/resources/flowers-xy-V201902220937263726.manifest"
    path2 = os.path.abspath("../") + "/resources/classification-multi-xy-V201902220937263726.manifest"
    test_single_default(path1)

    test_multi_default(path2)

    test_single_exactly_match_type(path1)

    test_multi_exactly_match_type(path2)
    print("test local Success")
  else:
    path1 = "s3://carbonsouth/manifest/flowers-xy-V201902220937263726.manifest"
    path2 = "s3://carbonsouth/manifest/classification-multi-xy-V201902220937263726.manifest"

    ak = argv[1]
    sk = argv[2]
    endpoint = argv[3]
    test_single_default(path1, ak, sk, endpoint)

    test_multi_default(path2, ak, sk, endpoint)

    test_single_exactly_match_type(path1, ak, sk, endpoint)

    test_multi_exactly_match_type(path2, ak, sk, endpoint)
    print("test OBS Success")


if __name__ == '__main__':
  # If user want to test OBS, please input ak, sk and endpoint.
  main(sys.argv)
  print("Success")
