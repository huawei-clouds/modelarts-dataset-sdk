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

import java.util.LinkedHashMap;

import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.*;

public class ManifestWriteTest extends TestCase {

  public void testWriteManifest() {
    LinkedHashMap map = new LinkedHashMap();
    map.put("source", "s3://southcarbon/1.jpg");
    map.put("annotation", "s3://southcarbon/1.txt");
    map.put("inference-loc", "s3://southcarbon/1.txt");
    map.put("znference-loc", "s3://southcarbon/1.txt");
    String value = map.toString();
    System.out.println(value);
    String value1 = value.replace("=", "\":\"").replace(", ", "\",\"").replace("{", "{\"").replace("}", "\"}");
    System.out.println(value1);
    JSONObject jsonObject = new JSONObject(true);
    jsonObject.put("source", "s3://southcarbon/1.jpg");
    jsonObject.put("annotation", "s3://southcarbon/1.txt");
    jsonObject.put("inference-loc", "s3://southcarbon/1.txt");
    jsonObject.put("znference-loc", "s3://southcarbon/1.txt");
    String value2 = jsonObject.toString();
    System.out.println(value2);

    JSONObject jsonObject3 = new JSONObject(new LinkedHashMap());
    jsonObject3.put("source", "s3://southcarbon/1.jpg");
    jsonObject3.put("annotation", "s3://southcarbon/1.txt");
    jsonObject3.put("inference-loc", "s3://southcarbon/1.txt");
    jsonObject3.put("znference-loc", "s3://southcarbon/1.txt");
    String value3 = jsonObject3.toString();
    System.out.println(value3);

    JSONObject jsonObject4 = new JSONObject(map);
    String value4 = jsonObject4.toString();
    System.out.println(value4);
    assert value1.startsWith("{\"source\":");
    assert value2.startsWith("{\"source\":");
    assert value3.startsWith("{\"source\":");
    assert value4.startsWith("{\"source\":");
  }

  public void testWriteManifestClassificationSample() throws Exception {
    String path = this.getClass().getResource("/").getPath() + "../../../resources/classification-xy-V201902220937263726.manifest";
    String path2 = this.getClass().getResource("/").getPath() + "../../../resources/classification-xy-V201902220937263726_2.manifest";
    Dataset dataset = parseManifest(path);
    validateClassification(dataset);
    dataset.save(path2);
    Dataset dataset2 = parseManifest(path2);
    validateClassification(dataset2);
    System.out.println("testParseManifestClassificationSample Success");
  }

  public void testParseManifestDetectionSample() throws Exception {
    String path = this.getClass().getResource("/").getPath() + "../../../resources/detect-test-xy-V201902220951335133.manifest";
    String path2 = this.getClass().getResource("/").getPath() + "../../../resources/detect-test-xy-V201902220951335133_6.manifest";
    Dataset dataset = parseManifest(path);
    validateDetectionSimple(dataset);
    dataset.save(path2);
    Dataset dataset2 = parseManifest(path2);
    validateDetectionSimple(dataset2);
    System.out.println("testParseManifestDetectionSample Success");
  }
}
