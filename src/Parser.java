import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Parser {

    public static ArrayList<Movie> parseMovies(String dir) throws IOException {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        File file = new File(dir);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            String[] movieStr = line.split("\\::");
            int id = Integer.parseInt(movieStr[0]);
            String name = movieStr[1].substring(0, movieStr[1].length() - 7);
            int year = Integer.parseInt(movieStr[1].substring(movieStr[1].length() - 5, movieStr[1].length() - 1));
            Movie movie = new Movie(id, name, year, movieStr[2]);
            moviecluster.movies.put(id, movie);
            line = br.readLine();
            moviecluster.movieMaxId = movie.id + 1;
        }
        br.close();
        fr.close();
        return movies;
    }

    public static ArrayList<User> parseUsers(String dir) throws IOException {
        ArrayList<User> users = new ArrayList<User>();
        File file = new File(dir);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            String[] userStr = line.split("::");
            int id = Integer.parseInt(userStr[0]);
            char gender = userStr[1].charAt(0);
            int age = Integer.parseInt(userStr[2]);
            int occupation = Integer.parseInt(userStr[3]);
            String zipCode = userStr[4];
            User user = new User(id, gender, age, occupation, zipCode);
            moviecluster.users.put(id, user);
            line = br.readLine();
            moviecluster.userMaxId = user.id + 1;

        }
        br.close();
        fr.close();

        return users;
    }

    public static void parseRating(String dir) throws IOException {
        File file = new File(dir);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            String[] ratingStr = line.split("::");
            int userId = Integer.parseInt(ratingStr[0]);
            int movieId = Integer.parseInt(ratingStr[1]);
            moviecluster.usersOnMovies[userId][movieId] = true;
            int rate = Integer.parseInt(ratingStr[2]);
            int timestamp = Integer.parseInt(ratingStr[3]);
            Rating rating = new Rating(userId, movieId, rate, timestamp);
            rating.evaluateRating();
            line = br.readLine();
        }
        br.close();
        fr.close();
    }

    public static ArrayList<Movie> getMoviesSubset(String dir) throws IOException {
        ArrayList<Movie> toReturn = new ArrayList<>();
        File file = new File(dir);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            try {
                int id = Integer.parseInt(line);
                Movie toAdd = moviecluster.movies.get(id);
                if (toAdd != null)
                    toReturn.add(toAdd);
            } catch (Exception exp) {
                throw new IOException();
            }
            line = br.readLine();
        }
        return toReturn;
    }

}

