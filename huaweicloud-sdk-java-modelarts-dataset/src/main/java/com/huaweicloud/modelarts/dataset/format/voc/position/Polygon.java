package com.huaweicloud.modelarts.dataset.format.voc.position;

import java.util.ArrayList;
import java.util.List;

public class Polygon implements Position
{
    private List<Point> pointList;

    public List<Point> addPoint(Point point)
    {
        if (null == pointList)
        {
            pointList = new ArrayList<Point>();
        }
        pointList.add(point);
        return pointList;
    }

    public List<Point> getPoints()
    {
        return pointList;
    }

    @Override
    public PositionType getType()
    {
        return PositionType.POLYGON;
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }
}

