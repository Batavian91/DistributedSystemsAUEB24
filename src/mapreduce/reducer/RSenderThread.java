package mapreduce.reducer;

import global.Action;
import global.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class RSenderThread extends Thread
{
    private final long ID;
    private final Reducer PARENT;

    public RSenderThread(long id, Reducer parent)
    {
        ID = id;
        PARENT = parent;
    }

    public void run()
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try
        {
            Message message = reduce();

            String ip = PARENT.getMaster().split(":")[0];
            int port = Integer.parseInt(PARENT.getMaster().split(":")[1]);

            requestSocket = new Socket(ip, port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /* handshake with master thread */
            out.writeObject("REDUCER: Hello, MASTER!\n");
            out.flush();
            String handshake = (String) in.readObject();
            System.out.println(handshake);

            out.writeObject(message);
            out.flush();

            PARENT.removeRequest(ID);

        } catch (UnknownHostException unknownHost)
        {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException)
        {
            System.err.println("An unexpected interruption occurred while trying to send data to Master!");
            //ioException.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            System.err.println("Class was not found!");
            //e.printStackTrace();
        } finally
        {
            try
            {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException)
            {
                System.err.println(STR."Unable to close stream instances in Reducer Thread \{threadId()}");
                //ioException.printStackTrace();
            }
        }
    }

    private Message reduce()
    {
        return new Message(ID, Action.ADD, "\nOK from Reducer!");
    }

}