

package com.document.render.office.fc.openxml4j.opc;

import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.InvalidOperationException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;



public final class PackagingURIHelper {

    public static final String RELATIONSHIP_PART_EXTENSION_NAME;

    public static final String RELATIONSHIP_PART_SEGMENT_NAME;

    public static final String PACKAGE_PROPERTIES_SEGMENT_NAME;

    public static final String PACKAGE_CORE_PROPERTIES_NAME;

    public static final char FORWARD_SLASH_CHAR;

    public static final String FORWARD_SLASH_STRING;

    public static final URI PACKAGE_RELATIONSHIPS_ROOT_URI;

    public static final PackagePartName PACKAGE_RELATIONSHIPS_ROOT_PART_NAME;

    public static final URI CORE_PROPERTIES_URI;

    public static final PackagePartName CORE_PROPERTIES_PART_NAME;

    public static final URI PACKAGE_ROOT_URI;

    public static final PackagePartName PACKAGE_ROOT_PART_NAME;
    private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F'};

    private static URI packageRootUri;


    static {
        RELATIONSHIP_PART_SEGMENT_NAME = "_rels";
        RELATIONSHIP_PART_EXTENSION_NAME = ".rels";
        FORWARD_SLASH_CHAR = '/';
        FORWARD_SLASH_STRING = "/";
        PACKAGE_PROPERTIES_SEGMENT_NAME = "docProps";
        PACKAGE_CORE_PROPERTIES_NAME = "core.xml";


        URI uriPACKAGE_ROOT_URI = null;
        URI uriPACKAGE_RELATIONSHIPS_ROOT_URI = null;
        URI uriPACKAGE_PROPERTIES_URI = null;
        try {
            uriPACKAGE_ROOT_URI = new URI("/");
            uriPACKAGE_RELATIONSHIPS_ROOT_URI = new URI(FORWARD_SLASH_CHAR
                    + RELATIONSHIP_PART_SEGMENT_NAME + FORWARD_SLASH_CHAR
                    + RELATIONSHIP_PART_EXTENSION_NAME);
            packageRootUri = new URI("/");
            uriPACKAGE_PROPERTIES_URI = new URI(FORWARD_SLASH_CHAR
                    + PACKAGE_PROPERTIES_SEGMENT_NAME + FORWARD_SLASH_CHAR
                    + PACKAGE_CORE_PROPERTIES_NAME);
        } catch (URISyntaxException e) {

        }
        PACKAGE_ROOT_URI = uriPACKAGE_ROOT_URI;
        PACKAGE_RELATIONSHIPS_ROOT_URI = uriPACKAGE_RELATIONSHIPS_ROOT_URI;
        CORE_PROPERTIES_URI = uriPACKAGE_PROPERTIES_URI;


        PackagePartName tmpPACKAGE_ROOT_PART_NAME = null;
        PackagePartName tmpPACKAGE_RELATIONSHIPS_ROOT_PART_NAME = null;
        PackagePartName tmpCORE_PROPERTIES_URI = null;
        try {
            tmpPACKAGE_RELATIONSHIPS_ROOT_PART_NAME = createPartName(PACKAGE_RELATIONSHIPS_ROOT_URI);
            tmpCORE_PROPERTIES_URI = createPartName(CORE_PROPERTIES_URI);
            tmpPACKAGE_ROOT_PART_NAME = new PackagePartName(PACKAGE_ROOT_URI, false);
        } catch (InvalidFormatException e) {

        }
        PACKAGE_RELATIONSHIPS_ROOT_PART_NAME = tmpPACKAGE_RELATIONSHIPS_ROOT_PART_NAME;
        CORE_PROPERTIES_PART_NAME = tmpCORE_PROPERTIES_URI;
        PACKAGE_ROOT_PART_NAME = tmpPACKAGE_ROOT_PART_NAME;
    }


    public static URI getPackageRootUri() {
        return packageRootUri;
    }


    public static boolean isRelationshipPartURI(URI partUri) {
        if (partUri == null)
            throw new IllegalArgumentException("partUri");

        return partUri.getPath().matches(
                ".*" + RELATIONSHIP_PART_SEGMENT_NAME + ".*" + RELATIONSHIP_PART_EXTENSION_NAME + "$");
    }


    public static String getFilename(URI uri) {
        if (uri != null) {
            String path = uri.getPath();
            int len = path.length();
            int num2 = len;
            while (--num2 >= 0) {
                char ch1 = path.charAt(num2);
                if (ch1 == PackagingURIHelper.FORWARD_SLASH_CHAR)
                    return path.substring(num2 + 1, len);
            }
        }
        return "";
    }


    public static String getFilenameWithoutExtension(URI uri) {
        String filename = getFilename(uri);
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1)
            return filename;
        return filename.substring(0, dotIndex);
    }


    public static URI getPath(URI uri) {
        if (uri != null) {
            String path = uri.getPath();
            int len = path.length();
            int num2 = len;
            while (--num2 >= 0) {
                char ch1 = path.charAt(num2);
                if (ch1 == PackagingURIHelper.FORWARD_SLASH_CHAR) {
                    try {
                        return new URI(path.substring(0, num2));
                    } catch (URISyntaxException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }


    public static URI combine(URI prefix, URI suffix) {
        URI retUri = null;
        try {
            retUri = new URI(combine(prefix.getPath(), suffix.getPath()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Prefix and suffix can't be combine !");
        }
        return retUri;
    }


    public static String combine(String prefix, String suffix) {
        if (!prefix.endsWith("" + FORWARD_SLASH_CHAR)
                && !suffix.startsWith("" + FORWARD_SLASH_CHAR))
            return prefix + FORWARD_SLASH_CHAR + suffix;
        else if ((!prefix.endsWith("" + FORWARD_SLASH_CHAR)
                && suffix.startsWith("" + FORWARD_SLASH_CHAR) || (prefix.endsWith(""
                + FORWARD_SLASH_CHAR) && !suffix.startsWith("" + FORWARD_SLASH_CHAR))))
            return prefix + suffix;
        else
            return "";
    }


    public static URI relativizeURI(URI sourceURI, URI targetURI, boolean msCompatible) {
        StringBuilder retVal = new StringBuilder();
        String[] segmentsSource = sourceURI.getPath().split("/", -1);
        String[] segmentsTarget = targetURI.getPath().split("/", -1);


        if (segmentsSource.length == 0) {
            throw new IllegalArgumentException("Can't relativize an empty source URI !");
        }


        if (segmentsTarget.length == 0) {
            throw new IllegalArgumentException("Can't relativize an empty target URI !");
        }



        if (sourceURI.toString().equals("/")) {
            String path = targetURI.getPath();
            if (msCompatible && path.length() > 0 && path.charAt(0) == '/') {
                try {
                    targetURI = new URI(path.substring(1));
                } catch (Exception e) {
                    return null;
                }
            }
            return targetURI;
        }




        int segmentsTheSame = 0;
        for (int i = 0; i < segmentsSource.length && i < segmentsTarget.length; i++) {
            if (segmentsSource[i].equals(segmentsTarget[i])) {

                segmentsTheSame++;
            } else {
                break;
            }
        }


        if ((segmentsTheSame == 0 || segmentsTheSame == 1) && segmentsSource[0].equals("")
                && segmentsTarget[0].equals("")) {
            for (int i = 0; i < segmentsSource.length - 2; i++) {
                retVal.append("../");
            }
            for (int i = 0; i < segmentsTarget.length; i++) {
                if (segmentsTarget[i].equals(""))
                    continue;
                retVal.append(segmentsTarget[i]);
                if (i != segmentsTarget.length - 1)
                    retVal.append("/");
            }

            try {
                return new URI(retVal.toString());
            } catch (Exception e) {
                return null;
            }
        }


        if (segmentsTheSame == segmentsSource.length && segmentsTheSame == segmentsTarget.length) {
            if (sourceURI.equals(targetURI)) {




                retVal.append(segmentsSource[segmentsSource.length - 1]);
            } else {
                retVal.append("");
            }

        } else {





            if (segmentsTheSame == 1) {
                retVal.append("/");
            } else {
                for (int j = segmentsTheSame; j < segmentsSource.length - 1; j++) {
                    retVal.append("../");
                }
            }


            for (int j = segmentsTheSame; j < segmentsTarget.length; j++) {
                if (retVal.length() > 0 && retVal.charAt(retVal.length() - 1) != '/') {
                    retVal.append("/");
                }
                retVal.append(segmentsTarget[j]);
            }
        }


        String fragment = targetURI.getRawFragment();
        if (fragment != null) {
            retVal.append("#").append(fragment);
        }

        try {
            return new URI(retVal.toString());
        } catch (Exception e) {
            return null;
        }
    }


    public static URI relativizeURI(URI sourceURI, URI targetURI) {
        return relativizeURI(sourceURI, targetURI, false);
    }


    public static URI resolvePartUri(URI sourcePartUri, URI targetUri) {
        if (sourcePartUri == null || sourcePartUri.isAbsolute()) {
            throw new IllegalArgumentException("sourcePartUri invalid - " + sourcePartUri);
        }

        if (targetUri == null || targetUri.isAbsolute()) {
            throw new IllegalArgumentException("targetUri invalid - " + targetUri);
        }

        return sourcePartUri.resolve(targetUri);
    }


    public static URI getURIFromPath(String path) {
        URI retUri;
        try {
            retUri = toURI(path);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("path");
        }
        return retUri;
    }


    public static URI getSourcePartUriFromRelationshipPartUri(URI relationshipPartUri) {
        if (relationshipPartUri == null)
            throw new IllegalArgumentException("Must not be null");

        if (!isRelationshipPartURI(relationshipPartUri))
            throw new IllegalArgumentException("Must be a relationship part");

        if (relationshipPartUri.compareTo(PACKAGE_RELATIONSHIPS_ROOT_URI) == 0)
            return PACKAGE_ROOT_URI;

        String filename = relationshipPartUri.getPath();
        String filenameWithoutExtension = getFilenameWithoutExtension(relationshipPartUri);
        filename = filename
                .substring(
                        0,
                        ((filename.length() - filenameWithoutExtension.length()) - RELATIONSHIP_PART_EXTENSION_NAME
                                .length()));
        filename = filename.substring(0,
                filename.length() - RELATIONSHIP_PART_SEGMENT_NAME.length() - 1);
        filename = combine(filename, filenameWithoutExtension);
        return getURIFromPath(filename);
    }


    public static PackagePartName createPartName(URI partUri) throws InvalidFormatException {
        if (partUri == null)
            throw new IllegalArgumentException("partName");

        return new PackagePartName(partUri, true);
    }


    public static PackagePartName createPartName(String partName) throws InvalidFormatException {
        URI partNameURI;
        try {
            partNameURI = toURI(partName);
        } catch (URISyntaxException e) {
            throw new InvalidFormatException(e.getMessage());
        }
        return createPartName(partNameURI);
    }


    public static PackagePartName createPartName(String partName, PackagePart relativePart)
            throws InvalidFormatException {
        URI newPartNameURI;
        try {
            newPartNameURI = resolvePartUri(relativePart.getPartName().getURI(), new URI(partName));
        } catch (URISyntaxException e) {
            throw new InvalidFormatException(e.getMessage());
        }
        return createPartName(newPartNameURI);
    }


    public static PackagePartName createPartName(URI partName, PackagePart relativePart)
            throws InvalidFormatException {
        URI newPartNameURI = resolvePartUri(relativePart.getPartName().getURI(), partName);
        return createPartName(newPartNameURI);
    }


    public static boolean isValidPartName(URI partUri) {
        if (partUri == null)
            throw new IllegalArgumentException("partUri");

        try {
            createPartName(partUri);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static String decodeURI(URI uri) {
        StringBuffer retVal = new StringBuffer();
        String uriStr = uri.toASCIIString();
        char c;
        for (int i = 0; i < uriStr.length(); ++i) {
            c = uriStr.charAt(i);
            if (c == '%') {


                if (((uriStr.length() - i) < 2)) {
                    throw new IllegalArgumentException("The uri " + uriStr
                            + " contain invalid encoded character !");
                }


                char decodedChar = (char) Integer.parseInt(uriStr.substring(i + 1, i + 3), 16);
                retVal.append(decodedChar);
                i += 2;
                continue;
            }
            retVal.append(c);
        }
        return retVal.toString();
    }


    public static PackagePartName getRelationshipPartName(PackagePartName partName) {
        if (partName == null)
            throw new IllegalArgumentException("partName");

        if (PackagingURIHelper.PACKAGE_ROOT_URI.getPath().equals(partName.getURI().getPath()))
            return PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_PART_NAME;

        if (partName.isRelationshipPartURI())
            throw new InvalidOperationException("Can't be a relationship part");

        String fullPath = partName.getURI().getPath();
        String filename = getFilename(partName.getURI());
        fullPath = fullPath.substring(0, fullPath.length() - filename.length());
        fullPath = combine(fullPath, PackagingURIHelper.RELATIONSHIP_PART_SEGMENT_NAME);
        fullPath = combine(fullPath, filename);
        fullPath = fullPath + PackagingURIHelper.RELATIONSHIP_PART_EXTENSION_NAME;

        PackagePartName retPartName;
        try {
            retPartName = createPartName(fullPath);
        } catch (InvalidFormatException e) {


            return null;
        }
        return retPartName;
    }


    public static URI toURI(String value) throws URISyntaxException {

        if (value.indexOf("\\") != -1) {
            value = value.replace('\\', '/');
        }



        int fragmentIdx = value.indexOf('#');
        if (fragmentIdx != -1) {
            String path = value.substring(0, fragmentIdx);
            String fragment = value.substring(fragmentIdx + 1);

            value = path + "#" + encode(fragment);
        }

        return new URI(value);
    }


    public static String encode(String s) {
        int n = s.length();
        if (n == 0)
            return s;

        ByteBuffer bb;
        try {
            bb = ByteBuffer.wrap(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xff;
            if (isUnsafe(b)) {
                sb.append('%');
                sb.append(hexDigits[(b >> 4) & 0x0F]);
                sb.append(hexDigits[(b >> 0) & 0x0F]);
            } else {
                sb.append((char) b);
            }
        }
        return sb.toString();
    }

    private static boolean isUnsafe(int ch) {
        return ch > 0x80 || " ".indexOf(ch) >= 0;
    }

}
