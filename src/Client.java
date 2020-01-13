/**
 * One computer is the client and the other is the server.
 * The client will be a personal computer.
 * The server is a public server so anyone can access it but
 * in the client we don't want everyone to access it so we need
 * extra security. This is shown in the constructor.
 */

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

    private JTextField textBox;
    private JTextArea chatWindow;
    // Stream flowing out.
    private ObjectOutputStream outputStream;
    // Stream flowing in.
    private ObjectInputStream inputStream;
    private String message = "";
    private String serverIP;
    private Socket socket;

    public Client (String host) {
        // Create a new initially invisible frame with the title client.
        super("Client");
        serverIP = host;
        textBox = new JTextField();
        textBox.setEditable(false);
        // Used whenever you click on the button or menu item or press enter.
        textBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Send what is typed into the message bar.
                sendMessage(e.getActionCommand());
                // Set the text to blank when the message has been sent.
                textBox.setText("");
            }
        });
        add(textBox, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        chatWindow.setEditable(false);
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 300);
        setVisible(true);
    }

    // Connect to server.
    protected void startRunning() throws IOException {
        try {
            connectToServer();
            setUpStreams();
            whileChatting();
        } catch (EOFException e) {
            showMessage("\nClient terminated the connection.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeEverything();
        }
    }

    // Connect to the server using the socket class. Sockets are made of an IP address and a port.
    private void connectToServer() throws IOException {
        showMessage("Connecting to server... \n");
        socket = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("You are connected to " + socket.getInetAddress().getHostName());
    }

    // Set up streams to send and receive messages.
    private void setUpStreams() throws IOException {
        // Create output stream object that writes to the specified output stream in the constructor.
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        // Wait for the content of the stream to be completely transferred to its destination.
        outputStream.flush();
        // Create an input stream object that reads from the specified input stream in the constructor.
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    // While chatting with the server.
    private void whileChatting() throws IOException {
        ableToType(true);
        do {
            try {
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("\n I don't know that object type");
            }
        } while (!message.equals("SERVER - END"));
    }

    private void closeEverything() throws IOException {
        showMessage("Closing everything down.");
        ableToType(false);
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            showMessage("Could not close down successfully.");
        }
    }

    // Send messages to the server.
    private void sendMessage(String message) {
        try {
            outputStream.writeObject("CLIENT - " + message);
            outputStream.flush();
        } catch (IOException e) {
            chatWindow.append("\nSomething went wrong with sending message.");
        }
    }

    // Update chat window.
    private void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.append(message);
            }
        });
    }

    // User can type into text box or not.
    private void ableToType(final Boolean canType) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textBox.setEditable(canType);
            }
        });
    }

}
