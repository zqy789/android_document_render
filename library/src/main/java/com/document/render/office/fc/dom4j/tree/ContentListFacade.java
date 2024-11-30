

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.IllegalAddException;
import com.document.render.office.fc.dom4j.Node;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



public class ContentListFacade extends AbstractList {

    private List branchContent;


    private AbstractBranch branch;

    public ContentListFacade(AbstractBranch branch, List branchContent) {
        this.branch = branch;
        this.branchContent = branchContent;
    }

    public boolean add(Object object) {
        branch.childAdded(asNode(object));

        return branchContent.add(object);
    }

    public void add(int index, Object object) {
        branch.childAdded(asNode(object));
        branchContent.add(index, object);
    }

    public Object set(int index, Object object) {
        branch.childAdded(asNode(object));

        return branchContent.set(index, object);
    }

    public boolean remove(Object object) {
        branch.childRemoved(asNode(object));

        return branchContent.remove(object);
    }

    public Object remove(int index) {
        Object object = branchContent.remove(index);

        if (object != null) {
            branch.childRemoved(asNode(object));
        }

        return object;
    }

    public boolean addAll(Collection collection) {
        int count = branchContent.size();

        for (Iterator iter = collection.iterator(); iter.hasNext(); count++) {
            add(iter.next());
        }

        return count == branchContent.size();
    }

    public boolean addAll(int index, Collection collection) {
        int count = branchContent.size();

        for (Iterator iter = collection.iterator(); iter.hasNext(); count--) {
            add(index++, iter.next());
        }

        return count == branchContent.size();
    }

    public void clear() {
        for (Iterator iter = iterator(); iter.hasNext(); ) {
            Object object = iter.next();
            branch.childRemoved(asNode(object));
        }

        branchContent.clear();
    }

    public boolean removeAll(Collection c) {
        for (Iterator iter = c.iterator(); iter.hasNext(); ) {
            Object object = iter.next();
            branch.childRemoved(asNode(object));
        }

        return branchContent.removeAll(c);
    }

    public int size() {
        return branchContent.size();
    }

    public boolean isEmpty() {
        return branchContent.isEmpty();
    }

    public boolean contains(Object o) {
        return branchContent.contains(o);
    }

    public Object[] toArray() {
        return branchContent.toArray();
    }

    public Object[] toArray(Object[] a) {
        return branchContent.toArray(a);
    }

    public boolean containsAll(Collection c) {
        return branchContent.containsAll(c);
    }

    public Object get(int index) {
        return branchContent.get(index);
    }

    public int indexOf(Object o) {
        return branchContent.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return branchContent.lastIndexOf(o);
    }

    protected Node asNode(Object object) {
        if (object instanceof Node) {
            return (Node) object;
        } else {
            throw new IllegalAddException("This list must contain instances of "
                    + "Node. Invalid type: " + object);
        }
    }

    protected List getBackingList() {
        return branchContent;
    }
}


