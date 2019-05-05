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
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATION_HARD;
import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATION_NAMES;
import static com.huaweicloud.modelarts.dataset.FieldName.PARSE_PASCAL_VOC;
import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.*;
import static com.huaweicloud.modelarts.dataset.utils.Validate.validateDetectionMultiple;
import static com.huaweicloud.modelarts.dataset.utils.Validate.validateDetectionMultipleAndVOCFilter3;

public class ManifestOBSWithClientTest {


  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      throw new RuntimeException("Please input access_key, secret_key, end_point, <parsePascalVOC> for reading obs files! ");
    }
    String path1 = "s3a://carbonsouth/manifest/detect-multi-s3-voc.manifest";
    String path2 = "s3a://carbonsouth/manifest/detect-multi-s3-voc-filter.manifest";
    String path3 = "s3a://carbonsouth/manifest/detect-multi-xy-V201902220951335133.manifest";

    String ak = args[0];
    String sk = args[1];
    String endPoint = args[2];
    ObsClient obsClient = new ObsClient(ak, sk, endPoint);
    Dataset dataset = null;
    // parse Pascal VOC xml file when parse manifest
    Map properties = new HashMap();
    properties.put(PARSE_PASCAL_VOC, true);
    dataset = parseManifest(path1, obsClient, properties);
    validateDetectionMultipleAndVOC(dataset);

    // it should throw exception when parsing obs files without obsClient.
    Dataset dataset2 = parseManifest(path1, obsClient);
    try {
      validateDetectionMultipleAndVOC(dataset2);
      Assert.assertTrue(false);
    } catch (Exception e) {
      Assert.assertTrue(e.getMessage().contains(
          "Please use getPascalVoc(ObsClient obsClient) because reading S3 file need obeClient."));
    }

    // parse Pascal VOC xml file after parse manifest, when getPascalVOC
    Dataset dataset3 = parseManifest(path1, obsClient);
    validateDetectionMultipleAndVOCGetWithObsClient(dataset3, obsClient);

    validateDetectionMultipleAndVOCGetWithObsClient(parseManifest(path2, obsClient), obsClient);
    ManifestOBSWithClientTest test = new ManifestOBSWithClientTest();
    test.testParseManifestImageDetectionFilterSimple(path2, obsClient);
    test.testParseManifestImageDetectionFilterAnnotationNameSimple(path2, obsClient);
    test.testParseManifestImageDetectionFilterAnnotationNameSimple(path2, obsClient);
    test.testParseManifestImageDetectionFilterAnnotationNamesSimple(path2, obsClient);
    test.testParseManifestImageDetectionFilterAnnotationName2Simple(path2, obsClient);
    test.testParseManifestImageDetectionFilterAnnotationName3Simple(path2, obsClient);
    test.testParseManifestImageDetectionFilterMultiple(path3, obsClient);
    System.out.println("Success");
  }

  public void testParseManifestImageDetectionFilterSimple(String path, ObsClient obsClient) {
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(ANNOTATION_HARD, true);
      properties.put(PARSE_PASCAL_VOC, true);
      dataset = parseManifest(path, obsClient, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterAnnotationNameSimple(String path, ObsClient obsClient) {
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(ANNOTATION_HARD, true);
      properties.put(PARSE_PASCAL_VOC, true);
      List annotationNameLists = new ArrayList();
      annotationNameLists.add("person");
      properties.put(ANNOTATION_NAMES, annotationNameLists);
      dataset = parseManifest(path, obsClient, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter2(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterAnnotationNamesSimple(String path, ObsClient obsClient) {
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(ANNOTATION_HARD, true);
      properties.put(PARSE_PASCAL_VOC, true);
      List annotationNameLists = new ArrayList();
      annotationNameLists.add("person");
      annotationNameLists.add("greenLight");
      properties.put(ANNOTATION_NAMES, annotationNameLists);
      dataset = parseManifest(path, obsClient, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterAnnotationName2Simple(String path, ObsClient obsClient) {
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(PARSE_PASCAL_VOC, true);
      List annotationNameLists = new ArrayList();
      annotationNameLists.add("person");
      properties.put(ANNOTATION_NAMES, annotationNameLists);
      dataset = parseManifest(path, obsClient, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter3(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterAnnotationName3Simple(String path, ObsClient obsClient) {
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      List annotationNameLists = new ArrayList();
      annotationNameLists.add("person");
      properties.put(ANNOTATION_NAMES, annotationNameLists);
      dataset = parseManifest(path, obsClient, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter3(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterMultiple(String path, ObsClient obsClient) {
    Dataset dataset = null;
    try {
      dataset = parseManifest(path, obsClient);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultiple(dataset);
    System.out.println("testParseManifestDetectionMultiple Success");
  }
}
