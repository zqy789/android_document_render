

package com.document.render.office.fc.openxml4j.opc;

import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JRuntimeException;

import java.net.URI;
import java.net.URISyntaxException;



public final class PackagePartName implements Comparable<PackagePartName> {


    private static String[] RFC3986_PCHAR_SUB_DELIMS = {"!", "$", "&", "'", "(", ")", "*", "+",
            ",", ";", "="};



    private static String[] RFC3986_PCHAR_UNRESERVED_SUP = {"-", ".", "_", "~"};

    private static String[] RFC3986_PCHAR_AUTHORIZED_SUP = {":", "@"};

    private URI partNameURI;

    private boolean isRelationship;


    PackagePartName(URI uri, boolean checkConformance) throws InvalidFormatException {
        if (checkConformance) {
            throwExceptionIfInvalidPartUri(uri);
        } else {
            if (!PackagingURIHelper.PACKAGE_ROOT_URI.equals(uri)) {
                throw new OpenXML4JRuntimeException(
                        "OCP conformance must be check for ALL part name except special cases : ['/']");
            }
        }
        this.partNameURI = uri;
        this.isRelationship = isRelationshipPartURI(this.partNameURI);
    }


    PackagePartName(String partName, boolean checkConformance) throws InvalidFormatException {
        URI partURI;
        try {
            partURI = new URI(partName);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("partName argmument is not a valid OPC part name !");
        }

        if (checkConformance) {
            throwExceptionIfInvalidPartUri(partURI);
        } else {
            if (!PackagingURIHelper.PACKAGE_ROOT_URI.equals(partURI)) {
                throw new OpenXML4JRuntimeException(
                        "OCP conformance must be check for ALL part name except special cases : ['/']");
            }
        }
        this.partNameURI = partURI;
        this.isRelationship = isRelationshipPartURI(this.partNameURI);
    }


    private static void throwExceptionIfInvalidPartUri(URI partUri) throws InvalidFormatException {
        if (partUri == null)
            throw new IllegalArgumentException("partUri");

        throwExceptionIfEmptyURI(partUri);


        throwExceptionIfAbsoluteUri(partUri);


        throwExceptionIfPartNameNotStartsWithForwardSlashChar(partUri);


        throwExceptionIfPartNameEndsWithForwardSlashChar(partUri);



        throwExceptionIfPartNameHaveInvalidSegments(partUri);
    }


    private static void throwExceptionIfEmptyURI(URI partURI) throws InvalidFormatException {
        if (partURI == null)
            throw new IllegalArgumentException("partURI");

        String uriPath = partURI.getPath();
        if (uriPath.length() == 0
                || ((uriPath.length() == 1) && (uriPath.charAt(0) == PackagingURIHelper.FORWARD_SLASH_CHAR)))
            throw new InvalidFormatException("A part name shall not be empty [M1.1]: "
                    + partURI.getPath());
    }


    private static void throwExceptionIfPartNameHaveInvalidSegments(URI partUri)
            throws InvalidFormatException {
        if (partUri == null) {
            throw new IllegalArgumentException("partUri");
        }


        String[] segments = partUri.toASCIIString().split("/");
        if (segments.length <= 1 || !segments[0].equals(""))
            throw new InvalidFormatException("A part name shall not have empty segments [M1.3]: "
                    + partUri.getPath());

        for (int i = 1; i < segments.length; ++i) {
            String seg = segments[i];
            if (seg == null || "".equals(seg)) {
                throw new InvalidFormatException(
                        "A part name shall not have empty segments [M1.3]: " + partUri.getPath());
            }

            if (seg.endsWith(".")) {
                throw new InvalidFormatException(
                        "A segment shall not end with a dot ('.') character [M1.9]: "
                                + partUri.getPath());
            }

            if ("".equals(seg.replaceAll("\\\\.", ""))) {


                throw new InvalidFormatException(
                        "A segment shall include at least one non-dot character. [M1.10]: "
                                + partUri.getPath());
            }


            checkPCharCompliance(seg);
        }
    }


    private static void checkPCharCompliance(String segment) throws InvalidFormatException {
        boolean errorFlag;
        for (int i = 0; i < segment.length(); ++i) {
            char c = segment.charAt(i);
            errorFlag = true;




            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                errorFlag = false;
            } else {

                for (int j = 0; j < RFC3986_PCHAR_UNRESERVED_SUP.length; ++j) {
                    if (c == RFC3986_PCHAR_UNRESERVED_SUP[j].charAt(0)) {
                        errorFlag = false;
                        break;
                    }
                }


                for (int j = 0; errorFlag && j < RFC3986_PCHAR_AUTHORIZED_SUP.length; ++j) {
                    if (c == RFC3986_PCHAR_AUTHORIZED_SUP[j].charAt(0)) {
                        errorFlag = false;
                    }
                }


                for (int j = 0; errorFlag && j < RFC3986_PCHAR_SUB_DELIMS.length; ++j) {
                    if (c == RFC3986_PCHAR_SUB_DELIMS[j].charAt(0)) {
                        errorFlag = false;
                    }
                }
            }

            if (errorFlag && c == '%') {


                if (((segment.length() - i) < 2)) {
                    throw new InvalidFormatException("The segment " + segment
                            + " contain invalid encoded character !");
                }



                errorFlag = false;


                char decodedChar = (char) Integer.parseInt(segment.substring(i + 1, i + 3), 16);
                i += 2;


                if (decodedChar == '/' || decodedChar == '\\')
                    throw new InvalidFormatException(
                            "A segment shall not contain percent-encoded forward slash ('/'), or backward slash ('\') characters. [M1.7]");




                if ((decodedChar >= 'A' && decodedChar <= 'Z')
                        || (decodedChar >= 'a' && decodedChar <= 'z')
                        || (decodedChar >= '0' && decodedChar <= '9'))
                    errorFlag = true;


                for (int j = 0; !errorFlag && j < RFC3986_PCHAR_UNRESERVED_SUP.length; ++j) {
                    if (c == RFC3986_PCHAR_UNRESERVED_SUP[j].charAt(0)) {
                        errorFlag = true;
                        break;
                    }
                }
                if (errorFlag)
                    throw new InvalidFormatException(
                            "A segment shall not contain percent-encoded unreserved characters. [M1.8]");
            }

            if (errorFlag)
                throw new InvalidFormatException(
                        "A segment shall not hold any characters other than pchar characters. [M1.6]");
        }
    }


    private static void throwExceptionIfPartNameNotStartsWithForwardSlashChar(URI partUri)
            throws InvalidFormatException {
        String uriPath = partUri.getPath();
        if (uriPath.length() > 0 && uriPath.charAt(0) != PackagingURIHelper.FORWARD_SLASH_CHAR)
            throw new InvalidFormatException(
                    "A part name shall start with a forward slash ('/') character [M1.4]: "
                            + partUri.getPath());
    }


    private static void throwExceptionIfPartNameEndsWithForwardSlashChar(URI partUri)
            throws InvalidFormatException {
        String uriPath = partUri.getPath();
        if (uriPath.length() > 0
                && uriPath.charAt(uriPath.length() - 1) == PackagingURIHelper.FORWARD_SLASH_CHAR)
            throw new InvalidFormatException(
                    "A part name shall not have a forward slash as the last character [M1.5]: "
                            + partUri.getPath());
    }


    private static void throwExceptionIfAbsoluteUri(URI partUri) throws InvalidFormatException {
        if (partUri.isAbsolute())
            throw new InvalidFormatException("Absolute URI forbidden: " + partUri);
    }


    private boolean isRelationshipPartURI(URI partUri) {
        if (partUri == null)
            throw new IllegalArgumentException("partUri");

        return partUri.getPath().matches(
                "^.*/" + PackagingURIHelper.RELATIONSHIP_PART_SEGMENT_NAME + "/.*\\"
                        + PackagingURIHelper.RELATIONSHIP_PART_EXTENSION_NAME + "$");
    }


    public boolean isRelationshipPartURI() {
        return this.isRelationship;
    }


    public int compareTo(PackagePartName otherPartName) {
        if (otherPartName == null)
            return -1;
        return this.partNameURI.toASCIIString().toLowerCase()
                .compareTo(otherPartName.partNameURI.toASCIIString().toLowerCase());
    }


    public String getExtension() {
        String fragment = this.partNameURI.getPath();
        if (fragment.length() > 0) {
            int i = fragment.lastIndexOf(".");
            if (i > -1)
                return fragment.substring(i + 1);
        }
        return "";
    }


    public String getName() {
        return this.partNameURI.toASCIIString();
    }


    @Override
    public boolean equals(Object otherPartName) {
        if (otherPartName == null || !(otherPartName instanceof PackagePartName))
            return false;
        return this.partNameURI.toASCIIString().toLowerCase()
                .equals(((PackagePartName) otherPartName).partNameURI.toASCIIString().toLowerCase());
    }

    @Override
    public int hashCode() {
        return this.partNameURI.toASCIIString().toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }




    public URI getURI() {
        return this.partNameURI;
    }
}
