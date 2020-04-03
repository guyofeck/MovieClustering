import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CorrelationClustering {
    static Set<Set<Movie>> clusters = new HashSet<>();

    public static Set<Set<Movie>> cluster(Collection<Movie> toCluster) {
        ArrayList<Movie> cluster = new ArrayList<>(toCluster);
        buildCluster(cluster);

        return clusters;
    }

    private static void buildCluster(ArrayList<Movie> cluster) {
        if (cluster.size() == 0)
            return;
        Set<Movie> toAdd = new HashSet<>();
        ArrayList<Movie> theRest = new ArrayList<>();
        int randomIndex = (int) (Math.random() * cluster.size());
        Movie pivot = cluster.get(randomIndex);
        toAdd.add(pivot);
        cluster.remove(pivot);
        for (Movie movie : cluster) {
            if (arePositiveCorrelated(pivot, movie))
                toAdd.add(movie);
            else
                theRest.add(movie);
        }
        clusters.add(toAdd);
        buildCluster(theRest);
    }

    private static boolean arePositiveCorrelated(Movie movieA, Movie movieB) {
        return moviecluster.getCorrelatedProbability(movieA.id, movieB.id) > movieA.probability * movieB.probability;
    }
}
