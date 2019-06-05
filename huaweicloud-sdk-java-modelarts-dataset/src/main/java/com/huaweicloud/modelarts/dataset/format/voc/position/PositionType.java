package com.huaweicloud.modelarts.dataset.format.voc.position;

public enum PositionType
{
    POINT("point"), DASHED("dashed"), LINE("line"), CIRCLE("circle"), BNDBOX("bndbox"), POLYGON("polygon");
    
    private String value;
    
    PositionType(String value)
    {
        this.value = value;
    }
    
}
