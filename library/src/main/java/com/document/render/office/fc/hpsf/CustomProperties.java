

package com.document.render.office.fc.hpsf;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



@SuppressWarnings("serial")
public class CustomProperties extends HashMap<Object, CustomProperty> {


    private Map<Long, String> dictionaryIDToName = new HashMap<Long, String>();


    private Map<String, Long> dictionaryNameToID = new HashMap<String, Long>();


    private boolean isPure = true;



    public CustomProperty put(final String name, final CustomProperty cp) {
        if (name == null) {

            isPure = false;
            return null;
        }
        if (!(name.equals(cp.getName())))
            throw new IllegalArgumentException("Parameter \"name\" (" + name +
                    ") and custom property's name (" + cp.getName() +
                    ") do not match.");


        final Long idKey = Long.valueOf(cp.getID());
        final Long oldID = dictionaryNameToID.get(name);
        dictionaryIDToName.remove(oldID);
        dictionaryNameToID.put(name, idKey);
        dictionaryIDToName.put(idKey, name);


        final CustomProperty oldCp = super.remove(oldID);
        super.put(idKey, cp);
        return oldCp;
    }



    private Object put(final CustomProperty customProperty) throws ClassCastException {
        final String name = customProperty.getName();


        final Long oldId = (Long) dictionaryNameToID.get(name);
        if (oldId != null)
            customProperty.setID(oldId.longValue());
        else {
            long max = 1;
            for (final Iterator<Long> i = dictionaryIDToName.keySet().iterator(); i.hasNext(); ) {
                final long id = i.next().longValue();
                if (id > max)
                    max = id;
            }
            customProperty.setID(max + 1);
        }
        return this.put(name, customProperty);
    }



    public Object remove(final String name) {
        final Long id = (Long) dictionaryNameToID.get(name);
        if (id == null)
            return null;
        dictionaryIDToName.remove(id);
        dictionaryNameToID.remove(name);
        return super.remove(id);
    }


    public Object put(final String name, final String value) {
        final MutableProperty p = new MutableProperty();
        p.setID(-1);
        p.setType(Variant.VT_LPWSTR);
        p.setValue(value);
        final CustomProperty cp = new CustomProperty(p, name);
        return put(cp);
    }


    public Object put(final String name, final Long value) {
        final MutableProperty p = new MutableProperty();
        p.setID(-1);
        p.setType(Variant.VT_I8);
        p.setValue(value);
        final CustomProperty cp = new CustomProperty(p, name);
        return put(cp);
    }


    public Object put(final String name, final Double value) {
        final MutableProperty p = new MutableProperty();
        p.setID(-1);
        p.setType(Variant.VT_R8);
        p.setValue(value);
        final CustomProperty cp = new CustomProperty(p, name);
        return put(cp);
    }


    public Object put(final String name, final Integer value) {
        final MutableProperty p = new MutableProperty();
        p.setID(-1);
        p.setType(Variant.VT_I4);
        p.setValue(value);
        final CustomProperty cp = new CustomProperty(p, name);
        return put(cp);
    }


    public Object put(final String name, final Boolean value) {
        final MutableProperty p = new MutableProperty();
        p.setID(-1);
        p.setType(Variant.VT_BOOL);
        p.setValue(value);
        final CustomProperty cp = new CustomProperty(p, name);
        return put(cp);
    }



    public Object get(final String name) {
        final Long id = (Long) dictionaryNameToID.get(name);
        final CustomProperty cp = (CustomProperty) super.get(id);
        return cp != null ? cp.getValue() : null;
    }



    public Object put(final String name, final Date value) {
        final MutableProperty p = new MutableProperty();
        p.setID(-1);
        p.setType(Variant.VT_FILETIME);
        p.setValue(value);
        final CustomProperty cp = new CustomProperty(p, name);
        return put(cp);
    }


    public Set keySet() {
        return dictionaryNameToID.keySet();
    }


    public Set<String> nameSet() {
        return dictionaryNameToID.keySet();
    }


    public Set<String> idSet() {
        return dictionaryNameToID.keySet();
    }


    Map<Long, String> getDictionary() {
        return dictionaryIDToName;
    }


    public boolean containsKey(Object key) {
        if (key instanceof Long) {
            return super.containsKey((Long) key);
        }
        if (key instanceof String) {
            return super.containsKey((Long) dictionaryNameToID.get(key));
        }
        return false;
    }


    public boolean containsValue(Object value) {
        if (value instanceof CustomProperty) {
            return super.containsValue((CustomProperty) value);
        } else {
            for (CustomProperty cp : super.values()) {
                if (cp.getValue() == value) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getCodepage() {
        int codepage = -1;
        for (final Iterator<CustomProperty> i = this.values().iterator(); codepage == -1 && i.hasNext(); ) {
            final CustomProperty cp = i.next();
            if (cp.getID() == PropertyIDMap.PID_CODEPAGE)
                codepage = ((Integer) cp.getValue()).intValue();
        }
        return codepage;
    }


    public void setCodepage(final int codepage) {
        final MutableProperty p = new MutableProperty();
        p.setID(PropertyIDMap.PID_CODEPAGE);
        p.setType(Variant.VT_I2);
        p.setValue(Integer.valueOf(codepage));
        put(new CustomProperty(p));
    }


    public boolean isPure() {
        return isPure;
    }


    public void setPure(final boolean isPure) {
        this.isPure = isPure;
    }
}
