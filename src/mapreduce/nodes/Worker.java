package mapreduce.nodes;

import mapreduce.nodes.Master;
import mapreduce.nodes.Server;

import java.io.*;

public class Worker extends Server
{
    public Worker(int port) throws IOException
    {
        super(port);
    }
    public static void main(String[] args) throws IOException
    {
        new Master(4322).runServer("Worker");
    }
}