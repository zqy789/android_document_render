

package com.document.render.office.fc.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;



public class BinaryTree extends AbstractMap {
    static int _KEY = 0;
    static int _VALUE = 1;
    private static int _INDEX_SUM = _KEY + _VALUE;
    private static int _MINIMUM_INDEX = 0;
    private static int _INDEX_COUNT = 2;
    private static String[] _data_name = new String[]
            {
                    "key", "value"
            };
    final Node[] _root;
    private final Set[] _key_set = new Set[]{null, null};
    private final Set[] _entry_set = new Set[]{null, null};
    private final Collection[] _value_collection = new Collection[]{null, null};
    int _size = 0;
    int _modifications = 0;


    public BinaryTree() {
        _root = new Node[]{null, null,};
    }


    public BinaryTree(Map map)
            throws ClassCastException, NullPointerException,
            IllegalArgumentException {
        this();
        putAll(map);
    }


    private static int compare(Comparable o1, Comparable o2) {
        return o1.compareTo(o2);
    }


    static Node leastNode(Node node, int index) {
        Node rval = node;

        if (rval != null) {
            while (rval.getLeft(index) != null) {
                rval = rval.getLeft(index);
            }
        }
        return rval;
    }


    static Node nextGreater(Node node, int index) {
        Node rval = null;

        if (node == null) {
            rval = null;
        } else if (node.getRight(index) != null) {



            rval = leastNode(node.getRight(index), index);
        } else {







            Node parent = node.getParent(index);
            Node child = node;

            while ((parent != null) && (child == parent.getRight(index))) {
                child = parent;
                parent = parent.getParent(index);
            }
            rval = parent;
        }
        return rval;
    }


    private static void copyColor(Node from, Node to, int index) {
        if (to != null) {
            if (from == null) {


                to.setBlack(index);
            } else {
                to.copyColor(from, index);
            }
        }
    }


    private static boolean isRed(Node node, int index) {
        return node == null ? false : node.isRed(index);
    }


    private static boolean isBlack(Node node, int index) {
        return node == null ? true : node.isBlack(index);
    }


    private static void makeRed(Node node, int index) {
        if (node != null) {
            node.setRed(index);
        }
    }


    private static void makeBlack(Node node, int index) {
        if (node != null) {
            node.setBlack(index);
        }
    }


    private static Node getGrandParent(Node node, int index) {
        return getParent(getParent(node, index), index);
    }


    private static Node getParent(Node node, int index) {
        return ((node == null) ? null
                : node.getParent(index));
    }


    private static Node getRightChild(Node node, int index) {
        return (node == null) ? null
                : node.getRight(index);
    }


    private static Node getLeftChild(Node node, int index) {
        return (node == null) ? null
                : node.getLeft(index);
    }


    private static boolean isLeftChild(Node node, int index) {
        if (node == null) {
            return true;
        }
        if (node.getParent(index) == null) {
            return false;
        }
        return node == node.getParent(index).getLeft(index);
    }


    private static boolean isRightChild(Node node, int index) {
        if (node == null) {
            return true;
        }
        if (node.getParent(index) == null) {
            return false;
        }
        return node == node.getParent(index).getRight(index);
    }


    private static void checkNonNullComparable(Object o,
                                               int index) {
        if (o == null) {
            throw new NullPointerException(_data_name[index]
                    + " cannot be null");
        }
        if (!(o instanceof Comparable)) {
            throw new ClassCastException(_data_name[index]
                    + " must be Comparable");
        }
    }


    private static void checkKey(Object key) {
        checkNonNullComparable(key, _KEY);
    }


    private static void checkValue(Object value) {
        checkNonNullComparable(value, _VALUE);
    }


    private static void checkKeyAndValue(Object key, Object value) {
        checkKey(key);
        checkValue(value);
    }


    public Object getKeyForValue(Object value)
            throws ClassCastException, NullPointerException {
        return doGet((Comparable) value, _VALUE);
    }


    public Object removeValue(Object value) {
        return doRemove((Comparable) value, _VALUE);
    }


    public Set entrySetByValue() {
        if (_entry_set[_VALUE] == null) {
            _entry_set[_VALUE] = new AbstractSet() {
                public Iterator iterator() {
                    return new BinaryTreeIterator(_VALUE) {
                        protected Object doGetNext() {
                            return _last_returned_node;
                        }
                    };
                }

                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry) o;
                    Object key = entry.getKey();
                    Node node = lookup((Comparable) entry.getValue(),
                            _VALUE);

                    return (node != null) && node.getData(_KEY).equals(key);
                }

                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry) o;
                    Object key = entry.getKey();
                    Node node = lookup((Comparable) entry.getValue(),
                            _VALUE);

                    if ((node != null) && node.getData(_KEY).equals(key)) {
                        doRedBlackDelete(node);
                        return true;
                    }
                    return false;
                }

                public int size() {
                    return BinaryTree.this.size();
                }

                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return _entry_set[_VALUE];
    }



    public Set keySetByValue() {
        if (_key_set[_VALUE] == null) {
            _key_set[_VALUE] = new AbstractSet() {
                public Iterator iterator() {
                    return new BinaryTreeIterator(_VALUE) {
                        protected Object doGetNext() {
                            return _last_returned_node.getData(_KEY);
                        }
                    };
                }

                public int size() {
                    return BinaryTree.this.size();
                }

                public boolean contains(Object o) {
                    return containsKey(o);
                }

                public boolean remove(Object o) {
                    int old_size = _size;

                    BinaryTree.this.remove(o);
                    return _size != old_size;
                }

                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return _key_set[_VALUE];
    }



    public Collection valuesByValue() {
        if (_value_collection[_VALUE] == null) {
            _value_collection[_VALUE] = new AbstractCollection() {
                public Iterator iterator() {
                    return new BinaryTreeIterator(_VALUE) {
                        protected Object doGetNext() {
                            return _last_returned_node.getData(_VALUE);
                        }
                    };
                }

                public int size() {
                    return BinaryTree.this.size();
                }

                public boolean contains(Object o) {
                    return containsValue(o);
                }

                public boolean remove(Object o) {
                    int old_size = _size;

                    removeValue(o);
                    return _size != old_size;
                }

                public boolean removeAll(Collection c) {
                    boolean modified = false;
                    Iterator iter = c.iterator();

                    while (iter.hasNext()) {
                        if (removeValue(iter.next()) != null) {
                            modified = true;
                        }
                    }
                    return modified;
                }

                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return _value_collection[_VALUE];
    }


    private Object doRemove(Comparable o, int index) {
        Node node = lookup(o, index);
        Object rval = null;

        if (node != null) {
            rval = node.getData(oppositeIndex(index));
            doRedBlackDelete(node);
        }
        return rval;
    }


    private Object doGet(Comparable o, int index) {
        checkNonNullComparable(o, index);
        Node node = lookup(o, index);

        return ((node == null) ? null
                : node.getData(oppositeIndex(index)));
    }


    private int oppositeIndex(int index) {




        return _INDEX_SUM - index;
    }


    public Node lookup(Comparable data, int index) {
        Node rval = null;
        Node node = _root[index];

        while (node != null) {
            int cmp = compare(data, node.getData(index));

            if (cmp == 0) {
                rval = node;
                break;
            }
            node = (cmp < 0) ? node.getLeft(index)
                    : node.getRight(index);
        }
        return rval;
    }


    private void rotateLeft(Node node, int index) {
        Node right_child = node.getRight(index);

        node.setRight(right_child.getLeft(index), index);
        if (right_child.getLeft(index) != null) {
            right_child.getLeft(index).setParent(node, index);
        }
        right_child.setParent(node.getParent(index), index);
        if (node.getParent(index) == null) {


            _root[index] = right_child;
        } else if (node.getParent(index).getLeft(index) == node) {
            node.getParent(index).setLeft(right_child, index);
        } else {
            node.getParent(index).setRight(right_child, index);
        }
        right_child.setLeft(node, index);
        node.setParent(right_child, index);
    }


    private void rotateRight(Node node, int index) {
        Node left_child = node.getLeft(index);

        node.setLeft(left_child.getRight(index), index);
        if (left_child.getRight(index) != null) {
            left_child.getRight(index).setParent(node, index);
        }
        left_child.setParent(node.getParent(index), index);
        if (node.getParent(index) == null) {


            _root[index] = left_child;
        } else if (node.getParent(index).getRight(index) == node) {
            node.getParent(index).setRight(left_child, index);
        } else {
            node.getParent(index).setLeft(left_child, index);
        }
        left_child.setRight(node, index);
        node.setParent(left_child, index);
    }


    private void doRedBlackInsert(Node inserted_node, int index) {
        Node current_node = inserted_node;

        makeRed(current_node, index);
        while ((current_node != null) && (current_node != _root[index])
                && (isRed(current_node.getParent(index), index))) {
            if (isLeftChild(getParent(current_node, index), index)) {
                Node y = getRightChild(getGrandParent(current_node, index),
                        index);

                if (isRed(y, index)) {
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(current_node, index), index);
                    current_node = getGrandParent(current_node, index);
                } else {
                    if (isRightChild(current_node, index)) {
                        current_node = getParent(current_node, index);
                        rotateLeft(current_node, index);
                    }
                    makeBlack(getParent(current_node, index), index);
                    makeRed(getGrandParent(current_node, index), index);
                    if (getGrandParent(current_node, index) != null) {
                        rotateRight(getGrandParent(current_node, index),
                                index);
                    }
                }
            } else {


                Node y = getLeftChild(getGrandParent(current_node, index),
                        index);

                if (isRed(y, index)) {
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(current_node, index), index);
                    current_node = getGrandParent(current_node, index);
                } else {
                    if (isLeftChild(current_node, index)) {
                        current_node = getParent(current_node, index);
                        rotateRight(current_node, index);
                    }
                    makeBlack(getParent(current_node, index), index);
                    makeRed(getGrandParent(current_node, index), index);
                    if (getGrandParent(current_node, index) != null) {
                        rotateLeft(getGrandParent(current_node, index),
                                index);
                    }
                }
            }
        }
        makeBlack(_root[index], index);
    }


    void doRedBlackDelete(Node deleted_node) {
        for (int index = _MINIMUM_INDEX; index < _INDEX_COUNT; index++) {



            if ((deleted_node.getLeft(index) != null)
                    && (deleted_node.getRight(index) != null)) {
                swapPosition(nextGreater(deleted_node, index), deleted_node,
                        index);
            }
            Node replacement = ((deleted_node.getLeft(index) != null)
                    ? deleted_node.getLeft(index)
                    : deleted_node.getRight(index));

            if (replacement != null) {
                replacement.setParent(deleted_node.getParent(index), index);
                if (deleted_node.getParent(index) == null) {
                    _root[index] = replacement;
                } else if (deleted_node
                        == deleted_node.getParent(index).getLeft(index)) {
                    deleted_node.getParent(index).setLeft(replacement, index);
                } else {
                    deleted_node.getParent(index).setRight(replacement,
                            index);
                }
                deleted_node.setLeft(null, index);
                deleted_node.setRight(null, index);
                deleted_node.setParent(null, index);
                if (isBlack(deleted_node, index)) {
                    doRedBlackDeleteFixup(replacement, index);
                }
            } else {


                if (deleted_node.getParent(index) == null) {


                    _root[index] = null;
                } else {


                    if (isBlack(deleted_node, index)) {
                        doRedBlackDeleteFixup(deleted_node, index);
                    }
                    if (deleted_node.getParent(index) != null) {
                        if (deleted_node
                                == deleted_node.getParent(index)
                                .getLeft(index)) {
                            deleted_node.getParent(index).setLeft(null,
                                    index);
                        } else {
                            deleted_node.getParent(index).setRight(null,
                                    index);
                        }
                        deleted_node.setParent(null, index);
                    }
                }
            }
        }
        shrink();
    }


    private void doRedBlackDeleteFixup(Node replacement_node,
                                       int index) {
        Node current_node = replacement_node;

        while ((current_node != _root[index])
                && (isBlack(current_node, index))) {
            if (isLeftChild(current_node, index)) {
                Node sibling_node =
                        getRightChild(getParent(current_node, index), index);

                if (isRed(sibling_node, index)) {
                    makeBlack(sibling_node, index);
                    makeRed(getParent(current_node, index), index);
                    rotateLeft(getParent(current_node, index), index);
                    sibling_node =
                            getRightChild(getParent(current_node, index), index);
                }
                if (isBlack(getLeftChild(sibling_node, index), index)
                        && isBlack(getRightChild(sibling_node, index), index)) {
                    makeRed(sibling_node, index);
                    current_node = getParent(current_node, index);
                } else {
                    if (isBlack(getRightChild(sibling_node, index), index)) {
                        makeBlack(getLeftChild(sibling_node, index), index);
                        makeRed(sibling_node, index);
                        rotateRight(sibling_node, index);
                        sibling_node =
                                getRightChild(getParent(current_node, index),
                                        index);
                    }
                    copyColor(getParent(current_node, index), sibling_node,
                            index);
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(getRightChild(sibling_node, index), index);
                    rotateLeft(getParent(current_node, index), index);
                    current_node = _root[index];
                }
            } else {
                Node sibling_node =
                        getLeftChild(getParent(current_node, index), index);

                if (isRed(sibling_node, index)) {
                    makeBlack(sibling_node, index);
                    makeRed(getParent(current_node, index), index);
                    rotateRight(getParent(current_node, index), index);
                    sibling_node =
                            getLeftChild(getParent(current_node, index), index);
                }
                if (isBlack(getRightChild(sibling_node, index), index)
                        && isBlack(getLeftChild(sibling_node, index), index)) {
                    makeRed(sibling_node, index);
                    current_node = getParent(current_node, index);
                } else {
                    if (isBlack(getLeftChild(sibling_node, index), index)) {
                        makeBlack(getRightChild(sibling_node, index), index);
                        makeRed(sibling_node, index);
                        rotateLeft(sibling_node, index);
                        sibling_node =
                                getLeftChild(getParent(current_node, index),
                                        index);
                    }
                    copyColor(getParent(current_node, index), sibling_node,
                            index);
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(getLeftChild(sibling_node, index), index);
                    rotateRight(getParent(current_node, index), index);
                    current_node = _root[index];
                }
            }
        }
        makeBlack(current_node, index);
    }


    private void swapPosition(Node x, Node y, int index) {


        Node x_old_parent = x.getParent(index);
        Node x_old_left_child = x.getLeft(index);
        Node x_old_right_child = x.getRight(index);
        Node y_old_parent = y.getParent(index);
        Node y_old_left_child = y.getLeft(index);
        Node y_old_right_child = y.getRight(index);
        boolean x_was_left_child =
                (x.getParent(index) != null)
                        && (x == x.getParent(index).getLeft(index));
        boolean y_was_left_child =
                (y.getParent(index) != null)
                        && (y == y.getParent(index).getLeft(index));


        if (x == y_old_parent) {
            x.setParent(y, index);
            if (y_was_left_child) {
                y.setLeft(x, index);
                y.setRight(x_old_right_child, index);
            } else {
                y.setRight(x, index);
                y.setLeft(x_old_left_child, index);
            }
        } else {
            x.setParent(y_old_parent, index);
            if (y_old_parent != null) {
                if (y_was_left_child) {
                    y_old_parent.setLeft(x, index);
                } else {
                    y_old_parent.setRight(x, index);
                }
            }
            y.setLeft(x_old_left_child, index);
            y.setRight(x_old_right_child, index);
        }
        if (y == x_old_parent) {
            y.setParent(x, index);
            if (x_was_left_child) {
                x.setLeft(y, index);
                x.setRight(y_old_right_child, index);
            } else {
                x.setRight(y, index);
                x.setLeft(y_old_left_child, index);
            }
        } else {
            y.setParent(x_old_parent, index);
            if (x_old_parent != null) {
                if (x_was_left_child) {
                    x_old_parent.setLeft(y, index);
                } else {
                    x_old_parent.setRight(y, index);
                }
            }
            x.setLeft(y_old_left_child, index);
            x.setRight(y_old_right_child, index);
        }


        if (x.getLeft(index) != null) {
            x.getLeft(index).setParent(x, index);
        }
        if (x.getRight(index) != null) {
            x.getRight(index).setParent(x, index);
        }
        if (y.getLeft(index) != null) {
            y.getLeft(index).setParent(y, index);
        }
        if (y.getRight(index) != null) {
            y.getRight(index).setParent(y, index);
        }
        x.swapColors(y, index);


        if (_root[index] == x) {
            _root[index] = y;
        } else if (_root[index] == y) {
            _root[index] = x;
        }
    }


    private void modify() {
        _modifications++;
    }


    private void grow() {
        modify();
        _size++;
    }


    private void shrink() {
        modify();
        _size--;
    }


    private void insertValue(Node newNode)
            throws IllegalArgumentException {
        Node node = _root[_VALUE];

        while (true) {
            int cmp = compare(newNode.getData(_VALUE), node.getData(_VALUE));

            if (cmp == 0) {
                throw new IllegalArgumentException(
                        "Cannot store a duplicate value (\""
                                + newNode.getData(_VALUE) + "\") in this Map");
            } else if (cmp < 0) {
                if (node.getLeft(_VALUE) != null) {
                    node = node.getLeft(_VALUE);
                } else {
                    node.setLeft(newNode, _VALUE);
                    newNode.setParent(node, _VALUE);
                    doRedBlackInsert(newNode, _VALUE);
                    break;
                }
            } else {
                if (node.getRight(_VALUE) != null) {
                    node = node.getRight(_VALUE);
                } else {
                    node.setRight(newNode, _VALUE);
                    newNode.setParent(node, _VALUE);
                    doRedBlackInsert(newNode, _VALUE);
                    break;
                }
            }
        }
    }




    public int size() {
        return _size;
    }


    public boolean containsKey(Object key)
            throws ClassCastException, NullPointerException {
        checkKey(key);
        return lookup((Comparable) key, _KEY) != null;
    }


    public boolean containsValue(Object value) {
        checkValue(value);
        return lookup((Comparable) value, _VALUE) != null;
    }


    public Object get(Object key)
            throws ClassCastException, NullPointerException {
        return doGet((Comparable) key, _KEY);
    }


    public Object put(Object key, Object value)
            throws ClassCastException, NullPointerException,
            IllegalArgumentException {
        checkKeyAndValue(key, value);
        Node node = _root[_KEY];

        if (node == null) {
            Node root = new Node((Comparable) key, (Comparable) value);

            _root[_KEY] = root;
            _root[_VALUE] = root;
            grow();
        } else {
            while (true) {
                int cmp = compare((Comparable) key, node.getData(_KEY));

                if (cmp == 0) {
                    throw new IllegalArgumentException(
                            "Cannot store a duplicate key (\"" + key
                                    + "\") in this Map");
                } else if (cmp < 0) {
                    if (node.getLeft(_KEY) != null) {
                        node = node.getLeft(_KEY);
                    } else {
                        Node newNode = new Node((Comparable) key,
                                (Comparable) value);

                        insertValue(newNode);
                        node.setLeft(newNode, _KEY);
                        newNode.setParent(node, _KEY);
                        doRedBlackInsert(newNode, _KEY);
                        grow();
                        break;
                    }
                } else {
                    if (node.getRight(_KEY) != null) {
                        node = node.getRight(_KEY);
                    } else {
                        Node newNode = new Node((Comparable) key,
                                (Comparable) value);

                        insertValue(newNode);
                        node.setRight(newNode, _KEY);
                        newNode.setParent(node, _KEY);
                        doRedBlackInsert(newNode, _KEY);
                        grow();
                        break;
                    }
                }
            }
        }
        return null;
    }


    public Object remove(Object key) {
        return doRemove((Comparable) key, _KEY);
    }


    public void clear() {
        modify();
        _size = 0;
        _root[_KEY] = null;
        _root[_VALUE] = null;
    }


    public Set keySet() {
        if (_key_set[_KEY] == null) {
            _key_set[_KEY] = new AbstractSet() {
                public Iterator iterator() {
                    return new BinaryTreeIterator(_KEY) {
                        protected Object doGetNext() {
                            return _last_returned_node.getData(_KEY);
                        }
                    };
                }

                public int size() {
                    return BinaryTree.this.size();
                }

                public boolean contains(Object o) {
                    return containsKey(o);
                }

                public boolean remove(Object o) {
                    int old_size = _size;

                    BinaryTree.this.remove(o);
                    return _size != old_size;
                }

                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return _key_set[_KEY];
    }


    public Collection values() {
        if (_value_collection[_KEY] == null) {
            _value_collection[_KEY] = new AbstractCollection() {
                public Iterator iterator() {
                    return new BinaryTreeIterator(_KEY) {
                        protected Object doGetNext() {
                            return _last_returned_node.getData(_VALUE);
                        }
                    };
                }

                public int size() {
                    return BinaryTree.this.size();
                }

                public boolean contains(Object o) {
                    return containsValue(o);
                }

                public boolean remove(Object o) {
                    int old_size = _size;

                    removeValue(o);
                    return _size != old_size;
                }

                public boolean removeAll(Collection c) {
                    boolean modified = false;
                    Iterator iter = c.iterator();

                    while (iter.hasNext()) {
                        if (removeValue(iter.next()) != null) {
                            modified = true;
                        }
                    }
                    return modified;
                }

                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return _value_collection[_KEY];
    }


    public Set entrySet() {
        if (_entry_set[_KEY] == null) {
            _entry_set[_KEY] = new AbstractSet() {
                public Iterator iterator() {
                    return new BinaryTreeIterator(_KEY) {
                        protected Object doGetNext() {
                            return _last_returned_node;
                        }
                    };
                }

                public boolean contains(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry) o;
                    Object value = entry.getValue();
                    Node node = lookup((Comparable) entry.getKey(),
                            _KEY);

                    return (node != null)
                            && node.getData(_VALUE).equals(value);
                }

                public boolean remove(Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    Map.Entry entry = (Map.Entry) o;
                    Object value = entry.getValue();
                    Node node = lookup((Comparable) entry.getKey(),
                            _KEY);

                    if ((node != null) && node.getData(_VALUE).equals(value)) {
                        doRedBlackDelete(node);
                        return true;
                    }
                    return false;
                }

                public int size() {
                    return BinaryTree.this.size();
                }

                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return _entry_set[_KEY];
    }


    private static final class Node
            implements Map.Entry {
        private Comparable[] _data;
        private Node[] _left;
        private Node[] _right;
        private Node[] _parent;
        private boolean[] _black;
        private int _hashcode;
        private boolean _calculated_hashcode;



        Node(Comparable key, Comparable value) {
            _data = new Comparable[]
                    {
                            key, value
                    };
            _left = new Node[]
                    {
                            null, null
                    };
            _right = new Node[]
                    {
                            null, null
                    };
            _parent = new Node[]
                    {
                            null, null
                    };
            _black = new boolean[]
                    {
                            true, true
                    };
            _calculated_hashcode = false;
        }


        public Comparable getData(int index) {
            return _data[index];
        }


        public void setLeft(Node node, int index) {
            _left[index] = node;
        }



        public Node getLeft(int index) {
            return _left[index];
        }


        public void setRight(Node node, int index) {
            _right[index] = node;
        }



        public Node getRight(int index) {
            return _right[index];
        }


        public void setParent(Node node, int index) {
            _parent[index] = node;
        }


        public Node getParent(int index) {
            return _parent[index];
        }


        public void swapColors(Node node, int index) {


            _black[index] ^= node._black[index];
            node._black[index] ^= _black[index];
            _black[index] ^= node._black[index];
        }


        public boolean isBlack(int index) {
            return _black[index];
        }


        public boolean isRed(int index) {
            return !_black[index];
        }


        public void setBlack(int index) {
            _black[index] = true;
        }


        public void setRed(int index) {
            _black[index] = false;
        }


        public void copyColor(Node node, int index) {
            _black[index] = node._black[index];
        }




        public Object getKey() {
            return _data[_KEY];
        }


        public Object getValue() {
            return _data[_VALUE];
        }


        public Object setValue(Object ignored)
                throws UnsupportedOperationException {
            throw new UnsupportedOperationException(
                    "Map.Entry.setValue is not supported");
        }


        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry) o;

            return _data[_KEY].equals(e.getKey())
                    && _data[_VALUE].equals(e.getValue());
        }



        public int hashCode() {
            if (!_calculated_hashcode) {
                _hashcode = _data[_KEY].hashCode()
                        ^ _data[_VALUE].hashCode();
                _calculated_hashcode = true;
            }
            return _hashcode;
        }


    }


    private abstract class BinaryTreeIterator
            implements Iterator {
        protected Node _last_returned_node;
        private int _expected_modifications;
        private Node _next_node;
        private int _type;


        BinaryTreeIterator(int type) {
            _type = type;
            _expected_modifications = BinaryTree.this._modifications;
            _last_returned_node = null;
            _next_node = leastNode(_root[_type], _type);
        }



        protected abstract Object doGetNext();





        public boolean hasNext() {
            return _next_node != null;
        }



        public Object next()
                throws NoSuchElementException, ConcurrentModificationException {
            if (_next_node == null) {
                throw new NoSuchElementException();
            }
            if (_modifications != _expected_modifications) {
                throw new ConcurrentModificationException();
            }
            _last_returned_node = _next_node;
            _next_node = nextGreater(_next_node, _type);
            return doGetNext();
        }



        public void remove()
                throws IllegalStateException, ConcurrentModificationException {
            if (_last_returned_node == null) {
                throw new IllegalStateException();
            }
            if (_modifications != _expected_modifications) {
                throw new ConcurrentModificationException();
            }
            doRedBlackDelete(_last_returned_node);
            _expected_modifications++;
            _last_returned_node = null;
        }


    }
}
