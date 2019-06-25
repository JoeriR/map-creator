package gui;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LeftsideMenu extends VBox {
	
	private final double size = 250;
	
	private NumberTextField gridWidth, gridHeight;
	private Label labelx;
	private Button updateGridButton;
	private HBox updateGridBox;
	
	private Button multiImportButton;
	private ListView<ImageView> imgListView;
	
	private Button exportButton;
	
	public LeftsideMenu() {
		// initialize components
		gridWidth = new NumberTextField("5");
		gridWidth.setMaxWidth(50);
		gridHeight = new NumberTextField("5");
		gridHeight.setMaxWidth(50);
		
		labelx = new Label("X");
		labelx.setTextFill(Color.WHITE);
		
		updateGridButton = new Button("update grid");
		updateGridButton.setId("updateGridButton");
		
		updateGridBox = new HBox();
		updateGridBox.getChildren().addAll(gridWidth, labelx, gridHeight, updateGridButton);
		updateGridBox.setSpacing(5);
		
		multiImportButton = new Button("multi-import");
		multiImportButton.setId("multiImportButton");
		
		imgListView = new ListView<ImageView>();
		imgListView.setMinWidth(size - 20);
		imgListView.setMaxWidth(size - 20);
		
		//imgListView.setPadding(new Insets(50));
		
		exportButton = new Button("export");
		exportButton.setId("exportButton");
		
		
		// add EventHandlers for Nodes that only have this class as scope
		multiImportButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Stage stage = ImgButton.stage;
				FileChooser fileChooser = ImgButton.fileChooser;
				
				List<File> inputfiles = new ArrayList<File>();
				inputfiles = fileChooser.showOpenMultipleDialog(stage);

				if (inputfiles != null) {
					fileChooser.setInitialDirectory(inputfiles.get(0).getParentFile());
					for (File f : inputfiles) {
						URI uri = f.toURI();
						Image img = new Image(uri.toString());
						ImageView imgView = new ImageView(img);
						imgView.setFitWidth(200);
						imgView.setFitHeight(200);
						imgListView.getItems().add(imgView);
						
						addListenersToListViewImage(imgListView);
					}
				}
			}
		});
		
		
		// add components to inner-VBox
		this.getChildren().add(updateGridBox);
		this.getChildren().add(multiImportButton);
		this.getChildren().add(imgListView);
		this.getChildren().add(exportButton);
		
		
		// meta settings
		this.setStyle("-fx-border-width: 5px; -fx-border-color: black; -fx-background-color: #505050;");
		
		this.setSpacing(5);
		
		this.setMinWidth(size);
		this.setMaxWidth(size);
	}
	
	public ArrayList<Button> getAllButtons() {
		ArrayList<Button> buttons = new ArrayList<>();
		buttons.add(exportButton);
		buttons.add(updateGridButton);
		buttons.add(multiImportButton);
		
		return buttons;
	}
	
	public int getNewGridWidth() {
		int i = Integer.parseInt(gridWidth.getText());
		return i;
	}
	
	public int getNewGridHeight() {
		int i = Integer.parseInt(gridHeight.getText());
		return i;
	}

	public ListView<ImageView> getImgListView() {
		return imgListView;
	}
	
	// TODO implementeer alle EventHandlers voor het drag en droppen naar ImgButtons
	private void addListenersToListViewImage(ListView<ImageView> listView) {
		
		listView.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Dragboard db = startDragAndDrop(TransferMode.ANY);
				
				ClipboardContent content = new ClipboardContent();
				Image selectedImage = listView.getSelectionModel().getSelectedItem().getImage();
				content.putImage(selectedImage);
				
				db.setContent(content);
				
				// set Graphical Effect on this imgView
				Glow glow = new Glow();
				listView.setEffect(glow);
				System.out.println("drag detected in listview");
			}
		});
		
		listView.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				event.acceptTransferModes(TransferMode.ANY);
				System.out.println("drag over in listview");
			}
		});
		
		listView.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				listView.setEffect(null);
				imgListView.getItems().remove(listView);
				System.out.println("drag done in listview");
			}
		});
	}
	
	
}
