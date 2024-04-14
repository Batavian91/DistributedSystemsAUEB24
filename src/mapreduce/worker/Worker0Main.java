package mapreduce.worker;

public class Worker0Main
{
    public static void main(String[] args)
    {
        String reducer = "127.0.0.1:4328";

        Worker worker = new Worker(4322, 0);
        worker.setReducer(reducer);

        worker.runWorker();
    }

}