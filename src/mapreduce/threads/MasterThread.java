package mapreduce.threads;

import accomodation.Accommodation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MasterThread extends ActionHandler
{
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public void run()
    {
        try
        {
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
            System.out.println("OK1");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            Accommodation acc = (Accommodation) inputStream.readObject();
            acc.addRating(5);
            acc.setPrice(50);
            outputStream.writeObject(acc);
            outputStream.flush();

            MapRequest m = new MapRequest(acc);
            m.run();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally
        {
            try
            {
                inputStream.close();
                outputStream.close();
            } catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        }
    }

    private class MapRequest
    {
        Accommodation test;

        MapRequest(Accommodation t)
        {
            this.test = t;
        }

        public void run()
        {
            Socket requestSocket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;

            try
            {
                requestSocket = new Socket("127.0.0.1", 4322);
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                out.writeObject(test);
                out.flush();
                Accommodation response = (Accommodation) in.readObject();
                System.out.println(STR."Price is \{response.getPrice()}");
            } catch (UnknownHostException unknownHost)
            {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException)
            {
                System.err.println("IO was interrupted!");
                //ioException.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                System.err.println("Class not found!");
                //e.printStackTrace();
            } finally
            {
                try
                {
                    in.close();
                    out.close();
                    requestSocket.close();
                } catch (IOException | NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}