/*
 * Copyright 2018 Deep Learning Service of Huawei Cloud. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.modelarts.dataset.format.voc.position;

import com.huaweicloud.modelarts.dataset.FieldName;

import static com.huaweicloud.modelarts.dataset.format.voc.position.PositionType.POINT;

public class Point implements Position
{
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
    public Point(String xName, String xValue, String yName, String yValue)
    {
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
    public Point(String xValue, String yValue)
    {
        this.xValue = xValue;
        this.yValue = yValue;
        this.xName = FieldName.X;
        this.yName = FieldName.Y;
    }
    
    public String getXName()
    {
        return xName;
    }
    
    public String getXValue()
    {
        return xValue;
    }
    
    public String getYName()
    {
        return yName;
    }
    
    public String getYValue()
    {
        return yValue;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        Point point = ((Point)obj);
        if (this == point)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        return point.xName.equals(this.xName)
            && point.xValue.equals(this.xValue)
            && point.yName.equals(this.yName)
            && point.yValue.equals(this.yValue);
    }
    
    @Override
    public PositionType getType()
    {
        return POINT;
    }
}
