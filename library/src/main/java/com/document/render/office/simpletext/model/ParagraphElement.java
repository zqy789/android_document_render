
package com.document.render.office.simpletext.model;


public class ParagraphElement extends AbstractElement {


    private ElementCollectionImpl leaf;


    public ParagraphElement() {
        super();
        leaf = new ElementCollectionImpl(10);
    }


    public String getText(IDocument doc) {
        int count = leaf.size();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < count; i++) {
            text.append(leaf.getElementForIndex(i).getText(null));
        }
        return text.toString();
    }


    public void appendLeaf(LeafElement leafElem) {
        leaf.addElement(leafElem);
    }


    public IElement getLeaf(long offset) {
        return leaf.getElement(offset);
    }


    public IElement getElementForIndex(int index) {
        return leaf.getElementForIndex(index);
    }


    public void dispose() {
        super.dispose();
        if (leaf != null) {
            leaf.dispose();
            leaf = null;
        }
    }

}
