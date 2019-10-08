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
from enum import Enum, unique


@unique
class PositionType(Enum):
    """
    Position type list of object detection
    """
    POINT = "point"
    LINE = "line"
    DASHED = "dashed"
    CIRCLE = "circle"
    BNDBOX = "bndbox"
    POLYGON = "polygon"
    POLYLINE = "polyline"


class Point(object):
    def __init__(self, x_value=None, y_value=None, x_name=field_name.X, y_name=field_name.Y):
        self._x_name = x_name
        self._x_value = x_value
        self._y_name = y_name
        self._y_value = y_value

    def get_x_name(self):
        return self._x_name

    def get_x_value(self):
        return self._x_value

    def get_y_name(self):
        return self._y_name

    def get_y_value(self):
        return self._y_value

    def get_feature(self):
        feature_points = [self._x_value, self._y_value]
        return feature_points

    def set_feature(self, feature):
        self._x_value = feature[0]
        self._y_value = feature[1]

    @classmethod
    def get_type(cls):
        return PositionType.POINT.value


class Line(object):
    def __init__(self, x1=None, y1=None, x2=None, y2=None):
        self._x1 = x1
        self._y1, = y1,
        self._x2 = x2
        self._y2 = y2

    def get_x1(self):
        return self._x1

    def get_y1(self):
        return self._y1

    def get_x2(self):
        return self._x2

    def get_y2(self):
        return self._y2

    def get_feature(self):
        feature_points = [[self._x1, self._y1], [self._x2, self._y2]]
        return feature_points

    def set_feature(self, feature):
        self._x1 = feature[0][0]
        self._y1 = feature[0][1]
        self._x2 = feature[1][0]
        self._y2 = feature[1][1]

    @classmethod
    def get_type(cls):
        return PositionType.LINE.value


class Dashed(object):
    def __init__(self, x1=None, y1=None, x2=None, y2=None):
        self._x1 = x1
        self._y1, = y1,
        self._x2 = x2
        self._y2 = y2

    def get_x1(self):
        return self._x1

    def get_y1(self):
        return self._y1

    def get_x2(self):
        return self._x2

    def get_y2(self):
        return self._y2

    def get_feature(self):
        feature_points = [[self._x1, self._y1], [self._x2, self._y2]]
        return feature_points

    def set_feature(self, feature):
        self._x1 = feature[0][0]
        self._y1 = feature[0][1]
        self._x2 = feature[1][0]
        self._y2 = feature[1][1]

    @classmethod
    def get_type(cls):
        return PositionType.DASHED.value


class Circle(object):
    def __init__(self, cx=None, cy=None, r=None):
        self._cx = cx
        self._cy = cy
        self._r = r

    def get_cx(self):
        return self._cx

    def get_cy(self):
        return self._cy

    def get_r(self):
        return self._r

    def get_feature(self):
        feature_points = [self._cx, self._cy, self._r]
        return feature_points

    def set_feature(self, feature):
        self._cx = feature[0]
        self._cy = feature[1]
        self._r = feature[2]

    @classmethod
    def get_type(cls):
        return PositionType.CIRCLE.value


class BNDBox(object):
    def __init__(self, x_min=None, y_min=None, x_max=None, y_max=None):
        self._x_min = x_min
        self._y_min = y_min
        self._x_max = x_max
        self._y_max = y_max

    def get_x_min(self):
        return self._x_min

    def get_y_min(self):
        return self._y_min

    def get_x_max(self):
        return self._x_max

    def get_y_max(self):
        return self._y_max

    def get_feature(self):
        feature_points = [[self._x_min, self._y_min], [self._x_max, self._y_max]]
        return feature_points

    def set_feature(self, feature):
        self._x_min = feature[0][0]
        self._y_min = feature[0][1]
        self._x_max = feature[1][0]
        self._y_max = feature[1][1]

    @classmethod
    def get_type(cls):
        return PositionType.BNDBOX.value


class Polygon(object):
    def __init__(self, points=None):
        self._points = points

    def get_points(self):
        return self._points

    def get_feature(self):
        points = self.get_points()
        feature_points = []
        for point in points:
            feature_points.append([point.get_x_value(), point.get_y_value()])
        return feature_points

    def set_feature(self, feature):
        self._points = []
        for point_num in range(len(feature)):
            point = feature[point_num]
            self._points.append(Point(point[0], point[1], "x" + str(point_num + 1), "y" + str(point_num + 1)))

    @classmethod
    def get_type(cls):
        return PositionType.POLYGON.value


class Polyline(object):
    def __init__(self, points=None):
        self._points = points

    def get_points(self):
        return self._points

    def get_feature(self):
        points = self.get_points()
        feature_points = []
        for point in points:
            feature_points.append([point.get_x_value(), point.get_y_value()])
        return feature_points

    @classmethod
    def get_type(cls):
        return PositionType.POLYLINE.value


def shape_is_point(doc, point_element, feature):
    x_element = doc.createElement(field_name.X)
    x_element.appendChild(doc.createTextNode(str(feature[0])))
    point_element.appendChild(x_element)
    y_element = doc.createElement(field_name.Y)
    y_element.appendChild(doc.createTextNode(str(feature[1])))
    point_element.appendChild(y_element)


def shape_is_line(doc, line_element, feature):
    point1 = feature[0]
    point2 = feature[1]
    x1_element = doc.createElement(field_name.X1)
    x1_element.appendChild(doc.createTextNode(str(point1[0])))
    line_element.appendChild(x1_element)
    y1_element = doc.createElement(field_name.Y1)
    y1_element.appendChild(doc.createTextNode(str(point1[1])))
    line_element.appendChild(y1_element)
    x2_element = doc.createElement(field_name.X2)
    x2_element.appendChild(doc.createTextNode(str(point2[0])))
    line_element.appendChild(x2_element)
    y2_element = doc.createElement(field_name.Y2)
    y2_element.appendChild(doc.createTextNode(str(point2[1])))
    line_element.appendChild(y2_element)


def shape_is_dashed(doc, dashed_element, feature):
    point1 = feature[0]
    point2 = feature[1]
    x1_element = doc.createElement(field_name.X1)
    x1_element.appendChild(doc.createTextNode(str(point1[0])))
    dashed_element.appendChild(x1_element)
    y1_element = doc.createElement(field_name.Y1)
    y1_element.appendChild(doc.createTextNode(str(point1[1])))
    dashed_element.appendChild(y1_element)
    x2_element = doc.createElement(field_name.X2)
    x2_element.appendChild(doc.createTextNode(str(point2[0])))
    dashed_element.appendChild(x2_element)
    y2_element = doc.createElement(field_name.Y2)
    y2_element.appendChild(doc.createTextNode(str(point2[1])))
    dashed_element.appendChild(y2_element)


def shape_is_circle(doc, circle_element, feature):
    cx_element = doc.createElement(field_name.CX)
    cx_element.appendChild(doc.createTextNode(str(feature[0])))
    circle_element.appendChild(cx_element)
    cy_element = doc.createElement(field_name.CY)
    cy_element.appendChild(doc.createTextNode(str(feature[1])))
    circle_element.appendChild(cy_element)
    r_element = doc.createElement(field_name.R)
    r_element.appendChild(doc.createTextNode(str(feature[2])))
    circle_element.appendChild(r_element)


def shape_is_bndbox(doc, bndbox_element, feature):
    point1 = feature[0]
    point2 = feature[1]
    xmin_element = doc.createElement(field_name.X_MIN)
    xmin_element.appendChild(doc.createTextNode(str(point1[0])))
    bndbox_element.appendChild(xmin_element)
    ymin_element = doc.createElement(field_name.Y_MIN)
    ymin_element.appendChild(doc.createTextNode(str(point1[1])))
    bndbox_element.appendChild(ymin_element)
    xmax_element = doc.createElement(field_name.X_MAX)
    xmax_element.appendChild(doc.createTextNode(str(point2[0])))
    bndbox_element.appendChild(xmax_element)
    ymax_element = doc.createElement(field_name.Y_MAX)
    ymax_element.appendChild(doc.createTextNode(str(point2[1])))
    bndbox_element.appendChild(ymax_element)


def shape_is_polygon(doc, polygon_element, feature):
    for xy_num in range(len(feature)):
        point = feature[xy_num]
        x_element = doc.createElement("x" + str(xy_num + 1))
        x_element.appendChild(doc.createTextNode(str(point[0])))
        polygon_element.appendChild(x_element)
        y_element = doc.createElement("y" + str(xy_num + 1))
        y_element.appendChild(doc.createTextNode(str(point[1])))
        polygon_element.appendChild(y_element)


def shape_is_polyline(doc, polyline_element, feature):
    for xy_num in range(len(feature)):
        point = feature[xy_num]
        x_element = doc.createElement("x" + str(xy_num + 1))
        x_element.appendChild(doc.createTextNode(str(point[0])))
        polyline_element.appendChild(x_element)
        y_element = doc.createElement("y" + str(xy_num + 1))
        y_element.appendChild(doc.createTextNode(str(point[1])))
        polyline_element.appendChild(y_element)


shape_cases = {PositionType.POINT.value: shape_is_point,
               PositionType.LINE.value: shape_is_line,
               PositionType.DASHED.value: shape_is_dashed,
               PositionType.CIRCLE.value: shape_is_circle,
               PositionType.BNDBOX.value: shape_is_bndbox,
               PositionType.POLYGON.value: shape_is_polygon,
               PositionType.POLYLINE.value: shape_is_polyline}

