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

package com.huaweicloud.modelarts.dataset.util;

import static com.huaweicloud.modelarts.dataset.Constants.S3A_PREFIX;
import static com.huaweicloud.modelarts.dataset.Constants.S3N_PREFIX;
import static com.huaweicloud.modelarts.dataset.Constants.SEPARATOR;

public class OBSUtil
{
    /**
     * get bucketname and objectkey fro path
     *
     * @param path manifest path
     * @return bucketname and objectkey array
     */
    public static String[] getBucketNameAndObjectKey(String path)
    {
        int index = 0;
        if (path.toLowerCase().startsWith(S3A_PREFIX) || path.toLowerCase().startsWith(S3N_PREFIX))
        {
            index = 6;
        }
        else
        {
            index = 5;
        }
        String pathWithoutPrefix = path.substring(index, path.length());
        String[] arr = pathWithoutPrefix.split(SEPARATOR);
        String[] result = new String[2];
        result[0] = arr[0];
        result[1] = path.substring(index + arr[0].length() + 1, path.length());
        return result;
    }
}
