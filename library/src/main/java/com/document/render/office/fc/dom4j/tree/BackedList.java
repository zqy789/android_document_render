

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.IllegalAddException;
import com.document.render.office.fc.dom4j.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



public class BackedList extends ArrayList {

    private List branchContent;


    private AbstractBranch branch;

    public BackedList(AbstractBranch branch, List branchContent) {
        this(branch, branchContent, branchContent.size());
    }

    public BackedList(AbstractBranch branch, List branchContent, int capacity) {
        super(capacity);
        this.branch = branch;
        this.branchContent = branchContent;
    }

    public BackedList(AbstractBranch branch, List branchContent, List initialContent) {
        super(initialContent);
        this.branch = branch;
        this.branchContent = branchContent;
    }

    public boolean add(Object object) {
        branch.addNode(asNode(object));

        return super.add(object);
    }

    public void add(int index, Object object) {
        int size = size();

        if (index < 0) {
            throw new IndexOutOfBoundsException("Index value: " + index + " is less than zero");
        } else if (index > size) {
            throw new IndexOutOfBoundsException("Index value: " + index
                    + " cannot be greater than " + "the size: " + size);
        }

        int realIndex;

        if (size == 0) {
            realIndex = branchContent.size();
        } else if (index < size) {
            realIndex = branchContent.indexOf(get(index));
        } else {
            realIndex = branchContent.indexOf(get(size - 1)) + 1;
        }

        branch.addNode(realIndex, asNode(object));
        super.add(index, object);
    }

    public Object set(int index, Object object) {
        int realIndex = branchContent.indexOf(get(index));

        if (realIndex < 0) {
            realIndex = (index == 0) ? 0 : Integer.MAX_VALUE;
        }

        if (realIndex < branchContent.size()) {
            branch.removeNode(asNode(get(index)));
            branch.addNode(realIndex, asNode(object));
        } else {
            branch.removeNode(asNode(get(index)));
            branch.addNode(asNode(object));
        }

        branch.childAdded(asNode(object));

        return super.set(index, object);
    }

    public boolean remove(Object object) {
        branch.removeNode(asNode(object));

        return super.remove(object);
    }

    public Object remove(int index) {
        Object object = super.remove(index);

        if (object != null) {
            branch.removeNode(asNode(object));
        }

        return object;
    }

    public boolean addAll(Collection collection) {
        ensureCapacity(size() + collection.size());

        int count = size();

        for (Iterator iter = collection.iterator(); iter.hasNext(); count--) {
            add(iter.next());
        }

        return count != 0;
    }

    public boolean addAll(int index, Collection collection) {
        ensureCapacity(size() + collection.size());

        int count = size();

        for (Iterator iter = collection.iterator(); iter.hasNext(); count--) {
            add(index++, iter.next());
        }

        return count != 0;
    }

    public void clear() {
        for (Iterator iter = iterator(); iter.hasNext(); ) {
            Object object = iter.next();
            branchContent.remove(object);
            branch.childRemoved(asNode(object));
        }

        super.clear();
    }


    public void addLocal(Object object) {
        super.add(object);
    }

    protected Node asNode(Object object) {
        if (object instanceof Node) {
            return (Node) object;
        } else {
            throw new IllegalAddException("This list must contain instances "
                    + "of Node. Invalid type: " + object);
        }
    }
}


