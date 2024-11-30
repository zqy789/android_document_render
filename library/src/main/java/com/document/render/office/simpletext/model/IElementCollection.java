

package com.document.render.office.simpletext.model;


public interface IElementCollection {

    public IElement getElement(long offset);


    public IElement getElementForIndex(int index);


    public int size();


    public void dispose();

}
