package com.huaweicloud.modelarts.dataset.format.voc.position;

public interface Position
{
    /**
     * get the position type, like point, line, circle, bndbox.
     *
     * @return
     */
    public PositionType getType();
}
