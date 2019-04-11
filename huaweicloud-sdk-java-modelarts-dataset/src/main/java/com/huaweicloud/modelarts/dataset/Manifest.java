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

import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.huaweicloud.modelarts.dataset.Constants.*;
import static com.huaweicloud.modelarts.dataset.FiledName.*;

/**
 * main class for parse manifest file
 */
public class Manifest {
  /**
   * judge whether is S3 path
   *
   * @param path manifest path
   * @return true if path is S3 path, other return false.
   */
  public static boolean isS3(String path) {
    if (path.toLowerCase().startsWith(S3_PREFIX) || path.toLowerCase().startsWith(S3N_PREFIX)
        || path.toLowerCase().startsWith(S3A_PREFIX)) {
      return true;
    } else if (path.toLowerCase().startsWith(HDFS_PREFIX)) {
      throw new RuntimeException("Don't support HDFS now! Only support Local and S3");
    } else {
      return false;
    }
  }

  /**
   * read manifest data from local and return with dataset format for manifest
   *
   * @param path manifest path
   * @return dataset object for manifest
   * @throws IOException
   */
  public static Dataset readFromLocal(String path) throws IOException {
    File file = new File(path);
    InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "GBK");
    BufferedReader bufferedReader = new BufferedReader(reader);
    String line;
    Dataset dataset = new Dataset();
    int sum = 0;
    while ((line = bufferedReader.readLine()) != null) {
      dataset.addSample(parseSample(line));
      sum++;
    }
    dataset.setSize(sum);
    return dataset;
  }

  /**
   * get string from json object by key
   *
   * @param jsonObject json object
   * @param key        key name
   * @return value if key exists in json object, return null if key doesn't exist in json object
   */
  public static String getString(JSONObject jsonObject, String key) {
    if (jsonObject.isNull(key)) {
      return null;
    } else {
      return jsonObject.getString(key);
    }
  }

  /**
   * get double from json object by key
   *
   * @param jsonObject json object
   * @param key        key name
   * @return value if key exists in json object, return null if key doesn't exist in json object
   */
  public static Double getDouble(JSONObject jsonObject, String key) {
    if (jsonObject.isNull(key)) {
      LOGGER.warn("Confidence is null in json object, so set confidence as 0.0");
      return 0.0;
    } else {
      return jsonObject.getDouble(key);
    }
  }

  /**
   * get json array from json object by key
   *
   * @param jsonObject json object
   * @param key        key name
   * @return json array if key exists in json object, return null if key doesn't exist in json object
   */
  public static JSONArray getJSONArray(JSONObject jsonObject, String key) {
    if (jsonObject.isNull(key)) {
      return null;
    } else {
      return jsonObject.getJSONArray(key);
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
  public static JSONObject getJSONObject(JSONObject jsonObject, String key) {
    if (jsonObject.isNull(key)) {
      return null;
    } else {
      try {
        return jsonObject.getJSONObject(key);
      } catch (JSONException e) {
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
  public static List<Annotation> parseAnnotations(JSONArray jsonArray) {
    List<Annotation> annotationList = new ArrayList<Annotation>();
    if (jsonArray == null) {
      return null;
    }
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = (JSONObject) jsonArray.get(i);
      annotationList.add(new Annotation(getString(jsonObject, NAME),
          getString(jsonObject, ANNOTATION_TYPE),
          getString(jsonObject, ANNOTATION_LOC),
          getJSONObject(jsonObject, ANNOTATION_PROPERTY),
          getDouble(jsonObject, ANNOTATION_CONFIDENCE),
          getString(jsonObject, ANNOTATION_CREATION_TIME),
          getString(jsonObject, ANNOTATION_ANNOTATED_BY),
          getString(jsonObject, ANNOTATION_FORMAT)));
    }
    return annotationList;
  }

  /**
   * parse sample by line string of manifest
   *
   * @param line line string in manifest
   * @return sample object
   */
  public static Sample parseSample(String line) {
    JSONObject jObject = new JSONObject(line);

    Sample sample = new Sample(getString(jObject, SOURCE),
        getString(jObject, FiledName.USAGE),
        getString(jObject, FiledName.INFERENCE_LOC),
        parseAnnotations(getJSONArray(jObject, FiledName.ANNOTATIONS)),
        getString(jObject, FiledName.ID)
    );
    return sample;
  }

  /**
   * parse manifest by manifest path
   *
   * @param path manifest path
   * @return Dataset of manifest
   * @throws Exception
   */
  public static Dataset parseManifest(String path) throws Exception {
    if (isS3(path)) {
      throw new Exception("Please input access_key, secret_key and end_point for reading obs files!");
    } else {
      return readFromLocal(path);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(Manifest.class.getName());

  /**
   * get bucketname and objectkey fro path
   *
   * @param path manifest path
   * @return bucketname and objectkey array
   */
  public static String[] getBucketNameAndObjectKey(String path) {
    int index = 0;
    if (path.toLowerCase().startsWith(S3A_PREFIX) || path.toLowerCase().startsWith(S3N_PREFIX)) {
      index = 6;
    } else {
      index = 5;
    }
    String pathWithoutPrefix = path.substring(index, path.length());
    String[] arr = pathWithoutPrefix.split(SEPARATOR);
    String[] result = new String[2];
    result[0] = arr[0];
    result[1] = path.substring(index + arr[0].length() + 1, path.length());
    return result;
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
  public static Dataset parseManifest(String path, String access_key, String secret_key, String end_point) throws IOException {
    if (!isS3(path)) {
      LOGGER.warn("Even though configure access_key, secret_key and end_point, but path is not S3 path, so it will read data from local! ");
      return readFromLocal(path);
    } else {
      ObsClient obsClient = new ObsClient(access_key, secret_key, end_point);
      String[] result = getBucketNameAndObjectKey(path);
      ObsObject obsObject = obsClient.getObject(result[0], result[1]);
      InputStream content = obsObject.getObjectContent();
      Dataset dataset = new Dataset();
      int sum = 0;
      if (content != null) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
          dataset.addSample(parseSample(line));
          sum++;
        }
        reader.close();
      }
      dataset.setSize(sum);
      return dataset;
    }
  }

}
