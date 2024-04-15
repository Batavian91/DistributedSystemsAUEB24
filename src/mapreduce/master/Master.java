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
    private HashMap<Long, SocketSet> activeConnections;

    public Master(int inPort)
    {
        try
        {
            this.server = new ServerSocket(inPort, 1000);
            this.activeConnections = new HashMap<>();
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

    protected synchronized long storeConnection(SocketSet socketSet)
    {
        UUID uniqueID = UUID.randomUUID();
        long id = Math.abs(uniqueID.getMostSignificantBits());
        activeConnections.put(id, socketSet);
        return id;
    }

    protected SocketSet getActiveConnectionById(long id)
    {
        return activeConnections.get(id);
    }

    protected void removeConnection(long id)
    {
        activeConnections.remove(id);
    }

    protected String[] getNodes()
    {
        return nodes;
    }

    protected String[] getReplicas()
    {
        return replicas;
    }

    protected void setNodes(String[] nodes)
    {
        this.nodes = nodes;
    }

    protected void setReplicas(String[] replicas)
    {
        this.replicas = replicas;
    }
}