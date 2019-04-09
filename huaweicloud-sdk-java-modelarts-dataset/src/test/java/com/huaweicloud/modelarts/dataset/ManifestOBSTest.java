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

import java.io.IOException;

import static com.huaweicloud.modelarts.dataset.Manifest.parseManifest;
import static com.huaweicloud.modelarts.dataset.utils.Validate.validateClassification;

public class ManifestOBSTest {

  public static void main(String[] args) throws IOException {
    if (args.length < 4) {
      throw new RuntimeException("Please input S3 path, access_key, secret_key and end_point for reading obs files! ");
    }
    String path = args[0];
    String ak = args[1];
    String sk = args[2];
    String endPoint = args[3];
    Dataset dataset = parseManifest(path, ak, sk, endPoint);
    validateClassification(dataset);
    System.out.println("Success");
  }
}
