package app;

import java.sql.*;
import java.util.Scanner;

public class TutorSystem {

    Scanner sc = new Scanner(System.in);
    Connection con = DBConnection.getConnection();
    int userId = -1;

    // REGISTER
    public void register() {
        try {
            System.out.print("Enter name: ");
            String name = sc.next();
            System.out.print("Enter password: ");
            String pass = sc.next();

            String q = "INSERT INTO users(name,password) VALUES(?,?)";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, name);
            ps.setString(2, pass);
            ps.executeUpdate();

            System.out.println("Registered Successfully!");
        } catch (Exception e) {
            System.out.println("Error in registration");
        }
    }

    // LOGIN
    public void login() {
        try {
            System.out.print("Enter name: ");
            String name = sc.next();
            System.out.print("Enter password: ");
            String pass = sc.next();

            String q = "SELECT * FROM users WHERE name=? AND password=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, name);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("id");
                System.out.println("Login Successful!");
            } else {
                System.out.println("Invalid credentials!");
            }

        } catch (Exception e) {
            System.out.println("Error in login");
        }
    }

    // VIEW TUTORS BY SUBJECT
    public void viewTutorsBySubject() {
        try {
            System.out.print("Enter subject: ");
            String subject = sc.next();

            String q = "SELECT * FROM tutors WHERE subject=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setString(1, subject);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                    "ID: " + rs.getInt("tutor_id") +
                    " | Name: " + rs.getString("tutor_name") +
                    " | Slots: " + rs.getInt("available_slots")
                );
            }

            if (!found) {
                System.out.println("No tutors found for this subject!");
            }

        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    // SCHEDULE SESSION
    public void scheduleSession() {
        if (userId == -1) {
            System.out.println("Login first!");
            return;
        }

        try {
            System.out.print("Enter subject: ");
            String subject = sc.next();

            System.out.print("Enter tutor ID: ");
            int tutorId = sc.nextInt();

            System.out.print("Enter time slot (Morning/Afternoon/Evening): ");
            String slot = sc.next();

            // Check tutor + subject
            String checkTutor = "SELECT available_slots FROM tutors WHERE tutor_id=? AND subject=?";
            PreparedStatement ps = con.prepareStatement(checkTutor);
            ps.setInt(1, tutorId);
            ps.setString(2, subject);

            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {

                String insert = "INSERT INTO sessions(user_id,tutor_id,subject,time_slot,session_date) VALUES(?,?,?,?,CURDATE())";
                PreparedStatement ps2 = con.prepareStatement(insert);
                ps2.setInt(1, userId);
                ps2.setInt(2, tutorId);
                ps2.setString(3, subject);
                ps2.setString(4, slot);
                ps2.executeUpdate();

                String update = "UPDATE tutors SET available_slots=available_slots-1 WHERE tutor_id=?";
                PreparedStatement ps3 = con.prepareStatement(update);
                ps3.setInt(1, tutorId);
                ps3.executeUpdate();

                System.out.println("Session Scheduled!");

            } else {
                System.out.println("Invalid tutor or no slots available!");
            }

        } catch (Exception e) {
            System.out.println("Error scheduling session");
        }
    }

    // CANCEL SESSION
    public void cancelSession() {
        try {
            System.out.print("Enter session ID: ");
            int id = sc.nextInt();

            String get = "SELECT tutor_id FROM sessions WHERE session_id=? AND user_id=?";
            PreparedStatement ps = con.prepareStatement(get);
            ps.setInt(1, id);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int tutorId = rs.getInt(1);

                String del = "DELETE FROM sessions WHERE session_id=?";
                PreparedStatement ps2 = con.prepareStatement(del);
                ps2.setInt(1, id);
                ps2.executeUpdate();

                String update = "UPDATE tutors SET available_slots=available_slots+1 WHERE tutor_id=?";
                PreparedStatement ps3 = con.prepareStatement(update);
                ps3.setInt(1, tutorId);
                ps3.executeUpdate();

                System.out.println("Session Cancelled!");
            } else {
                System.out.println("Invalid session ID!");
            }

        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    // VIEW MY SESSIONS
    public void viewMySessions() {
        try {
            String q = "SELECT * FROM sessions WHERE user_id=?";
            PreparedStatement ps = con.prepareStatement(q);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                    "Session ID: " + rs.getInt("session_id") +
                    " | Tutor ID: " + rs.getInt("tutor_id") +
                    " | Subject: " + rs.getString("subject") +
                    " | Slot: " + rs.getString("time_slot") +
                    " | Date: " + rs.getDate("session_date")
                );
            }

            if (!found) {
                System.out.println("No sessions found!");
            }

        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}