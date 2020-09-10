package com.algorithm.stack;

public interface Stack<AnyType> {
    // 获取栈大小
    int getSize();

    // 栈是否为空
    boolean isEmpty();

    // 向栈顶添加元素e
    void push(AnyType e);

    // 弹出栈顶元素
    AnyType pop();

    // 查看栈顶元素
    AnyType peek();
}
