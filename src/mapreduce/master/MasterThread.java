package mapreduce.master;

import accomodation.Accommodation;
import mapreduce.helpers.Message;
import mapreduce.helpers.Node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MasterThread extends Thread
{
    private final Socket connection;
    private final Master parent;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final String[] nodes;
    private final String[] replicas;

    public MasterThread(Socket connection, Master parent)
    {
        this.connection = connection;
        this.parent = parent;
        nodes = parent.getNodes();
        replicas = parent.getReplicas();
    }

    public void run()
    {
        try
        {
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
        } catch (IOException ioException)
        {
            System.err.println(STR."Unable to create stream instances in Master Thread \{threadId()}");
            //ioException.printStackTrace();
        }
        try
        {
            /*two-way handshake*/
            String handshake = (String) inputStream.readObject();
            System.out.println(handshake);
            outputStream.writeObject("MASTER: Greetings! What can I do for you?");
            outputStream.flush();

            /*read object*/
            Message msg = (Message) inputStream.readObject();

            /* if client */
            if (handshake.startsWith("CLIENT"))
            {
                /* store connection - get unique id for mapping*/
                long id = parent.storeConnection(connection);

                /* if request is to add accommodations, map to specific workers */
                if (msg.action().equals("AC"))
                {
                    mapToSpecific(id, msg);
                }
                /* else map request to all workers */
                else
                {
                    mapToAll(id, msg);
                }

                /* do not close connection with client */

            } else if (handshake.startsWith("REDUCER"))
            {
                long rid = msg.id();

                /* close connection with reducer*/
                inputStream.close();
                outputStream.close();

                /* retrieve active connection with client */
                Socket con = parent.getActiveConnectionById(rid);
                sendDataToClient(con, msg.parameters());

                /* close connection with client */
                parent.removeConnection(rid);

            } else
            {
                System.out.println("Wrong handshake conversation!");
                inputStream.close();
                outputStream.close();
            }

        } catch (IOException ioException)
        {
            System.err.println(STR."Execution was abruptly interrupted in Master Thread \{threadId()}");
            //ioException.printStackTrace();
        } catch (ClassNotFoundException c)
        {
            System.err.println(STR."Master Thread \{threadId()} returned a ClassNotFoundException");
            //c.printStackTrace();
        }
    }

    private void mapToSpecific(long id, Message msg)
    {
        try
        {
            ArrayList<Accommodation> accommodations = (ArrayList<Accommodation>) msg.parameters();

            /* use a table of nodes to split equally accommodations to workers */
            Node[] nodesToStoreAcc = new Node[nodes.length];

            for (int i = 0; i < nodes.length; i++)
            {
                nodesToStoreAcc[i] = new Node(i);
            }

            for (Accommodation acc : accommodations)
            {
                int nodeID = acc.getAccName().hashCode() % nodes.length;
                nodesToStoreAcc[nodeID].arrayList.add(acc);
            }

            String ip;
            int port;

            for (int i = 0; i < nodes.length; i++)
            {
                Message message = new Message(id, "AC", nodesToStoreAcc[i].arrayList);

                // send to main node
                ip = nodes[i].split(":")[0];
                port = Integer.parseInt(nodes[i].split(":")[1]);
                sendToWorker(ip, port, message);

                // send to replica
                ip = replicas[i].split(":")[0];
                port = Integer.parseInt(replicas[i].split(":")[1]);
                sendToWorker(ip, port, message);
            }

        } catch (Exception e)
        {
            System.err.println("Impossible casting while trying to map add request!");
            //throw new RuntimeException(e);
        }
    }

    private void mapToAll(long id, Message msg)
    {
        Message message = new Message(id, msg.action(), msg.parameters());
        String ip;
        int port;
        boolean sent;

        for (int i = 0; i < nodes.length; i++)
        {
            // send to main nodes
            ip = nodes[i].split(":")[0];
            port = Integer.parseInt(nodes[i].split(":")[1]);
            sent = sendToWorker(ip, port, message);

            if (!sent)
            // send to replicas
            {
                ip = replicas[i].split(":")[0];
                port = Integer.parseInt(replicas[i].split(":")[1]);
                sendToWorker(ip, port, message);
            }
        }
    }

    private boolean sendToWorker(String ip, int port, Message msg)
    {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        boolean sent = false;

        try
        {
            requestSocket = new Socket(ip, port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            /*handshake with worker*/
            out.writeObject("MASTER: Hello, WORKER!");
            out.flush();
            String handshake = (String) inputStream.readObject();
            System.out.println(handshake);

            out.writeObject(msg);
            out.flush();
            String response = (String) in.readObject();
            System.out.println(response);
            sent = true;

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
                System.err.println(STR."Unable to close stream instances in Master Thread \{threadId()}");
                //ioException.printStackTrace();
            }
        }
        return sent;
    }

    private void sendDataToClient(Socket connection, Object msg)
    {
        try
        {
            outputStream = new ObjectOutputStream(connection.getOutputStream());
            inputStream = new ObjectInputStream(connection.getInputStream());
        } catch (IOException ioException)
        {
            System.err.println(STR."Unable to create stream instances in Master Thread \{threadId()}");
            //ioException.printStackTrace();
        }
        try
        {
            outputStream.writeObject(msg);
            outputStream.flush();
        } catch (IOException ioException)
        {
            System.err.println(STR."Execution was abruptly interrupted in Master Thread \{threadId()}");
            //ioException.printStackTrace();
        } finally
        {
            try
            {
                inputStream.close();
                outputStream.close();
            } catch (IOException ioException)
            {
                System.err.println(STR."Unable to close stream instances in Master Thread \{threadId()}");
                //ioException.printStackTrace();
            }
        }
    }

}