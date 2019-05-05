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

package com.huaweicloud.modelarts.dataset.utils;

import com.huaweicloud.modelarts.dataset.Annotation;
import com.huaweicloud.modelarts.dataset.Dataset;
import com.huaweicloud.modelarts.dataset.Sample;
import com.obs.services.ObsClient;
import org.junit.Assert;

import java.util.List;

import static com.huaweicloud.modelarts.dataset.format.voc.PascalVocIOTest.validateVOC;
import static com.huaweicloud.modelarts.dataset.format.voc.PascalVocIOTest.validateVOCMultipleObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Validate {
  public static void validateClassification(Dataset dataset) {
    assertEquals(dataset.getSize(), 19);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 19);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("Cat".equals(annotation.getName()) || "Dog".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        Assert.assertTrue("black".equals(annotation.getProperty().get("color")));
        assertEquals(annotation.getConfidence(), 0.8, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 08:2"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateClassification2(Dataset dataset) {
    assertEquals(dataset.getSize(), 10);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 10);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/flowers"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("sunflowers".equals(annotation.getName()) || "daisy".equals(annotation.getName())
            || "tulips".equals(annotation.getName())
            || "dandelion".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        Assert.assertTrue(annotation.getProperty().isEmpty());
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-03-30 17:22"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateClassificationMultiple(Dataset dataset) {
    assertEquals(dataset.getSize(), 19);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 19);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(2 == annotationList.size() || 1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("Cat".equals(annotation.getName()) || "Dog".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        // TODO: validate the property with value
        assertNotNull(annotation.getProperty());
        assertEquals(annotation.getConfidence(), 0.8, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 08:2"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateClassificationDetection(Dataset dataset) {
    assertEquals(dataset.getSize(), 5);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 5);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue(null == sample.getId() || sample.getId().startsWith("XGDVG"));
      Assert.assertTrue(sample.getUsage().equalsIgnoreCase("TRAIN") || sample.getUsage().equalsIgnoreCase("INFERENCE"));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(null == annotationList || 2 == annotationList.size());
      if (null != annotationList) {
        for (int j = 0; j < annotationList.size(); j++) {
          Annotation annotation = annotationList.get(j);
          Assert.assertTrue(null == annotation.getName() || "Cat".equals(annotation.getName()));
          Assert.assertTrue(annotation.getType().equalsIgnoreCase("modelarts/object_detection")
              || annotation.getType().equalsIgnoreCase("modelarts/image_classification"));
          Assert.assertTrue(null == annotation.getAnnotationLoc() || annotation.getAnnotationLoc().startsWith("s3://path/manifest/data/2007_0"));
          Assert.assertTrue(null == annotation.getProperty() || annotation.getProperty().isEmpty());
          Assert.assertTrue(0.8 == annotation.getConfidence() || 0 == annotation.getConfidence());
          Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20"));
          assertEquals(annotation.getAnnotatedBy(), "human");
          Assert.assertTrue(null == annotation.getAnnotationFormat() || annotation.getAnnotationFormat().equalsIgnoreCase("PASCAL VOC"));
        }
      }
    }
  }

  public static void validateDetectionSimple(Dataset dataset) {
    assertEquals(dataset.getSize(), 8);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 8);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/label-0220/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/object_detection");
        Assert.assertTrue(annotation.getAnnotationLoc().startsWith("s3://path/manifest/data/2007_0"));
        // TODO: validate the property with value
        assertEquals(annotation.getProperty(), null);
        assertEquals(annotation.getConfidence(), 0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 03:16"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), "PASCAL VOC");
      }
    }
  }

  public static void validateDetectionMultiple(Dataset dataset) {
    assertEquals(dataset.getSize(), 8);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 8);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/label-0220/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size() || 2 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/object_detection");
        Assert.assertTrue(annotation.getAnnotationLoc().startsWith("s3://path/manifest/data/2007_0"));
        // TODO: validate the property with value
        assertEquals(annotation.getProperty(), null);
        assertEquals(annotation.getConfidence(), 0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 03:16"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), "PASCAL VOC");
      }
    }
  }

  public static void validateDetectionMultipleAndVOC(Dataset dataset) {
    assertEquals(dataset.getSize(), 8);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 8);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/label-0220/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size() || 2 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/object_detection");
        Assert.assertTrue(annotation.getAnnotationLoc().endsWith(".xml"));
        // TODO: validate the property with value
        assertEquals(annotation.getProperty(), null);
        assertEquals(annotation.getConfidence(), 0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 03:16"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), "PASCAL VOC");
        if (annotation.getAnnotationLoc().endsWith("2007_000027.xml")) {
          validateVOC(annotation.getPascalVoc());
        } else if (annotation.getAnnotationLoc().endsWith("000000115967_1556247179208.xml")) {
          validateVOCMultipleObject(annotation.getPascalVoc());
        } else {
          Assert.assertTrue(false);
        }
      }
    }
  }

  public static void validateDetectionMultipleAndVOCGetWithObsClient(Dataset dataset, ObsClient obsClient) {
    assertEquals(dataset.getSize(), 8);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 8);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/label-0220/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size() || 2 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/object_detection");
        Assert.assertTrue(annotation.getAnnotationLoc().endsWith(".xml"));
        // TODO: validate the property with value
        assertEquals(annotation.getProperty(), null);
        assertEquals(annotation.getConfidence(), 0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 03:16"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), "PASCAL VOC");
        if (annotation.getAnnotationLoc().endsWith("2007_000027.xml")) {
          validateVOC(annotation.getPascalVoc(obsClient));
        } else if (annotation.getAnnotationLoc().endsWith("000000115967_1556247179208.xml")) {
          validateVOCMultipleObject(annotation.getPascalVoc(obsClient));
        } else {
          Assert.assertTrue(false);
        }
      }
    }
  }
}
