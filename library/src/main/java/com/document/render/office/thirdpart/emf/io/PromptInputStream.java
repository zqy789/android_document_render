
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;
import java.io.InputStream;


public class PromptInputStream extends RoutedInputStream {


    public PromptInputStream(InputStream input) {
        super(input);
    }


    public void addPromptListener(String prompt, PromptListener listener) {
        final PromptListener promptListener = listener;
        addRoute(prompt, prompt, new RouteListener() {
            public void routeFound(RoutedInputStream.Route input)
                    throws IOException {
                promptListener.promptFound(input);
            }
        });
    }
}
