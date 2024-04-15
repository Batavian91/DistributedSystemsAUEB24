import global.Accommodation;
import global.DateRange;
import global.Pair;

import java.time.LocalDate;

public class Test
{

    public static void main(String[] args)
    {
        Accommodation acc = new Accommodation("n", 1, "k", 1,1,1);
        acc.addAvailableDates(new DateRange(LocalDate.parse("2024-04-01"), LocalDate.parse("2024-12-31")));
        acc.addAvailableDates(new DateRange(LocalDate.parse("2024-01-01"), LocalDate.parse("2024-03-31")));
        Pair<Integer, String> p1 = acc.addBookingDates(new DateRange(LocalDate.parse("2024-05-01"), LocalDate.parse("2024-05-31")));
        Pair<Integer, String> p2 = acc.addBookingDates(new DateRange(LocalDate.parse("2024-05-31"), LocalDate.parse("2024-05-31")));
        System.out.println(p1.getType2());
        System.out.println(p2.getType2());
    }

}