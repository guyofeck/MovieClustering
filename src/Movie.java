import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Movie implements Serializable {
    int id;
    String name;
    int year;
    ArrayList<Genre> genres;
    Set<Rating> rating;
    Set<Integer> users;
    double probability;

    public Movie(int id, String name, int year, String genres) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.genres = genresToArray(genres);
        this.rating = new HashSet<>();
        this.probability = 0;
        this.users = new HashSet<>();
    }

    public void addRating(Rating movieRate) {
        rating.add(movieRate);
        users.add(movieRate.userId);
    }

    public void removeMovieFromUserList() {
        for (Rating rate : rating) {
            moviecluster.users.get(rate.userId).rating.remove(rate);
        }
    }

    public String toString() {
        return id + " " + name;
    }

    public int totalViews() {
        return rating.size();
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getNormalizedProbability() {
        double prob = 0;
        for (Integer userId : users) {
            prob += (2 / (double) moviecluster.users.get(userId).numberOfWathcedMovies());
        }
        return prob;
    }

    private ArrayList<Genre> genresToArray(String str) {
        String[] stringArray = str.split("\\|");
        ArrayList<Genre> movieGenre = new ArrayList<Genre>();
        for (String movieStr : stringArray) {
            movieGenre.add(genreToEnum(movieStr));
        }

        return movieGenre;
    }

    private Genre genreToEnum(String str) {
        switch (str) {
            case "Action":
                return Genre.ACTION;
            case "Adventure":
                return Genre.ADVENTURE;
            case "Animation":
                return Genre.ANIMATION;
            case "Children's":
                return Genre.CHILDREN;
            case "Comedy":
                return Genre.COMEDY;
            case "Crime":
                return Genre.CRIME;
            case "Documentary":
                return Genre.DOCUMENTARY;
            case "Drama":
                return Genre.DRAMA;
            case "Fantasy":
                return Genre.FANTASY;
            case "Film-Noir":
                return Genre.FILMNOIR;
            case "Horror":
                return Genre.HORROR;
            case "Musical":
                return Genre.MUSICAL;
            case "Mystery":
                return Genre.MYSTERY;
            case "Romance":
                return Genre.ROMANCE;
            case "Sci-Fi":
                return Genre.SCIFI;
            case "Thriller":
                return Genre.THRILLER;
            case "War":
                return Genre.WAR;
            case "Western":
                return Genre.WESTERN;
        }
        throw new IllegalArgumentException("no such genre");
    }

}


