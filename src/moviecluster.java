import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class moviecluster {
    static HashMap<Integer, Movie> movies = new HashMap<>();
    static HashMap<Integer, User> users = new HashMap<>();
    static int totalUsers;
    static int totalMovies;
    static HashMap<Integer, HashMap<Integer, Double>> correlatedMovies = new HashMap<>();
    static Set<Set<Movie>> correlationClustering;
    static double[][] corrMovies;
    static boolean[][] usersOnMovies;
    static int movieMaxId;
    static int userMaxId;


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ArrayList<Movie> subset;
        String folder = args[0];
        int runCorrelationAlgorithm = Integer.parseInt(args[1]);
        int toReload = Integer.parseInt(args[3]);
        //TODO check relative vs absolute address
        if (toReload == 1) {
            Parser.parseMovies(folder + "/movies.dat");
            Parser.parseUsers(folder + "/users.dat");
            corrMovies = new double[movieMaxId][movieMaxId];
            usersOnMovies = new boolean[userMaxId][movieMaxId];
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
        subset = Parser.getMoviesSubset(args[2]); // new ArrayList<>(movies.values());

        if (runCorrelationAlgorithm == 1)
            correlationClustering = CorrelationClustering.cluster(subset);

        int movieCounter = 0;
        for (Set<Movie> set : correlationClustering) {
            movieCounter += set.size();
        }

        System.out.println("number of movies in cluster : " + movieCounter);
        System.out.println("total movies of cluster:" + subset.size());
        Printer.printClusters(correlationClustering);

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
            for (Movie secondMovie : movies.values()) {
                if (firstMovie.equals(secondMovie))
                    continue;
                double correlatedProbability = getCorrelatedProbability(secondMovie.id, firstMovie.id);
                if (correlatedProbability != 0.0) {
                    corrMovies[firstMovie.id][secondMovie.id] = correlatedProbability;
                    continue;
                }
                double aggregateCorrelation = 0;
                for (Rating rate : firstMovie.rating) {
                    if (usersOnMovies[rate.userId][secondMovie.id]) {
                        int numberOfWatchedMovies = users.get(rate.userId).numberOfWathcedMovies();
                        aggregateCorrelation += 2 / (double) (numberOfWatchedMovies * (numberOfWatchedMovies - 1));
                    }
                }
                correlatedProbability = usersFactor * (moviesFactor + aggregateCorrelation);
                corrMovies[firstMovie.id][secondMovie.id] = correlatedProbability;
            }
        }
    }


    public static double getCorrelatedProbability(int a, int b) {
        return corrMovies[a][b];
    }


    public static void writeFiles() throws IOException {
        FileOutputStream fos1 =
                new FileOutputStream("correlation.ser");
        ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
        oos1.writeObject(corrMovies);
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
        corrMovies = (double[][]) ois1.readObject();
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


