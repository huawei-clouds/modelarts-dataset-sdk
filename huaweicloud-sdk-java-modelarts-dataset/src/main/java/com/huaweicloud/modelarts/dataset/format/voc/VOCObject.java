package com.huaweicloud.modelarts.dataset.format.voc;

import com.huaweicloud.modelarts.dataset.format.voc.position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * object of VOC, positive can be point, line or others.
 */
public class VOCObject
{
    private String name;
    
    private String pose;
    
    private String truncated;
    
    private String occluded;
    
    private String difficult;
    
    private String confidence;
    
    private Position position;
    
    private List<VOCObject> parts;
    
    /**
     * constructor for VOCObject
     * Please refer to http://host.robots.ox.ac.uk/pascal/VOC/voc2012/htmldoc/index.html
     *
     * @param name      object name
     * @param pose      pose value
     * @param truncated an object marked as `truncated' indicates that the bounding box specified
     *                  for the object does not correspond to the full extent of the object
     *                  e.g. an image of a person from the waist up, or a view of a car extending outside the image.
     *                  The truncated field being set to 1 indicates that the object is ``truncated'' in the image.
     * @param occluded  an object marked as `occluded' indicates that a significant portion of
     *                  the object within the bounding box is occluded by another object.
     *                  The occluded field being set to 1 indicates that the object is significantly occluded by another object.
     *                  Participants are free to use or ignore this field as they see fit.
     * @param difficult an object marked as `difficult' indicates that the object is considered
     *                  difficult to recognize, for example an object which is clearly visible but unidentifiable
     *                  without substantial use of context. Objects marked as difficult are currently
     *                  ignored in the evaluation of the challenge.
     *                  The difficult field being set to 1 indicates that the object has been annotated as ``difficult'',
     *                  for example an object which is clearly visible but difficult to recognize without substantial use of context.
     * @param position  positive can be point, line or others.
     */
    public VOCObject(String name, String pose, String truncated, String occluded, String difficult, Position position)
    {
        this.name = name;
        this.pose = pose;
        this.truncated = truncated;
        this.occluded = occluded;
        this.difficult = difficult;
        this.position = position;
        this.parts = new ArrayList<VOCObject>();
    }
    
    /**
     * constructor for VOCObject
     * Please refer to http://host.robots.ox.ac.uk/pascal/VOC/voc2012/htmldoc/index.html
     *
     * @param name       object name
     * @param pose       pose value
     * @param truncated  an object marked as `truncated' indicates that the bounding box specified
     *                   for the object does not correspond to the full extent of the object
     *                   e.g. an image of a person from the waist up, or a view of a car extending outside the image.
     *                   The truncated field being set to 1 indicates that the object is ``truncated'' in the image.
     * @param occluded   an object marked as `occluded' indicates that a significant portion of
     *                   the object within the bounding box is occluded by another object.
     *                   The occluded field being set to 1 indicates that the object is significantly occluded by another object.
     *                   Participants are free to use or ignore this field as they see fit.
     * @param difficult  an object marked as `difficult' indicates that the object is considered
     *                   difficult to recognize, for example an object which is clearly visible but unidentifiable
     *                   without substantial use of context. Objects marked as difficult are currently
     *                   ignored in the evaluation of the challenge.
     *                   The difficult field being set to 1 indicates that the object has been annotated as ``difficult'',
     *                   for example an object which is clearly visible but difficult to recognize without substantial use of context.
     * @param confidence Confidence for annotation that was annotated by machine, the value: 0 <= Confidence < 1, Optional field
     * @param position   positive can be point, line or others.
     * @param parts
     */
    public VOCObject(String name, String pose, String truncated, String occluded, String difficult, String confidence,
        Position position, List<VOCObject> parts)
    {
        this.name = name;
        this.pose = pose;
        this.truncated = truncated;
        this.occluded = occluded;
        this.difficult = difficult;
        this.confidence = confidence;
        this.position = position;
        this.parts = parts;
    }
    
    public void setParts(List<VOCObject> parts)
    {
        this.parts = parts;
    }
    
    public String getOccluded()
    {
        return occluded;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getPose()
    {
        return pose;
    }
    
    public String getTruncated()
    {
        return truncated;
    }
    
    public String getDifficult()
    {
        return difficult;
    }
    
    public String getConfidence()
    {
        return confidence;
    }
    
    public Position getPosition()
    {
        return position;
    }
    
    public List<VOCObject> getParts()
    {
        return parts;
    }
}
