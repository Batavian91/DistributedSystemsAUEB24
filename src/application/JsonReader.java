package application;

import global.Accommodation;
import global.DateRange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;

public class JsonReader
{
    public ArrayList<Accommodation> readAccommodationsFromFile(String filePath)
    {
        ArrayList<Accommodation> accommodations= new ArrayList<>();

        try
        {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Accommodation accommodation = jsonToAccommodation(jsonObject);
                accommodations.add(accommodation);
            }

        } catch (Exception e)
        {
            System.out.println("Reading from json file failed! Please, try again!");
            //e.printStackTrace();
        }
        return accommodations;
    }

    private Accommodation jsonToAccommodation(JSONObject jsonObject)
    {
        String roomName = jsonObject.getString("roomName");
        int noOfPersons = jsonObject.getInt("noOfPersons");
        String area = jsonObject.getString("area");
        int price = jsonObject.getInt("price");
        int stars = jsonObject.getInt("stars");
        int noOfReviews = jsonObject.getInt("noOfReviews");
        String roomImage = jsonObject.getString("roomImage");
        LocalDate startDate = LocalDate.parse(jsonObject.getString("startDate"));
        LocalDate endDate = LocalDate.parse(jsonObject.getString("endDate"));

        Accommodation accommodation = new Accommodation(roomName, noOfPersons, area, price,
                stars*noOfReviews, noOfReviews);
        accommodation.setPhoto(roomImage);
        accommodation.addAvailableDates((new DateRange(startDate, endDate)));

        return accommodation;
    }
}