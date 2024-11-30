
package com.document.render.office.system;

import java.util.Vector;



public interface IDialogAction {

    public void doAction(int id, Vector<Object> model);


    public IControl getControl();


    public void dispose();
}
