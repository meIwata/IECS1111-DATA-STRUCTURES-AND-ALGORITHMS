package data0811;

import java.util.*;

public class ArticulationPointTest {

    public static List<Integer>[] generateRandomConnectedGraph(int V, int E, Random rand) {
        if (V <= 0) throw new IllegalArgumentException("頂點數必須大於 0");
        if (E < V - 1) throw new IllegalArgumentException("邊數不足以形成連通圖");
        if (E > (long) V * (V - 1) / 2) throw new IllegalArgumentException("邊數超過完全圖的邊數");

        @SuppressWarnings("unchecked")
        List<Integer>[] graph = new List[V];
        for (int i = 0; i < V; i++) {
            graph[i] = new ArrayList<>();
        }

        // 創建生成樹確保連通性
        List<Integer> vertices = new ArrayList<>();
        for (int i = 0; i < V; i++) vertices.add(i);
        Collections.shuffle(vertices, rand);

        Set<String> existingEdges = new HashSet<>();
        int edgeCount = 0;

        // 添加生成樹的邊
        for (int i = 1; i < V; i++) {
            int u = vertices.get(i);
            int v = vertices.get(rand.nextInt(i)); // 連接到之前的任意頂點
            addEdge(graph, u, v, existingEdges);
            edgeCount++;
        }

        // 添加剩餘的隨機邊
        int maxAttempts = E * 10; // 避免無限循環
        int attempts = 0;

        while (edgeCount < E && attempts < maxAttempts) {
            int u = rand.nextInt(V);
            int v = rand.nextInt(V);

            if (u != v && addEdge(graph, u, v, existingEdges)) {
                edgeCount++;
            }
            attempts++;
        }

        return graph;
    }

    private static boolean addEdge(List<Integer>[] graph, int u, int v, Set<String> existingEdges) {
        String edge = Math.min(u, v) + "," + Math.max(u, v);
        if (!existingEdges.contains(edge)) {
            existingEdges.add(edge);
            graph[u].add(v);
            graph[v].add(u);
            return true;
        }
        return false;
    }

    // 驗證結果正確性的輔助方法
    private static boolean validateResults(Set<Integer> simple, Set<Integer> fast,
                                           List<Integer>[] graph) {
        if (!simple.equals(fast)) {
            System.err.println("結果不一致！");
            System.err.println("簡單版結果: " + simple);
            System.err.println("快速版結果: " + fast);
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int[] verticesList = {10, 50, 100, 200, 500, 1000, 2000, 5000};
        int[] edgeMultipliers = {2, 5, 10, 20, 50, 100};
        Random rand = new Random(42);

        System.out.println("關節點演算法性能測試");
        System.out.println("=".repeat(85));
        System.out.printf("%-8s %-8s %12s %12s %10s %-8s%n",
                "頂點數", "邊數", "簡單版(ms)", "快速版(ms)", "速度比", "正確性");
        System.out.println("-".repeat(85));

        Set<String> tested = new HashSet<>();

        for (int V : verticesList) {
            for (int multiplier : edgeMultipliers) {
                long maxEdges = (long) V * (V - 1) / 2;
                int E = (int) Math.min((long) V * multiplier, maxEdges);

                // 確保邊數足夠形成連通圖
                if (E < V - 1) continue;

                String key = V + "," + E;
                if (tested.contains(key)) continue;
                tested.add(key);

                try {
                    List<Integer>[] graph = generateRandomConnectedGraph(V, E, rand);

                    // 簡單版測試
                    long startTime = System.nanoTime();
                    Set<Integer> simpleResult = SimpleArticulationPoint.findArticulationPoints(graph);
                    long simpleTime = System.nanoTime() - startTime;

                    // 快速版測試
                    FastArticulationPoint fast = new FastArticulationPoint();
                    startTime = System.nanoTime();
                    Set<Integer> fastResult = fast.findArticulationPoints(graph);
                    long fastTime = System.nanoTime() - startTime;

                    // 驗證正確性
                    boolean isCorrect = validateResults(simpleResult, fastResult, graph);
                    double speedRatio = fastTime > 0 ? (double) simpleTime / fastTime : Double.POSITIVE_INFINITY;

                    System.out.printf("%-11d %-8d %15.3f %13.3f %12.2f %4s%n",
                            V, E,
                            simpleTime / 1_000_000.0,
                            fastTime / 1_000_000.0,
                            speedRatio,
                            isCorrect ? "✓" : "✗");

                    // 對於大圖，簡單版可能太慢，跳過
                    if (V > 1000 && simpleTime > 1_000_000_000L) { // 超過1秒
                        System.out.println("警告：簡單版運行時間過長，後續大圖僅測試快速版");
                        break;
                    }

                } catch (Exception e) {
                    System.err.printf("測試失敗 V=%d, E=%d: %s%n", V, E, e.getMessage());
                }
            }
        }
    }
}