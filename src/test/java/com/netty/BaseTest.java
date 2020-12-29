package com.netty;

import com.algorithm.tree.BST;
import com.algorithm.tree.BinarySearchTree;
import com.utils.SystemPropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class BaseTest {

    @Test
    public void test1() {
        boolean flag = SystemPropertyUtil.getBoolean("java.util.secureRandomSeed", false);
        log.info("{}", flag);
    }

    @Test
    public void test2() {
        String a = "a";
        String b = "b";
        log.info("{}", a.compareTo(b));
    }

    @Test
    public void test3() {
        BST<Integer> bst = new BST<>();
        bst.add(19);

        bst.add(20);

        bst.add(18);
    }

    @Test
    public void test4() {
        BinarySearchTree<Integer> binarySearchTree = new BinarySearchTree<>();

        binarySearchTree.insert(19);

        binarySearchTree.insert(20);

        binarySearchTree.insert(18);
    }
}
