package mapreduce.reducer;

import global.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ReducerThread extends Thread
{
    private final Socket CONNECTION;
    private final Reducer PARENT;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public ReducerThread(Socket connection, Reducer parent)
    {
        CONNECTION = connection;
        PARENT = parent;
    }

    public void run()
    {
        try
        {
            outputStream = new ObjectOutputStream(CONNECTION.getOutputStream());
            inputStream = new ObjectInputStream(CONNECTION.getInputStream());
        } catch (IOException ioException)
        {
            System.err.println(STR."Unable to create stream instances in Worker Thread \{threadId()}");
            //ioException.printStackTrace();
        }
        try
        {
            /* handshake with worker thread */
            String handshake = (String) inputStream.readObject();
            System.out.println(handshake);
            outputStream.writeObject("REDUCER: Greetings! What can I do for you?\n");
            outputStream.flush();

            /* read object sent from worker thread */
            Message msg = (Message) inputStream.readObject();

            outputStream.writeObject("Message received!");
            outputStream.flush();

            inputStream.close();
            outputStream.close();

            /* store temporarily data received */
            long id = msg.id();
            PARENT.storeRequest(id, msg);

        } catch (IOException ioException)
        {
            System.err.println(STR."Execution was abruptly interrupted in Reducer Thread \{threadId()}");
            //ioException.printStackTrace();
        } catch (ClassNotFoundException c)
        {
            System.err.println(STR."Reducer Thread \{threadId()} returned a ClassNotFoundException");
            //c.printStackTrace();
        }
    }

}