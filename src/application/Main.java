package application;

public class Main
{
    public static void main(String[] args)
    {
        Manager manager = new Manager("Nick");
        String path = STR."\{System.getProperty("user.dir")}\\resources\\testRoom.json";
        manager.addAccommodation(path);
    }

}