package org.example;

import java.util.*;
import java.util.concurrent.*;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int countTrack = 100;
        char find = 'R';
        Map<Integer, Integer> sizeToFreq = new HashMap<>();

        // Создаём пул потоков
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < countTrack; i++) {
            //задание для каждого нового потока
            Thread thread = new Thread(() -> {
                String maxLeght = generateRoute("RLRFR", 100);
                int Count = (int) maxLeght.chars().filter(ch -> ch == find).count();
                //критическая секция мапы, запускаем по одному потоку
                synchronized (sizeToFreq) {
                    if (sizeToFreq.containsKey(Count)) {
                        sizeToFreq.put(Count, (sizeToFreq.get(Count) + 1));
                    } else {
                        sizeToFreq.put(Count, 1);
                    }
                    sizeToFreq.notify(); //сообщаем о возможности работы вторым потоком
                }
            });
            thread.start();//стартуем поток
            threads.add(thread);//добавляем в массив потоков каждый новый

        }

        Thread threadSecond = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    if (sizeToFreq.isEmpty()) {
                        try {
                            sizeToFreq.wait();//ожидание сигнала о возможности обработки
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Текущий лидер: " +
                            sizeToFreq.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey());
                }
            }
        }
        );
        threadSecond.start();

        for (Thread threadFirst : threads) {
            threadFirst.join(); // зависаем, ждём когда поток объект которого лежит в thread завершится
        }

        threadSecond.interrupt();//прерываем поток

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