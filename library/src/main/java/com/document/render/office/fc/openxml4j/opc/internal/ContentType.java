

package com.document.render.office.fc.openxml4j.opc.internal;

import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public final class ContentType {


    private final static Pattern patternMediaType;

    static {

        String token = "[\\x21-\\x7E&&[^\\(\\)<>@,;:\\\\/\"\\[\\]\\?={}\\x20\\x09]]";









        patternMediaType = Pattern.compile("^(" + token + "+)/(" + token + "+)$");
    }


    private String type;

    private String subType;

    private Hashtable<String, String> parameters;


    public ContentType(String contentType) throws InvalidFormatException {

        String contentTypeASCII = null;
        try {
            contentTypeASCII = new String(contentType.getBytes(), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new InvalidFormatException("The specified content type is not an ASCII value.");
        }

        Matcher mMediaType = patternMediaType.matcher(contentTypeASCII);
        if (!mMediaType.matches())
            throw new InvalidFormatException("The specified content type '" + contentType
                    + "' is not compliant with RFC 2616: malformed content type.");


        if (mMediaType.groupCount() >= 2) {
            this.type = mMediaType.group(1);
            this.subType = mMediaType.group(2);

            this.parameters = new Hashtable<String, String>(1);
            for (int i = 4; i <= mMediaType.groupCount() && (mMediaType.group(i) != null); i += 2) {
                this.parameters.put(mMediaType.group(i), mMediaType.group(i + 1));
            }
        }
    }

    @Override
    public final String toString() {
        StringBuffer retVal = new StringBuffer();
        retVal.append(this.getType());
        retVal.append("/");
        retVal.append(this.getSubType());







        return retVal.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return (!(obj instanceof ContentType))
                || (this.toString().equalsIgnoreCase(obj.toString()));
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }




    public String getSubType() {
        return this.subType;
    }


    public String getType() {
        return this.type;
    }


    public String getParameters(String key) {
        return parameters.get(key);
    }
}
