package accomodation;

import java.time.LocalDate;

public class DateRange implements iDateRange, Comparable<DateRange>
{
    protected LocalDate startDate;
    protected LocalDate endDate;

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
        return this.endDate.compareTo(range.startDate) <= 0 || this.startDate.compareTo(range.endDate) >= 0;
    }

    @Override
    public int compareTo(DateRange range)
    {
        if (range == null)
            return 1;
        if (this.startDate.compareTo(range.endDate) >= 0)
            return 1;
        else if (this.endDate.compareTo(range.startDate) <= 0)
            return -1;
        else
            return 0; //never used when isAcceptedDateRange() is called
    }
}