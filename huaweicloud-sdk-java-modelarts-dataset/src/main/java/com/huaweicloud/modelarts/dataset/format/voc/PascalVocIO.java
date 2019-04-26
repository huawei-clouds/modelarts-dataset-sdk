package com.huaweicloud.modelarts.dataset.format.voc;

import com.huaweicloud.modelarts.dataset.FieldName;
import com.huaweicloud.modelarts.dataset.format.voc.position.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

import static com.huaweicloud.modelarts.dataset.FieldName.ANNOTATIONS;

/**
 * main class for parsing pascal VOC format
 */
public class PascalVocIO {
  private String folder;
  private String fileName;
  private Source source;
  private String width;
  private String height;
  private String depth;
  private String segmented;
  private List<VOCObject> vocObjects = new ArrayList<>();

  public PascalVocIO(String filePath) {
    parseXML(filePath);
  }

  private static final org.apache.log4j.Logger LOGGER =
      org.apache.log4j.Logger.getLogger(PascalVocIO.class.getName());

  /**
   * get voc object
   *
   * @param objectNodeList objectNode List
   * @return VOCObject
   */
  public VOCObject getVOCObject(NodeList objectNodeList) {
    String name = null;
    String pose = null;
    String truncated = null;
    String occluded = null;
    String difficult = null;
    String xMin = null;
    String yMin = null;
    String xMax = null;
    String yMax = null;
    Position position = null;
    List<VOCObject> parts = new ArrayList<>();
    for (int j = 0; j < objectNodeList.getLength(); j++) {
      String objectNodeName = objectNodeList.item(j).getNodeName();
      if (FieldName.NAME.equalsIgnoreCase(objectNodeName)) {
        name = objectNodeList.item(j).getFirstChild().getNodeValue();
      } else if (FieldName.POSE.equalsIgnoreCase(objectNodeName)) {
        pose = objectNodeList.item(j).getFirstChild().getNodeValue();
      } else if (FieldName.TRUNCATED.equalsIgnoreCase(objectNodeName)) {
        truncated = objectNodeList.item(j).getFirstChild().getNodeValue();
      } else if (FieldName.OCCLUDED.equalsIgnoreCase(objectNodeName)) {
        occluded = objectNodeList.item(j).getFirstChild().getNodeValue();
      } else if (FieldName.DIFFICULT.equalsIgnoreCase(objectNodeName)) {
        difficult = objectNodeList.item(j).getFirstChild().getNodeValue();
      } else if (PositionType.BNDBOX.name().equalsIgnoreCase(objectNodeName)) {
        NodeList bndBoxNodeList = objectNodeList.item(j).getChildNodes();
        for (int k = 0; k < bndBoxNodeList.getLength(); k++) {
          String bndBoxNodeName = bndBoxNodeList.item(k).getNodeName();
          if (FieldName.XMAX.equalsIgnoreCase(bndBoxNodeName)) {
            xMax = bndBoxNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.XMIN.equalsIgnoreCase(bndBoxNodeName)) {
            xMin = bndBoxNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.YMAX.equalsIgnoreCase(bndBoxNodeName)) {
            yMax = bndBoxNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.YMIN.equalsIgnoreCase(bndBoxNodeName)) {
            yMin = bndBoxNodeList.item(k).getFirstChild().getNodeValue();
          }
          position = new BNDBox(xMin, yMin, xMax, yMax);
        }
      } else if (FieldName.PART.equalsIgnoreCase(objectNodeName)) {
        NodeList bndBoxNodeList = objectNodeList.item(j).getChildNodes();
        if (null == parts) {
          parts = new ArrayList<>();
        }
        parts.add(getVOCObject(bndBoxNodeList));
      } else if (PositionType.POLYGON.name().equalsIgnoreCase(objectNodeName)) {
        NodeList polygonNodeList = objectNodeList.item(j).getChildNodes();
        Polygon polygon = new Polygon();
        Map<String, String> points = new LinkedHashMap<>();
        for (int k = 0; k < polygonNodeList.getLength(); k++) {
          String polygonNodeName = polygonNodeList.item(k).getNodeName();
          if (polygonNodeName.toLowerCase().startsWith("x") || polygonNodeName.toLowerCase().startsWith("y")) {
            points.put(polygonNodeName,
                polygonNodeList.item(k).getFirstChild().getNodeValue());
          } else if (polygonNodeName.toLowerCase().startsWith("#text")) {
          } else {
            LOGGER.warn(polygonNodeName + " is Unrecognized in polygon.");
          }
        }
        Object[] keySet = points.keySet().toArray();
        Set<String> validateKey = new HashSet<>();
        for (int k = 0; k < keySet.length; k++) {
          String key = String.valueOf(keySet[k]);
          if (validateKey.contains(key)) {
            continue;
          }
          if (key.toLowerCase().startsWith("x")) {
            String yName = "y" + key.substring(1, key.length());
            polygon.addPoint(new Point(key, points.get(key), yName, points.get(yName)));
            validateKey.add(key);
            validateKey.add(yName);
            points.remove(yName);
          } else if (key.toLowerCase().startsWith("y")) {
            String xName = "x" + key.substring(1, key.length());
            polygon.addPoint(new Point(key, points.get(key), xName, points.get(xName)));
            validateKey.add(key);
            validateKey.add(xName);
            points.remove(xName);
          } else {
            throw new RuntimeException("Polygon should start with x or y");
          }
          points.remove(key);
        }
        position = polygon;
      } else if (PositionType.DASHED.name().equalsIgnoreCase(objectNodeName)) {
        NodeList dashedNodeList = objectNodeList.item(j).getChildNodes();
        String x1 = null;
        String y1 = null;
        String x2 = null;
        String y2 = null;
        for (int k = 0; k < dashedNodeList.getLength(); k++) {
          String dashedNodeName = dashedNodeList.item(k).getNodeName();
          if (FieldName.X1.equalsIgnoreCase(dashedNodeName)) {
            x1 = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.Y1.equalsIgnoreCase(dashedNodeName)) {
            y1 = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.X2.equalsIgnoreCase(dashedNodeName)) {
            x2 = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.Y2.equalsIgnoreCase(dashedNodeName)) {
            y2 = dashedNodeList.item(k).getFirstChild().getNodeValue();
          }
        }
        position = new Dashed(x1, y1, x2, y2);
      } else if (PositionType.LINE.name().equalsIgnoreCase(objectNodeName)) {
        NodeList dashedNodeList = objectNodeList.item(j).getChildNodes();
        String x1 = null;
        String y1 = null;
        String x2 = null;
        String y2 = null;
        for (int k = 0; k < dashedNodeList.getLength(); k++) {
          String dashedNodeName = dashedNodeList.item(k).getNodeName();
          if (FieldName.X1.equalsIgnoreCase(dashedNodeName)) {
            x1 = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.Y1.equalsIgnoreCase(dashedNodeName)) {
            y1 = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.X2.equalsIgnoreCase(dashedNodeName)) {
            x2 = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.Y2.equalsIgnoreCase(dashedNodeName)) {
            y2 = dashedNodeList.item(k).getFirstChild().getNodeValue();
          }
        }
        position = new Line(x1, y1, x2, y2);
      } else if (PositionType.CIRCLE.name().equalsIgnoreCase(objectNodeName)) {
        NodeList dashedNodeList = objectNodeList.item(j).getChildNodes();
        String cx = null;
        String cy = null;
        String r = null;
        for (int k = 0; k < dashedNodeList.getLength(); k++) {
          String circleNodeName = dashedNodeList.item(k).getNodeName();
          if (FieldName.CX.equalsIgnoreCase(circleNodeName)) {
            cx = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.CY.equalsIgnoreCase(circleNodeName)) {
            cy = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.R.equalsIgnoreCase(circleNodeName)) {
            r = dashedNodeList.item(k).getFirstChild().getNodeValue();
          }
        }
        position = new Circle(cx, cy, r);
      } else if (PositionType.POINT.name().equalsIgnoreCase(objectNodeName)) {
        NodeList dashedNodeList = objectNodeList.item(j).getChildNodes();
        String x = null;
        String y = null;
        for (int k = 0; k < dashedNodeList.getLength(); k++) {
          String pointNodeName = dashedNodeList.item(k).getNodeName();
          if (FieldName.X.equalsIgnoreCase(pointNodeName)) {
            x = dashedNodeList.item(k).getFirstChild().getNodeValue();
          } else if (FieldName.Y.equalsIgnoreCase(pointNodeName)) {
            y = dashedNodeList.item(k).getFirstChild().getNodeValue();
          }
        }
        position = new Point(x, y);
      }
    }
    return new VOCObject(name, pose, truncated, occluded, difficult, position, parts);
  }

  /**
   * parse pascal VOC XML file by given file path
   *
   * @param filePath pascal VOC XML file path
   * @return PascalVocIO object
   * @throws RuntimeException
   */
  public PascalVocIO parseXML(String filePath) throws RuntimeException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.parse(filePath);
      NodeList nodeList = (document.getElementsByTagName(ANNOTATIONS).item(0).getChildNodes());
      for (int i = 0; i < nodeList.getLength(); i++) {
        String nodeName = nodeList.item(i).getNodeName();
        if (FieldName.FOLDER_NAME.equalsIgnoreCase(nodeName)) {
          this.folder = nodeList.item(i).getFirstChild().getNodeValue();
        } else if (FieldName.FILE_NAME.equalsIgnoreCase(nodeName)) {
          this.fileName = nodeList.item(i).getFirstChild().getNodeValue();
        } else if (FieldName.SOURCE.equalsIgnoreCase(nodeName)) {
          NodeList sourceNodeList = nodeList.item(i).getChildNodes();
          String database = null;
          String annotation = null;
          String image = null;
          for (int j = 0; j < sourceNodeList.getLength(); j++) {
            String sourceNodeName = sourceNodeList.item(j).getNodeName();
            if (FieldName.DATABASE.equalsIgnoreCase(sourceNodeName)) {
              database = sourceNodeList.item(j).getFirstChild().getNodeValue();
            } else if (FieldName.ANNOTATIONS.equalsIgnoreCase(sourceNodeName)) {
              annotation = sourceNodeList.item(j).getFirstChild().getNodeValue();
            } else if (FieldName.IMAGE.equalsIgnoreCase(sourceNodeName)) {
              image = sourceNodeList.item(j).getFirstChild().getNodeValue();
            }
          }
          source = new Source(database, annotation, image);
        } else if (FieldName.SIZE.equalsIgnoreCase(nodeName)) {
          NodeList sizeNodeList = nodeList.item(i).getChildNodes();
          for (int j = 0; j < sizeNodeList.getLength(); j++) {
            String sourceNodeName = sizeNodeList.item(j).getNodeName();
            if (FieldName.WIDTH.equalsIgnoreCase(sourceNodeName)) {
              this.width = sizeNodeList.item(j).getFirstChild().getNodeValue();
            } else if (FieldName.HEIGHT.equalsIgnoreCase(sourceNodeName)) {
              this.height = sizeNodeList.item(j).getFirstChild().getNodeValue();
            } else if (FieldName.DEPTH.equalsIgnoreCase(sourceNodeName)) {
              this.depth = sizeNodeList.item(j).getFirstChild().getNodeValue();
            }
          }
        } else if (FieldName.SEGMENTED.equalsIgnoreCase(nodeName)) {
          this.segmented = nodeList.item(i).getFirstChild().getNodeValue();
        } else if (FieldName.OBJECT.equalsIgnoreCase(nodeName)) {
          vocObjects.add(getVOCObject(nodeList.item(i).getChildNodes()));
        } else {
          LOGGER.warn(nodeName + " is Unrecognized.");
        }
      }
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return this;
  }

  public String getFolder() {
    return folder;
  }

  public String getFileName() {
    return fileName;
  }

  public Source getSource() {
    return source;
  }

  public String getWidth() {
    return width;
  }

  public String getHeight() {
    return height;
  }

  public String getDepth() {
    return depth;
  }

  public String getSegmented() {
    return segmented;
  }

  public List<VOCObject> getVocObjects() {
    return vocObjects;
  }
}
