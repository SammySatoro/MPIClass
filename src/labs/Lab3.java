package labs;

import mpi.*;

import java.util.Arrays;
import java.util.Random;

public class Lab3 {
    public Lab3(String[] args) {
        // Инициализируем MPI с помощью MPI.Init(args).
        MPI.Init(args);

        // Получаем номер текущего процесса (myrank) и общее количество процессов (size).
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Инициализируем переменные s, buf, TAG, centre, и объект Random для генерации случайных чисел.
        int s = myrank;
        int buf[] = new int[1];
        int TAG = 0;
        int centre = size / 2;
        Random random = new Random();

        // Инициализируем переменные sendRequest и recvRequest для запросов на отправку и прием данных.
        Request sendRequest = null;
        Request recvRequest = null;

        // Проверяем, является ли текущий процесс процессом с номером 0.
        if (myrank == 0) {
            // Инициализируем массивы buf1 и buf2 для получения данных от других процессов.
            int buf1[] = new int[centre - 1];
            int buf2[] = new int[size - 2 - centre];

            // Инициируем запрос на асинхронный прием данных от процесса с номером centre.
            recvRequest = MPI.COMM_WORLD.Irecv(buf1, 0, centre - 1, MPI.INT, centre, 1);
            // Дожидаемся завершения приема.
            recvRequest.Wait();
            System.out.println("Ok 1");

            // Инициируем запрос на асинхронный прием данных от процесса с номером size - 1.
            recvRequest = MPI.COMM_WORLD.Irecv(buf2, 0, size - 2 - centre, MPI.INT, size - 1, 2);
            // Дожидаемся завершения приема.
            recvRequest.Wait();
            System.out.println("Ok 2");

            // Инициализируем массив buf3 для объединения данных из buf1 и buf2.
            int buf3[] = new int[size - 3];

            // Копируем данные из buf1 и buf2 в buf3.
            for (int i = 0; i < buf1.length; i++) {
                buf3[i] = buf1[i];
            }
            for (int i = 0; i < buf2.length; i++) {
                buf3[buf1.length + i] = buf2[i];
            }

            // Вызываем функцию сортировки слиянием.
            mergeSort(buf3);

            // Создаем строку, содержащую отсортированные данные из buf3, и выводим её.
            String str = "";
            for (int i : buf3) {
                str += i + ", ";
            }
            System.out.println("result:" + str);
        } else if (myrank < centre) {
            // Генерируем случайное число в заданном диапазоне и отправляем его процессу с номером centre.
            int randomInRange = random.nextInt(100) + 1;
            System.out.println("proc #" + myrank + " - " + randomInRange);
            sendRequest = MPI.COMM_WORLD.Isend(new int[] { randomInRange }, 0, 1, MPI.INT, centre, TAG);
        } else if (myrank > centre && myrank < size - 1) {
            // Генерируем случайное число в заданном диапазоне и отправляем его процессу с номером size - 1.
            int randomInRange = random.nextInt(100) + 1;
            System.out.println("proc #" + myrank + " - " + randomInRange);
            sendRequest = MPI.COMM_WORLD.Isend(new int[] { randomInRange }, 0, 1, MPI.INT, size - 1, TAG);
        } else if (myrank == centre) {
            // Инициализируем массив new_buf для приема данных от процессов до центрального значения.
            int new_buf[] = new int[centre - 1];
            int parcels_received = 0;
            while (parcels_received != (centre - 1)) {
                // Инициируем запрос на асинхронный прием данных от любого процесса (MPI.ANY_SOURCE).
                recvRequest = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, MPI.ANY_SOURCE, TAG);
                // Дожидаемся завершения приема.
                recvRequest.Wait();
                new_buf[parcels_received] = buf[0];
                parcels_received += 1;
            }
            // Вызываем функцию сортировки слиянием для данных из new_buf.
            mergeSort(new_buf);
            // Отправляем отсортированные данные процессу с номером 0.
            sendRequest = MPI.COMM_WORLD.Isend(new_buf, 0, parcels_received, MPI.INT, 0, 1);
        } else {
            // Инициализируем массив new_buf для приема данных от процессов после центрального значения.
            int new_buf[] = new int[size - centre - 2];
            int parcels_received = 0;
            while (parcels_received != (size - centre - 2)) {
                // Инициируем запрос на асинхронный прием данных от любого процесса (MPI.ANY_SOURCE).
                recvRequest = MPI.COMM_WORLD.Irecv(buf, 0, 1, MPI.INT, MPI.ANY_SOURCE, TAG);
                // Дожидаемся завершения приема.
                recvRequest.Wait();
                new_buf[parcels_received] = buf[0];
                parcels_received += 1;
            }
            // Вызываем функцию сортировки слиянием для данных из new_buf.
            mergeSort(new_buf);
            // Отправляем отсортированные данные процессу с номером 0.
            sendRequest = MPI.COMM_WORLD.Isend(new_buf, 0, parcels_received, MPI.INT, 0, 2);
            System.out.println("sent the package:" + myrank + " - " + Arrays.toString(new_buf));
        }

        // Завершаем работу MPI с помощью MPI.Finalize().
        MPI.Finalize();
    }

    // Определяем функцию mergeSort для сортировки массива с помощью сортировки слиянием.
    public static void mergeSort(int[] arr) {
        // Проверяем базовый случай: если массив состоит из одного элемента или менее, не выполняем сортировку.
        if (arr.length <= 1) {
            return;
        }

        // Находим середину массива.
        int middle = arr.length / 2;

        // Создаем два подмассива, разделяя исходный массив пополам.
        int[] left = Arrays.copyOfRange(arr, 0, middle);
        int[] right = Arrays.copyOfRange(arr, middle, arr.length);

        // Рекурсивно сортируем левый и правый подмассивы.
        mergeSort(left);
        mergeSort(right);

        // Объединяем отсортированные подмассивы обратно в исходный массив.
        merge(arr, left, right);
    }

    // Определяем функцию merge для слияния двух отсортированных подмассивов в один отсортированный массив.
    public static void merge(int[] result, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;

        // Сравниваем элементы левого и правого подмассивов и объединяем их в результирующий массив.
        while (i < left.length && j < right.length) {
            if (left[i] < right[j]) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }

        // Если в одном из подмассивов остались элементы, добавляем их в результирующий массив.
        while (i < left.length) {
            result[k++] = left[i++];
        }

        while (j < right.length) {
            result[k++] = right[j++];
        }
    }
}