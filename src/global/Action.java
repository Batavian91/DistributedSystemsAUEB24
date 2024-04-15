package global;

import java.io.Serializable;

public enum Action implements Serializable
{
    ADD("ADD"),
    PRINT_ALL("PRINT_ALL"),
    PRINT("PRINT"),
    SEARCH("SEARCH"),
    BOOK("BOOK"),
    REVIEW("REVIEW"),
    READ("READ"),
    WRITE("WRITE");

    public final String OPTION;

    Action(String option)
    {
        this.OPTION = option;
    }

}