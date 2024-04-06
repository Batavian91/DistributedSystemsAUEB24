package mapreduce.master;

public class MasterMain
{
    public static void main(String[] args)
    {
        String[] workers = new String[3];
        workers[0] = "127.0.0.1:4322";
        workers[1] = "127.0.0.1:4323";
        workers[2] = "127.0.0.1:4324";

        String[] hotStandByWorkers = new String[3];
        hotStandByWorkers[0] = "127.0.0.1:4325";
        hotStandByWorkers[1] = "127.0.0.1:4326";
        hotStandByWorkers[2] = "127.0.0.1:4327";

        Master master = new Master(4321);
        master.setNodes(workers);
        master.setReplicas(hotStandByWorkers);

        master.runMaster();
    }

}