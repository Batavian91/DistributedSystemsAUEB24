package application;

import global.DateRange;
import global.Filter;
import global.Pair;
import global.Room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class BookingApp
{
    public BookingApp()
    {
        System.out.println("Welcome to PHILOXENIA!");
    }

    public static void main(String[] args)
    {
        BookingApp dummy = new BookingApp();

        String master = "127.0.0.1:4321";
        BookingAgent agent = new BookingAgent(master);

        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        String exit;
        int choice;
        int mChoice;
        int vChoice;
        int v2Choice;

        while (loop)
        {
            // request user input - accept specific values
            do
            {
                dummy.displayUserChoices();
                choice = scanner.nextInt();
            } while (choice != 0 && choice != 1 && choice != 2);

            // manager operations
            if (choice == 1)
            {
                do
                {
                    dummy.displayManagerChoices();
                    mChoice = scanner.nextInt();
                } while (mChoice != 0 && mChoice != 1 && mChoice != 2);

                // add accommodations
                if (mChoice == 1)
                {
                    String path = STR."\{System.getProperty("user.dir")}\\resources\\testRoom.json";
                    agent.add(path);
                }
                // print reservations
                else if (mChoice == 2)
                {
                    DateRange dtRange = new DateRange(LocalDate.MIN, LocalDate.MAX);
                    agent.print(dtRange);
                }
            }
            // visitor operations
            else if (choice == 2)
            {
                do
                {
                    dummy.displayVisitorChoices();
                    vChoice = scanner.nextInt();
                } while (vChoice != 0 && vChoice != 1);

                // search
                if (vChoice == 1)
                {
                    Filter filter = dummy.filter();
                    ArrayList<Room> rooms = agent.search(filter);

                    if (rooms == null)
                    {
                        System.out.println("\nNo rooms were found!");
                    } else
                    {
                        // display rooms
                        int index = 0;
                        for (Room room : rooms)
                            System.out.println(STR."\{index++}: \{room}");

                        do
                        {
                            dummy.displayExtraVisitorChoices();
                            v2Choice = scanner.nextInt();
                        } while (v2Choice != 0 && v2Choice != 1 && v2Choice != 2);

                        // book
                        if (v2Choice == 1)
                        {
                            System.out.println("\nChoose a room to book...");
                            int r = scanner.nextInt();

                            System.out.println("Enter dates...");
                            LocalDate dt1 = LocalDate.parse(scanner.nextLine());
                            LocalDate dt2 = LocalDate.parse(scanner.nextLine());
                            DateRange range = new DateRange(dt1, dt2);

                            String name = rooms.get(--r).NAME;
                            Pair<String, DateRange> room = new Pair<>(name, range);

                            agent.book(room);

                        }
                        // review
                        else if (v2Choice == 2)
                        {
                            System.out.println("\nChoose a room to review...");
                            int r = scanner.nextInt();

                            System.out.println("Enter stars (1,2,3,4,5)...");
                            int stars = scanner.nextInt();

                            String name = rooms.get(--r).NAME;
                            Pair<String, Integer> room = new Pair<>(name, stars);

                            agent.review(room);
                        }
                    }
                }
            }

            // continue or exit
            System.out.println("\nDo you wish to exit the app? (Y/N)");
            do
            {
                exit = scanner.nextLine();
            } while (!(exit.equalsIgnoreCase("Y") || exit.equalsIgnoreCase("N")));

            loop = exit.equalsIgnoreCase("N");
        }
    }

    private void displayUserChoices()
    {
        System.out.println("\nChoose type of user:");
        System.out.println("0. EXIT");
        System.out.println("1. MANAGER");
        System.out.println("2. VISITOR");
    }

    private void displayManagerChoices()
    {
        System.out.println("\nChoose an option:");
        System.out.println("0. EXIT");
        System.out.println("1. ADD ROOMS");
        System.out.println("2. PRINT RESERVATIONS");
    }

    private void displayVisitorChoices()
    {
        System.out.println("\nChoose an option:");
        System.out.println("0. EXIT");
        System.out.println("1. SEARCH");
    }

    private void displayExtraVisitorChoices()
    {
        System.out.println("\nChoose an operation:");
        System.out.println("0. EXIT");
        System.out.println("1. BOOK");
        System.out.println("2. REVIEW");
    }

    private Filter filter()
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Insert area...");
        String area = scan.nextLine();
        System.out.println("Insert dates...");
        LocalDate dt1 = LocalDate.parse(scan.nextLine());
        LocalDate dt2 = LocalDate.parse(scan.nextLine());
        DateRange range = new DateRange(dt1, dt2);
        System.out.println("Insert number of guests...");
        int guests = scan.nextInt();
        System.out.println("Insert price...");
        int price = scan.nextInt();
        System.out.println("Insert stars...");
        int stars = scan.nextInt();
        return new Filter(area, range, guests, price, stars);
    }
}