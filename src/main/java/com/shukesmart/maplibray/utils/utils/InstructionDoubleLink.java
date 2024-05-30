package com.shukesmart.maplibray.utils.utils;

public class InstructionDoubleLink<K> {
    //定义成员变量
    int size = 0;       //存放元素数
    Node<K> first;      //头节点
    Node<K> last;       //尾节点

    //定义内部类Node<K>
    private static class Node<K> {
        K item;         //定义K类型item 用来存储
        Node<K> left;   //定义左节点
        Node<K> right;  //定义右节点

        //构造方法
        Node(Node<K> left, K data, Node<K> right) {
            this.item = data;
            this.left = left;
            this.right = right;
        }
    }

    //无参构造方法
    public InstructionDoubleLink() {
    }

    //查找、定义索引节点方法
    Node<K> node(int index) {
        if (index < (size >> 1)) {
            Node<K> x = first;
            for (int i = 0; i < index; i++) {
                x = x.right;
            }
            return x;
        } else {
            Node<K> x = last;
            for (int i = size - 1; i > index; i--) {
                x = x.left;
            }
            return x;
        }
    }

    //索引异常报错
    private void indexWrong(int index) {
        if (!isIndexOk(index)) {
            throw new RuntimeException("错误啦");
        } else
            return;
    }

    //定义个boolean类型方法  判断索引是否合法
    private boolean isIndexOk(int index) {
        return index >= 0 && index < size;  //索引大于0 并且索引永远比size小只有在空的时候相等都为0，判断了索引是否合法
    }

    //addFirst方法  在头部添加
    public void myAddFirst(K k) {
        final Node<K> f = first;        //存原来的头指针
        final Node<K> newNode = new Node<>(null, k, f);       //定义一个新节点（即要插入的节点）
        first = newNode;        //将新节点作为头指针
        if (f == null) {      //f为null就是空链表，此时插入一个又是头又是尾
            last = newNode;
        } else {
            f.left = newNode;     //新节点插在了原来的头节点前面，f.left就是原来头节点向左指向插入的新节点
        }
        size++;
    }


    //addLast方法  在尾部添加
    public void myAddLast(K k) {
        final Node<K> l = last;     //存原来的尾指针
        final Node<K> newNode = new Node<>(l, k, null);       //定义一个新节点（即要插入的节点）
        last = newNode;     //将新节点作为尾指针
        if (l == null) {      //l为null就是空链表，此时插入一个又是头又是尾
            first = newNode;
        } else {
            l.right = newNode;  //新节点插在了原来的尾节点后面，l.right就是原来尾节点向右指向插入的新节点
        }
        size++;
    }

    //add方法   指定位置添加
    public void myAdd(int index, K k) {
        indexWrong(index);      //判断索引合法性
        if (index == size) {      //判断是否为空链表
            myAddFirst(k);
        } else {
            add(k, node(index));
        }
    }

    //myAdd添加实现的主要部分
    void add(K k, Node<K> after) {
        final Node<K> before = after.left;      //before当作插入位置的前一个元素
        final Node<K> newNode = new Node<>(before, k, after);     //after当作插入位置的后一个元素
        after.left = newNode;
        if (before == null) {
            first = newNode;
        } else {
            before.right = newNode;     //前面有元素，则和插入的元素连接起来
        }
        size++;
    }


    //读值方法
    public K myGet(int index) {
        indexWrong(index);
        return node(index).item;
    }


    //设值方法
    public K mySet(int index, K k) {
        indexWrong(index);
        Node<K> x = node(index);
        K oldK = x.item;
        x.item = k;
        return oldK;
    }


    //删除头部
    public K myRemoveFirt() {
        final Node<K> f = first;    //f就是要删除的头节点
        if (f == null) {      //判断是否有头节点，没有头节点那么就是空链表
            throw new RuntimeException("链表为空，无法删除!");
        }
        return removeFirst(f);
    }

    //删除头部的主要部分
    private K removeFirst(Node<K> f) {
        final K data = f.item;      //存储删除的数据
        final Node<K> after = f.right;  //删除的头节点指向的后一个元素after
        f.item = null;      //将数据删除
        f.right = null;     //将删除的头节点与后面元素的连接断开,此时已删除，方便GC回收这个删除的节点
        first = after;      //后一个元素成为新的头节点
        if (after == null) {
            last = null;    //如果删除了头节点后，后面没有元素了，那么尾节点就为空,即链表中现在没有元素
        } else {     //删除后，后面还有元素
            after.left = null;      //将后面元素的前端指向null，即成为了新的头节点
        }
        size--;
        return data;    //返回删除的数据
    }


    //删除尾部
    public K myRemoveLast() {
        final Node<K> l = last;     //l就是要删除的尾节点
        if (l == null) {
            throw new RuntimeException("链表为空,无法删除!");
        }
        return removeLast(l);
    }

    //尾部删除的主要部分
    private K removeLast(Node<K> l) {
        final K data = l.item;      //存储删除的数据
        final Node<K> before = l.left;  //删除的尾节点前面的一个元素 before
        l.item = null;      //将数据删除
        l.left = null;      //将删除的尾节点与前面元素的连接断开，此时已删除，方便GC回收这个删除的节点
        last = before;      //前一个元素称为新的尾节点
        if (before == null) {
            first = null;   //如果删除了尾节点后，前面没有元素了，那么头节点就为空，即链表中现在没有元素
        } else {     //删除后，前面还有元素
            before.right = null;  //将前面元素的后端指向null，即成为了新的尾节点
        }
        size--;
        return data;
    }

    //指定位置删除
    public K myRemove(int index) {
        indexWrong(index);      //判断索引是否合法
        return remove(node(index));
    }

    //指定位置删除的主要部分
    private K remove(Node<K> index) {
        final K data = index.item;      //存储要删除位置的数据
        final Node<K> before = index.left;  //before为插入位置前一个元素
        final Node<K> after = index.right;  //after为插入位置后一个元素

        if (before == null) {
            first = after;      //如果前一个元素为null，即前面没有元素，那么删除的就是头节点，那么头节点删除后后面那个元素after就成为新的头节点
        } else {
            before.right = after;   //删除的元素的前后两个元素连接
            index.left = null;  //删除一个节点要把和前后两个元素的连接都断掉，这一步是断掉和前面元素的连接
        }

        if (after == null) {
            last = before;      //如果后一个元素为null，即后面没有元素，那么删除的就是尾节点，那么尾节点删除后前面那个元素before就成为新的尾节点
        } else {
            after.left = before;     //删除的元素的前后两个元素连接
            index.right = null;      //删除一个节点要把和前后两个元素的连接都断掉，这一步是断掉和后面元素的连接
        }
        index.item = null;      //删除数据
        size--;
        //此时已完全删除
        return data;        //返回删除的数据
    }

}