package Tsp;

import java.io.IOException;
import java.util.Random;

/**
 * 遗传算法求解TSP问题
 */
public class GA {
    private int[][] pos = {{6734, 1453}, {2233, 10}, {5530, 1424}, {401, 841}, {3082, 1644}, {7608, 4458}, {7573, 3716}, {7265, 1268}, {6898, 1885}, {1112, 2049}, {5468, 2606},
            {5989, 2873}, {4706, 2674}, {4612, 2035}, {6347, 2683}, {6107, 669}, {7611, 5184}, {7462, 3590}, {7732, 4723}, {5900, 3561}, {4483, 3369}, {6101, 1110}, {5199, 2182},
            {1633, 2809}, {4307, 2322}, {675, 1006}, {7555, 4819}, {7541, 3981}, {3177, 756}, {7352, 4506}};
    private final static int M = 10;// 种群规模
    private final static int cityNum = 30; // 城市数量，染色体长度
    private int T; // 运行代数
    private int[][] distance; // 距离矩阵
    private int bestDistance; // 最佳长度
    private int[] bestPath; // 最佳路径
    private int[][] oldPopulation; // 父代种群
    private int[][] newPopulation; // 子代种群
    private int[] fitness; // 个体的适应度
    private float[] Pi; // 个体的累积概率
    private float pCorss; // 交叉概率
    private float pMutate; // 变异概率
    private int t;// 当前代数
    private Random random;


    public GA(int t, float corss, float mutate) {
        T = t;
        pCorss = corss;
        pMutate = mutate;
        distance = new int[cityNum][cityNum];
        for (int i = 0; i < cityNum - 1; i++) { // 计算距离矩阵, 距离计算方法为伪欧氏距离
            distance[i][i] = 0; // 对角线为0
            for (int j = i + 1; j < cityNum; j++) {
                double rij = Math.sqrt(((pos[i][0] - pos[j][0]) * (pos[i][0] - pos[j][0]) + (pos[i][1] - pos[j][1]) * (pos[i][1] - pos[j][1])));
                distance[i][j] = (int) Math.round(rij); // 四舍五入
                distance[j][i] = distance[i][j];
            }
        }
        distance[cityNum - 1][cityNum - 1] = 0;
        bestDistance = Integer.MAX_VALUE;
        bestPath = new int[cityNum + 1];
        newPopulation = new int[M][cityNum];
        oldPopulation = new int[M][cityNum];
        fitness = new int[M];
        Pi = new float[M];
        random = new Random(System.currentTimeMillis());
    }

    /**
     * 初始化种群
     */
    void initGroup() {
        int i, j, k;
        for (k = 0; k < M; k++) {   // 种群数
            oldPopulation[k][0] = random.nextInt(cityNum);
            for (i = 1; i < cityNum; ) {    // 染色体长度
                oldPopulation[k][i] = random.nextInt(cityNum);
                for (j = 0; j < i; j++) {
                    if (oldPopulation[k][i] == oldPopulation[k][j]) {
                        break;
                    }
                }
                if (j == i) {
                    i++;
                }
            }
        }
    }

    /**
     * 计算某个染色体的实际距离作为染色体适应度
     */
    public int evaluate(int[] chromosome) {
        int len = 0;
        for (int i = 1; i < cityNum; i++) {
            len += distance[chromosome[i - 1]][chromosome[i]];
        }
        len += distance[chromosome[cityNum - 1]][chromosome[0]]; // 回到起点
        return len;
    }

    /**
     * 计算种群中每个个体的累积概率
     */
    void countRate() {
        int k;
        double sumFitness = 0;// 适应度总和
        for (k = 0; k < M; k++) {
            sumFitness += fitness[k];
        }
        Pi[0] = (float) (fitness[0] / sumFitness);
        for (k = 1; k < M; k++) {
            Pi[k] = (float) (fitness[k] / sumFitness + Pi[k - 1]);
        }
    }

    /**
     * 挑选适应度最高的个体
     */
    public void selectBestChild() {
        int k, i, maxid;
        int maxevaluation;
        maxid = 0;
        maxevaluation = fitness[0];
        for (k = 1; k < M; k++) {
            if (maxevaluation > fitness[k]) {
                maxevaluation = fitness[k];
                maxid = k;
            }
        }
        if (bestDistance > maxevaluation) {
            bestDistance = maxevaluation;
            for (i = 0; i < cityNum; i++) {
                bestPath[i] = oldPopulation[maxid][i];
            }
        }
        copyGh(0, maxid);   // 将当代种群中适应度最高的染色体k复制到新种群中的第一位
    }

    /**
     * 轮盘赌挑选子代个体
     */
    public void selectChild() {
        int k, i, selectId;
        float ran1; // 挑选概率
        for (k = 1; k < M; k++) {
            ran1 = random.nextFloat();
            for (i = 0; i < M; i++) {
                if (ran1 <= Pi[i]) {
                    break;
                }
            }
            selectId = i;
            copyGh(k, selectId);
        }
    }

    /**
     * 复制染色体
     */
    public void copyGh(int k, int kk) {
        int i;
        for (i = 0; i < cityNum; i++) {
            newPopulation[k][i] = oldPopulation[kk][i];
        }
    }

    /**
     * 种群进化
     */
    public void evolution() {
        int k;
        selectBestChild();
        selectChild();
        float r;
        for (k = 0; k < M; k = k + 2) {
            r = random.nextFloat(); // 交叉概率
            if (r < pCorss) { // 交叉
                orderCrossover(k, k + 1);
            } else {
                r = random.nextFloat(); // 变异概率
                if (r < pMutate) {
                    variation(k);
                }
                r = random.nextFloat(); // 变异概率
                if (r < pMutate) {
                    variation(k + 1);
                }
            }
        }
    }

    public boolean hasElement(int[] a, int b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == b) {
                return true;
            }
        }
        return false;
    }

    /**
     * 顺序交叉
     */
    public void orderCrossover(int k1, int k2) {
        int[] child1 = new int[cityNum];
        int[] child2 = new int[cityNum];
        int ran1 = random.nextInt(cityNum);
        int ran2 = random.nextInt(cityNum);
        while (ran1 == ran2) {
            ran2 = random.nextInt(cityNum);
        }
        if (ran1 > ran2) {
            int temp = ran1;
            ran1 = ran2;
            ran2 = temp;
        }
        for (int i = ran1; i <= ran2; i++) {  // 生成子代交叉部分
            child1[i] = newPopulation[k1][i];
            child2[i] = newPopulation[k2][i];
        }
        for (int i = 0; i < cityNum; i++) {
            if (i >= ran1 && i <= ran2) {
                continue;
            }
            for (int j = 0; j < cityNum; j++) {
                if (!hasElement(child1, newPopulation[k2][j])) {
                    child1[i] = newPopulation[k2][j];
                    break;
                }
            }
        }
        for (int i = 0; i < cityNum; i++) {
            if (i >= ran1 && i <= ran2) {
                continue;
            }
            for (int j = 0; j < cityNum; j++) {
                if (!hasElement(child2, newPopulation[k1][j])) {
                    child2[i] = newPopulation[k1][j];
                    break;
                }
            }
        }
    }


    /**
     * 随机多次变异
     */
    public void variation(int k) {
        int ran1, ran2, temp;
        int count;
        count = random.nextInt(cityNum); // 变异次数
        for (int i = 0; i < count; i++) {
            ran1 = random.nextInt(cityNum);
            ran2 = random.nextInt(cityNum);
            while (ran1 == ran2) {
                ran2 = random.nextInt(cityNum);
            }
            temp = newPopulation[k][ran1];
            newPopulation[k][ran1] = newPopulation[k][ran2];
            newPopulation[k][ran2] = temp;
        }
    }

    public void run() {
        int i;
        int k;
        // 初始化种群
        initGroup();
        // 计算初始化种群适应度，Fitness[max]
        for (k = 0; k < M; k++) {
            fitness[k] = evaluate(oldPopulation[k]);
        }
        // 计算初始化种群中各个个体的累积概率，Pi[max]
        countRate();
        System.out.println("初始种群...");
        for (k = 0; k < M; k++) {
            for (i = 0; i < cityNum; i++) {
                System.out.printf("%-4d", oldPopulation[k][i]);
            }
            System.out.println();
        }
        for (t = 0; t < T; t++) {
            evolution();
            // 将新种群newGroup复制到旧种群oldGroup中，准备下一代进化
            for (k = 0; k < M; k++) {
                for (i = 0; i < cityNum; i++) {
                    oldPopulation[k][i] = newPopulation[k][i];
                }
            }
            // 计算种群适应度
            for (k = 0; k < M; k++) {
                fitness[k] = evaluate(oldPopulation[k]);
            }
            // 计算种群中各个个体的累积概率
            countRate();
        }
        System.out.println("最后种群...");
        for (k = 0; k < M; k++) {
            for (i = 0; i < cityNum; i++) {
                System.out.printf("%-4d", oldPopulation[k][i]);
            }
            System.out.println();
        }
        System.out.println("最小距离：" + bestDistance);
        System.out.print("最优路径：");
        for (i = 0; i < cityNum; i++) {
            System.out.printf("%-4d", bestPath[i]);
        }
    }


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        GA ga = new GA(10, 0.8f, 0.9f);
        ga.run();
    }

}
