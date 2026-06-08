package com.facens.bibliotecagps;

import java.util.List;

public class GoogleBooksResposta {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }


    public static class Item {
        private String id;
        private VolumeInfo volumeInfo;

        public String getId() {
            return id;
        }

        public VolumeInfo getVolumeInfo() {
            return volumeInfo;
        }
    }

    public static class VolumeInfo {
        private String title;
        private List<String> authors;
        private String publisher;
        private String publishedDate;

        public String getTitle() {
            return title;
        }

        public List<String> getAuthors() {
            return authors;
        }

        public String getPublisher() {
            return publisher;
        }

        public String getPublishedDate() {
            return publishedDate;
        }
    }
}