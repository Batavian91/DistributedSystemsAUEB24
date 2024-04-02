package mapreduce.nodes;

import java.io.*;

public class Master extends Server
{
    public Master(int port) throws IOException
    {
        super(port);
    }
    public static void main(String[] args) throws IOException
    {
        new Master(4321).runServer("Master");
    }
}