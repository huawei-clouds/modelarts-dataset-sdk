package com.huaweicloud.modelarts.dataset.format.voc;

import com.huaweicloud.modelarts.dataset.format.voc.position.*;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.huaweicloud.modelarts.dataset.format.voc.ValidatePascalVocIO.validate;

public class PascalVocIOTest extends TestCase
{
    private String resourcePath = this.getClass().getResource("/").getPath();
    
    @Test
    public void testReadVocXMLFromCloud()
    {
        String path = resourcePath + "/voc/000000089955_1556180702627.xml";
        PascalVocIO pascalVocIO = new PascalVocIO(path);
        validate(pascalVocIO);
    }
    
    @Test
    public void testReadVocXMLFromCloudWithParse()
    {
        String path = resourcePath + "/voc/000000089955_1556180702627.xml";
        PascalVocIO pascalVocIO = new PascalVocIO();
        validate(pascalVocIO.parseXML(path));
    }
    
    @Test
    public void testReadVocXMLFromPython()
    {
        String path = resourcePath + "/voc/test.xml";
        
        PascalVocIO pascalVocIO = new PascalVocIO(path);
        Assert.assertTrue("tests".equals(pascalVocIO.getFolder()));
        Assert.assertTrue("test".equals(pascalVocIO.getFileName()));
        Assert.assertTrue("Unknown".equals(pascalVocIO.getSource().getDatabase()));
        Assert.assertTrue("512".equals(pascalVocIO.getHeight()));
        Assert.assertTrue("512".equals(pascalVocIO.getWidth()));
        Assert.assertTrue("1".equals(pascalVocIO.getDepth()));
        Assert.assertTrue("0".equals(pascalVocIO.getSegmented()));
        
        List<VOCObject> vocObjectList = pascalVocIO.getVocObjects();
        Assert.assertTrue(2 == vocObjectList.size());
        for (int i = 0; i < vocObjectList.size(); i++)
        {
            VOCObject vocObject = vocObjectList.get(i);
            Assert.assertTrue(PositionType.BNDBOX.equals(vocObject.getPosition().getType()));
            BNDBox bndBox = (BNDBox)vocObject.getPosition();
            
            if ("person".equals(vocObject.getName()))
            {
                Assert.assertTrue("Unspecified".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("1".equals(vocObject.getDifficult()));
                Assert.assertTrue("60".equals(bndBox.getXMin()));
                Assert.assertTrue("40".equals(bndBox.getYMin()));
                Assert.assertTrue("430".equals(bndBox.getXMax()));
                Assert.assertTrue("504".equals(bndBox.getYMax()));
            }
            else if ("face".equals(vocObject.getName()))
            {
                Assert.assertTrue("Unspecified".equals(vocObject.getPose()));
                Assert.assertTrue("1".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue("113".equals(bndBox.getXMin()));
                Assert.assertTrue("40".equals(bndBox.getYMin()));
                Assert.assertTrue("450".equals(bndBox.getXMax()));
                Assert.assertTrue("403".equals(bndBox.getYMax()));
            }
            else
            {
                Assert.assertTrue(false);
            }
        }
    }
    
    @Test
    public void testReadVocXMLForAllType()
    {
        String path = resourcePath + "/voc/000000115967_1556247179208.xml";
        
        PascalVocIO pascalVocIO = new PascalVocIO(path);
        
        validateVOCMultipleObject(pascalVocIO);
    }
    
    @Test
    public void testReadVocXMLFromVOCUK()
    {
        String path = resourcePath + "/voc/2007_000027.xml";
        
        PascalVocIO pascalVocIO = new PascalVocIO(path);
        validateVOC(pascalVocIO);
    }
    
    @Test
    public void testReadVocXMLFromVOCLabelProperties()
    {
        String path = resourcePath + "/voc/labelProperties.xml";
        PascalVocIO pascalVocIO = new PascalVocIO(path);
        validateVOCLabelProperties(pascalVocIO);
    }
    
    @Test
    public void testReadVocXMLFromVOCLabelPropertiesEmpty()
    {
        String path = resourcePath + "/voc/errorFiles/labelPropertiesEmpty.xml";
        PascalVocIO pascalVocIO = new PascalVocIO(path);
        validateVOCLabelPropertiesEmpty(pascalVocIO);
    }
    
    @Test
    public void testReadVocXMLFromVOCLabelPropertiesEmpty2()
    {
        String path = resourcePath + "/voc/errorFiles/labelPropertiesEmpty2.xml";
        try
        {
            new PascalVocIO(path);
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains(
                    "Can't parse the XML file, java.lang.IllegalArgumentException: object properties 颜色 can't be empty in VOC file!"));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCLabelPropertiesEmpty3()
    {
        String path = resourcePath + "/voc/errorFiles/labelPropertiesEmpty3.xml";
        try
        {
            new PascalVocIO(path);
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains(
                    "Can't parse the XML file, java.lang.IllegalArgumentException: object properties color can't be empty in VOC file!"));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_WithoutFolder()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_folder_error.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("folder can't be empty in VOC file!; The file is"));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_WithoutFileName()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_filename_error.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage().contains("filename can't be empty in VOC file!; The file is"));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_emptyAnnotation()
    {
        String path = resourcePath + "/voc/errorFiles/emptyAnnotation.xml";
        try
        {
            new PascalVocIO(path);
            Assert.assertTrue(true);
        }
        catch (Exception e)
        {
            Assert.fail();
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_empty()
    {
        String path = resourcePath + "/voc/errorFiles/empty.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains("Can't parse the XML file, org.xml.sax.SAXParseException; Premature end of file.; The file "));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_emptySource()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_source_database_empty.xml";
        try
        {
            PascalVocIO pascalVocIO = new PascalVocIO(path);
            Assert.assertTrue(null == pascalVocIO.getSource().getDatabase());
        }
        catch (Exception e)
        {
            Assert.fail();
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_width_empty()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_width_empty.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains(
                    "Can't parse the XML file, java.lang.IllegalArgumentException: width can't be empty in VOC file!; The file is "));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_segmented_empty()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_segmented_empty.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains(
                    "Can't parse the XML file, java.lang.IllegalArgumentException: segmented can't be empty in VOC file!; The file is "));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_object_empty()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_object_empty.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains(
                    "Can't parse the XML file, java.lang.IllegalArgumentException: object can't be empty in VOC file!; The file is "));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_object_name_empty()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_object_name_empty.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains(
                    "Can't parse the XML file, java.lang.IllegalArgumentException: object name can't be empty in VOC file!; The file is "));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_object_bndbox_empty()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_bndbox_error.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains(
                    "Can't parse the XML file, java.lang.IllegalArgumentException: object bndbox can't be empty in VOC file!; The file is "));
        }
    }
    
    @Test
    public void testReadVocXMLFromVOCUK_object_different_empty()
    {
        String path = resourcePath + "/voc/errorFiles/2007_000027_difficult_empty.xml";
        try
        {
            new PascalVocIO(path);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(e.getMessage()
                .contains(
                    "Can't parse the XML file, java.lang.IllegalArgumentException: object difficult can't be empty in VOC file!; The file is "));
        }
    }
    
    public static void validateVOC(PascalVocIO pascalVocIO)
    {
        Assert.assertTrue("VOC2012".equals(pascalVocIO.getFolder()));
        Assert.assertTrue("2007_000027.jpg".equals(pascalVocIO.getFileName()));
        Assert.assertTrue("The VOC2007 Database".equals(pascalVocIO.getSource().getDatabase()));
        Assert.assertTrue("486".equals(pascalVocIO.getWidth()));
        Assert.assertTrue("500".equals(pascalVocIO.getHeight()));
        Assert.assertTrue("3".equals(pascalVocIO.getDepth()));
        Assert.assertTrue("0".equals(pascalVocIO.getSegmented()));
        
        List<VOCObject> vocObjectList = pascalVocIO.getVocObjects();
        Assert.assertTrue(1 == vocObjectList.size());
        for (int i = 0; i < vocObjectList.size(); i++)
        {
            VOCObject vocObject = vocObjectList.get(i);
            Assert.assertTrue("person".equals(vocObject.getName()));
            Assert.assertTrue("Unspecified".equals(vocObject.getPose()));
            Assert.assertTrue("0".equals(vocObject.getDifficult()));
            Assert.assertTrue("0.8".equals(vocObject.getConfidence()));
            Assert.assertTrue(null == vocObject.getOccluded());
            Assert.assertTrue("0".equals(vocObject.getTruncated()));
            Assert.assertTrue(PositionType.BNDBOX.equals(vocObject.getPosition().getType()));
            BNDBox bndBox = (BNDBox)vocObject.getPosition();
            
            if ("174".equals(((BNDBox)vocObject.getPosition()).getXMin()))
            {
                Assert.assertTrue("101".equals(bndBox.getYMin()));
                Assert.assertTrue("349".equals(bndBox.getXMax()));
                Assert.assertTrue("351".equals(bndBox.getYMax()));
            }
            else
            {
                Assert.assertTrue(false);
            }
            List<VOCObject> parts = vocObject.getParts();
            Assert.assertTrue(4 == parts.size());
            for (int j = 0; j < parts.size(); j++)
            {
                Assert.assertTrue(PositionType.BNDBOX.equals(vocObject.getPosition().getType()));
                VOCObject part = parts.get(j);
                
                BNDBox partBNDBox = (BNDBox)part.getPosition();
                
                if ("head".equals(part.getName()))
                {
                    Assert.assertTrue("0.8".equals(part.getConfidence()));
                    if ("169".equals(partBNDBox.getXMin()))
                    {
                        Assert.assertTrue("104".equals(partBNDBox.getYMin()));
                        Assert.assertTrue("209".equals(partBNDBox.getXMax()));
                        Assert.assertTrue("146".equals(partBNDBox.getYMax()));
                    }
                    else
                    {
                        Assert.assertTrue(false);
                    }
                }
                else if ("hand".equals(part.getName()))
                {
                    Assert.assertTrue(null == (part.getConfidence()));
                    if ("278".equals(partBNDBox.getXMin()))
                    {
                        Assert.assertTrue("210".equals(partBNDBox.getYMin()));
                        Assert.assertTrue("297".equals(partBNDBox.getXMax()));
                        Assert.assertTrue("233".equals(partBNDBox.getYMax()));
                    }
                    else
                    {
                        Assert.assertTrue(false);
                    }
                }
                else if ("foot".equals(part.getName()))
                {
                    if ("273".equals(partBNDBox.getXMin()))
                    {
                        Assert.assertTrue("333".equals(partBNDBox.getYMin()));
                        Assert.assertTrue("297".equals(partBNDBox.getXMax()));
                        Assert.assertTrue("354".equals(partBNDBox.getYMax()));
                    }
                    else if ("319".equals(partBNDBox.getXMin()))
                    {
                        Assert.assertTrue("307".equals(partBNDBox.getYMin()));
                        Assert.assertTrue("340".equals(partBNDBox.getXMax()));
                        Assert.assertTrue("326".equals(partBNDBox.getYMax()));
                    }
                    else
                    {
                        Assert.assertTrue(false);
                    }
                }
                else
                {
                    Assert.assertTrue(false);
                }
                
            }
        }
    }
    
    public static void validateVOCLabelProperties(PascalVocIO pascalVocIO)
    {
        Assert.assertTrue("Images".equals(pascalVocIO.getFolder()));
        Assert.assertTrue("000000484951.jpg".equals(pascalVocIO.getFileName()));
        Assert.assertTrue("Unknown".equals(pascalVocIO.getSource().getDatabase()));
        Assert.assertTrue("640".equals(pascalVocIO.getWidth()));
        Assert.assertTrue("426".equals(pascalVocIO.getHeight()));
        Assert.assertTrue("3".equals(pascalVocIO.getDepth()));
        Assert.assertTrue("0".equals(pascalVocIO.getSegmented()));
        
        List<VOCObject> vocObjectList = pascalVocIO.getVocObjects();
        Assert.assertTrue(6 == vocObjectList.size());
        for (int i = 0; i < vocObjectList.size(); i++)
        {
            VOCObject vocObject = vocObjectList.get(i);
            if ("labelProperties".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(5 == properties.size());
                Assert.assertTrue("green".equals(properties.get("color")));
                Assert.assertTrue("绿色".equals(properties.get("属性")));
                Assert.assertTrue("blue".equals(properties.get("color2")));
                Assert.assertTrue("蓝色".equals(properties.get("颜色2")));
    
                Assert.assertTrue("value".equals(properties.get("property")));
                Assert.assertTrue("绿色".equals(properties.get("属性")));
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
                
                BNDBox bndBox = (BNDBox)vocObject.getPosition();
                Assert.assertTrue("199".equals(bndBox.getXMin()));
                Assert.assertTrue("356".equals(bndBox.getYMin()));
                Assert.assertTrue("230".equals(bndBox.getXMax()));
                Assert.assertTrue("385".equals(bndBox.getYMax()));
            }
            else if ("big".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(0 == properties.size());
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
                
                BNDBox bndBox = (BNDBox)vocObject.getPosition();
                Assert.assertTrue("248".equals(bndBox.getXMin()));
                Assert.assertTrue("59".equals(bndBox.getYMin()));
                Assert.assertTrue("638".equals(bndBox.getXMax()));
                Assert.assertTrue("292".equals(bndBox.getYMax()));
            }
            else if ("labelProperties3".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(1 == properties.size());
                Assert.assertTrue("红色".equals(properties.get("颜色")));
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
            }
            else if ("labelProperties4".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(1 == properties.size());
                Assert.assertTrue("blue".equals(properties.get("color2")));
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
            }
            else if ("labelProperties2".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(1 == properties.size());
                Assert.assertTrue("yellow".equals(properties.get("color")));
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
                
            }
            else if ("small".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(0 == properties.size());
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
            }
            else
            {
                Assert.fail();
            }
            
        }
    }
    
    public static void validateVOCLabelPropertiesEmpty(PascalVocIO pascalVocIO)
    {
        Assert.assertTrue("Images".equals(pascalVocIO.getFolder()));
        Assert.assertTrue("000000484951.jpg".equals(pascalVocIO.getFileName()));
        Assert.assertTrue("Unknown".equals(pascalVocIO.getSource().getDatabase()));
        Assert.assertTrue("640".equals(pascalVocIO.getWidth()));
        Assert.assertTrue("426".equals(pascalVocIO.getHeight()));
        Assert.assertTrue("3".equals(pascalVocIO.getDepth()));
        Assert.assertTrue("0".equals(pascalVocIO.getSegmented()));
        
        List<VOCObject> vocObjectList = pascalVocIO.getVocObjects();
        Assert.assertTrue(5 == vocObjectList.size());
        for (int i = 0; i < vocObjectList.size(); i++)
        {
            VOCObject vocObject = vocObjectList.get(i);
            if ("labelProperties".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(0 == properties.size());
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
                
                BNDBox bndBox = (BNDBox)vocObject.getPosition();
                Assert.assertTrue("199".equals(bndBox.getXMin()));
                Assert.assertTrue("356".equals(bndBox.getYMin()));
                Assert.assertTrue("230".equals(bndBox.getXMax()));
                Assert.assertTrue("385".equals(bndBox.getYMax()));
            }
            else if ("big".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(0 == properties.size());
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
                
                BNDBox bndBox = (BNDBox)vocObject.getPosition();
                Assert.assertTrue("248".equals(bndBox.getXMin()));
                Assert.assertTrue("59".equals(bndBox.getYMin()));
                Assert.assertTrue("638".equals(bndBox.getXMax()));
                Assert.assertTrue("292".equals(bndBox.getYMax()));
            }
            else if ("labelProperties3".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(1 == properties.size());
                Assert.assertTrue("红色".equals(properties.get("颜色")));
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
            }
            else if ("labelProperties2".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(1 == properties.size());
                Assert.assertTrue("yellow".equals(properties.get("color")));
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
                
            }
            else if ("small".equals(vocObject.getName()))
            {
                Map properties = vocObject.getProperties();
                Assert.assertTrue(0 == properties.size());
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == (vocObject.getConfidence()));
            }
            else
            {
                Assert.fail();
            }
            
        }
    }
    
    public static void validateVOCMultipleObject(PascalVocIO pascalVocIO)
    {
        Assert.assertTrue("Images".equals(pascalVocIO.getFolder()));
        Assert.assertTrue("000000115967.jpg".equals(pascalVocIO.getFileName()));
        Assert.assertTrue("Unknown".equals(pascalVocIO.getSource().getDatabase()));
        Assert.assertTrue("640".equals(pascalVocIO.getWidth()));
        Assert.assertTrue("480".equals(pascalVocIO.getHeight()));
        Assert.assertTrue("3".equals(pascalVocIO.getDepth()));
        Assert.assertTrue("0".equals(pascalVocIO.getSegmented()));
        List<VOCObject> vocObjectList = pascalVocIO.getVocObjects();
        Assert.assertTrue(11 == vocObjectList.size());
        for (int i = 0; i < vocObjectList.size(); i++)
        {
            VOCObject vocObject = vocObjectList.get(i);
            if ("car".equals(vocObject.getName()))
            {
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue("0.8".equals(vocObject.getConfidence()));
                Assert.assertTrue(PositionType.POLYGON.equals(vocObject.getPosition().getType()));
                Polygon polygon = (Polygon)vocObject.getPosition();
                List<Point> pointList = polygon.getPoints();
                List<Point> pointExpectResult = new ArrayList<Point>();
                pointExpectResult.add(new Point("x1", "0", "y1", "292"));
                pointExpectResult.add(new Point("x2", "12", "y2", "279"));
                pointExpectResult.add(new Point("x3", "102", "y3", "280"));
                pointExpectResult.add(new Point("x4", "122", "y4", "294"));
                pointExpectResult.add(new Point("x5", "134", "y5", "326"));
                pointExpectResult.add(new Point("x6", "137", "y6", "321"));
                pointExpectResult.add(new Point("x7", "138", "y7", "312"));
                pointExpectResult.add(new Point("x8", "148", "y8", "314"));
                pointExpectResult.add(new Point("x9", "152", "y9", "319"));
                pointExpectResult.add(new Point("x10", "150", "y10", "326"));
                pointExpectResult.add(new Point("x11", "136", "y11", "331"));
                pointExpectResult.add(new Point("x12", "149", "y12", "349"));
                pointExpectResult.add(new Point("x13", "147", "y13", "414"));
                pointExpectResult.add(new Point("x14", "128", "y14", "422"));
                pointExpectResult.add(new Point("x15", "111", "y15", "422"));
                pointExpectResult.add(new Point("x16", "104", "y16", "403"));
                pointExpectResult.add(new Point("x17", "26", "y17", "403"));
                pointExpectResult.add(new Point("x18", "24", "y18", "415"));
                pointExpectResult.add(new Point("x19", "7", "y19", "419"));
                pointExpectResult.add(new Point("x20", "0", "y20", "424"));
                pointExpectResult.add(new Point("x21", "1", "y21", "413"));
                
                List<Point> pointExpectResult2 = new ArrayList<Point>();
                pointExpectResult2.add(new Point("x1", "259", "y1", "340"));
                pointExpectResult2.add(new Point("x2", "258", "y2", "328"));
                pointExpectResult2.add(new Point("x3", "263", "y3", "322"));
                pointExpectResult2.add(new Point("x4", "278", "y4", "322"));
                pointExpectResult2.add(new Point("x5", "281", "y5", "329"));
                pointExpectResult2.add(new Point("x6", "282", "y6", "333"));
                pointExpectResult2.add(new Point("x7", "282", "y7", "341"));
                pointExpectResult2.add(new Point("x8", "280", "y8", "342"));
                
                if (pointExpectResult.contains(pointList.get(0)))
                {
                    for (int j = 0; j < pointList.size(); j++)
                    {
                        Assert.assertTrue(pointExpectResult.contains(pointList.get(j)));
                    }
                }
                else if (pointExpectResult2.contains(pointList.get(0)))
                {
                    for (int j = 0; j < pointList.size(); j++)
                    {
                        Assert.assertTrue(pointExpectResult2.contains(pointList.get(j)));
                    }
                }
                else
                {
                    Assert.assertTrue(false);
                }
            }
            else if ("DottedLine".equals(vocObject.getName()))
            {
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue("0".equals(vocObject.getPose()));
                
                Assert.assertTrue(PositionType.DASHED.equals(vocObject.getPosition().getType()));
                Dashed dashed = (Dashed)vocObject.getPosition();
                if ("474".equals(dashed.getX1()))
                {
                    Assert.assertTrue("456".equals(dashed.getY1()));
                    Assert.assertTrue("370".equals(dashed.getX2()));
                    Assert.assertTrue("339".equals(dashed.getY2()));
                }
                else if ("160".equals(dashed.getX1()))
                {
                    Assert.assertTrue("445".equals(dashed.getY1()));
                    Assert.assertTrue("195".equals(dashed.getX2()));
                    Assert.assertTrue("368".equals(dashed.getY2()));
                }
                else
                {
                    Assert.assertTrue(false);
                }
            }
            else if ("line".equals(vocObject.getName()))
            {
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                Assert.assertTrue("0".equals(vocObject.getPose()));
                
                Assert.assertTrue(PositionType.LINE.equals(vocObject.getPosition().getType()));
                Line line = (Line)vocObject.getPosition();
                if ("154".equals(line.getX1()))
                {
                    Assert.assertTrue("449".equals(line.getY1()));
                    Assert.assertTrue("466".equals(line.getX2()));
                    Assert.assertTrue("457".equals(line.getY2()));
                }
                else
                {
                    Assert.assertTrue(false);
                }
            }
            else if ("trafficlight".equals(vocObject.getName()))
            {
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                
                Assert.assertTrue(PositionType.BNDBOX.equals(vocObject.getPosition().getType()));
                BNDBox bndBox = (BNDBox)vocObject.getPosition();
                if ("374".equals(bndBox.getXMin()))
                {
                    Assert.assertTrue("209".equals(bndBox.getYMin()));
                    Assert.assertTrue("405".equals(bndBox.getXMax()));
                    Assert.assertTrue("224".equals(bndBox.getYMax()));
                }
                else if ("255".equals(bndBox.getXMin()))
                {
                    Assert.assertTrue("205".equals(bndBox.getYMin()));
                    Assert.assertTrue("293".equals(bndBox.getXMax()));
                    Assert.assertTrue("220".equals(bndBox.getYMax()));
                }
                else
                {
                    Assert.assertTrue(false);
                }
            }
            else if ("greenLight".equals(vocObject.getName()))
            {
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("1".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                
                Assert.assertTrue(PositionType.CIRCLE.equals(vocObject.getPosition().getType()));
                Circle circle = (Circle)vocObject.getPosition();
                if ("61".equals(circle.getCx()))
                {
                    Assert.assertTrue("204".equals(circle.getCy()));
                    Assert.assertTrue("5".equals(circle.getR()));
                }
                else
                {
                    Assert.assertTrue(false);
                }
            }
            else if ("redPoint".equals(vocObject.getName()))
            {
                Assert.assertTrue("0".equals(vocObject.getPose()));
                Assert.assertTrue("0".equals(vocObject.getDifficult()));
                Assert.assertTrue(null == vocObject.getOccluded());
                Assert.assertTrue("0".equals(vocObject.getTruncated()));
                
                Assert.assertTrue(PositionType.POINT.equals(vocObject.getPosition().getType()));
                Point point = (Point)vocObject.getPosition();
                if ("259".equals(point.getXValue()))
                {
                    Assert.assertTrue("282".equals(point.getYValue()) || "290".equals(point.getYValue()));
                }
                else if ("226".equals(point.getXValue()))
                {
                    Assert.assertTrue("281".equals(point.getYValue()));
                }
                else
                {
                    Assert.assertTrue(false);
                }
            }
            else
            {
                Assert.assertTrue(false);
            }
        }
    }
    
}
