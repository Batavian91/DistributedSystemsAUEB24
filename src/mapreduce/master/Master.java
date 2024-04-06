package mapreduce.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class Master
{
    private ServerSocket server;
    private String[] nodes;
    private String[] replicas;
    private static HashMap<long, Socket> activeConnections;

    public Master(int inPort)
    {
        try
        {
            this.server = new ServerSocket(inPort, 1000);
            activeConnections = null;
        } catch (IOException ioException)
        {
            System.err.println("Could not initialize Master!");
            //ioException.printStackTrace();
        }
    }

    public void runMaster()
    {
        try
        {
            while (true)
            {
                Socket serverListener = server.accept();

                MasterThread thread = new MasterThread(serverListener, this);

                thread.start();
            }
        } catch (IOException ioException)
        {
            System.err.println("Execution was abruptly interrupted in Master!");
            //ioException.printStackTrace();
        } finally
        {
            try
            {
                this.server.close();
            } catch (NullPointerException | IOException e)
            {
                System.err.println("An exception occurred while trying to close Master!");
                //e.printStackTrace();
            }
        }
    }

    public synchronized long storeConnection(Socket connection)
    {
        UUID uniqueID = UUID.randomUUID();
        long id = Math.abs(uniqueID.getMostSignificantBits());
        activeConnections.put(id, connection);
        return id;
    }

    public Socket getActiveConnectionById(long id)
    {
        return activeConnections.get(id);
    }

    public synchronized void removeConnection(long id)
    {
        try
        {
            activeConnections.get(id).close();
        } catch (IOException ioException)
        {
            System.err.println("Could not close client connection!");
        }

        activeConnections.remove(id);
    }

    public String[] getNodes()
    {
        return nodes;
    }

    public String[] getReplicas()
    {
        return replicas;
    }

    public void setNodes(String[] nodes)
    {
        this.nodes = nodes;
    }

    public void setReplicas(String[] replicas)
    {
        this.replicas = replicas;
    }
}