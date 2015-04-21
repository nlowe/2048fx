package eecs1510.Game.Gui;

import eecs1510.Game.Cell;
import eecs1510.Game.GameController;
import eecs1510.Game.Rules;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Created by nathan on 4/11/15
 */
public class BoardView extends Pane{

    private final MainWindow controller;
    private final GameController gameController;
    private final Canvas canvas;

    public BoardView(MainWindow controller){
        super();

        //Computed max size (4 cells of 132 pixels each + 5 boarders at 18 pixels each)
        setMinSize(618, 618);
        setMaxSize(618, 618);

        this.controller = controller;
        this.gameController = controller.getGameController();
        canvas = new Canvas();

        getChildren().add(canvas);

        // Make the Canvas fill it's parent
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
        widthProperty().addListener((resize) -> doResize());
        heightProperty().addListener((resize) -> doResize());

        // Events
        canvas.setFocusTraversable(true);
        canvas.addEventFilter(MouseEvent.ANY, (e) -> canvas.requestFocus());
        canvas.setOnKeyReleased(e -> {
            if(controller.getKeyManager().handleKey(e)){
                e.consume();
            }
        });

        canvas.requestFocus();

        this.gameController.onMoveComplete((move) -> draw());
    }

    private void doResize(){
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());

        draw();
    }

    private void draw(){
        double w = getWidth();
        double h = getHeight();

        System.out.println("DRAW ON " + w + "x" + h + " canvas");

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, w, h);

        drawCells(w, h, g);
        drawBorders(w, h, g);
    }

    private void drawBorders(double w, double h, GraphicsContext g){
        int size = Rules.BOARD_SIZE;
        g.setStroke(Color.BLACK);
        g.setLineWidth(2);

        for(int row = 0; row <= size; row++){
            for(int col = 0; col <= size; col++){
                g.strokeLine(col * (w/size), 0, col * (w/size), h);
            }
            g.strokeLine(0, row * (h/size), w, row * (h/size));
        }
    }

    private void drawCells(double w, double h, GraphicsContext g){
        int size = Rules.BOARD_SIZE;
        g.setFill(Color.RED);

        for(int row=0; row < size; row++){
            for(int col = 0; col < size; col++){
                Cell c = gameController.cellAt(row, col);

                if(c != null){
                    double cx = col * (w/size) + 1;
                    double cy = row * (h/size) + 1;

                    double cw = (w/(size))-2;
                    double ch = (h/(size))-2;
                    g.setFill(Color.RED);
                    g.fillRect(cx, cy, cw, ch);

                    g.setFill(Color.BLACK);
                    g.fillText(String.valueOf(c.getCellValue()), cx + cw/2, cy+ch/2);
                }
            }
        }
    }

    public void forceDraw() {
        draw();
    }

    public void refocus(){
        canvas.requestFocus();
    }

}
