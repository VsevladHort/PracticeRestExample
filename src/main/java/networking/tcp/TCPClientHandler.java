package networking.tcp;

import homework_processing.implementations.ReceiverImpl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPClientHandler implements Runnable {
    private final Socket clientSocket;
    private final OutputStreamWriter outputStreamWriter;
    private final BufferedInputStream bufferedInputStream;
    private final StoreServerTCP parent;
    private volatile boolean isConnected;
    private static final Logger LOGGER = Logger.getLogger(TCPClientHandler.class.getCanonicalName());

    public TCPClientHandler(Socket accept, StoreServerTCP parent) throws IOException {
        clientSocket = accept;
        outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
        bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
        this.parent = parent;
        isConnected = true;
    }

    @Override
    public void run() {
        while (parent.getIsRunning() && isConnected) {
            try {
                new ReceiverImpl(bufferedInputStream.readAllBytes()).receiveMessage();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    public void stop() throws IOException {
        isConnected = false;
        outputStreamWriter.close();
        bufferedInputStream.close();
        clientSocket.close();
    }
}
