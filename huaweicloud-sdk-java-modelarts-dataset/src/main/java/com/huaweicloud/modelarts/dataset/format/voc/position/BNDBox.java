package com.huaweicloud.modelarts.dataset.format.voc.position;

public class BNDBox implements Position
{
    private String xMin;

    private String yMin;

    private String xMax;

    private String yMax;

    /**
     * bounding box for object
     * (xMin,yMin),(xMin,yMax),(xMax,yMax),(xMax,yMin)
     *
     * @param xMin min x value of BNDBox
     * @param yMin min y value of BNDBox
     * @param xMax max x value of BNDBox
     * @param yMax max y value of BNDBox
     */
    public BNDBox(String xMin, String yMin, String xMax, String yMax)
    {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    public String getXMin()
    {
        return xMin;
    }

    public String getYMin()
    {
        return yMin;
    }

    public String getXMax()
    {
        return xMax;
    }

    public String getYMax()
    {
        return yMax;
    }

    @Override
    public PositionType getType()
    {
        return PositionType.BNDBOX;
    }
}
