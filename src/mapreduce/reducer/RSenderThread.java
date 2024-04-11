package mapreduce.reducer;

import global.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class RSenderThread extends Thread
{
    private final long id;
    private final Reducer parent;

    public RSenderThread(long id, Reducer parent)
    {
        this.id = id;
        this.parent = parent;
    }

    public void run()
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try
        {
            Message message;

            if (parent.data.containsKey(id))
                message = new Message(id, "REPLY", parent.data.get(id));
            else
                message = new Message(id, "REPLY", "Data entered successfully!");

            String ip = parent.getMaster().split(":")[0];
            int port = Integer.parseInt(parent.getMaster().split(":")[1]);

            requestSocket = new Socket(ip, port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /*handshake with master*/
            out.writeObject("REDUCER: Hello, MASTER!");
            out.flush();
            String handshake = (String) in.readObject();
            System.out.println(handshake);

            out.writeObject(message);
            out.flush();

            parent.activeRequests.remove(id);
            parent.data.remove(id);

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

}