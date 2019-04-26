package com.huaweicloud.modelarts.dataset.format.voc;

import com.huaweicloud.modelarts.dataset.format.voc.position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * object of VOC, positive can be point, line or others.
 */
public class VOCObject {
  private String name;
  private String pose;
  private String truncated;
  private String occluded;
  private String difficult;
  private Position position;
  private List<VOCObject> parts;

  /**
   * constructor for VOCObject
   *
   * @param name      object name
   * @param pose      pose value
   * @param truncated an object marked as `truncated' indicates that the bounding box specified
   *                  for the object does not correspond to the full extent of the object
   *                  e.g. an image of a person from the waist up, or a view of a car extending outside the image.
   * @param occluded  an object marked as `occluded' indicates that a significant portion of
   *                  the object within the bounding box is occluded by another object.
   * @param difficult an object marked as `difficult' indicates that the object is considered
   *                  difficult to recognize, for example an object which is clearly visible but unidentifiable
   *                  without substantial use of context. Objects marked as difficult are currently
   *                  ignored in the evaluation of the challenge.
   * @param position  positive can be point, line or others.
   */
  public VOCObject(String name, String pose, String truncated, String occluded, String difficult, Position position) {
    this.name = name;
    this.pose = pose;
    this.truncated = truncated;
    this.occluded = occluded;
    this.difficult = difficult;
    this.position = position;
    this.parts = new ArrayList<>();
  }

  public VOCObject(String name, String pose, String truncated, String occluded, String difficult, Position position, List<VOCObject> parts) {
    this.name = name;
    this.pose = pose;
    this.truncated = truncated;
    this.occluded = occluded;
    this.difficult = difficult;
    this.position = position;
    this.parts = parts;
  }

  public void setParts(List<VOCObject> parts) {
    this.parts = parts;
  }

  public String getOccluded() {
    return occluded;
  }

  public String getName() {
    return name;
  }

  public String getPose() {
    return pose;
  }

  public String getTruncated() {
    return truncated;
  }

  public String getDifficult() {
    return difficult;
  }

  public Position getPosition() {
    return position;
  }

  public List<VOCObject> getParts() {
    return parts;
  }
}
