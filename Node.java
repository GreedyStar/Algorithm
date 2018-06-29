package Gomoku;

/**
 * Author ZhangHaoxiang
 * Date   2017/11/4
 */
public class Node {
    public int p; // 落子方（玩家or AI）
    public int x; // 落子坐标x
    public int y; // 落子坐标y

    public Node() {

    }

    public Node(int p, int x, int y) {
        this.p = p;
        this.x = x;
        this.y = y;
    }

    public Node(Node node) {
        this.p = node.p == Main.AI ? Main.PLAYER : Main.AI;
        this.x = node.x;
        this.y = node.y;
    }

}
