import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    int id;
    char gender;
    int ageGroup;
    int occupation;
    String zipCode;
    ArrayList<Rating> rating;


    public User(int id, char gender, int ageGroup, int occupation, String zipCode) {
        this.id = id;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.occupation = occupation;
        this.zipCode = zipCode;
        this.rating = new ArrayList<>();
    }

    public void addRating(Rating movieRating) {
        rating.add(movieRating);
    }

    public int numberOfWathcedMovies() {
        return rating.size();
    }


    public static String[] occupations = {
            "other",
            "academic/educator",
            "artist",
            "clerical/admin",
            "college/grad student",
            "customer service",
            "doctor/health care",
            "executive/managerial",
            "farmer",
            "homemaker",
            "K-12 student",
            "lawyer",
            "programmer",
            "retired",
            "sales/marketing",
            "scientist",
            "self-employed",
            "technician/engineer",
            "tradesman/craftsman",
            "unemployed",
            "writer"
    };

    public String getUserOccupation() {
        return occupations[this.occupation];
    }
}


