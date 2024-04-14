package mapreduce.worker;

public class Worker2Main
{
    public static void main(String[] args)
    {
        String reducer = "127.0.0.1:4328";

        Worker worker2 = new Worker(4324, 2);
        worker2.setReducer(reducer);

        worker2.runWorker();
    }
}
