import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class extending TableCell in order to manage the populating of images inside a TableView.
 */
public class PosterTableCell extends TableCell<Content, Image> {
    private static final int COLUMN_SIZE = 35;

    private final ImageView imageView = new ImageView();

    /**
     * Method that handles the updating of Items inside the TableView by setting the image as necessary.
     * @param image Image to be displayed.
     * @param empty Signals of the cell is empty.
     */
    @Override
    protected void updateItem(Image image, boolean empty) {
        super.updateItem(image, empty);

        if (empty || image == null) {
            setGraphic(null);
        } else {
            imageView.setImage(image);
            imageView.setFitWidth(COLUMN_SIZE);
            imageView.setPreserveRatio(true);
            setGraphic(imageView);
        }
    }
}
