package mapreduce.reducer;

import global.Action;
import global.Message;
import global.Pair;
import global.Room;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    @SuppressWarnings("unchecked")
    private Message reduce()
    {
        ArrayList<Message> data = PARENT.data.get(ID);
        Action action = data.getFirst().action();

        switch(action)
        {
            case ADD:
                return new Message(ID, action, "\nRooms were successfully added!");

            case PRINT_ALL:

                ArrayList<Pair<Room, String>> bookings = new ArrayList<>();

                for (Message msg : data)
                {
                    if (msg.parameters() != null)
                        bookings.addAll((ArrayList<Pair<Room, String>>) msg.parameters());
                }

                return new Message(ID, action, bookings);

            case PRINT:

                HashMap<String, Integer> map = new HashMap<>();

                for (Message msg : data)
                {
                    if (msg.parameters() != null)
                    {
                        HashMap<String, Integer> temp = ((HashMap<String, Integer>)msg.parameters());

                        for (Map.Entry<String, Integer> entry : temp.entrySet())
                            map.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    }
                }

                return new Message(ID, action, map);

            case SEARCH:

                ArrayList<Room> rooms = new ArrayList<>();

                for (Message msg : data)
                {
                    if (msg.parameters() != null)
                        rooms.addAll((ArrayList<Room>) msg.parameters());
                }

                return new Message(ID, action, rooms);

            case BOOK:

                String response = "";
                Pair<Integer, String> parameters;

                for (Message msg : data)
                {
                    parameters = (Pair<Integer, String>) msg.parameters();

                    if (parameters.getType1() != 0)
                    {
                        response = parameters.getType2();
                        break;
                    }
                }

                return new Message(ID, action, response);

            case REVIEW:
                return new Message(ID, action, "\nYour review was successfully added!");

            default:
                break;
        }

        return new Message(ID, action, "\nBad Request!");
    }

}