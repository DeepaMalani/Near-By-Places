package myapp.nearby;

public class PlacesTypesSource {

    private String[] placeTitles ={
            "Airport","Atm", "Bakery", "Bank", "Bar","Beauty Salon",
            "Book store","Bus station", "Cafe", "Church",
            "Dentist","Doctor","Gas station","Grocery store", "Gym","Hospital",
            "Library","Lodging", "Mall","Theater","Park","Pharmacy",
            "Restaurant","School","Train station","Taxi stand"

    };

    private String[] placeValues = {
             "airport","atm","bakery","bank","bar","beauty+salon",
            "book+store","bus+station","cafe","church","dentist","doctor",
            "gas+station","grocery+store", "gym","hospital","library","lodging","shopping+mall","movie+theater",
            "park","pharmacy","restaurant","school","train+station","taxi+service"
    };

    public String[] getPlaceTitles()
    {
        return placeTitles;
    }

    public String[] getPlaceValues()
    {
        return placeValues;
    }
}
