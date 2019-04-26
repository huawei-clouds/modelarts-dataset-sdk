package com.huaweicloud.modelarts.dataset.format.voc.position;

public class Line extends Dashed {
  /**
   * Line, start point (x1,y1), end point (x2, y2)
   *
   * @param x1 x value of start point
   * @param y1 y value of start point
   * @param x2 x value of end point
   * @param y2 y value of end point
   */
  public Line(String x1, String y1, String x2, String y2) {
    super(x1, y1, x2, y2);
  }

  @Override
  public PositionType getType() {
    return PositionType.LINE;
  }
}
