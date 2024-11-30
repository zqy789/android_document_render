

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;
import com.document.render.office.fc.dom4j.ElementPath;


class SAXModifyElementHandler implements ElementHandler {
    private ElementModifier elemModifier;

    private Element modifiedElement;

    public SAXModifyElementHandler(ElementModifier elemModifier) {
        this.elemModifier = elemModifier;
    }

    public void onStart(ElementPath elementPath) {
        this.modifiedElement = elementPath.getCurrent();
    }

    public void onEnd(ElementPath elementPath) {
        try {
            Element origElement = elementPath.getCurrent();
            Element currentParent = origElement.getParent();

            if (currentParent != null) {

                Element clonedElem = (Element) origElement.clone();


                modifiedElement = elemModifier.modifyElement(clonedElem);

                if (modifiedElement != null) {

                    modifiedElement.setParent(origElement.getParent());
                    modifiedElement.setDocument(origElement.getDocument());


                    int contentIndex = currentParent.indexOf(origElement);
                    currentParent.content().set(contentIndex, modifiedElement);
                }


                origElement.detach();
            } else {
                if (origElement.isRootElement()) {

                    Element clonedElem = (Element) origElement.clone();


                    modifiedElement = elemModifier.modifyElement(clonedElem);

                    if (modifiedElement != null) {

                        modifiedElement.setDocument(origElement.getDocument());


                        Document doc = origElement.getDocument();
                        doc.setRootElement(modifiedElement);
                    }


                    origElement.detach();
                }
            }



            if (elementPath instanceof ElementStack) {
                ElementStack elementStack = ((ElementStack) elementPath);
                elementStack.popElement();
                elementStack.pushElement(modifiedElement);
            }
        } catch (Exception ex) {
            throw new SAXModifyException(ex);
        }
    }


    protected Element getModifiedElement() {
        return modifiedElement;
    }
}


