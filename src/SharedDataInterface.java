/**
 * SharedDataInterface.java
 *
 * The SharedDataInterface is a remote interface that defines methods for
 * interacting with shared data in a distributed system using Java RMI (Remote
 * Method Invocation).
 *
 * @author Dr. Rami Sabouni,
 * Systems and Computer Engineering,
 * Carleton University
 * @version 1.0, March 11, 2024
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.DatagramPacket;


public interface SharedDataInterface extends Remote {

    /**
     * Retrieves a message from the shared data.
     *
     * @return a DatagramPacket representing the retrieved message.
     * @throws RemoteException if a communication-related exception occurs during the remote method invocation.
     */
    DatagramPacket getMessage() throws RemoteException;

    /**
     * Adds a message to the shared data.
     *
     * @param packet a DatagramPacket representing the message to be added to the shared data.
     * @throws RemoteException if a communication-related exception occurs during the remote method invocation.
     */
    void addMessage(DatagramPacket packet) throws RemoteException;
}
