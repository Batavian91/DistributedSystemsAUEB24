package global;

import java.io.Serializable;
import java.time.LocalDate;

public class DateRange implements iDateRange, Comparable<DateRange>, Serializable
{
    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public LocalDate getStartDate() { return this.startDate; }

    @Override
    public LocalDate getEndDate() { return this.endDate; }

    @Override
    public boolean isAcceptedDateRange(DateRange range)
    {
        return this.endDate.isBefore(range.startDate) || this.startDate.isAfter(range.endDate);
    }

    @Override
    public int compareTo(DateRange range)
    {
        if (range == null)
            return 1;
        if (this.startDate.isAfter(range.endDate))
            return 1;
        else if (this.endDate.isBefore(range.startDate))
            return -1;
        else
            return 0; //never used when isAcceptedDateRange() is called
    }

    public String toString()
    {
        return STR."\{getStartDate().toString()} : \{getEndDate().toString()}";
    }
}