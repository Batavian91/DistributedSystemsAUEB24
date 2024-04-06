package mapreduce.helpers;

import accomodation.Accommodation;

import java.util.ArrayList;

public class Node
{
    protected int tag;
    public ArrayList<Accommodation> arrayList;

    public Node(int i)
    {
        this.tag = i;
        this.arrayList = null;
    }
}