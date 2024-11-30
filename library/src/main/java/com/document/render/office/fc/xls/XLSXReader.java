
package com.document.render.office.fc.xls;

import android.net.Uri;

import com.document.render.bean.DocSourceType;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;
import com.document.render.office.fc.dom4j.ElementPath;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.PackageRelationship;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipCollection;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipTypes;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.xls.Reader.WorkbookReader;
import com.document.render.office.fc.xls.Reader.shared.StyleReader;
import com.document.render.office.fc.xls.Reader.shared.ThemeColorReader;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.sheetProperty.Palette;
import com.document.render.office.ss.util.ColorUtil;
import com.document.render.office.system.AbortReaderError;
import com.document.render.office.system.IControl;
import com.document.render.office.system.StopReaderError;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;



public class XLSXReader extends SSReader {


    private String filePath;
    private int docSourceType;

    private ZipPackage zipPackage;
    private Workbook book;

    private PackagePart packagePart;
    private int sharedStringIndex;

    private String key;
    private boolean searched;


    public XLSXReader(IControl control, String filePath, int docSourceType) {
        this.control = control;
        this.filePath = filePath;
        this.docSourceType = docSourceType;
    }


    public Object getModel() throws Exception {
        book = new Workbook(false);

        InputStream is = null;
        switch (docSourceType) {
            case DocSourceType.URL:
                URL url = new URL(filePath);
                is = url.openStream();
                break;
            case DocSourceType.URI:
                Uri uri = Uri.parse(filePath);
                is = control.getActivity().getContentResolver().openInputStream(uri);
                break;
            case DocSourceType.PATH:
                is = new FileInputStream(filePath);
                break;
            case DocSourceType.ASSETS:
                is = control.getActivity().getAssets().open(filePath);
                break;
        }
        zipPackage = new ZipPackage(is);

        initPackagePart();

        processWorkbook();

        return book;
    }


    private void initPackagePart() throws Exception {
        PackageRelationship coreRel = zipPackage.getRelationshipsByType(
                PackageRelationshipTypes.CORE_DOCUMENT).getRelationship(0);
        if (!coreRel.getTargetURI().toString().equals("/xl/workbook.xml")) {
            throw new Exception("Format error");
        }
        this.packagePart = zipPackage.getPart(coreRel);
    }


    private void processWorkbook() throws Exception {
        getWorkBookSharedObjects();

        WorkbookReader.instance().read(zipPackage, packagePart, book, this);
    }


    private void getWorkBookSharedObjects() throws Exception {

        getPaletteColor();


        getThemeColor(packagePart);


        getStyles(packagePart);


        getSharedString(packagePart);

    }


    private void getPaletteColor() {
        Palette palette = new Palette();
        int index = Palette.FIRST_COLOR_INDEX;
        byte[] rgb = palette.getColor(index);

        while (rgb != null) {
            book.addColor(index++, ColorUtil.rgb(rgb[0], rgb[1], rgb[2]));

            rgb = palette.getColor(index);
        }

        palette.dispose();
        palette = null;
    }


    private void getThemeColor(PackagePart documentPart) throws Exception {
        if (documentPart.getRelationshipsByType(PackageRelationshipTypes.THEME_PART).size() <= 0) {
            return;
        }


        PackageRelationship styleRel = documentPart.getRelationshipsByType(
                PackageRelationshipTypes.THEME_PART).getRelationship(0);
        PackagePart themeParts = zipPackage.getPart(styleRel.getTargetURI());


        ThemeColorReader.instance().getThemeColor(themeParts, book);

    }


    private void getSharedString(PackagePart documentPart) throws Exception {
        PackageRelationshipCollection sharedStringsRelCollection = documentPart.getRelationshipsByType(
                PackageRelationshipTypes.SHAREDSTRINGS_PART);
        if (sharedStringsRelCollection.size() <= 0) {
            return;
        }

        PackageRelationship sharedStringsRel = sharedStringsRelCollection.getRelationship(0);
        PackagePart sharedStringsParts = zipPackage.getPart(sharedStringsRel.getTargetURI());
        sharedStringIndex = 0;

        SAXReader saxreader = new SAXReader();

        try {
            saxreader.addHandler("/sst/si", new SharedStringSaxHandler());
            InputStream in = sharedStringsParts.getInputStream();
            saxreader.read(in);
            in.close();
        } finally {
            saxreader.resetHandlers();
        }
    }


    private void getStyles(PackagePart documentPart) throws Exception {
        if (documentPart.getRelationshipsByType(PackageRelationshipTypes.STYLE_PART).size() <= 0) {
            return;
        }


        PackageRelationship styleRel = documentPart.getRelationshipsByType(
                PackageRelationshipTypes.STYLE_PART).getRelationship(0);
        PackagePart styleParts = zipPackage.getPart(styleRel.getTargetURI());

        StyleReader.instance().getWorkBookStyle(styleParts, book, this);
    }


    public boolean searchContent(File file, String key) throws Exception {
        key = key.toLowerCase();

        zipPackage = new ZipPackage(file.getAbsolutePath());
        PackageRelationship coreRel = zipPackage.getRelationshipsByType(
                PackageRelationshipTypes.CORE_DOCUMENT).getRelationship(0);
        this.packagePart = zipPackage.getPart(coreRel);

        boolean hasSearched;

        if (searchContent_SharedString(packagePart, key)) {
            hasSearched = true;
        } else {

            hasSearched = WorkbookReader.instance().searchContent(zipPackage, this, packagePart, key);
        }

        dispose();

        return hasSearched;
    }


    private boolean searchContent_SharedString(PackagePart documentPart, String key) throws Exception {
        PackageRelationshipCollection sharedStringsRelCollection = documentPart.getRelationshipsByType(
                PackageRelationshipTypes.SHAREDSTRINGS_PART);
        if (sharedStringsRelCollection.size() <= 0) {
            return false;
        }

        PackageRelationship sharedStringsRel = sharedStringsRelCollection.getRelationship(0);
        PackagePart sharedStringsParts = zipPackage.getPart(sharedStringsRel.getTargetURI());

        this.key = key;
        searched = false;

        SAXReader saxreader = new SAXReader();

        try {
            saxreader.addHandler("/sst/si", new SearchSharedStringSaxHandler());
            InputStream in = sharedStringsParts.getInputStream();
            saxreader.read(in);
            in.close();
        } catch (StopReaderError e) {
            return true;
        } finally {
            saxreader.resetHandlers();
        }

        return searched;
    }


    public void dispose() {
        super.dispose();

        filePath = null;
        book = null;
        zipPackage = null;
        packagePart = null;
        key = null;
    }


    class SharedStringSaxHandler implements ElementHandler {


        public void onStart(ElementPath elementPath) {
        }


        public void onEnd(ElementPath elementPath) {
            if (abortReader) {
                throw new AbortReaderError("abort Reader");
            }

            Element elem = elementPath.getCurrent();
            String name = elem.getName();
            if (name.equals("si")) {
                Element ele;
                String str;
                ele = elem.element("t");
                if (ele != null) {
                    book.addSharedString(sharedStringIndex, ele.getText());
                } else {
                    book.addSharedString(sharedStringIndex, elem);
                }

                sharedStringIndex++;
            }

            elem.detach();
        }

    }


    class SearchSharedStringSaxHandler implements ElementHandler {


        public void onStart(ElementPath elementPath) {

        }


        public void onEnd(ElementPath elementPath) {
            if (abortReader) {
                throw new AbortReaderError("abort Reader");
            }

            Element stringItem = elementPath.getCurrent();
            String name = stringItem.getName();
            if (name.equals("si")) {
                Element ele = stringItem.element("t");
                if (ele != null) {
                    if (ele.getText().toLowerCase().contains(key)) {
                        searched = true;
                    }
                } else {
                    @SuppressWarnings("unchecked")
                    Iterator<Element> iter1 = stringItem.elementIterator("r");
                    String str = "";
                    while (iter1.hasNext()) {
                        ele = iter1.next();
                        str = str + ele.element("t").getText();
                    }

                    if (str.toLowerCase().contains(key)) {
                        searched = true;
                    }
                }
            }
            stringItem.detach();
            if (searched) {
                throw new StopReaderError("stop");
            }
        }

    }
}
