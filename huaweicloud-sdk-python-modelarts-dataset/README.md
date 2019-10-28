

Hello huaweicloud-sdk-python-modelarts-dataset

​           

----



- [VOC xml文件参数说明](#voc-xml文件参数说明)
- [Read and write VOC xml](#read-voc-xml)
- [Read and write VOC manifest](#read-voc-manifest)
- [API List for voc xml](#api-list-for-voc-xml)
- [API List for manifest](#api-list-for-manifest)



## VOC xml文件参数说明

#### 表1 VOC xml文件基本属性

| 参数                  | 是否必选 | 参数类型             | 描述                                                         |
| --------------------- | -------- | -------------------- | ------------------------------------------------------------ |
| file_name             | 是       | String               | voc标记的图像文件名称                                        |
| size_list             | 是       | List\<Int>           | voc标记的图像尺寸，列表[width, height, depth]或[width, height] |
| voc_object_tags       | 否       | List\<String>        | voc标签名列表                                                |
| voc_object_properties | 否       | List\<LabelProperty> | voc标签属性列表，见表2 VOC xml文件LabelProperty属性          |

#### 表2 VOC xml文件LabelProperty属性

| 参数                       | 是否必选 | 参数类型 | 描述                                                         |
| -------------------------- | -------- | -------- | ------------------------------------------------------------ |
| pose                       | 否       | String   | 位置描述                                                     |
| truncated                  | 否       | String   | 是否被截断                                                   |
| difficult                  | 否       | String   | 是否属于难例                                                 |
| occluded                   | 否       | String   | 是否被遮挡                                                   |
| @modelarts:shortcut        | 否       | String   | 内置属性：标签快捷键，默认为空                               |
| @modelarts:color           | 否       | String   | 内置属性：标签展示的颜色，为色彩的16进制码，默认为空         |
| @modelarts:shape           | 否       | String   | 内置属性：物体检测标签的形状，默认为空。如设置该值，取值可选：                                                                                                             **· bndbox 矩形**                                                                                          **· polygon 多边形**                                                                                    **· polyline 折线形**                                                                                     **· circle 圆形**                                                                                             **· line 直线**                                                                                                 **· dashed 虚线**                                                                                               **· point 点** |
| @modelarts:feature         | 否       | List     | 内置属性：物体检测标签的坐标，默认为空。各形状的坐标内容为：                                                                                                           · bndbox:  [[xmin, ymin], [xmax, ymax]]                                               · polygon:  List<[xi, yi]>                                                                         · polyline:  List<[xi, yi]>                                                                           · circle:  [center_x, center_y, radius]                                                      · line:  [[x1, y1], [x2, y2]]                                                                         · dashed:  [[x1, y1], [x2, y2]]                                                                     · point:  [x, y] |
| 非@modelarts开头的其他属性 | 否       | String   | 用户自定义属性                                               |



## Read VOC xml

#### Example for reading VOC xml

```python
import os
import sys

from modelarts.pascal_voc import PascalVocIO


def main(argv):
    if len(argv) < 2:
        path = os.path.abspath('../../../') + "/resources/voc/000000089955_1556180702627.xml"
        pascal_voc = PascalVocIO.parse_xml(path)
    else:
        path = argv[1]
        ak = argv[2]
        sk = argv[3]
        endpoint = argv[4]
        pascal_voc = PascalVocIO.parse_xml(path, ak, sk, endpoint)


if __name__ == '__main__':
    # If user want to read from OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)

```



## Write VOC xml

#### Example for writing VOC xml

```python
import os
import sys

from modelarts.pascal_voc import PascalVocIO, VocObject


def create_pascal_voc():
    size_list = [640, 321, 3]
    file_name = "000000089955.jpg"
    voc_object_tags = ["trafficlight", "trafficlight"]
    voc_object_properties = [{"@modelarts:color": "#FFFFF0", "@modelarts:shortcut": "C",
                              "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[347, 186], [382, 249]], "custom attribute": "value"},
                             {"@modelarts:color": "#FFFFE0", "@modelarts:shortcut": "D",
                              "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[544, 50], [591, 149]]}, "custom attribute": "value"]
    voc_objects = []
    for i in range(len(voc_object_tags)):
        object_tag = voc_object_tags[i]
        object_properties = voc_object_properties[i]
        voc_objects.append(VocObject(name=object_tag, properties=object_properties))

    return PascalVocIO(file_name=file_name, width=size_list[0], height=size_list[1], depth=size_list[2],
                       voc_objects=voc_objects)


def main(argv):
    path = os.path.abspath('../../../') + "/resources/voc/000000089955_1556180702627_2.xml"
    pascal_voc = create_pascal_voc()
    if len(argv) < 2:
        pascal_voc.save_xml(path)
    else:
        path2 = argv[1]
        ak = argv[2]
        sk = argv[3]
        endpoint = argv[4]
        pascal_voc.save_xml(path2, ak, sk, endpoint)


if __name__ == '__main__':
    # If user want to write to OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)

```



## Read VOC manifest

#### Example for reading VOC manifest

```python
import os
import sys

from modelarts.pascal_voc import PascalVocIO
from modelarts import manifest


def main(argv):
    if len(argv) < 2:
        path = os.path.abspath('../../../') + "/resources/detect-multi-local-voc.manifest"
        dataset = manifest.parse_manifest(path)
        for sample in dataset.get_sample_list():
            annotations = sample.get_annotations()
            for annotation in annotations:
            	xml_path = annotation.get_loc():
                pascal_voc = PascalVocIO.parse_xml(xml_path)
	else:
        path = argv[1]
        ak = argv[2]
        sk = argv[3]
        endpoint = argv[4]
        dataset = manifest.parse_manifest(path, ak, sk, endpoint)
        for sample in dataset.get_sample_list():
            annotations = sample.get_annotations()
            for annotation in annotations:
            	xml_path = annotation.get_loc():
                pascal_voc = PascalVocIO.parse_xml(xml_path, ak, sk, endpoint)

                
if __name__ == '__main__':
    # If user want to read from OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)

```



## Write VOC manifest

#### Example for writing VOC manifest

```python
import os
import sys

from modelarts.pascal_voc import PascalVocIO, VocObject
from modelarts.manifest import Annotation, Sample, DataSet


def create_pascal_voc():
    size_list = [640, 321, 3]
    file_name = "000000089955.jpg"
    voc_object_tags = ["trafficlight", "trafficlight"]
    voc_object_properties = [{"@modelarts:color": "#FFFFF0", "@modelarts:shortcut": "C",
                              "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[347, 186], [382, 249]], "custom attribute": "value"},
                             {"@modelarts:color": "#FFFFE0", "@modelarts:shortcut": "D",
                              "pose": "0", "truncated": "0", "difficult": "0",
                              "@modelarts:shape": "bndbox", "@modelarts:feature": [[544, 50], [591, 149]]}, "custom attribute": "value"]
    voc_objects = []
    for i in range(len(voc_object_tags)):
        object_tag = voc_object_tags[i]
        object_properties = voc_object_properties[i]
        voc_objects.append(VocObject(name=object_tag, properties=object_properties))

    return PascalVocIO(file_name=file_name, width=size_list[0], height=size_list[1], depth=size_list[2], voc_objects=voc_objects)


def create_manifest(path, ak=None, sk=None, endpoint=None):
    size = 0
    sample_list = []
    for i in range(8):
        size = size + 1
        source = "s3://obs-ma/test/label-0220/datafiles/1 (15)_1550632618199" + str(i) + ".jpg"
        usage = "TRAIN"
        inference_loc = "s3://obs-ma/test/label-0220/datafiles/1 (15)_1550632618199" + str(i) + ".txt"
        annotations_list = []

        for j in range(1):
            annotation_type = "modelarts/object_detection"
            annotation_loc = path[:path.rfind('.manifest')] + "_" + str(i) + ".xml"
            annotation_creation_time = "2019-02-20 03:16:58"
            annotation_format = "PASCAL VOC"
            annotated_by = "human"
            annotations_list.append(
                Annotation(type=annotation_type, loc="." + annotation_loc,
                           creation_time=annotation_creation_time,
                           annotated_by=annotated_by, annotation_format=annotation_format))
            pascal_voc = create_pascal_voc()
            pascal_voc.save_xml(annotation_loc, ak, sk, endpoint)

        sample_list.append(Sample(source=source, usage=usage, annotations=annotations_list, inference_loc=inference_loc))
    return DataSet(sample=sample_list, size=size)


def main(argv):
    if len(argv) < 2:
        path = os.path.abspath('../../../resources') + "/detect-multi-local-voc_2.manifest"
        dataset = create_manifest(path)
        dataset.save(path)
    else:
        path = argv[1]
        ak = argv[2]
        sk = argv[3]
        endpoint = argv[4]
		dataset = create_manifest(path, ak, sk, endpoint)
        dataset.save(path, ak, sk, endpoint)

if __name__ == '__main__':
    # If user want to write to OBS, please input OBS path, ak, sk and endpoint.
    main(sys.argv)

```



## API List for voc xml

### class modelarts.pascal_voc.PascalVocIO

```python
__init__(self, folder=None, file_name=None, source=None, width=None, height=None, depth=None, segmented=None, voc_objects=None):    
    """    
    :param folder: image folder    
    :param file_name: image name    
    :param source: include database for example The VOC2007 Database, annotation format for example PASCAL VOC2007, image source for example flickr.    
    :param width: image size    
    :param height: image size    
    :param depth: image size    
    :param segmented: whether the image is segmented   
    :param voc_objects: instance of class VocObject contains the annotation of object detection   
    """
```

```python
get_file_name(self):    
    """    
    :return: file name, mandatory field    
    """
```

```python
get_source(self):    
    """    
    :return: image source, optional field   
    """
```

```python
get_width(self):    
    """    
    :return: image width, mandatory field    
    """
```

```python
get_height(self):   
    """    
    :return: image height, mandatory field   
    """
```

```python
get_depth(self):    
    """    
    :return: image depth, mandatory field    
    """
```

```python
get_segmented(self):   
    """   
    :return: whether image is segmented, optional field   
    """
```

```python
get_voc_objects(self):   
    """   
    :return: instance list of class VocObject, mandatory field  
    """
```

```python
save_xml(self, xml_file_path, access_key=None, secret_key=None, end_point=None, save_mode='w', ssl_verify=False, max_retry_count=3, timeout=60):    
    """    
    write xml file to local or OBS   
    
    :param xml_file_path: xml file output path   
    :param access_key: access key of OBS   
    :param secret_key: secret key of OBS   
    :param end_point: end point of OBS    
    :param save_mode: write mode of saving xml, default is 'w'
    :param ssl_verify: whether use ssl, set True if user want to verify certification, otherwise set False; default is False    
    :param max_retry_count: max retry count, default is 3    
    :param timeout: timeout [10,60], default is 60  
    """
```

```python
parse_xml(cls, xml_file_path, access_key=None, secret_key=None, end_point=None, obs_client=None, ssl_verify=False, max_retry_count=3, timeout=60):    
    """    
    Read xml file from local or OBS   
    
    :param xml_file_path: xml file input path   
    :param access_key: access key of OBS   
    :param secret_key: secret key of OBS   
    :param end_point: end point of OBS   
    :param obs_client: obs client   
    :param ssl_verify: whether use ssl, set True if user want to verify certification, otherwise set False; default is False  
    :param max_retry_count: max retry count, default is 3    
    :param timeout: timeout [10,60], default is 60    
    :return: instance of PascalVocIO 
    """
```



### class modelarts.pascal_voc.VocObject

```python
__init__(self, name=None, properties=None, pose=None, truncated=None, occluded=None, difficult=None, difficult_coefficient=None, confidence=None, position=None, parts=None):
        """
        Constructor for VocObject

        :param name: object name
        :param properties: object properties
        :param pose: pose value
        :param truncated: an object marked as 'truncated' indicates that the bounding box specified for the object does not correspond to the full extent of the object. e.g. an image of a person from the waist up, or a view of a car extending outside the image. The truncated field being set to 1 indicates that the object is "truncated" in the image.
        :param occluded: an object marked as 'occluded' indicates that a significant portion of the object within the bounding box is occluded by another object. The occluded field being set to 1 indicates that the object is significantly occluded by another object. Participants are free to use or ignore this field as they see fit.
        :param difficult: an object marked as 'difficult' indicates that the object is considered difficult to recognize, for example an object which is clearly visible but unidentifiable without substantial use of context. Objects marked as difficult are currently ignored in the evaluation of the challenge. The difficult field being set to 1 indicates that the object has been annotated as "difficult", for example an object which is clearly visible but difficult to recognize without substantial use of context.
        :param difficult_coefficient: difficult coefficient describes how difficult it is to recognize the object if an object marked as 'difficult'. The difficult coefficient field being set to 1 indicates that the object is hardly to recognize.
        :param confidence: Confidence for annotation that was annotated by machine, the value: 0 <= Confidence < 1, optional field.
        :param position: position of object, witch can be point, line or others.
        :param parts: one object may have subobjects
        """
```

```python
get_name(self):    
    """    
    :return: the object name, mandatory field    
    """
```

```python
get_properties(self):    
    """    
    :return: the properties of object, mandatory field   
    """
```

```python
get_pose(self):   
    """    
    :return: the pose of object, optional field    
    """
```

```python
get_truncated(self):   
    """    
    :return: whether the object is truncated, optional field    
    """
```

```python
get_occluded(self):   
    """   
    :return: whether the object is occluded, optional field  
    """
```

```python
get_difficult(self):   
    """   
    :return: whether the object is difficult, optional field   
    """
```

```python
get_difficult_coefficient(self):  
    """   
    :return: how difficult the object is to recognize, optional field  
    """
```

```python
get_confidence(self):  
    """   
    :return: confidence value of the object annotation, optional field  
    """
```

```python
get_position(self):    
    """    
    :return: the position of object, mandatory field,
    		 return a instance of object shape class: Point, Line, Dashed, Circle, BNDBox, Polygon, Polyline
    """
```

```python
get_parts(self):   
    """   
    :return: subobjects, optional field  
    """
```

### modelarts.voc_position

#### class modelarts.voc_position.Point

```python
__init__(self, x_value=None, y_value=None, x_name='x', y_name='y')
```

```python
get_feature(self):
	"""    
    :return: [self._x_value, self._y_value] 
    """
```

```python
get_type(cls):
	"""    
    :return: "point" 
    """
```

#### class modelarts.voc_position.Line

```python
__init__(self, x1=None, y1=None, x2=None, y2=None)
```

```python
get_feature(self):
    """    
    :return: [[self._x1, self._y1], [self._x2, self._y2]]
    """
```

```python
get_type(cls):    
    """       
    :return: "line"    
    """
```

#### class modelarts.voc_position.Dashed

```python
__init__(self, x1=None, y1=None, x2=None, y2=None)
```

```python
get_feature(self):
    """    
    :return: [[self._x1, self._y1], [self._x2, self._y2]]
    """
```

```python
get_type(cls):    
    """       
    :return: "dashed"    
    """
```

#### class modelarts.voc_position.Circle

```python
__init__(self, cx=None, cy=None, r=None)
```

```python
get_feature(self):
    """    
    :return: [self._cx, self._cy, self._r]
    """
```

```python
get_type(cls):    
    """       
    :return: "circle"    
    """
```

#### class modelarts.voc_position.BNDBox

```python
__init__(self, x_min=None, y_min=None, x_max=None, y_max=None)
```

```python
get_feature(self):
    """    
    :return: [[self._x_min, self._y_min], [self._x_max, self._y_max]]
    """
```

```python
get_type(cls):    
    """       
    :return: "bndbox"    
    """
```

#### class modelarts.voc_position.Polygon

```python
__init__(self, points=None)
	"""    
    :param points: instance list of class Point
    """
```

```python
get_feature(self):
    """    
    :return: List<[xi, yi]>
    """
```

```python
get_type(cls):    
    """       
    :return: "polygon"    
    """
```

#### class modelarts.voc_position.Polyline

```python
__init__(self, points=None)
	"""    
    :param points: instance list of class Point
    """
```

```python
get_feature(self):
    """    
    :return: List<[xi, yi]>
    """
```

```python
get_type(cls):    
    """       
    :return: "polyline"    
    """
```



## API List for manifest

### class modelarts.manifest.DataSet 

```python
__init__(self, sample, size=None)
  """
  dataset for manifest
  dataset architecture:
    --size
    --sample list
      --sample 1
      --sample 2
          --source
          ...
          --annotation list
            --annotation 1
            --annotation 2
              --name
              --annotation_loc
              --type
              ...
              annotated_by
  """
```

```python
get_size(self):
    """
    :return size of the dataset, or the number of sample. Optional field
    """
```

```python
get_sample_list(self):  
    """ 
    :return a list of sample  Mandatory field 
    """
```

```python
save(self, path, access_key=None, secret_key=None, end_point=None, saveMode="w", ssl_verify=False, max_retry_count=3, timeout=60):  
    """  
    save dataset to local or OBS  It will overwrite if the file path already exists.  Please check the file path before invoking this method  
    
    :param path: manifest output path  
    :param access_key: access key of OBS  
    :param secret_key: secret key of OBS  
    :param end_point: end point of OBS 
    :param saveMode: default is "w", it will overwrite if file already exists. User can set "a" if user want to append content to one file. Can not append to a normal object.  
    :param ssl_verify: whether use ssl, set True if user want to verify certification, otherwise set False; default is False  
    :param max_retry_count: max retry count, default is 3 
    :param timeout: timeout [10,60], default is 60 
    """
```

### class modelarts.manifest.Sample

```python
__init__(self, source, annotations=None, usage=None, inference_loc=None, id=None, source_type=None, source_property=None)
	"""
    :param source: source image path  
    :param annotations: annotations of image
    :param usage: usage of image
    :param inference_loc: label file of image
    :param id: image id
    :param source_type: image source type
    :param source_property: image source property
    """
```

```python
get_source(self): 
    """ 
    :return "source" attribute  Mandatory field 
    """
```

```python
get_source_type(self): 
    """ 
    :return "source_type" attribute  Optional field 
    """
```

```python
get_id(self):  
    """ 
    :return "id" attribute, one of  Optional field 
    """
```

```python
get_usage(self):  
    """  
    :return "usage" attribute, one of  Optional field 
    """
```

```python
get_inference_loc(self): 
    """ 
    :return "inference_loc" attribute, one of Optional field 
    """
```

```python
get_annotations(self):  
    """ 
    :return a list of class Annotation. Optional field 
    """
```

### class modelarts.manifest.Annotation

```python
__init__(self, name=None, type=None, loc=None, property=None, confidence=None, creation_time=None, annotated_by=None, annotation_format=None, hard=None)
	"""
    :param name: name of annotation
    :param type: type of dataset
    :param loc: usage of image
    :param property: label file of image
    :param confidence: image id
    :param creation_time: image source type
    :param annotated_by: image source property
    :param annotation_format: image source property
    :param hard: image source property
    """
```

```python
get_type(self):
    """
    :return type of dataset: modelarts/image_classification, modelarts/object_detection. Optional field
    """
```

```python
get_name(self):  
    """  
    :return the name of this annotation, like "cat". Mandatory field if get_loc is None  
    """
```

```python
get_loc(self):  
    """  
    :return in case of object detection, this will return the annotation file,  otherwise return null. Mandatory field if get_name is None  
    """
```

```python
get_property(self): 
    """ 
    :return a KV pair list. Optional field  
    """
```

```python
get_hard(self): 
    """  
    :return set true if it's hard annotation, set false  if it's not hard annotation.  Optional field 
    """
```

```python
get_confidence(self):  
    """  :return confidence of label.  Optional field  """
```

```python
get_creation_time(self):  
    """  :return when this annotation is created.  Optional field  """
```

```python
get_annotation_format(self): 
    """  :return when this annotation format.  Optional field  """
```

```python
get_annotated_by(self): 
    """  :return who this annotation is created by.  Optional field  """
```



