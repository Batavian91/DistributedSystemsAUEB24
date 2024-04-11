package global;

import java.io.Serializable;

public enum Action implements Serializable
{
    ADD("ADD"),
    PRINT("PRINT"),
    SEARCH("SEARCH"),
    BOOK("BOOK"),
    REVIEW("REVIEW");

    public final String OPTION;

    Action(String option)
    {
        this.OPTION = option;
    }

}