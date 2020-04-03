import java.util.Set;

public class CostCalculator {

    public static double calculateCluster(Set<Set<Movie>> clusters) {
        double aggregateCost = 0;
        for (Set<Movie> cluster : clusters) {
            aggregateCost += calculateSingleCluster(cluster);
        }
        return aggregateCost;
    }

    public static double calculateSingleCluster(Set<Movie> cluster) {
        double cost = 0;
        double size = cluster.size();
        if (size == 1) {
            return Math.log(1 / cluster.iterator().next().probability);
        } else {
            for (Movie firstMovie : cluster) {
                for (Movie secondMovie : cluster) {
                    if (firstMovie.equals(secondMovie))
                        continue;
                    double correlation = moviecluster.getCorrelatedProbability(firstMovie.id, secondMovie.id);
                    if (correlation == -1)
                        throw new IllegalArgumentException("correlation was not calculated as expected");
                    cost += (1 / (size - 1)) * Math.log(1 / correlation);
                }
            }
            return cost;
        }
    }
}
