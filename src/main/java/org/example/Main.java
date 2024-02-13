package org.example;

import java.util.*;
import java.util.concurrent.*;


public class Main {
    public static void main(String[] args) {
        final int countTrack = 100;
        char find = 'R';
        Map<Integer, Integer> sizeToFreq = new HashMap<>();

        // Создаём пул потоков
        ExecutorService executor = Executors.newFixedThreadPool(countTrack);

        for (int i = 0; i < countTrack; i++) {
            //задание для каждого нового потока
            Runnable task = () -> {
                String maxLeght = generateRoute("RLRFR", 100);
                int Count = (int) maxLeght.chars().filter(ch -> ch == find).count();
                //критическая секция мапы, запускаем по одному потоку
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(Count)) {
                        sizeToFreq.put(Count, (sizeToFreq.get(Count) + 1));
                    } else {
                        sizeToFreq.put(Count, 1);
                    }
                }
            };
            // Отправляем задачу на выполнение в пул потоков
            Future<Integer> newTask = (Future<Integer>) executor.submit(task);
        }
        // Завершаем работу пула потоков
        executor.shutdown();

        System.out.println("Самое частое количество повторений " +
                sizeToFreq.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey() +
                " (встретилось " + sizeToFreq.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue()
                + " раз)" + "\nДругие размеры:");
        sizeToFreq.forEach((k, v) -> {
            if (sizeToFreq.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey() != k)
                System.out.println(
                        " - " + k + " (" + v + " раз)");
        });

    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}