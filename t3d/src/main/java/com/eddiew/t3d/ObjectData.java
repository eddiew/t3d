package com.eddiew.t3d;

/**
 * Created by Eddie on 10/12/13.
 */
public class ObjectData{
    public float[] vertexData;
    public short[] drawOrder;
    public float[] modelMatrix;
    public final int objectId;
    public ObjectData(float[] vertexData, short[] drawOrder, float[] modelMatrix, int objectId){
        this.vertexData = vertexData;
        this.drawOrder = drawOrder;
        this.modelMatrix = modelMatrix;
        this.objectId = objectId;
    }
}
