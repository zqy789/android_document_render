


package com.document.render.office.fc.poifs.eventfilesystem;

import com.document.render.office.fc.poifs.filesystem.DocumentDescriptor;
import com.document.render.office.fc.poifs.filesystem.POIFSDocumentPath;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;




class POIFSReaderRegistry {


    private Set omnivorousListeners;





    private Map selectiveListeners;






    private Map chosenDocumentDescriptors;



    POIFSReaderRegistry() {
        omnivorousListeners = new HashSet();
        selectiveListeners = new HashMap();
        chosenDocumentDescriptors = new HashMap();
    }



    void registerListener(final POIFSReaderListener listener,
                          final POIFSDocumentPath path,
                          final String documentName) {
        if (!omnivorousListeners.contains(listener)) {



            Set descriptors = (Set) selectiveListeners.get(listener);

            if (descriptors == null) {


                descriptors = new HashSet();
                selectiveListeners.put(listener, descriptors);
            }
            DocumentDescriptor descriptor = new DocumentDescriptor(path,
                    documentName);

            if (descriptors.add(descriptor)) {




                Set listeners =
                        (Set) chosenDocumentDescriptors.get(descriptor);

                if (listeners == null) {


                    listeners = new HashSet();
                    chosenDocumentDescriptors.put(descriptor, listeners);
                }
                listeners.add(listener);
            }
        }
    }



    void registerListener(final POIFSReaderListener listener) {
        if (!omnivorousListeners.contains(listener)) {





            removeSelectiveListener(listener);
            omnivorousListeners.add(listener);
        }
    }



    Iterator getListeners(final POIFSDocumentPath path, final String name) {
        Set rval = new HashSet(omnivorousListeners);
        Set selectiveListeners =
                (Set) chosenDocumentDescriptors.get(new DocumentDescriptor(path,
                        name));

        if (selectiveListeners != null) {
            rval.addAll(selectiveListeners);
        }
        return rval.iterator();
    }

    private void removeSelectiveListener(final POIFSReaderListener listener) {
        Set selectedDescriptors = (Set) selectiveListeners.remove(listener);

        if (selectedDescriptors != null) {
            Iterator iter = selectedDescriptors.iterator();

            while (iter.hasNext()) {
                dropDocument(listener, (DocumentDescriptor) iter.next());
            }
        }
    }

    private void dropDocument(final POIFSReaderListener listener,
                              final DocumentDescriptor descriptor) {
        Set listeners = (Set) chosenDocumentDescriptors.get(descriptor);

        listeners.remove(listener);
        if (listeners.size() == 0) {
            chosenDocumentDescriptors.remove(descriptor);
        }
    }
}

