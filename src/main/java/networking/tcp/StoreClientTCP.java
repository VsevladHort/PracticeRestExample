package networking.tcp;

import packets.Constants;
import packets.abstractions.MessageWrapper;
import packets.abstractions.ReceivedPacket;
import packets.exceptions.DiscardException;
import packets.implementations.MessageWrapperImpl;
import packets.implementations.ReceivedPacketImpl;
import packets.utils.implementations.CRCCalculatorImplementation;
import packets.utils.implementations.CiphererSimpleImpl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StoreClientTCP {
    private Socket clientSocket;
    private OutputStream out;
    private BufferedInputStream in;
    private static int freeId = 0;
    private final int id;
    private long bPktId;
    private volatile boolean isTryingToConnect;
    private static final Logger LOGGER = Logger.getLogger(TCPClientHandler.class.getCanonicalName());
    private static final int MAX_RECONNECT_ATTEMPTS = 3;
    private static final int WAIT_BEFORE_RECONNECT_ATTEMPT = 10_000;
    private final MessageWrapper messageWrapper = new MessageWrapperImpl(CRCCalculatorImplementation.provide());

    public StoreClientTCP() {
        id = freeId++;
        bPktId = 0;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = clientSocket.getOutputStream();
        in = new BufferedInputStream(clientSocket.getInputStream());
    }

    /**
     * @return message in the form of ReceivedPacket, null if unsuccessful
     */
    public ReceivedPacket sendMessage(byte[] message, int cType, int bUserId) throws InterruptedException {
        isTryingToConnect = true;
        int reconnectTimes = 0;
        while (isTryingToConnect && reconnectTimes < MAX_RECONNECT_ATTEMPTS) {
            try {
                out.write(messageWrapper.wrap(message,
                        (byte) id, bPktId, cType, bUserId, new CiphererSimpleImpl()));
                break;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
                LOGGER.log(Level.INFO, "Trying to reconnect: % d".formatted(reconnectTimes));
                Thread.sleep(WAIT_BEFORE_RECONNECT_ATTEMPT);
                reconnectTimes++;
            }
        }
        reconnectTimes = 0;
        while (isTryingToConnect && reconnectTimes < MAX_RECONNECT_ATTEMPTS) {
            byte[] receivedMessage = new byte[0];
            try {
                receivedMessage = in.readAllBytes();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
                LOGGER.log(Level.INFO, "Trying to reconnect: % d".formatted(reconnectTimes));
                Thread.sleep(WAIT_BEFORE_RECONNECT_ATTEMPT);
                reconnectTimes++;
            }
            int startIndex = 0;
            for (int i = 0; i < receivedMessage.length; i++) {
                if (receivedMessage[i] == Constants.MAGIC) {
                    startIndex = i;
                    break;
                }
            }
            if (receivedMessage.length > 0)
                try {
                    return new ReceivedPacketImpl(receivedMessage, startIndex, new CiphererSimpleImpl());
                } catch (DiscardException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
        }
        return null;
    }

    public void stopConnection() throws IOException {
        isTryingToConnect = false;
        in.close();
        out.close();
        clientSocket.close();
    }
}
