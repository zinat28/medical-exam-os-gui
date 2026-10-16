package com.summer.section1.group4.medical_entrance_exam.zinat.Applicant;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class ExamCenterController {
    @FXML
    private Label CenterAllocationLabel;
    @FXML
    private TextArea ExamCenterDetailsTA;

    // ---- NEW: live data from the exam server ----
    @FXML
    private TextArea SeatsTA;                 // live seat table (refreshes every 2 s)
    @FXML
    private ComboBox<String> CenterChoiceCB;  // centers reported by the server
    @FXML
    private Button BookSeatBTN;
    @FXML
    private Label ServerStatusLabel;          // "online" / "offline"

    private Timeline poller;                  // repeats every 2 seconds on the UI thread
    private boolean wasAttached = false;      // true once this screen has been shown

    // Mock "center mapping index" master data — District -> Venue -> Room pool.
    private static final String[][] CENTER_MAP = {
            {"Dhaka", "Dhaka Medical College Exam Hall"},
            {"Chattogram", "Chattogram Medical College Exam Hall"},
            {"Rajshahi", "Rajshahi Medical College Exam Hall"},
            {"Sylhet", "Sylhet MAG Osmani Medical College Exam Hall"},
            {"Khulna", "Khulna Medical College Exam Hall"},
            {"Rangpur", "Rangpur Medical College Exam Hall"}
    };

    @FXML
    public void initialize() {
        CenterChoiceCB.getItems().setAll("DHAKA", "CTG", "RAJ");   // <- add this line
        loadCenterAllocation();
        showBookingState();
        startSeatPolling();
    }

    @FXML
    public void ReVerifyCenterAllocationOA(ActionEvent actionEvent) {
        loadCenterAllocation();
        showBookingState();
        refreshSeatsAsync();
    }

    // =====================================================================
    // Talking to the exam server.
    // Rule: network calls run on a BACKGROUND thread, and the result is handed
    // back to the UI thread with Platform.runLater(...). If the network call ran
    // on the UI thread, the whole window would freeze while waiting.
    // =====================================================================

    private void startSeatPolling() {
        refreshSeatsAsync();
        poller = new Timeline(new KeyFrame(Duration.seconds(2), e -> pollTick()));
        poller.setCycleCount(Timeline.INDEFINITE);
        poller.play();
    }

    private void pollTick() {
        if (SeatsTA.getScene() != null) {
            wasAttached = true;
        } else if (wasAttached) {          // the screen was replaced by another one: stop polling
            poller.stop();
            return;
        }
        refreshSeatsAsync();
    }

    private void refreshSeatsAsync() {
        Thread t = new Thread(() -> {
            String reply;
            try {
                reply = ServerClient.request("SEATS");
            } catch (IOException ex) {
                reply = null;
            }
            final String result = reply;
            Platform.runLater(() -> showSeats(result));
        }, "seat-refresh");
        t.setDaemon(true);
        t.start();
    }

    private void showSeats(String reply) {
        String where = ServerClient.host() + ":" + ServerClient.port();
        if (reply == null) {
            SeatsTA.setText("Exam server is offline.\n\nStart it from the medsh shell:\n    exam start\n\nReason: "
                    + ServerClient.lastError());
            ServerStatusLabel.setStyle("-fx-text-fill: #cc0000;");
            ServerStatusLabel.setText("● Exam server OFFLINE (" + where + ")");
            return;
        }
        SeatsTA.setText(reply);
        ServerStatusLabel.setStyle("-fx-text-fill: #068053;");
        ServerStatusLabel.setText("● Exam server ONLINE (" + where + ")");

        List<String> codes = ServerClient.centerCodes(reply);
        if (!codes.isEmpty() && !codes.equals(CenterChoiceCB.getItems())) {
            String selected = CenterChoiceCB.getValue();
            CenterChoiceCB.getItems().setAll(codes);
            if (selected != null && codes.contains(selected)) CenterChoiceCB.setValue(selected);
        }
    }

    @FXML
    public void BookSeatOA(ActionEvent actionEvent) {
        ApplicationSession session = ApplicationSession.getInstance();

        if (session.isSeatBooked()) {
            setStatus("STATUS: You already hold a seat at " + session.getBookedCenter()
                    + " (ref " + session.getBookingRef() + ").", false);
            return;
        }
        final String center = CenterChoiceCB.getValue();
        if (center == null || center.isEmpty()) {
            setStatus("STATUS: Please choose an exam center first.", false);
            return;
        }

        final String name = session.getApplicant() != null
                ? session.getApplicant().getApplicantName() : "guest";

        BookSeatBTN.setDisable(true);           // no double clicks while we wait
        setStatus("STATUS: Booking a seat at " + center + " ...", true);

        Thread t = new Thread(() -> {
            String reply;
            try {
                reply = ServerClient.request("BOOK " + center + " " + name.replaceAll("\\s+", " "));
            } catch (IOException ex) {
                reply = null;
            }
            final String result = reply;
            Platform.runLater(() -> onBookReply(center, result));
        }, "seat-booking");
        t.setDaemon(true);
        t.start();
    }

    private void onBookReply(String center, String reply) {
        ApplicationSession session = ApplicationSession.getInstance();

        if (reply == null) {
            BookSeatBTN.setDisable(false);
            setStatus("STATUS: Cannot reach the exam server. Start it with:  exam start", false);
        } else if (reply.startsWith("OK")) {
            String ref = ServerClient.bookingRef(reply);
            session.setSeatBooking(center, ref);
            CenterChoiceCB.setDisable(true);
            showBookingState();
        } else if (reply.startsWith("FULL")) {
            BookSeatBTN.setDisable(false);
            setStatus("STATUS: " + center + " is FULL. Please choose another center.", false);
        } else {
            BookSeatBTN.setDisable(false);
            setStatus("STATUS: Server replied: " + reply, false);
        }
        refreshSeatsAsync();
    }

    /** Shows the confirmed seat (if any) and locks the Book button. */
    private void showBookingState() {
        ApplicationSession session = ApplicationSession.getInstance();
        if (session.isSeatBooked()) {
            BookSeatBTN.setDisable(true);
            CenterChoiceCB.setDisable(true);
            CenterChoiceCB.setValue(session.getBookedCenter());
            setStatus("STATUS: Seat CONFIRMED at " + session.getBookedCenter()
                    + "  (booking ref " + session.getBookingRef() + ").", true);
        }
    }

    private void setStatus(String text, boolean ok) {
        CenterAllocationLabel.setStyle("-fx-text-fill: " + (ok ? "#068053" : "#cc0000") + ";");
        CenterAllocationLabel.setText(text);
    }

    // =====================================================================
    // Original offline logic (unchanged): works even when the server is down.
    // =====================================================================

    private void loadCenterAllocation() {
        ApplicationSession session = ApplicationSession.getInstance();

        if (session.getRollNumber() == null) {
            ExamCenterDetailsTA.setText("");
            CenterAllocationLabel.setText("STATUS: No roll number on file. Complete Goal 4 (Admit Card) first.");
            return;
        }

        String roll = session.getRollNumber();

        int index = Math.abs(roll.hashCode()) % CENTER_MAP.length;
        String district = CENTER_MAP[index][0];
        String venue = CENTER_MAP[index][1];
        int seatNumber = 100 + (Math.abs(roll.hashCode()) % 400);


        StringBuilder slip = new StringBuilder();
        slip.append("===== OFFICIAL MEDICAL ENTRANCE EXAMINATION ENTRY SLIP =====\n");
        slip.append("Roll Number      : ").append(roll).append("\n");
        slip.append("District         : ").append(district).append("\n");
        slip.append("Venue Institute  : ").append(venue).append("\n");
        slip.append("Seat Number      : ").append(seatNumber).append("\n");
        slip.append("==============================================================\n");
        ExamCenterDetailsTA.setText(slip.toString());


        CenterAllocationLabel.setText("Status: Center credentials compiled and loaded cleanly.");
    }
}
