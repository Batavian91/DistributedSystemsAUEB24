package mapreduce.worker;

public class Worker3Main
{
    public static void main(String[] args)
    {
        String reducer = "127.0.0.1:4328";

        Worker worker3 = new Worker(4325, 3);
        worker3.setReducer(reducer);

        worker3.runWorker();
    }

}