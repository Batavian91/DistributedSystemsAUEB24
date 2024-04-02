package mapreduce.nodes;

import accomodation.Accommodation;

import java.io.*;
import java.net.*;

public class Client extends Thread
{
    Accommodation t;

    Client(Accommodation t)
    {
        this.t = t;
    }

    public void run()
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try
        {
            requestSocket = new Socket("127.0.0.1", 4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeObject(t);
            out.flush();
            Accommodation response = (Accommodation) in.readObject();
            System.out.println(STR."Price is \{response.getPrice()}");

        } catch (UnknownHostException unknownHost)
        {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally
        {
            try
            {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String args[])
    {
        Accommodation accommodation = new Accommodation("first", 1, "area1", 1, 1, 1);
        new Client(accommodation).start();
    }
}