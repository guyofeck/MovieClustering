import java.io.Serializable;

public class Rating implements Serializable {
    int userId;
    int movieId;
    int rate;
    int timestamp;

    public Rating(int userId, int movieId, int rate, int timestamp) {
        this.userId = userId;
        this.movieId = movieId;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public void evaluateRating() {
        moviecluster.users.get(userId).addRating(this);
        moviecluster.movies.get(movieId).addRating(this);
    }
}
