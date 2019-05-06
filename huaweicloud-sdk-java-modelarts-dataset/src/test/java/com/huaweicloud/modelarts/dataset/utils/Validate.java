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

import static com.huaweicloud.modelarts.dataset.FieldName.*;
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
        assertNotNull(annotation.getProperty());
        assertEquals(annotation.getConfidence(), 0.8, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 08:2"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateClassificationMultipleFilter(Dataset dataset) {
    assertEquals(dataset.getSize(), 4);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 4);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("Dog".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertNotNull(annotation.getProperty());
        assertEquals(annotation.getConfidence(), 0.8, 0);
        Assert.assertTrue(annotation.isHard());
        Assert.assertTrue(annotation.isHard());
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 08:2"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateClassificationMultipleFilterWithoutHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 12);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 12);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("Dog".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertNotNull(annotation.getProperty());
        assertEquals(annotation.getConfidence(), 0.8, 0);
        if ("s3://obs-ma/test/classification/datafiles/9_1550650986597.jpg".equals(sample.getSource())) {
          Assert.assertTrue(annotation.isHard());
        }
        if ("s3://obs-ma/test/classification/datafiles/5_1550650985477.jpg".equals(sample.getSource())) {
          Assert.assertTrue(!annotation.isHard());
        }
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 08:2"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateClassificationMultipleFilterWithFalseHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 8);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 8);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("Dog".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertNotNull(annotation.getProperty());
        assertEquals(annotation.getConfidence(), 0.8, 0);
        Assert.assertTrue(!annotation.isHard());
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-02-20 08:2"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateClassificationMultipleFilterWithHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 8);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 8);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size() || 2 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("Cat".equals(annotation.getName()) || "Dog".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertNotNull(annotation.getProperty());
        assertEquals(annotation.getConfidence(), 0.8, 0);
        Assert.assertTrue(annotation.isHard());
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
      if ("s3://obs-ma/test/label-0220/datafiles/1 (5)_15506326179922.jpg".equals(sample.getSource())) {
        Assert.assertTrue(2 == annotationList.size());
      } else {
        Assert.assertTrue(1 == annotationList.size());
      }
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/object_detection");
        Assert.assertTrue(annotation.getAnnotationLoc().endsWith(".xml"));
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

  public static void validateTextClassification(Dataset dataset) {
    assertEquals(dataset.getSize(), 6);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 6);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://raw data "));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("TRAIN".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("label1".equals(annotation.getName()) || "label2".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/text_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        Assert.assertTrue("#3399ff".equals(annotation.getProperty().get(PROPERTY_COLOR)));
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-17 10:39:19"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateTextClassificationMultiple(Dataset dataset) {
    assertEquals(dataset.getSize(), 6);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 6);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://raw data "));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("TRAIN".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size() || 2 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("label1".equals(annotation.getName()) || "label2".equals(annotation.getName()));
        Assert.assertTrue("modelarts/text_entity".equals(annotation.getType())
            || "modelarts/text_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        Assert.assertTrue("#3399ff".equals(annotation.getProperty().get(PROPERTY_COLOR)));
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-17 10:39:19"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateTextClassificationMultipleFilter(Dataset dataset) {
    assertEquals(dataset.getSize(), 2);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 2);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://raw data "));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("TRAIN".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("label1".equals(annotation.getName()));
        Assert.assertTrue("modelarts/text_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        Assert.assertTrue("#3399ff".equals(annotation.getProperty().get(PROPERTY_COLOR)));
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.isHard());
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-17 10:39:19"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateTextClassificationMultipleFilterWithoutHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 4);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 4);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://raw data "));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("TRAIN".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("label1".equals(annotation.getName()) || "label2".equals(annotation.getName()));
        Assert.assertTrue("modelarts/text_entity".equals(annotation.getType())
            || "modelarts/text_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        Assert.assertTrue("#3399ff".equals(annotation.getProperty().get(PROPERTY_COLOR)));
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-17 10:39:19"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateTextClassificationMultipleFilterWithFalseHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 2);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 2);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://raw data "));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("TRAIN".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("label1".equals(annotation.getName()) || "label2".equals(annotation.getName()));
        Assert.assertTrue("modelarts/text_entity".equals(annotation.getType())
            || "modelarts/text_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        Assert.assertTrue("#3399ff".equals(annotation.getProperty().get(PROPERTY_COLOR)));
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(!annotation.isHard());
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-17 10:39:19"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateTextEntity(Dataset dataset) {
    assertEquals(dataset.getSize(), 6);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 6);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("TRAIN".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("name".equals(annotation.getName()));
        assertEquals(annotation.getType(), "modelarts/text_entity");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(0 == Integer.parseInt(annotation.getProperty().get(PROPERTY_START_INDEX).toString()));
        Assert.assertTrue(3 <= Integer.parseInt(annotation.getProperty().get(PROPERTY_END_INDEX).toString()));
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-17 11:22:12"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateTextEntityMultiple(Dataset dataset) {
    assertEquals(dataset.getSize(), 8);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 8);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      if (sample.getSource().contains("Bob is from Shenzhen, Jack is from Guangzho")) {
        Assert.assertTrue(4 == annotationList.size());
      } else {
        Assert.assertTrue(2 == annotationList.size());
      }
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("name".equals(annotation.getName()) || "location".equals(annotation.getName()));
        Assert.assertTrue("modelarts/text_entity".equals(annotation.getType())
            || "modelarts/text_entity".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(0 <= Integer.parseInt(annotation.getProperty().get(PROPERTY_START_INDEX).toString()));
        Assert.assertTrue(3 <= Integer.parseInt(annotation.getProperty().get(PROPERTY_END_INDEX).toString()));
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateTextEntityMultipleFilter(Dataset dataset) {
    assertEquals(dataset.getSize(), 4);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 4);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      if (sample.getSource().contains("Bob is from Shenzhen, Jack is from Guangzho")) {
        Assert.assertTrue(2 == annotationList.size());
      } else {
        Assert.assertTrue(1 == annotationList.size());
      }
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("name".equals(annotation.getName()) || "location".equals(annotation.getName()));
        Assert.assertTrue("modelarts/text_entity".equals(annotation.getType())
            || "modelarts/text_entity".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.isHard());
        Assert.assertTrue(0 <= Integer.parseInt(annotation.getProperty().get(PROPERTY_START_INDEX).toString()));
        Assert.assertTrue(3 <= Integer.parseInt(annotation.getProperty().get(PROPERTY_END_INDEX).toString()));
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateTextEntityMultipleFilterWithFalseHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 4);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 4);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("content://"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      if (sample.getSource().contains("Bob is from Shenzhen, Jack is from Guangzho")) {
        Assert.assertTrue(2 == annotationList.size());
      } else {
        Assert.assertTrue(1 == annotationList.size());
      }
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("name".equals(annotation.getName()) || "location".equals(annotation.getName()));
        Assert.assertTrue("modelarts/text_entity".equals(annotation.getType())
            || "modelarts/text_entity".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(!annotation.isHard());
        Assert.assertTrue(0 <= Integer.parseInt(annotation.getProperty().get(PROPERTY_START_INDEX).toString()));
        Assert.assertTrue(3 <= Integer.parseInt(annotation.getProperty().get(PROPERTY_END_INDEX).toString()));
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioClassification(Dataset dataset) {
    assertEquals(dataset.getSize(), 7);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 7);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset1"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("speech".equals(annotation.getName())
            || "program".equals(annotation.getName())
            || "1".equals(annotation.getName())
        );
        assertEquals(annotation.getType(), "modelarts/" + AUDIO_CLASSIFICATION);
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 11:"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioClassificationMultiple(Dataset dataset) {
    assertEquals(dataset.getSize(), 7);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 7);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size() || 2 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("speech".equals(annotation.getName())
            || "program".equals(annotation.getName())
            || "1".equals(annotation.getName())
        );
        Assert.assertTrue("modelarts/Audio_entity".equals(annotation.getType())
            || "modelarts/audio_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 11:"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioClassificationMultipleFilter(Dataset dataset) {
    assertEquals(dataset.getSize(), 2);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 2);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("speech".equals(annotation.getName()));
        Assert.assertTrue("modelarts/Audio_entity".equals(annotation.getType())
            || "modelarts/audio_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.isHard());
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 11:"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioClassificationMultipleFilterMultipleNames(Dataset dataset) {
    assertEquals(dataset.getSize(), 3);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 3);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      if ("s3://modelartscarbon/audio/dataset1/speech3_1556596464879.wav".equals(sample.getSource())) {
        Assert.assertTrue(2 == annotationList.size());
      } else {
        Assert.assertTrue(1 == annotationList.size());
      }
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("speech".equals(annotation.getName())
            || "program".equals(annotation.getName()));
        Assert.assertTrue("modelarts/Audio_entity".equals(annotation.getType())
            || "modelarts/audio_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.isHard());
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 11:"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioClassificationMultipleFilterWithoutHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 4);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 4);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("speech".equals(annotation.getName()));
        Assert.assertTrue("modelarts/Audio_entity".equals(annotation.getType())
            || "modelarts/audio_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 11:"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioClassificationMultipleFilterWithFalseHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 2);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 2);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue("speech".equals(annotation.getName()));
        Assert.assertTrue("modelarts/Audio_entity".equals(annotation.getType())
            || "modelarts/audio_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(!annotation.isHard());
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 11:"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioContent(Dataset dataset) {
    assertEquals(dataset.getSize(), 4);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 4);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset3"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/audio_content");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        String propertyContent = annotation.getProperty().get(PROPERTY_CONTENT).toString();
        Assert.assertTrue("music, di da di da".equals(propertyContent)
            || "Hello world".equals(propertyContent)
            || "every word".equals(propertyContent)
            || "Hello manifest".equals(propertyContent)
        );
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 12:02:30"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioContentFilter(Dataset dataset) {
    assertEquals(dataset.getSize(), 1);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 1);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset3"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/audio_content");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(annotation.isHard());
        String propertyContent = annotation.getProperty().get(PROPERTY_CONTENT).toString();
        Assert.assertTrue("Hello manifest".equals(propertyContent)
        );
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 12:02:30"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioContentFilterWithoutHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 3);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 3);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset3"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/audio_content");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        String propertyContent = annotation.getProperty().get(PROPERTY_CONTENT).toString();
        Assert.assertTrue("music, di da di da".equals(propertyContent)
            || "Hello world".equals(propertyContent)
            || "Hello manifest".equals(propertyContent)
        );
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 12:02:30"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioContentFilterWithFalseHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 2);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 2);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset3"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/audio_content");
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        Assert.assertTrue(!annotation.isHard());
        String propertyContent = annotation.getProperty().get(PROPERTY_CONTENT).toString();
        Assert.assertTrue("music, di da di da".equals(propertyContent)
            || "Hello world".equals(propertyContent)
            || "Hello manifest".equals(propertyContent)
        );
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 12:02:30"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateAudioContentMultiple(Dataset dataset) {
    assertEquals(dataset.getSize(), 4);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 4);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://modelartscarbon/audio/dataset3"));
      assertEquals(sample.getInferenceLoc(), null);
      Assert.assertTrue("train".equals(sample.getUsage()) || "inference".equals(sample.getUsage()));
      List<Annotation> annotationList = sample.getAnnotations();
      Assert.assertTrue(1 == annotationList.size() || 2 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        Assert.assertTrue("modelarts/audio_content".equals(annotation.getType())
            || "modelarts/audio_classification".equals(annotation.getType()));
        assertEquals(annotation.getAnnotationLoc(), null);
        assertEquals(annotation.getConfidence(), 0.0, 0);
        String propertyContent = annotation.getProperty().get(PROPERTY_CONTENT).toString();
        Assert.assertTrue("music, di da di da".equals(propertyContent)
            || "Hello world".equals(propertyContent)
            || "every word".equals(propertyContent)
            || "Hello manifest".equals(propertyContent)
        );
        Assert.assertTrue(annotation.getCreationTime().startsWith("2019-04-30 12:02:30"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), null);
      }
    }
  }

  public static void validateDetectionMultipleAndVOCFilter(Dataset dataset) {
    assertEquals(dataset.getSize(), 3);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 3);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      Assert.assertTrue(sample.getSource().startsWith("s3://obs-ma/test/label-0220/datafiles"));
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      if ("s3://obs-ma/test/label-0220/datafiles/1 (5)_15506326179922.jpg".equals(sample.getSource())) {
        Assert.assertTrue(2 == annotationList.size());
      } else {
        Assert.assertTrue(1 == annotationList.size());
      }
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        Assert.assertTrue(null == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/object_detection");
        Assert.assertTrue(annotation.getAnnotationLoc().endsWith(".xml"));
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

        if ("s3://obs-ma/test/label-0220/datafiles/1 (5)_15506326179922.jpg".equals(sample.getSource())) {
          if ("/Users/xubo/Desktop/xubo/git/dataset/resources/VOC/000000115967_1556247179208.xml".equals(annotation.getAnnotationLoc())
              || "s3://carbonsouth/manifest/voc/000000115967_1556247179208.xml".equals(annotation.getAnnotationLoc())) {
            Assert.assertTrue(!annotation.isHard());
          } else {
            Assert.assertTrue(annotation.isHard());
          }
        }
      }
    }
  }

  public static void validateDetectionMultipleAndVOCFilterWithFalseHard(Dataset dataset) {
    assertEquals(dataset.getSize(), 7);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 7);
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
        Assert.assertTrue(annotation.getAnnotationLoc().endsWith(".xml"));
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
        Assert.assertTrue(!annotation.isHard());
      }
    }
  }

  public static void validateDetectionMultipleAndVOCFilter2(Dataset dataset) {
    assertEquals(dataset.getSize(), 2);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 2);
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
        Assert.assertTrue(annotation.getAnnotationLoc().endsWith(".xml"));
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
        if ("s3://obs-ma/test/label-0220/datafiles/1 (5)_15506326179922.jpg".equals(sample.getSource())) {
          Assert.assertTrue(annotation.isHard());
        }
      }
    }
  }

  public static void validateDetectionMultipleAndVOCFilter3(Dataset dataset) {
    assertEquals(dataset.getSize(), 7);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 7);
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
        Assert.assertTrue(annotation.getAnnotationLoc().endsWith(".xml"));
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

        if ("s3://obs-ma/test/label-0220/datafiles/1 (5)_15506326179922.jpg".equals(sample.getSource())) {
          Assert.assertTrue(annotation.isHard());
        }
      }
    }
  }


}

