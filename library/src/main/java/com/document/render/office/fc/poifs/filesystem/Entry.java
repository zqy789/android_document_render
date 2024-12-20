


package com.document.render.office.fc.poifs.filesystem;



public interface Entry {



    public String getName();



    public boolean isDirectoryEntry();



    public boolean isDocumentEntry();



    public DirectoryEntry getParent();



    public boolean delete();



    public boolean renameTo(final String newName);
}

