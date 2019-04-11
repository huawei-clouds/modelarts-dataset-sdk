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

from modelarts import manifest, field_name


def check_data(sample_list):
  assert len(sample_list) == 8
  for raw_data, label_list in sample_list:
    assert str(raw_data).startswith("s3://obs-ma/test/label-0220/datafiles")
    assert len(label_list) > 0


def test_single_default(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "object_detection", False, *args)
  assert (label_type == field_name.single_lable)
  check_data(sample_list)
  print("Success: test_single_default")


def test_multi_default(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "object_detection", False, *args)
  assert (label_type == field_name.multi_lable)
  check_data(sample_list)
  print("Success: test_multi_default")


def test_single_exactly_match_type(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "modelarts/object_detection", True, *args)
  assert (label_type == field_name.single_lable)
  check_data(sample_list)
  print("Success: test_single_exactly_match_type")


def test_multi_exactly_match_type(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "modelarts/object_detection", True, *args)
  assert (label_type == field_name.multi_lable)
  check_data(sample_list)
  print("Success: test_multi_exactly_match_type")


def test_single_default_from_obs(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "object_detection", False, *args)
  assert (label_type == field_name.single_lable)
  check_data(sample_list)
  print("Success: test_single_default_from_obs")


def main(argv):
  if len(argv) < 2:
    path1 = os.path.abspath("../../../") + "/resources/detect-test-xy-V201902220951335133.manifest"
    path2 = os.path.abspath("../../../") + "/resources/detect-multi-xy-V201902220951335133.manifest"
    test_single_default(path1)

    test_multi_default(path2)

    test_single_exactly_match_type(path1)

    test_multi_exactly_match_type(path2)
    print("test local Success")
  else:
    path1 = "s3://carbonsouth/manifest/detect-test-xy-V201902220951335133.manifest"
    path2 = "s3://carbonsouth/manifest/detect-multi-xy-V201902220951335133.manifest"

    ak = argv[1]
    sk = argv[2]
    endpoint = argv[3]
    test_single_default(path1, ak, sk, endpoint)

    test_multi_default(path2, ak, sk, endpoint)

    test_single_exactly_match_type(path1, ak, sk, endpoint)

    test_multi_exactly_match_type(path2, ak, sk, endpoint)
    test_single_default_from_obs(path1, ak, sk, endpoint)
    print("test OBS Success")


if __name__ == '__main__':
  # If user want to test OBS, please input ak, sk and endpoint.
  main(sys.argv)
  print("Success")
