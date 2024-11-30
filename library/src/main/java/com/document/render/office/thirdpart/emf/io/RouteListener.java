
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;


public interface RouteListener {


    public void routeFound(RoutedInputStream.Route input) throws IOException;
}
