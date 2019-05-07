package com.huaweicloud.modelarts.dataset.format.voc;

import com.obs.services.ObsClient;

import static com.huaweicloud.modelarts.dataset.format.voc.PascalVocIOTest.validateVOC;
import static com.huaweicloud.modelarts.dataset.format.voc.PascalVocIOTest.validateVOCMultipleObject;
import static com.huaweicloud.modelarts.dataset.format.voc.ValidatePascalVocIO.validate;

public class PascalVocIOFromOBSTest {

  public static void main(String[] args) {
    if (args.length < 4) {
      throw new RuntimeException("Please input access_key, secret_key and end_point for reading obs files! ");
    }
    String path = "s3a://carbonsouth/manifest/voc/000000089955_1556180702627.xml";
    String ak = args[0];
    String sk = args[1];
    String endPoint = args[2];
    ObsClient obsClient = new ObsClient(ak, sk, endPoint);
    PascalVocIO pascalVocIO = new PascalVocIO(path, obsClient);
    validate(pascalVocIO);

    PascalVocIO pascalVocIO2 = new PascalVocIO("s3a://carbonsouth/manifest/voc/2007_000027.xml", obsClient);
    validateVOC(pascalVocIO2);

    PascalVocIO pascalVocIO3 = new PascalVocIO("s3a://carbonsouth/manifest/voc/000000115967_1556247179208.xml", obsClient);
    validateVOCMultipleObject(pascalVocIO3);
    System.out.println("Success");
  }
}
