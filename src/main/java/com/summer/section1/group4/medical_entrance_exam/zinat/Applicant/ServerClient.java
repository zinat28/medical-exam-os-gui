package com.summer.section1.group4.medical_entrance_exam.zinat.Applicant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ServerClient - how the JavaFX app talks to the Exam Server (the same Java server
 * that the medsh shell controls with "exam start").
 *
 * Protocol (plain text over TCP): send ONE line, read the reply until the server closes.
 *   SEATS | BOOK <center> <name> | STATS | PING
 *
 * IMPORTANT: request() blocks while it waits for the network. Never call it on the
 * JavaFX Application Thread (the UI would freeze). The controllers call it from a
 * background thread and use Platform.runLater(...) to show the result.
 */
public class ServerClient {

    public static String host() {
        String h = System.getenv("EXAM_HOST");
        return (h == null || h.isBlank()) ? "127.0.0.1" : h;
    }

    public static int port() {
        for (String name : new String[]{"EXAM_PORT", "MEDSH_EXAM_PORT"}) {
            String v = System.getenv(name);
            if (v != null && v.matches("\\d{1,5}")) return Integer.parseInt(v);
        }
        return 5050;
    }

    /** The last connection error (empty when the last request worked). Shown in the GUI. */
    private static volatile String lastError = "";

    public static String lastError() {
        return lastError;
    }

    /** Sends one command line and returns the full reply (lines joined with \n). */
    public static String request(String line) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host(), port()), 4000);   // connect timeout
            socket.setSoTimeout(5000);                                     // read timeout

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(line);
            out.flush();
            // The server answers ONE line and then closes the connection, so we simply read
            // until the end of the stream. (No shutdownOutput(): not every forwarder passes it on.)

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder reply = new StringBuilder();
            String l;
            while ((l = in.readLine()) != null) {
                if (reply.length() > 0) reply.append('\n');
                reply.append(l);
            }
            if (reply.length() == 0) throw new IOException("server closed the connection without a reply");
            lastError = "";
            return reply.toString();
        } catch (IOException e) {
            lastError = e.toString();
            throw e;
        }
    }

    /** From a SEATS reply ("DHAKA  capacity=20 ...") returns the center codes: [DHAKA, CTG, RAJ]. */
    public static List<String> centerCodes(String seatsReply) {
        List<String> codes = new ArrayList<>();
        for (String line : seatsReply.split("\n")) {
            String t = line.trim();
            if (!t.isEmpty() && t.contains("capacity=")) codes.add(t.split("\\s+")[0]);
        }
        return codes;
    }

    /** From "OK DHAKA roll=M-100021 name=x" returns "M-100021" (or null). */
    public static String bookingRef(String okReply) {
        for (String part : okReply.split("\\s+"))
            if (part.startsWith("roll=")) return part.substring(5);
        return null;
    }
}
