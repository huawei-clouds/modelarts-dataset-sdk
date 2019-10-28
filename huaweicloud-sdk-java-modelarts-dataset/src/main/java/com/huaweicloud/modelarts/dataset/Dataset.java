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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.huaweicloud.modelarts.dataset.FieldName.*;
import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATIONS;

/**
 * Dataset class for manifest
 */
public class Dataset
{
    /**
     * the number of sample in manifest/dataset
     */
    private int size;
    
    /**
     * sample list in manifest/dataset
     */
    private List<Sample> samples;
    
    public Dataset()
    {
        samples = new ArrayList<Sample>();
    }
    
    public Dataset(List<Sample> samples)
    {
        this.samples = samples;
    }
    
    public Dataset(int size, List<Sample> samples)
    {
        this.size = size;
        this.samples = samples;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public void setSize(int size)
    {
        this.size = size;
    }
    
    public List<Sample> getSamples()
    {
        return samples;
    }
    
    public void setSamples(List<Sample> samples)
    {
        this.samples = samples;
    }
    
    /**
     * Add a sample into dataset object
     *
     * @param sample sample of manifest
     */
    public void addSample(Sample sample)
    {
        this.samples.add(sample);
    }
    
    /**
     * put obs into json object
     *
     * @param jsonObject
     * @param key
     * @param value
     */
    private void put(JSONObject jsonObject, String key, Object value)
    {
        if (null != value)
        {
            jsonObject.put(key, value);
        }
    }
    
    /**
     * convert sample to Json string
     *
     * @param sample sample of manifest
     * @return sample json string
     */
    private String toJSONString(Sample sample)
    {
        JSONObject jsonObject = new JSONObject(true);
        put(jsonObject, ID, sample.getId());
        put(jsonObject, SOURCE, sample.getSource());
        put(jsonObject, SOURCE_TYPE, sample.getSourceType());
        put(jsonObject, USAGE, sample.getUsage());
        List<Schema> schema = sample.getSchema();
        Map property = sample.getProperty();
        if (null != schema)
        {
            if (null == property)
            {
                property = new JSONObject();
            }
            JSONObject jsonObject1 = new JSONObject(true);
            for (int i = 0; i < schema.size(); i++)
            {
                jsonObject1.put(schema.get(i).getName(), schema.get(i).getType());
            }
            property.put(PROPERTY_SCHEMA, jsonObject1);
        }
        
        put(jsonObject, SOURCE_PROPERTY, property);
        
        put(jsonObject, INFERENCE_LOC, sample.getInferenceLoc());
        
        if (null != sample.getAnnotations())
        {
            jsonObject.put(ANNOTATIONS, toJSONString(sample.getAnnotations()));
        }
        return jsonObject.toJSONString();
    }
    
    /**
     * convert annotation list to JSON Array
     *
     * @param annotations annotation list
     * @return json array of annotation list
     */
    private JSONArray toJSONString(List<Annotation> annotations)
    {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < annotations.size(); i++)
        {
            JSONObject jsonObject = new JSONObject(true);
            Annotation annotation = annotations.get(i);
            put(jsonObject, FieldName.ANNOTATION_NAME, annotation.getName());
            put(jsonObject, FieldName.ANNOTATION_LOC, annotation.getAnnotationLoc());
            put(jsonObject, FieldName.ANNOTATION_TYPE, annotation.getType());
            put(jsonObject, FieldName.ANNOTATION_CONFIDENCE, annotation.getConfidence());
            put(jsonObject, FieldName.ANNOTATION_PROPERTY, annotation.getProperty());
            put(jsonObject, FieldName.ANNOTATION_HARD, annotation.isHard());
            put(jsonObject, FieldName.ANNOTATION_ANNOTATED_BY, annotation.getAnnotatedBy());
            put(jsonObject, FieldName.ANNOTATION_CREATION_TIME, annotation.getCreationTime());
            put(jsonObject, FieldName.ANNOTATION_FORMAT, annotation.getAnnotationFormat());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
    
    /**
     * save the dataset to local as json format manifest
     *
     * @param path manifest output file path
     * @throws IOException
     */
    public void save(String path)
        throws IOException
    {
        BufferedWriter bufferedWriter = null;
        try
        {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
            for (int i = 0; i < samples.size(); i++)
            {
                String jsonStr = toJSONString(samples.get(i));
                bufferedWriter.write(jsonStr + "\n");
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            bufferedWriter.close();
        }
    }
    
    // TODO: support OBS
    
    /**
     * save the dataset to OBS as json format manifest
     *
     * @param path manifest output file path
     * @throws IOException
     */
    public void save(String path, String access_key, String secret_key, String end_point)
        throws IOException
    {
        BufferedWriter bufferedWriter = null;
        try
        {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
            for (int i = 0; i < samples.size(); i++)
            {
                String jsonStr = toJSONString(samples.get(i));
                bufferedWriter.write(jsonStr + "\n");
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            bufferedWriter.close();
        }
    }
    
    @Override
    public String toString()
    {
        return "Dataset{" +
            "size=" + size +
            ", samples=" + samples +
            '}';
    }
}
