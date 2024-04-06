package mapreduce.helpers;

import java.io.Serializable;

public record Message(long id, String action, Object parameters) implements Serializable
{
}