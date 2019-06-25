package gui;

import java.io.File;
import java.net.URI;

import com.sun.xml.internal.ws.dump.LoggingDumpTube.Position;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImgButton extends Button implements EventHandler<MouseEvent> 
{
	private Image image;

	// these are static because every button uses the same Stage and FC
	protected static FileChooser fileChooser = new FileChooser();
	protected static Stage stage = new Stage();
	
	protected static ImgButton imgButtonSwapSlot = null;

	public ImgButton() {
		this.image = null;

		//this.setOnAction(this);
		
		super.setMinWidth(50);
		super.setMinHeight(50);
		
		// TODO fix het eventhandler zooitje :S
		
		this.setOnMouseClicked(this);
		this.setOnDragDetected(this);
		setDragEventHandlers();


	}

	public ImgButton(Image img) {
		this.image = img;
		//this.setOnAction(this);
	}
	
	private void setDragEventHandlers() {
		// Draws a DropShadow-effect while dragging over this button
		setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				
				event.acceptTransferModes(TransferMode.ANY);
				
				InnerShadow innerShadow = new InnerShadow();
				innerShadow.setColor(Color.BLACK);
				innerShadow.setRadius(getWidth());
				innerShadow.setWidth(getWidth());
				innerShadow.setHeight(getHeight());
                
                setEffect(innerShadow);
                
                System.out.println("drag_over in ImgButton");
			}
		});
		
		// Removes the DropShadow-effect when the cursor leaves this button
		setOnDragExited(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				event.acceptTransferModes(TransferMode.ANY);
                setEffect(null);
                //event.consume();
                
                System.out.println("drag_exited in ImgButton");
			}
		});
		
		// transfers the images after de drag-and-drop has been finished
		setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				
				
				ImgButton origin = imgButtonSwapSlot;
				Image temp = image;
				
				// the images will only swap when they are not NULL
				if (origin.image != null && image != null) {

					image = origin.image;
					origin.image = temp;

					imgButtonSwapSlot = null;

					origin.updateImageView(origin.image);
					updateImageView(image);
				}
				
				event.setDropCompleted(true);
				System.out.println("drag_dropped in ImgButton");
			}
		});
		
	}

	public void setImage(Image img) {
		this.image = img;
	}
	
	public Image getImage() {
		return this.image;
	}
	
	private void updateImageView(Image img) {
		ImageView imgView = new ImageView(image);
		
		this.getChildren().clear();
		this.getChildren().add(imgView);

		// update button size to prevent overlapping
		this.setMinWidth(image.getWidth());
		this.setMaxWidth(image.getWidth());
		this.setMinHeight(image.getHeight());
		this.setMaxHeight(image.getHeight());
	}
	
	private void setImageThroughFileChooser() {
		System.out.println("er is op een ImgButton geklikt");

		File input;
		input = fileChooser.showOpenDialog(stage);
		if (input != null) {

			fileChooser.setInitialDirectory(input.getParentFile());

			URI uri = input.toURI();
			Image chosenImage = new Image(uri.toString());

			this.image = chosenImage;
			
			updateImageView(this.image);
			
			// TODO add a border to this button (optional)
			/*
			 * imgView.setStyle(
			 * "-fx-border-width: 20px; -fx-border-color: black");
			 * this.setStyle(
			 * "-fx-border-width: 20px; -fx-border-color: black");
			 */

		}
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			setImageThroughFileChooser();
		}
		
		// starts the drag-and-drop
		if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
			imgButtonSwapSlot = this;
			Dragboard db = startDragAndDrop(TransferMode.ANY);
			
			db.setDragViewOffsetX(50);
			db.setDragViewOffsetY(50);
			
			
			
			ClipboardContent content = new ClipboardContent();
            content.putImage(this.image);
            
            db.setContent(content);
			
			
			System.out.println("drag_detected in ImgButton");
		}
		
		
		/*
		 * oude niet werkende mouse handlers, is mischien nog handig
		 * 
		if (event.getEventType() == event.MOUSE_PRESSED) {
			imgButtonSwapSlot = this;
			swappingInProgress = true;
			
			System.out.println("MouseEvent MOUSE_PRESSED on: " + this.image);
		}
		
		if (event.getEventType() == event.MOUSE_RELEASED) {
			
			ImgButton ib = (ImgButton) event.getSource();
			
			System.out.println("event source img = " + ib.image);
			
			if (swappingInProgress == true && imgButtonSwapSlot != this && imgButtonSwapSlot != null) {
				Image temp = this.image;
				this.image = imgButtonSwapSlot.image;
				imgButtonSwapSlot.image = temp;
				
			}
			System.out.println("MouseEvent MOUSE_RELEASED on: " + this.image);
			
			// cleanup code na een release
			imgButtonSwapSlot = null;
			swappingInProgress = false;
			
		}
		*/
		
	}
	
//	@Override
//	public void handle(ActionEvent event) {
//		if (event.getSource() == this) {
//			System.out.println("er is op een ImgButton geklikt");
//
//			File input;
//			input = fileChooser.showOpenDialog(stage);
//			if (input != null) {
//
//				fileChooser.setInitialDirectory(input.getParentFile());
//
//				URI uri = input.toURI();
//				Image chosenImage = new Image(uri.toString());
//
//				this.image = chosenImage;
//				ImageView imgView = new ImageView(image);
//				
//				this.getChildren().clear();
//				this.getChildren().add(imgView);
//
//				// update button size to prevent overlapping
//				this.setMinWidth(image.getWidth());
//				this.setMaxWidth(image.getWidth());
//				this.setMinHeight(image.getHeight());
//				this.setMaxHeight(image.getHeight());
//
//				// TODO add a border to this button (optional)
//				/*
//				 * imgView.setStyle(
//				 * "-fx-border-width: 20px; -fx-border-color: black");
//				 * this.setStyle(
//				 * "-fx-border-width: 20px; -fx-border-color: black");
//				 */
//
//			}
//
//		}
//
//	}

}
