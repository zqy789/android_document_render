
package com.document.render.office.simpletext.model;



public class Style {

    
    private int id = -1;
    
    private int baseID = -1;
    
    private String name;
    
    private byte type;
    
    private IAttributeSet attr;

    public Style() {
        attr = new AttributeSetImpl();
    }

    
    public int getId() {
        return id;
    }

    
    public void setId(int id) {
        this.id = id;
    }

    
    public int getBaseID() {
        return baseID;
    }

    
    public void setBaseID(int baseID) {
        this.baseID = baseID;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public byte getType() {
        return type;
    }

    
    public void setType(byte type) {
        this.type = type;
    }

    
    public IAttributeSet getAttrbuteSet() {
        return attr;
    }

    
    public void setAttrbuteSet(IAttributeSet attr) {
        this.attr = attr;
    }

    public void dispose() {
        name = null;
        if (attr != null) {
            attr.dispose();
            attr = null;
        }
    }

}
