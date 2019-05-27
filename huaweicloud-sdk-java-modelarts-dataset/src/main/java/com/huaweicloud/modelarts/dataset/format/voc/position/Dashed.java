package com.huaweicloud.modelarts.dataset.format.voc.position;

public class Dashed implements Position
{
    private String x1;

    private String y1;

    private String x2;

    private String y2;

    /**
     * dashed, start point (x1,y1), end point (x2, y2)
     *
     * @param x1 x value of start point
     * @param y1 y value of start point
     * @param x2 x value of end point
     * @param y2 y value of end point
     */
    public Dashed(String x1, String y1, String x2, String y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public String getX1()
    {
        return x1;
    }

    public String getY1()
    {
        return y1;
    }

    public String getX2()
    {
        return x2;
    }

    public String getY2()
    {
        return y2;
    }

    @Override
    public PositionType getType()
    {
        return PositionType.DASHED;
    }
}
