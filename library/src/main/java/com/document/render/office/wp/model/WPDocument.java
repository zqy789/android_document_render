

package com.document.render.office.wp.model;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.ElementCollectionImpl;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.model.STDocument;
import com.document.render.office.simpletext.model.SectionElement;



public class WPDocument extends STDocument {


    private ElementCollectionImpl root[];

    private ElementCollectionImpl para[];

    private ElementCollectionImpl table[];
    private BackgroundAndFill pageBG;

    public WPDocument() {
        root = new ElementCollectionImpl[6];
        para = new ElementCollectionImpl[6];
        table = new ElementCollectionImpl[4];

        initRoot();
    }


    private void initRoot() {

        root[0] = new ElementCollectionImpl(5);

        root[1] = new ElementCollectionImpl(3);

        root[2] = new ElementCollectionImpl(3);

        root[3] = new ElementCollectionImpl(5);

        root[4] = new ElementCollectionImpl(5);

        root[5] = new ElementCollectionImpl(5);


        para[0] = new ElementCollectionImpl(100);

        para[1] = new ElementCollectionImpl(3);

        para[2] = new ElementCollectionImpl(3);

        para[3] = new ElementCollectionImpl(5);

        para[4] = new ElementCollectionImpl(5);

        para[5] = new ElementCollectionImpl(5);


        table[0] = new ElementCollectionImpl(5);

        table[1] = new ElementCollectionImpl(5);

        table[2] = new ElementCollectionImpl(5);

        table[3] = new ElementCollectionImpl(5);
    }


    public IElement getSection(long offset) {
        return root[0].getElement(offset);
    }


    public void appendSection(IElement elem) {
        root[0].addElement(elem);
    }


    public void appendElement(IElement elem, long offset) {
        if (elem.getType() == WPModelConstant.PARAGRAPH_ELEMENT) {
            appendParagraph(elem, offset);
        }
        ElementCollectionImpl collection = getRootCollection(offset);
        if (collection != null) {
            collection.addElement(elem);
        }
    }


    public IElement getHFElement(long offset, byte type) {
        ElementCollectionImpl collection = getRootCollection(offset);
        if (collection != null) {
            return collection.getElement(offset);
        }
        return null;
    }


    public IElement getFEElement(long offset) {

        return null;
    }


    public IElement getParagraph(long offset) {

        if ((offset & WPModelConstant.AREA_MASK) == WPModelConstant.TEXTBOX) {
            IElement e = getTextboxSectionElement(offset);
            if (e != null) {
                return ((SectionElement) e).getParaCollection().getElement(offset);
            }
        }
        ElementCollectionImpl collection = getParaCollection(offset);
        if (collection != null) {
            return collection.getElement(offset);
        }
        return null;
    }


    public IElement getParagraph0(long offset) {
        IElement elem = getParagraph(offset);
        if (AttrManage.instance().getParaLevel(elem.getAttribute()) >= 0) {
            ElementCollectionImpl collection = getTableCollection(offset);
            if (collection != null) {
                return collection.getElement(offset);
            }
        }
        return elem;
    }


    public IElement getParagraphForIndex(int index, long area) {

        if ((area & WPModelConstant.AREA_MASK) == WPModelConstant.TEXTBOX) {
            IElement e = getTextboxSectionElement(area);
            if (e != null) {
                return ((SectionElement) e).getParaCollection().getElementForIndex(index);
            }
        }
        ElementCollectionImpl collection = getParaCollection(area);
        if (collection != null) {
            return collection.getElementForIndex(index);
        }
        return null;
    }


    public void appendParagraph(IElement element, long area) {
        if (element.getType() == WPModelConstant.TABLE_ELEMENT) {
            ElementCollectionImpl collection = getTableCollection(area);
            if (collection != null) {
                collection.addElement(element);
            }
            return;
        }

        if ((area & WPModelConstant.AREA_MASK) == WPModelConstant.TEXTBOX) {
            IElement e = getTextboxSectionElement(area);
            if (e != null) {
                ((SectionElement) e).appendParagraph(element, area);
                return;
            }
        }
        ElementCollectionImpl collection = getParaCollection(area);
        if (collection != null) {
            collection.addElement(element);
        }
    }


    public int getParaCount(long area) {

        if ((area & WPModelConstant.AREA_MASK) == WPModelConstant.TEXTBOX) {
            IElement e = getTextboxSectionElement(area);
            if (e != null) {
                return ((SectionElement) e).getParaCollection().size();
            }
        }
        ElementCollectionImpl collection = getParaCollection(area);
        if (collection != null) {
            return collection.size();
        }
        return 0;
    }


    private ElementCollectionImpl getRootCollection(long offset) {
        long area = offset & WPModelConstant.AREA_MASK;

        if (area == WPModelConstant.MAIN) {
            return root[0];
        }

        else if (area == WPModelConstant.HEADER) {
            return root[1];
        }

        else if (area == WPModelConstant.FOOTER) {
            return root[2];
        }

        else if (area == WPModelConstant.FOOTNOTE) {
            return root[3];
        }

        else if (area == WPModelConstant.ENDNOTE) {
            return root[4];
        }

        else if (area == WPModelConstant.TEXTBOX) {
            return root[5];
        }
        return null;
    }


    public ElementCollectionImpl getParaCollection(long offset) {
        long area = offset & WPModelConstant.AREA_MASK;

        if (area == WPModelConstant.MAIN) {
            return para[0];
        }

        else if (area == WPModelConstant.HEADER) {
            return para[1];
        }

        else if (area == WPModelConstant.FOOTER) {
            return para[2];
        }

        else if (area == WPModelConstant.FOOTNOTE) {
            return para[3];
        }

        else if (area == WPModelConstant.ENDNOTE) {
            return para[4];
        }

        else if (area == WPModelConstant.TEXTBOX) {
            return para[5];
        }
        return null;
    }


    public ElementCollectionImpl getTableCollection(long offset) {
        long area = offset & WPModelConstant.AREA_MASK;

        if (area == WPModelConstant.MAIN) {
            return table[0];
        }

        else if (area == WPModelConstant.HEADER) {
            return table[1];
        }

        else if (area == WPModelConstant.FOOTER) {
            return table[2];
        }



        else if (area == WPModelConstant.TEXTBOX) {
            return table[3];
        }
        return null;
    }


    public long getLength(long offset) {
        ElementCollectionImpl root = getRootCollection(offset);
        if (root != null) {

            if ((offset & WPModelConstant.AREA_MASK) == WPModelConstant.TEXTBOX) {
                IElement e = getTextboxSectionElement(offset);
                if (e != null) {
                    return e.getEndOffset() - e.getStartOffset();
                }
            }
            return root.getElementForIndex(root.size() - 1).getEndOffset() -
                    root.getElementForIndex(0).getStartOffset();
        }
        return 0;
    }


    private IElement getTextboxSectionElement(long offset) {
        long index = (offset & WPModelConstant.TEXTBOX_MASK) >> 32;
        if (root[5] != null) {
            return root[5].getElementForIndex((int) index);
        }
        return null;
    }


    public IElement getTextboxSectionElementForIndex(int index) {
        if (root[5] != null) {
            return root[5].getElementForIndex((int) index);
        }
        return null;
    }

    public BackgroundAndFill getPageBackground() {
        return pageBG;
    }

    public void setPageBackground(BackgroundAndFill pageBG) {
        this.pageBG = pageBG;
    }


    public void dispose() {
        super.dispose();

        if (root != null) {
            for (int i = 0; i < root.length; i++) {
                root[i].dispose();
                root[i] = null;
            }
            root = null;
        }

        if (para != null) {
            for (int i = 0; i < para.length; i++) {
                para[i].dispose();
                para[i] = null;
            }
            para = null;
        }

        if (table != null) {
            for (int i = 0; i < table.length; i++) {
                table[i].dispose();
                table[i] = null;
            }
            table = null;
        }
    }
}
