package labs;

import mpi.MPI;

import java.util.Random;

public class Lab4 {
    public Lab4(String[] args) {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        // Генерация случайных векторов
        int[] vectorA = generateRandomVector(1000);
        int[] vectorB = generateRandomVector(1000);
        // Распределение вектора A
        int elementsPerProcess = vectorA.length / size;
        int[] localVectorA = new int[elementsPerProcess];
        MPI.COMM_WORLD.Scatter(vectorA, 0, elementsPerProcess, MPI.INT, localVectorA, 0, elementsPerProcess, MPI.INT, 0);

        // Передача вектора B по кольцу
        int[] receivedVectorB = new int[elementsPerProcess];
        int previousRank = (rank - 1 + size) % size;
        int nextRank = (rank + 1) % size;

        for (int i = 0; i < size - 1; i++) {
            MPI.COMM_WORLD.Sendrecv_replace(localVectorA, 0, elementsPerProcess, MPI.INT, nextRank, 0, previousRank, 0);
        }

        // Вычисление частичного результата для каждого процесса
        int partialResult = 0;
        for (int i = 0; i < elementsPerProcess; i++) {
            partialResult += localVectorA[i] * vectorB[i];
        }

        // Сбор частичных результатов со всех процессов
        int[] allPartialResults = new int[size];
        System.out.println("Rank " + rank + ": Partial Result: " + partialResult);
        MPI.COMM_WORLD.Gather(new int[]{partialResult}, 0, 1, MPI.INT, allPartialResults, 0, 1, MPI.INT, 0);

        // Вывод окончательного результата в корневом процессе
        if (rank == 0) {
            int scalarProduct = 0;
            for (int result : allPartialResults) {
                scalarProduct += result;
            }

            System.out.println("Скалярное произведение: " + scalarProduct);
        }

        MPI.Finalize();
    }

    // Вспомогательный метод для генерации случайного вектора
    private static int[] generateRandomVector(int length) {
        int[] vector = new int[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            vector[i] = random.nextInt(10); // Генерация случайного числа от 0 до 9
        }
        return vector;
    }
}
