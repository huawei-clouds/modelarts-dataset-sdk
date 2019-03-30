import os
import sys

import manifest_test_flowers
from manifest import Annotation, Sample, DataSet


def create_manifest():
  size = 0;
  sample_list = []
  for i in range(19):
    size = size + 1
    source = "s3://obs-ma/test/flowers/datafiles/1_1550650984970_" + str(i) + ".jpg"
    usage = "TRAIN"
    inference_loc = "s3://obs-ma/test/flowers/datafiles/1_1550650984970_" + str(i) + ".txt"
    annotations_list = []

    for j in range(1):
      annotation_type = "modelarts/image_classification"
      if 0 == i % 2:
        annotation_name = "Cat"
      else:
        annotation_name = "Dog"
      annotation_creation_time = "2019-02-20 08:23:06"
      annotation_format = "manifest"
      annotation_confidence = 0.8
      annotated_by = "human"
      annotations_list.append(
        Annotation(name=annotation_name, type=annotation_type,
                   confidence=annotation_confidence,
                   creation_time=annotation_creation_time,
                   annotated_by=annotated_by, annotation_format=annotation_format))
    sample_list.append(
      Sample(source=source, usage=usage, annotations=annotations_list, inference_loc=inference_loc))
  return DataSet(sample=sample_list, size=size)


def main(argv):
  path = os.path.abspath('../') + "/resources/flowers-xy-V201902220937263726_2.manifest"
  dataset = create_manifest()
  if argv.__len__() < 2:
    dataset.save(path)
    para = []
    para.append(path)
    manifest_test_flowers.main(para)
  else:
    path2 = argv[1]
    ak = argv[2]
    sk = argv[3]
    endpoint = argv[4]
    dataset.save(path2, ak, sk, endpoint)


if __name__ == '__main__':
  # If user want to test OBS, please input OBS path, ak, sk and endpoint.
  main(sys.argv)
  print("Success")
