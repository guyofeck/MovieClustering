import java.util.Iterator;
import java.util.Set;

public class Printer {

    public static void printClusters(Set<Set<Movie>> clusters){
        for(Set<Movie> cluster : clusters){
                printSingleCluster(cluster);
        }
        System.out.println(CostCalculator.calculateCluster(clusters));
    }

    public static void printSingleCluster(Set<Movie> singleCluster){
        Iterator<Movie> iter = singleCluster.iterator();
        while (iter.hasNext()) {
            System.out.print(iter.next().toString());
            if(iter.hasNext())
                System.out.print(", ");
        }
        System.out.println();
    }
}
