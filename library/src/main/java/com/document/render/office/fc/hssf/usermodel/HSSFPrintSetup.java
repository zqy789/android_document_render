


package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.PrintSetupRecord;
import com.document.render.office.fc.ss.usermodel.PrintSetup;



public class HSSFPrintSetup implements PrintSetup {
    PrintSetupRecord printSetupRecord;

    
    protected HSSFPrintSetup(PrintSetupRecord printSetupRecord) {
        this.printSetupRecord = printSetupRecord;
    }

    
    public short getPaperSize() {
        return printSetupRecord.getPaperSize();
    }

    
    public void setPaperSize(short size) {
        printSetupRecord.setPaperSize(size);
    }

    
    public short getScale() {
        return printSetupRecord.getScale();
    }

    
    public void setScale(short scale) {
        printSetupRecord.setScale(scale);
    }

    
    public short getPageStart() {
        return printSetupRecord.getPageStart();
    }

    
    public void setPageStart(short start) {
        printSetupRecord.setPageStart(start);
    }

    
    public short getFitWidth() {
        return printSetupRecord.getFitWidth();
    }

    
    public void setFitWidth(short width) {
        printSetupRecord.setFitWidth(width);
    }

    
    public short getFitHeight() {
        return printSetupRecord.getFitHeight();
    }

    
    public void setFitHeight(short height) {
        printSetupRecord.setFitHeight(height);
    }

    
    public short getOptions() {
        return printSetupRecord.getOptions();
    }

    
    public void setOptions(short options) {
        printSetupRecord.setOptions(options);
    }

    
    public boolean getLeftToRight() {
        return printSetupRecord.getLeftToRight();
    }

    
    public void setLeftToRight(boolean ltor) {
        printSetupRecord.setLeftToRight(ltor);
    }

    
    public boolean getLandscape() {
        return !printSetupRecord.getLandscape();
    }

    
    public void setLandscape(boolean ls) {
        printSetupRecord.setLandscape(!ls);
    }

    
    public boolean getValidSettings() {
        return printSetupRecord.getValidSettings();
    }

    
    public void setValidSettings(boolean valid) {
        printSetupRecord.setValidSettings(valid);
    }

    
    public boolean getNoColor() {
        return printSetupRecord.getNoColor();
    }

    
    public void setNoColor(boolean mono) {
        printSetupRecord.setNoColor(mono);
    }

    
    public boolean getDraft() {
        return printSetupRecord.getDraft();
    }

    
    public void setDraft(boolean d) {
        printSetupRecord.setDraft(d);
    }

    
    public boolean getNotes() {
        return printSetupRecord.getNotes();
    }

    
    public void setNotes(boolean printnotes) {
        printSetupRecord.setNotes(printnotes);
    }

    
    public boolean getNoOrientation() {
        return printSetupRecord.getNoOrientation();
    }

    
    public void setNoOrientation(boolean orientation) {
        printSetupRecord.setNoOrientation(orientation);
    }

    
    public boolean getUsePage() {
        return printSetupRecord.getUsePage();
    }

    
    public void setUsePage(boolean page) {
        printSetupRecord.setUsePage(page);
    }

    
    public short getHResolution() {
        return printSetupRecord.getHResolution();
    }

    
    public void setHResolution(short resolution) {
        printSetupRecord.setHResolution(resolution);
    }

    
    public short getVResolution() {
        return printSetupRecord.getVResolution();
    }

    
    public void setVResolution(short resolution) {
        printSetupRecord.setVResolution(resolution);
    }

    
    public double getHeaderMargin() {
        return printSetupRecord.getHeaderMargin();
    }

    
    public void setHeaderMargin(double headermargin) {
        printSetupRecord.setHeaderMargin(headermargin);
    }

    
    public double getFooterMargin() {
        return printSetupRecord.getFooterMargin();
    }

    
    public void setFooterMargin(double footermargin) {
        printSetupRecord.setFooterMargin(footermargin);
    }

    
    public short getCopies() {
        return printSetupRecord.getCopies();
    }

    
    public void setCopies(short copies) {
        printSetupRecord.setCopies(copies);
    }
}
