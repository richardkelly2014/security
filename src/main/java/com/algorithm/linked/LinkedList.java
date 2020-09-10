package com.algorithm.linked;

/**
 * 单向链表
 *
 * @param <AnyType>
 */
public class LinkedList<AnyType> {


    private class Node {
        public AnyType e;
        public Node next;

        public Node(AnyType e, Node next) {
            this.e = e;
            this.next = next;
        }

        public Node(AnyType e) {
            this(e, null);
        }
    }


    private Node head;
    private int size;

    public LinkedList() {
        head = null;
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void addFirst(AnyType e) {
        head = new Node(e, head);

        size++;
    }

    public void addLast(AnyType e) {
        add(size, e);
    }

    public void add(int index, AnyType e) {
        if (index < 0 || index > size) {
            throw new IllegalArgumentException("Add failed. Illegal index.");
        }
        if (index == 0) {
            addFirst(e);
        } else {
            Node prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
            }

            prev.next = new Node(e, prev.next);
            size++;
        }
    }

}
