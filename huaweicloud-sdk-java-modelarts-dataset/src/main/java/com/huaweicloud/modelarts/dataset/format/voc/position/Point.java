package com.huaweicloud.modelarts.dataset.format.voc.position;

import com.huaweicloud.modelarts.dataset.FieldName;

import static com.huaweicloud.modelarts.dataset.format.voc.position.PositionType.POINT;

public class Point implements Position {
  private String xName;
  private String xValue;
  private String yName;
  private String yValue;

  /**
   * point expression
   *
   * @param xName  x name of point
   * @param xValue x value of point
   * @param yName  y name of point
   * @param yValue y value of point
   */
  public Point(String xName, String xValue, String yName, String yValue) {
    this.xName = xName;
    this.xValue = xValue;
    this.yName = yName;
    this.yValue = yValue;
  }

  /**
   * point expression
   * Default value: x name of point is x, y name of point is y
   *
   * @param xValue x value of point
   * @param yValue y value of point
   */
  public Point(String xValue, String yValue) {
    this.xValue = xValue;
    this.yValue = yValue;
    this.xName = FieldName.X;
    this.yName = FieldName.Y;
  }

  public String getXName() {
    return xName;
  }

  public String getXValue() {
    return xValue;
  }

  public String getYName() {
    return yName;
  }

  public String getYValue() {
    return yValue;
  }

  @Override
  public boolean equals(Object obj) {
    Point point = ((Point) obj);
    if (this == point) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return point.xName.equals(this.xName)
        && point.xValue.equals(this.xValue)
        && point.yName.equals(this.yName)
        && point.yValue.equals(this.yValue);
  }

  @Override
  public PositionType getType() {
    return POINT;
  }
}
