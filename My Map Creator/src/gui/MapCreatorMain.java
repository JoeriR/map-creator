package gui;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MapCreatorMain extends Application implements EventHandler<ActionEvent> {

	// declare all GUI components
	private GridPane mainPane;
	private GridPane canvasPane;
	private LeftsideMenu leftsideMenu;
	
	private ArrayList<ArrayList<ImgButton>> imgButtons;
	
	private boolean drawGridLines = false;

	public void initializeGUIComponents() {

		imgButtons = new ArrayList<>();

		leftsideMenu = new LeftsideMenu();
		canvasPane = new GridPane();
		mainPane = new GridPane();

		for (Button b : leftsideMenu.getAllButtons()) {
			b.setOnAction(this);
		}

		canvasPane.setStyle("-fx-border-width: 5px; -fx-border-color: black; -fx-background-color: #202020;");
		
		mainPane.add(leftsideMenu, 0, 0);
		mainPane.add(canvasPane, 1, 0);
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		initializeGUIComponents();

		Scene scene = new Scene(mainPane);

		mainStage.setMinWidth(600);
		mainStage.setMinHeight(500);

		mainStage.setTitle("Map Creator - by Joeri");
		mainStage.setScene(scene);
		mainStage.sizeToScene();

		scene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				ListView<ImageView> imgListView = leftsideMenu.getImgListView();
				imgListView.setMinHeight(newValue.doubleValue() - 100);
				}
		});

		fillCanvasPane(5, 5);

		mainStage.show();
		
		// FOR TESTING
		// testMethod();

	}

	// TEST METHOD
	private void testMethod() {
		System.out.println("===============\nEXECUTING TEST METHOD ... \n===============\n");
		
		

	}

	private void fillCanvasPane(int horSize, int verSize) {
		
		// TODO updating the gridsize should keep current images, not delete everything
		
		// empty the canvasPane and de imgButtons list before adding new ImgButtons
		imgButtons.clear();
		canvasPane.getChildren().clear();
		
		for (int j = 0; j < verSize; ++j) {
			ArrayList<ImgButton> row = new ArrayList<>();
			for (int i = 0; i < horSize; ++i) {
				ImgButton newImgButton = new ImgButton();
				row.add(newImgButton);
				
				canvasPane.add(newImgButton, i, j);
			}
			imgButtons.add(row);
		}
	}

	private Canvas renderComposedCanvas() {
		Canvas canvas = new Canvas();
		GraphicsContext gc = canvas.getGraphicsContext2D();
		System.out.println("Rendering composedCanvas");

		// Calculate Width and Height
		double maxWidth = calculateCanvasWidth();
		double maxHeight = calculateCanvasHeight();

		canvas.setWidth(maxWidth);
		canvas.setHeight(maxHeight);
		System.out.println("width of Canvas = " + maxWidth);
		System.out.println("height of Canvas = " + maxHeight);

		gc.setFill(new Color(0.1, 0.1, 0.1, 1));
		gc.setStroke(new Color(0.5, 0.1, 0.1, 1));
		gc.fillRect(0, 0, maxWidth, maxHeight);

		double currentX = 0;
		double currentY = 0;
		double heightOfRow = 0;
		int rowIndex = 0;
		int columnIndex = 0;

		for (ArrayList<ImgButton> row : imgButtons) {
			currentX = 0;
			currentY += heightOfRow;
			heightOfRow = 0;
			
			columnIndex = 0;
			
			for (ImgButton ib : row) {
				double width, height;
				
				if (ib.getImage() != null) {
					Image img = ib.getImage();
					width = img.getWidth();
					height = img.getHeight();
					gc.drawImage(img, currentX, currentY);
					
					System.out.println("coordinate: (" + columnIndex + "," + rowIndex + ")   width: " + width + "   heigth: " + height);
				} 
				else {
					// TODO get width and height of a cell in gridpane
					// without using this deprecated method plz
					//
					// nvm, I can't find a way to do this without using the deprecated method 
					
					@Deprecated
					Bounds cellBounds;
					cellBounds = canvasPane.impl_getCellBounds(columnIndex, rowIndex);
					
					width = cellBounds.getWidth();
					height = cellBounds.getHeight();
					
					System.out.println("coordinate: (" + columnIndex + "," + rowIndex + ")   width: " + width + "   heigth: " + height);

					drawGridLines = true;
					if (drawGridLines) {
						gc.strokeRect(currentX, currentY, width, height);
					}
				}

				currentX += width;
				++columnIndex;

				if (height > heightOfRow)
					heightOfRow = height;
			}
			
			++rowIndex;
		}

		return canvas;
	}
	
	// TODO use a FileChooser instead of the current hard-coded path
	private boolean exportComposedCanvas(Canvas canvas) {
		boolean exportSucces = false;
		System.out.println("Exporting composedCanvas");

		File exportFile = new File("composedCanvas_test_export.png");

		SnapshotParameters params = new SnapshotParameters();
		WritableImage writeImg = canvas.snapshot(params, null);
		RenderedImage renderImg = SwingFXUtils.fromFXImage(writeImg, null);

		try {
			ImageIO.write(renderImg, "png", exportFile);
			System.out.println("export succes \n");
			System.out.println(renderImg);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("export failed");
		}

		return exportSucces;
	}

	private double calculateCanvasWidth() {
		double maxWidth = 0;
		double widthOfRow = 0;
		
		int rowIndex = 0;
		int columnIndex = 0;
		
		for (ArrayList<ImgButton> row : imgButtons) {
			widthOfRow = 0;
			columnIndex = 0;
			
			for (ImgButton ib : row) {
				
				if (ib.getImage() != null) {
					widthOfRow += ib.getWidth();
				} 
				else {
					@Deprecated
					int cellWidth = (int) canvasPane.impl_getCellBounds(columnIndex, rowIndex).getWidth();
					widthOfRow += cellWidth;
				}
				
				++columnIndex;
			}

			System.out.println("width of row = " + widthOfRow);
			
			++rowIndex;
			
			if (widthOfRow > maxWidth)
				maxWidth = widthOfRow;
		}

		return maxWidth;
	}

	private double calculateCanvasHeight() {
		double maxHeight = 0;
		double heightOfColumn = 0;
		
		int rowIndex = 0;
		int columnIndex = 0;
		int columnAmount = getAmountOfColumns();

		while (columnIndex < columnAmount) {
			heightOfColumn = 0;
			rowIndex = 0;
			
			for (ArrayList<ImgButton> ib : imgButtons) {
				ImgButton b = ib.get(columnIndex);

				if (b.getImage() != null) {
					heightOfColumn += b.getHeight();
				}
				else {
					int cellHeight = (int) canvasPane.impl_getCellBounds(columnIndex, rowIndex).getHeight();
					heightOfColumn += cellHeight;
				}
				
				++rowIndex;
			}

			System.out.println("heightOfColumn = " + heightOfColumn);

			if (heightOfColumn > maxHeight) 
				maxHeight = heightOfColumn;

			++columnIndex;
		}

		return maxHeight;
	}
	
	public int getAmountOfColumns() {
		int amountOfColumns = 1;
		int currentColumns = 0;
		
		for (ArrayList<ImgButton> row : imgButtons) {
			currentColumns = 0;
			
			for (ImgButton ib : row) {
				++currentColumns;
				
				if (currentColumns > amountOfColumns)
					amountOfColumns = currentColumns;
			}
		}
		return amountOfColumns;
	}
	
	@Override
	public void handle(ActionEvent event) {

		if (event.getSource() instanceof Button) {
			Button b = (Button) event.getSource();

			switch (b.getId()) {
				case "exportButton" :
					Canvas composedCanvas = renderComposedCanvas();
					exportComposedCanvas(composedCanvas);
					break;
					
				case "updateGridButton" :
					int newGridWidth = leftsideMenu.getNewGridWidth();
					int newGridHeight = leftsideMenu.getNewGridHeight();
					fillCanvasPane(newGridWidth, newGridHeight);
					System.out.println("updated canvas to a " + newGridWidth + "x" + newGridHeight + " size grid");
					break;
					
			}

		}

	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	/*
	 * code for importing Images into this program
	 * 
	 * File bestand; bestand = fileChooser.showOpenDialog(stageBackdoor); if
	 * (bestand != null) { canvas.leegDeStack(); URI uri = bestand.toURI();
	 * Image afbeelding = new Image(uri.toString()); }
	 */

	/*
	 * code for exporting Images from this program
	 * 
	 * SnapshotParameters params = new SnapshotParameters(); WritableImage img =
	 * canvas.snapshot(params, null); RenderedImage img2 =
	 * SwingFXUtils.fromFXImage(img, null);
	 * 
	 * try { ImageIO.write(img2, ".png", huidigBestand); } catch (IOException e)
	 * { e.printStackTrace(); }
	 */
}
