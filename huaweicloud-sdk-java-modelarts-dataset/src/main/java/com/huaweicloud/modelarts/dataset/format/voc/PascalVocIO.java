package com.huaweicloud.modelarts.dataset.format.voc;

import com.huaweicloud.modelarts.dataset.FieldName;
import com.huaweicloud.modelarts.dataset.format.voc.position.*;
import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATIONS;
import static com.huaweicloud.modelarts.dataset.FieldName.VOC_PROPERTY_KEY;
import static com.huaweicloud.modelarts.dataset.FieldName.VOC_PROPERTY_VALUE;
import static com.huaweicloud.modelarts.dataset.util.OBSUtil.getBucketNameAndObjectKey;

/**
 * main class for parsing pascal VOC format
 */
public class PascalVocIO
{
    private String folder;
    
    private String fileName;
    
    private Source source;
    
    private String width;
    
    private String height;
    
    private String depth;
    
    private String segmented;
    
    private List<VOCObject> vocObjects = new ArrayList<VOCObject>();
    
    public PascalVocIO()
    {
    }
    
    /**
     * constructor with file path
     *
     * @param filePath xml file path
     */
    public PascalVocIO(String filePath)
    {
        parseXML(filePath);
    }
    
    /**
     * constructor with file path and obs client, read data from obs
     * obs client should already configure ak sk and endpoint, which should available.
     *
     * @param filePath  xml file path
     * @param obsClient obs client
     */
    public PascalVocIO(String filePath, ObsClient obsClient)
    {
        if (null == obsClient)
        {
            parseXML(filePath);
        }
        else
        {
            parseXML(filePath, obsClient);
        }
    }
    
    private static final org.apache.log4j.Logger LOGGER =
        org.apache.log4j.Logger.getLogger(PascalVocIO.class.getName());
    
    /**
     * get voc object
     *
     * @param objectNodeList objectNode List
     * @return VOCObject
     */
    public VOCObject getVOCObject(NodeList objectNodeList)
    {
        if (null == objectNodeList || 1 == objectNodeList.getLength())
        {
            throw new IllegalArgumentException(FieldName.OBJECT + " can't be empty in VOC file!");
        }
        String name = null;
        Map properties = new LinkedHashMap();
        String pose = null;
        String truncated = null;
        String occluded = null;
        String difficult = null;
        String confidence = null;
        String xMin = null;
        String yMin = null;
        String xMax = null;
        String yMax = null;
        Position position = null;
        List<VOCObject> parts = new ArrayList<VOCObject>();
        for (int j = 0; j < objectNodeList.getLength(); j++)
        {
            String objectNodeName = objectNodeList.item(j).getNodeName();
            if (FieldName.NAME.equalsIgnoreCase(objectNodeName))
            {
                name = getMandatoryNodeValue(objectNodeList,
                    j,
                    FieldName.OBJECT + " " + FieldName.NAME + " can't be empty in VOC file!");
            }
            else if (FieldName.VOC_PROPERTIES.equalsIgnoreCase(objectNodeName))
            {
                NodeList propertiesNodeList = objectNodeList.item(j).getChildNodes();
    
                for (int i = 0; i < propertiesNodeList.getLength(); i++)
                {
                    String propertiesName = propertiesNodeList.item(i).getNodeName();
                    if ("#text" != propertiesName)
                    {
        
                        NodeList propertyNodeList = propertiesNodeList.item(i).getChildNodes();
                        if (propertyNodeList.getLength() > 1)
                        {
                            String propertyKey = null;
                            String propertyValue = null;
            
                            for (int k = 0; k < propertyNodeList.getLength(); k++)
                            {
                                String propertyName = propertyNodeList.item(k).getNodeName();
                                if ("#text" != propertyName)
                                {
                                    if (VOC_PROPERTY_KEY == propertyName.toLowerCase())
                                    {
                                        propertyKey = getMandatoryNodeValue(propertyNodeList,
                                            k,
                                            FieldName.OBJECT + " " + FieldName.VOC_PROPERTIES
                                                + " " + propertyName + " can't be empty in VOC file!");
                                    }
                                    if (VOC_PROPERTY_VALUE == propertyName.toLowerCase())
                                    {
                                        propertyValue = getMandatoryNodeValue(propertyNodeList,
                                            k,
                                            FieldName.OBJECT + " " + FieldName.VOC_PROPERTIES
                                                + " " + propertyName + " can't be empty in VOC file!");
                                    }
                                }
                            }
                            assert (null != propertyKey);
                            properties.put(propertyKey, propertyValue);
                        }
                        else
                        {
                            String propertiesValue = getMandatoryNodeValue(propertiesNodeList,
                                i,
                                FieldName.OBJECT + " " + FieldName.VOC_PROPERTIES
                                    + " " + propertiesName + " can't be empty in VOC file!");
                            properties.put(propertiesName, propertiesValue);
                        }
                    }
                }
            }
            else if (FieldName.POSE.equalsIgnoreCase(objectNodeName))
            {
                pose = getMandatoryNodeValue(objectNodeList,
                    j,
                    FieldName.OBJECT + " " + FieldName.POSE + " can't be empty in VOC file!");
            }
            else if (FieldName.TRUNCATED.equalsIgnoreCase(objectNodeName))
            {
                truncated = getMandatoryNodeValue(objectNodeList,
                    j,
                    FieldName.OBJECT + " " + FieldName.TRUNCATED + " can't be empty in VOC file!");
            }
            else if (FieldName.OCCLUDED.equalsIgnoreCase(objectNodeName))
            {
                occluded = getMandatoryNodeValue(objectNodeList,
                    j,
                    FieldName.OBJECT + " " + FieldName.OCCLUDED + " can't be empty in VOC file!");
            }
            else if (FieldName.DIFFICULT.equalsIgnoreCase(objectNodeName))
            {
                difficult = getMandatoryNodeValue(objectNodeList,
                    j,
                    FieldName.OBJECT + " " + FieldName.DIFFICULT + " can't be empty in VOC file!");
            }
            else if (FieldName.CONFIDENCE.equalsIgnoreCase(objectNodeName))
            {
                confidence = objectNodeList.item(j).getFirstChild().getNodeValue();
            }
            else if (PositionType.BNDBOX.name().equalsIgnoreCase(objectNodeName))
            {
                NodeList bndBoxNodeList = objectNodeList.item(j).getChildNodes();
                if (null == bndBoxNodeList || 1 == bndBoxNodeList.getLength())
                {
                    throw new IllegalArgumentException(
                        FieldName.OBJECT + " " + PositionType.BNDBOX.name().toLowerCase() +
                            " can't be empty in VOC file!");
                }
                for (int k = 0; k < bndBoxNodeList.getLength(); k++)
                {
                    String bndBoxNodeName = bndBoxNodeList.item(k).getNodeName();
                    if (FieldName.XMAX.equalsIgnoreCase(bndBoxNodeName))
                    {
                        xMax = bndBoxNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.XMIN.equalsIgnoreCase(bndBoxNodeName))
                    {
                        xMin = bndBoxNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.YMAX.equalsIgnoreCase(bndBoxNodeName))
                    {
                        yMax = bndBoxNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.YMIN.equalsIgnoreCase(bndBoxNodeName))
                    {
                        yMin = bndBoxNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    position = new BNDBox(xMin, yMin, xMax, yMax);
                }
            }
            else if (FieldName.PART.equalsIgnoreCase(objectNodeName))
            {
                NodeList bndBoxNodeList = objectNodeList.item(j).getChildNodes();
                if (null == parts)
                {
                    parts = new ArrayList<VOCObject>();
                }
                parts.add(getVOCObject(bndBoxNodeList));
            }
            else if (PositionType.POLYGON.name().equalsIgnoreCase(objectNodeName))
            {
                NodeList polygonNodeList = objectNodeList.item(j).getChildNodes();
                Polygon polygon = new Polygon();
                Map<String, String> points = new LinkedHashMap<String, String>();
                for (int k = 0; k < polygonNodeList.getLength(); k++)
                {
                    String polygonNodeName = polygonNodeList.item(k).getNodeName();
                    if (polygonNodeName.toLowerCase().startsWith("x") || polygonNodeName.toLowerCase().startsWith("y"))
                    {
                        points.put(polygonNodeName,
                            polygonNodeList.item(k).getFirstChild().getNodeValue());
                    }
                    else if (polygonNodeName.toLowerCase().startsWith("#text"))
                    {
                    }
                    else
                    {
                        LOGGER.warn(polygonNodeName + " is Unrecognized in polygon.");
                    }
                }
                Object[] keySet = points.keySet().toArray();
                Set<String> validateKey = new HashSet<String>();
                for (int k = 0; k < keySet.length; k++)
                {
                    String key = String.valueOf(keySet[k]);
                    if (validateKey.contains(key))
                    {
                        continue;
                    }
                    if (key.toLowerCase().startsWith("x"))
                    {
                        String yName = "y" + key.substring(1, key.length());
                        polygon.addPoint(new Point(key, points.get(key), yName, points.get(yName)));
                        validateKey.add(key);
                        validateKey.add(yName);
                        points.remove(yName);
                    }
                    else if (key.toLowerCase().startsWith("y"))
                    {
                        String xName = "x" + key.substring(1, key.length());
                        polygon.addPoint(new Point(key, points.get(key), xName, points.get(xName)));
                        validateKey.add(key);
                        validateKey.add(xName);
                        points.remove(xName);
                    }
                    else
                    {
                        throw new RuntimeException("Polygon should start with x or y");
                    }
                    points.remove(key);
                }
                position = polygon;
            }
            else if (PositionType.DASHED.name().equalsIgnoreCase(objectNodeName))
            {
                NodeList dashedNodeList = objectNodeList.item(j).getChildNodes();
                String x1 = null;
                String y1 = null;
                String x2 = null;
                String y2 = null;
                for (int k = 0; k < dashedNodeList.getLength(); k++)
                {
                    String dashedNodeName = dashedNodeList.item(k).getNodeName();
                    if (FieldName.X1.equalsIgnoreCase(dashedNodeName))
                    {
                        x1 = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.Y1.equalsIgnoreCase(dashedNodeName))
                    {
                        y1 = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.X2.equalsIgnoreCase(dashedNodeName))
                    {
                        x2 = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.Y2.equalsIgnoreCase(dashedNodeName))
                    {
                        y2 = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                }
                position = new Dashed(x1, y1, x2, y2);
            }
            else if (PositionType.LINE.name().equalsIgnoreCase(objectNodeName))
            {
                NodeList dashedNodeList = objectNodeList.item(j).getChildNodes();
                String x1 = null;
                String y1 = null;
                String x2 = null;
                String y2 = null;
                for (int k = 0; k < dashedNodeList.getLength(); k++)
                {
                    String dashedNodeName = dashedNodeList.item(k).getNodeName();
                    if (FieldName.X1.equalsIgnoreCase(dashedNodeName))
                    {
                        x1 = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.Y1.equalsIgnoreCase(dashedNodeName))
                    {
                        y1 = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.X2.equalsIgnoreCase(dashedNodeName))
                    {
                        x2 = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.Y2.equalsIgnoreCase(dashedNodeName))
                    {
                        y2 = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                }
                position = new Line(x1, y1, x2, y2);
            }
            else if (PositionType.CIRCLE.name().equalsIgnoreCase(objectNodeName))
            {
                NodeList dashedNodeList = objectNodeList.item(j).getChildNodes();
                String cx = null;
                String cy = null;
                String r = null;
                for (int k = 0; k < dashedNodeList.getLength(); k++)
                {
                    String circleNodeName = dashedNodeList.item(k).getNodeName();
                    if (FieldName.CX.equalsIgnoreCase(circleNodeName))
                    {
                        cx = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.CY.equalsIgnoreCase(circleNodeName))
                    {
                        cy = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.R.equalsIgnoreCase(circleNodeName))
                    {
                        r = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                }
                position = new Circle(cx, cy, r);
            }
            else if (PositionType.POINT.name().equalsIgnoreCase(objectNodeName))
            {
                NodeList dashedNodeList = objectNodeList.item(j).getChildNodes();
                String x = null;
                String y = null;
                for (int k = 0; k < dashedNodeList.getLength(); k++)
                {
                    String pointNodeName = dashedNodeList.item(k).getNodeName();
                    if (FieldName.X.equalsIgnoreCase(pointNodeName))
                    {
                        x = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                    else if (FieldName.Y.equalsIgnoreCase(pointNodeName))
                    {
                        y = dashedNodeList.item(k).getFirstChild().getNodeValue();
                    }
                }
                position = new Point(x, y);
            }
        }
        return new VOCObject(name, properties, pose, truncated, occluded, difficult, confidence, position, parts);
    }
    
    private String getMandatoryNodeValue(NodeList nodeList, int i, String errorMsg)
    {
        if (null == nodeList.item(i).getFirstChild())
        {
            throw new IllegalArgumentException(errorMsg);
        }
        else
        {
            return nodeList.item(i).getFirstChild().getNodeValue();
        }
    }
    
    private String getOptionalNodeValue(NodeList nodeList, int i, String msg)
    {
        if (null == nodeList.item(i).getFirstChild())
        {
            LOGGER.info(msg);
            return null;
        }
        else
        {
            return nodeList.item(i).getFirstChild().getNodeValue();
        }
    }
    
    /**
     * parseXML Document to  PascalVocIO object
     *
     * @param document Document object
     * @return PascalVocIO object
     */
    private PascalVocIO parseXML(Document document)
    {
        NodeList nodeList = (document.getElementsByTagName(ANNOTATIONS).item(0).getChildNodes());
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            String nodeName = nodeList.item(i).getNodeName();
            if (FieldName.FOLDER_NAME.equalsIgnoreCase(nodeName))
            {
                this.folder =
                    getMandatoryNodeValue(nodeList, i, FieldName.FOLDER_NAME + " can't be empty in VOC file!");
            }
            else if (FieldName.FILE_NAME.equalsIgnoreCase(nodeName))
            {
                this.fileName =
                    getMandatoryNodeValue(nodeList, i, FieldName.FILE_NAME + " can't be empty in VOC file!");
            }
            else if (FieldName.SOURCE.equalsIgnoreCase(nodeName))
            {
                NodeList sourceNodeList = nodeList.item(i).getChildNodes();
                String database = null;
                String annotation = null;
                String image = null;
                for (int j = 0; j < sourceNodeList.getLength(); j++)
                {
                    String sourceNodeName = sourceNodeList.item(j).getNodeName();
                    if (FieldName.DATABASE.equalsIgnoreCase(sourceNodeName))
                    {
                        database =
                            getOptionalNodeValue(sourceNodeList, j, FieldName.DATABASE + " is empty in VOC file!");
                    }
                    else if (FieldName.ANNOTATIONS.equalsIgnoreCase(sourceNodeName))
                    {
                        annotation =
                            getOptionalNodeValue(sourceNodeList, j, FieldName.ANNOTATIONS + " is empty in VOC file!");
                    }
                    else if (FieldName.IMAGE.equalsIgnoreCase(sourceNodeName))
                    {
                        image = getOptionalNodeValue(sourceNodeList, j, FieldName.IMAGE + " is empty in VOC file!");
                    }
                }
                source = new Source(database, annotation, image);
            }
            else if (FieldName.SIZE.equalsIgnoreCase(nodeName))
            {
                NodeList sizeNodeList = nodeList.item(i).getChildNodes();
                for (int j = 0; j < sizeNodeList.getLength(); j++)
                {
                    String sourceNodeName = sizeNodeList.item(j).getNodeName();
                    if (FieldName.WIDTH.equalsIgnoreCase(sourceNodeName))
                    {
                        this.width =
                            getMandatoryNodeValue(sizeNodeList, j, FieldName.WIDTH + " can't be empty in VOC file!");
                    }
                    else if (FieldName.HEIGHT.equalsIgnoreCase(sourceNodeName))
                    {
                        this.height =
                            getMandatoryNodeValue(sizeNodeList, j, FieldName.HEIGHT + " can't be empty in VOC file!");
                    }
                    else if (FieldName.DEPTH.equalsIgnoreCase(sourceNodeName))
                    {
                        this.depth =
                            getMandatoryNodeValue(sizeNodeList, j, FieldName.DEPTH + " can't be empty in VOC file!");
                    }
                }
            }
            else if (FieldName.SEGMENTED.equalsIgnoreCase(nodeName))
            {
                this.segmented =
                    getMandatoryNodeValue(nodeList, i, FieldName.SEGMENTED + " can't be empty in VOC file!");
            }
            else if (FieldName.OBJECT.equalsIgnoreCase(nodeName))
            {
                vocObjects.add(getVOCObject(nodeList.item(i).getChildNodes()));
            }
            else
            {
                LOGGER.warn(nodeName + " is Unrecognized.");
            }
        }
        return this;
    }
    
    /**
     * parse pascal VOC XML file by given file path
     *
     * @param filePath pascal VOC XML file path
     * @return PascalVocIO object
     * @throws RuntimeException
     */
    public PascalVocIO parseXML(String filePath)
    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try
        {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(filePath);
            return parseXML(document);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            String msg = String.format("Can't parse the XML file, %s; The file is %s", e, filePath);
            LOGGER.error(msg);
            throw new RuntimeException(msg);
        }
    }
    
    /**
     * parse pascal VOC XML file by given file path
     *
     * @param filePath  pascal VOC XML file path
     * @param obsClient obs client, obs client should already configure ak sk and endpoint, which should available.
     * @return PascalVocIO object
     * @throws RuntimeException Can't parse the XML file
     */
    public PascalVocIO parseXML(String filePath, ObsClient obsClient)
    {
        
        String[] result = getBucketNameAndObjectKey(filePath);
        ObsObject obsObject = obsClient.getObject(result[0], result[1]);
        InputStream content = obsObject.getObjectContent();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try
        {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(content);
            return parseXML(document);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Can't parse the XML file,", e);
        }
    }
    
    public String getFolder()
    {
        return folder;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public Source getSource()
    {
        return source;
    }
    
    public String getWidth()
    {
        return width;
    }
    
    public String getHeight()
    {
        return height;
    }
    
    public String getDepth()
    {
        return depth;
    }
    
    public String getSegmented()
    {
        return segmented;
    }
    
    public List<VOCObject> getVocObjects()
    {
        return vocObjects;
    }
}
