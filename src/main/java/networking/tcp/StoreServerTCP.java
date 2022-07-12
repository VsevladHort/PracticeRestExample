package networking.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StoreServerTCP implements Runnable {
    private final ServerSocket serverSocket;
    private final Set<Thread> runningThreads;
    private final List<TCPClientHandler> runningHandlers;
    private volatile boolean isRunning = true;
    private static final Logger LOGGER = Logger.getLogger(StoreServerTCP.class.getCanonicalName());

    public StoreServerTCP(int port) throws IOException {
        runningThreads = new HashSet<>();
        runningHandlers = new ArrayList<>();
        serverSocket = new ServerSocket(port);
    }

    public void start() throws IOException {
        while (isRunning) {
            var handler = new TCPClientHandler(serverSocket.accept(), this);
            runningHandlers.add(handler);
            Thread thread = new Thread(handler);
            runningThreads.add(thread);
            thread.start();
        }
    }

    public void stop() throws IOException {
        synchronized (this) {
            isRunning = false;
            serverSocket.close();
            runningHandlers.forEach(it -> {
                try {
                    it.stop();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
            });
            runningThreads.clear();
        }
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    public Set<Thread> getRunningThreads() {
        return runningThreads;
    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }
}