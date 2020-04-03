import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class moviecluster {
    static HashMap<Integer, Movie> movies = new HashMap<>();
    static HashMap<Integer, User> users = new HashMap<>();
    static int totalUsers;
    static int totalMovies;
    static HashMap<Integer, HashMap<Integer, Double>> correlatedMovies = new HashMap<>();
    static Set<Set<Movie>> correlationClustering;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ArrayList<Movie> subset;
        String folder = args[0];
        int runCorrelationAlgorithm = Integer.parseInt(args[1]);
        int toReload = Integer.parseInt(args[3]);
        //TODO check relative vs absolute address
        if (toReload == 1) {
            Parser.parseMovies(folder + "/movies.dat");
            Parser.parseUsers(folder + "/users.dat");
            Parser.parseRating(folder + "/ratings.dat");
            filterUnwatchedMovies();
            totalUsers = users.size();
            totalMovies = movies.size();
            calculateMovieProbability();
            calculateCorrelatedProbability();
            System.out.println("finish processing");
            writeFiles();
        } else {
            readFiles();
            System.out.println("done reading");
            totalUsers = users.size();
            totalMovies = movies.size();
        }
        subset = Parser.getMoviesSubset(args[2]);

        if (runCorrelationAlgorithm == 1)
            correlationClustering = CorrelationClustering.cluster(subset);

        int movieCounter = 0;
        for (Set<Movie> set : correlationClustering) {
            for (Movie mov : set) {
                movieCounter += 1;
            }
        }

        double correlationCost = CostCalculator.calculateCluster(correlationClustering);
        System.out.println("number of movies in cluster : " + movieCounter);
        System.out.println("total movies :" + totalMovies);
        System.out.println("Cost : " + correlationCost);

        System.out.println("hey");

    }

    public static void filterUnwatchedMovies() {
        HashMap<Integer, Movie> updatedMovies = new HashMap<>(movies);
        for (Movie movie : movies.values()) {
            if (movie.users.size() < 10) {
                System.err.println("Movie " + movie.id + " ignored because it has only " + movie.users.size() + " ratings");
                movie.removeMovieFromUserList();
                updatedMovies.remove(movie.id);
            }
        }
        movies = updatedMovies;
    }

    public static void calculateMovieProbability() {
        double moviesFactor = 2 / (double) totalMovies;
        double usersFactor = (1 / (double) (totalUsers + 1));
        for (Movie movie : movies.values()) {
            double probability = usersFactor * (moviesFactor + movie.getNormalizedProbability());
            movie.setProbability(probability);
        }
    }

    public static void calculateCorrelatedProbability() {
        double usersFactor = 1 / (double) (totalUsers + 1);
        double moviesFactor = 2 / (double) (totalMovies * (totalMovies - 1));
        for (Movie firstMovie : movies.values()) {
            correlatedMovies.put(firstMovie.id, new HashMap<>());
            for (Movie secondMovie : movies.values()) {
                if (firstMovie.equals(secondMovie))
                    continue;
                double correlatedProbability = getCorrelatedProbability(secondMovie.id, firstMovie.id);
                if (correlatedProbability != -1) {
                    correlatedMovies.get(firstMovie.id).put(secondMovie.id, correlatedProbability);
                    continue;
                }
                Set<Integer> userIntersection = new HashSet<>(firstMovie.users);
                double aggregateCorrelation = 0;
                userIntersection.retainAll(secondMovie.users); // intersect users that have seen both movies
                for (Integer user : userIntersection) {
                    int numberOfWatchedMovies = users.get(user).numberOfWathcedMovies();
                    aggregateCorrelation += 2 / (double) (numberOfWatchedMovies * (numberOfWatchedMovies - 1));
                }
                correlatedProbability = usersFactor * (moviesFactor + aggregateCorrelation);
                correlatedMovies.get(firstMovie.id).put(secondMovie.id, correlatedProbability);
            }
        }
    }

    public static boolean seekPair(int a, int b) {
        if (correlatedMovies.get(a) == null)
            return false;
        return correlatedMovies.get(a).get(b) != null;
    }

    public static double getCorrelatedProbability(int a, int b) {
        if (seekPair(a, b))
            return correlatedMovies.get(a).get(b);
        return -1;
    }


    public static void writeFiles() throws IOException {
        FileOutputStream fos1 =
                new FileOutputStream("correlation.ser");
        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
        oos1.writeObject(correlatedMovies);
        oos1.close();
        fos1.close();

        FileOutputStream fos2 =
                new FileOutputStream("movies.ser");
        ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
        oos2.writeObject(movies);
        oos2.close();
        fos2.close();

        FileOutputStream fos3 =
                new FileOutputStream("users.ser");
        ObjectOutputStream oos3 = new ObjectOutputStream(fos3);
        oos3.writeObject(users);
        oos3.close();
        fos3.close();

    }

    public static void readFiles() throws IOException, ClassNotFoundException {
        FileInputStream fis1 = new FileInputStream("correlation.ser");
        ObjectInputStream ois1 = new ObjectInputStream(fis1);
        correlatedMovies = (HashMap) ois1.readObject();
        ois1.close();
        fis1.close();

        FileInputStream fis2 = new FileInputStream("movies.ser");
        ObjectInputStream ois2 = new ObjectInputStream(fis2);
        movies = (HashMap) ois2.readObject();
        ois2.close();
        fis2.close();

        FileInputStream fis3 = new FileInputStream("users.ser");
        ObjectInputStream ois3 = new ObjectInputStream(fis3);
        users = (HashMap) ois3.readObject();
        ois3.close();
        fis3.close();
    }


}


