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

from modelarts.test import test_read_voc_manifest_and_xml, test_write_voc_xml
from modelarts.manifest import Annotation, Sample, DataSet
from modelarts.pascal_voc import PascalVocIO


def create_manifest(path_base, ak=None, sk=None, endpoint=None):
    size = 0
    sample_list = []
    for i in range(8):
        size = size + 1
        source = "s3://obs-ma/test/label-0220/datafiles/1 (15)_1550632618199" + str(i) + ".jpg"
        usage = "TRAIN"
        inference_loc = "s3://obs-ma/test/label-0220/datafiles/1 (15)_1550632618199" + str(i) + ".txt"
        annotations_list = []
        print("sample ", i)

        for j in range(2):
            annotation_type = "modelarts/object_detection"
            annotation_loc = "/000000089955_1556180702627_" + str(i) + str(j) + ".xml"
            annotation_creation_time = "2019-02-20 03:16:58"
            annotation_format = "PASCAL VOC"
            annotated_by = "human"
            annotations_list.append(
                Annotation(type=annotation_type, loc="." + annotation_loc,
                           creation_time=annotation_creation_time,
                           annotated_by=annotated_by, annotation_format=annotation_format))
            pascal_voc = test_write_voc_xml.create_pascal_voc()
            pascal_voc.save_xml(path_base + annotation_loc, ak, sk, endpoint)
            print("test write VOC xml: Success")

        sample_list.append(
            Sample(source=source, usage=usage, annotations=annotations_list, inference_loc=inference_loc))
    return DataSet(sample=sample_list, size=size)


def main(argv):
    if len(argv) < 2:
        path_base = os.path.abspath('../../../resources')
        path = path_base + "/detect-multi-local-voc_2.manifest"
        dataset = create_manifest(path_base)
        dataset.save(path)
        print("test write local manifest: Success\n")
        para = ["test_read_voc_manifest_and_xml.py"]
        para.append(path)
        test_read_voc_manifest_and_xml.main(para)
    else:
        path2 = argv[1]
        path_base = path2[:path2.rfind('/')]
        ak = argv[2]
        sk = argv[3]
        endpoint = argv[4]
        dataset = create_manifest(path_base, ak, sk, endpoint)
        dataset.save(path2, ak, sk, endpoint)
        print("test write OBS manifest: Success\n")


if __name__ == '__main__':
    # If user want to test OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)
    print("Success")
