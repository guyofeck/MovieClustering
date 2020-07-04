import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MultiCorrelationClustering {
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
            if (arePositiveCorrelatedToEntireCluster(toAdd, movie))
                toAdd.add(movie);
            else
                theRest.add(movie);
        }
        clusters.add(toAdd);
        buildCluster(theRest);
    }

    private static boolean arePositiveCorrelatedToEntireCluster(Set<Movie> movieSet, Movie subjectMovie) {
        for (Movie element : movieSet) {
            if(moviecluster.getCorrelatedProbability(element.id, subjectMovie.id) < element.probability * subjectMovie.probability){
                return false;
            }
        }
        return true;
    }
}
