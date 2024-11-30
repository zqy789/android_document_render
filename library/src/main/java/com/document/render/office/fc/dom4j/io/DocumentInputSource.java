

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Document;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;



class DocumentInputSource extends InputSource {

    private Document document;

    public DocumentInputSource() {
    }

    public DocumentInputSource(Document document) {
        this.document = document;
        setSystemId(document.getName());
    }





    public Document getDocument() {
        return document;
    }


    public void setDocument(Document document) {
        this.document = document;
        setSystemId(document.getName());
    }





    public Reader getCharacterStream() {
        try {
            StringWriter out = new StringWriter();
            XMLWriter writer = new XMLWriter(out);
            writer.write(document);
            writer.flush();

            return new StringReader(out.toString());
        } catch (final IOException e) {



            return new Reader() {
                public int read(char[] ch, int offset, int length) throws IOException {
                    throw e;
                }

                public void close() throws IOException {
                }
            };
        }
    }


    public void setCharacterStream(Reader characterStream) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}


