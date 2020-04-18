import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static java.lang.System.exit;

public class moviecluster {
    static HashMap<Integer, Movie> movies = new HashMap<>();
    static HashMap<Integer, User> users = new HashMap<>();
    static int totalUsers;
    static int totalMovies;
    static Set<Set<Movie>> correlationClustering;
    static Set<Set<Movie>> multiCorrelationClustering;
    static double[][] corrMovies;
    static boolean[][] usersOnMovies;
    static int movieMaxId;
    static int userMaxId;


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ArrayList<Movie> subset;
        subset = new ArrayList<>();
        String folder;

        int runCorrelationAlgorithm = validateArgsAndExtractChosenAlgorithm(args);
        folder = args[0];

        //TODO In order to initialize script we will parse the data and write it into specific files
/*
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
*/

        // TODO given preprocessed files, we read them
        readFiles();

        subset = validateAndSetMoviesSubset(args, subset);

        totalUsers = users.size();
        totalMovies = movies.size();

        if (runCorrelationAlgorithm == 1) {
            correlationClustering = CorrelationClustering.cluster(subset);
            System.out.println("correlation clustering");
            Printer.printClusters(correlationClustering);
        }
        if (runCorrelationAlgorithm == 2) {
            multiCorrelationClustering = MultiCorrelationClustering.cluster(subset);
            System.out.println("Our improved algorithm - multi correlation clustering");
            Printer.printClusters(multiCorrelationClustering);
        }

        //TODO Used to analysis the improved algorithm vs the correlation clustering algorithm
/*

        for (int i = 1; i < 21; i++) {
            subset = generateSubset(100);
            correlationClustering = CorrelationClustering.cluster(subset);
            multiCorrelationClustering = MultiCorrelationClustering.cluster(subset);
            System.out.println("subset" + i);
            for(Movie mov : subset){
                System.out.println(mov.id);
            }
            System.out.println("correlated");
            System.out.println(CostCalculator.calculateCluster(correlationClustering));
            System.out.println("multi correlated");
            System.out.println(CostCalculator.calculateCluster(multiCorrelationClustering));

            CorrelationClustering.clusters = new HashSet<>();
            MultiCorrelationClustering.clusters = new HashSet<>();
        }
 */
    }

    private static ArrayList<Movie> validateAndSetMoviesSubset(String[] args, ArrayList<Movie> subset) {
        try {
            subset = Parser.getMoviesSubset(args[2]);
        } catch (IOException io) {
            System.err.println("An error occurred while parsing the subset movies file");
            exit(1);
        }
        return subset;
    }

    private static int validateArgsAndExtractChosenAlgorithm(String[] args) {
        String folder;
        int runCorrelationAlgorithm = 0;
        if (args.length != 3) {
            System.err.println("Invalid number of arguments, expected 3 atguments but actually was given " + args.length + " arguments");
            exit(1);
        }
        folder = args[0];
        try {
            runCorrelationAlgorithm = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println(e);
            exit(1);
        }
        if (runCorrelationAlgorithm < 1 || runCorrelationAlgorithm > 2) {
            System.err.println("Invalid given argument for running a clustering algorithm, expected to get 1 or 2 but actually given " + runCorrelationAlgorithm);
            exit(1);
        }
        return runCorrelationAlgorithm;
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


    public static double getCorrelatedProbability(int a, int b) {
        return corrMovies[a][b];
    }

    //TODO used to filter unwatched movies as part of the preprocessing
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

    //TODO was part of the preprocessing
    public static void calculateMovieProbability() {
        double moviesFactor = 2 / (double) totalMovies;
        double usersFactor = (1 / (double) (totalUsers + 1));
        for (Movie movie : movies.values()) {
            double probability = usersFactor * (moviesFactor + movie.getNormalizedProbability());
            movie.setProbability(probability);
        }
    }

    //TODO was part of the preprocessing
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


    //TODO Used to generate random movies subset in order to check the improved algorithm
    public static ArrayList<Movie> generateSubset(int n) {
        ArrayList<Movie> toReturn = new ArrayList<>();
        ArrayList<Movie> existingMovies = new ArrayList<>(movies.values());
        int pivot;
        Movie pivotElement;
        for (int i = 0; i < n; i++) {
            pivot = (int) (Math.random() * existingMovies.size());
            pivotElement = existingMovies.get(pivot);
            toReturn.add(pivotElement);
            existingMovies.remove(pivot);
        }
        return toReturn;
    }

    //TODO Used to write the preprocessed files
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
}


