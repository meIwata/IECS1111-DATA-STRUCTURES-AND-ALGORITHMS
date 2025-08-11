package data0811;

import java.util.*;

public class FastArticulationPoint {
    private int time;

    public Set<Integer> findArticulationPoints(List<Integer>[] graph) {
        int n = graph.length;
        if (n <= 1) return new HashSet<>();

        time = 0;
        int[] disc = new int[n];      // 發現時間
        int[] low = new int[n];       // 低連接值
        int[] parent = new int[n];    // DFS 樹中的父節點
        boolean[] ap = new boolean[n]; // 關節點標記
        boolean[] visited = new boolean[n];

        Arrays.fill(parent, -1);

        // 對所有未訪問的節點執行 DFS（處理斷開的圖）
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(graph, i, visited, disc, low, parent, ap);
            }
        }

        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (ap[i]) {
                result.add(i);
            }
        }
        return result;
    }

    private void dfs(List<Integer>[] graph, int u, boolean[] visited,
                     int[] disc, int[] low, int[] parent, boolean[] ap) {
        visited[u] = true;
        disc[u] = low[u] = time++;  // 修正：應該是 time++ 而不是 ++time
        int children = 0;

        for (int v : graph[u]) {
            if (!visited[v]) {
                children++;
                parent[v] = u;
                dfs(graph, v, visited, disc, low, parent, ap);

                // 更新 low 值
                low[u] = Math.min(low[u], low[v]);

                // 根節點：如果有多於一個子樹，則為關節點
                if (parent[u] == -1 && children > 1) {
                    ap[u] = true;
                }

                // 非根節點：如果 low[v] >= disc[u]，則 u 為關節點
                if (parent[u] != -1 && low[v] >= disc[u]) {
                    ap[u] = true;
                }
            } else if (v != parent[u]) {
                // 回邊：更新 low 值
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }
}
