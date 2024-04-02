package accomodation;

import java.util.Date;

public interface iDateRange
{
    Date getStartDate();

    Date getEndDate();

    boolean isAcceptedDateRange(DateRange range);

    int compareTo(DateRange range);
}