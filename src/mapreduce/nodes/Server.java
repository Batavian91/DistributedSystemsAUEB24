package mapreduce.nodes;

import mapreduce.threads.ActionHandler;
import mapreduce.threads.MasterThread;
import mapreduce.threads.ReducerThread;
import mapreduce.threads.WorkerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private ServerSocket server;
    public Socket serverListener;
    public Server(int inPort) throws IOException
    {
        try
        {
            this.server = new ServerSocket(inPort, 1000);
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
            System.exit(1);
        }
    }

    public void runServer(String handler)
    {
        try
        {
            while (true)
            {
                serverListener = server.accept();
                ActionHandler action;

                if (handler.equals("Master"))
                    action = new MasterThread();
                else if (handler.equals("Worker"))
                    action = new WorkerThread();
                else
                    action = new ReducerThread();

                action.setConnection(serverListener);
                action.start();
            }
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
            System.exit(1);
        } finally
        {
            try
            {
                this.server.close();
            } catch (NullPointerException | IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}