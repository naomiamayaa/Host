/**
 * SharedDataImpl.java
 *
 * The SharedDataImpl class is an implementation of the SharedDataInterface
 * remote interface. It provides methods to interact with shared data in a
 * distributed system using Java RMI (Remote Method Invocation).
 *
 * @author Dr. Rami Sabouni,
 * Systems and Computer Engineering,
 * Carleton University
 * @version 1.0, March 11, 2024
 */
import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;


public class SharedDataImpl extends UnicastRemoteObject implements SharedDataInterface {

    /** The queue to store messages as shared data. */
    private Queue<DatagramPacket> messages;

    /**
     * Constructs a new SharedDataImpl instance. Initializes the message queue.
     *
     * @throws RemoteException if a communication-related exception occurs during the remote object export.
     */
    public SharedDataImpl() throws RemoteException {
        super();
        this.messages = new LinkedList<>();
    }

    /**
     * Retrieves a message from the shared data.
     *
     * @return a String representing the retrieved message.
     * @throws RemoteException if a communication-related exception occurs during the remote method invocation.
     */
    @Override
    public synchronized DatagramPacket getMessage() throws RemoteException {
        return messages.remove();
    }

    /**
     * Adds a message to the shared data.
     *
     * @param packet a DatagramPacket representing the message to be added to the shared data.
     * @throws RemoteException if a communication-related exception occurs during the remote method invocation.
     */
    @Override
    public synchronized void addMessage(DatagramPacket packet) throws RemoteException {
        messages.add(packet);
    }
}
