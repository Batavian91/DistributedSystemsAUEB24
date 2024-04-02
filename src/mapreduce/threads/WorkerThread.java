package mapreduce.threads;

import accomodation.Accommodation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class WorkerThread extends ActionHandler
{
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    public void run()
    {
        try
        {
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
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
}