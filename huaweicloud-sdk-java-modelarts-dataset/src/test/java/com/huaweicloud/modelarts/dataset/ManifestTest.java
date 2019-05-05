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

import junit.framework.TestCase;
import org.junit.Assert;

import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.*;

public class ManifestTest extends TestCase {

  private String resourcePath = this.getClass().getResource("/").getPath() + "../../../resources/";

  public void testParseManifestClassificationSample() {
    String path = resourcePath + "/classification-xy-V201902220937263726.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateClassification(dataset);
    System.out.println("testParseManifestClassificationSample Success");
  }

  public void testParseManifestClassificationSample2() {
    String path = resourcePath + "/V002.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateClassification2(dataset);
    System.out.println("testParseManifestClassificationSample Success");
  }

  public void testParseManifestClassificationMultiple() {
    String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateClassificationMultiple(dataset);
    System.out.println("testParseManifestClassificationMultiple Success");
  }

  public void testParseManifestClassificationDetection() {
    String path = resourcePath + "/classification-detection-multi-xy-V201902220937263726.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateClassificationDetection(dataset);
    System.out.println("testParseManifestClassificationDetection Success");
  }

  public void testParseManifestDetectionSimple() {
    String path = resourcePath + "/detect-test-xy-V201902220951335133.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionSimple(dataset);
    System.out.println("testParseManifestDetectionSimple Success");
  }

  public void testParseManifestDetectionMultiple() {
    String path = resourcePath + "/detect-multi-xy-V201902220951335133.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultiple(dataset);
    System.out.println("testParseManifestDetectionMultiple Success");
  }

  public void testParseManifestDetectionMultipleAndVOC() throws Exception {
    String path = resourcePath + "/detect-multi-local-voc.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path, true);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOC(dataset);
    System.out.println("testParseManifestDetectionMultipleAndVOC Success");
  }

  public void testParseManifestDetectionMultipleAndVOCGet() throws Exception {
    String path = resourcePath + "/detect-multi-local-voc.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOC(dataset);
    System.out.println("testParseManifestDetectionMultipleAndVOC Success");
  }

}
