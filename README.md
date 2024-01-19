# release-date-tracker
Upcoming Releases Scraper

Overview
This Java application scrapes upcoming movie and TV show releases from IMDB, presenting them in a user-friendly JavaFX GUI. It provides a convenient way to stay updated with the latest entertainment releases.

Features
- IMDB Data Scraping: Extracts release data for movies and TV shows.
- JavaFX Interface: An easy graphical user interface for ease of use.
- Filter and Favorites: Users can filter content by genre and add releases to favorites.

Prerequisites
Before installation, ensure you have the following:
- Java JDK 11 or later.
- JavaFX SDK compatible with your JDK version.
- IDE for Java (e.g., IntelliJ IDEA, Eclipse).

Dependencies
This project relies on the following libraries:
- javax.mail.jar for email functionalities.
- jsoup-1.17.1.jar for HTML parsing and web scraping.
- activation.jar for additional email handling capabilities.

Installation
Clone the Repository:
- "git clone https://github.com/moonmilkshake/release-date-tracker.git"

Open and Configure in IDE:
- Import the project into your preferred IDE.
- Add the JavaFX SDK as a library to your project.
- Include the above dependencies in your project's library path.

Configure VM options for JavaFX:
 "--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml"

Usage
- Run WebScraperGUI.java to launch the application.
- The GUI will display upcoming releases and allow interaction through various features.
- Use the filter options to customize your view and add releases to your favorites list.

Structure
- WebScraper.java: Handles the web scraping logic.
- WebScraperGUI.java: Manages the GUI and user interactions.
- Additional classes for data management and utility functions.

Contributing

Contributions are welcome. To contribute:
- Fork the repository.
- Create a feature branch (git checkout -b feature/YourFeature).
- Commit your changes (git commit -m 'Add some feature').
- Push to the branch (git push origin feature/YourFeature).
- Open a pull request.

License
- Distributed under the MIT License. See LICENSE for more information.

Contact
Alexander Svärling - alexandersvarling@gmail.com
Project Link: https://github.com/moonmilkshake/release-date-tracker
