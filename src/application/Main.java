package application;

import accomodation.Accommodation;
import accomodation.DateRange;

import java.util.Date;

public class Main
{
    public static void main(String[] args)
    {
        Manager manager = new Manager("Nick");
        String path = STR."\{System.getProperty("user.dir")}\\resources\\testRoom.json";
        manager.addAccommodation(path);
        for (Accommodation acc : manager.arrayList)
        {   manager.addDates(acc, new DateRange(new Date(123,10,10), new Date(123,11,11)));
            //System.out.println(acc.availableByOwnerDates);}
        }
    }
}