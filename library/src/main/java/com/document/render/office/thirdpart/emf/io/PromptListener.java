
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;


public interface PromptListener {


    public void promptFound(RoutedInputStream.Route route) throws IOException;
}
