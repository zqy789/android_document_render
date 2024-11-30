

package com.document.render.office.fc.dom4j;

import com.document.render.office.fc.dom4j.tree.QNameCache;
import com.document.render.office.fc.dom4j.util.SingletonStrategy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



public class QName implements Serializable {

    private static SingletonStrategy singleton = null;

    static {
        try {
            String defaultSingletonClass = "com.wxiwei.fc.dom4j.util.SimpleSingleton";
            Class clazz = null;
            try {
                String singletonClass = defaultSingletonClass;
                singletonClass = System.getProperty("com.wxiwei.fc.dom4j.QName.singleton.strategy",
                        singletonClass);
                clazz = Class.forName(singletonClass);
            } catch (Exception exc1) {
                try {
                    String singletonClass = defaultSingletonClass;
                    clazz = Class.forName(singletonClass);
                } catch (Exception exc2) {
                }
            }
            singleton = (SingletonStrategy) clazz.newInstance();
            singleton.setSingletonClassName(QNameCache.class.getName());
        } catch (Exception exc3) {
        }
    }


    private String name;


    private String qualifiedName;


    private transient Namespace namespace;


    private int hashCode;


    private DocumentFactory documentFactory;

    public QName(String name) {
        this(name, Namespace.NO_NAMESPACE);
    }

    public QName(String name, Namespace namespace) {
        this.name = (name == null) ? "" : name;
        this.namespace = (namespace == null) ? Namespace.NO_NAMESPACE : namespace;
    }

    public QName(String name, Namespace namespace, String qualifiedName) {
        this.name = (name == null) ? "" : name;
        this.qualifiedName = qualifiedName;
        this.namespace = (namespace == null) ? Namespace.NO_NAMESPACE : namespace;
    }

    public static QName get(String name) {
        return getCache().get(name);
    }

    public static QName get(String name, Namespace namespace) {
        return getCache().get(name, namespace);
    }

    public static QName get(String name, String prefix, String uri) {
        if (((prefix == null) || (prefix.length() == 0)) && (uri == null)) {
            return QName.get(name);
        } else if ((prefix == null) || (prefix.length() == 0)) {
            return getCache().get(name, Namespace.get(uri));
        } else if (uri == null) {
            return QName.get(name);
        } else {
            return getCache().get(name, Namespace.get(prefix, uri));
        }
    }

    public static QName get(String qualifiedName, String uri) {
        if (uri == null) {
            return getCache().get(qualifiedName);
        } else {
            return getCache().get(qualifiedName, uri);
        }
    }

    public static QName get(String localName, Namespace namespace, String qualifiedName) {
        return getCache().get(localName, namespace, qualifiedName);
    }

    private static QNameCache getCache() {
        QNameCache cache = (QNameCache) singleton.instance();
        return cache;
    }


    public String getName() {
        return name;
    }


    public String getQualifiedName() {
        if (qualifiedName == null) {
            String prefix = getNamespacePrefix();

            if ((prefix != null) && (prefix.length() > 0)) {
                qualifiedName = prefix + ":" + name;
            } else {
                qualifiedName = name;
            }
        }

        return qualifiedName;
    }


    public Namespace getNamespace() {
        return namespace;
    }


    public String getNamespacePrefix() {
        if (namespace == null) {
            return "";
        }

        return namespace.getPrefix();
    }


    public String getNamespaceURI() {
        if (namespace == null) {
            return "";
        }

        return namespace.getURI();
    }


    public int hashCode() {
        if (hashCode == 0) {
            hashCode = getName().hashCode() ^ getNamespaceURI().hashCode();

            if (hashCode == 0) {
                hashCode = 0xbabe;
            }
        }

        return hashCode;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof QName) {
            QName that = (QName) object;


            if (hashCode() == that.hashCode()) {
                return getName().equals(that.getName())
                        && getNamespaceURI().equals(that.getNamespaceURI());
            }
        }

        return false;
    }

    public String toString() {
        return super.toString() + " [name: " + getName() + " namespace: \"" + getNamespace()
                + "\"]";
    }


    public DocumentFactory getDocumentFactory() {
        return documentFactory;
    }

    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {


        out.writeObject(namespace.getPrefix());
        out.writeObject(namespace.getURI());

        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String prefix = (String) in.readObject();
        String uri = (String) in.readObject();

        in.defaultReadObject();

        namespace = Namespace.get(prefix, uri);
    }
}


