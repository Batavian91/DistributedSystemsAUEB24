package mapreduce.master;

import global.Accommodation;
import global.Action;
import global.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MasterThread extends Thread
{
    private final Socket CONNECTION;
    private final Master PARENT;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private final String[] NODES;
    private final String[] REPLICAS;

    public MasterThread(Socket connection, Master parent)
    {
        CONNECTION = connection;
        PARENT = parent;
        NODES = parent.getNodes();
        REPLICAS = parent.getReplicas();
    }

    public void run()
    {
        try
        {
            outputStream = new ObjectOutputStream(CONNECTION.getOutputStream());
            inputStream = new ObjectInputStream(CONNECTION.getInputStream());
        } catch (IOException ioException)
        {
            System.err.println(STR."Unable to create stream instances in Master Thread \{threadId()}");
            //ioException.printStackTrace();
        }
        try
        {
            /* two-way handshake */
            String handshake = (String) inputStream.readObject();
            System.out.println(handshake);
            outputStream.writeObject("MASTER: Greetings! What can I do for you?\n");
            outputStream.flush();

            /* read message sent either from client or from reducer */
            Message msg = (Message) inputStream.readObject();

            /* if client */
            if (handshake.startsWith("CLIENT"))
            {
                /* store connection - get unique id for mapping*/
                long id = PARENT.storeConnection(new SocketSet(CONNECTION, outputStream, inputStream));

                /* if request is to add accommodations, map specific data to every worker */
                if (msg.action().equals(Action.ADD))
                {
                    mapDifferentDataToAll(id, msg);
                }
                /* else map same request to all workers */
                else
                {
                    mapSameDataToAll(id, msg);
                }

                /* do not close connection with client */

            } else if (handshake.startsWith("REDUCER"))
            {
                long rid = msg.id();

                /* close connection with reducer */
                inputStream.close();
                outputStream.close();

                /* send data to client */
                sendDataToClient(rid, msg.parameters());

                /* remove connection from map */
                PARENT.removeConnection(rid);

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

    @SuppressWarnings("unchecked")
    private void mapDifferentDataToAll(long id, Message msg)
    {
        try
        {
            ArrayList<Accommodation> accommodations = (ArrayList<Accommodation>) msg.parameters();

            /* use a table of nodes to split equally accommodations to workers */
            Node[] nodesToStoreAcc = new Node[NODES.length];

            for (int i = 0; i < NODES.length; i++)
            {
                nodesToStoreAcc[i] = new Node(i);
            }

            for (Accommodation acc : accommodations)
            {
                int nodeID = Math.abs(acc.getAccName().hashCode() % NODES.length);
                nodesToStoreAcc[nodeID].arrayList.add(acc);
            }

            String ip;
            int port;

            for (int i = 0; i < NODES.length; i++)
            {
                Message message = new Message(id, Action.ADD, nodesToStoreAcc[i].arrayList);

                // send to main node
                ip = NODES[i].split(":")[0];
                port = Integer.parseInt(NODES[i].split(":")[1]);
                sendDataToWorker(ip, port, message);

                // send to replica
                ip = REPLICAS[i].split(":")[0];
                port = Integer.parseInt(REPLICAS[i].split(":")[1]);
                sendDataToWorker(ip, port, message);
            }

        } catch (Exception e)
        {
            System.err.println("Impossible casting while trying to map add request!");
            //e.printStackTrace();
        }
    }

    private void mapSameDataToAll(long id, Message msg)
    {
        Message message = new Message(id, msg.action(), msg.parameters());
        String ip;
        int port;
        boolean sent;

        for (int i = 0; i < NODES.length; i++)
        {
            // send to main nodes
            ip = NODES[i].split(":")[0];
            port = Integer.parseInt(NODES[i].split(":")[1]);
            sent = sendDataToWorker(ip, port, message);

            if (!sent || msg.action().equals(Action.BOOK) || msg.action().equals(Action.REVIEW))
            // send to replicas
            {
                ip = REPLICAS[i].split(":")[0];
                port = Integer.parseInt(REPLICAS[i].split(":")[1]);
                sendDataToWorker(ip, port, message);
            }
        }
    }

    private boolean sendDataToWorker(String ip, int port, Message msg)
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

            /* handshake with worker thread */
            out.writeObject("MASTER: Hello, WORKER!\n");
            out.flush();
            String handshake = (String) in.readObject();
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

    private void sendDataToClient(long id, Object data)
    {
        SocketSet socketSet = PARENT.getActiveConnectionById(id);
        outputStream = socketSet.OUTPUT;
        inputStream = socketSet.INPUT;

        try
        {
            outputStream.writeObject(data);
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