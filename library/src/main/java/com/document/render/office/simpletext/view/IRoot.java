
package com.document.render.office.simpletext.view;



public interface IRoot {

    public static final int MINLAYOUTWIDTH = 5;


    public boolean canBackLayout();


    public void backLayout();


    public ViewContainer getViewContainer();
}
