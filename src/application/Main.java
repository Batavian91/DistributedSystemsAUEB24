package application;

import global.DateRange;
import global.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        String master = "127.0.0.1:4321";
        BookingApp app = new BookingApp(master);

        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        String exit;
        int choice;
        int mChoice;
        int vChoice;
        int v2Choice;

        System.out.println("Welcome to PHILOXENIA!");

        while (loop)
        {
            // request user input - accept specific values
            do
            {
                displayUserChoices();
                choice = scanner.nextInt();
            } while (choice != 0 && choice != 1 && choice != 2);

            // manager operations
            if (choice == 1)
            {
                do
                {
                    displayManagerChoices();
                    mChoice = scanner.nextInt();
                } while (mChoice != 0 && mChoice != 1 && mChoice != 2);

                // add accommodations
                if (mChoice == 1)
                {
                    String path = STR."\{System.getProperty("user.dir")}\\resources\\testRoom.json";
                    app.add(path);
                }
                // print reservations
                else if (mChoice == 2)
                {
                    DateRange dtRange = new DateRange(LocalDate.MIN, LocalDate.MAX);
                    app.print(dtRange);
                }
            }
            // visitor operations
            else if (choice == 2)
            {
                do
                {
                    displayVisitorChoices();
                    vChoice = scanner.nextInt();
                } while (vChoice != 0 && vChoice != 1);

                // search
                if (vChoice == 1)
                {
                    do
                    {
                        displayExtraVisitorChoices();
                        v2Choice = scanner.nextInt();
                    } while (v2Choice != 0 && v2Choice != 1 && v2Choice != 2);

                    if (v2Choice == 1)
                    {
                        System.out.println("book");
                        //TODO app.book;
                    } else if (v2Choice == 2)
                    {
                        //TODO app.review;
                        System.out.println("review");
                    }
                }
            }

            // continue or exit
            System.out.println("Do you wish to exit the app? (Y/N)");
            do
            {
                exit = scanner.nextLine();
            } while (!(exit.equalsIgnoreCase("Y") || exit.equalsIgnoreCase("N")));

            loop = exit.equalsIgnoreCase("N");
        }
    }

    private static void displayUserChoices()
    {
        System.out.println("Choose type of user:");
        System.out.println("0. EXIT");
        System.out.println("1. MANAGER");
        System.out.println("2. VISITOR");
    }

    private static void displayManagerChoices()
    {
        System.out.println("Choose an option:");
        System.out.println("0. EXIT");
        System.out.println("1. ADD ROOMS");
        System.out.println("2. PRINT RESERVATIONS");
    }

    private static void displayVisitorChoices()
    {
        System.out.println("Choose an option:");
        System.out.println("0. EXIT");
        System.out.println("1. SEARCH");
    }

    private static void displayExtraVisitorChoices()
    {
        System.out.println("Choose an operation:");
        System.out.println("0. EXIT");
        System.out.println("1. BOOK");
        System.out.println("2. REVIEW");
    }
}