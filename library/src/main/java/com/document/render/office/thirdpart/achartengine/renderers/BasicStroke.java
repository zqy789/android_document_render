
package com.document.render.office.thirdpart.achartengine.renderers;

import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;

import java.io.Serializable;


public class BasicStroke implements Serializable {
    
    public static final BasicStroke SOLID = new BasicStroke(Cap.BUTT, Join.MITER, 4, null, 0);
    
    public static final BasicStroke DASHED = new BasicStroke(Cap.ROUND, Join.BEVEL, 10, new float[]{
            10, 10}, 1);
    
    public static final BasicStroke DOTTED = new BasicStroke(Cap.ROUND, Join.BEVEL, 5, new float[]{
            2, 10}, 1);
    
    private Cap mCap;
    
    private Join mJoin;
    
    private float mMiter;
    
    private float[] mIntervals;
    
    private float mPhase;

    
    public BasicStroke(Cap cap, Join join, float miter, float[] intervals, float phase) {
        mCap = cap;
        mJoin = join;
        mMiter = miter;
        mIntervals = intervals;
    }

    
    public Cap getCap() {
        return mCap;
    }

    
    public Join getJoin() {
        return mJoin;
    }

    
    public float getMiter() {
        return mMiter;
    }

    
    public float[] getIntervals() {
        return mIntervals;
    }

    
    public float getPhase() {
        return mPhase;
    }

}
