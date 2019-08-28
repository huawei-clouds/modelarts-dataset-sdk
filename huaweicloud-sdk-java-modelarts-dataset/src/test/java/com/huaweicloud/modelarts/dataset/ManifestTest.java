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

import com.huaweicloud.modelarts.dataset.format.voc.PascalVocIO;
import com.huaweicloud.modelarts.dataset.format.voc.VOCObject;
import com.huaweicloud.modelarts.dataset.format.voc.position.BNDBox;
import junit.framework.TestCase;
import org.apache.carbondata.common.exceptions.sql.InvalidLoadOptionException;
import org.apache.carbondata.core.metadata.datatype.DataTypes;
import org.apache.carbondata.sdk.file.CarbonReader;
import org.apache.carbondata.sdk.file.CarbonWriter;
import org.apache.carbondata.sdk.file.Field;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATION_CONFIDENCE;
import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATION_HARD;
import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATION_HARD_COEFFICIENT;
import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATION_NAMES;
import static com.huaweicloud.modelarts.dataset.FieldName.PARSE_PASCAL_VOC;
import static com.huaweicloud.modelarts.dataset.Manifest.getAnnotations;
import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.*;
import static org.apache.carbondata.core.util.path.CarbonTablePath.CARBON_DATA_EXT;
import static org.apache.carbondata.sdk.file.utils.SDKUtil.listFiles;

public class ManifestTest extends TestCase
{
    
    private String resourcePath = this.getClass().getResource("/").getPath();
    
    public void testParseManifestImageClassificationSample()
    {
        String path = resourcePath + "/classification-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassification(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestCarbonDataImageClassificationSample()
        throws IOException, InterruptedException
    {
        String path = resourcePath + "/carbon/ImageClassificationV003/V003.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        List<Sample> sampleList = dataset.getSamples();
        Assert.assertTrue(sampleList.size() == 1);
        List files = new ArrayList();
        String resourcesPath = new File(this.getClass().getResource("/").getPath() + ".").getCanonicalPath();
        for (int i = 0; i < sampleList.size(); i++)
        {
            Sample sample = sampleList.get(i);
            Assert.assertTrue("carbondata".equalsIgnoreCase(sample.getSourceType()));
            Assert.assertTrue(sample.getSchema().size() == 5);
            files.add(resourcesPath + "/" + sample.getSource());
        }
        List<Schema> schemas = sampleList.get(0).getSchema();
        String[] projection = new String[schemas.size()];
        for (int i = 0; i < schemas.size(); i++)
        {
            projection[i] = schemas.get(i).getName();
        }
        CarbonReader reader = CarbonReader
            .builder(files.get(0).toString().substring(0, Math.max(files.get(0).toString().lastIndexOf("/"), 1)))
            .projection(projection)
            .withFileLists(files)
            .build();
        List<Sample> carbonSamples = new ArrayList<Sample>();
        while (reader.hasNext())
        {
            Object[] row = (Object[])reader.readNextRow();
            List<Annotation> annotationList = getAnnotations(row[4].toString(), null);
            System.out.println(row[0] + " " + row[1]);
            Sample sample = new Sample(row[1].toString(),
                "carbondata",
                sampleList.get(0).getProperty(),
                row[3].toString(),
                null,
                annotationList,
                row[0].toString());
            sample.setSourceContent((byte[])row[2]);
            carbonSamples.add(sample);
        }
        Assert.assertTrue(carbonSamples.size() == 15);
        for (int i = 0; i < carbonSamples.size(); i++)
        {
            Sample sample = carbonSamples.get(i);
            Assert.assertTrue(sample.getSource() != null);
            Assert.assertTrue(sample.getSourceType().equalsIgnoreCase("carbondata"));
            Assert.assertTrue(sample.getUsage().equalsIgnoreCase("train"));
            Assert.assertTrue(sample.getSchema().size() == 5);
            Assert.assertTrue(sample.getProperty() != null);
            for (int j = 0; j < sample.getAnnotations().size(); j++)
            {
                Annotation annotation = sample.getAnnotations().get(j);
                Assert.assertTrue(annotation.getName() != null);
                Assert.assertTrue(annotation.getType().equalsIgnoreCase("modelarts/image_classification"));
                Assert.assertTrue(annotation.getCreationTime() != null);
                Assert.assertTrue(annotation.getAnnotatedBy() != null);
            }
        }
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestCarbonDataObjectDetectionSample()
        throws IOException, InterruptedException
    {
        String path = resourcePath + "/carbon/ObjectDetectionV002/V002.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        List<Sample> sampleList = dataset.getSamples();
        Assert.assertTrue(sampleList.size() == 1);
        List files = new ArrayList();
        String resourcesPath = new File(this.getClass().getResource("/").getPath() + ".").getCanonicalPath();
        for (int i = 0; i < sampleList.size(); i++)
        {
            Sample sample = sampleList.get(i);
            Assert.assertTrue("carbondata".equalsIgnoreCase(sample.getSourceType()));
            Assert.assertTrue(sample.getSchema().size() == 5);
            files.add(resourcesPath + "/" + sample.getSource());
        }
        List<Schema> schemas = sampleList.get(0).getSchema();
        String[] projection = new String[schemas.size()];
        for (int i = 0; i < schemas.size(); i++)
        {
            projection[i] = schemas.get(i).getName();
        }
        CarbonReader reader = CarbonReader
            .builder(files.get(0).toString().substring(0, Math.max(files.get(0).toString().lastIndexOf("/"), 1)))
            .projection(projection)
            .withFileLists(files)
            .build();
        List<Sample> carbonSamples = new ArrayList<Sample>();
        while (reader.hasNext())
        {
            Object[] row = (Object[])reader.readNextRow();
            List<Annotation> annotationList = getAnnotations(row[4].toString(), null);
            System.out.println(row[0] + " " + row[1]);
            carbonSamples.add(new Sample(row[1].toString(),
                "carbondata",
                sampleList.get(0).getProperty(),
                row[3].toString(),
                null,
                annotationList,
                row[0].toString()));
        }
        Assert.assertTrue(carbonSamples.size() == 5);
        for (int i = 0; i < carbonSamples.size(); i++)
        {
            Sample sample = carbonSamples.get(i);
            Assert.assertTrue(sample.getSource() != null);
            Assert.assertTrue(sample.getSourceType().equalsIgnoreCase("carbondata"));
            Assert.assertTrue(sample.getUsage().equalsIgnoreCase("train"));
            Assert.assertTrue(sample.getSchema().size() == 5);
            Assert.assertTrue(sample.getProperty() != null);
            Assert.assertTrue(sample.getAnnotations().size() == 1);
            for (int j = 0; j < sample.getAnnotations().size(); j++)
            {
                Annotation annotation = sample.getAnnotations().get(j);
                Assert.assertTrue(annotation.getName() == null);
                Assert.assertTrue(annotation.getType().equalsIgnoreCase("modelarts/object_detection"));
                Assert.assertTrue(annotation.getCreationTime() != null);
                Assert.assertTrue(annotation.getAnnotatedBy() != null);
                PascalVocIO pascalVocIO = new PascalVocIO();
                pascalVocIO = pascalVocIO.parseXMLValue(annotation.getAnnotationLoc());
                Assert.assertTrue(pascalVocIO.getFolder().equalsIgnoreCase("images"));
                Assert.assertTrue(pascalVocIO.getFileName().contains("ILSVRC2012_val"));
                Assert.assertTrue(pascalVocIO.getSource().getDatabase().contains("Unknown"));
                Assert.assertTrue(pascalVocIO.getWidth() != null);
                Assert.assertTrue(pascalVocIO.getHeight() != null);
                Assert.assertTrue(pascalVocIO.getDepth().equalsIgnoreCase("3"));
                Assert.assertTrue(pascalVocIO.getSegmented().equalsIgnoreCase("0"));
                Assert.assertTrue(pascalVocIO.getVocObjects().size() >= 1);
                for (int k = 0; k < pascalVocIO.getVocObjects().size(); k++)
                {
                    VOCObject vocObject = pascalVocIO.getVocObjects().get(k);
                    Assert.assertTrue(vocObject.getName() != null);
                    Assert.assertTrue(vocObject.getPose().equalsIgnoreCase("Unspecified"));
                    Assert.assertTrue(vocObject.getTruncated().equalsIgnoreCase("0"));
                    Assert.assertTrue(vocObject.getOccluded().equalsIgnoreCase("0"));
                    Assert.assertTrue(vocObject.getDifficult().equalsIgnoreCase("0"));
                    BNDBox bndBox = (BNDBox)vocObject.getPosition();
                    Assert.assertTrue(bndBox.getXMin() != null);
                    Assert.assertTrue(bndBox.getYMin() != null);
                    Assert.assertTrue(bndBox.getXMax() != null);
                    Assert.assertTrue(bndBox.getYMax() != null);
                }
            }
        }
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationSample2()
    {
        String path = resourcePath + "/V002.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassification2(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationMultiple()
    {
        String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultiple(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationDetection()
    {
        String path = resourcePath + "/classification-detection-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationDetection(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionSimple()
    {
        String path = resourcePath + "/detect-test-xy-V201902220951335133_1.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionSimple(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionMultiple()
    {
        String path = resourcePath + "/detect-multi-xy-V201902220951335133.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultiple(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionMultipleAndVOC()
    {
        String path = resourcePath + "/detect-multi-local-voc.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(PARSE_PASCAL_VOC, true);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOC(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionMultipleAndVOCGet()
    {
        String path = resourcePath + "/detect-multi-local-voc.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOC(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextClassificationSample()
    {
        String path = resourcePath + "/text_classification.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassification(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextClassificationMultiple()
    {
        String path = resourcePath + "/text_classification_multiple_label.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultiple(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextEntitySample()
    {
        String path = resourcePath + "/text_entity.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextEntity(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextEntityMultiple()
    {
        String path = resourcePath + "/text_entity_duplicate_label.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextEntityMultiple(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioClassificationSample()
    {
        String path = resourcePath + "/audio_classification.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassification(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioClassificationMultiple()
    {
        String path = resourcePath + "/audio_classification_mutiple_label.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultiple(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioContentSample()
    {
        String path = resourcePath + "/audio_content.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioContent(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioContentMultiple()
    {
        String path = resourcePath + "/audio_content_inference.manifest";
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioContentMultiple(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionFilterSimple()
    {
        String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionFilterWithFalseHard()
    {
        String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilterWithFalseHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionFilterAnnotationNameSimple()
    {
        String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("person");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter2(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionFilterAnnotationNamesSimple()
    {
        String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("person");
            annotationNameLists.add("greenLight");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionFilterAnnotationName2Simple()
    {
        String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("person");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter3(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageDetectionFilterAnnotationName3Simple()
    {
        String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("person");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter3(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNames()
    {
        String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("Dog");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilter(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesLowerCase()
    {
        String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("dog");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        Assert.assertTrue(0 == dataset.getSize());
        Assert.assertTrue(0 == dataset.getSamples().size());
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesWithoutHard()
    {
        String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("Dog");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilterWithoutHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesWithFalseHard()
    {
        String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("Dog");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilterWithFalseHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesWithHard()
    {
        String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilterWithHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesWithoutParseVOC()
    {
        String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, false);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("Dog");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilter(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextClassificationFilterAnnotationNames()
    {
        String path = resourcePath + "/text_classification_multiple_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("label1");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultipleFilter(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextClassificationFilterAnnotationNamesWithoutHard()
    {
        String path = resourcePath + "/text_classification_multiple_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("label1");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultipleFilterWithFalseHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextClassificationFilterAnnotationNamesWithoutHardVOC()
    {
        String path = resourcePath + "/text_classification_multiple_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(PARSE_PASCAL_VOC, false);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("label1");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultipleFilterWithoutHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextClassificationFilterAnnotationNamesWithFalseHardVOC()
    {
        String path = resourcePath + "/text_classification_multiple_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            properties.put(PARSE_PASCAL_VOC, false);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("label1");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultipleFilterWithFalseHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextEntityMultipleFilter()
    {
        String path = resourcePath + "/text_entity_duplicate_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("name");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextEntityMultipleFilter(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestTextEntityMultipleFilterWithFalseHard()
    {
        String path = resourcePath + "/text_entity_duplicate_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("name");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextEntityMultipleFilterWithFalseHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioClassificationMultipleFilter()
    {
        String path = resourcePath + "/audio_classification_mutiple_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("speech");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultipleFilter(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioClassificationMultipleFilterMultipleNames()
    {
        String path = resourcePath + "/audio_classification_mutiple_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("speech");
            annotationNameLists.add("program");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultipleFilterMultipleNames(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioClassificationMultipleFilterWithoutHard()
    {
        String path = resourcePath + "/audio_classification_mutiple_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("speech");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultipleFilterWithoutHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioClassificationMultipleFilterWithFalseHard()
    {
        String path = resourcePath + "/audio_classification_mutiple_label.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("speech");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultipleFilterWithFalseHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioContentSampleFilter()
    {
        String path = resourcePath + "/audio_content.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("Hello manifest");
            annotationNameLists.add("music, di da di da");
            annotationNameLists.add("Hello world");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioContentFilter(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioContentSampleFilterWithoutHard()
    {
        String path = resourcePath + "/audio_content.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("Hello manifest");
            annotationNameLists.add("music, di da di da");
            annotationNameLists.add("Hello world");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioContentFilterWithoutHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    public void testParseManifestAudioContentSampleFilterWithFalseHard()
    {
        String path = resourcePath + "/audio_content.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("Hello manifest");
            annotationNameLists.add("music, di da di da");
            annotationNameLists.add("Hello world");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioContentFilterWithFalseHard(dataset);
        System.out.println(this.getName() + " Success");
    }
    
    String imagePath = resourcePath + "./image/carbondatalogo.jpg";
    
    String carbonPath = "./target/binary";
    
    String manifestPath = "./target/binary" + System.currentTimeMillis() + ".manifest";
    
    @BeforeClass
    public void setUp()
        throws IOException, InvalidLoadOptionException
    {
        int num = 3;
        int rows = 10;
        try
        {
            FileUtils.deleteDirectory(new File(carbonPath));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Field[] fields = new Field[5];
        fields[0] = new Field("name", DataTypes.STRING);
        fields[1] = new Field("age", DataTypes.INT);
        fields[2] = new Field("image1", DataTypes.BINARY);
        fields[3] = new Field("image2", DataTypes.BINARY);
        fields[4] = new Field("image3", DataTypes.BINARY);
        
        byte[] originBinary = null;
        
        // read and write image data
        for (int j = 0; j < num; j++)
        {
            CarbonWriter writer = CarbonWriter
                .builder()
                .outputPath(carbonPath)
                .withCsvInput(new org.apache.carbondata.sdk.file.Schema(fields))
                .writtenBy("SDKS3Example")
                .withPageSizeInMb(1)
                .build();
            
            for (int i = 0; i < rows; i++)
            {
                // read image and encode to Hex
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(imagePath));
                originBinary = new byte[bis.available()];
                while ((bis.read(originBinary)) != -1)
                {
                }
                // write data
                writer.write(new Object[] {"robot" + (i % 10), i, originBinary, originBinary, originBinary});
                bis.close();
            }
            writer.close();
        }
    }
    
    @Test
    public void testWriteWithByteArrayDataType()
        throws IOException, InterruptedException
    {
        
        CarbonReader reader = CarbonReader
            .builder(carbonPath, "_temp")
            .build();
        
        System.out.println("\nData:");
        int i = 0;
        while (i < 3 && reader.hasNext())
        {
            Object[] row = (Object[])reader.readNextRow();
            
            byte[] outputBinary = (byte[])row[1];
            byte[] outputBinary2 = (byte[])row[2];
            byte[] outputBinary3 = (byte[])row[3];
            System.out.println(row[0] + " " + row[1] + " image1 size:" + outputBinary.length
                + " image2 size:" + outputBinary2.length + " image3 size:" + outputBinary3.length);
            
            for (int k = 0; k < 3; k++)
            {
                
                byte[] originBinaryTemp = (byte[])row[1 + k];
                // validate output binary data and origin binary data
                assert (originBinaryTemp.length == outputBinary.length);
                for (int j = 0; j < originBinaryTemp.length; j++)
                {
                    assert (originBinaryTemp[j] == outputBinary[j]);
                }
                
                // save image, user can compare the save image and original image
                String destString = "./target/binary/image" + k + "_" + i + ".jpg";
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destString));
                bos.write(originBinaryTemp);
                bos.close();
            }
            i++;
        }
        System.out.println("\nFinished");
        reader.close();
    }
    
    @Test
    public void testWriteManifestForCarbon()
        throws Exception
    {
        ArrayList carbonList = listFiles(carbonPath, CARBON_DATA_EXT);
        Dataset dataset = new Dataset();
        for (int i = 0; i < carbonList.size(); i++)
        {
            List<Schema> schema = new ArrayList<Schema>();
            schema.add(new Schema("name", "string"));
            schema.add(new Schema("age", "int"));
            schema.add(new Schema("image1", DataTypes.BINARY.toString()));
            schema.add(new Schema("image2", DataTypes.BINARY.toString()));
            schema.add(new Schema("image3", DataTypes.BINARY.toString()));
            Sample sample = new Sample(carbonList.get(i).toString(), "carbon", "TRAIN", schema);
            dataset.addSample(sample);
            dataset.save(manifestPath);
        }
    }
    
}
