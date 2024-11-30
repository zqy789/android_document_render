

package com.document.render.office.fc.hpsf;

import com.document.render.office.fc.util.LittleEndian;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class Section {


    protected Map<Long, String> dictionary;


    protected ClassID formatID;

    protected long offset;

    protected int size;

    protected Property[] properties;

    private boolean wasNull;



    protected Section() {
    }



    public Section(final byte[] src, final int offset)
            throws UnsupportedEncodingException {
        int o1 = offset;


        formatID = new ClassID(src, o1);
        o1 += ClassID.LENGTH;


        this.offset = LittleEndian.getUInt(src, o1);
        o1 = (int) this.offset;


        size = (int) LittleEndian.getUInt(src, o1);
        o1 += LittleEndian.INT_SIZE;


        final int propertyCount = (int) LittleEndian.getUInt(src, o1);
        o1 += LittleEndian.INT_SIZE;


        properties = new Property[propertyCount];


        int pass1Offset = o1;
        final List<PropertyListEntry> propertyList = new ArrayList<PropertyListEntry>(propertyCount);
        PropertyListEntry ple;
        for (int i = 0; i < properties.length; i++) {
            ple = new PropertyListEntry();


            ple.id = (int) LittleEndian.getUInt(src, pass1Offset);
            pass1Offset += LittleEndian.INT_SIZE;


            ple.offset = (int) LittleEndian.getUInt(src, pass1Offset);
            pass1Offset += LittleEndian.INT_SIZE;


            propertyList.add(ple);
        }


        Collections.sort(propertyList);


        for (int i = 0; i < propertyCount - 1; i++) {
            PropertyListEntry ple1 = propertyList.get(i);
            PropertyListEntry ple2 = propertyList.get(i + 1);
            ple1.length = ple2.offset - ple1.offset;
        }
        if (propertyCount > 0) {
            ple = propertyList.get(propertyCount - 1);
            ple.length = size - ple.offset;
        }


        int codepage = -1;
        for (final Iterator<PropertyListEntry> i = propertyList.iterator();
             codepage == -1 && i.hasNext(); ) {
            ple = i.next();


            if (ple.id == PropertyIDMap.PID_CODEPAGE) {

                int o = (int) (this.offset + ple.offset);
                final long type = LittleEndian.getUInt(src, o);
                o += LittleEndian.INT_SIZE;

                if (type != Variant.VT_I2)
                    throw new HPSFRuntimeException
                            ("Value type of property ID 1 is not VT_I2 but " +
                                    type + ".");


                codepage = LittleEndian.getUShort(src, o);
            }
        }


        int i1 = 0;
        for (final Iterator<PropertyListEntry> i = propertyList.iterator(); i.hasNext(); ) {
            ple = i.next();
            Property p = new Property(ple.id, src,
                    this.offset + ple.offset,
                    ple.length, codepage);
            if (p.getID() == PropertyIDMap.PID_CODEPAGE)
                p = new Property(p.getID(), p.getType(), Integer.valueOf(codepage));
            properties[i1++] = p;
        }


        dictionary = (Map) getProperty(0);
    }


    public ClassID getFormatID() {
        return formatID;
    }


    public long getOffset() {
        return offset;
    }


    public int getSize() {
        return size;
    }


    public int getPropertyCount() {
        return properties.length;
    }


    public Property[] getProperties() {
        return properties;
    }


    public Object getProperty(final long id) {
        wasNull = false;
        for (int i = 0; i < properties.length; i++)
            if (id == properties[i].getID())
                return properties[i].getValue();
        wasNull = true;
        return null;
    }



    protected int getPropertyIntValue(final long id) {
        final Number i;
        final Object o = getProperty(id);
        if (o == null)
            return 0;
        if (!(o instanceof Long || o instanceof Integer))
            throw new HPSFRuntimeException
                    ("This property is not an integer type, but " +
                            o.getClass().getName() + ".");
        i = (Number) o;
        return i.intValue();
    }



    protected boolean getPropertyBooleanValue(final int id) {
        final Boolean b = (Boolean) getProperty(id);
        if (b == null) {
            return false;
        }
        return b.booleanValue();
    }


    public boolean wasNull() {
        return wasNull;
    }


    public String getPIDString(final long pid) {
        String s = null;
        if (dictionary != null)
            s = (String) dictionary.get(Long.valueOf(pid));
        if (s == null)
            s = SectionIDMap.getPIDString(getFormatID().getBytes(), pid);
        if (s == null)
            s = SectionIDMap.UNDEFINED;
        return s;
    }


    public boolean equals(final Object o) {
        if (o == null || !(o instanceof Section))
            return false;
        final Section s = (Section) o;
        if (!s.getFormatID().equals(getFormatID()))
            return false;


        Property[] pa1 = new Property[getProperties().length];
        Property[] pa2 = new Property[s.getProperties().length];
        System.arraycopy(getProperties(), 0, pa1, 0, pa1.length);
        System.arraycopy(s.getProperties(), 0, pa2, 0, pa2.length);


        Property p10 = null;
        Property p20 = null;
        for (int i = 0; i < pa1.length; i++) {
            final long id = pa1[i].getID();
            if (id == 0) {
                p10 = pa1[i];
                pa1 = remove(pa1, i);
                i--;
            }
            if (id == 1) {

                pa1 = remove(pa1, i);
                i--;
            }
        }
        for (int i = 0; i < pa2.length; i++) {
            final long id = pa2[i].getID();
            if (id == 0) {
                p20 = pa2[i];
                pa2 = remove(pa2, i);
                i--;
            }
            if (id == 1) {

                pa2 = remove(pa2, i);
                i--;
            }
        }


        if (pa1.length != pa2.length)
            return false;


        boolean dictionaryEqual = true;
        if (p10 != null && p20 != null)
            dictionaryEqual = p10.getValue().equals(p20.getValue());
        else if (p10 != null || p20 != null)
            dictionaryEqual = false;
        if (dictionaryEqual) {
            return Util.equals(pa1, pa2);
        }
        return false;
    }


    private Property[] remove(final Property[] pa, final int i) {
        final Property[] h = new Property[pa.length - 1];
        if (i > 0)
            System.arraycopy(pa, 0, h, 0, i);
        System.arraycopy(pa, i + 1, h, i, h.length - i);
        return h;
    }


    public int hashCode() {
        long hashCode = 0;
        hashCode += getFormatID().hashCode();
        final Property[] pa = getProperties();
        for (int i = 0; i < pa.length; i++)
            hashCode += pa[i].hashCode();
        final int returnHashCode = (int) (hashCode & 0x0ffffffffL);
        return returnHashCode;
    }


    public String toString() {
        final StringBuffer b = new StringBuffer();
        final Property[] pa = getProperties();
        b.append(getClass().getName());
        b.append('[');
        b.append("formatID: ");
        b.append(getFormatID());
        b.append(", offset: ");
        b.append(getOffset());
        b.append(", propertyCount: ");
        b.append(getPropertyCount());
        b.append(", size: ");
        b.append(getSize());
        b.append(", properties: [\n");
        for (int i = 0; i < pa.length; i++) {
            b.append(pa[i].toString());
            b.append(",\n");
        }
        b.append(']');
        b.append(']');
        return b.toString();
    }


    public Map<Long, String> getDictionary() {
        return dictionary;
    }


    public int getCodepage() {
        final Integer codepage =
                (Integer) getProperty(PropertyIDMap.PID_CODEPAGE);
        if (codepage == null)
            return -1;
        int cp = codepage.intValue();
        return cp;
    }


    class PropertyListEntry implements Comparable<PropertyListEntry> {
        int id;
        int offset;
        int length;


        public int compareTo(final PropertyListEntry o) {
            final int otherOffset = o.offset;
            if (offset < otherOffset)
                return -1;
            else if (offset == otherOffset)
                return 0;
            else
                return 1;
        }

        public String toString() {
            final StringBuffer b = new StringBuffer();
            b.append(getClass().getName());
            b.append("[id=");
            b.append(id);
            b.append(", offset=");
            b.append(offset);
            b.append(", length=");
            b.append(length);
            b.append(']');
            return b.toString();
        }
    }

}
