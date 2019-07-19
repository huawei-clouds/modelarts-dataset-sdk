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

package com.huaweicloud.modelarts.dataset;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * sample of manifest
 */
public class Sample
{
    /**
     * source path of raw data
     * Mandatory field
     */
    private String source;
    
    /**
     * source type of raw data
     * Optional field
     */
    private String sourceType;
    
    /**
     * usage of this sample, like "TRAIN", "EVAL", "TEST", "inference"
     * Optional field
     */
    private String usage;
    
    /**
     * inference location
     * Optional field
     */
    private String inferenceLoc;
    
    /**
     * annotation list
     * Optional field
     */
    private List<Annotation> annotations;
    
    /**
     * Source property, like the schema of data
     * Optional field
     */
    private Map<String, Object> property;
    
    private List<Schema> schema;
    
    /**
     * sample id
     * Optional field
     */
    private String id;
    
    public Sample()
    {
    }
    
    public Sample(String source)
    {
        this.source = source;
    }
    
    public Sample(String source, String sourceType, Map<String, Object> property, String usage, String inferenceLoc,
        List<Annotation> annotations, String id)
    {
        this.source = source;
        this.sourceType = sourceType;
        this.property = property;
        this.usage = usage;
        this.inferenceLoc = inferenceLoc;
        this.annotations = annotations;
        this.id = id;
        if (property != null)
        {
            this.schema = new ArrayList<Schema>();
            JSONObject jsonObject = (JSONObject)property.get(FieldName.PROPERTY_SCHEMA);
            if (jsonObject != null)
            {
                for (String key : jsonObject.keySet())
                {
                    this.schema.add(new Schema(key, jsonObject.getString(key)));
                }
            }
        }
    }
    
    public Sample(String source, String sourceType, String usage, List<Schema> schema)
    {
        this.source = source;
        this.sourceType = sourceType;
        this.usage = usage;
        this.schema = schema;
    }
    
    public String getSource()
    {
        return source;
    }
    
    public void setSource(String source)
    {
        this.source = source;
    }
    
    public String getSourceType()
    {
        return sourceType;
    }
    
    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }
    
    public String getUsage()
    {
        return usage;
    }
    
    public Map<String, Object> getProperty()
    {
        return property;
    }
    
    public void setProperty(JSONObject property)
    {
        this.property = property;
    }
    
    public List<Schema> getSchema()
    {
        return schema;
    }
    
    public void setSchema(List<Schema> schema)
    {
        this.schema = schema;
    }
    
    public void setUsage(String usage)
    {
        this.usage = usage;
    }
    
    public String getInferenceLoc()
    {
        return inferenceLoc;
    }
    
    public void setInferenceLoc(String inferenceLoc)
    {
        this.inferenceLoc = inferenceLoc;
    }
    
    public List<Annotation> getAnnotations()
    {
        return annotations;
    }
    
    public void setAnnotations(List<Annotation> annotations)
    {
        this.annotations = annotations;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    @Override
    public String toString()
    {
        return "Sample{" +
            "source='" + source + '\'' +
            ", usage='" + usage + '\'' +
            ", inferenceLoc='" + inferenceLoc + '\'' +
            ", annotations=" + annotations +
            ", id='" + id + '\'' +
            '}';
    }
}
