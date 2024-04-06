package mapreduce.worker;

public class WorkerMain
{

    public static void main(String[] args)
    {
        String reducer = "127.0.0.1:4328";

        Worker worker = new Worker(4322, 0);
        worker.setReducer(reducer);

        worker.runWorker();


        Worker worker1 = new Worker(4323, 1);
        worker1.setReducer(reducer);

        worker1.runWorker();


        Worker worker2 = new Worker(4324, 2);
        worker2.setReducer(reducer);

        worker2.runWorker();


        Worker worker3 = new Worker(4325, 3);
        worker3.setReducer(reducer);

        worker3.runWorker();


        Worker worker4 = new Worker(4326, 4);
        worker4.setReducer(reducer);

        worker4.runWorker();


        Worker worker5 = new Worker(4327, 5);
        worker5.setReducer(reducer);

        worker5.runWorker();
    }

}