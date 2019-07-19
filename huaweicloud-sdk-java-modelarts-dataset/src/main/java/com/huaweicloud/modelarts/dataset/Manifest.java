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
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.modelarts.dataset.format.voc.PascalVocIO;
import com.huaweicloud.modelarts.dataset.format.voc.VOCObject;
import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.huaweicloud.modelarts.dataset.Constants.*;
import static com.huaweicloud.modelarts.dataset.FieldName.*;
import static com.huaweicloud.modelarts.dataset.util.OBSUtil.getBucketNameAndObjectKey;

/**
 * main class for parse manifest file
 */
public class Manifest
{
    /**
     * judge whether is S3 path
     *
     * @param path manifest path
     * @return true if path is S3 path, other return false.
     */
    public static boolean isS3(String path)
    {
        if (path.toLowerCase().startsWith(S3_PREFIX) || path.toLowerCase().startsWith(S3N_PREFIX)
            || path.toLowerCase().startsWith(S3A_PREFIX))
        {
            return true;
        }
        else if (path.toLowerCase().startsWith(HDFS_PREFIX))
        {
            throw new RuntimeException("Don't support HDFS now! Only support Local and S3");
        }
        else
        {
            return false;
        }
    }
    
    /**
     * read manifest data from local and return with dataset format for manifest
     *
     * @param path       manifest path
     * @param properties the properties of parsing manifest. like:
     *                   1.properties.put(ANNOTATION_HARD, true);
     *                   filter samples that have hard annotation (isHard in annotation or pascalVOC difficult is 1)
     *                   2.properties.put(PARSE_PASCAL_VOC, true);
     *                   it will parse VOC file if set true, otherwise will not parse VOC file.
     *                   3.List annotationNameLists = new ArrayList();
     *                   annotationNameLists.add("person");
     *                   properties.put(ANNOTATION_NAMES, annotationNameLists)
     *                   filter samples which annotation name in annotationNameLists
     * @return dataset object for manifest
     * @throws IOException
     */
    private static Dataset readFromLocal(String path, Map properties)
        throws IOException
    {
        File file = new File(path);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "GBK");
        BufferedReader bufferedReader = null;
        Dataset dataset = new Dataset();
        try
        {
            bufferedReader = new BufferedReader(reader);
            String line;
            int sum = 0;
            properties = addRelativePath(properties, path);
            while ((line = bufferedReader.readLine()) != null)
            {
                Sample sample = parseSample(line, properties, null);
                if (null != sample)
                {
                    dataset.addSample(sample);
                    sum++;
                }
            }
            dataset.setSize(sum);
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            bufferedReader.close();
        }
        return dataset;
    }
    
    private static Map addRelativePath(Map properties, String relativePath)
    {
        if (null == properties)
        {
            properties = new HashMap();
        }
        properties.put(RELATIVE_PATH, relativePath.substring(0, relativePath.lastIndexOf('/')));
        return properties;
    }
    
    /**
     * get double from json object by key
     *
     * @param jsonObject json object
     * @param key        key name
     * @return value if key exists in json object, return null if key doesn't exist in json object
     */
    private static Double getDouble(JSONObject jsonObject, String key)
    {
        if (null == jsonObject.getString(key))
        {
            LOGGER.warn("Confidence is null in json object, so set confidence as 0.0");
            return 0.0;
        }
        else
        {
            return jsonObject.getDouble(key);
        }
    }
    
    /**
     * get json object by key
     * For compatible the property
     *
     * @param jsonObject json object
     * @param key        key
     * @return true json object if the result is json object, empty json object if it has exception
     */
    private static JSONObject getJSONObject(JSONObject jsonObject, String key)
    {
        if (null == jsonObject.getString(key))
        {
            return null;
        }
        else
        {
            try
            {
                return jsonObject.getJSONObject(key);
            }
            catch (JSONException e)
            {
                LOGGER.warn("It should be json object");
                return new JSONObject();
            }
        }
    }
    
    /**
     * parse annotation by json Array
     *
     * @param jsonArray json Array
     * @return annotation list
     */
    private static List<Annotation> parseAnnotations(JSONArray jsonArray, Map properties, ObsClient obsClient)
    {
        List<Annotation> annotationList = new ArrayList<Annotation>();
        if (jsonArray == null)
        {
            return null;
        }
        for (int i = 0; i < jsonArray.size(); i++)
        {
            JSONObject jsonObject = (JSONObject)jsonArray.get(i);
            String annotationLoc = getString(jsonObject, ANNOTATION_LOC, ANNOTATION_LOC2);
            if (null != annotationLoc && null != properties && annotationLoc.startsWith(RELATIVE_PATH_PREFIX))
            {
                annotationLoc = properties.get(RELATIVE_PATH) + annotationLoc.substring(1, annotationLoc.length());
            }
            
            Annotation annotation = new Annotation(jsonObject.getString(NAME),
                jsonObject.getString(ANNOTATION_TYPE),
                annotationLoc,
                getJSONObject(jsonObject, ANNOTATION_PROPERTY),
                getDouble(jsonObject, ANNOTATION_CONFIDENCE),
                getString(jsonObject, ANNOTATION_CREATION_TIME, ANNOTATION_CREATION_TIME2),
                getString(jsonObject, ANNOTATION_ANNOTATED_BY, ANNOTATION_ANNOTATED_BY2),
                getString(jsonObject, ANNOTATION_FORMAT, ANNOTATION_FORMAT2),
                Boolean.parseBoolean(jsonObject.getString(ANNOTATION_HARD)));
            if (null != properties && 0 != properties.size() &&
                Boolean.parseBoolean(String.valueOf(properties.get(PARSE_PASCAL_VOC)))
                && null != annotationLoc)
            {
                if (!isS3(annotationLoc))
                {
                    annotation.setPascalVoc(new PascalVocIO(annotationLoc));
                }
                else
                {
                    annotation.setPascalVoc(new PascalVocIO(annotationLoc, obsClient));
                }
            }
            
            if (null != properties && 0 != properties.size())
            {
                if (Boolean.parseBoolean(String.valueOf(properties.get(ANNOTATION_HARD))))
                {
                    if (isHard(annotation, obsClient))
                    {
                        if (isFilterAnnotations(annotation, properties, obsClient))
                        {
                            annotationList.add(annotation);
                        }
                    }
                }
                else if ("false".equals(String.valueOf(properties.get(ANNOTATION_HARD)).toLowerCase()))
                {
                    if (isNotHard(annotation, obsClient))
                    {
                        if (isFilterAnnotations(annotation, properties, obsClient))
                        {
                            annotationList.add(annotation);
                        }
                    }
                }
                else
                {
                    if (isFilterAnnotations(annotation, properties, obsClient))
                    {
                        annotationList.add(annotation);
                    }
                }
            }
            else
            {
                annotationList.add(annotation);
            }
        }
        return annotationList;
    }
    
    public static boolean isFilterAnnotations(Annotation annotation, Map properties, ObsClient obsClient)
    {
        if (null != properties && 0 != properties.size() &&
            null != properties.get(ANNOTATION_NAMES))
        {
            List annotationNames = (List)properties.get(ANNOTATION_NAMES);
            if (annotationNames.contains(annotation.getName()))
            {
                return true;
            }
            else
            {
                PascalVocIO pascalVocIO = annotation.getPascalVoc(obsClient);
                if (null != pascalVocIO)
                {
                    List<VOCObject> vocObjects = pascalVocIO.getVocObjects();
                    for (int i = 0; i < vocObjects.size(); i++)
                    {
                        VOCObject vocObject = vocObjects.get(i);
                        if (annotationNames.contains(vocObject.getName()))
                        {
                            return true;
                        }
                    }
                }
                if (null != annotation.getProperty())
                {
                    String propertyContent = String.valueOf(annotation.getProperty().get(PROPERTY_CONTENT));
                    if (annotationNames.contains(propertyContent))
                    {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }
    
    private static boolean isHard(Annotation annotation, ObsClient obsClient)
    {
        if (annotation.isHard())
        {
            return true;
        }
        PascalVocIO pascalVocIO = annotation.getPascalVoc(obsClient);
        if (null != pascalVocIO)
        {
            List<VOCObject> vocObjects = pascalVocIO.getVocObjects();
            for (int i = 0; i < vocObjects.size(); i++)
            {
                VOCObject vocObject = vocObjects.get(i);
                if (1 == Integer.parseInt(vocObject.getDifficult()))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static boolean isNotHard(Annotation annotation, ObsClient obsClient)
    {
        if (annotation.isHard())
        {
            return false;
        }
        PascalVocIO pascalVocIO = annotation.getPascalVoc(obsClient);
        if (null != pascalVocIO)
        {
            List<VOCObject> vocObjects = pascalVocIO.getVocObjects();
            for (int i = 0; i < vocObjects.size(); i++)
            {
                VOCObject vocObject = vocObjects.get(i);
                if (0 == Integer.parseInt(vocObject.getDifficult()))
                {
                    return true;
                }
            }
        }
        if (!annotation.isHard())
        {
            return true;
        }
        return false;
    }
    
    private static String getString(JSONObject jObject, String key1, String key2)
    {
        String value = jObject.getString(key1);
        if (null == value)
        {
            value = jObject.getString(key2);
        }
        return value;
    }
    
    /**
     * parse sample by line string of manifest
     *
     * @param line line string in manifest
     * @return sample object
     */
    private static Sample parseSample(String line, Map properties, ObsClient obsClient)
    {
        JSONObject jObject = JSONObject.parseObject(line);
        
        List<Annotation> annotationList =
            parseAnnotations(jObject.getJSONArray(FieldName.ANNOTATIONS), properties, obsClient);
        if (0 != properties.size() && (Boolean.parseBoolean(String.valueOf(properties.get(ANNOTATION_HARD)))
            || "false".equals(String.valueOf(properties.get(ANNOTATION_HARD)).toLowerCase())
            || null != properties.get(ANNOTATION_NAMES)))
        {
            if (annotationList.size() > 0)
            {
                return new Sample(jObject.getString(SOURCE),
                    jObject.getString(SOURCE_TYPE),
                    jObject.getJSONObject(SOURCE_PROPERTY),
                    jObject.getString(FieldName.USAGE),
                    getString(jObject, INFERENCE_LOC, INFERENCE_LOC2),
                    annotationList,
                    jObject.getString(FieldName.ID)
                );
            }
            else
            {
                return null;
            }
        }
        else
        {
            return new Sample(jObject.getString(SOURCE),
                jObject.getString(SOURCE_TYPE),
                jObject.getJSONObject(SOURCE_PROPERTY),
                jObject.getString(FieldName.USAGE),
                getString(jObject, INFERENCE_LOC, INFERENCE_LOC2),
                annotationList,
                jObject.getString(FieldName.ID)
            );
        }
    }
    
    /**
     * parse manifest by manifest path
     * default properties value is false.
     *
     * @param path manifest path
     * @return Dataset of manifest
     * @throws Exception
     */
    public static Dataset parseManifest(String path)
        throws Exception
    {
        return parseManifest(path, new HashMap());
    }
    
    /**
     * parse manifest by manifest path
     *
     * @param path       manifest path
     * @param properties the properties of parsing manifest. like:
     *                   1.properties.put(ANNOTATION_HARD, true);
     *                   filter samples that have hard annotation (isHard in annotation or pascalVOC difficult is 1)
     *                   2.properties.put(PARSE_PASCAL_VOC, true);
     *                   it will parse VOC file if set true, otherwise will not parse VOC file.
     *                   3.List annotationNameLists = new ArrayList();
     *                   annotationNameLists.add("person");
     *                   properties.put(ANNOTATION_NAMES, annotationNameLists)
     *                   filter samples which annotation name in annotationNameLists
     * @return Dataset of manifest
     * @throws Exception
     */
    public static Dataset parseManifest(String path, Map properties)
        throws Exception
    {
        if (isS3(path))
        {
            throw new Exception("Please input access_key, secret_key and end_point for reading obs files!");
        }
        else
        {
            return readFromLocal(path, properties);
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger(Manifest.class.getName());
    
    /**
     * parse manifest from S3, with obsClient.
     *
     * @param path       manifest path
     * @param obsClient  obsClient, already config ak, sk and endpoint
     * @param properties the properties of parsing manifest. like:
     *                   1.properties.put(ANNOTATION_HARD, true);
     *                   filter samples that have hard annotation (isHard in annotation or pascalVOC difficult is 1)
     *                   2.properties.put(PARSE_PASCAL_VOC, true);
     *                   it will parse VOC file if set true, otherwise will not parse VOC file.
     *                   3.List annotationNameLists = new ArrayList();
     *                   annotationNameLists.add("person");
     *                   properties.put(ANNOTATION_NAMES, annotationNameLists)
     *                   filter samples which annotation name in annotationNameLists
     * @return Dataset of manifest
     * @throws IOException
     */
    private static Dataset readFromOBS(String path, ObsClient obsClient, Map properties)
        throws IOException
    {
        String[] result = getBucketNameAndObjectKey(path);
        ObsObject obsObject = obsClient.getObject(result[0], result[1]);
        InputStream content = obsObject.getObjectContent();
        Dataset dataset = new Dataset();
        int sum = 0;
        properties = addRelativePath(properties, path);
        if (content != null)
        {
            BufferedReader reader = null;
            try
            {
                reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    Sample sample = parseSample(line, properties, obsClient);
                    if (null != sample)
                    {
                        dataset.addSample(sample);
                        sum++;
                    }
                }
            }
            catch (IOException e)
            {
                throw e;
            }
            finally
            {
                reader.close();
            }
        }
        dataset.setSize(sum);
        return dataset;
    }
    
    /**
     * parse manifest from S3, with access_key, secret_key and end_point.
     * It will parse manifest from local if the path is local, even though configure access_key, secret_key and end_point.
     *
     * @param path       manifest path
     * @param access_key access_key of OBS
     * @param secret_key secret_key of OBS
     * @param end_point  end_point of OBS
     * @return Dataset of manifest
     * @throws IOException
     */
    public static Dataset parseManifest(String path, String access_key, String secret_key, String end_point)
        throws IOException
    {
        return parseManifest(path, access_key, secret_key, end_point, new HashMap());
    }
    
    /**
     * parse manifest from S3, with access_key, secret_key and end_point.
     * It will parse manifest from local if the path is local, even though configure access_key, secret_key and end_point.
     *
     * @param path       manifest path
     * @param access_key access_key of OBS
     * @param secret_key secret_key of OBS
     * @param end_point  end_point of OBS
     * @param properties the properties of parsing manifest. like:
     *                   1.properties.put(ANNOTATION_HARD, true);
     *                   filter samples that have hard annotation (isHard in annotation or pascalVOC difficult is 1)
     *                   2.properties.put(PARSE_PASCAL_VOC, true);
     *                   it will parse VOC file if set true, otherwise will not parse VOC file.
     *                   3.List annotationNameLists = new ArrayList();
     *                   annotationNameLists.add("person");
     *                   properties.put(ANNOTATION_NAMES, annotationNameLists)
     *                   filter samples which annotation name in annotationNameLists
     * @return Dataset of manifest
     * @throws IOException
     */
    public static Dataset parseManifest(String path, String access_key, String secret_key,
        String end_point, Map properties)
        throws IOException
    {
        if (!isS3(path))
        {
            LOGGER.warn(
                "Even though configure access_key, secret_key and end_point, but path is not S3 path, so it will read data from local! ");
            return readFromLocal(path, properties);
        }
        else
        {
            ObsClient obsClient = new ObsClient(access_key, secret_key, end_point);
            return readFromOBS(path, obsClient, properties);
        }
    }
    
    /**
     * parse manifest from S3, with obsClient.
     * It will parse manifest from local if the path is local, even though configure access_key, secret_key and end_point.
     *
     * @param path      manifest path
     * @param obsClient obsClient, already config ak, sk and endpoint
     * @return Dataset of manifest
     * @throws IOException
     */
    public static Dataset parseManifest(String path, ObsClient obsClient)
        throws IOException
    {
        return parseManifest(path, obsClient, new HashMap());
    }
    
    /**
     * parse manifest from S3, with obsClient.
     * It will parse manifest from local if the path is local, even though configure access_key, secret_key and end_point.
     *
     * @param path       manifest path
     * @param obsClient  obsClient, already config ak, sk and endpoint
     * @param properties the properties of parsing manifest. like:
     *                   1.properties.put(ANNOTATION_HARD, true);
     *                   filter samples that have hard annotation (isHard in annotation or pascalVOC difficult is 1)
     *                   2.properties.put(PARSE_PASCAL_VOC, true);
     *                   it will parse VOC file if set true, otherwise will not parse VOC file.
     *                   3.List annotationNameLists = new ArrayList();
     *                   annotationNameLists.add("person");
     *                   properties.put(ANNOTATION_NAMES, annotationNameLists)
     *                   filter samples which annotation name in annotationNameLists
     * @return Dataset of manifest
     * @throws IOException
     */
    public static Dataset parseManifest(String path, ObsClient obsClient, Map properties)
        throws IOException
    {
        if (!isS3(path))
        {
            LOGGER.warn(
                "Even though configure access_key, secret_key and end_point, but path is not S3 path, so it will read data from local! ");
            return readFromLocal(path, properties);
        }
        else
        {
            return readFromOBS(path, obsClient, properties);
        }
    }
    
}
