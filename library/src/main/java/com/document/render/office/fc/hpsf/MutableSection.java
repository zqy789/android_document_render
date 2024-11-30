

package com.document.render.office.fc.hpsf;

import com.document.render.office.fc.util.LittleEndian;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;



public class MutableSection extends Section {

    private boolean dirty = true;



    private List<Property> preprops;



    private byte[] sectionBytes;



    public MutableSection() {
        dirty = true;
        formatID = null;
        offset = -1;
        preprops = new LinkedList<Property>();
    }



    public MutableSection(final Section s) {
        setFormatID(s.getFormatID());
        final Property[] pa = s.getProperties();
        final MutableProperty[] mpa = new MutableProperty[pa.length];
        for (int i = 0; i < pa.length; i++)
            mpa[i] = new MutableProperty(pa[i]);
        setProperties(mpa);
        setDictionary(s.getDictionary());
    }


    private static int writeDictionary(final OutputStream out,
                                       final Map<Long, String> dictionary, final int codepage)
            throws IOException {
        int length = TypeWriter.writeUIntToStream(out, dictionary.size());
        for (final Iterator<Long> i = dictionary.keySet().iterator(); i.hasNext(); ) {
            final Long key = i.next();
            final String value = dictionary.get(key);

            if (codepage == Constants.CP_UNICODE) {

                int sLength = value.length() + 1;
                if (sLength % 2 == 1)
                    sLength++;
                length += TypeWriter.writeUIntToStream(out, key.longValue());
                length += TypeWriter.writeUIntToStream(out, sLength);
                final byte[] ca =
                        value.getBytes(VariantSupport.codepageToEncoding(codepage));
                for (int j = 2; j < ca.length; j += 2) {
                    out.write(ca[j + 1]);
                    out.write(ca[j]);
                    length += 2;
                }
                sLength -= value.length();
                while (sLength > 0) {
                    out.write(0x00);
                    out.write(0x00);
                    length += 2;
                    sLength--;
                }
            } else {

                length += TypeWriter.writeUIntToStream(out, key.longValue());
                length += TypeWriter.writeUIntToStream(out, value.length() + 1);
                final byte[] ba =
                        value.getBytes(VariantSupport.codepageToEncoding(codepage));
                for (int j = 0; j < ba.length; j++) {
                    out.write(ba[j]);
                    length++;
                }
                out.write(0x00);
                length++;
            }
        }
        return length;
    }


    public void setFormatID(final ClassID formatID) {
        this.formatID = formatID;
    }


    public void setFormatID(final byte[] formatID) {
        ClassID fid = getFormatID();
        if (fid == null) {
            fid = new ClassID();
            setFormatID(fid);
        }
        fid.setBytes(formatID);
    }


    public void setProperty(final int id, final String value) {
        setProperty(id, Variant.VT_LPWSTR, value);
        dirty = true;
    }



    public void setProperty(final int id, final int value) {
        setProperty(id, Variant.VT_I4, Integer.valueOf(value));
        dirty = true;
    }



    public void setProperty(final int id, final long value) {
        setProperty(id, Variant.VT_I8, Long.valueOf(value));
        dirty = true;
    }



    public void setProperty(final int id, final boolean value) {
        setProperty(id, Variant.VT_BOOL, Boolean.valueOf(value));
        dirty = true;
    }



    public void setProperty(final int id, final long variantType,
                            final Object value) {
        final MutableProperty p = new MutableProperty();
        p.setID(id);
        p.setType(variantType);
        p.setValue(value);
        setProperty(p);
        dirty = true;
    }



    public void setProperty(final Property p) {
        final long id = p.getID();
        removeProperty(id);
        preprops.add(p);
        dirty = true;
    }



    public void removeProperty(final long id) {
        for (final Iterator<Property> i = preprops.iterator(); i.hasNext(); )
            if (i.next().getID() == id) {
                i.remove();
                break;
            }
        dirty = true;
    }



    protected void setPropertyBooleanValue(final int id, final boolean value) {
        setProperty(id, Variant.VT_BOOL, Boolean.valueOf(value));
    }



    public int getSize() {
        if (dirty) {
            try {
                size = calcSize();
                dirty = false;
            } catch (HPSFRuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new HPSFRuntimeException(ex);
            }
        }
        return size;
    }



    private int calcSize() throws WritingNotSupportedException, IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(out);
        out.close();

        sectionBytes = Util.pad4(out.toByteArray());
        return sectionBytes.length;
    }



    public int write(final OutputStream out)
            throws WritingNotSupportedException, IOException {

        if (!dirty && sectionBytes != null) {
            out.write(sectionBytes);
            return sectionBytes.length;
        }


        final ByteArrayOutputStream propertyStream =
                new ByteArrayOutputStream();


        final ByteArrayOutputStream propertyListStream =
                new ByteArrayOutputStream();


        int position = 0;


        position += 2 * LittleEndian.INT_SIZE +
                getPropertyCount() * 2 * LittleEndian.INT_SIZE;


        int codepage = -1;
        if (getProperty(PropertyIDMap.PID_DICTIONARY) != null) {
            final Object p1 = getProperty(PropertyIDMap.PID_CODEPAGE);
            if (p1 != null) {
                if (!(p1 instanceof Integer))
                    throw new IllegalPropertySetDataException
                            ("The codepage property (ID = 1) must be an " +
                                    "Integer object.");
            } else

                setProperty(PropertyIDMap.PID_CODEPAGE, Variant.VT_I2,
                        Integer.valueOf(Constants.CP_UNICODE));
            codepage = getCodepage();
        }


        Collections.sort(preprops, new Comparator<Property>() {
            public int compare(final Property p1, final Property p2) {
                if (p1.getID() < p2.getID())
                    return -1;
                else if (p1.getID() == p2.getID())
                    return 0;
                else
                    return 1;
            }
        });


        for (final ListIterator<Property> i = preprops.listIterator(); i.hasNext(); ) {
            final MutableProperty p = (MutableProperty) i.next();
            final long id = p.getID();


            TypeWriter.writeUIntToStream(propertyListStream, p.getID());
            TypeWriter.writeUIntToStream(propertyListStream, position);


            if (id != 0)

                position += p.write(propertyStream, getCodepage());
            else {
                if (codepage == -1)
                    throw new IllegalPropertySetDataException
                            ("Codepage (property 1) is undefined.");
                position += writeDictionary(propertyStream, dictionary,
                        codepage);
            }
        }
        propertyStream.close();
        propertyListStream.close();


        byte[] pb1 = propertyListStream.toByteArray();
        byte[] pb2 = propertyStream.toByteArray();


        TypeWriter.writeToStream(out, LittleEndian.INT_SIZE * 2 +
                pb1.length + pb2.length);


        TypeWriter.writeToStream(out, getPropertyCount());


        out.write(pb1);


        out.write(pb2);

        int streamLength = LittleEndian.INT_SIZE * 2 + pb1.length + pb2.length;
        return streamLength;
    }


    public int getPropertyCount() {
        return preprops.size();
    }


    public Property[] getProperties() {
        properties = (Property[]) preprops.toArray(new Property[0]);
        return properties;
    }


    public void setProperties(final Property[] properties) {
        this.properties = properties;
        preprops = new LinkedList<Property>();
        for (int i = 0; i < properties.length; i++)
            preprops.add(properties[i]);
        dirty = true;
    }


    public Object getProperty(final long id) {

        getProperties();
        return super.getProperty(id);
    }



    public void setDictionary(final Map<Long, String> dictionary)
            throws IllegalPropertySetDataException {
        if (dictionary != null) {
            this.dictionary = dictionary;


            setProperty(PropertyIDMap.PID_DICTIONARY, -1, dictionary);


            final Integer codepage =
                    (Integer) getProperty(PropertyIDMap.PID_CODEPAGE);
            if (codepage == null)
                setProperty(PropertyIDMap.PID_CODEPAGE, Variant.VT_I2,
                        Integer.valueOf(Constants.CP_UNICODE));
        } else

            removeProperty(PropertyIDMap.PID_DICTIONARY);
    }



    public void setProperty(final int id, final Object value) {
        if (value instanceof String)
            setProperty(id, (String) value);
        else if (value instanceof Long)
            setProperty(id, ((Long) value).longValue());
        else if (value instanceof Integer)
            setProperty(id, ((Integer) value).intValue());
        else if (value instanceof Short)
            setProperty(id, ((Short) value).intValue());
        else if (value instanceof Boolean)
            setProperty(id, ((Boolean) value).booleanValue());
        else if (value instanceof Date)
            setProperty(id, Variant.VT_FILETIME, value);
        else
            throw new HPSFRuntimeException(
                    "HPSF does not support properties of type " +
                            value.getClass().getName() + ".");
    }



    public void clear() {
        final Property[] properties = getProperties();
        for (int i = 0; i < properties.length; i++) {
            final Property p = properties[i];
            removeProperty(p.getID());
        }
    }


    public void setCodepage(final int codepage) {
        setProperty(PropertyIDMap.PID_CODEPAGE, Variant.VT_I2,
                Integer.valueOf(codepage));
    }
}
