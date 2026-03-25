package app;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TutorSystem ts = new TutorSystem();
        Scanner sc = new Scanner(System.in);

        while (true) {

            // AUTH MENU
            while (ts.userId == -1) {
                System.out.println("\n1.Register\n2.Login\n3.Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();

                switch (ch) {
                    case 1: ts.register(); break;
                    case 2: ts.login(); break;
                    case 3:
                        sc.close();
                        System.exit(0);
                    default: System.out.println("Invalid choice");
                }
            }

            // MAIN MENU AFTER LOGIN
            while (ts.userId != -1) {
                System.out.println("\n1.View Tutors by Subject\n2.Schedule Session\n3.Cancel Session\n4.View My Sessions\n5.Logout");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();

                switch (ch) {
                    case 1: ts.viewTutorsBySubject(); break;
                    case 2: ts.scheduleSession(); break;
                    case 3: ts.cancelSession(); break;
                    case 4: ts.viewMySessions(); break;
                    case 5:
                        ts.userId = -1;
                        System.out.println("Logged out!");
                        break;
                    default:
                        System.out.println("Invalid choice");
                }
            }
        }
    }
}