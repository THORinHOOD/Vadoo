package catwithbowtie.chatchat.Fragments;

import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
    public static String SERVER_IP ; //server IP address
    public static int SERVER_PORT;

    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, String ip, int port) {
        mMessageListener = listener;
        this.SERVER_IP = ip;
        this.SERVER_PORT = port;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        Log.d("TCP", "i was here 3");
        if (mBufferOut == null) {
            Log.d("TCP", "null");
        }
        if (mBufferOut != null && !mBufferOut.checkError()) {
            Log.d("TCP", "send");
            try {
                mBufferOut.print(message);
            } catch(Exception e) {

            }
            mBufferOut.flush();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        mRun = false;
        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }
        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run(String login) throws IOException {

        mRun = true;

        try {

            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            try {
                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), false);
                if (mBufferOut == null) {
                    Log.d("TCP", "mBufferOut null");
                } else {
                    Log.d("TCP", "mBufferOut not null");
                }
                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if (mBufferIn == null) {
                    Log.d("TCP", "mBufferIn null");
                } else {
                    Log.d("TCP", "mBufferIn not null");
                }

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    Log.d("TCP", "preMRun");

                    try {
                        //Log.d("TCP", mServerMessage.toString());
                       // Log.d("TCP", mBufferIn.toString());
                       // Log.d("TCP", mMessageListener.toString());
                        mServerMessage = mBufferIn.readLine();
                        if (mServerMessage != null && mMessageListener != null) {
                            //call the method messageReceived from MyActivity class
                            Log.d("TCP", "mRunIn");
                            mMessageListener.messageReceived(mServerMessage);
                        }
                    } catch(Exception e) {
                        Log.d("TCP", e.toString());
                    }
                }

            } catch (Exception e) {
                Log.d("TCP", e.toString());
            } finally {
                socket.close();
            }
        } catch(UnknownHostException e) {
           Log.d("TCP", e.toString());
        }
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}