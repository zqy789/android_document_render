

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;


class PruningElementStack extends ElementStack {

    private ElementHandler elementHandler;


    private String[] path;


    private int matchingElementIndex;

    public PruningElementStack(String[] path, ElementHandler elementHandler) {
        this.path = path;
        this.elementHandler = elementHandler;
        checkPath();
    }

    public PruningElementStack(String[] path, ElementHandler elementHandler, int defaultCapacity) {
        super(defaultCapacity);
        this.path = path;
        this.elementHandler = elementHandler;
        checkPath();
    }

    public Element popElement() {
        Element answer = super.popElement();

        if ((lastElementIndex == matchingElementIndex) && (lastElementIndex >= 0)) {





            if (validElement(answer, lastElementIndex + 1)) {
                Element parent = null;

                for (int i = 0; i <= lastElementIndex; i++) {
                    parent = stack[i];

                    if (!validElement(parent, i)) {
                        parent = null;

                        break;
                    }
                }

                if (parent != null) {
                    pathMatches(parent, answer);
                }
            }
        }

        return answer;
    }

    protected void pathMatches(Element parent, Element selectedNode) {
        elementHandler.onEnd(this);
        parent.remove(selectedNode);
    }

    protected boolean validElement(Element element, int index) {
        String requiredName = path[index];
        String name = element.getName();

        if (requiredName == name) {
            return true;
        }

        if ((requiredName != null) && (name != null)) {
            return requiredName.equals(name);
        }

        return false;
    }

    private void checkPath() {
        if (path.length < 2) {
            throw new RuntimeException("Invalid path of length: " + path.length
                    + " it must be greater than 2");
        }

        matchingElementIndex = path.length - 2;
    }
}


