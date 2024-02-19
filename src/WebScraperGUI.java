import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Class that handles everything GUI-related for the program. Displays Content in Table View and sets up ways for
 * users to interact with the data.
 */
public class WebScraperGUI extends Application {
    private static final int BUTTON_HEIGHT = 30;
    private static final MappedData MAPPED_DATA = new MappedData();
    private final List<Content> favoriteContent = new ArrayList<>();
    private TableView<Content> tableView;
    private ObservableList<Content> moviesObsList;
    private ObservableList<Content> tvShowObsList;
    private TableColumn<Content, Image> posterColumn;
    private AnchorPane topAnchorPane;
    private AnchorPane bottomAnchorPane;
    private Button filterButton;
    private Button favoriteButton;
    private Button removeButton;
    private Button favoriteContentButton;
    private Button sendButton;
    private Button viewImdbPageButton;
    private Button refreshButton;
    private HBox radioButtonsHbox;
    private RadioButton moviesRadioButton;
    private RadioButton tvShowsRadioButton;
    private ProgressBar progressBar;
    private boolean filterIsActive = false;
    private boolean loading = false;


    /**
     * Start-method for GUI. Sets up everything related to the GUI.
     * @param primaryStage Primary stage for GUI.
     */
    @Override
    public void start(Stage primaryStage) {
        setupTableView();
        setupComponents();
        setupListeners();

        loadMovies();

        BorderPane root = new BorderPane();
        root.setTop(topAnchorPane);
        root.setCenter(tableView);
        root.setBottom(bottomAnchorPane);
        root.setPadding(new Insets(5));
        Scene scene = new Scene(root, 1150, 800);
        primaryStage.setTitle("Release date Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Method for adding instance of Content to favorites through interaction with TableView.
     */
    private void addFavorite() {
        Content selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (!favoriteContent.contains(selectedItem)) {
            favoriteContent.add(selectedItem);
        }
    }

    /**
     * Method for removing instance of Content from favorites through interaction with TableView.
     */
    private void removeFavorite() {
        Content selectedItem = tableView.getSelectionModel().getSelectedItem();
        favoriteContent.removeIf(c -> c == selectedItem);
    }

    /**
     * Method that handles showing favorite Content via use of popup.
     */
    private void showFavoritesPopup() {
        ObservableList<Content> favoriteContentObsList = createObservableArrayList(favoriteContent);
        ListView<String> popupListView = new ListView<>();
        popupListView.setEditable(false);

        for (Content c : favoriteContentObsList) {
            popupListView.getItems().add(c.getMainAttributes());
        }

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());
        VBox popupRoot = new VBox(popupListView, closeButton);
        popupRoot.setPadding(new Insets(10));
        Scene popupScene = new Scene(popupRoot, 500, 300);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    /**
     * Method that handles interaction for sending Email of favorite Content by use of popup.
     */
    private void sendFavoritesPopup() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        Label toLabel = new Label("To");
        Label fromLabel = new Label("From");
        Label subjectLabel = new Label("Subject");
        Label messageLabel = new Label("Message");

        TextField toField = new TextField();
        toField.setPromptText("Enter email of person to receive favorites");
        toField.setFocusTraversable(false);
        TextField fromField = new TextField();
        fromField.setPromptText("Enter your name so the recipient knows who sent the recommendations :)");
        fromField.setFocusTraversable(false);
        TextField subjectField = new TextField("Hey! I wanna recommend some upcoming titles I think you'd really like!");
        subjectField.setEditable(false);

        //Builds message of favorite Content as a String
        StringBuilder messageSb = new StringBuilder();
        for (Content c : favoriteContent) {
            messageSb.append(c.toString());
            messageSb.append("\n\n");
        }
        if (messageSb.toString().isEmpty()) {
            messageSb.append("No favorite titles added yet!");
        }
        TextArea messageArea = new TextArea(messageSb.toString());
        messageArea.setEditable(false);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            if (!favoriteContent.isEmpty()) {
                messageSb.append("From ").append(fromField.getText());
                sendEmail(toField.getText(), subjectField.getText(), messageSb.toString());
                popupStage.close();
            }
        });
        HBox buttonHBox = new HBox(closeButton, sendButton);

        grid.add(toLabel, 0, 0);
        grid.add(toField, 1, 0);
        grid.add(fromLabel, 0, 1);
        grid.add(fromField, 1, 1);
        grid.add(subjectLabel, 0, 2);
        grid.add(subjectField, 1, 2);
        grid.add(messageLabel, 0, 3);
        grid.add(messageArea, 1, 3);
        grid.add(buttonHBox, 1, 4);

        Scene popupScene = new Scene(grid, 625, 300);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    /**
     * Method initiates the sending of Email by use of EmailSender-class.
     * @param to Email-address where Content should be sent.
     * @param subject String subject-line of email.
     * @param message String message to be sent.
     */
    private void sendEmail(String to, String subject, String message) { //Disabled as email-address is needed for sending
//        EmailSender emailSender = new EmailSender(to, subject, message);
//        boolean emailSent = emailSender.send();
//        if (!emailSent) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText("Couldn't find email address! Please try another one.");
//            alert.getButtonTypes().setAll(ButtonType.CLOSE);
//            alert.showAndWait();
//        }
    }

    /**
     * Method that handles user choices of filtering by Genre via use of Popup.
     */
    private void filtersPopup() {
        if (filterIsActive) {
            if (moviesRadioButton.isSelected()) {
                populateTableView(moviesObsList);
            } else {
                populateTableView(tvShowObsList);
            }
            removeFiltering();
        } else {
            Stage popupStage = new Stage();
            VBox componentsVBox = new VBox(10);

            Label genreLabel = new Label("Genres");
            componentsVBox.getChildren().add(genreLabel);

            HashSet<String> genres = null;
            if (moviesRadioButton.isSelected()) {
                genres = MAPPED_DATA.getMovieGenres();
            } else if (tvShowsRadioButton.isSelected()) {
                genres = MAPPED_DATA.getTvShowGenres();
            }


            if (genres != null) {
                //Sets up all genres as ToggleButtons for user to interact with if more than one genre exists.
                List<ToggleButton> toggleButtons = new ArrayList<>();
                for (String s : genres) {
                    ToggleButton tb = new ToggleButton(s);
                    toggleButtons.add(tb);
                    tb.setFocusTraversable(false);
                }
                List<HBox> hBoxes = new ArrayList<>();
                int hBoxTracker = 0;
                for (ToggleButton toggleButton : toggleButtons) {
                    if (hBoxes.isEmpty()) {
                        hBoxes.add(new HBox(10));
                    }
                    if (hBoxes.get(hBoxTracker).getChildren().size() > 2) {
                        hBoxes.add(new HBox(10));
                        hBoxTracker++;
                    }
                    hBoxes.get(hBoxTracker).getChildren().add(toggleButton);
                }
                for (HBox hbox : hBoxes) {
                    componentsVBox.getChildren().add(hbox);
                }

                Separator separator = new Separator();
                separator.setOrientation(Orientation.HORIZONTAL);
                componentsVBox.getChildren().add(separator);

                Button closeButton = new Button("Close");
                closeButton.setOnAction(e -> popupStage.close());
                Button okButton = new Button("Ok");
                okButton.setOnAction(e -> {
                    handleGenreFiltering(toggleButtons);
                    popupStage.close();
                });

                popupStage.initModality(Modality.APPLICATION_MODAL);
                HBox buttonHbox = new HBox(10);
                buttonHbox.getChildren().addAll(closeButton, okButton);

                BorderPane root = new BorderPane();
                root.setPadding(new Insets(10));
                root.setTop(componentsVBox);
                root.setBottom(buttonHbox);
                Scene popupScene = new Scene(root, 360, 350);
                popupStage.setScene(popupScene);
                popupStage.show();
            }
        }
    }

    /**
     * Method that resets chosen filters.
     */
    private void removeFiltering() {
        if (filterIsActive) {
            filterButton.setText("Choose filter");
            filterIsActive = false;
        }
    }

    /**
     * Method that retrieves all user-selected genres to display and forwards it to be displayed.
     * @param toggleButtons List of all genre-buttons the user can interact with.
     */
    private void handleGenreFiltering(List<ToggleButton> toggleButtons) {
        List<String> selectedGenres = new ArrayList<>();
        for (ToggleButton tb : toggleButtons) {
            if (tb.isSelected()) {
                selectedGenres.add(tb.getText());
            }
        }
        if (!selectedGenres.isEmpty()) {
            filterIsActive = true;
            filterButton.setText("Remove filter");
            displayByGenre(selectedGenres);
        }
    }

    /**
     * Method responsible for getting all Content belonging to user-selected genre for filtering.
     * @param selectedGenres User-selected genres to filter by.
     */
    private void displayByGenre(List<String> selectedGenres) {
        Set<Content> contentToDisplay = new HashSet<>();
        Map<String, List<Content>> contentByGenre;
        if (moviesRadioButton.isSelected()) {
            contentByGenre = MAPPED_DATA.getMoviesByGenre();
        } else {
            contentByGenre = MAPPED_DATA.getTvShowsByGenre();
        }

        for (String s : contentByGenre.keySet()) {
            if (selectedGenres.contains(s)) {
                contentToDisplay.addAll(contentByGenre.get(s));
            }
        }
        ObservableList<Content> contentObsList = FXCollections.observableArrayList(contentToDisplay);
        populateTableView(contentObsList);
    }

    /**
     * Method that "refreshes" tableview by starting a new scrape for movies/tv-shows.
     */
    private void refreshData() {
        if (moviesRadioButton.isSelected()) {
            loadMovies();
        } else if (tvShowsRadioButton.isSelected()) {
            loadTvShows();
        }
        removeFiltering();
    }

    /**
     * Method that initiates scraping of movies, done with asynchronicity, and forwards scarped data to populate tableView.
     */
    private void loadMovies() {
        loading = true;
        handleComponentsDuringContentLoading();
        new WebScraper().scrapeMoviesAsync(data -> {
            moviesObsList = createObservableArrayList(data);
            populateTableView(moviesObsList);
            loading = false;
            handleComponentsDuringContentLoading();
            startMovieGenreMappingTask(data);
        }, progressBar);
    }

    /**
     * Method that initiates scraping of tv-shows, done with asynchronicity, and forwards scarped data to populate tableView.
     */
    private void loadTvShows() {
        loading = true;
        handleComponentsDuringContentLoading();
        new WebScraper().scrapeTvShowsAsync(data -> {
            tvShowObsList = createObservableArrayList(data);
            populateTableView(tvShowObsList);
            loading = false;
            handleComponentsDuringContentLoading();
            startTvShowGenreMappingTask(data);
        }, progressBar);
    }

    /**
     * Method that handles enabling and disabling of components during scrape (loading of data)
     */
    private void handleComponentsDuringContentLoading(){
        if (loading) {
            progressBar.setVisible(true);
            moviesRadioButton.setDisable(true);
            tvShowsRadioButton.setDisable(true);
            filterButton.setDisable(true);
        } else {
            moviesRadioButton.setDisable(false);
            tvShowsRadioButton.setDisable(false);
            filterButton.setDisable(false);
            progressBar.setVisible(false);
        }
    }

    /**
     * Initiates asynchronous mapping of movies by genre
     * @param data Takes a list of all Content.
     */
    private void startMovieGenreMappingTask(List<Content> data) {
        MAPPED_DATA.mapMovieGenresAsync(() -> {
        }, data);
    }

    /**
     * Initiates asynchronous mapping of tv-shows by genre
     * @param data Takes a list of all Content.
     */
    private void startTvShowGenreMappingTask(List<Content> data) {
        MAPPED_DATA.mapTvShowGenresAsync(() -> {
        }, data);
    }

    /**
     * Method that sends user to the Contents imdb-page using their default browser
     */
    private void showPopupWebView(Content content) {
        if (content != null) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(content.imdbUrl()));
                }
            } catch (IOException | URISyntaxException e) {
                System.out.println("Error launching browser: " + e.getMessage());
            }
        }
    }

    /**
     * Method that turns a List-object into an ObservableArrayList.
     * @param data The list in question.
     * @return Returns the data as ObservableArrayList.
     */
    private ObservableList<Content> createObservableArrayList(List<Content> data) {
        return FXCollections.observableArrayList(data);
    }

    /**
     * Populates a TableView with given data.
     * @param data Data containing Content to be displayed in TableView.
     */
    private void populateTableView(List<Content> data) {
        tableView.getItems().clear();
        tableView.getItems().addAll(data);
        posterColumn.setPrefWidth(25);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
    }

    /**
     * Method that sets up all components of the main window that are to be displayed.
     */
    private void setupComponents() {
        //Top left-side setup
        HBox leftSideButtonsHbox = new HBox(5);
        filterButton = new Button("Choose filter");
        favoriteButton = new Button("Add to favorites!");
        removeButton = new Button("Remove from favorites");
        favoriteContentButton = new Button("View favorites");
        sendButton = new Button("Send favorites");
        viewImdbPageButton = new Button("View IMDB Page");
        leftSideButtonsHbox.getChildren().addAll(filterButton, favoriteButton, removeButton, favoriteContentButton, sendButton, viewImdbPageButton);
        leftSideButtonsHbox.setPadding(new Insets(10));
        filterButton.setPrefHeight(BUTTON_HEIGHT);
        favoriteButton.setPrefHeight(BUTTON_HEIGHT);
        removeButton.setPrefHeight(BUTTON_HEIGHT);
        favoriteContentButton.setPrefHeight(BUTTON_HEIGHT);
        sendButton.setPrefHeight(BUTTON_HEIGHT);
        viewImdbPageButton.setPrefHeight(BUTTON_HEIGHT);

        //Top right-side setup
        HBox rightSideButtonsHbox = new HBox(5);
        refreshButton = new Button("Refresh");
        refreshButton.setPrefHeight(BUTTON_HEIGHT);
        rightSideButtonsHbox.getChildren().add(refreshButton);
        rightSideButtonsHbox.setPadding(new Insets(10));

        //Top setup
        topAnchorPane = new AnchorPane();
        topAnchorPane.getChildren().addAll(leftSideButtonsHbox, rightSideButtonsHbox);
        AnchorPane.setLeftAnchor(leftSideButtonsHbox, 10.0);
        AnchorPane.setRightAnchor(rightSideButtonsHbox, 10.0);

        //Bottom left-side setup
        radioButtonsHbox = new HBox(5);
        moviesRadioButton = new RadioButton("Movies");
        moviesRadioButton.setSelected(true);
        tvShowsRadioButton = new RadioButton("Tv-shows");
        ToggleGroup toggleGroup = new ToggleGroup();
        moviesRadioButton.setToggleGroup(toggleGroup);
        tvShowsRadioButton.setToggleGroup(toggleGroup);
        radioButtonsHbox.getChildren().addAll(moviesRadioButton, tvShowsRadioButton);
        radioButtonsHbox.setPadding(new Insets(10));

        //Bottom right-side setup
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(150);
        HBox progressBarBox = new HBox(progressBar);
        progressBarBox.setPadding(new Insets(10));

        //Bottom setup
        bottomAnchorPane = new AnchorPane();
        bottomAnchorPane.getChildren().addAll(radioButtonsHbox, progressBarBox);
        AnchorPane.setLeftAnchor(radioButtonsHbox, 10.0);
        AnchorPane.setRightAnchor(progressBarBox, 10.0);
    }

    /**
     * Method sets up a TableView and connects columns to relevant attributes.
     */
    private void setupTableView() {
        tableView = new TableView<>();

        posterColumn = new TableColumn<>("Poster");
        posterColumn.setSortable(false);
        posterColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPoster()));
        posterColumn.setCellFactory(column -> new PosterTableCell());
        TableColumn<Content, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Content, String[]> genresColumn = new TableColumn<>("Genres");
        genresColumn.setCellValueFactory(new PropertyValueFactory<>("genres"));
        genresColumn.setSortable(false);
        TableColumn<Content, String[]> topCastColumn = new TableColumn<>("Top Cast");
        topCastColumn.setCellValueFactory(new PropertyValueFactory<>("topCast"));
        topCastColumn.setSortable(false);
        TableColumn<Content, LocalDate> releaseDateColumn = new TableColumn<>("Release Date");
        releaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));

        List<TableColumn<Content, ?>> columns = Arrays.asList(posterColumn, titleColumn, genresColumn, topCastColumn, releaseDateColumn);
        tableView.getColumns().addAll(columns);
    }

    /**
     * Method that assigns all appropriate listeners to relevant Components.
     */
    private void setupListeners() {
        moviesRadioButton.setOnAction(e -> {
            if (moviesObsList == null) {
                loadMovies();
            } else {
                populateTableView(moviesObsList);
            }
            removeFiltering();
        });

        tvShowsRadioButton.setOnAction(e -> {
            if (tvShowObsList == null) {
                loadTvShows();
            } else {
                populateTableView(tvShowObsList);
            }
            removeFiltering();
        });

        filterButton.setOnAction(e -> filtersPopup());
        favoriteButton.setOnAction(e -> addFavorite());
        removeButton.setOnAction(e -> removeFavorite());
        favoriteContentButton.setOnAction(e -> showFavoritesPopup());
        sendButton.setOnAction(e -> sendFavoritesPopup());
        viewImdbPageButton.setOnAction(e -> {
            Content selectedItem = tableView.getSelectionModel().getSelectedItem();
            showPopupWebView(selectedItem);
        });
        refreshButton.setOnAction(e -> refreshData());
    }

}
