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

from modelarts import manifest
from modelarts.field_name import CARBON
from obs import ObsClient


def test_single_default(path, obsClient):
  if obsClient is None:
    sources = manifest.getSources(path, CARBON)
  else:
    sources = manifest.getSources(path, CARBON, obsClient)
  assert 3 == len(sources)
  for source in sources:
    print(source)
  print("Success: test_single_default")


def main(argv):
  if len(argv) < 2:
    path1 = os.path.abspath("../../../") + "/resources/binary1557487619292.manifest"
    test_single_default(path1)

    print("test local Success")
  else:
    path1 = "s3a://manifest/carbon/manifestcarbon/obsbinary1557717977531.manifest"
    ak = argv[1]
    sk = argv[2]
    endpoint = argv[3]
    obsClient = ObsClient(access_key_id=ak, secret_access_key=sk, server=endpoint)
    test_single_default(path1, obsClient)

    print("test OBS Success")


if __name__ == '__main__':
  # If user want to test OBS, please input ak, sk and endpoint.
  main(sys.argv)
  print("Success")
