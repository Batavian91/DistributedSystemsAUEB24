package mapreduce.worker;

public class Worker1Main
{
    public static void main(String[] args)
    {
        String reducer = "127.0.0.1:4328";

        Worker worker1 = new Worker(4323, 1);
        worker1.setReducer(reducer);

        worker1.runWorker();
    }

}