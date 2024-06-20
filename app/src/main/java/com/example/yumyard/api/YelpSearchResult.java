package com.example.yumyard.api;

import java.util.List;

public class YelpSearchResult {
    public List<Business> businesses;

    public static class Business {
        public String id;
        public String name;
        public Location location;

        public static class Location {
            public String address1;
        }
    }
}
