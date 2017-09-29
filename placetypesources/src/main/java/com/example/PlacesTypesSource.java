package com.example;

public class PlacesTypesSource {

    private String[] placeTitles ={
            "Atm", "Bakery", "Bank", "Bar",
            "Bus station", "Cafe", "Church", "Gas station",
            "Gym","Library","Mall","Theater","Park","Pharmacy",
            "Restaurant","School"

    };

    private String[] placeValues = {
            "atm", "bakery","bank","bar",
            "bus_station","cafe","church","gas_station",
            "gym","library","shopping_mall","movie_theater",
            "park","pharmacy","restaurant","school"
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
