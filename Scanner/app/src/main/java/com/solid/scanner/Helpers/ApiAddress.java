package com.solid.scanner.Helpers;

public enum ApiAddress {
    ALL("http://pumapp.000webhostapp.com/api.php?products=all"),
    MY("http://pumapp.000webhostapp.com/api.php?products=subscription&userKey="),
    SEARCH("http://pumapp.000webhostapp.com/api.php?product="),
    SUBSCRIBE("http://pumapp.000webhostapp.com/api.php?action=subscribe"),
    UNSUBSCRIBE("http://pumapp.000webhostapp.com/api.php?action=unsubscribe");

    private String url;

    ApiAddress(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }


}
