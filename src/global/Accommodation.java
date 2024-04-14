package global;

import java.io.Serializable;
import java.util.TreeSet;

public class Accommodation implements Serializable
{
    private final String accName;
    private int guests;
    private final String location;
    private int price;
    private int totalRating;
    private int noOfReviews;
    private String photo;
    protected TreeSet<DateRange> availableByOwnerDates = new TreeSet<>();
    public TreeSet<DateRange> bookedByVisitorDates = new TreeSet<>();

    public Accommodation(String accName, int guests, String location, int price, int totalRating, int noOfReviews)
    {
        this.accName = accName;
        this.guests = guests;
        this.location = location;
        this.price = price;
        this.totalRating = totalRating;
        this.noOfReviews = noOfReviews;
    }

    public String getAccName()
    {
        return accName;
    }

    public int getGuests()
    {
        return guests;
    }

    public String getLocation()
    {
        return location;
    }

    public int getPrice()
    {
        return price;
    }

    public int getRating()
    {
        if (noOfReviews == 0)
            return 0;
        return totalRating /noOfReviews;
    }

    public int getNoOfReviews()
    {
        return noOfReviews;
    }

    public String getPhoto()
    {
        return photo;
    }

    public void setGuests(int guests)
    {
        this.guests = guests;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public void addRating(int stars)
    {
        this.totalRating += stars;
        this.noOfReviews++;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    public void addAvailableDates(DateRange dateRange)
    {
        boolean acceptAccAddition = true;

        for (DateRange range : availableByOwnerDates)
        {
            if (!dateRange.isAcceptedDateRange(range))
            {
                acceptAccAddition = false;
                break;
            }
        }
        if (acceptAccAddition)
        {
            availableByOwnerDates.add(dateRange);
            //System.out.println("Accommodation was successfully added!");
        } else
        {
            System.out.println("The range you have chosen to add overlaps with existing dates!");
        }
    }

    public synchronized Pair<Integer, String> addBookingDates(DateRange dateRange)
    {
        boolean isAvailable = true;

        for (DateRange range : availableByOwnerDates)
        {
            if (!dateRange.isAcceptedDateRange(range))
            {
                isAvailable = false;
                break;
            }
        }

        if (!isAvailable)
        {
            return new Pair<>(1, "Not available for the dates chosen");
        }
        boolean isNotBooked = true;

        for (DateRange range : bookedByVisitorDates)
        {
            if (!dateRange.isAcceptedDateRange(range))
            {
                isNotBooked = false;
                break;
            }
        }
        if (isNotBooked)
        {
            bookedByVisitorDates.add(dateRange);
            return new Pair<>(2, "Successfully booked!");
        } else
        {
            return new Pair<>(3, "Already booked for the dates you have chosen!");
        }
    }

    public int countBookings(DateRange range)
    {
        int counter = 0;

        for (DateRange dtr : bookedByVisitorDates)
            if (!(dtr.getEndDate().isBefore(range.getStartDate())
                    || range.getEndDate().isBefore(dtr.getStartDate())))
                ++counter;

        return counter;
    }

}