package mapreduce.reducer;

import global.Accommodation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Reducer
{
    private ServerSocket server;
    private String master;
    private int numberOfWorkers;
    protected HashMap<Long, Integer> activeRequests;
    protected HashMap<Long, ArrayList<Accommodation>> data;

    public Reducer(int inPort, int workers)
    {
        try
        {
            this.server = new ServerSocket(inPort, 1000);
            this.numberOfWorkers = workers;
            this.activeRequests = null;
            this.data = null;
        } catch (IOException ioException)
        {
            System.err.println("Could not initialize Reducer!");
            //ioException.printStackTrace();
        }
    }

    public void runReducer()
    {
        try
        {
            while (true)
            {
                Socket serverListener = server.accept();

                ReducerThread thread = new ReducerThread(serverListener, this);

                thread.start();
            }
        } catch (IOException ioException)
        {
            System.err.println("Execution was abruptly interrupted in Reducer!");
            //ioException.printStackTrace();
        } finally
        {
            try
            {
                this.server.close();
            } catch (NullPointerException | IOException e)
            {
                System.err.println("An exception occurred while trying to close Reducer!");
                //e.printStackTrace();
            }
        }
    }

    public String getMaster()
    {
        return master;
    }

    public void setMaster(String master)
    {
        this.master = master;
    }

    public synchronized void reduce(long id, boolean hasData, ArrayList<Accommodation> arrayList)
    {
        if (!activeRequests.containsKey(id))
        {
            activeRequests.put(id, 1);

            if (hasData && arrayList != null)
            {
                data.put(id, arrayList);
            }
        } else
        {
            int counter = activeRequests.get(id);

            activeRequests.put(id, ++counter);

            if (hasData && arrayList != null)
            {
                data.get(id).addAll(arrayList);
            }

            if (counter == numberOfWorkers)
            {
                RSenderThread rThread = new RSenderThread(id, this);
                rThread.start();
            }
        }
    }

}