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

import java.util.HashMap;
import java.util.Map;

import static com.huaweicloud.modelarts.dataset.FieldName.PARSE_PASCAL_VOC;
import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.validateClassification;
import static com.huaweicloud.modelarts.dataset.utils.Validate.validateDetectionMultipleAndVOC;
import static com.huaweicloud.modelarts.dataset.utils.Validate.validateDetectionMultipleAndVOCGetWithObsClient;
import static com.huaweicloud.modelarts.dataset.utils.constants.S3_TEST_PREFIX;

public class ManifestOBSTest
{
    // VM parameter: -Dfile.encoding=POSIX
    public static void main(String[] args)
        throws Exception
    {
        if (args.length < 3)
        {
            throw new RuntimeException(
                "Please input access_key, secret_key, end_point, <parsePascalVOC>  for reading obs files! ");
        }
        String path = S3_TEST_PREFIX + "/manifest/detect-multi-s3-voc.manifest";
        String ak = args[0];
        String sk = args[1];
        String endPoint = args[2];
        Dataset dataset = null;
        if (args.length > 4 && Boolean.parseBoolean(args[3]))
        {
            
            // parse Pascal VOC xml file when parse manifest
            Map properties = new HashMap();
            properties.put(PARSE_PASCAL_VOC, true);
            dataset = parseManifest(path, ak, sk, endPoint, properties);
            validateDetectionMultipleAndVOC(dataset);
            
            // it should throw exception when parsing obs files without obsClient.
            Dataset dataset2 = parseManifest(path, ak, sk, endPoint);
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
            Dataset dataset3 = parseManifest(path, ak, sk, endPoint);
            ObsClient obsClient = new ObsClient(ak, sk, endPoint);
            validateDetectionMultipleAndVOCGetWithObsClient(dataset3, obsClient);
        }
        else
        {
            path = "s3://carbonsouth/manifest/classification-xy-V201902220937263726.manifest";
            dataset = parseManifest(path, ak, sk, endPoint);
            validateClassification(dataset);
        }
        System.out.println("Success");
    }
}
