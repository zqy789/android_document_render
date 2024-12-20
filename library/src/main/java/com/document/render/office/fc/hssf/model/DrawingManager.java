

package com.document.render.office.fc.hssf.model;


import com.document.render.office.fc.ddf.EscherDgRecord;
import com.document.render.office.fc.ddf.EscherDggRecord;

import java.util.HashMap;
import java.util.Map;


public class DrawingManager {
    EscherDggRecord dgg;
    Map dgMap = new HashMap();

    public DrawingManager(EscherDggRecord dgg) {
        this.dgg = dgg;
    }

    public EscherDgRecord createDgRecord() {
        EscherDgRecord dg = new EscherDgRecord();
        dg.setRecordId(EscherDgRecord.RECORD_ID);
        short dgId = findNewDrawingGroupId();
        dg.setOptions((short) (dgId << 4));
        dg.setNumShapes(0);
        dg.setLastMSOSPID(-1);
        dgg.addCluster(dgId, 0);
        dgg.setDrawingsSaved(dgg.getDrawingsSaved() + 1);
        dgMap.put(Short.valueOf(dgId), dg);
        return dg;
    }


    public int allocateShapeId(short drawingGroupId) {

        EscherDgRecord dg = (EscherDgRecord) dgMap.get(Short.valueOf(drawingGroupId));
        int lastShapeId = dg.getLastMSOSPID();



        int newShapeId = 0;
        if (lastShapeId % 1024 == 1023) {


            newShapeId = findFreeSPIDBlock();

            dgg.addCluster(drawingGroupId, 1);
        } else {


            for (int i = 0; i < dgg.getFileIdClusters().length; i++) {
                EscherDggRecord.FileIdCluster c = dgg.getFileIdClusters()[i];
                if (c.getDrawingGroupId() == drawingGroupId) {
                    if (c.getNumShapeIdsUsed() != 1024) {

                        c.incrementShapeId();
                    }
                }

                if (dg.getLastMSOSPID() == -1) {
                    newShapeId = findFreeSPIDBlock();
                } else {

                    newShapeId = dg.getLastMSOSPID() + 1;
                }
            }
        }

        dgg.setNumShapesSaved(dgg.getNumShapesSaved() + 1);

        if (newShapeId >= dgg.getShapeIdMax()) {


            dgg.setShapeIdMax(newShapeId + 1);
        }

        dg.setLastMSOSPID(newShapeId);

        dg.incrementShapeCount();


        return newShapeId;
    }


    short findNewDrawingGroupId() {
        short dgId = 1;
        while (drawingGroupExists(dgId))
            dgId++;
        return dgId;
    }

    boolean drawingGroupExists(short dgId) {
        for (int i = 0; i < dgg.getFileIdClusters().length; i++) {
            if (dgg.getFileIdClusters()[i].getDrawingGroupId() == dgId)
                return true;
        }
        return false;
    }

    int findFreeSPIDBlock() {
        int max = dgg.getShapeIdMax();
        int next = ((max / 1024) + 1) * 1024;
        return next;
    }

    public EscherDggRecord getDgg() {
        return dgg;
    }

}
