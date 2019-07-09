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

package com.huaweicloud.modelarts.dataset.format.voc;

import com.obs.services.ObsClient;

import static com.huaweicloud.modelarts.dataset.format.voc.PascalVocIOTest.validateVOC;
import static com.huaweicloud.modelarts.dataset.format.voc.PascalVocIOTest.validateVOCMultipleObject;
import static com.huaweicloud.modelarts.dataset.format.voc.ValidatePascalVocIO.validate;
import static com.huaweicloud.modelarts.dataset.utils.constants.S3_TEST_PREFIX;

public class PascalVocIOFromOBSTest
{
    
    public static void main(String[] args)
    {
        if (args.length < 4)
        {
            throw new RuntimeException("Please input access_key, secret_key and end_point for reading obs files! ");
        }
        String path = S3_TEST_PREFIX + "/manifest/voc/000000089955_1556180702627.xml";
        String ak = args[0];
        String sk = args[1];
        String endPoint = args[2];
        ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        PascalVocIO pascalVocIO = new PascalVocIO(path, obsClient);
        validate(pascalVocIO);
        
        PascalVocIO pascalVocIO2 = new PascalVocIO(S3_TEST_PREFIX + "/manifest/voc/2007_000027.xml", obsClient);
        validateVOC(pascalVocIO2);
        
        PascalVocIO pascalVocIO3 =
            new PascalVocIO(S3_TEST_PREFIX + "/manifest/voc/000000115967_1556247179208.xml", obsClient);
        validateVOCMultipleObject(pascalVocIO3);
        System.out.println("Success");
    }
}
