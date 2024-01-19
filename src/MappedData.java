import javafx.concurrent.Task;
import java.util.*;

/**
 * Class that handles mapping of Content-data by use of Task.
 */
public class MappedData {
    private Map<String, List<Content>> moviesByGenre = new HashMap<>();
    private Map<String, List<Content>> tvShowsByGenre = new HashMap<>();
    private Set<String> movieGenres = new HashSet<>();
    private Set<String> tvShowGenres = new HashSet<>();


    /**
     * Method that sets up and starts task of mapping movies by genres.
     * @param callback Callback to be performed upon success.
     * @param data of Content (of type movie)
     */
    public void mapMovieGenresAsync(Runnable callback, List<Content> data) {
        Task<Map<String, List<Content>>> task = new GenreMapper(data);

        task.setOnSucceeded(e -> {
            moviesByGenre = task.getValue();
            callback.run();
            movieGenres = moviesByGenre.keySet();
        });

        new Thread(task).start();
    }

    /**
     * Method that sets up and starts task of mapping tv-shows by genres.
     * @param callback Callback to be performed upon success.
     * @param data of Content (of type tv-show)
     */
    public void mapTvShowGenresAsync(Runnable callback, List<Content> data) {
        Task<Map<String, List<Content>>> task = new GenreMapper(data);

        task.setOnSucceeded(e -> {
            tvShowsByGenre = task.getValue();
            callback.run();
            tvShowGenres = tvShowsByGenre.keySet();
        });

        new Thread(task).start();
    }

    /**
     * Getter for all movie genres.
     * @return Returns a copy hashset of all movie genres.
     */
    public HashSet<String> getMovieGenres() {
        return new HashSet<>(movieGenres);
    }

    /**
     * Getter for tv-show genres.
     * @return Returns a copy hashset of all tv-show genres.
     */
    public HashSet<String> getTvShowGenres() {
        return new HashSet<>(tvShowGenres);
    }

    /**
     * Getter for map of movies with genres as keys.
     * @return Returns a copy of the map.
     */
    public Map<String, List<Content>> getMoviesByGenre() {
        return new HashMap<>(moviesByGenre);
    }

    /**
     * Getter for map of tv-shows with genres as keys.
     * @return Returns a copy of the map.
     */
    public Map<String, List<Content>> getTvShowsByGenre() {
        return new HashMap<>(tvShowsByGenre);
    }


    /**
     * Inner class that handles the Task of mapping Content by genre.
     */
    private class GenreMapper extends Task<Map<String, List<Content>>> {
        private final List<Content> data;

        /**
         * Constructor sets data to instance variable.
         * @param data Data of all content sent to be mapped.
         */
        public GenreMapper(List<Content> data) {
            this.data = data;
        }

        /**
         * Call method that handles the mapping by genres.
         * @return Returns the mapped content.
         * @throws Exception Uncaught exception.
         */
        @Override
        protected Map<String, List<Content>> call() throws Exception {

            Map<String, List<Content>> genreMap = new HashMap<>();

            for (Content c : data) {
                for (String s : c.genres()) {
                    List<Content> key = genreMap.get(s);
                    if (key == null) {
                        genreMap.put(s, new ArrayList<>());
                        key = genreMap.get(s);
                    }
                    key.add(c);
                }
            }
            return genreMap;
        }

    }

}
