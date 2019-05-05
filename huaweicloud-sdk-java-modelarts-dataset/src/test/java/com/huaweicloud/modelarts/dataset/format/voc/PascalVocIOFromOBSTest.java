package com.huaweicloud.modelarts.dataset.format.voc;

import com.obs.services.ObsClient;

import static com.huaweicloud.modelarts.dataset.format.voc.ValidatePascalVocIO.validate;

public class PascalVocIOFromOBSTest {

  public static void main(String[] args) {
    // like: s3a://carbonsouth/manifest/voc/000000089955_1556180702627.xml ak sk endpoint
    if (args.length < 4) {
      throw new RuntimeException("Please input S3 path, access_key, secret_key and end_point for reading obs files! ");
    }
    String path = "s3a://carbonsouth/manifest/voc/000000089955_1556180702627.xml";
    String ak = args[0];
    String sk = args[1];
    String endPoint = args[2];
    ObsClient obsClient = new ObsClient(ak, sk, endPoint);
    PascalVocIO pascalVocIO = new PascalVocIO(path, obsClient);
    validate(pascalVocIO);
    System.out.println("Success");
  }
}
