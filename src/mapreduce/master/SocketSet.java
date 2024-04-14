package mapreduce.master;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketSet
{
    protected final Socket SOCKET;
    protected final ObjectOutputStream OUTPUT;
    protected final ObjectInputStream INPUT;

    protected SocketSet(Socket socket, ObjectOutputStream out, ObjectInputStream in)
    {
        SOCKET = socket;
        OUTPUT = out;
        INPUT = in;
    }
}