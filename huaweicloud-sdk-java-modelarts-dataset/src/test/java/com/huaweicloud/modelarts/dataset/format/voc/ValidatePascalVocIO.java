package com.huaweicloud.modelarts.dataset.format.voc;

import com.huaweicloud.modelarts.dataset.format.voc.position.BNDBox;
import com.huaweicloud.modelarts.dataset.format.voc.position.PositionType;
import org.junit.Assert;

import java.util.List;

public class ValidatePascalVocIO {

  public static void validate(PascalVocIO pascalVocIO) {
    Assert.assertTrue("Images".equals(pascalVocIO.getFolder()));
    Assert.assertTrue("000000089955.jpg".equals(pascalVocIO.getFileName()));
    Assert.assertTrue("Unknown".equals(pascalVocIO.getSource().getDatabase()));
    Assert.assertTrue("640".equals(pascalVocIO.getWidth()));
    Assert.assertTrue("321".equals(pascalVocIO.getHeight()));
    Assert.assertTrue("3".equals(pascalVocIO.getDepth()));
    Assert.assertTrue("0".equals(pascalVocIO.getSegmented()));

    List<VOCObject> vocObjectList = pascalVocIO.getVocObjects();
    Assert.assertTrue(2 == vocObjectList.size());
    for (int i = 0; i < vocObjectList.size(); i++) {
      VOCObject vocObject = vocObjectList.get(i);
      Assert.assertTrue("trafficlight".equals(vocObject.getName()));
      Assert.assertTrue("0".equals(vocObject.getPose()));
      Assert.assertTrue("0".equals(vocObject.getDifficult()));
      Assert.assertTrue(null == vocObject.getOccluded());
      Assert.assertTrue("0".equals(vocObject.getTruncated()));
      Assert.assertTrue(PositionType.BNDBOX.equals(vocObject.getPosition().getType()));
      BNDBox bndBox = (BNDBox) vocObject.getPosition();

      if ("347".equals(((BNDBox) vocObject.getPosition()).getXMin())) {
        Assert.assertTrue("186".equals(bndBox.getYMin()));
        Assert.assertTrue("382".equals(bndBox.getXMax()));
        Assert.assertTrue("249".equals(bndBox.getYMax()));
      } else if ("544".equals(bndBox.getXMin())) {
        Assert.assertTrue("50".equals(bndBox.getYMin()));
        Assert.assertTrue("591".equals(bndBox.getXMax()));
        Assert.assertTrue("149".equals(bndBox.getYMax()));
      } else {
        Assert.assertTrue(false);
      }
    }
  }
}
