import javafx.scene.image.Image;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Record for creating Content-objects (for the purposes of this implementation: movies and tv-shows).
 * @param poster A poster belonging to the specific Content.
 * @param title Name of the Content.
 * @param genres All genres the Content belongs in.
 * @param topCast The top cast related to the Content.
 * @param releaseDate The scheduled date of release.
 * @param type The specific type of Content (in this case, movie or tv-show).
 */
public record Content(Image poster, String title, String[] genres, String[] topCast, LocalDate releaseDate, String type, String imdbUrl) {

    /**
     * Returns an image of the poster. Was needed for the cell value factory to work properly.
     * @return Image of a poster.
     */
    public Image getPoster() {
        return poster;
    }

    /**
     * Returns the title of the Content. Was needed for the cell value factory to work properly.
     * @return String name of Content.
     */
    public String getTitle() {
        return title;
    }

    /**
     * A specific formatting of a Contents genres, used by cell value factory.
     * @return returns the formatted string of genres.
     */
    public String getGenres() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genres.length; i++) {
            sb.append(genres[i]);
            if (i != genres.length-1) {
                sb.append(", ");
            }
        }
        if (sb.isEmpty()) {
            return "Not specified";
        } else {
            return sb.toString();
        }
    }

    /**
     * A specific formatting of a Contents top cast, used by cell value factory.
     * @return returns the formatted string of top cast.
     */
    public String getTopCast() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < topCast.length; i++) {
            sb.append(topCast[i]);
            if (i != topCast.length-1) {
                sb.append(", " + "\n");
            }
        }
        if (sb.isEmpty()) {
            return "None specified";
        } else {
            return sb.toString();
        }
    }

    /**
     * Method for getting release date. Was needed for cell value factory to work properly.
     * @return String of release date.
     */
    public String getReleaseDate() {
        return releaseDate.toString();
    }

    /**
     * Method builds and returns a String with a Contents main attributes.
     * @return Returns a string with main attributes.
     */
    public String getMainAttributes() {
        return title + " is a " + type.toLowerCase() + " that is to be released " + releaseDate + ".";
    }

    /**
     * Records toString. Built with relevant attributes.
     * @return Returns toString.
     */
    @Override
    public String toString() {
        return "Title: " + title + ",  Genres: " + Arrays.toString(genres) + ", Top cast: " + Arrays.toString(topCast) + ", Release date: " + releaseDate.toString() + ", Type: " + type + ".";
    }
}
