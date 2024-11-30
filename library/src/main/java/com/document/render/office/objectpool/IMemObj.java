
package com.document.render.office.objectpool;


public interface IMemObj {


    public void free();


    public IMemObj getCopy();
}
