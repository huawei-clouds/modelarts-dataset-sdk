package com.huaweicloud.modelarts.dataset;

import com.obs.services.ObsClient;
import com.obs.services.internal.security.ProviderCredentials;
import com.obs.services.model.ObsObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OBSTest
{
    public static void main(String[] args)
        throws IOException
    {
        String endPoint = "http://obs.cn-south-1.myhwclouds.com";
        String ak = "GYLW3KJVB7IFHNF5W3JV";
        String sk = "6KYRbqkodxzk9xIka743Z5E1Y3nm9KnGsHPuZqPT";

        // 创建ObsClient实例
        final ObsClient obsClient = new ObsClient(ak, sk, endPoint);
    
//        ObsObject obsObject = obsClient.getObject("carbonsouth", "carbondatalogo.jpg");
//
//        // 读取对象内容
//        System.out.println("Object content:");
//        InputStream input = obsObject.getObjectContent();
//        byte[] b = new byte[1024];
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        int len;
//        while ((len=input.read(b)) != -1){
//            bos.write(b, 0, len);
//        }
//
//        System.out.println(new String(bos.toByteArray()));
//        bos.close();
//        input.close();
    
//        ObsObject obsObject = obsClient.getObject("carbonsouth", "carbondatalogo.jpg");
//        BufferedInputStream bis = new BufferedInputStream(obsObject.getObjectContent());
//        byte[] originBinary = new byte[bis.available()];
//        int index=0;
//        while ((bis.read(originBinary)) != -1) {
//            index++;
//            System.out.println(index);
//        }
//        bis.close();
    
//
//        ObsObject obsObject = obsClient.getObject("carbonsouth", "carbondatalogo.jpg");
//        BufferedInputStream bis = new BufferedInputStream(obsObject.getObjectContent());
////        obsClient.credentials.getAccessKey();
////        ProviderCredentials p =obsClient.getProviderCredentials();
//        Long contentLength=obsObject.getMetadata().getContentLength();
//        if(contentLength<Integer.MAX_VALUE){
//            byte[] originBinary = new byte[contentLength.intValue()];
//            int index=0;
//            bis.read(originBinary);
//            bis.close();
//            String destString = "./target/binary/image" + ".jpg";
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destString));
//            bos.write(originBinary);
//            bos.close();
//        }
        // w
    
    
        ObsObject obsObject = obsClient.getObject("carbonsouth", "carbondatalogo.jpg");
        BufferedInputStream bis = new BufferedInputStream(obsObject.getObjectContent());
//        obsClient.credentials.getAccessKey();
//        ProviderCredentials p =obsClient.getProviderCredentials();
        Long contentLength=obsObject.getMetadata().getContentLength();
        if(contentLength<Integer.MAX_VALUE){
            InputStream input = obsObject.getObjectContent();
            byte[] originBinary = new byte[contentLength.intValue()];
            byte[] originBinary2 = new byte[contentLength.intValue()];
            String destString = "./target/binary/image" + ".jpg";
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            int sum=0;
            while ((len=input.read(originBinary)) != -1){
                System.out.println(sum+":"+len);
//                bos.write(originBinary, sum, sum+len);
                bos.write(originBinary, 0, len);
                sum=sum+len;
            }
            System.out.println(sum);
            BufferedOutputStream bos1 = new BufferedOutputStream(new FileOutputStream(destString));
            bos1.write(bos.toByteArray());
            bos1.close();
        }
        
    }
}
