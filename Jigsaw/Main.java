package Jigsaw;

import java.util.ArrayList;
import java.util.List;

/**
 * A*算法解决15数码问题
 */
public class Main {

    public static void main(String[] a) {
        Node node = new Node();
        node.initData();
        function(node, new ArrayList<>());
    }

    static void function(Node n, List<Node> openList) {
        openList.add(n);
        while (!openList.isEmpty()) {
            int index = minIndex(openList);
            Node node = openList.get(index);
            openList.remove(index);
            if (node.isFinish()) {
                node.finish();
                return;
            } else {
                addNode(node, openList);
            }
        }
    }

    /**
     * 将节点n的子节点添加到openList中
     * @param n
     * @param openList
     */
    public static void addNode(Node n, List<Node> openList) {
        int direction = 0;
        while (direction < 4) {
            Node node = n.change(direction);
            if (node != null && n.preDirection != direction) {
                int indexOpen = node.hasSameData(openList);
                if (indexOpen >= 0) {
                    if (node.cost < openList.get(indexOpen).cost) {
                        openList.get(indexOpen).cost = node.cost;
                        openList.get(indexOpen).pre = node.pre;
                    }
                } else {
                    openList.add(node);
                }
            }
            direction++;
        }
    }

    /**
     * 寻找最小代价节点索引
     * @param list
     * @return
     */
    public static int minIndex(List<Node> list) {
        int index = 0;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).cost < list.get(index).cost) {
                index = i;
            }
        }
        return index;
    }

}
