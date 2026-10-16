package com.summer.section1.group4.medical_entrance_exam.zinat.Applicant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ApplicationSession is a SINGLETON.
 * <p>
 * Every screen (Form, Documents, Payment, Admit Card, Exam Center, Result,
 * Choice Locking, College Allocation) is loaded fresh by FXMLLoader, which means
 * every controller is a brand-new object with no memory of the others.
 * Since this project has no real database, this class plays that role:
 * it is the ONE object, shared by every controller, that remembers what the
 * applicant has done so far (Goal 1 -> Goal 8). This is exactly the kind of
 * "system memory" the CRA document keeps referring to.
 * <p>
 * How the Singleton pattern works here:
 *  - The constructor is private, so nobody outside this class can say "new ApplicationSession()".
 *  - There is exactly one instance, created once, held in a private static field.
 *  - Every controller reaches it the same way: ApplicationSession.getInstance().
 */
public class ApplicationSession {

    private static final ApplicationSession instance = new ApplicationSession();

    private ApplicationSession() {
    }

    public static ApplicationSession getInstance() {
        return instance;
    }

    public static final String[] MEDICAL_COLLEGES = {
            "Dhaka Medical College",
            "Sher-e-Bangla Medical College",
            "Chittagong Medical College",
            "Rajshahi Medical College",
            "Sylhet MAG Osmani Medical College",
            "Rangpur Medical College",
            "Mymensingh Medical College",
            "Comilla Medical College",
            "Khulna Medical College"
    };

    // ---------------------------------------------------------------
    // GOAL 1: Application Form Fill Up
    // ---------------------------------------------------------------
    private Applicant applicant;

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public boolean isFormSubmitted() {
        return applicant != null;
    }

    // ---------------------------------------------------------------
    // GOAL 2: Document Upload
    // ---------------------------------------------------------------
    private String identityFileName;
    private String sscTranscriptFileName;
    private String hscTranscriptFileName;
    private boolean documentsUploaded = false;

    public String getIdentityFileName() {
        return identityFileName;
    }

    public void setIdentityFileName(String identityFileName) {
        this.identityFileName = identityFileName;
    }

    public String getSscTranscriptFileName() {
        return sscTranscriptFileName;
    }

    public void setSscTranscriptFileName(String sscTranscriptFileName) {
        this.sscTranscriptFileName = sscTranscriptFileName;
    }

    public String getHscTranscriptFileName() {
        return hscTranscriptFileName;
    }

    public void setHscTranscriptFileName(String hscTranscriptFileName) {
        this.hscTranscriptFileName = hscTranscriptFileName;
    }

    public boolean isDocumentsUploaded() {
        return documentsUploaded;
    }

    public void setDocumentsUploaded(boolean documentsUploaded) {
        this.documentsUploaded = documentsUploaded;
    }

    // ---------------------------------------------------------------
    // GOAL 3: Payment
    // ---------------------------------------------------------------
    private boolean paymentCleared = false;
    private String paymentProofFileName;
    public static final double REGISTRATION_FEE = 15.00;

    public boolean isPaymentCleared() {
        return paymentCleared;
    }

    public void setPaymentCleared(boolean paymentCleared) {
        this.paymentCleared = paymentCleared;
    }

    public String getPaymentProofFileName() {
        return paymentProofFileName;
    }

    public void setPaymentProofFileName(String paymentProofFileName) {
        this.paymentProofFileName = paymentProofFileName;
    }

    // ---------------------------------------------------------------
    // GOAL 4: Admit Card Download (roll number is generated here, once,
    // and reused by every later goal")
    // ---------------------------------------------------------------
    private String rollNumber;
    private boolean admitCardGenerated = false;

    public String getRollNumber() {
        return rollNumber;
    }

    /** Generates a roll number the first time it's needed, then always returns the same one. */
    public String getOrCreateRollNumber() {
        if (rollNumber == null) {
            int randomSixDigits = 100000 + new Random().nextInt(900000);
            rollNumber = "M-" + randomSixDigits;
        }
        return rollNumber;
    }

    public boolean isAdmitCardGenerated() {
        return admitCardGenerated;
    }

    public void setAdmitCardGenerated(boolean admitCardGenerated) {
        this.admitCardGenerated = admitCardGenerated;
    }

    // ---------------------------------------------------------------
    // GOAL 5: Exam Center seat (booked on the exam SERVER, see ServerClient)
    // ---------------------------------------------------------------
    private String bookedCenter;
    private String bookingRef;

    public boolean isSeatBooked() {
        return bookedCenter != null;
    }

    public String getBookedCenter() {
        return bookedCenter;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setSeatBooking(String center, String ref) {
        this.bookedCenter = center;
        this.bookingRef = ref;
    }

    // ---------------------------------------------------------------
    // GOAL 6: Result
    // ---------------------------------------------------------------
    private boolean resultDeclared = false;
    private boolean passed = false;
    private int securedMarks;
    private int meritRank;
    public static final int PASSING_MARKS = 240;
    public static final int TOTAL_MARKS = 300;

    public boolean isResultDeclared() {
        return resultDeclared;
    }

    public boolean isPassed() {
        return passed;
    }

    public int getSecuredMarks() {
        return securedMarks;
    }

    public int getMeritRank() {
        return meritRank;
    }

    /** Simulates fetching exam records + computing a merit rank from the applicant's GPA. */
    public void declareResult() {
        if (applicant == null) return;
        double gpaAverage = (applicant.getSscGpa() + applicant.getHscGpa()) / 2.0;
        // Turn the GPA average (0-5.00) into a mark out of 300, plus a little randomness
        // so re-running the demo doesn't always look identical.
        int baseMarks = (int) Math.round((gpaAverage / 5.0) * TOTAL_MARKS);
        int noise = new Random().nextInt(21) - 10; // -10..+10
        securedMarks = Math.max(0, Math.min(TOTAL_MARKS, baseMarks + noise));
        passed = securedMarks >= PASSING_MARKS;
        meritRank = passed ? Math.max(1, (TOTAL_MARKS - securedMarks) * 7 + new Random().nextInt(15)) : -1;
        resultDeclared = true;
    }

    // ---------------------------------------------------------------
    // GOAL 7: Choice Locking
    // ---------------------------------------------------------------
    private final List<String> lockedChoices = new ArrayList<>();
    private boolean choicesLocked = false;

    public List<String> getLockedChoices() {
        return lockedChoices;
    }

    public boolean isChoicesLocked() {
        return choicesLocked;
    }

    public void lockChoices(List<String> choices) {
        lockedChoices.clear();
        lockedChoices.addAll(choices);
        choicesLocked = true;
    }

    // ---------------------------------------------------------------
    // GOAL 8: College Allocation
    // ---------------------------------------------------------------
    private String allocatedCollege;
    private boolean allocationDone = false;

    public String getAllocatedCollege() {
        return allocatedCollege;
    }

    public boolean isAllocationDone() {
        return allocationDone;
    }

    /** Simulates the DGME allotment-matrix engine: merit rank decides which locked choice is granted. */
    public void runAllocationEngine() {
        if (!choicesLocked || lockedChoices.isEmpty()) return;
        int index = Math.min(meritRank % lockedChoices.size(), lockedChoices.size() - 1);
        if (index < 0) index = 0;
        allocatedCollege = lockedChoices.get(index);
        allocationDone = true;
    }
}