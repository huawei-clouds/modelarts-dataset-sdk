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
import static com.huaweicloud.modelarts.dataset.utils.constants.S3_TEST_PREFIX;

public class ManifestOBSWithClientTest
{
    
    public static void main(String[] args)
        throws Exception
    {
        if (args.length < 3)
        {
            throw new RuntimeException(
                "Please input access_key, secret_key, end_point, <parsePascalVOC> for reading obs files! ");
        }
        String path1 = S3_TEST_PREFIX + "/manifest/detect-multi-s3-voc.manifest";
        String path2 = S3_TEST_PREFIX + "/manifest/detect-multi-s3-voc-filter.manifest";
        String path3 = S3_TEST_PREFIX + "/manifest/detect-multi-xy-V201902220951335133.manifest";
        
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
        try
        {
            validateDetectionMultipleAndVOC(dataset2);
            Assert.assertTrue(false);
        }
        catch (Exception e)
        {
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
        
        String pathPrefix = S3_TEST_PREFIX + "/manifest";
        test.testParseManifestImageClassificationFilterAnnotationNames(pathPrefix, obsClient);
        test.testParseManifestImageClassificationFilterAnnotationNamesLowerCase(pathPrefix, obsClient);
        test.testParseManifestImageClassificationFilterAnnotationNamesWithoutHard(pathPrefix, obsClient);
        test.testParseManifestImageClassificationFilterAnnotationNamesWithFalseHard(pathPrefix, obsClient);
        test.testParseManifestImageClassificationFilterAnnotationNamesWithHard(pathPrefix, obsClient);
        test.testParseManifestImageClassificationFilterAnnotationNamesWithoutParseVOC(pathPrefix, obsClient);
        test.testParseManifestTextClassificationFilterAnnotationNames(pathPrefix, obsClient);
        test.testParseManifestTextClassificationFilterAnnotationNamesWithoutHard(pathPrefix, obsClient);
        test.testParseManifestTextClassificationFilterAnnotationNamesWithoutHardVOC(pathPrefix, obsClient);
        test.testParseManifestTextClassificationFilterAnnotationNamesWithFalseHardVOC(pathPrefix, obsClient);
        test.testParseManifestTextEntityMultipleFilter(pathPrefix, obsClient);
        test.testParseManifestTextEntityMultipleFilterWithFalseHard(pathPrefix, obsClient);
        test.testParseManifestAudioClassificationMultipleFilter(pathPrefix, obsClient);
        test.testParseManifestAudioClassificationMultipleFilterMultipleNames(pathPrefix, obsClient);
        test.testParseManifestAudioClassificationMultipleFilterWithoutHard(pathPrefix, obsClient);
        test.testParseManifestAudioClassificationMultipleFilterWithFalseHard(pathPrefix, obsClient);
        test.testParseManifestAudioContentSampleFilter(pathPrefix, obsClient);
        test.testParseManifestAudioContentSampleFilterWithoutHard(pathPrefix, obsClient);
        test.testParseManifestAudioContentSampleFilterWithFalseHard(pathPrefix, obsClient);
        test.testParseManifestImageDetectionFilterWithFalseHard(pathPrefix, obsClient);
        test.testParseManifestImageDetectionFilterWithRelativePath(pathPrefix, obsClient);
        
        System.out.println("Success");
    }
    
    public void testParseManifestImageDetectionFilterSimple(String path, ObsClient obsClient)
    {
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter(dataset);
        System.out.println("testParseManifestImageDetectionFilterSimple Success");
    }
    
    public void testParseManifestImageDetectionFilterAnnotationNameSimple(String path, ObsClient obsClient)
    {
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("person");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter2(dataset);
        System.out.println("testParseManifestImageDetectionFilterAnnotationNameSimple Success");
    }
    
    public void testParseManifestImageDetectionFilterAnnotationNamesSimple(String path, ObsClient obsClient)
    {
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter(dataset);
        System.out.println("testParseManifestImageDetectionFilterAnnotationNamesSimple Success");
    }
    
    public void testParseManifestImageDetectionFilterAnnotationName2Simple(String path, ObsClient obsClient)
    {
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(PARSE_PASCAL_VOC, true);
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("person");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter3(dataset);
        System.out.println("testParseManifestImageDetectionFilterAnnotationName2Simple Success");
    }
    
    public void testParseManifestImageDetectionFilterAnnotationName3Simple(String path, ObsClient obsClient)
    {
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            List annotationNameLists = new ArrayList();
            annotationNameLists.add("person");
            properties.put(ANNOTATION_NAMES, annotationNameLists);
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilter3(dataset);
        System.out.println("testParseManifestImageDetectionFilterAnnotationName3Simple Success");
    }
    
    public void testParseManifestImageDetectionFilterMultiple(String path, ObsClient obsClient)
    {
        Dataset dataset = null;
        try
        {
            dataset = parseManifest(path, obsClient);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultiple(dataset);
        System.out.println("testParseManifestImageDetectionFilterMultiple Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNames(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilter(dataset);
        System.out.println("testParseManifestImageClassificationFilterAnnotationNames Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesLowerCase(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        Assert.assertTrue(0 == dataset.getSize());
        Assert.assertTrue(0 == dataset.getSamples().size());
        System.out.println("testParseManifestImageClassificationFilterAnnotationNamesLowerCase Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesWithoutHard(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilterWithoutHard(dataset);
        
        System.out.println("testParseManifestImageClassificationFilterAnnotationNamesWithoutHard Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesWithFalseHard(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilterWithFalseHard(dataset);
        System.out.println("testParseManifestImageClassificationFilterAnnotationNamesWithFalseHard Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesWithHard(String resourcePath,
        ObsClient obsClient)
    {
        String path = resourcePath + "/classification-multi-xy-V201902220937263726.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, true);
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilterWithHard(dataset);
        System.out.println("testParseManifestImageClassificationFilterAnnotationNamesWithHard Success");
    }
    
    public void testParseManifestImageClassificationFilterAnnotationNamesWithoutParseVOC(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateClassificationMultipleFilter(dataset);
        System.out.println("testParseManifestImageClassificationFilterAnnotationNamesWithoutParseVOC Success");
    }
    
    public void testParseManifestTextClassificationFilterAnnotationNames(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultipleFilter(dataset);
        System.out.println("testParseManifestTextClassificationFilterAnnotationNames Success");
    }
    
    public void testParseManifestTextClassificationFilterAnnotationNamesWithoutHard(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultipleFilterWithFalseHard(dataset);
        System.out.println("testParseManifestTextClassificationFilterAnnotationNamesWithoutHard Success");
    }
    
    public void testParseManifestTextClassificationFilterAnnotationNamesWithoutHardVOC(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultipleFilterWithoutHard(dataset);
        System.out.println("testParseManifestTextClassificationFilterAnnotationNamesWithoutHardVOC Success");
    }
    
    public void testParseManifestTextClassificationFilterAnnotationNamesWithFalseHardVOC(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextClassificationMultipleFilterWithFalseHard(dataset);
        System.out.println("testParseManifestTextClassificationFilterAnnotationNamesWithFalseHardVOC Success");
    }
    
    public void testParseManifestTextEntityMultipleFilter(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextEntityMultipleFilter(dataset);
        System.out.println("testParseManifestTextEntityMultipleFilter Success");
    }
    
    public void testParseManifestTextEntityMultipleFilterWithFalseHard(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateTextEntityMultipleFilterWithFalseHard(dataset);
        System.out.println("testParseManifestTextEntityMultipleFilterWithFalseHard Success");
    }
    
    public void testParseManifestAudioClassificationMultipleFilter(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultipleFilter(dataset);
        System.out.println("testParseManifestAudioClassificationMultipleFilter Success");
    }
    
    public void testParseManifestAudioClassificationMultipleFilterMultipleNames(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultipleFilterMultipleNames(dataset);
        System.out.println("testParseManifestAudioClassificationMultipleFilterMultipleNames Success");
    }
    
    public void testParseManifestAudioClassificationMultipleFilterWithoutHard(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultipleFilterWithoutHard(dataset);
        System.out.println("testParseManifestAudioClassificationMultipleFilterWithoutHard Success");
    }
    
    public void testParseManifestAudioClassificationMultipleFilterWithFalseHard(String resourcePath,
        ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioClassificationMultipleFilterWithFalseHard(dataset);
        System.out.println("testParseManifestAudioClassificationMultipleFilterWithFalseHard Success");
    }
    
    public void testParseManifestAudioContentSampleFilter(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioContentFilter(dataset);
        System.out.println("testParseManifestAudioContentSampleFilter Success");
    }
    
    public void testParseManifestAudioContentSampleFilterWithoutHard(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioContentFilterWithoutHard(dataset);
        System.out.println("testParseManifestAudioContentSampleFilterWithoutHard Success");
    }
    
    public void testParseManifestAudioContentSampleFilterWithFalseHard(String resourcePath, ObsClient obsClient)
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
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateAudioContentFilterWithFalseHard(dataset);
        System.out.println("testParseManifestAudioContentSampleFilterWithFalseHard Success");
    }
    
    public void testParseManifestImageDetectionFilterWithFalseHard(String resourcePath, ObsClient obsClient)
    {
        String path = resourcePath + "/detect-multi-s3-voc-filter.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilterWithFalseHard(dataset);
        System.out.println("testParseManifestImageDetectionFilterWithFalseHard Success");
    }
    
    public void testParseManifestImageDetectionFilterWithRelativePath(String resourcePath, ObsClient obsClient)
    {
        String path = resourcePath + "/detect-multi-local-voc-filter.manifest";
        Dataset dataset = null;
        try
        {
            Map properties = new HashMap();
            properties.put(ANNOTATION_HARD, false);
            dataset = parseManifest(path, obsClient, properties);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        validateDetectionMultipleAndVOCFilterWithFalseHard(dataset);
        System.out.println("testParseManifestImageDetectionFilterWithFalseHard Success");
    }
    
}
