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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Validate {
  public static void validateClassification(Dataset dataset) {
    assertEquals(dataset.getSize(), 19);
    List<Sample> sampleList = dataset.getSamples();
    assertEquals(sampleList.size(), 19);
    for (int i = 0; i < sampleList.size(); i++) {
      Sample sample = sampleList.get(i);
      assert sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles");
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      assertEquals(annotationList.size(), 1);
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        assert ("Cat" == annotation.getName() || "Dog" == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        // TODO: validate the property with value
        assert (annotation.getProperty().isEmpty());
        assertEquals(annotation.getConfidence(), 0.8, 0);
        assert (annotation.getCreationTime().startsWith("2019-02-20 08:23"));
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
      assert sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles");
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      assert (2 == annotationList.size() || 1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        assert ("Cat" == annotation.getName() || "Dog" == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/image_classification");
        assertEquals(annotation.getAnnotationLoc(), null);
        // TODO: validate the property with value
        assertNotNull(annotation.getProperty());
        assertEquals(annotation.getConfidence(), 0.8, 0);
        assert (annotation.getCreationTime().startsWith("2019-02-20 08:23"));
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
      assert sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles");
      assertEquals(sample.getInferenceLoc(), null);
      assert sample.getId().startsWith("XGDVG");
      assert (sample.getUsage().equalsIgnoreCase("TRAIN") || sample.getUsage().equalsIgnoreCase("INFERENCE"));
      List<Annotation> annotationList = sample.getAnnotations();
      assert (2 == annotationList.size() || 1 == annotationList.size());
      if (null != annotationList) {
        for (int j = 0; j < annotationList.size(); j++) {
          Annotation annotation = annotationList.get(j);
          assert ("Cat" == annotation.getName() || "Dog" == annotation.getName());
          assert (annotation.getType().equalsIgnoreCase("modelarts/object_detection")
              || annotation.getType().equalsIgnoreCase("modelarts/image_classification"));
          assert (annotation.getAnnotationLoc().startsWith("s3://path/manifest/data/2007_0"));
          // TODO: validate the property with value
          assert (null == annotation.getProperty() || annotation.getProperty().isEmpty());
          assert (0.8 == annotation.getConfidence() || 0 == annotation.getConfidence());
          assert (annotation.getCreationTime().startsWith("2019-02-20 08:23"));
          assertEquals(annotation.getAnnotatedBy(), "human");
          assert (null == annotation.getAnnotationFormat() || annotation.getAnnotationFormat().equalsIgnoreCase("PASCAL VOC"));
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
      assert sample.getSource().startsWith("s3://obs-ma/test/classification/datafiles");
      assertEquals(sample.getInferenceLoc(), null);
      assertEquals(sample.getUsage(), "TRAIN");
      List<Annotation> annotationList = sample.getAnnotations();
      assert (1 == annotationList.size());
      for (int j = 0; j < annotationList.size(); j++) {
        Annotation annotation = annotationList.get(j);
        assert ("Cat" == annotation.getName() || "Dog" == annotation.getName());
        assertEquals(annotation.getType(), "modelarts/object_detection");
        assert (annotation.getAnnotationLoc().startsWith("s3://path/manifest/data/2007_0"));
        // TODO: validate the property with value
        assertEquals(annotation.getProperty(), null);
        assertEquals(annotation.getConfidence(), 0, 0);
        assert (annotation.getCreationTime().startsWith("2019-02-20 08:23"));
        assertEquals(annotation.getAnnotatedBy(), "human");
        assertEquals(annotation.getAnnotationFormat(), "PASCAL VOC");
      }
    }
  }


}
