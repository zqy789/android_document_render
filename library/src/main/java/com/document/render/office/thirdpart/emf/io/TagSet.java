
package com.document.render.office.thirdpart.emf.io;

import java.util.HashMap;
import java.util.Map;


public class TagSet {


    protected Map tags;


    protected Tag defaultTag;


    public TagSet() {

        defaultTag = new UndefinedTag();
        tags = new HashMap();
    }


    public void addTag(Tag tag) {
        System.out.println("addTag==========");
        int id = tag.getTag();
        if (id != Tag.DEFAULT_TAG) {
            tags.put(new Integer(id), tag);
        } else {
            defaultTag = tag;
        }
    }


    public Tag get(int tagID) {
        Tag tag = (Tag) tags.get(new Integer(tagID));
        if (tag == null) {
            tag = defaultTag;
        }
        return tag;
    }


    public boolean exists(int tagID) {
        return (tags.get(new Integer(tagID)) != null);
    }
}
