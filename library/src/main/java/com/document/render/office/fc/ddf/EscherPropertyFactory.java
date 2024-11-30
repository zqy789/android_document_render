

package com.document.render.office.fc.ddf;


import com.document.render.office.fc.util.LittleEndian;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public final class EscherPropertyFactory {

    public List<EscherProperty> createProperties(byte[] data, int offset, short numProperties) {
        List<EscherProperty> results = new ArrayList<EscherProperty>();

        int pos = offset;


        for (int i = 0; i < numProperties; i++) {
            short propId;
            int propData;
            propId = LittleEndian.getShort(data, pos);
            propData = LittleEndian.getInt(data, pos + 2);
            short propNumber = (short) (propId & (short) 0x3FFF);
            boolean isComplex = (propId & (short) 0x8000) != 0;
            boolean isBlipId = (propId & (short) 0x4000) != 0;

            byte propertyType = EscherProperties.getPropertyType(propNumber);
            if (propertyType == EscherPropertyMetaData.TYPE_BOOLEAN)
                results.add(new EscherBoolProperty(propId, propData));
            else if (propertyType == EscherPropertyMetaData.TYPE_RGB)
                results.add(new EscherRGBProperty(propId, propData));
            else if (propertyType == EscherPropertyMetaData.TYPE_SHAPEPATH)
                results.add(new EscherShapePathProperty(propId, propData));
            else {
                if (!isComplex)
                    results.add(new EscherSimpleProperty(propId, propData));
                else {
                    if (propertyType == EscherPropertyMetaData.TYPE_ARRAY)
                        results.add(new EscherArrayProperty(propId, new byte[propData]));
                    else
                        results.add(new EscherComplexProperty(propId, new byte[propData]));
                }
            }
            pos += 6;

        }


        for (Iterator<EscherProperty> iterator = results.iterator(); iterator.hasNext(); ) {
            EscherProperty p = iterator.next();
            if (p instanceof EscherComplexProperty) {
                if (p instanceof EscherArrayProperty) {
                    pos += ((EscherArrayProperty) p).setArrayData(data, pos);
                } else {
                    byte[] complexData = ((EscherComplexProperty) p).getComplexData();
                    System.arraycopy(data, pos, complexData, 0, complexData.length);
                    pos += complexData.length;
                }
            }
        }
        return results;
    }
}
