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


from modelarts import field_name
from modelarts.voc_position import BNDBox, Circle, Polygon, Polyline, Point, Line, Dashed, PositionType, shape_cases
from xml.dom.minidom import Document
from modelarts.file_util import __is_local, save
from modelarts.file_util import __read
from obs import ObsClient

import xml.sax
import xml.sax.handler

import xml.dom.minidom
import codecs
import collections
import logging
import os

logging.basicConfig(level=logging.DEBUG)


def get_voc_annotation_file(data_set):
    data_objects = data_set.get_sample_list()
    for data_object in data_objects:
        source = data_object.get_source()
        usage = data_object.get_usage()
        annotations = data_object.get_annotations()
        for annotation in annotations:
            annotation_type = annotation.get_type()
            annotation_name = annotation.get_name()
            annotation_loc = annotation.get_loc()
            annotation_property = annotation.get_property()
            annotation_create_time = annotation.get_creation_time()
        assert annotation.get_annotation_format() == "PASCAL VOC"
        annotation_annotated_by = annotation.get_annotated_by()
        print(str(annotation_type) + "\t" + str(annotation_name) + "\t" + str(annotation_loc) + "\t" + str(
            annotation_property) + "\t" + str(annotation_create_time) + "\t" + str(annotation_annotated_by))
        return annotation_loc


def build_position(doc, voc_object_element, type, feature):
    position_element = doc.createElement(type)
    voc_object_element.appendChild(position_element)
    try:
        shape_cases[type](doc, position_element, feature)
    except KeyError:
        logging.error("Error shape type: %s, object shape only can be %s, %s, %s, %s, %s, %s, %s" % (
            type,
            PositionType.POINT.value,
            PositionType.LINE.value,
            PositionType.DASHED.value,
            PositionType.BNDBOX.value,
            PositionType.CIRCLE.value,
            PositionType.POLYGON.value,
            PositionType.POLYLINE.value))


def generate_xml(file_name, xml_file_path, width=None, height=None, depth=None, voc_objects=None):
    """
    Write voc annotation to xml file.
    It will overwrite if the xml file path already exists.

    :param file_name: file name which is annotated
    :param xml_file_path: xml output path
    :param size_list: voc object size, which is [width, height, depth]
    :param voc_object_tags: list of object name
    :param voc_object_properties: list of object property
    :return: None
    """
    doc = Document()

    # root element
    annotation_element = doc.createElement(field_name.ANNOTATIONS)
    doc.appendChild(annotation_element)

    # folder
    folder_element = doc.createElement(field_name.FOLDER_NAME)
    folder_element.appendChild(doc.createTextNode("Images"))
    annotation_element.appendChild(folder_element)

    # filename
    file_name_element = doc.createElement(field_name.FILE_NAME)
    file_name_element.appendChild(doc.createTextNode(file_name))
    annotation_element.appendChild(file_name_element)

    # source
    source_element = doc.createElement(field_name.SOURCE)
    annotation_element.appendChild(source_element)

    database_element = doc.createElement(field_name.DATABASE)
    database_element.appendChild(doc.createTextNode("Unknown"))
    source_element.appendChild(database_element)

    # size
    size_element = doc.createElement(field_name.SIZE)
    annotation_element.appendChild(size_element)

    width_element = doc.createElement(field_name.WIDTH)
    width_element.appendChild(doc.createTextNode(str(width)))
    size_element.appendChild(width_element)

    height_element = doc.createElement(field_name.HEIGHT)
    height_element.appendChild(doc.createTextNode(str(height)))
    size_element.appendChild(height_element)

    depth_element = doc.createElement(field_name.DEPTH)
    depth_value = depth if depth is not None else 0
    depth_element.appendChild(doc.createTextNode(str(depth_value)))
    size_element.appendChild(depth_element)

    # segmented
    segmented_element = doc.createElement(field_name.SEGMENTED)
    segmented_element.appendChild(doc.createTextNode("0"))
    annotation_element.appendChild(segmented_element)

    # voc objects
    for voc_object in voc_objects:
        voc_object_element = doc.createElement(field_name.OBJECT)
        annotation_element.appendChild(voc_object_element)

        # name
        name_element = doc.createElement(field_name.NAME)
        name_element.appendChild(doc.createTextNode(str(voc_object.get_name())))
        voc_object_element.appendChild(name_element)

        # properties
        properties = voc_object.get_properties()

        # pose
        if field_name.POSE in properties:
            pose_element = doc.createElement(field_name.POSE)
            pose_element.appendChild(doc.createTextNode(str(properties[field_name.POSE])))
            voc_object_element.appendChild(pose_element)
            properties.pop(field_name.POSE)

        # truncated
        if field_name.TRUNCATED in properties:
            truncated_element = doc.createElement(field_name.TRUNCATED)
            truncated_element.appendChild(doc.createTextNode(str(properties[field_name.TRUNCATED])))
            voc_object_element.appendChild(truncated_element)
            properties.pop(field_name.TRUNCATED)

        # difficult
        if field_name.DIFFICULT in properties:
            difficult_element = doc.createElement(field_name.DIFFICULT)
            difficult_element.appendChild(doc.createTextNode(str(properties[field_name.DIFFICULT])))
            voc_object_element.appendChild(difficult_element)
            properties.pop(field_name.DIFFICULT)

        # occluded
        if field_name.OCCLUDED in properties:
            occluded_element = doc.createElement(field_name.OCCLUDED)
            occluded_element.appendChild(doc.createTextNode(str(properties[field_name.OCCLUDED])))
            voc_object_element.appendChild(occluded_element)
            properties.pop(field_name.OCCLUDED)

        # position
        shape_name = properties[field_name.SHAPE]
        feature = properties[field_name.FEATURE]
        build_position(doc, voc_object_element, shape_name, feature)

        # custom attribute
        if properties is not None and len(properties) > 0:
            custom_attributes = []
            if field_name.SECONDARY_LABELS in properties:
                secondary_labels_value = properties.get(field_name.SECONDARY_LABELS)
                if isinstance(secondary_labels_value, dict):
                    for secondary_label_key, secondary_label_value in secondary_labels_value.items():
                        custom_attributes.append({secondary_label_key: secondary_label_value})
                else:
                    custom_attributes.append({field_name.SECONDARY_LABELS: str(secondary_labels_value)})
                properties.pop(field_name.SECONDARY_LABELS)

            # user-defined attributes do not start with @modelarts
            for property_key, property_value in properties.items():
                if not property_key.startswith(field_name.INTERNAL_PROPERTY_KEY_PREFIX):
                    custom_attributes.append({property_key: property_value})

            # attributes start with @modelarts added to properties
            modelarts_attributes = []
            if field_name.CLOCKWISE_ANGLE in properties:
                modelarts_attributes.append({field_name.CLOCKWISE_ANGLE: properties[field_name.CLOCKWISE_ANGLE]})
                properties.pop(field_name.CLOCKWISE_ANGLE)
            if field_name.ROTATE_BACKGROUND in properties:
                modelarts_attributes.append({field_name.ROTATE_BACKGROUND: properties[field_name.ROTATE_BACKGROUND]})
                properties.pop(field_name.ROTATE_BACKGROUND)

            # output custom attribute
            if len(custom_attributes) > 0 or len(modelarts_attributes) > 0:
                properties_element = doc.createElement(field_name.VOC_PROPERTIES)
                for custom_attribute in custom_attributes:
                    property_element = doc.createElement(field_name.VOC_PROPERTY)
                    property_key_element = doc.createElement(field_name.VOC_PROPERTY_KEY)
                    property_value_element = doc.createElement(field_name.VOC_PROPERTY_VALUE)
                    property_key_element.appendChild(doc.createTextNode(str(list(custom_attribute.keys())[0])))
                    property_value_element.appendChild(doc.createTextNode(str(list(custom_attribute.values())[0])))
                    property_element.appendChild(property_key_element)
                    property_element.appendChild(property_value_element)
                    properties_element.appendChild(property_element)

                for modelarts_attribute in modelarts_attributes:
                    property_element = doc.createElement(field_name.VOC_PROPERTY)
                    property_key_element = doc.createElement(field_name.VOC_PROPERTY_KEY)
                    property_value_element = doc.createElement(field_name.VOC_PROPERTY_VALUE)
                    property_key_element.appendChild(doc.createTextNode(str(list(modelarts_attribute.keys())[0])))
                    property_value_element.appendChild(doc.createTextNode(str(list(modelarts_attribute.values())[0])))
                    property_element.appendChild(property_key_element)
                    property_element.appendChild(property_value_element)
                    properties_element.appendChild(property_element)

                voc_object_element.appendChild(properties_element)

        with codecs.open(xml_file_path, 'w', encoding='utf-8') as f:
            f.write(doc.toprettyxml(indent='\t'))


def get_voc_object(object_element):
    """
    Parse DOM element of object to VOC object.

    :param object_element: DOM element of object
    :return: instance of VocObject
    """

    name = None
    properties = collections.OrderedDict()
    pose = None
    truncated = None
    occluded = None
    difficult = None
    confidence = None
    position = None
    parts = None
    position_flag = False
    position_type = None

    for object_child_element in object_element.childNodes:
        # name
        if object_child_element.nodeName == field_name.NAME:
            name_node_list = object_child_element.childNodes
            if len(name_node_list) > 0:
                name = name_node_list[0].data
            else:
                raise Exception(field_name.OBJECT + " " + field_name.NAME + " can't be empty in VOC file!")

        # properties
        elif object_child_element.nodeName == field_name.VOC_PROPERTIES:
            properties_node_list = object_child_element.childNodes
            for properties_child_element in properties_node_list:
                properties_child_name = properties_child_element.nodeName
                if properties_child_name != "#text":
                    if len(properties_child_element.childNodes) > 1:
                        property_key = None
                        property_value = None
                        for property_element in properties_child_element.childNodes:
                            property_child_name = property_element.nodeName
                            if property_child_name != "#text":
                                if property_child_name == field_name.VOC_PROPERTY_KEY:
                                    if len(property_element.childNodes) > 0:
                                        property_key = property_element.childNodes[0].data
                                    else:
                                        raise Exception(field_name.OBJECT + " " + field_name.VOC_PROPERTIES + " " +
                                                        field_name.VOC_PROPERTY + " " + field_name.VOC_PROPERTY_KEY +
                                                        " can't be empty in VOC file!")
                                if property_child_name == field_name.VOC_PROPERTY_VALUE:
                                    if len(property_element.childNodes) > 0:
                                        property_value = property_element.childNodes[0].data
                                    else:
                                        raise Exception(field_name.OBJECT + " " + field_name.VOC_PROPERTIES + " " +
                                                        field_name.VOC_PROPERTY + " " + field_name.VOC_PROPERTY_VALUE +
                                                        " can't be empty in VOC file!")
                        if property_key is not None and property_value is not None:
                            properties[property_key] = property_value
                    else:
                        if len(properties_child_element.childNodes) > 0:
                            properties[properties_child_name] = properties_child_element.childNodes[0].data
                        else:
                            raise Exception(
                                field_name.OBJECT + " " + field_name.VOC_PROPERTIES + " " +
                                properties_child_name + " can't be empty in VOC file!")

        # pose
        elif object_child_element.nodeName == field_name.POSE:
            pose_node_list = object_child_element.childNodes
            if len(pose_node_list) > 0:
                pose = pose_node_list[0].data
            else:
                raise Exception(field_name.OBJECT + " " + field_name.POSE + " can't be empty in VOC file!")

        # truncated
        elif object_child_element.nodeName == field_name.TRUNCATED:
            truncated_node_list = object_child_element.childNodes
            if len(truncated_node_list) > 0:
                truncated = truncated_node_list[0].data
            else:
                raise Exception(field_name.OBJECT + " " + field_name.TRUNCATED + " can't be empty in VOC file!")

        # occluded
        elif object_child_element.nodeName == field_name.OCCLUDED:
            occluded_node_list = object_child_element.childNodes
            if len(occluded_node_list) > 0:
                occluded = occluded_node_list[0].data
            else:
                raise Exception(field_name.OBJECT + " " + field_name.OCCLUDED + " can't be empty in VOC file!")

        # difficult
        elif object_child_element.nodeName == field_name.DIFFICULT:
            difficult_node_list = object_child_element.childNodes
            if len(difficult_node_list) > 0:
                difficult = difficult_node_list[0].data
            else:
                raise Exception(field_name.OBJECT + " " + field_name.DIFFICULT + " can't be empty in VOC file!")

        # confidence
        elif object_child_element.nodeName == field_name.CONFIDENCE:
            confidence_node_list = object_child_element.childNodes
            if len(confidence_node_list) > 0:
                confidence = confidence_node_list[0].data

        elif object_child_element.nodeName == PositionType.BNDBOX.value:
            bndbox_node_list = object_child_element.childNodes
            check_position(position_flag, position_type, object_child_element.nodeName)
            position_flag = True
            position_type = PositionType.BNDBOX.value
            if len(bndbox_node_list) < 2:
                raise Exception(field_name.OBJECT + " " + PositionType.BNDBOX.value + " can't be empty in VOC file!")
            x_min = None
            y_min = None
            x_max = None
            y_max = None
            for bndbox_element in bndbox_node_list:
                bndbox_node_name = bndbox_element.nodeName
                if bndbox_node_name == field_name.X_MIN:
                    x_min = bndbox_element.childNodes[0].data
                if bndbox_node_name == field_name.Y_MIN:
                    y_min = bndbox_element.childNodes[0].data
                if bndbox_node_name == field_name.X_MAX:
                    x_max = bndbox_element.childNodes[0].data
                if bndbox_node_name == field_name.Y_MAX:
                    y_max = bndbox_element.childNodes[0].data
            if x_min is not None and y_min is not None and x_max is not None and y_max is not None:
                position = BNDBox(int(x_min), int(y_min), int(x_max), int(y_max))
            else:
                raise Exception(
                    field_name.OBJECT + " " + PositionType.BNDBOX.value + " should have xmin, ymin, xmax, ymax.")

        elif object_child_element.nodeName == PositionType.POLYGON.value:
            polygon_node_list = object_child_element.childNodes
            check_position(position_flag, position_type, object_child_element.nodeName)
            position_flag = True
            position_type = PositionType.POLYGON.value
            if len(polygon_node_list) < 2:
                raise Exception(field_name.OBJECT + " " + PositionType.POLYGON.value + " can't be empty in VOC file!")
            points_xy = {}
            for polygon_element in polygon_node_list:
                polygon_node_name = polygon_element.nodeName
                if polygon_node_name.startswith("x") or polygon_node_name.startswith("y"):
                    points_xy[polygon_node_name] = int(polygon_element.childNodes[0].data)
            points = []
            for xy_key in points_xy:
                if xy_key.startswith("x") and ("y" + xy_key[1:]) in points_xy:
                    points.append(Point(points_xy[xy_key], points_xy["y" + xy_key[1:]], xy_key, "y" + xy_key[1:]))
                else:
                    continue
                if xy_key.startswith("y") and ("x" + xy_key[1:]) in points_xy:
                    points.append(Point(points_xy["x" + xy_key[1:]], points_xy[xy_key], "x" + xy_key[1:], xy_key))
                else:
                    continue
            if len(points) > 0:
                position = Polygon(points)
            else:
                raise Exception(field_name.OBJECT + " " + PositionType.POLYGON.value + " should have xn, yn.")

        elif object_child_element.nodeName == PositionType.POLYLINE.value:
            polyline_node_list = object_child_element.childNodes
            check_position(position_flag, position_type, object_child_element.nodeName)
            position_flag = True
            position_type = PositionType.POLYLINE.value
            if len(polyline_node_list) < 2:
                raise Exception(field_name.OBJECT + " " + PositionType.POLYLINE.value + " can't be empty in VOC file!")
            points_xy = {}
            for polyline_element in polyline_node_list:
                polyline_node_name = polyline_element.nodeName
                if polyline_node_name.startswith("x") or polyline_node_name.startswith("y"):
                    points_xy[polyline_node_name] = int(polyline_element.childNodes[0].data)
            points = []
            for xy_key in points_xy:
                if xy_key.startswith("x") and ("y" + xy_key[1:]) in points_xy:
                    points.append(Point(points_xy[xy_key], points_xy["y" + xy_key[1:]], xy_key, "y" + xy_key[1:]))
                else:
                    continue
                if xy_key.startswith("y") and ("x" + xy_key[1:]) in points_xy:
                    points.append(Point(points_xy["x" + xy_key[1:]], points_xy[xy_key], "x" + xy_key[1:], xy_key))
                else:
                    continue
            if len(points) > 0:
                position = Polyline(points)
            else:
                raise Exception(field_name.OBJECT + " " + PositionType.POLYLINE.value + " should have xn, yn.")

        elif object_child_element.nodeName == PositionType.DASHED.value:
            dashed_node_list = object_child_element.childNodes
            check_position(position_flag, position_type, object_child_element.nodeName)
            position_flag = True
            position_type = PositionType.DASHED.value
            if len(dashed_node_list) < 2:
                raise Exception(field_name.OBJECT + " " + PositionType.DASHED.value + " can't be empty in VOC file!")
            x1 = None
            y1 = None
            x2 = None
            y2 = None
            for dashed_element in dashed_node_list:
                dashed_node_name = dashed_element.nodeName
                if dashed_node_name == field_name.X1:
                    x1 = dashed_element.childNodes[0].data
                if dashed_node_name == field_name.Y1:
                    y1 = dashed_element.childNodes[0].data
                if dashed_node_name == field_name.X2:
                    x2 = dashed_element.childNodes[0].data
                if dashed_node_name == field_name.Y2:
                    y2 = dashed_element.childNodes[0].data
            if x1 is not None and y1 is not None and x2 is not None and y2 is not None:
                position = Dashed(int(x1), int(y1), int(x2), int(y2))
            else:
                raise Exception(
                    field_name.OBJECT + " " + PositionType.DASHED.value + " should have x1, y1, x2, y2.")

        elif object_child_element.nodeName == PositionType.LINE.value:
            line_node_list = object_child_element.childNodes
            check_position(position_flag, position_type, object_child_element.nodeName)
            position_flag = True
            position_type = PositionType.LINE.value
            if len(line_node_list) < 2:
                raise Exception(field_name.OBJECT + " " + PositionType.LINE.value + " can't be empty in VOC file!")
            x1 = None
            y1 = None
            x2 = None
            y2 = None
            for line_element in line_node_list:
                line_node_name = line_element.nodeName
                if line_node_name == field_name.X1:
                    x1 = line_element.childNodes[0].data
                if line_node_name == field_name.Y1:
                    y1 = line_element.childNodes[0].data
                if line_node_name == field_name.X2:
                    x2 = line_element.childNodes[0].data
                if line_node_name == field_name.Y2:
                    y2 = line_element.childNodes[0].data
            if x1 is not None and y1 is not None and x2 is not None and y2 is not None:
                position = Line(int(x1), int(y1), int(x2), int(y2))
            else:
                raise Exception(
                    field_name.OBJECT + " " + PositionType.LINE.value + " should have x1, y1, x2, y2.")

        elif object_child_element.nodeName == PositionType.CIRCLE.value:
            circle_node_list = object_child_element.childNodes
            check_position(position_flag, position_type, object_child_element.nodeName)
            position_flag = True
            position_type = PositionType.CIRCLE.value
            if len(circle_node_list) < 2:
                raise Exception(field_name.OBJECT + " " + PositionType.CIRCLE.value + " can't be empty in VOC file!")
            cx = None
            cy = None
            r = None
            for circle_element in circle_node_list:
                circle_node_name = circle_element.nodeName
                if circle_node_name == field_name.CX:
                    cx = circle_element.childNodes[0].data
                if circle_node_name == field_name.CY:
                    cy = circle_element.childNodes[0].data
                if circle_node_name == field_name.R:
                    r = circle_element.childNodes[0].data
            if cx is not None and cy is not None and r is not None:
                position = Circle(int(cx), int(cy), int(r))
            else:
                raise Exception(
                    field_name.OBJECT + " " + PositionType.CIRCLE.value + " should have cx, cy, r.")

        elif object_child_element.nodeName == PositionType.POINT.value:
            point_node_list = object_child_element.childNodes
            check_position(position_flag, position_type, object_child_element.nodeName)
            position_flag = True
            position_type = PositionType.POINT.value
            if len(point_node_list) < 2:
                raise Exception(field_name.OBJECT + " " + PositionType.POINT.value + " can't be empty in VOC file!")
            x = None
            y = None
            for point_element in point_node_list:
                point_node_name = point_element.nodeName
                if point_node_name == field_name.X:
                    x = point_element.childNodes[0].data
                if point_node_name == field_name.Y:
                    y = point_element.childNodes[0].data
            if x is not None and y is not None:
                position = Point(int(x), int(y))
            else:
                raise Exception(
                    field_name.OBJECT + " " + PositionType.POINT.value + " should have x, y.")

        # part
        elif object_child_element.nodeName == field_name.PART:
            if parts is None:
                parts = []
            parts.append(get_voc_object(object_child_element))

    return VocObject(name, properties, pose, truncated, occluded, difficult, confidence,
                     position, parts)


def parse_voc_annotation_file(xml_path):
    """
    Parse xml file to PascalVoc.

    :param xml_path: xml input path
    :return: instance of PascalVocIO
    """
    if os.path.exists(xml_path):
        dom = xml.dom.minidom.parse(xml_path)
        collection = dom.documentElement

        folder = None
        file_name = None
        source = None
        width = None
        height = None
        depth = None
        segmented = None
        voc_objects = None
        for child_element in collection.childNodes:
            # folder
            if child_element.nodeName == field_name.FOLDER_NAME:
                folder_node_list = child_element.childNodes
                if len(folder_node_list) > 0:
                    folder = folder_node_list[0].data
                else:
                    raise Exception(field_name.FOLDER_NAME + " can't be empty in VOC file!")

            # filename
            elif child_element.nodeName == field_name.FILE_NAME:
                file_name_node_list = child_element.childNodes
                if len(file_name_node_list) > 0:
                    file_name = file_name_node_list[0].data
                else:
                    raise Exception(field_name.FILE_NAME + " can't be empty in VOC file!")

            # source
            elif child_element.nodeName == field_name.SOURCE:
                database = None
                annotation = None
                image = None
                for source_child_element in child_element.childNodes:
                    if source_child_element.nodeName == field_name.DATABASE:
                        database_node_list = source_child_element.childNodes
                        if len(database_node_list) > 0:
                            database = database_node_list[0].data
                        else:
                            logging.debug(field_name.SOURCE + " " + field_name.DATABASE + " is empty in VOC file.")

                    elif source_child_element.nodeName == field_name.ANNOTATIONS:
                        annotation_node_list = source_child_element.childNodes
                        if len(annotation_node_list) > 0:
                            annotation = annotation_node_list[0].data
                        else:
                            logging.debug(field_name.SOURCE + " " + field_name.ANNOTATIONS + " is empty in VOC file.")

                    elif source_child_element.nodeName == field_name.IMAGE:
                        image_node_list = source_child_element.childNodes
                        if len(image_node_list) > 0:
                            image = image_node_list[0].data
                        else:
                            logging.debug(field_name.SOURCE + " " + field_name.IMAGE + " is empty in VOC file.")

                source = Source(database, annotation, image)

            # size
            elif child_element.nodeName == field_name.SIZE:
                for size_child_element in child_element.childNodes:
                    if size_child_element.nodeName == field_name.WIDTH:
                        width_node_list = size_child_element.childNodes
                        if len(width_node_list) > 0:
                            width = int(width_node_list[0].data)
                        else:
                            raise Exception(field_name.SIZE + " " + field_name.WIDTH + " can't be empty in VOC file!")

                    elif size_child_element.nodeName == field_name.HEIGHT:
                        height_node_list = size_child_element.childNodes
                        if len(height_node_list) > 0:
                            height = int(height_node_list[0].data)
                        else:
                            raise Exception(field_name.SIZE + " " + field_name.HEIGHT + " can't be empty in VOC file!")

                    elif size_child_element.nodeName == field_name.DEPTH:
                        depth_node_list = size_child_element.childNodes
                        if len(depth_node_list) > 0:
                            depth = int(depth_node_list[0].data)
                        else:
                            raise Exception(field_name.SIZE + " " + field_name.DEPTH + " can't be empty in VOC file!")

            # segmented
            elif child_element.nodeName == field_name.SEGMENTED:
                segmented_node_list = child_element.childNodes
                if len(segmented_node_list) > 0:
                    segmented = segmented_node_list[0].data
                else:
                    raise Exception(field_name.SEGMENTED + " can't be empty in VOC file!")

            # objects
            elif child_element.nodeName == field_name.OBJECT:
                object_node_list = child_element.childNodes
                if len(object_node_list) > 1:
                    if voc_objects is None:
                        voc_objects = []
                    object_element = child_element
                    voc_objects.append(get_voc_object(object_element))
                else:
                    raise Exception(field_name.OBJECT + " can't be empty in VOC file!")

        return PascalVocIO(folder, file_name, source, width, height, depth, segmented, voc_objects)


def check_position(position_flag, position_type, object_child_node_name):
    if position_flag:
        if position_type == object_child_node_name:
            raise Exception("The number of %s %s can't more than one in one object or one part" %
                        (field_name.OBJECT, position_type))
        else:
            raise Exception("The number of %s position type can't more than one in one object or one part, "
                            "there are %s and %s in the %s" %
                            (field_name.OBJECT, position_type, object_child_node_name, field_name.OBJECT))


def parse_xml_file(xml_file_path, access_key=None, secret_key=None, end_point=None, obs_client=None,
                   ssl_verify=False, max_retry_count=3, timeout=60):
    local = __is_local(xml_file_path)
    if local:
        try:
            return parse_voc_annotation_file(xml_file_path)
        except Exception as e:
            raise Exception("Can't parse the XML file, %s; The file is %s " % (str(e), xml_file_path))
    else:
        if (access_key is None or secret_key is None or end_point is None) and obs_client is None:
            raise ValueError("Please input ak, sk and endpoint or obs_client")
        if obs_client is not None:
            obs_client = obs_client
        else:
            obs_client = ObsClient(
                access_key_id=access_key,
                secret_access_key=secret_key,
                server=end_point,
                long_conn_mode=True,
                ssl_verify=ssl_verify,
                max_retry_count=max_retry_count,
                timeout=timeout)
        xml_b = __read(xml_file_path, obs_client)
        local_tmp_path = "./tmp.xml"
        with open(local_tmp_path, "w") as f:
            f.write(xml_b.decode())
        try:
            pascal_voc = parse_voc_annotation_file(local_tmp_path)
            if os.path.exists(local_tmp_path):
                os.remove(local_tmp_path)
            return pascal_voc
        except Exception as e:
            raise Exception("Can't parse the XML file, %s; The file is %s " % (str(e), xml_file_path))


class PascalVocIO(object):
    def __init__(self, folder=None, file_name=None, source=None, width=None, height=None, depth=None,
                 segmented=None, voc_objects=None):
        """
        Constructor for PascalVoc

        :param folder: image folder
        :param file_name: image name
        :param source: include database for example The VOC2007 Database,
                        annotation format for example PASCAL VOC2007,
                        image source for example flickr.
        :param width: image size
        :param height: image size
        :param depth: image size
        :param segmented: whether the image is segmented
        :param voc_objects: voc object contains the annotation of object detection
        """
        self._folder = folder
        self._file_name = file_name
        self._source = source
        self._width = width
        self._height = height
        self._depth = depth
        self._segmented = segmented
        self._voc_objects = voc_objects

    def get_folder(self):
        """
        :return: fold name, optional field
        """
        return self._folder

    def get_file_name(self):
        """
        :return: file name, mandatory field
        """
        return self._file_name

    def get_source(self):
        """
        :return: image source, optional field
        """
        return self._source

    def get_width(self):
        """
        :return: image width, mandatory field
        """
        return self._width

    def get_height(self):
        """
        :return: image height, mandatory field
        """
        return self._height

    def get_depth(self):
        """
        :return: image depth, mandatory field
        """
        return self._depth

    def get_segmented(self):
        """
        :return: whether image is segmented, optional field
        """
        return self._segmented

    def get_voc_objects(self):
        """
        :return: instance list of VocObject, mandatory field
        """
        return self._voc_objects

    def save_xml(self, xml_file_path, access_key=None, secret_key=None, end_point=None, save_mode='w',
                 ssl_verify=False, max_retry_count=3, timeout=60):
        """
        write xml file to local or OBS

        :param xml_file_path: xml file output path
        :param access_key: access key of OBS
        :param secret_key: secret key of OBS
        :param end_point: end point of OBS
        :param save_mode: write mode of saving xml
        :param ssl_verify: whether use ssl, set True if user want to verify certification, otherwise set False; default is False
        :param max_retry_count: max retry count, default is 3
        :param timeout: timeout [10,60], default is 60
        :return: None
        """
        if str(xml_file_path).lower().startswith(field_name.s3) or str(xml_file_path).lower().startswith(field_name.s3a):
            if access_key is None:
                raise Exception("access_key is None")
            elif secret_key is None:
                raise Exception("secret_key is None")
            elif end_point is None:
                raise Exception("end_point is None")
            else:
                xml_lines = []
                local_tmp_path = "./tmp.xml"
                generate_xml(self._file_name, local_tmp_path, width=self._width, height=self._height, depth=self._depth,
                             voc_objects=self._voc_objects)
                with open(local_tmp_path, 'r') as f:
                    for line in f.readlines():
                        xml_lines.append(line.strip('\n'))
                os.remove(local_tmp_path)
                save(xml_lines, xml_file_path, access_key=access_key, secret_key=secret_key, end_point=end_point,
                     saveMode=save_mode, ssl_verify=ssl_verify, max_retry_count=max_retry_count, timeout=timeout)
        else:
            generate_xml(self._file_name, xml_file_path, width=self._width, height=self._height, depth=self._depth,
                         voc_objects=self._voc_objects)

    @classmethod
    def parse_xml(cls, xml_file_path, access_key=None, secret_key=None, end_point=None, obs_client=None,
                  ssl_verify=False, max_retry_count=3, timeout=60):
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
        return parse_xml_file(xml_file_path, access_key=access_key, secret_key=secret_key, end_point=end_point, obs_client=obs_client,
                              ssl_verify=ssl_verify, max_retry_count=max_retry_count, timeout=timeout)


class VocObject(object):
    def __init__(self, name=None, properties=None, pose=None, truncated=None, occluded=None,
                 difficult=None, confidence=None, position=None, parts=None):
        """
        Constructor for VOC Object

        :param name: object name
        :param properties: object properties
        :param pose: pose value
        :param truncated: an object marked as 'truncated' indicates that the bounding box specified for the object
                does not correspond to the full extent of the object.
                e.g. an image of a person from the waist up, or a view of a car extending outside the image.
                The truncated field being set to 1 indicates that the object is "truncated" in the image.
        :param occluded: an object marked as 'occluded' indicates that a significant portion of the object
                within the bounding box is occluded by another object.
                The occluded field being set to 1 indicates that the object is significantly occluded by another object.
                Participants are free to use or ignore this field as they see fit.
        :param difficult: an object marked as 'difficult' indicates that the object is considered difficult to recognize,
                for example an object which is clearly visible but unidentifiable without substantial use of context.
                Objects marked as difficult are currently ignored in the evaluation of the challenge.
                The difficult field being set to 1 indicates that the object has been annotated as "difficult",
                for example an object which is clearly visible but difficult to recognize without substantial use of context.
        :param confidence: Confidence for annotation that was annotated by machine, the value: 0 <= Confidence < 1, optional field.
        :param position: position can be point, line or others.
        :param parts: one object may have subobjects
        """
        self._name = name
        self._properties = properties
        self._pose = pose
        self._truncated = truncated
        self._occluded = occluded
        self._difficult = difficult
        self._confidence = confidence
        self._position = position
        self._parts = parts

    def set_parts(self, parts):
        self._parts = parts

    def get_name(self):
        """
        :return: the object name, mandatory field
        """
        return self._name

    def get_properties(self):
        """
        :return: the properties of object, mandatory field
        """
        return self._properties

    def get_pose(self):
        """
        :return: the pose of object, optional field
        """
        return self._pose

    def get_truncated(self):
        """
        :return: whether the object is truncated, optional field
        """
        return self._truncated

    def get_occluded(self):
        """
        :return: whether the object is occluded, optional field
        """
        return self._occluded

    def get_difficult(self):
        """
        :return: whether the object is difficult, optional field
        """
        return self._difficult

    def get_confidence(self):
        """
        :return: confidence value of the object annotation, optional field
        """
        return self._confidence

    def get_position(self):
        """
        :return: the pose of object, mandatory field
        """
        return self._position

    def get_parts(self):
        """
        :return: subobjects, optional field
        """
        return self._parts


class Source(object):
    def __init__(self, database=None, annotation=None, image=None):
        self._database = database
        self._annotation = annotation
        self._image = image

    def set_database(self, database):
        self._database = database

    def set_annotation(self, annotation):
        self._annotation = annotation

    def set_image(self, image):
        self._image = image

    def get_database(self):
        return self._database

    def get_annotation(self):
        return self._annotation

    def get_image(self):
        return self._image

