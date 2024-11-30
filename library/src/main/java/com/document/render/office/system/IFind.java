
package com.document.render.office.system;

public interface IFind {

    public boolean find(String value);


    public boolean findBackward();


    public boolean findForward();



    public int getPageIndex();


    public void resetSearchResult();


    public void dispose();
}
