package mapreduce.threads;

import java.net.Socket;

public class ActionHandler extends Thread
{
    Socket connection;

    public ActionHandler()
    {
        this.connection = null;
    }

    public void setConnection(Socket connection)
    {
        this.connection = connection;
    }
}