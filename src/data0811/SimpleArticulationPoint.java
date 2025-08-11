package data0811;

import java.util.*;

public class SimpleArticulationPoint {
    public static Set<Integer> findArticulationPoints(List<Integer>[] graph) {
        int n = graph.length;
        if (n <= 1) return new HashSet<>(); // 修正：單個節點沒有關節點

        Set<Integer> result = new HashSet<>();

        for (int v = 0; v < n; v++) {
            boolean[] visited = new boolean[n];
            visited[v] = true; // 移除節點 v

            // 找到一個未被移除的節點作為 DFS 起點
            int start = -1;
            for (int i = 0; i < n; i++) {
                if (i != v) {
                    start = i;
                    break;
                }
            }

            if (start == -1) continue; // 理論上不會發生

            dfs(graph, start, visited);

            // 檢查是否所有節點都被訪問（除了被移除的 v）
            boolean isArticulationPoint = false;
            for (int i = 0; i < n; i++) {
                if (i != v && !visited[i]) {
                    isArticulationPoint = true;
                    break;
                }
            }

            if (isArticulationPoint) {
                result.add(v);
            }
        }
        return result;
    }

    private static void dfs(List<Integer>[] graph, int u, boolean[] visited) {
        visited[u] = true;
        for (int v : graph[u]) {
            if (!visited[v]) {
                dfs(graph, v, visited);
            }
        }
    }
}