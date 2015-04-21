package eecs1510.Game.Gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Created by nathan on 4/9/15
 */
public class WelcomeButton extends Button{

    private final ImageView imageView;
    private Label title;
    private Label description;

    public WelcomeButton(Image icon, String title, String description){
        super();

        getStyleClass().add("welcome-button");

        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        this.title = new Label(title);
        this.title.getStyleClass().add("welcome-button-title");
        this.description = new Label(description);
        this.description.getStyleClass().add("welcome-button-description");

        imageView = new ImageView();
        imageView.setImage(icon);

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setPadding(new Insets(10, 20, 10, 20));

        grid.add(imageView, 0, 0, 1, 2);
        grid.add(this.title, 1, 0);
        grid.add(this.description, 1, 1);

        this.setGraphic(grid);
    }

    public Image getIcon(){
        return imageView.getImage();
    }

    public void setIcon(Image icon){
        this.imageView.setImage(icon);
    }

    public String getTitle(){
        return title.getText();
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    public String getDescription(){
        return description.getText();
    }

    public void setDescription(String description){
        this.description.setText(description);
    }
}
