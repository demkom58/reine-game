package com.crown.graphic.util;

public interface ListHandler<T> {
    /**
     * Invoked when object is added to list.
     *
     * @param index   Index of new object in list.
     * @param element Object added to list.
     */
    void added(int index, T element);

    /**
     * Invoked when element is removed from list.
     *
     * @param index   Index of removed element.
     * @param element Object removed from list.
     */
    void removed(int index, T element);
}
