package labs;

import mpi.MPI;

public class Lab5 {
    public Lab5(String[] args) {
        MPI.Init(args);

        long startTime = System.currentTimeMillis();

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Пример графа в виде матрицы смежности
        int[][] graph = {
                {0, 1, 3, Integer.MAX_VALUE},
                {1, 0, 2, 5},
                {3, 2, 0, 4},
                {Integer.MAX_VALUE, 5, 4, 0}
        };

        // Вычисление кратчайших путей с использованием алгоритма Флойда-Уоршелла
        for (int k = 0; k < graph.length; k++) {
            for (int i = 0; i < graph.length; i++) {
                for (int j = 0; j < graph.length; j++) {
                    if (graph[i][k] != Integer.MAX_VALUE && graph[k][j] != Integer.MAX_VALUE) {
                        graph[i][j] = Math.min(graph[i][j], graph[i][k] + graph[k][j]);
                    }
                }
            }
        }

        // Находим вершину с минимальной суммой расстояний
        int minSum = Integer.MAX_VALUE;
        int center = -1;

        for (int i = 0; i < graph.length; i++) {
            int sum = 0;
            for (int j = 0; j < graph.length; j++) {
                sum += graph[i][j];
            }

            if (sum < minSum) {
                minSum = sum;
                center = i;
            }
        }
        System.out.println("Process " + rank + ": The center of the graph is the vertex " + center);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Process " + rank + ": lead time - " + elapsedTime + " milliseconds");

        MPI.Finalize();
    }
}
