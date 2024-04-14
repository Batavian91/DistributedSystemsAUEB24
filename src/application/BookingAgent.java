package application;

import global.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class BookingAgent
{
    private final String MASTER_IP;
    private final int MASTER_PORT;

    public BookingAgent(String master)
    {
        MASTER_IP = master.split(":")[0];
        MASTER_PORT = Integer.parseInt(master.split(":")[1]);
    }

    public void add(String filepath)
    {
        ArrayList<Accommodation> arrayList = new JsonReader().readAccommodationsFromFile(filepath);
        Message request = new Message(0, Action.ADD, arrayList);
        Object response = sendToMaster(request);

        if (response == null)
            System.out.println("Addition was unsuccessful! Please, try again!");
        else
            System.out.println((String) response);
    }

    @SuppressWarnings("unchecked")
    public void print(DateRange dateRange)
    {
        Message request = new Message(0, Action.PRINT, dateRange);
        Object response = sendToMaster(request);

        if (response != null)
        {
            HashMap<String, Integer> map = (HashMap<String, Integer>) response;

            for (String area: map.keySet())
            {
                System.out.println(STR."\{area}: \{map.get(area)}reservations.");
            }
        } else
        {
            System.out.println("No reservations to print!");
        }
    }

//    @SuppressWarnings("unchecked")
//    public void printAll()
//    {
//        Message request = new Message(0, Action.PRINT, dateRange);
//        Object response = sendToMaster(request);
//
//        if (response != null)
//        {
//            ArrayList<Room> rooms = (ArrayList<Room>)response;
//
//            for (Room room : rooms)
//            {
//                System.out.println(room.toString());
//            }
//        } else
//        {
//            System.out.println("No rooms to print!");
//        }
//    }

    @SuppressWarnings("unchecked")
    public ArrayList<Room> search(Filter filters)
    {
        Message request = new Message(0, Action.SEARCH, filters);
        Object response = sendToMaster(request);

        if (response != null)
            return (ArrayList<Room>)response;

        return null;
    }

    public void book(Pair<String, DateRange> room)
    {
        Message request = new Message(0, Action.BOOK, room);
        Object response = sendToMaster(request);

        if (response == null)
            System.out.println("Booking was unsuccessful! Please, try again!");
        else
            System.out.println((String) response);
    }

    public void review(Pair<String, Integer> star)
    {
        Message request = new Message(0, Action.REVIEW, star);
        Object response = sendToMaster(request);

        if (response == null)
            System.out.println("Your review could not be entered! Please, try again!");
        else
            System.out.println((String) response);
    }

    private Object sendToMaster(Message request)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Object response = null;

        try
        {
            requestSocket = new Socket(MASTER_IP, MASTER_PORT);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /* handshake with master */
            out.writeObject("CLIENT: Hello, MASTER!\n");
            out.flush();
            String handshake = (String) in.readObject();
            System.out.println(handshake);

            out.writeObject(request);
            out.flush();

            response = in.readObject();

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
                System.err.println("Unable to close stream instances in client!");
                //ioException.printStackTrace();
            }
        }
        return response;
    }

}