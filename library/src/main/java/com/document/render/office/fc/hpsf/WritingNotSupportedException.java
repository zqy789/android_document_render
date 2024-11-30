

package com.document.render.office.fc.hpsf;


public class WritingNotSupportedException
        extends UnsupportedVariantTypeException {


    public WritingNotSupportedException(final long variantType,
                                        final Object value) {
        super(variantType, value);
    }

}
