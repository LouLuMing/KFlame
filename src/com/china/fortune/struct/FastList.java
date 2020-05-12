package com.china.fortune.struct;

import java.util.Arrays;

public class FastList<E> {
    private static final int DEFAULT_CAPACITY = 8;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE >> 1;

    protected int iSize;
    protected Object[] elementData;

    public FastList() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    public FastList(int initialCapacity) {
        if (initialCapacity > 0) {
            elementData = new Object[initialCapacity];
        } else {
            elementData = EMPTY_ELEMENTDATA;
        }
    }

    protected boolean grow(int minCapacity) {
        if (minCapacity > elementData.length) {
            if (minCapacity < MAX_ARRAY_SIZE) {
                int newCapacity = elementData.length;
                if (newCapacity == 0) {
                    newCapacity = DEFAULT_CAPACITY;
                } else {
                    newCapacity += (newCapacity >> 1);
                }
                while (newCapacity < minCapacity) {
                    newCapacity += (newCapacity >> 1);
                }

                if (newCapacity < MAX_ARRAY_SIZE) {
                    elementData = Arrays.copyOf(elementData, newCapacity);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public int indexOf(Object o) {
        if (o != null) {
            for (int i = 0; i < iSize; i++) {
                if (o.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        if (o != null) {
            for (int i = iSize -1; i >= 0; i--) {
                if (o.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public E get(int index) {
        if (index >= 0 && index < iSize) {
            return (E)elementData[index];
        } else {
            return null;
        }
    }

    public E getAndSetNull(int index) {
        if (index >= 0 && index < iSize) {
            E obj = (E)elementData[index];
            elementData[index] = null;
            return obj;
        } else {
            return null;
        }
    }

    public E set(int index, E element) {
        if (index >= 0 && index < iSize) {
            E oldValue = (E)elementData[index];
            elementData[index] = element;
            return oldValue;
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return iSize == 0;
    }

    public int size() {
        return iSize;
    }

    public void size(int i) {
        iSize = i;
    }

    public void add(int index, E element) {
        if (grow(iSize + 1)) {
            System.arraycopy(elementData, index, elementData, index + 1,
                    iSize - index);
            elementData[index] = element;
            iSize++;
        }
    }

    public void add(E e) {
        if (grow(iSize + 1)) {
            elementData[iSize] = e;
            iSize++;
        }
    }

    public E remove(E o) {
        int index = indexOf(o);
        if (index >= 0) {
            E oldValue = (E)elementData[index];
            int numMoved = iSize - index - 1;
            if (numMoved > 0) {
                System.arraycopy(elementData, index + 1, elementData, index,
                        numMoved);
            }
            elementData[--iSize] = null;
            return oldValue;
        } else {
            return null;
        }
    }

    public E remove(int index) {
        if (index >= 0 && index < iSize) {
            E oldValue = (E)elementData[index];
            int numMoved = iSize - index - 1;
            if (numMoved > 0) {
                System.arraycopy(elementData, index + 1, elementData, index,
                        numMoved);
            }
            elementData[--iSize] = null;
            return oldValue;
        } else {
            return null;
        }
    }

    public void clear() {
        for (int i = 0; i < iSize; i++) {
            elementData[i] = null;
        }
        iSize = 0;
    }

    public int countNull() {
        int iNull = 0;
        for (int i = 0; i < iSize; i++) {
            if (elementData[i] == null) {
                iNull++;
            }
        }
        return iNull;
    }
    public FastList<E> clone() {
        FastList<E> v = new FastList<E>(elementData.length);
        System.arraycopy(elementData, 0, v.elementData, 0, iSize);
        v.iSize = iSize;
        return v;
    }

    public void push(E item) {
        add(item);
    }

    public E pop() {
        if (iSize > 0) {
            return remove(iSize - 1);
        } else {
            return null;
        }
    }

    public E peek() {
        return (E)elementData[iSize - 1];
    }

    public boolean contains(E o) {
        int index = indexOf(o);
        return index >= 0;
    }
}
