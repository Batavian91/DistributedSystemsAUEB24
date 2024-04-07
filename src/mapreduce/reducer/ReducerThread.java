package mapreduce.reducer;

import accomodation.Accommodation;
import mapreduce.helpers.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ReducerThread extends Thread
{
    private final Socket connection;
    private final Reducer parent;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public ReducerThread(Socket connection, Reducer parent)
    {
        this.connection = connection;
        this.parent = parent;
    }

    public void run()
    {
        try
        {
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
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
            outputStream.writeObject("REDUCER: Greetings! What can I do for you?");
            outputStream.flush();

            /*read object sent from worker thread */
            Message msg = (Message) inputStream.readObject();

            outputStream.writeObject("Message received!");
            outputStream.flush();

            inputStream.close();
            outputStream.close();

            /* handle data received */
            long id = msg.id();
            String request = msg.action();

            switch (request)
            {
                case "AC", "BOOK", "REVIEW":
                    parent.reduce(id, false, null);
                    break;

                case "PR", "AREA", "DATE", "GUEST", "STAR", "SEARCH":
                    ArrayList<Accommodation> arrayList = null;

                    if(msg.parameters() != null)
                        arrayList = (ArrayList<Accommodation>) msg.parameters();

                    parent.reduce(id, true, arrayList);
                    break;

                default:
                    System.out.println("Message received from worker was not in correct format!");
                    break;
            }

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