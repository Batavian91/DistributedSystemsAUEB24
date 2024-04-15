package application;

import global.DateRange;
import global.Filter;
import global.Pair;
import global.Room;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        int m2Choice;
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
                    do
                    {
                        dummy.displayExtraManagerChoices();
                        m2Choice = scanner.nextInt();
                    } while (m2Choice != 0 && m2Choice != 1 && m2Choice != 2);

                    // print all
                    if (m2Choice == 1)
                    {
                        agent.printAll();
                    }
                    //print by area, date
                    else if (m2Choice == 2)
                    {
                        DateRange dtRange = new DateRange(LocalDate.parse("2024-04-01"), LocalDate.parse("2024-12-31"));
                        agent.print(dtRange);
                    }
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

                    if (rooms == null || rooms.isEmpty())
                    {
                        System.out.println("\nNo rooms were found!");
                    } else
                    {
                        // display rooms
                        int index = 0;
                        for (Room room : rooms)
                            System.out.println(STR."\{++index}: \{room}");

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
                            DateRange range = dummy.insertValidDates();

                            String name = rooms.get(--r).NAME;
                            Pair<String, DateRange> room = new Pair<>(name, range);

                            agent.book(room);

                        }
                        // review
                        else if (v2Choice == 2)
                        {
                            System.out.println("\nChoose a room to review...");
                            int r = scanner.nextInt();

                            int stars;
                            do
                            {
                                System.out.println("\nEnter stars (1,2,3,4,5)...");
                                stars = scanner.nextInt();
                            } while (stars < 1 || stars > 5);

                            String name = rooms.get(--r).NAME;
                            Pair<String, Integer> room = new Pair<>(name, stars);

                            agent.review(room);
                        }
                    }
                }
            }

            // continue or exit
            do
            {
                System.out.println("Do you wish to exit the app? (Y/N)");
                exit = scanner.next();
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

    private void displayExtraManagerChoices()
    {
        System.out.println("\nChoose an operation:");
        System.out.println("0. EXIT");
        System.out.println("1. PRINT ALL BOOKINGS");
        System.out.println("2. PRINT BY AREA, DATE");
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

    public Filter filter()
    {
        Scanner scan = new Scanner(System.in);

        System.out.println("\nEnter the desired area or press Enter to continue...");
        String area = scan.nextLine();

        String answer;
        DateRange range = new DateRange(LocalDate.parse("2024-04-01"), LocalDate.parse("2024-12-31"));
        do
        {
            System.out.println("Would you like to apply a date filter? (Y/N)");
            answer = scan.next();
        } while (!(answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("N")));

        if (answer.equalsIgnoreCase("Y"))
            range= insertValidDates();

        int guests = 0;
        System.out.println("Enter number of guests...");
        if (scan.hasNextInt())
        {
            guests = scan.nextInt();
        }

        int price = 0;
        System.out.println("Enter price...");
        if (scan.hasNextInt())
        {
            price = scan.nextInt();
        }

        int stars = 0;
        System.out.println("Enter stars (1,2,3,4,5)...");
        if (scan.hasNextInt())
        {
            stars = scan.nextInt();
            stars = stars < 1 || stars > 5 ? 0 : stars;
        }

        return new Filter(area, range, guests, price, stars);
    }

    private DateRange insertValidDates()
    {
        Scanner scan = new Scanner(System.in);
        DateRange range;

        do
        {
            System.out.println("Insert dates... (YYYY-MM-DD)");
            try
            {
                LocalDate dt1 = LocalDate.parse(scan.nextLine());
                LocalDate dt2 = LocalDate.parse(scan.nextLine());

                if (dt2.isBefore(dt1))
                {
                    System.out.println("Departure date cannot precede the arrival date!");
                } else if (dt1.isBefore(LocalDate.now()) || dt2.isBefore(LocalDate.now()))
                {
                    System.out.println("Past dates are not valid!");
                } else
                {
                    range = new DateRange(dt1, dt2);
                    break;
                }
            } catch (DateTimeParseException e)
            {
                System.out.println("Date format appears to be invalid! Re-enter dates in the correct format... (YYYY-MM-DD)");
            }
        } while (true);

        return range;
    }

}