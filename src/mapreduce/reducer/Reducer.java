package mapreduce.reducer;

import global.Action;
import global.Message;

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
    private HashMap<Long, Integer> activeRequests;
    protected HashMap<Long, ArrayList<Message>> data;

    public Reducer(int inPort, int workers)
    {
        try
        {
            this.server = new ServerSocket(inPort, 1000);
            this.numberOfWorkers = workers;
            this.activeRequests = new HashMap<>();
            this.data = new HashMap<>();
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

    protected String getMaster()
    {
        return master;
    }

    protected void setMaster(String master)
    {
        this.master = master;
    }

    protected synchronized void storeRequest(long id, Message message, Action rw)
    {
        if (!activeRequests.containsKey(id))
        {
            activeRequests.put(id, 1);
            data.put(id, new ArrayList<>());
        } else
        {
            activeRequests.merge(id, 1, Integer::sum);
        }
        data.get(id).add(message);

        if (rw.equals(Action.WRITE))
        {
            if (activeRequests.get(id) == numberOfWorkers)
            {
                RSenderThread rThread = new RSenderThread(id, this);
                rThread.start();
            }
        } else
        {
            if (activeRequests.get(id) == (numberOfWorkers/2))
            {
                RSenderThread rThread = new RSenderThread(id, this);
                rThread.start();
            }
        }
    }

    protected void removeRequest(long id)
    {
        activeRequests.remove(id);
        data.remove(id);
    }

}