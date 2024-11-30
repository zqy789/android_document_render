

package com.document.render.office.fc.hslf.model.textproperties;

import com.document.render.office.fc.hslf.record.StyleTextPropAtom;
import com.document.render.office.fc.util.LittleEndian;

import java.util.LinkedList;



public class TextPropCollection {
    private int charactersCovered;
    private short reservedField;
    private LinkedList<TextProp> textPropList;
    private int maskSpecial = 0;

    
    public TextPropCollection(int charactersCovered, short reservedField) {
        this.charactersCovered = charactersCovered;
        this.reservedField = reservedField;
        textPropList = new LinkedList<TextProp>();
    }

    
    public TextPropCollection(int textSize) {
        charactersCovered = textSize;
        reservedField = -1;
        textPropList = new LinkedList<TextProp>();
    }

    public int getSpecialMask() {
        return maskSpecial;
    }

    
    public int getCharactersCovered() {
        return charactersCovered;
    }

    
    public LinkedList<TextProp> getTextPropList() {
        return textPropList;
    }

    
    public TextProp findByName(String textPropName) {
        for (int i = 0; i < textPropList.size(); i++) {
            TextProp prop = textPropList.get(i);
            if (prop.getName().equals(textPropName)) {
                return prop;
            }
        }
        return null;
    }

    
    public TextProp addWithName(String name) {
        
        TextProp base = null;
        for (int i = 0; i < StyleTextPropAtom.characterTextPropTypes.length; i++) {
            if (StyleTextPropAtom.characterTextPropTypes[i].getName().equals(name)) {
                base = StyleTextPropAtom.characterTextPropTypes[i];
            }
        }
        for (int i = 0; i < StyleTextPropAtom.paragraphTextPropTypes.length; i++) {
            if (StyleTextPropAtom.paragraphTextPropTypes[i].getName().equals(name)) {
                base = StyleTextPropAtom.paragraphTextPropTypes[i];
            }
        }
        if (base == null) {
            throw new IllegalArgumentException("No TextProp with name " + name
                    + " is defined to add from");
        }

        
        TextProp textProp = (TextProp) base.clone();
        int pos = 0;
        for (int i = 0; i < textPropList.size(); i++) {
            TextProp curProp = textPropList.get(i);
            if (textProp.getMask() > curProp.getMask()) {
                pos++;
            }
        }
        textPropList.add(pos, textProp);
        return textProp;
    }

    
    public int buildTextPropList(int containsField, TextProp[] potentialProperties, byte[] data,
                                 int dataOffset) {
        int bytesPassed = 0;

        
        
        for (int i = 0; i < potentialProperties.length; i++) {
            

            
            if ((containsField & potentialProperties[i].getMask()) != 0) {
                if (dataOffset + bytesPassed >= data.length) {
                    
                    
                    maskSpecial |= potentialProperties[i].getMask();
                    return bytesPassed;
                }

                
                TextProp prop = (TextProp) potentialProperties[i].clone();
                int val = 0;
                if (prop.getSize() == 2) {
                    val = LittleEndian.getShort(data, dataOffset + bytesPassed);
                } else if (prop.getSize() == 4) {
                    val = LittleEndian.getInt(data, dataOffset + bytesPassed);
                } else if (prop.getSize() == 0) {
                    
                    maskSpecial |= potentialProperties[i].getMask();
                    continue;
                }
                if (CharFlagsTextProp.NAME.equals(prop.getName()) && val < 0) {
                    val = 0;
                }
                prop.setValue(val);
                bytesPassed += prop.getSize();
                if ("tabStops".equals(prop.getName())) {
                    bytesPassed += val * 4;
                }
                textPropList.add(prop);
            }
        }

        
        return bytesPassed;
    }

    
    public void updateTextSize(int textSize) {
        charactersCovered = textSize;
    }

    public short getReservedField() {
        return reservedField;
    }

    public void setReservedField(short val) {
        reservedField = val;
    }

    
    public void dispose() {
        if (textPropList != null) {
            for (int i = 0; i < textPropList.size(); i++) {
                textPropList.get(i).dispose();
            }
            textPropList.clear();
            textPropList = null;
        }
    }
}
