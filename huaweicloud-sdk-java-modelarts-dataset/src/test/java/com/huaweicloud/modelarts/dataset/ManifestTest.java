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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATION_HARD;
import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATION_NAMES;
import static com.huaweicloud.modelarts.dataset.FieldName.PARSE_PASCAL_VOC;
import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.*;

public class ManifestTest extends TestCase {

  private String resourcePath = this.getClass().getResource("/").getPath() + "../../../resources/";

  public void testParseManifestImageClassificationSample() {
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

  public void testParseManifestImageClassificationSample2() {
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

  public void testParseManifestImageClassificationMultiple() {
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

  public void testParseManifestImageClassificationDetection() {
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

  public void testParseManifestImageDetectionSimple() {
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

  public void testParseManifestImageDetectionMultiple() {
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

  public void testParseManifestImageDetectionMultipleAndVOC() {
    String path = resourcePath + "/detect-multi-local-voc.manifest";
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(PARSE_PASCAL_VOC, true);
      dataset = parseManifest(path, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOC(dataset);
    System.out.println("testParseManifestDetectionMultipleAndVOC Success");
  }

  public void testParseManifestImageDetectionMultipleAndVOCGet() {
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

  public void testParseManifestTextClassificationSample() {
    String path = resourcePath + "/text_classification.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateTextClassification(dataset);
    System.out.println("testParseManifestTextClassificationSample Success");
  }


  public void testParseManifestTextClassificationMultiple() {
    String path = resourcePath + "/text_classification_multiple_label.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateTextClassificationMultiple(dataset);
    System.out.println("testParseManifestTextClassificationMultiple Success");
  }

  public void testParseManifestTextEntitySample() {
    String path = resourcePath + "/text_entity.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateTextEntity(dataset);
    System.out.println("testParseManifestTextEntitySample Success");
  }


  public void testParseManifestTextEntityMultiple() {
    String path = resourcePath + "/text_entity_duplicate_label.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateTextEntityMultiple(dataset);
    System.out.println("testParseManifestTextEntityMultiple Success");
  }

  public void testParseManifestAudioClassificationSample() {
    String path = resourcePath + "/audio_classification.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateAudioClassification(dataset);
    System.out.println("testParseManifestAudioClassificationSample Success");
  }

  public void testParseManifestAudioClassificationMultiple() {
    String path = resourcePath + "/audio_classification_mutiple_label.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateAudioClassificationMultiple(dataset);
    System.out.println("testParseManifestAudioClassificationMultiple Success");
  }

  public void testParseManifestAudioContentSample() {
    String path = resourcePath + "/audio_content.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateAudioContent(dataset);
    System.out.println("testParseManifestAudioContentSample Success");
  }

  public void testParseManifestAudioContentMultiple() {
    String path = resourcePath + "/audio_content_inference.manifest";
    Dataset dataset = null;
    try {
      dataset = parseManifest(path);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateAudioContentMultiple(dataset);
    System.out.println("testParseManifestAudioContentMultiple Success");
  }

  public void testParseManifestImageDetectionFilterSimple() {
    String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(ANNOTATION_HARD, true);
      properties.put(PARSE_PASCAL_VOC, true);
      dataset = parseManifest(path, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterAnnotationNameSimple() {
    String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(ANNOTATION_HARD, true);
      properties.put(PARSE_PASCAL_VOC, true);
      List annotationNameLists = new ArrayList();
      annotationNameLists.add("person");
      properties.put(ANNOTATION_NAMES, annotationNameLists);
      dataset = parseManifest(path, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter2(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterAnnotationNamesSimple() {
    String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(ANNOTATION_HARD, true);
      properties.put(PARSE_PASCAL_VOC, true);
      List annotationNameLists = new ArrayList();
      annotationNameLists.add("person");
      annotationNameLists.add("greenLight");
      properties.put(ANNOTATION_NAMES, annotationNameLists);
      dataset = parseManifest(path, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterAnnotationName2Simple() {
    String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      properties.put(PARSE_PASCAL_VOC, true);
      List annotationNameLists = new ArrayList();
      annotationNameLists.add("person");
      properties.put(ANNOTATION_NAMES, annotationNameLists);
      dataset = parseManifest(path, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter3(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterAnnotationName3Simple() {
    String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
    Dataset dataset = null;
    try {
      Map properties = new HashMap();
      List annotationNameLists = new ArrayList();
      annotationNameLists.add("person");
      properties.put(ANNOTATION_NAMES, annotationNameLists);
      dataset = parseManifest(path, properties);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.assertTrue(false);
    }
    validateDetectionMultipleAndVOCFilter3(dataset);
    System.out.println("testParseManifestImageDetectionFilterSimple Success");
  }

  public void testParseManifestImageDetectionFilterMultiple() {
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
}
