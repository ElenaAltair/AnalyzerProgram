import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    /*
    public static int maxA = 0;
    public static int maxB = 0;
    public static int maxC = 0;
    */
    public static CopyOnWriteArrayList<Integer> countABC = new CopyOnWriteArrayList<>(new Integer[]{0, 0, 0});

    public static void main(String[] args) {

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    String str = generateText("abc", 100000);
                    queueA.put(str);
                    queueB.put(str);
                    queueC.put(str);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        thread1.start();


        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    checkCount(0, 'a', queueA);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        thread2.start();

        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    checkCount(1, 'b', queueB);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        thread3.start();

        Thread thread4 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                try {
                    checkCount(2, 'c', queueC);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        thread4.start();

        //Главный поток продолжит работу только после того, как все потоки завершат работу

        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Максимальное количество символов 'a': " + countABC.get(0));
        System.out.println("Максимальное количество символов 'b': " + countABC.get(1));
        System.out.println("Максимальное количество символов 'c': " + countABC.get(2));
        /*
        System.out.println("Максимальное количество символов 'a': " + maxA);
        System.out.println("Максимальное количество символов 'b': " + maxB);
        System.out.println("Максимальное количество символов 'c': " + maxC);
        */
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void checkCount(int index, char charABC, BlockingQueue<String> queue) throws InterruptedException {
        String str = queue.take();
        int count = 0;
        for (int j = 0; j < str.length(); j++) {
            if (str.charAt(j) == charABC) {
                count++;
            }
        }

        if (countABC.get(index) < count) {
            countABC.set(index, count);
        }
        /*
        if (maxC < count) {
           maxC = count;
        }
        */
    }
}

/*
Задача 1. Программа-анализатор
Описание
Ваш коллега из первой задачи до сих пор ломает голову над математической статистикой.
Благодаря уже известному вам генератору, он создаёт из символов "abc" 10 000 текстов,
длиной 100 000 каждый.

Генератор текстов.
Теперь его интересует, как бы выглядел текст, в котором содержится максимальное количество:

символов 'a';
символов 'b';
символов 'c'.
Попробуем решить эту задачу многопоточно: чтобы за анализ строк на предмет максимального
количества каждого из трёх символов отвечал отдельный поток.

То есть за поиск строки с самым большим количеством символов 'a' отвечал бы один поток,
за поиск с самым большим количеством 'b' — второй и за 'c' — третий.

Но сгенерировать все тексты, сохранить их в массив и затем пройтись по ним неправильно,
т. к. суммарно в текстах было бы около 1 млрд. символов,
что привело бы к избыточному расходу памяти.
Мы можем пойти другим путём и распараллелить этап создания строк и этапы их анализа.

Для этого строки будут генерироваться в отдельном потоке и заполнять блокирующие очереди,
максимальный размер которых ограничим 100 строками.

Очереди нужно будет сделать по одной для каждого из трёх анализирующих потоков,
т. к. строка должна быть обработана каждым таким потоком.

Подсказка.
Воспользуйтесь ArrayBlockingQueue.
 */
