package com.huaweicloud.modelarts.dataset.format.voc.position;

public class Circle implements Position {
  private String cx;
  private String cy;
  private String r;

  /**
   * circle for object
   *
   * @param cx x value of center point of a circle
   * @param cy y value of center point of a circle
   * @param r  radius of a circle
   */
  public Circle(String cx, String cy, String r) {
    this.cx = cx;
    this.cy = cy;
    this.r = r;
  }

  public String getCx() {
    return cx;
  }

  public String getCy() {
    return cy;
  }

  public String getR() {
    return r;
  }

  @Override
  public PositionType getType() {
    return PositionType.CIRCLE;
  }
}
