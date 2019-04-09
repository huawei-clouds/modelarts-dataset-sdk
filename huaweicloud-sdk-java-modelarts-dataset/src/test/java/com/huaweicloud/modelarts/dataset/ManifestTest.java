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

import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.*;

public class ManifestTest extends TestCase {
  public void testParseManifestClassificationSample() throws Exception {
    String path = this.getClass().getResource("/").getPath() + "../../../resources/classification-xy-V201902220937263726.manifest";
    Dataset dataset = parseManifest(path);
    validateClassification(dataset);
    System.out.println("testParseManifestClassificationSample Success");
  }

  public void testParseManifestClassificationMultiple() throws Exception {
    String path = this.getClass().getResource("/").getPath() + "../../../resources/classification-multi-xy-V201902220937263726.manifest";
    Dataset dataset = parseManifest(path);
    validateClassificationMultiple(dataset);
    System.out.println("testParseManifestClassificationMultiple Success");
  }

  public void testParseManifestClassificationDetection() throws Exception {
    String path = this.getClass().getResource("/").getPath() + "../../../resources/classification-detection-multi-xy-V201902220937263726.manifest";
    Dataset dataset = parseManifest(path);
    validateClassificationDetection(dataset);
    System.out.println("testParseManifestClassificationDetection Success");
  }

  public void testParseManifestDetectionSimple() throws Exception {
    String path = this.getClass().getResource("/").getPath() + "../../../resources/detect-test-xy-V201902220951335133.manifest";
    Dataset dataset = parseManifest(path);
    validateDetectionSimple(dataset);
    System.out.println("testParseManifestDetectionSimple Success");
  }

  public void testParseManifestDetectionMultiple() throws Exception {
    String path = this.getClass().getResource("/").getPath() + "../../../resources/detect-multi-xy-V201902220951335133.manifest";
    Dataset dataset = parseManifest(path);
    validateDetectionSimple(dataset);
    System.out.println("testParseManifestDetectionMultiple Success");
  }
}
