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
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.LinkedHashMap;

import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.*;

public class ManifestWriteTest extends TestCase {
  private String resourcePath = this.getClass().getResource("/").getPath();
  private String outputPath = this.getClass().getResource("/").getPath();

  public void testWriteManifest() {
    LinkedHashMap map = new LinkedHashMap();
    map.put("source", "s3://southcarbon/1.jpg");
    map.put("annotation", "s3://southcarbon/1.txt");
    map.put("inference-loc", "s3://southcarbon/1.txt");
    map.put("znference-loc", "s3://southcarbon/1.txt");
    String value = map.toString();
    String value1 = value.replace("=", "\":\"").replace(", ", "\",\"").replace("{", "{\"").replace("}", "\"}");
    JSONObject jsonObject = new JSONObject(true);
    jsonObject.put("source", "s3://southcarbon/1.jpg");
    jsonObject.put("annotation", "s3://southcarbon/1.txt");
    jsonObject.put("inference-loc", "s3://southcarbon/1.txt");
    jsonObject.put("znference-loc", "s3://southcarbon/1.txt");
    String value2 = jsonObject.toString();

    JSONObject jsonObject3 = new JSONObject(new LinkedHashMap());
    jsonObject3.put("source", "s3://southcarbon/1.jpg");
    jsonObject3.put("annotation", "s3://southcarbon/1.txt");
    jsonObject3.put("inference-loc", "s3://southcarbon/1.txt");
    jsonObject3.put("znference-loc", "s3://southcarbon/1.txt");
    String value3 = jsonObject3.toString();

    JSONObject jsonObject4 = new JSONObject(map);
    String value4 = jsonObject4.toString();
    assert value1.startsWith("{\"source\":");
    assert value2.startsWith("{\"source\":");
    assert value3.startsWith("{\"source\":");
    assert value4.startsWith("{\"source\":");
  }

  public void testWriteManifestClassificationSample() {
    String path = resourcePath + "/classification-xy-V201902220937263726.manifest";
    String path2 = outputPath + "/classification-xy-V201902220937263726_2.manifest";
    Dataset dataset = null;
    Dataset dataset2 = null;

    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }

    validateClassification(dataset);
    try {
      dataset.save(path2);
      dataset2 = parseManifest(path2);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateClassification(dataset2);
    System.out.println("testParseManifestClassificationSample Success");
  }

  public void testParseManifestDetectionSample() {
    String path = resourcePath + "/detect-test-xy-V201902220951335133.manifest";
    String path2 = outputPath + "/detect-test-xy-V201902220951335133_6.manifest";
    Dataset dataset = null;
    Dataset dataset2 = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionSimple(dataset);
    try {
      dataset.save(path2);
      dataset2 = parseManifest(path2);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionSimple(dataset2);
    System.out.println("testParseManifestDetectionSample Success");
  }
}
