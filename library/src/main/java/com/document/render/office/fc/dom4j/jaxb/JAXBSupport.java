

package com.document.render.office.fc.dom4j.jaxb;


abstract class JAXBSupport {
    private String contextPath;

    private ClassLoader classloader;







    public JAXBSupport(String contextPath) {
        this.contextPath = contextPath;
    }

    public JAXBSupport(String contextPath, ClassLoader classloader) {
        this.contextPath = contextPath;
        this.classloader = classloader;
    }



}


