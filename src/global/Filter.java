package global;

import java.io.Serializable;

public class Filter  implements Serializable
{
    public final String PARAM_AREA;
    public final DateRange PARAM_DATE;
    public final int PARAM_GUESTS;
    public final int PARAM_PRICE;
    public final int PARAM_STARS;

    public Filter(String PARAM_AREA, DateRange PARAM_DATE, int PARAM_GUESTS, int PARAM_PRICE, int PARAM_STARS)
    {
        this.PARAM_AREA = PARAM_AREA;
        this.PARAM_DATE = PARAM_DATE;
        this.PARAM_GUESTS = PARAM_GUESTS;
        this.PARAM_PRICE = PARAM_PRICE;
        this.PARAM_STARS = PARAM_STARS;
    }

    @Override
    public String toString()
    {
        return STR."Filter{PARAM_AREA='\{PARAM_AREA}\{'\''}, PARAM_DATE=\{PARAM_DATE}, PARAM_GUESTS=\{PARAM_GUESTS},"
                + STR."PARAM_PRICE=\{PARAM_PRICE}, PARAM_STARS=\{PARAM_STARS}\{'}'}";
    }
}