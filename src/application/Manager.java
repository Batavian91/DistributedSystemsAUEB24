package application;

import accomodation.Accommodation;
import accomodation.DateRange;
import accomodation.JsonReader;

import java.util.ArrayList;

public class Manager
{
    public ArrayList<Accommodation> arrayList;
    private final String name;

    public Manager(String name)
    {
        this.name = name;
    }

    public void addAccommodation(String path)
    {
        arrayList = new JsonReader().readAccommodationsFromFile(path);
        //send the list to workers
    }

    public void addDates(Accommodation acc, DateRange range)
    {
        //DateRange range = new DateRange(new Date(123,10,10), new Date(123,11,11));
        acc.addAvailableDates(range);
    }

    public void printReservations()
    {
        System.out.println(STR."Reservations for manager: \{this.name}");
        // get reservations from workers
    }
}