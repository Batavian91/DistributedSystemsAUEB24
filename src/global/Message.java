package global;

import java.io.Serializable;

public record Message(long id, Action action, Object parameters) implements Serializable
{
}