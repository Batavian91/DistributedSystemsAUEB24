package mapreduce.reducer;

public class ReducerMain
{

    public static void main(String[] args)
    {
        String master = "127.0.0.1:4321";

        Reducer reducer = new Reducer(4328, 4);
        reducer.setMaster(master);

        reducer.runReducer();
    }

}