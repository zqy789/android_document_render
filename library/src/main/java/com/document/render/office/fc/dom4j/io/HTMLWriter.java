

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentException;
import com.document.render.office.fc.dom4j.DocumentHelper;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Entity;
import com.document.render.office.fc.dom4j.Node;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;



public class HTMLWriter extends XMLWriter {
    protected static final HashSet DEFAULT_PREFORMATTED_TAGS;
    protected static final OutputFormat DEFAULT_HTML_FORMAT;
    private static String lineSeparator = System.getProperty("line.separator");

    static {


        DEFAULT_PREFORMATTED_TAGS = new HashSet();
        DEFAULT_PREFORMATTED_TAGS.add("PRE");
        DEFAULT_PREFORMATTED_TAGS.add("SCRIPT");
        DEFAULT_PREFORMATTED_TAGS.add("STYLE");
        DEFAULT_PREFORMATTED_TAGS.add("TEXTAREA");
    }

    static {
        DEFAULT_HTML_FORMAT = new OutputFormat("  ", true);
        DEFAULT_HTML_FORMAT.setTrimText(true);
        DEFAULT_HTML_FORMAT.setSuppressDeclaration(true);
    }

    private Stack formatStack = new Stack();

    private String lastText = "";

    private int tagsOuput = 0;


    private int newLineAfterNTags = -1;

    private HashSet preformattedTags = DEFAULT_PREFORMATTED_TAGS;


    private HashSet omitElementCloseSet;

    public HTMLWriter(Writer writer) {
        super(writer, DEFAULT_HTML_FORMAT);
    }

    public HTMLWriter(Writer writer, OutputFormat format) {
        super(writer, format);
    }

    public HTMLWriter() throws UnsupportedEncodingException {
        super(DEFAULT_HTML_FORMAT);
    }

    public HTMLWriter(OutputFormat format) throws UnsupportedEncodingException {
        super(format);
    }

    public HTMLWriter(OutputStream out) throws UnsupportedEncodingException {
        super(out, DEFAULT_HTML_FORMAT);
    }

    public HTMLWriter(OutputStream out, OutputFormat format) throws UnsupportedEncodingException {
        super(out, format);
    }


    public static String prettyPrintHTML(String html) throws java.io.IOException,
            java.io.UnsupportedEncodingException, DocumentException {
        return prettyPrintHTML(html, true, true, false, true);
    }


    public static String prettyPrintXHTML(String html) throws java.io.IOException,
            java.io.UnsupportedEncodingException, DocumentException {
        return prettyPrintHTML(html, true, true, true, false);
    }


    public static String prettyPrintHTML(String html, boolean newlines, boolean trim,
                                         boolean isXHTML, boolean expandEmpty) throws java.io.IOException,
            java.io.UnsupportedEncodingException, DocumentException {
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setNewlines(newlines);
        format.setTrimText(trim);
        format.setXHTML(isXHTML);
        format.setExpandEmptyElements(expandEmpty);

        HTMLWriter writer = new HTMLWriter(sw, format);
        Document document = DocumentHelper.parseText(html);
        writer.write(document);
        writer.flush();

        return sw.toString();
    }

    public void startCDATA() throws SAXException {
    }

    public void endCDATA() throws SAXException {
    }



    protected void writeCDATA(String text) throws IOException {


        if (getOutputFormat().isXHTML()) {
            super.writeCDATA(text);
        } else {
            writer.write(text);
        }

        lastOutputNodeType = Node.CDATA_SECTION_NODE;
    }

    protected void writeEntity(Entity entity) throws IOException {
        writer.write(entity.getText());
        lastOutputNodeType = Node.ENTITY_REFERENCE_NODE;
    }

    protected void writeDeclaration() throws IOException {
    }

    protected void writeString(String text) throws IOException {

        if (text.equals("\n")) {
            if (!formatStack.empty()) {
                super.writeString(lineSeparator);
            }

            return;
        }

        lastText = text;

        if (formatStack.empty()) {
            super.writeString(text.trim());
        } else {
            super.writeString(text);
        }
    }


    protected void writeClose(String qualifiedName) throws IOException {
        if (!omitElementClose(qualifiedName)) {
            super.writeClose(qualifiedName);
        }
    }

    protected void writeEmptyElementClose(String qualifiedName) throws IOException {
        if (getOutputFormat().isXHTML()) {

            if (omitElementClose(qualifiedName)) {





                writer.write(" />");
            } else {
                super.writeEmptyElementClose(qualifiedName);
            }
        } else {

            if (omitElementClose(qualifiedName)) {

                writer.write(">");
            } else {


                super.writeEmptyElementClose(qualifiedName);
            }
        }
    }



    protected boolean omitElementClose(String qualifiedName) {
        return internalGetOmitElementCloseSet().contains(qualifiedName.toUpperCase());
    }

    private HashSet internalGetOmitElementCloseSet() {
        if (omitElementCloseSet == null) {
            omitElementCloseSet = new HashSet();
            loadOmitElementCloseSet(omitElementCloseSet);
        }

        return omitElementCloseSet;
    }


    protected void loadOmitElementCloseSet(Set set) {
        set.add("AREA");
        set.add("BASE");
        set.add("BR");
        set.add("COL");
        set.add("HR");
        set.add("IMG");
        set.add("INPUT");
        set.add("LINK");
        set.add("META");
        set.add("P");
        set.add("PARAM");
    }


    public Set getOmitElementCloseSet() {
        return (Set) (internalGetOmitElementCloseSet().clone());
    }


    public void setOmitElementCloseSet(Set newSet) {

        omitElementCloseSet = new HashSet();

        if (newSet != null) {
            omitElementCloseSet = new HashSet();

            Object aTag;
            Iterator iter = newSet.iterator();

            while (iter.hasNext()) {
                aTag = iter.next();

                if (aTag != null) {
                    omitElementCloseSet.add(aTag.toString().toUpperCase());
                }
            }
        }
    }


    public Set getPreformattedTags() {
        return (Set) (preformattedTags.clone());
    }


    public void setPreformattedTags(Set newSet) {




        preformattedTags = new HashSet();

        if (newSet != null) {
            Object aTag;
            Iterator iter = newSet.iterator();

            while (iter.hasNext()) {
                aTag = iter.next();

                if (aTag != null) {
                    preformattedTags.add(aTag.toString().toUpperCase());
                }
            }
        }
    }


    public boolean isPreformattedTag(String qualifiedName) {


        return (preformattedTags != null)
                && (preformattedTags.contains(qualifiedName.toUpperCase()));
    }




    protected void writeElement(Element element) throws IOException {
        if (newLineAfterNTags == -1) {
            lazyInitNewLinesAfterNTags();
        }

        if (newLineAfterNTags > 0) {
            if ((tagsOuput > 0) && ((tagsOuput % newLineAfterNTags) == 0)) {
                super.writer.write(lineSeparator);
            }
        }

        tagsOuput++;

        String qualifiedName = element.getQualifiedName();
        String saveLastText = lastText;
        int size = element.nodeCount();

        if (isPreformattedTag(qualifiedName)) {
            OutputFormat currentFormat = getOutputFormat();
            boolean saveNewlines = currentFormat.isNewlines();
            boolean saveTrimText = currentFormat.isTrimText();
            String currentIndent = currentFormat.getIndent();



            formatStack.push(new FormatState(saveNewlines, saveTrimText, currentIndent));

            try {


                super.writePrintln();

                if ((saveLastText.trim().length() == 0) && (currentIndent != null)
                        && (currentIndent.length() > 0)) {





                    super.writer.write(justSpaces(saveLastText));
                }



                currentFormat.setNewlines(false);
                currentFormat.setTrimText(false);
                currentFormat.setIndent("");


                super.writeElement(element);
            } finally {
                FormatState state = (FormatState) formatStack.pop();
                currentFormat.setNewlines(state.isNewlines());
                currentFormat.setTrimText(state.isTrimText());
                currentFormat.setIndent(state.getIndent());
            }
        } else {
            super.writeElement(element);
        }
    }

    private String justSpaces(String text) {
        int size = text.length();
        StringBuffer res = new StringBuffer(size);
        char c;

        for (int i = 0; i < size; i++) {
            c = text.charAt(i);

            switch (c) {
                case '\r':
                case '\n':

                    continue;

                default:
                    res.append(c);
            }
        }

        return res.toString();
    }

    private void lazyInitNewLinesAfterNTags() {
        if (getOutputFormat().isNewlines()) {

            newLineAfterNTags = 0;
        } else {
            newLineAfterNTags = getOutputFormat().getNewLineAfterNTags();
        }
    }



    private class FormatState {
        private boolean newlines = false;

        private boolean trimText = false;

        private String indent = "";

        public FormatState(boolean newLines, boolean trimText, String indent) {
            this.newlines = newLines;
            this.trimText = trimText;
            this.indent = indent;
        }

        public boolean isNewlines() {
            return newlines;
        }

        public boolean isTrimText() {
            return trimText;
        }

        public String getIndent() {
            return indent;
        }
    }
}




