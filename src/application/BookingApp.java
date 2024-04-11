package application;

import global.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class BookingApp
{
    private final String masterIP;
    private final int masterPort;

    public BookingApp(String master)
    {
        masterIP = master.split(":")[0];
        masterPort = Integer.parseInt(master.split(":")[1]);
    }

    public void add(String filepath)
    {
        ArrayList<Accommodation> arrayList = new JsonReader().readAccommodationsFromFile(filepath);
        Message request = new Message(0, Action.ADD.OPTION, arrayList);
        Message response = sendToMaster(request);

        if (response == null)
            System.out.println("Addition was unsuccessful! Please, try again!");
        else
            System.out.println((String) response.parameters());
    }

    @SuppressWarnings("unchecked")
    public void print(DateRange dateRange)
    {
        Message request = new Message(0, Action.PRINT.OPTION, dateRange);
        Message response = sendToMaster(request);

        if (response != null)
        {
            if (response.parameters() != null)
            {
                ArrayList<Room> rooms = (ArrayList<Room>)response.parameters();

                for (Room room : rooms)
                {
                    System.out.println(room.toString());
                }
            } else
            {
                System.out.println("No rooms to print!");
            }
        } else
        {
            System.out.println("Printing reservations was unsuccessful! Please, try again!");
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Room> search(Filter filters)
    {
        Message request = new Message(0, Action.SEARCH.OPTION, filters);
        Message response = sendToMaster(request);

        if (response != null)
            if (response.parameters() != null)
                return (ArrayList<Room>)response.parameters();

        return null;
    }

    public String book(Pair<String, DateRange> room)
    {
        Message request = new Message(0, Action.BOOK.OPTION, room);
        Message response = sendToMaster(request);

        if (response == null)
            return "Booking was unsuccessful! Please, try again!";
        else
            return (String) response.parameters();
    }

    public void review(Pair<String, Integer> star)
    {
        Message request = new Message(0, Action.REVIEW.OPTION, star);
        Message response = sendToMaster(request);

        if (response == null)
            System.out.println("Your review could not be entered! Please, try again!");
        else
            System.out.println((String) response.parameters());
    }

    private Message sendToMaster(Message request)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        Message response = null;

        try
        {
            requestSocket = new Socket(masterIP, masterPort);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /*handshake with master*/
            out.writeObject("CLIENT: Hello, MASTER!");
            out.flush();
            String handshake = (String) in.readObject();
            System.out.println(handshake);

            out.writeObject(request);
            out.flush();

            response = (Message)in.readObject();

        } catch (UnknownHostException unknownHost)
        {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException)
        {
            System.err.println("An unexpected interruption occurred while trying to send data to Workers!");
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