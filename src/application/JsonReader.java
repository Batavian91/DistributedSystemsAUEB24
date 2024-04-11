package application;

import global.Accommodation;
import global.DateRange;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class JsonReader
{
    public ArrayList<Accommodation> readAccommodationsFromFile(String filePath)
    {
        try
        {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray jsonArray = new JSONArray(content);
            ArrayList<Accommodation> accommodations= new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Accommodation accommodation = jsonToAccommodation(jsonObject);
                accommodations.add(accommodation);
            }
            return accommodations;
        } catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
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

        Accommodation accommodation = new Accommodation(roomName, noOfPersons, area, price,
                stars*noOfReviews, noOfReviews);
        accommodation.setPhoto(roomImage);

        return accommodation;
    }
}