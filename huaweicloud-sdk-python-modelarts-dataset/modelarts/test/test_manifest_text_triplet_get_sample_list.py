# -*- coding: utf-8 -*-
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

from modelarts import manifest, field_name
from modelarts.field_name import text_entity, text_triplet, label_separator


import sys

def check_data_and_triplet_label(sample_list):
  assert len(sample_list) == 1
  for raw_data, label_list in sample_list:
    assert "is from" in str(raw_data)
    assert 1 == len(label_list)
    label, triplet_id, from_id, to_id = str.split(label_list[0], label_separator)
    assert "name" in label
    assert triplet_id == "R1"
    assert from_id == "E1"
    assert to_id == "E2"


def check_data_and_entity_label(sample_list):
  assert len(sample_list) == 1
  for raw_data, label_list in sample_list:
    assert "is from" in str(raw_data)
    assert 2 == len(label_list)
    for label in label_list:
      label_name, start_index, end_index = str.split(label, label_separator)
      if label_name == "name1":
        assert int(start_index) == 0
        assert int(end_index) == 4
      if label_name == "name2":
        assert int(start_index) == 6
        assert int(end_index) == 10


def test_single_default(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, text_triplet, False, *args)
  assert (label_type == field_name.multi_lable)
  check_data_and_triplet_label(sample_list)
  sample_list, label_type = manifest.get_sample_list(path, text_entity, False, *args)
  assert (label_type == field_name.multi_lable)
  check_data_and_entity_label(sample_list)
  print("Success: test_single_default")


def test_single_exactly_match_type(path, *args):
  sample_list, label_type = manifest.get_sample_list(path, "modelarts/" + text_triplet, True, *args)
  assert (label_type == field_name.multi_lable)
  check_data_and_triplet_label(sample_list)
  sample_list, label_type = manifest.get_sample_list(path, "modelarts/" + text_entity, True, *args)
  assert (label_type == field_name.multi_lable)
  check_data_and_entity_label(sample_list)
  print("Success: test_single_exactly_match_type")


def main(argv):
  if len(argv) < 2:
    path1 = os.path.abspath("../../../") + "/resources/text_triplet.manifest"
    test_single_default(path1)
    test_single_exactly_match_type(path1)
    print("test local Success")

  else:
    path1 = "s3://carbonsouth/manifest/text_entity.manifest"

    ak = argv[1]
    sk = argv[2]
    endpoint = argv[3]
    test_single_default(path1, ak, sk, endpoint)
    test_single_exactly_match_type(path1, ak, sk, endpoint)
    print("test OBS Success")


if __name__ == '__main__':
  # If user want to test OBS, please input ak, sk and endpoint.
  main(sys.argv)
  print("Success")
