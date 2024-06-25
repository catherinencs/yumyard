package com.example.yumyard.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionsResponse {
    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public static class Route {
        @SerializedName("overview_polyline")
        private Polyline overviewPolyline;

        public Polyline getOverviewPolyline() {
            return overviewPolyline;
        }

        public static class Polyline {
            @SerializedName("points")
            private String points;

            public String getPoints() {
                return points;
            }
        }
    }
}
