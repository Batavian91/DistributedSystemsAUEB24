package mapreduce.worker;

import accomodation.Accommodation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Worker
{
    private ServerSocket server;
    private int worker;
    private String reducer;
    protected ArrayList<Accommodation> accommodations;

    public Worker(int inPort, int worker)
    {
        try
        {
            this.server = new ServerSocket(inPort, 1000);
            this.worker = worker;
            this.accommodations = null;
        } catch (IOException ioException)
        {
            System.err.println("Could not initialize Worker!");
            //ioException.printStackTrace();
        }
    }

    public void runWorker()
    {
        try
        {
            while (true)
            {
                Socket serverListener = server.accept();

                WorkerThread thread = new WorkerThread(serverListener, this);

                thread.start();
            }
        } catch (IOException ioException)
        {
            System.err.println("Execution was abruptly interrupted in Worker!");
            //ioException.printStackTrace();
        } finally
        {
            try
            {
                this.server.close();
            } catch (NullPointerException | IOException e)
            {
                System.err.println("An exception occurred while trying to close Worker!");
                //e.printStackTrace();
            }
        }
    }

    public int getWorker()
    {
        return worker;
    }

    public String getReducer()
    {
        return reducer;
    }

    public void setReducer(String reducer)
    {
        this.reducer = reducer;
    }

}