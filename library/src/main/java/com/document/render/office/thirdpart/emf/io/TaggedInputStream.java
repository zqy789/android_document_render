
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;
import java.io.InputStream;


public abstract class TaggedInputStream extends ByteCountInputStream {


    protected TagSet tagSet;


    protected ActionSet actionSet;


    private TagHeader tagHeader;


    public TaggedInputStream(InputStream in, TagSet tagSet, ActionSet actionSet) {
        this(in, tagSet, actionSet, false);
    }


    public TaggedInputStream(InputStream in, TagSet tagSet,
                             ActionSet actionSet, boolean littleEndian) {
        super(in, littleEndian, 8);

        this.tagSet = tagSet;
        this.actionSet = actionSet;
    }


    public void addTag(Tag tag) {
        tagSet.addTag(tag);
    }


    protected abstract TagHeader readTagHeader() throws IOException;


    public Tag readTag() throws IOException {

        tagHeader = readTagHeader();
        if (tagHeader == null) {
            return null;
        }

        int size = (int) tagHeader.getLength();


        Tag tag = tagSet.get(tagHeader.getTag());


        pushBuffer(size);
        tag = tag.read(tagHeader.getTag(), this, size);
        byte[] rest = popBuffer();


        if (rest != null) {
            throw new IncompleteTagException(tag, rest);
        }
        return tag;
    }


    public TagHeader getTagHeader() {
        return tagHeader;
    }


    public void addAction(Action action) {
        actionSet.addAction(action);
    }


    protected abstract ActionHeader readActionHeader() throws IOException;


    public Action readAction() throws IOException {

        ActionHeader header = readActionHeader();
        if (header == null) {
            return null;
        }

        int size = (int) header.getLength();


        Action action = actionSet.get(header.getAction());

        pushBuffer(size);
        action = action.read(header.getAction(), this, size);
        byte[] rest = popBuffer();

        if (rest != null) {
            throw new IncompleteActionException(action, rest);
        }
        return action;
    }

}
