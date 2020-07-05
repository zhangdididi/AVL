import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 实现 纯 key 模型的 AVL 树
// 如果要实现 key-value 模型，只需要在结点中，多保存一个 value 即可
public class AVLTree {
    /**
     * 记录树的根结点，如果是空树，则 root == null
     */
    private Node root = null;

    /**
     * 插入 AVL 树
     * @param key 要插入的关键字
     * @throws RuntimeException 如果 key 重复了
     */
    public void insert(int key) {
        if (root == null) {
            // 空树的插入，单独处理即可
            root = new Node(key, null);
            return;
        }

        // 走到这里，肯定不是空树
        Node parent = null;
        Node cur = root;

        while (cur != null) {
            if (key == cur.key) {
                throw new RuntimeException("key(" + key + ") 已经重复了");
            } else if (key < cur.key) {
                parent = cur;
                cur = cur.left;
            } else {
                parent = cur;
                cur = cur.right;
            }
        }

        // 直到找到 null 的位置，才真正开始插入
        if (key < parent.key) {
            cur = parent.left = new Node(key, parent);
        } else {
            // 这里只能是 key > parent.key，因为如果相等
            // 刚才循环中就抛异常了
            cur = parent.right = new Node(key, parent);
        }

        // 上面的过程是，正常搜索树的插入过程
        // parent 是要调整 BF 的结点
        // cur 是破坏源所在的根结点
        while (true) {
            // 根据情况，更新平衡因子
            if (cur == parent.left) {
                parent.bf++;
            } else {
                // cur == parent.right
                parent.bf--;
            }

            // 分情况处理
            if (parent.bf == 0) {
                break;
            } else if (parent.bf == 2) {
                // 进行失衡的修复
                // 左左失衡 OR 左右失衡
                if (cur.bf == 1) {
                    // 左左失衡
                    fixLeftLeftLoseBalance(parent);
                } else {
                    // -1
                    // 左右失衡
                    fixLeftRightLoseBalance(parent);
                }
                break;
            } else if (parent.bf == -2) {
                // 进行失衡的修复
                // 右右失衡 OR 右左失衡
                if (cur.bf == -1) {
                    // 右右失衡
                    fixRightRightLoseBalance(parent);
                } else {
                    // 1
                    // 右左失衡
                    fixRightLeftLoseBalance(parent);
                }
                break;
            } else if (parent == root){
                // -1/1 已经到根的位置了
                break;
            }

            // 如果需要继续
            Node parentOfParent = parent.parent;
            cur = parent;
            parent = parentOfParent;
        }
    }

    private void leftRotate(Node parent) {
        // 如果前面实现都正确，并且已经走到这个位置时，说明 parent 一定不是 null
        // 并且 parent.right 也一定不是 null
        Node parentOfParent = parent.parent;
        Node right = parent.right;
        Node leftOfRight = right.left;
        // parentOfParent 和 leftOfRight 可能是 null

        right.parent = parentOfParent;
        // 需要明确越来 parent 是 parentOfParent 的左还是右
        if (parentOfParent == null) {
            // 原来的根是 parent
            // 现在的根是 right
            root = right;
        } else if (parent == parentOfParent.left) {
            parentOfParent.left = right;
        } else {
            parentOfParent.right = right;
        }

        right.left = parent;
        parent.parent = right;

        parent.right = leftOfRight;
        if (leftOfRight != null) {
            leftOfRight.parent = parent;
        }
    }

    private void rightRotate(Node parent) {
        Node parentOfParent = parent.parent;
        Node left = parent.left;
        Node rightOfLeft = left.right;

        left.parent = parentOfParent;
        if (parentOfParent == null) {
            root = left;
        } else if (parent == parentOfParent.left) {
            parentOfParent.left = left;
        } else {
            parentOfParent.right = left;
        }

        left.right = parent;
        parent.parent = left;

        parent.left = rightOfLeft;
        if (rightOfLeft != null) {
            rightOfLeft.parent = parent;
        }
    }

    private void fixRightLeftLoseBalance(Node parent) {
        Node rightOfNode = parent.right;
        Node leftOfRightOfNode = rightOfNode.left;

        rightRotate(rightOfNode);
        leftRotate(parent);

        if (leftOfRightOfNode.bf == -1) {
            parent.bf = 1;
            rightOfNode.bf = leftOfRightOfNode.bf = 0;
        } else if (leftOfRightOfNode.bf == 1) {
            rightOfNode.bf = -1;
            parent.bf = leftOfRightOfNode.bf = 0;
        } else {
            parent.bf = rightOfNode.bf = leftOfRightOfNode.bf = 0;
        }
    }

    private void fixRightRightLoseBalance(Node parent) {
        Node rightOfNode = parent.right;

        leftRotate(parent);

        parent.bf = rightOfNode.bf = 0;
    }

    private void fixLeftRightLoseBalance(Node parent) {
        // 没必要进行 null 比较，已经走到这里来，如果外面的方法实现没问题，肯定不是出现 null
        Node leftOfNode = parent.left;
        Node rightOfLeftOfNode = leftOfNode.right;

        leftRotate(leftOfNode);
        rightRotate(parent);

        // 根据之前的计算结果，填写 BF 即可
        if (rightOfLeftOfNode.bf == 1) {
            parent.bf = -1;
            leftOfNode.bf = 0;
            rightOfLeftOfNode.bf = 0;
        } else if (rightOfLeftOfNode.bf == -1) {
            parent.bf = 0;
            leftOfNode.bf = 1;
            rightOfLeftOfNode.bf = 0;
        } else {
            parent.bf = leftOfNode.bf = rightOfLeftOfNode.bf = 0;
        }
    }

    private void fixLeftLeftLoseBalance(Node parent) {
        Node leftOfNode = parent.left;

        // 左左失衡，对失衡结点右旋
        rightRotate(parent);

        // 根据计算的结果，更新 BF
        parent.bf = leftOfNode.bf = 0;
    }

    /**
     * 在 AVL 树中查找对应的 key
     * @param key 要查找的关键字
     * @return AVL 是否包含这个 key
     */
    public boolean contains(int key) {
        Node cur = root;

        while (cur != null) {
            if (key == cur.key) {
                return true;
            } else if (key < cur.key) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }

        return false;
    }

    public void verify() {
        List<Integer> inOrderList = new ArrayList<>();
        // 计算该数的中序遍历
        inOrder(inOrderList, root);
        // 如何判断其是否有序呢？  把得到中序序列排序，如果排序后的结果和原来的结果一样，说明原来就有序
        List<Integer> inOrderListCopy = new ArrayList<>(inOrderList);
        Collections.sort(inOrderListCopy);

        if (!inOrderListCopy.equals(inOrderList)) {
            throw new RuntimeException("AVL 树规则不满足：中序遍历无序");
        }
        System.out.println("中序有序：验证OK");

        // 验证每个结点的 BF 是否计算正确
        preOrderAndCalcBF(root);
        System.out.println("BF 计算正确性: 验证OK");

        // 验证每个结点的 BF 是否都是 (-1, 0, 1)
        preOrderAndVerifyBF(root);
        System.out.println("BF 满足AVL特性: 验证OK");
    }

    private static void preOrderAndVerifyBF(Node node) {
        if (node != null) {
            if (node.bf != -1 && node.bf != 0 && node.bf != 1) {
                throw new RuntimeException("结点(" + node.key + ")的 BF 是 " + node.bf);
            }

            preOrderAndVerifyBF(node.left);
            preOrderAndVerifyBF(node.right);
        }
    }

    private static int height(Node node) {
        if (node == null) {
            return 0;
        }

        int left = height(node.left);
        int right = height(node.right);
        return Math.max(left, right) + 1;
    }

    private static void preOrderAndCalcBF(Node node) {
        if (node != null) {
            int left = height(node.left);
            int right = height(node.right);
            if (left - right != node.bf) {
                throw new RuntimeException("结点(" + node.key + ")的 BF 计算有错误");
            }

            preOrderAndCalcBF(node.left);
            preOrderAndCalcBF(node.right);
        }
    }

    private static void inOrder(List<Integer> list, Node node) {
        if (node != null) {
            inOrder(list, node.left);
            // 处理 node
            list.add(node.key);
            inOrder(list, node.right);
        }
    }
}
