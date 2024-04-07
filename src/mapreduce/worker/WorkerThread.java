package mapreduce.worker;

import accomodation.Accommodation;
import accomodation.DateRange;
import mapreduce.helpers.Message;
import mapreduce.helpers.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class WorkerThread extends Thread
{
    private final Socket connection;
    private final Worker parent;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public WorkerThread(Socket connection, Worker parent)
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
            /* handshake with master thread */
            String handshake = (String) inputStream.readObject();
            System.out.println(handshake);
            outputStream.writeObject("WORKER: Greetings! What can I do for you?");
            outputStream.flush();

            /*read object sent from master thread */
            Message msg = (Message) inputStream.readObject();

            outputStream.writeObject("Message received!");
            outputStream.flush();

            inputStream.close();
            outputStream.close();

            /* handle request */
            long id = msg.id();
            String request = msg.action();

            Message result = null;
            ArrayList<Accommodation> arrayList = null;

            switch (request)
            {
                case "AC":

                    arrayList = (ArrayList<Accommodation>) msg.parameters();
                    parent.accommodations.addAll(arrayList);
                    result = new Message(id, "AC", "OK");

                    break;

                case "PR":

                    for (Accommodation acc : parent.accommodations)
                    {
                        if (acc.hasBookings())
                            arrayList.add(acc);
                    }
                    result = new Message(id, "PR", arrayList);

                    break;

                case "AREA":
                    
                    for (Accommodation acc : parent.accommodations)
                    {
                        if (acc.getLocation().equals(msg.parameters()))
                            arrayList.add(acc);
                    }
                    result = new Message(id, "AREA", arrayList);

                    break;

                case "DATE":

                    for (Accommodation acc : parent.accommodations)
                    {
                        if (acc.bookedByVisitorDates.contains((DateRange) msg.parameters()))
                            arrayList.add(acc);
                    }
                    result = new Message(id, "DATE", arrayList);

                    break;

                case "GUEST":

                    for (Accommodation acc : parent.accommodations)
                    {
                        if (acc.getGuests() == (Integer)msg.parameters())
                            arrayList.add(acc);
                    }
                    result = new Message(id, "GUEST", arrayList);

                    break;

                case "STAR":

                    for (Accommodation acc : parent.accommodations)
                    {
                        if (acc.getRating() == (Integer)msg.parameters())
                            arrayList.add(acc);
                    }
                    result = new Message(id, "STAR", arrayList);

                    break;

                case "BOOK":

                    Pair<Accommodation, DateRange> pair = (Pair<Accommodation, DateRange>) msg.parameters();
                    Accommodation accommodation = pair.getType1();
                    DateRange dtr = pair.getType2();

                    String parameters = "";

                    for (Accommodation acc : parent.accommodations)
                    {
                        if (acc  == accommodation)
                        {
                            parameters = acc.addBookingDates(dtr);
                        }
                    }
                    result = new Message(id, "BOOK", parameters);

                    break;

                case "REVIEW":

                    Pair<Accommodation, Integer> pair1 = (Pair<Accommodation, Integer>) msg.parameters();
                    Accommodation accommodation1 = pair1.getType1();
                    int stars = pair1.getType2();

                    for (Accommodation acc : parent.accommodations)
                    {
                        if (acc  == accommodation1)
                        {
                            acc.addRating(stars);
                        }
                    }
                    result = new Message(id, "REVIEW", "OK");

                    break;

                case "SEARCH":

                    result = new Message(id, "SEARCH", parent.accommodations);

                    break;

                default:
                    break;
            }

            sendToReducer(result);

        } catch (IOException ioException)
        {
            System.err.println(STR."Execution was abruptly interrupted in Worker Thread \{threadId()}");
            //ioException.printStackTrace();
        } catch (ClassNotFoundException c)
        {
            System.err.println(STR."Worker Thread \{threadId()} returned a ClassNotFoundException");
            //c.printStackTrace();
        }
    }

    private void sendToReducer(Message result)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try
        {
            String ip = parent.getReducer().split(":")[0];
            int port = Integer.parseInt(parent.getReducer().split(":")[1]);

            requestSocket = new Socket(ip, port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /*handshake with reducer*/
            out.writeObject(STR."WORKER \{parent.getWorker()}: Hello, REDUCER!");
            out.flush();
            String handshake = (String) inputStream.readObject();
            System.out.println(handshake);

            out.writeObject(result);
            out.flush();
            String response = (String) in.readObject();
            System.out.println(response);

        } catch (UnknownHostException unknownHost)
        {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException)
        {
            System.err.println("An unexpected interruption occurred while trying to send data to Reducer!");
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
                System.err.println(STR."Unable to close stream instances in Worker Thread \{threadId()}");
                //ioException.printStackTrace();
            }
        }
    }

}