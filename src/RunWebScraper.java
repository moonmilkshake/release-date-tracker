/**
 * This program is a web-scraper that scrapes information about upcoming releases of movies and tv-shows from IMDB.
 * The user can view all releases through a GUI, add/remove favorites, sort by title/release date, filter by genre and
 * send favorite content to an email. Ability to refresh (perform new scrape) is also available.
 */
public class RunWebScraper {

    /**
     * Method for running program.
     * @param args For start via terminal.
     */
    public static void main(String[] args) {
        WebScraperGUI.launch(WebScraperGUI.class, args);
    }
}
