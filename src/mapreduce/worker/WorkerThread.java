package mapreduce.worker;

import global.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkerThread extends Thread
{
    private final Socket CONNECTION;
    private final Worker PARENT;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public WorkerThread(Socket connection, Worker parent)
    {
        CONNECTION = connection;
        PARENT = parent;
    }

    @SuppressWarnings("unchecked")
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
            /* handshake with master thread */
            String handshake = (String) inputStream.readObject();
            System.out.println(handshake);
            outputStream.writeObject(STR."WORKER \{PARENT.getWorker()}: Greetings! What can I do for you?\n");
            outputStream.flush();

            /* read object sent from master thread */
            Message msg = (Message) inputStream.readObject();

            outputStream.writeObject("Message received!");
            outputStream.flush();

            inputStream.close();
            outputStream.close();

            /* handle request */
            long id = msg.id();
            Action action = msg.action();

            Message result = null;

            switch (action)
            {

                case ADD:

                    ArrayList<Accommodation> arrayList = (ArrayList<Accommodation>) msg.parameters();
                    if (arrayList != null)
                        PARENT.accommodations.addAll(arrayList);
                    result = new Message(id, action, "OK");

                    break;

                case PRINT:

                    HashMap<String, Integer> map = new HashMap<>();
                    DateRange range = (DateRange) msg.parameters();

                    for (Accommodation acc : PARENT.accommodations)
                    {
                        String area = acc.getLocation();
                        int counter = acc.countBookings(range);

                        if (counter != 0)
                            map.merge(area, counter, Integer::sum);
                    }
                    result = new Message(id, action, map);

                    break;

                case SEARCH:

                    ArrayList<Room> rooms = new ArrayList<>();
                    Filter filter = (Filter) msg.parameters();
                    DateRange range1 = filter.PARAM_DATE;

                    for (Accommodation acc : PARENT.accommodations)
                    {
                        // area
                        if (!(filter.PARAM_AREA.isEmpty() && filter.PARAM_AREA.equals(acc.getLocation())))
                            continue;

                        // date
                        boolean overlap = false;
                        for (DateRange dtr : acc.bookedByVisitorDates)
                            if (!(dtr.getEndDate().isBefore(range1.getStartDate())
                                    || range1.getEndDate().isBefore(dtr.getStartDate())))
                            {
                                overlap = true;
                                break;
                            }

                        if (overlap)
                            continue;

                        // guests
                        if (!(filter.PARAM_GUESTS < 1 && filter.PARAM_GUESTS == acc.getGuests()))
                            continue;

                        // price
                        if (!(filter.PARAM_PRICE < 1 && filter.PARAM_PRICE == acc.getPrice()))
                            continue;

                        //stars
                        if (!(filter.PARAM_STARS < 1 && filter.PARAM_STARS == acc.getRating()))
                            continue;

                        Room room = new Room(acc.getAccName(), acc.getGuests(), acc.getLocation(), acc.getPrice(),
                                acc.getRating(), acc.getNoOfReviews(), acc.getPhoto());

                        rooms.add(room);
                    }
                    result = new Message(id, action, rooms);

                    break;

                case BOOK:

                    Pair<String, DateRange> room = (Pair<String, DateRange>) msg.parameters();
                    String name = room.getType1();
                    DateRange dtr = room.getType2();

                    Pair<Integer, String> parameters = new Pair<>(0, "");

                    for (Accommodation acc : PARENT.accommodations)
                    {
                        if (name.equalsIgnoreCase(acc.getAccName()))
                        {
                            parameters = acc.addBookingDates(dtr);
                            break;
                        }
                    }
                    result = new Message(id, action, parameters);

                    break;

                case REVIEW:

                    Pair<String, Integer> pair1 = (Pair<String, Integer>) msg.parameters();
                    String name1 = pair1.getType1();
                    int stars = pair1.getType2();

                    for (Accommodation acc : PARENT.accommodations)
                    {
                        if (name1.equalsIgnoreCase(acc.getAccName()))
                        {
                            acc.addRating(stars);
                            break;
                        }
                    }
                    result = new Message(id, action, "OK");

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
        } finally
        {
            try
            {
                inputStream.close();
                outputStream.close();
            } catch (IOException e)
            {
                System.err.println(STR."Unable to close stream instances in Worker Thread \{threadId()}");
                //e.printStackTrace();
            }
        }
    }

    private void sendToReducer(Message result)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try
        {
            String ip = PARENT.getReducer().split(":")[0];
            int port = Integer.parseInt(PARENT.getReducer().split(":")[1]);

            requestSocket = new Socket(ip, port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /* handshake with reducer thread */
            out.writeObject(STR."WORKER \{PARENT.getWorker()}: Hello, REDUCER!\n");
            out.flush();
            String handshake = (String) in.readObject();
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