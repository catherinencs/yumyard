package com.example.yumyard.api;

import java.util.List;

public class YelpBusinessDetail {
    private String id;
    private String name;
    private String phone;
    private String price;
    private List<String> photos;
    private Location location;
    private List<Hour> hours;
    private Coordinates coordinates; // Add coordinates field

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Hour> getHours() {
        return hours;
    }

    public void setHours(List<Hour> hours) {
        this.hours = hours;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public static class Location {
        private String address1;
        private String address2;
        private String address3;
        private String city;
        private String zip_code;
        private String country;
        private String state;

        // Getters and setters

        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
        }

        public String getAddress3() {
            return address3;
        }

        public void setAddress3(String address3) {
            this.address3 = address3;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZipCode() {
            return zip_code;
        }

        public void setZipCode(String zip_code) {
            this.zip_code = zip_code;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public static class Hour {
        private String type;
        private List<Open> open;
        private boolean is_open_now;

        // Getters and setters

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Open> getOpen() {
            return open;
        }

        public void setOpen(List<Open> open) {
            this.open = open;
        }

        public boolean isIsOpenNow() {
            return is_open_now;
        }

        public void setIsOpenNow(boolean is_open_now) {
            this.is_open_now = is_open_now;
        }

        public static class Open {
            private String start;
            private String end;
            private int day;

            // Getters and setters

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }
        }
    }

    public static class Coordinates {
        private double latitude;
        private double longitude;

        // Getters and setters

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}
