package mapreduce.helpers;

public class Pair<T1, T2>
{
    private final T1 type1;
    private final T2 type2;

    public Pair(T1 type1, T2 type2)
    {
        this.type1 = type1;
        this.type2 = type2;
    }

    public T1 getType1()
    {
        return type1;
    }

    public T2 getType2()
    {
        return type2;
    }
}