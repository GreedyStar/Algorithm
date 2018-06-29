package Jigsaw;

import java.util.List;
import java.util.Stack;

public class Node {
    private final static int goalMatrix[][] = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 0}}; // 目标状态（二维数组表示）
    private final static int goal[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0}; // 目标状态（一维数据表示）
    private final static int init[] = {11, 9, 4, 15, 1, 3, 0, 12, 7, 5, 8, 6, 13, 2, 10, 14}; // 初始状态
    private final static int initX = 1;
    private final static int initY = 2;
    // result: 41 steps

    // private final static int init[] = { 5, 1, 2, 4, 9, 6, 3, 8, 13, 15, 10,
    // 11, 14, 0, 7, 12 };
    // private final static int initX = 3;
    // private final static int initY = 1;
    // result: 14 steps

    // private final static int init[] = { 1, 2, 3, 4, 5, 10, 6, 8, 0, 9, 7, 12,
    // 13, 14, 11, 15 };
    // private final static int initX = 2;
    // private final static int initY = 0;
    // result: 6 steps

    // private final static int init[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
    // 12, 13, 14, 15, 0 };
    // private final static int initX = 3;
    // private final static int initY = 3;
    // result: 0 steps

    // private final static int init[] = { 2, 3, 4, 8, 1, 6, 7, 0, 5, 10, 11,
    // 12, 9, 13, 14, 15 };
    // private final static int initX = 1;
    // private final static int initY = 3;
    // result: 10 steps

    public int data[];
    public float cost;
    public int level;
    public int x, y;
    public int preDirection;
    public Node pre;

    public Node() {
        data = new int[16];
        cost = 0;
        level = 0;
        x = 0;
        y = 0;
        preDirection = -1;
        pre = null;
    }

    public Node(Node node) {
        data = new int[16];
        for (int i = 0; i < data.length; i++) {
            data[i] = node.data[i];
        }
        cost = 0;
        level = node.level;
        x = node.x;
        y = node.y;
        preDirection = node.preDirection;
    }

    public void initData() {
        for (int i = 0; i < data.length; i++) {
            data[i] = init[i];
        }
        x = initX;
        y = initY;
        cost = level + costDistance(data, goalMatrix);
    }

    public int costDistance(int[] a, int[][] b) {  // 计算二维数组元素之间的曼哈顿距离
        int c[][] = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                c[i][j] = a[4 * i + j];
            }
        }
        int cost = 0;
        boolean flag = false;
        for (int x1 = 0; x1 < 4; x1++) {
            for (int y1 = 0; y1 < 4; y1++) {
                flag = false;
                for (int x2 = 0; x2 < 4; x2++) {
                    for (int y2 = 0; y2 < 4; y2++) {
                        if (c[x1][y1] == b[x2][y2]) {
                            cost += (Math.abs(x1 - x2) + Math.abs(y1 - y2));
                            flag = true;
                        }
                        if (flag) {
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
            }
        }
        return cost;
    }

    public int hasSameData(List<Node> list) { // 空白块移动
        boolean isFind = false;
        float minCost = 1000000000;
        for (int j = 0; j < list.size(); j++) {
            if (isSame(data, list.get(j).data)) {
                isFind = true;
                if (list.get(j).cost < minCost) {
                    minCost = list.get(j).cost;
                }
            }
        }
        if (isFind) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).cost == minCost && isSame(data, list.get(i).data)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean isSame(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public Node change(int direction) { // 根据direction移动空白块
        Node node = new Node(this);
        int i = 0, j = 0;
        switch (direction) {
            case 0: // 上
                i = node.x - 1;
                j = node.y;
                node.preDirection = 1;
                break;
            case 1: // 下
                i = node.x + 1;
                j = node.y;
                node.preDirection = 0;
                break;
            case 2: // 左
                i = node.x;
                j = node.y - 1;
                node.preDirection = 3;
                break;
            case 3: // 右
                i = node.x;
                j = node.y + 1;
                node.preDirection = 2;
                break;
            default:
                break;
        }
        if (i >= 0 && i <= 3 && j >= 0 && j <= 3) { // 边界限制
            int temp = node.data[4 * node.x + node.y];
            node.data[4 * node.x + node.y] = node.data[4 * i + j];
            node.data[4 * i + j] = temp;
            node.x = i;
            node.y = j;
            node.level++;
            node.cost();
            node.pre = this;
        } else {
            return null;
        }
        return node;
    }

    public void cost() { // 计算评估函数
        cost = 0;
        cost += (level + costDistance(data, goalMatrix));
    }

    public boolean isFinish() {
        boolean result = true;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != goal[i]) {
                return false;
            }
        }
        return true;
    }

    public void show() {
        System.out.println("**************");
        for (int i = 0; i < data.length; i++) {
            System.out.printf("%-4d", data[i]);
            if ((i + 1) % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println("level:" + level);
        System.out.println("cost:" + cost);
        System.out.println("**************");
    }

    public void finish() {
        Stack<Node> stack = new Stack<>();
        int steps = 0;
        stack.push(this);
        while (pre != null) {
            stack.push(pre);
            pre = pre.pre;
        }
        steps = stack.size() - 1;
        while (!stack.empty()) {
            stack.pop().show();
        }
        System.out.println("finish in " + steps + " steps");
    }

}
