import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Class that handles the web-scraping from IMDB:s data of upcoming movies and tv-shows.
 */
public class WebScraper {
    private static final String IMDB_MOVIE_URL = "https://www.imdb.com/calendar/sweden";
    private static final String IMDB_TV_URL = "https://www.imdb.com/calendar/?ref_=rlm&region=US&type=TV";
    private static final String IMDB_URL = "https://www.imdb.com";
    private static final String currentYear;
    private static final String nextYear;
    private static final String yearAfterNext;
    private List<Content> data;
    private String contentType;

    static {
        Year year = Year.now();
        currentYear = year.format(DateTimeFormatter.ofPattern("yyyy"));
        nextYear = year.plusYears(1).format(DateTimeFormatter.ofPattern("yyyy"));
        yearAfterNext = year.plusYears(2).format(DateTimeFormatter.ofPattern("yyyy"));
    }

    /**
     * Initiates async movie scrape.
     * @param callback Forwards callback.
     * @param progressBar Forwards progressbar.
     */
    public void scrapeMoviesAsync(Consumer<List<Content>> callback, ProgressBar progressBar) {
        contentType = "Movie";
        startScrape(callback, progressBar, IMDB_MOVIE_URL);
    }

    /**
     * Initiates async tv-show scrape.
     * @param callback Forwards callback.
     * @param progressBar Forwards progressbar.
     */
    public void scrapeTvShowsAsync(Consumer<List<Content>> callback, ProgressBar progressBar) {
        contentType = "Tv-show";
        startScrape(callback, progressBar, IMDB_TV_URL);
    }

    /**
     * Method that handles setting up scrape, starting it and handles OnSucceeded.
     * @param callback Handles result of scraping task.
     * @param progressBar A bar that displays progress of scrape in the GUI.
     * @param url URL on which to perform scrape.
     */
    private void startScrape(Consumer<List<Content>> callback, ProgressBar progressBar, String url) {
        Task<List<Content>> task = new WebScrapingTask(url);

        task.setOnSucceeded(e -> {
            List<Content> data = task.getValue();
            callback.accept(data);
        });

        progressBar.progressProperty().bind(task.progressProperty());

        new Thread(task).start();
    }

    /**
     * Class that extends Task, carries the Call-method and functionality of the scrape.
     */
    private class WebScrapingTask extends Task<List<Content>> {
        Image poster = null;
        String title;
        String[] genres;
        String[] topCast;
        LocalDate releaseDate;
        String contentsImdbUrl;
        Image previousPoster = null;
        int posterIndex;

        private final String URL;

        /**
         * Constructor that sets up URL.
         * @param URL URL to web-site on which to scrape.
         */
        public WebScrapingTask(String URL) {
            this.URL = URL;
        }

        /**
         * Fetches upcoming content releases from a specified URL and processes them into a list of {@code Content} objects.
         * Uses JSoup for HTML parsing. The method filters content based on the current, next, and year after next release dates,
         * and collects information such as titles, genres, top cast, and IMDB URLs. Progress is reported back for UI updates.
         *
         * @return List of upcoming content releases as {@code Content} objects.
         * @throws IOException for network-related errors.
         * @throws Exception for any other unexpected errors.
         */
        @Override
        protected List<Content> call() throws Exception {
            try {
                data = new ArrayList<>();
                final Document document = Jsoup.connect(URL).get();

                //Get all upcoming releases to iterate through
                Elements articleElements = document.select("article.sc-48add019-1.hSuRMl");

                //Set up advancing of Progress bar for reporting back to GUI.
                int totalSteps = articleElements.size();
                int currentStep = 0;

                //Iterate over each Article-element (where one Article-element contains all Content to be released a given date)
                for (Element articleElement : articleElements) {

                    //Get all set release-dates and iterate through one at a time
                    Elements dateElements = articleElement.select("h3.ipc-title__text");
                    for (Element dateElement : dateElements) {
                        if (dateElement.text().contains(yearAfterNext) || dateElement.text().contains(nextYear) || dateElement.text().contains(currentYear)) {

                            setCurrentReleaseDate(dateElement);

                            //Get all posters for a given date to iterate through while collecting Content attributes
                            Elements posterElements = articleElement.select("img.ipc-image");
                            posterIndex = 0;

                            //Get the summary of a Content to be released at the given date
                            Elements summaryElements = articleElement.select("div.ipc-metadata-list-summary-item__tc");
                            for (Element summaryElement : summaryElements) {
                                setCurrentContentsImdbUrl(summaryElement);
                                setCurrentPoster(posterElements);
                                setCurrentTitle(summaryElement);
                                setCurrentGenres(summaryElement);
                                setCurrentTopCast(summaryElement);

                                //Check that Contents poster isn't assigned to previous Contents poster (which would mean it has no own poster)
                                if (previousPoster == poster) {
                                    poster = null;
                                } else {
                                    previousPoster = poster;
                                }
                                data.add(new Content(poster, title, genres, topCast, releaseDate, contentType, contentsImdbUrl));
                            }
                        }
                    }
                    //Handle progress for Progress bar
                    currentStep++;
                    double progress = (double) currentStep / totalSteps;
                    updateProgress(progress, 1);
                }
            } catch (IOException e){
                System.out.println("IOException: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected exception: " + e.getMessage());
            }
            return data;
        }

        /**
         * Sets current Poster-element through given summary via Element
         * @param posterElements Takes data of all posters for a given period.
         */
        private void setCurrentPoster(Elements posterElements) {
            if (posterIndex < posterElements.size()) {
                poster = new Image(posterElements.get(posterIndex).attr("src"));
                posterIndex++;
            }
        }

        /**
         * Sets title of a given content.
         * @param summaryElement Takes a summary wherein title of content resides.
         */
        private void setCurrentTitle(Element summaryElement) {
            title = summaryElement.select("a.ipc-metadata-list-summary-item__t").text();
        }

        /**
         * Sets all genres of given content.
         * @param summaryElement Takes a summary wherein title of content resides.
         */
        private void setCurrentGenres(Element summaryElement) {
            Elements genreUlElements = summaryElement.select("ul.ipc-inline-list.ipc-inline-list--show-dividers.ipc-inline-list--no-wrap.ipc-inline-list--inline.ipc-metadata-list-summary-item__tl.base");
            Elements genreLiElements = genreUlElements.select("li");
            genres = new String[genreLiElements.size()];
            for (int i = 0; i < genreLiElements.size(); i++) {
                genres[i] = genreLiElements.get(i).text();
            }
        }

        /**
         * Sets all top cast of given content.
         * @param summaryElement Takes a summary wherein title of content resides.
         */
        private void setCurrentTopCast(Element summaryElement) {
            Elements topCastUlElements = summaryElement.select("ul.ipc-inline-list.ipc-inline-list--show-dividers.ipc-inline-list--no-wrap.ipc-inline-list--inline.ipc-metadata-list-summary-item__stl.base");
            Elements topCastLiElements = topCastUlElements.select("li");
            topCast = new String[topCastLiElements.size()];
            for (int i = 0; i < topCastLiElements.size(); i++) {
                topCast[i] = topCastLiElements.get(i).text();
            }
        }

        /**
         * Sets release date of given content.
         * @param dateElement Takes an element where date is to be extracted from.
         */
        private void setCurrentReleaseDate(Element dateElement) {
            String dateString = dateElement.text();
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            releaseDate = LocalDate.parse(dateString, inputFormatter);
        }

        /**
         * Sets IMDB URL to given content.
         * @param summaryElement Takes a summary wherein title of content resides.
         */
        private void setCurrentContentsImdbUrl(Element summaryElement) {
            contentsImdbUrl = IMDB_URL +
                    summaryElement.select("a.ipc-metadata-list-summary-item__t").attr("href");
        }

    }


}
