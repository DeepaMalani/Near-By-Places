package com.example;

public class PlacesTypesSource {

    private String[] placeTitles ={
            "Airport","Atm", "Bakery", "Bank", "Bar","Beauty Salon",
            "Book store","Bus station", "Cafe", "Church",
            "Dentist","Doctor","Gas station", "Gym","Hospital",
            "Library","Lodging", "Mall","Theater","Park","Pharmacy",
            "Restaurant","School","Store","Train station","Taxi stand"

    };

    private String[] placeValues = {
             "airport","atm","bakery","bank","bar","beauty_salon",
            "book_store","bus_station","cafe","church","dentist","doctor",
            "gas_station", "gym","hospital","library","lodging","shopping_mall","movie_theater",
            "park","pharmacy","restaurant","school","store","train_station","taxi_stand"
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
