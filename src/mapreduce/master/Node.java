package mapreduce.master;

import global.Accommodation;

import java.util.ArrayList;

public class Node
{
    protected int tag;
    protected ArrayList<Accommodation> arrayList;

    protected Node(int i)
    {
        this.tag = i;
        this.arrayList = new ArrayList<>();
    }
}