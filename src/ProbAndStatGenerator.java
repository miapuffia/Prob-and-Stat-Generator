import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ProbAndStatGenerator extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		Label dataLabel = new Label("Data (space/new line deliminated):");
		
		TextArea dataTextArea = new TextArea();
		dataTextArea.setMinHeight(100);
		
		dataTextArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.TAB && !event.isShiftDown() && !event.isControlDown()) {
					event.consume();
					
					KeyEvent newEvent = new KeyEvent(event.getSource(), event.getTarget(), event.getEventType(), event.getCharacter(), event.getText(), event.getCode(), event.isShiftDown(), true, event.isAltDown(), event.isMetaDown());
					
					dataTextArea.fireEvent(newEvent);
				}
			}
		});
		
		Button whatDataButton = new Button("What kind of data can I use?");
		
		whatDataButton.setOnAction(e -> {
			QuickAlert.show(AlertType.INFORMATION, "What kind of data can I use?", ""
				+ "The type of data each function supports is not consistant.\n"
				+ "More compatibility may be added in the future if the need arises.\n"
				+ "Currently, these are the data types supported by each function:\n"
				+ "\t- Generate table:\n"
				+ "\t\t• Transform data - Decimal or integer data, integer only transformations\n"
				+ "\t\t• Frequency table - Integer only data and class width\n"
				+ "\t\t• Standard deviation - Decimal or integer only data\n"
				+ "\t\t• Coefficient of variation - Decimal or integer data\n"
				+ "\t\t• Chebyshev's interval - Decimal or integer data\n"
				+ "\t\t• Weighted average - Decimal or integer data\n"
				+ "\t\t• Relative probability - Integer only data\n"
				+ "\t- Generate graph:\n"
				+ "\t\t• Histogram - Integer only data and class width\n"
				+ "\t\t• Relative histogram - Integer only data and class width\n"
				+ "\t\t• Ogive - Integer only data and class width\n"
				+ "\t\t• Stem-and-leaf diaply - Decimal or integer data (chosen by selection)\n"
				+ "\t\t• Box plot - Integer only data");
			});
		
		Button transformButton = new Button("Transform data");
		
		transformButton.setOnAction(e -> {
			Number[] transforms = QuickAlert.showNumericalInput("Transform data", "Go", "Addition:", "Subtraction:", "Multiplication:", "Division:", "Modulo:").get();
			
			String newDataString = "";
			
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\.?\\d\\s]", "").split("\\s");
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					try {
						int tempData = Integer.parseInt(stringData[i]);
						
						if(transforms[0] != null) {
							tempData += transforms[0].intValue();
						}
						
						if(transforms[1] != null) {
							tempData -= transforms[1].intValue();
						}
						
						if(transforms[2] != null) {
							tempData *= transforms[2].intValue();
						}
						
						if(transforms[3] != null) {
							tempData /= transforms[3].intValue();
						}
						
						if(transforms[4] != null) {
							tempData %= transforms[4].intValue();
						}
						
						newDataString += tempData + " ";
					} catch(NumberFormatException nfe) {
						try {
							double tempData = Double.parseDouble(stringData[i]);
							
							if(transforms[0] != null) {
								tempData += transforms[0].intValue();
							}
							
							if(transforms[1] != null) {
								tempData -= transforms[1].intValue();
							}
							
							if(transforms[2] != null) {
								tempData *= transforms[2].intValue();
							}
							
							if(transforms[3] != null) {
								tempData /= transforms[3].intValue();
							}
							
							if(transforms[4] != null) {
								tempData %= transforms[4].intValue();
							}
							
							newDataString += tempData + " ";
						} catch(NumberFormatException nfe2) {
							
						}
					}
				}
			}
			
			dataTextArea.setText(newDataString);
		});
		
		Label classWidthLabel = new Label(" ");
		classWidthLabel.setAlignment(Pos.CENTER);
		classWidthLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		MenuButton generateTableMenu = new MenuButton("Generate table");
		
		MenuButton generateGraphMenu = new MenuButton("Generate graph");
		
		HBox mainHBox = new HBox(10, whatDataButton, transformButton, classWidthLabel, generateTableMenu, generateGraphMenu);
		HBox.setHgrow(classWidthLabel, Priority.ALWAYS);
		
		GridPane mainTable = new GridPane();
		mainTable.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2, 2, 2, 2))));
		mainTable.setAlignment(Pos.CENTER);
		
		
		//FREQUENCY TABLE
		
		
		MenuItem mainTableMenuItem = new MenuItem("Frequency table");
		mainTableMenuItem.setOnAction(e -> {
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			Number numberClassesNumber = QuickAlert.showNumericalInput("Frequency table", "Go", "Number of classes").get()[0];
			
			if(numberClassesNumber == null) {
				QuickAlert.show(AlertType.ERROR, "Number of classes required", "There is no value for the number of classes.\nEnter the value and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			int numberClasses = numberClassesNumber.intValue();
			
			ArrayList<Integer> data = new ArrayList<Integer>();
			
			int minData = -1;
			int maxData = 0;
			
			for(int i = 0; i < stringData.length; i++) {
				try {
					minData = Integer.parseInt(stringData[i]);
					
					if(minData != -1) {
						break;
					}
				} catch(NumberFormatException nfe) {}
			}
			
			if(minData == -1) {
				QuickAlert.show(AlertType.ERROR, "Invalid data", "No valid data found in the data text area.");
				return;
			}
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					data.add(Integer.parseInt(stringData[i]));
					
					if(data.get(data.size() - 1) > maxData) {
						maxData = data.get(data.size() - 1);
					} else if(data.get(data.size() - 1) < minData) {
						minData = data.get(data.size() - 1);
					}
				}
			}
			
			int classWidth = (int) Math.ceil((double) (maxData - minData) / numberClasses);
			
			classWidthLabel.setText("Class width: " + classWidth);
			
			int[] frequency = new int[numberClasses];
			
			for(int i = 0; i < data.size(); i++) {
				try {
					frequency[(int) Math.floor((double) (data.get(i) - minData) / classWidth)]++;
				} catch(IndexOutOfBoundsException iobe) {
					QuickAlert.show(AlertType.ERROR, "Invalid data or number of classes", "The data or number of classes entered has caused a computational error.\nIf you are sure the entered data is correct, this is a bug.");
					return;
				}
			}
			
			int cumulativeFrequency = 0;
			
			mainTable.add(new Label("Class limits"), 0, 0, 2, 1);
			mainTable.add(new Label("Class boundaries"), 2, 0, 2, 1);
			mainTable.add(new Label("Midpoint"), 4, 0);
			mainTable.add(new Label("Frequency"), 5, 0);
			mainTable.add(new Label("Relative frequency"), 6, 0);
			mainTable.add(new Label("Cumulative frequency"), 7, 0);
			
			for(int i = 0; i < frequency.length; i++) {
				int lowerClassLimit = minData + (i * classWidth);
				int upperClassLimit = lowerClassLimit + classWidth - 1;
				double lowerClassBoundary = lowerClassLimit - 0.5;
				double upperClassBoundary = upperClassLimit + 0.5;
				double midpoint = (upperClassLimit + lowerClassLimit) / 2.0;
				double relativeFrequency = (double) frequency[i] / data.size();
				cumulativeFrequency += frequency[i];
				
				mainTable.add(new Label(lowerClassLimit + ""), 0, i + 1);
				mainTable.add(new Label(upperClassLimit + ""), 1, i + 1);
				mainTable.add(new Label(lowerClassBoundary + ""), 2, i + 1);
				mainTable.add(new Label(upperClassBoundary + ""), 3, i + 1);
				mainTable.add(new Label(ToDecimal.to(2, midpoint) + ""), 4, i + 1);
				mainTable.add(new Label(frequency[i] + ""), 5, i + 1);
				mainTable.add(new Label(ToDecimal.to(2, relativeFrequency) + ""), 6, i + 1);
				mainTable.add(new Label(cumulativeFrequency + ""), 7, i + 1);
			}
			
			for(int i = 0; i < mainTable.getChildren().size(); i++) {
				Label cell = ((Label) mainTable.getChildren().get(i));
				
				int borderT = 1, borderR = 1, borderB = 0, borderL = 0;

				if((i - 6) % 8 == 0) {
					borderL++;
				}
				
				if(GridPane.getRowIndex(cell) == 0) {
					borderB++;
					borderL++;
					
					cell.setStyle("-fx-font-weight: bold;");
				} else {
					if(GridPane.getRowIndex(mainTable.getChildren().get(i)) == mainTable.getRowCount() - 1) {
						borderB++;
					}
					
					if((i - 6) % 8 == 2 || (i - 6) % 8 == 4 || (i - 6) % 8 == 5 || (i - 6) % 8 == 6 || (i - 6) % 8 == 7) {
						borderL++;
					}
				}
				
				cell.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderT, borderR, borderB, borderL))));
				
				cell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				cell.setAlignment(Pos.CENTER);
				GridPane.setHgrow(mainTable.getChildren().get(i), Priority.ALWAYS);
				GridPane.setVgrow(mainTable.getChildren().get(i), Priority.ALWAYS);
				GridPane.setFillWidth(mainTable.getChildren().get(i), true);
				GridPane.setFillHeight(mainTable.getChildren().get(i), true);
			}
		});
		
		
		//STANDARD DEVIATION
		
		
		MenuItem standardDeviationMenuItem = new MenuItem("Standard deviation");
		standardDeviationMenuItem.setOnAction(e -> {
			String[] stringData = dataTextArea.getText().replaceAll("[-–—−]", "-").replaceAll("[^-\\d\\.?\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			ArrayList<Double> data = new ArrayList<Double>();
			
			double minData = -1;
			double maxData = 0;
			
			for(int i = 0; i < stringData.length; i++) {
				try {
					minData = Double.parseDouble(stringData[i]);
					
					if(minData != -1) {
						break;
					}
				} catch(NumberFormatException nfe) {}
			}
			
			if(minData == -1) {
				QuickAlert.show(AlertType.ERROR, "Invalid data", "No valid data found in the data text area.");
				return;
			}
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("") && !stringData[i].equals("-")) {
					data.add(Double.parseDouble(stringData[i]));
					
					if(data.get(data.size() - 1) > maxData) {
						maxData = data.get(data.size() - 1);
					} else if(data.get(data.size() - 1) < minData) {
						minData = data.get(data.size() - 1);
					}
				}
			}
			
			Collections.sort(data);
			
			double range = maxData - minData;
			double median = getMedian(data);
			double sum = 0;
			double sumSquared = 0;
			int maxMode = 1;
			int currentMode = 1;
			double tempData = data.get(0);
			
			ArrayList<Double> modes = new ArrayList<Double>();
			
			for(int i = 0; i < data.size(); i++) {
				sum += data.get(i);
				sumSquared += Math.pow(data.get(i), 2);
				
				if(i != 0 && data.get(i) == tempData) {
					currentMode++;
					
					if(currentMode > maxMode) {
						maxMode = currentMode;
						modes.clear();
						modes.add(data.get(i));
					} else if(currentMode == maxMode) {
						modes.add(data.get(i));
					}
				} else {
					tempData = data.get(i);
					currentMode = 1;
				}
			}
			
			double mean = sum / data.size();
			
			double sumOfSquares = 0;
			
			for(int i = 0; i < data.size(); i++) {
				sumOfSquares += Math.pow(data.get(i) - mean, 2);
			}
			
			double sampleVariance = sumOfSquares / (data.size() - 1);
			double populationVariance = sumOfSquares / data.size();
			double sampleCoefficientVariation = (sampleVariance / mean) * 100;
			
			double sampleStandardDeviation = Math.sqrt(sampleVariance);
			double populationStandardDeviation = Math.sqrt(populationVariance);
			double populationCoefficientVariation = (populationVariance / mean) * 100;
			
			Label rangeLabel = new Label("Range:\n" + range);
			rangeLabel.setTextAlignment(TextAlignment.CENTER);
			rangeLabel.setAlignment(Pos.CENTER);
			rangeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			rangeLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
			rangeLabel.setPadding(new Insets(5, 5, 5, 5));

			Label sumLabel = new Label("Σx:\n" + sum);
			sumLabel.setTextAlignment(TextAlignment.CENTER);
			sumLabel.setAlignment(Pos.CENTER);
			sumLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			sumLabel.setPadding(new Insets(5, 5, 5, 5));
			sumLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));

			Label sumSquaredLabel = new Label("Σx²:\n" + sumSquared);
			sumSquaredLabel.setTextAlignment(TextAlignment.CENTER);
			sumSquaredLabel.setAlignment(Pos.CENTER);
			sumSquaredLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			sumSquaredLabel.setPadding(new Insets(5, 5, 5, 5));
			
			Label meanLabel = new Label("Mean:\n" + mean);
			meanLabel.setTextAlignment(TextAlignment.CENTER);
			meanLabel.setAlignment(Pos.CENTER);
			meanLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));
			meanLabel.setPadding(new Insets(5, 5, 5, 5));
			meanLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

			Label medianLabel = new Label("Median:\n" + median);
			medianLabel.setTextAlignment(TextAlignment.CENTER);
			medianLabel.setAlignment(Pos.CENTER);
			medianLabel.setPadding(new Insets(5, 5, 5, 5));
			medianLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

			Label modeLabel = new Label();
			modeLabel.setTextAlignment(TextAlignment.CENTER);
			modeLabel.setAlignment(Pos.CENTER);
			modeLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 1))));
			modeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			modeLabel.setPadding(new Insets(5, 5, 5, 5));
			
			if(modes.size() > 1) {
				modeLabel.setText("Modes:\n" + modes.toString().replaceAll("\\[?\\]?", ""));
			} else if(modes.size() == 1) {
				modeLabel.setText("Mode:\n" + modes.toString().replaceAll("\\[?\\]?", ""));
			} else {
				modeLabel.setText("No mode\n ");
			}
			
			Label sumOfSquaresLabel = new Label("Sum of squares:\n" + sumOfSquares);
			sumOfSquaresLabel.setTextAlignment(TextAlignment.CENTER);
			sumOfSquaresLabel.setAlignment(Pos.CENTER);
			sumOfSquaresLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			sumOfSquaresLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 1, 0))));
			sumOfSquaresLabel.setPadding(new Insets(5, 5, 5, 5));
			
			Label sampleVarianceLabel = new Label("Sample variance:\n" + sampleVariance);
			sampleVarianceLabel.setTextAlignment(TextAlignment.CENTER);
			sampleVarianceLabel.setAlignment(Pos.CENTER);
			sampleVarianceLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			sampleVarianceLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));
			sampleVarianceLabel.setPadding(new Insets(5, 5, 5, 5));
			
			Label populationVarianceLabel = new Label("Polulation variance:\n" + populationVariance);
			populationVarianceLabel.setTextAlignment(TextAlignment.CENTER);
			populationVarianceLabel.setAlignment(Pos.CENTER);
			populationVarianceLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			populationVarianceLabel.setPadding(new Insets(5, 5, 5, 5));
			
			Label sampleStandardDeviationLabel = new Label("Sample standard deviation:\n" + sampleStandardDeviation);
			sampleStandardDeviationLabel.setTextAlignment(TextAlignment.CENTER);
			sampleStandardDeviationLabel.setAlignment(Pos.CENTER);
			sampleStandardDeviationLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			sampleStandardDeviationLabel.setPadding(new Insets(5, 5, 5, 5));
			sampleStandardDeviationLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));
			
			Label populationStandardDeviationLabel = new Label("Population standard deviation:\n" + populationStandardDeviation);
			populationStandardDeviationLabel.setTextAlignment(TextAlignment.CENTER);
			populationStandardDeviationLabel.setAlignment(Pos.CENTER);
			populationStandardDeviationLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			populationStandardDeviationLabel.setPadding(new Insets(5, 5, 5, 5));
			
			Label sampleCoefficientVarianceLabel = new Label("Sample coefficient\nof variance:\n" + sampleCoefficientVariation + "%");
			sampleCoefficientVarianceLabel.setTextAlignment(TextAlignment.CENTER);
			sampleCoefficientVarianceLabel.setAlignment(Pos.CENTER);
			sampleCoefficientVarianceLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			sampleCoefficientVarianceLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));
			sampleCoefficientVarianceLabel.setPadding(new Insets(5, 5, 5, 5));
			
			Label populationCoefficientVarianceLabel = new Label("Population coefficient\nof variance:\n" + populationCoefficientVariation + "%");
			populationCoefficientVarianceLabel.setTextAlignment(TextAlignment.CENTER);
			populationCoefficientVarianceLabel.setAlignment(Pos.CENTER);
			populationCoefficientVarianceLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			populationCoefficientVarianceLabel.setPadding(new Insets(5, 5, 5, 5));
			
			mainTable.add(rangeLabel, 0, 0);
			
			HBox sumHBox = new HBox(sumLabel, sumSquaredLabel);
			HBox.setHgrow(sumLabel, Priority.ALWAYS);
			HBox.setHgrow(sumSquaredLabel, Priority.ALWAYS);
			sumHBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
			
			mainTable.add(sumHBox, 0, 1);
			
			HBox meanMedianModeHBox = new HBox(meanLabel, medianLabel, modeLabel);
			HBox.setHgrow(meanLabel, Priority.ALWAYS);
			HBox.setHgrow(medianLabel, Priority.ALWAYS);
			HBox.setHgrow(modeLabel, Priority.ALWAYS);
			
			mainTable.add(meanMedianModeHBox, 0, 2);
			mainTable.add(sumOfSquaresLabel, 0, 3);
			
			HBox varianceHBox = new HBox(sampleVarianceLabel, populationVarianceLabel);
			HBox.setHgrow(sampleVarianceLabel, Priority.ALWAYS);
			HBox.setHgrow(populationVarianceLabel, Priority.ALWAYS);
			
			mainTable.add(varianceHBox, 0, 4);
			
			HBox standardDeviationHBox = new HBox(sampleStandardDeviationLabel, populationStandardDeviationLabel);
			HBox.setHgrow(sampleStandardDeviationLabel, Priority.ALWAYS);
			HBox.setHgrow(populationStandardDeviationLabel, Priority.ALWAYS);
			standardDeviationHBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 1, 0))));

			mainTable.add(standardDeviationHBox, 0, 5);
			
			HBox coefficientVarianceHBox = new HBox(sampleCoefficientVarianceLabel, populationCoefficientVarianceLabel);
			HBox.setHgrow(sampleCoefficientVarianceLabel, Priority.ALWAYS);
			HBox.setHgrow(populationCoefficientVarianceLabel, Priority.ALWAYS);

			mainTable.add(coefficientVarianceHBox, 0, 6);
		});
		
		
		//COEFFICIENT OF VARIATION
		
		
		MenuItem coefficientVariationMenuItem = new MenuItem("Coefficient of variation");
		coefficientVariationMenuItem.setOnAction(e -> {
			QuickAlert.show(AlertType.INFORMATION, "Data format", "This function requires exactly 2 data values.\nThat is the mean (x̄ or μ) and the standard deviation (s, σ).");
			
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\.?\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			ArrayList<Double> data = new ArrayList<Double>();
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					data.add(Double.parseDouble(stringData[i]));
				}
			}
			
			if(data.size() != 2) {
				QuickAlert.show(AlertType.ERROR, "Incorrect data", "Exactly 2 values are required for this function.\nCheck your data and try again.");
				return;
			}
			
			double coefficientVariance = (data.get(1) / data.get(0)) * 100;
			
			Label coefficientVarianceLabel = new Label("Coefficient of variance:\n" + coefficientVariance + "%");
			coefficientVarianceLabel.setTextAlignment(TextAlignment.CENTER);
			coefficientVarianceLabel.setAlignment(Pos.CENTER);
			coefficientVarianceLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			
			mainTable.add(coefficientVarianceLabel, 0, 0);
		});
		
		
		//CHEBYSHEV INTERVAL
		
		
		MenuItem chebyshevIntervalMenuItem = new MenuItem("Chebyshev interval");
		chebyshevIntervalMenuItem.setOnAction(e -> {
			QuickAlert.show(AlertType.INFORMATION, "Data format", "This function requires exactly 3 data values.\nFirst is the mean (x̄ or μ), second is the standard deviation (s or σ),\nand third is the coefficient of variation as a percentage.");
			
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\.?\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			ArrayList<Double> data = new ArrayList<Double>();
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					data.add(Double.parseDouble(stringData[i]));
				}
			}
			
			if(data.size() != 3) {
				QuickAlert.show(AlertType.ERROR, "Incorrect data", "Exactly 3 values are required for this function.\nCheck your data and try again.");
				return;
			}
			
			double calculatedStandardDeviation = Math.sqrt(-100 / (data.get(2) - 100));
			
			double lowerInterval = data.get(0) - (calculatedStandardDeviation * data.get(1));
			double upperInterval = data.get(0) + (calculatedStandardDeviation * data.get(1));
			
			Label lowerIntervalLabel = new Label("Lower Chebyshev interval:\n" + lowerInterval);
			lowerIntervalLabel.setTextAlignment(TextAlignment.CENTER);
			lowerIntervalLabel.setAlignment(Pos.CENTER);
			lowerIntervalLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			lowerIntervalLabel.setPadding(new Insets(5, 5, 5, 5));
			lowerIntervalLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));
			
			Label upperIntervalLabel = new Label("Upper Chebyshev interval:\n" + upperInterval);
			upperIntervalLabel.setTextAlignment(TextAlignment.CENTER);
			upperIntervalLabel.setAlignment(Pos.CENTER);
			upperIntervalLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			upperIntervalLabel.setPadding(new Insets(5, 5, 5, 5));

			mainTable.add(lowerIntervalLabel, 0, 0);
			mainTable.add(upperIntervalLabel, 1, 0);
		});
		
		
		//WEIGHTED AVERAGE
		
		
		MenuItem weightedAverageMenuItem = new MenuItem("Weighted average");
		weightedAverageMenuItem.setOnAction(e -> {
			QuickAlert.show(AlertType.INFORMATION, "Data format", "To properly calculate weighted average, values and weights are taken in pairs.\nAs long as every value is followed by its weight, you may use whatever format or style you wish.\nSpecial characters will not have any effect.");
			
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\.?\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			ArrayList<Double> data = new ArrayList<Double>();
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					data.add(Double.parseDouble(stringData[i]));
				}
			}
			
			if(data.size() % 2 == 1) {
				QuickAlert.show(AlertType.ERROR, "Missing data", "There are an odd number of data values.\nThis cannot happen for weighted value pairs.\nCheck your data and try again.");
				return;
			}
			
			double weightedTotal = 0;
			double weight = 0;
			
			for(int i = 0; i < data.size(); i += 2) {
				weightedTotal += data.get(i) * data.get(i + 1);
				weight += data.get(i + 1);
			}
			
			mainTable.add(new Label("Weighted average:"), 0, 0);
			mainTable.add(new Label((weightedTotal / weight) + ""), 0, 1);
		});
		
		
		//RELATIVE PROBABILITY
		
		
		MenuItem relativeProbabilityMenuItem = new MenuItem("Relative probability");
		relativeProbabilityMenuItem.setOnAction(e -> {
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			ArrayList<Integer> data = new ArrayList<Integer>();
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					data.add(Integer.parseInt(stringData[i]));
				}
			}
			
			int total = 0;
			
			for(int i = 0; i < data.size(); i++) {
				total += data.get(i);
			}
			
			Label dataHeaderLabel = new Label("Data");
			dataHeaderLabel.setPadding(new Insets(5, 5, 5, 5));
			dataHeaderLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
			
			Label slashHeaderLabel = new Label("/");
			slashHeaderLabel.setPadding(new Insets(5, 5, 5, 5));
			slashHeaderLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
			
			Label totalHeaderLabel = new Label("Total");
			totalHeaderLabel.setPadding(new Insets(5, 5, 5, 5));
			totalHeaderLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
			
			Label equalHeaderLabel = new Label("=");
			equalHeaderLabel.setPadding(new Insets(5, 5, 5, 5));
			equalHeaderLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

			Label probabilityHeaderLabel = new Label("Probability");
			probabilityHeaderLabel.setPadding(new Insets(5, 5, 5, 5));
			probabilityHeaderLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
			
			mainTable.add(dataHeaderLabel, 0, 0);
			GridPane.setHalignment(dataHeaderLabel, HPos.RIGHT);
			mainTable.add(slashHeaderLabel, 1, 0);
			mainTable.add(totalHeaderLabel, 2, 0);
			mainTable.add(equalHeaderLabel, 3, 0);
			mainTable.add(probabilityHeaderLabel, 4, 0);
			
			for(int i = 0; i < data.size(); i++) {
				Label dataValueLabel = new Label(data.get(i) + "");
				dataValueLabel.setPadding(new Insets(5, 5, 5, 5));

				Label slashValueLabel = new Label("/");
				slashValueLabel.setPadding(new Insets(5, 5, 5, 5));
				
				Label totalValueLabel = new Label(total + "");
				totalValueLabel.setPadding(new Insets(5, 5, 5, 5));

				Label equalValueLabel = new Label("=");
				equalValueLabel.setPadding(new Insets(5, 5, 5, 5));

				Label probabilityValueLabel = new Label(((double) data.get(i) / total) + "");
				probabilityValueLabel.setPadding(new Insets(5, 5, 5, 5));
				
				mainTable.add(dataValueLabel, 0, i + 1);
				GridPane.setHalignment(dataValueLabel, HPos.RIGHT);
				mainTable.add(slashValueLabel, 1, i + 1);
				mainTable.add(totalValueLabel, 2, i + 1);
				mainTable.add(equalValueLabel, 3, i + 1);
				mainTable.add(probabilityValueLabel, 4, i + 1);
			}
		});
		
		
		//PROBABILITY OF A, B
		
		
		MenuItem probabilityABMenuItem = new MenuItem("Probability of A, B");
		probabilityABMenuItem.setOnAction(e -> {
			QuickAlert.show(AlertType.INFORMATION, "Data not used", "The only data used for this function will be entered in the table below.");
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			TextField aValueTextView = new TextField();
			aValueTextView.setMaxWidth(150);
			
			HBox aValueHBox = new HBox(10, new Label("P(A): "), aValueTextView);
			aValueHBox.setAlignment(Pos.CENTER_LEFT);
			aValueHBox.setPadding(new Insets(5, 5, 5, 5));
			
			TextField bValueTextView = new TextField();
			bValueTextView.setMaxWidth(150);
			
			HBox bValueHBox = new HBox(10, new Label("P(B): "), bValueTextView);
			bValueHBox.setAlignment(Pos.CENTER_RIGHT);
			bValueHBox.setPadding(new Insets(5, 5, 5, 5));
			
			HBox valuesHBox = new HBox(aValueHBox, bValueHBox);
			valuesHBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1), new Insets(5, 5, 5, 5))));
			valuesHBox.setPadding(new Insets(5, 5, 5, 5));
			HBox.setHgrow(aValueHBox, Priority.ALWAYS);
			
			Label independentAAndBLabel = new Label("P(A and B): ");
			Label independentAOrBLabel = new Label("P(A or B): ");
			Label independentAIfBLabel = new Label("P(A | B): ");
			Label independentBIfALabel = new Label("P(B | A): ");
			
			VBox independentVBox = new VBox(10, new Label("Independent:"), independentAAndBLabel, independentAOrBLabel, independentAIfBLabel, independentBIfALabel);
			independentVBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1), new Insets(5, 5, 5, 5))));
			independentVBox.setPadding(new Insets(5, 5, 5, 5));
			
			Label mutuallyExclusiveAOrBLabel = new Label("P(A or B): ");
			
			VBox mutuallyExclusiveVBox = new VBox(10, new Label("Mutually exclusive:"), new Label("P(A and B): 0.0"), mutuallyExclusiveAOrBLabel, new Label("P(A | B): 0.0"), new Label("P(B | A): 0.0"));
			mutuallyExclusiveVBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1), new Insets(5, 5, 5, 5))));
			mutuallyExclusiveVBox.setPadding(new Insets(5, 5, 5, 5));
			
			TextField aAndBTextField = new TextField();
			aAndBTextField.setMaxWidth(150);
			
			TextField aOrBTextField = new TextField();
			aOrBTextField.setMaxWidth(150);
			
			TextField aIfBTextField = new TextField();
			aIfBTextField.setMaxWidth(150);
			
			TextField bIfATextField = new TextField();
			bIfATextField.setMaxWidth(150);
			
			aValueTextView.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(!newValue.matches("^\\d*\\.?\\d*$")) {
						aValueTextView.setText(oldValue);
						return;
					}
					
					try {
						independentAAndBLabel.setText("P(A and B): " + (Double.parseDouble(aValueTextView.getText()) * Double.parseDouble(bValueTextView.getText())));
						
						independentAOrBLabel.setText("P(A or B): " + (Double.parseDouble(aValueTextView.getText()) + Double.parseDouble(bValueTextView.getText()) - (Double.parseDouble(aValueTextView.getText()) * Double.parseDouble(bValueTextView.getText()))));
						
						independentAIfBLabel.setText("P(A | B): " + Double.parseDouble(aValueTextView.getText()));
						
						independentBIfALabel.setText("P(B | A): " + Double.parseDouble(bValueTextView.getText()));
						
						mutuallyExclusiveAOrBLabel.setText("P(A or B): " + (Double.parseDouble(aValueTextView.getText()) + Double.parseDouble(bValueTextView.getText())));
					} catch(NumberFormatException nfe) {}
				}
			});
			
			bValueTextView.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(!newValue.matches("^\\d*\\.?\\d*$")) {
						aValueTextView.setText(oldValue);
						return;
					}
					
					try {
						independentAAndBLabel.setText("P(A and B): " + (Double.parseDouble(aValueTextView.getText()) * Double.parseDouble(bValueTextView.getText())));
						
						independentAOrBLabel.setText("P(A or B): " + (Double.parseDouble(aValueTextView.getText()) + Double.parseDouble(bValueTextView.getText()) - (Double.parseDouble(aValueTextView.getText()) * Double.parseDouble(bValueTextView.getText()))));
						
						independentAIfBLabel.setText("P(A | B): " + Double.parseDouble(aValueTextView.getText()));
						
						independentBIfALabel.setText("P(B | A): " + Double.parseDouble(bValueTextView.getText()));
						
						mutuallyExclusiveAOrBLabel.setText("P(A or B): " + (Double.parseDouble(aValueTextView.getText()) + Double.parseDouble(bValueTextView.getText())));
					} catch(NumberFormatException nfe) {}
				}
			});
			
			aAndBTextField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(!newValue.matches("^\\d*\\.?\\d*$")) {
						aAndBTextField.setText(oldValue);
						return;
					}
					
					try {
						if(Double.parseDouble(bValueTextView.getText()) != 0) {
							aIfBTextField.setText((Double.parseDouble(aAndBTextField.getText()) / Double.parseDouble(bValueTextView.getText())) + "");
						}
						
						if(Double.parseDouble(aValueTextView.getText()) != 0) {
							bIfATextField.setText((Double.parseDouble(aAndBTextField.getText()) / Double.parseDouble(aValueTextView.getText())) + "");
						}
						
						aOrBTextField.setText((Double.parseDouble(aValueTextView.getText()) + Double.parseDouble(bValueTextView.getText()) - (Double.parseDouble(aAndBTextField.getText()))) + "");
					} catch(NumberFormatException nfe) {}
				}
			});
			
			aOrBTextField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(!newValue.matches("^\\d*\\.?\\d*$")) {
						aOrBTextField.setText(oldValue);
						return;
					}
					
					try {
						aAndBTextField.setText((Double.parseDouble(aValueTextView.getText()) + Double.parseDouble(bValueTextView.getText()) - (Double.parseDouble(aOrBTextField.getText()))) + "");
					} catch(NumberFormatException nfe) {}
				}
			});
			
			aIfBTextField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(!newValue.matches("^\\d*\\.?\\d*$")) {
						aIfBTextField.setText(oldValue);
						return;
					}
					
					try {
						aAndBTextField.setText((Double.parseDouble(bValueTextView.getText()) * Double.parseDouble(aIfBTextField.getText())) + "");
					} catch(NumberFormatException nfe) {}
				}
			});
			
			bIfATextField.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
					if(!newValue.matches("^\\d*\\.?\\d*$")) {
						bIfATextField.setText(oldValue);
						return;
					}
					
					try {
						aAndBTextField.setText((Double.parseDouble(aValueTextView.getText()) * Double.parseDouble(bIfATextField.getText())) + "");
					} catch(NumberFormatException nfe) {}
				}
			});
			
			GridPane generalGridPane = new GridPane();
			generalGridPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 1, 1, 1), new Insets(5, 5, 5, 5))));
			generalGridPane.setPadding(new Insets(5, 5, 5, 5));
			generalGridPane.setHgap(10);
			generalGridPane.setVgap(10);

			generalGridPane.add(new Label("General:"), 0, 0, 2, 1);
			generalGridPane.add(new Label("P(A and B): "), 0, 1);
			generalGridPane.add(aAndBTextField, 1, 1);
			generalGridPane.add(new Label("P(A or B): "), 0, 2);
			generalGridPane.add(aOrBTextField, 1, 2);
			generalGridPane.add(new Label("P(A | B): "), 0, 3);
			generalGridPane.add(aIfBTextField, 1, 3);
			generalGridPane.add(new Label("P(B | A): "), 0, 4);
			generalGridPane.add(bIfATextField, 1, 4);
			
			mainTable.add(valuesHBox, 0, 0, 2, 1);
			mainTable.add(independentVBox, 0, 1);
			mainTable.add(mutuallyExclusiveVBox, 0, 2);
			mainTable.add(generalGridPane, 1, 1, 1, 2);
		});
		
		generateTableMenu.getItems().addAll(mainTableMenuItem, standardDeviationMenuItem, coefficientVariationMenuItem, chebyshevIntervalMenuItem, weightedAverageMenuItem, relativeProbabilityMenuItem, probabilityABMenuItem);
		
		
		//HISTOGRAM
		
		
		MenuItem histogramMenuItem = new MenuItem("Histogram");
		histogramMenuItem.setOnAction(e -> {
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			Number numberClassesNumber = QuickAlert.showNumericalInput("Histogram", "Go", "Number of classes").get()[0];
			
			if(numberClassesNumber == null) {
				QuickAlert.show(AlertType.ERROR, "Number of classes required", "There is no value for the number of classes.\nEnter the value and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			int numberClasses = numberClassesNumber.intValue();
			
			ArrayList<Integer> data = new ArrayList<Integer>();
			
			int minData = -1;
			int maxData = 0;
			
			for(int i = 0; i < stringData.length; i++) {
				try {
					minData = Integer.parseInt(stringData[i]);
					
					if(minData != -1) {
						break;
					}
				} catch(NumberFormatException nfe) {}
			}
			
			if(minData == -1) {
				QuickAlert.show(AlertType.ERROR, "Invalid data", "No valid data found in the data text area.");
				return;
			}
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					data.add(Integer.parseInt(stringData[i]));
					
					if(data.get(data.size() - 1) > maxData) {
						maxData = data.get(data.size() - 1);
					} else if(data.get(data.size() - 1) < minData) {
						minData = data.get(data.size() - 1);
					}
				}
			}
			
			int classWidth = (int) Math.ceil((double) (maxData - minData) / numberClasses);
			
			classWidthLabel.setText("Class width: " + classWidth);
			
			int[] frequency = new int[numberClasses];
			
			for(int i = 0; i < data.size(); i++) {
				try {
					frequency[(int) Math.floor((double) (data.get(i) - minData) / classWidth)]++;
				} catch(IndexOutOfBoundsException iobe) {
					QuickAlert.show(AlertType.ERROR, "Invalid data or number of classes", "The data or number of classes entered has caused a computational error.\nIf you are sure the entered data is correct, this is a bug.");
					return;
				}
			}
			
			CategoryAxis xAxis = new CategoryAxis();
			
			NumberAxis yAxis = new NumberAxis();
			
			BarChart<String, Number> histogram = new BarChart<String, Number>(xAxis, yAxis);
			histogram.setTitle("Histogram");
			histogram.setId("BarChart");
			histogram.getXAxis().setLabel("Data");
			histogram.getYAxis().setLabel("Frequency");
			histogram.setLegendVisible(false);
			histogram.setBarGap(0);
			histogram.setCategoryGap(0);
			histogram.setMinWidth(500);
			
			mainTable.setAlignment(Pos.CENTER);
			
			XYChart.Series<String, Number> lowerSeries = new XYChart.Series<String, Number>();
			
			Label textData = new Label();
			
			for(int i = 0; i < frequency.length; i++) {
				int lowerClassLimit = minData + (i * classWidth);
				int upperClassLimit = lowerClassLimit + classWidth - 1;
				double lowerClassBoundary = lowerClassLimit - 0.5;
				double upperClassBoundary = upperClassLimit + 0.5;
				
				lowerSeries.getData().add(new XYChart.Data<String, Number>(lowerClassBoundary + "", frequency[i]));
				
				textData.setText(textData.getText() + "(" + lowerClassBoundary + " - " + upperClassBoundary + ", " + ToDecimal.to(2, frequency[i]) + ")\n");
			}
			
			lowerSeries.getData().add(new XYChart.Data<String, Number>((maxData + 0.5) + "", 0));
			
			histogram.getData().add(lowerSeries);
			
			mainTable.add(histogram, 0, 0);
			mainTable.add(textData, 1, 0);
		});
		
		
		//RELATIVE HISTOGRAM
		
		
		MenuItem relativeHistogramMenuItem = new MenuItem("Relative histogram");
		relativeHistogramMenuItem.setOnAction(e -> {
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			Number numberClassesNumber = QuickAlert.showNumericalInput("Relative histogram", "Go", "Number of classes").get()[0];
			
			if(numberClassesNumber == null) {
				QuickAlert.show(AlertType.ERROR, "Number of classes required", "There is no value for the number of classes.\nEnter the value and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			int numberClasses = numberClassesNumber.intValue();
			
			ArrayList<Integer> data = new ArrayList<Integer>();
			
			int minData = -1;
			int maxData = 0;
			
			for(int i = 0; i < stringData.length; i++) {
				try {
					minData = Integer.parseInt(stringData[i]);
					
					if(minData != -1) {
						break;
					}
				} catch(NumberFormatException nfe) {}
			}
			
			if(minData == -1) {
				QuickAlert.show(AlertType.ERROR, "Invalid data", "No valid data found in the data text area.");
				return;
			}
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					data.add(Integer.parseInt(stringData[i]));
					
					if(data.get(data.size() - 1) > maxData) {
						maxData = data.get(data.size() - 1);
					} else if(data.get(data.size() - 1) < minData) {
						minData = data.get(data.size() - 1);
					}
				}
			}
			
			int classWidth = (int) Math.ceil((double) (maxData - minData) / numberClasses);
			
			classWidthLabel.setText("Class width: " + classWidth);
			
			int[] frequency = new int[numberClasses];
			
			for(int i = 0; i < data.size(); i++) {
				try {
					frequency[(int) Math.floor((double) (data.get(i) - minData) / classWidth)]++;
				} catch(IndexOutOfBoundsException iobe) {
					QuickAlert.show(AlertType.ERROR, "Invalid data or number of classes", "The data or number of classes entered has caused a computational error.\nIf you are sure the entered data is correct, this is a bug.");
					return;
				}
			}
			
			CategoryAxis xAxis = new CategoryAxis();
			
			NumberAxis yAxis = new NumberAxis();
			
			BarChart<String, Number> histogram = new BarChart<String, Number>(xAxis, yAxis);
			histogram.setTitle("Histogram");
			histogram.setId("BarChart");
			histogram.getXAxis().setLabel("Data");
			histogram.getYAxis().setLabel("Frequency");
			histogram.setLegendVisible(false);
			histogram.setBarGap(0);
			histogram.setCategoryGap(0);
			histogram.setMinWidth(500);
			
			XYChart.Series<String, Number> lowerSeries = new XYChart.Series<String, Number>();
			
			Label textData = new Label();
			
			for(int i = 0; i < frequency.length; i++) {
				int lowerClassLimit = minData + (i * classWidth);
				int upperClassLimit = lowerClassLimit + classWidth - 1;
				double lowerClassBoundary = lowerClassLimit - 0.5;
				double upperClassBoundary = upperClassLimit + 0.5;
				double relativeFrequency = (double) frequency[i] / data.size();
				
				lowerSeries.getData().add(new XYChart.Data<String, Number>(lowerClassBoundary + "", relativeFrequency));
				
				textData.setText(textData.getText() + "(" + lowerClassBoundary + " - " + upperClassBoundary + ", " + ToDecimal.to(2, relativeFrequency) + ")\n");
			}
			
			lowerSeries.getData().add(new XYChart.Data<String, Number>((maxData + 0.5) + "", 0));
			
			histogram.getData().add(lowerSeries);
			
			mainTable.add(histogram, 0, 0);
			mainTable.add(textData, 1, 0);
		});
		
		
		//OGIVE
		
		
		MenuItem ogiveMenuItem = new MenuItem("Ogive");
		ogiveMenuItem.setOnAction(e -> {
			String[] stringData = dataTextArea.getText().replaceAll("[^\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			Number numberClassesNumber = QuickAlert.showNumericalInput("Ogive", "Go", "Number of classes").get()[0];
			
			if(numberClassesNumber == null) {
				QuickAlert.show(AlertType.ERROR, "Number of classes required", "There is no value for the number of classes.\nEnter the value and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			int numberClasses = numberClassesNumber.intValue();
			
			ArrayList<Integer> data = new ArrayList<Integer>();
			
			int minData = -1;
			int maxData = 0;
			
			for(int i = 0; i < stringData.length; i++) {
				try {
					minData = Integer.parseInt(stringData[i]);
					
					if(minData != -1) {
						break;
					}
				} catch(NumberFormatException nfe) {}
			}
			
			if(minData == -1) {
				QuickAlert.show(AlertType.ERROR, "Invalid data", "No valid data found in the data text area.");
				return;
			}
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					data.add(Integer.parseInt(stringData[i]));
					
					if(data.get(data.size() - 1) > maxData) {
						maxData = data.get(data.size() - 1);
					} else if(data.get(data.size() - 1) < minData) {
						minData = data.get(data.size() - 1);
					}
				}
			}
			
			int classWidth = (int) Math.ceil((double) (maxData - minData) / numberClasses);
			
			classWidthLabel.setText("Class width: " + classWidth);
			
			int[] frequency = new int[numberClasses];
			
			for(int i = 0; i < data.size(); i++) {
				try {
					frequency[(int) Math.floor((double) (data.get(i) - minData) / classWidth)]++;
				} catch(IndexOutOfBoundsException iobe) {
					QuickAlert.show(AlertType.ERROR, "Invalid data or number of classes", "The data or number of classes entered has caused a computational error.\nIf you are sure the entered data is correct, this is a bug.");
					return;
				}
			}
			
			int cumulativeFrequency = 0;
			
			NumberAxis xAxis = new NumberAxis();
			
			NumberAxis yAxis = new NumberAxis();
			
			LineChart<Number, Number> histogram = new LineChart<Number, Number>(xAxis, yAxis);
			histogram.setTitle("Histogram");
			histogram.getXAxis().setLabel("Data");
			histogram.getYAxis().setLabel("Frequency");
			histogram.setLegendVisible(false);
			histogram.setMinWidth(500);
			
			XYChart.Series<Number, Number> dataSeries = new XYChart.Series<Number, Number>();
			
			Label textData = new Label();
			
			dataSeries.getData().add(new XYChart.Data<Number, Number>(minData - 0.5, 0));
			
			textData.setText("(" + (minData - 0.5) + ", 0)");
			
			for(int i = 0; i < frequency.length; i++) {
				int lowerClassLimit = minData + (i * classWidth);
				int upperClassLimit = lowerClassLimit + classWidth - 1;
				double upperClassBoundary = upperClassLimit + 0.5;
				cumulativeFrequency += frequency[i];
				
				dataSeries.getData().add(new XYChart.Data<Number, Number>(upperClassBoundary, cumulativeFrequency));
				
				textData.setText(textData.getText() + ", (" + upperClassBoundary + ", " + cumulativeFrequency + ")");
				
				if(i != frequency.length - 1) {
					textData.setText(textData.getText() + "\n(" + upperClassBoundary + ", " + cumulativeFrequency + ")");
				}
			}
			
			histogram.getData().add(dataSeries);
			
			mainTable.add(histogram, 0, 0);
			mainTable.add(textData, 1, 0);
		});
		
		
		//STEM AND LEAF DISPLAY
		
		
		MenuItem stemLeafDisplayenuItem = new MenuItem("Stem-and-leaf display");
		stemLeafDisplayenuItem.setOnAction(e -> {
			int type = QuickAlert.showRadioBox(AlertType.INFORMATION, "Stem-and-leaf display", "Specify the type of data to be used.", "Go", new RadioButton("Whole numbers"), new RadioButton("Decimal values")).get();
			
			String[] stringData = null;
			
			if(type == 0) {
				stringData = dataTextArea.getText().replaceAll("[^\\d\\s]", "").split("\\s");
			} else if(type == 1) {
				stringData = dataTextArea.getText().replaceAll("[^\\d\\.?\\d\\s]", "").split("\\s");
			} else {
				QuickAlert.show(AlertType.ERROR, "Incorrect data", "The data does not match the type of stem-and-leaf display.");
				return;
			}
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			ArrayList<Number> data = new ArrayList<Number>();
			
			Number minData = -1;
			Number maxData = 0;
			
			for(int i = 0; i < stringData.length; i++) {
				try {
					if(type == 0) {
						minData = Integer.parseInt(stringData[i]);
						
						if(minData.intValue() != -1) {
							break;
						}
					} else if(type == 1) {
						minData = Double.parseDouble(stringData[i]);
						
						if(minData.doubleValue() != -1.0) {
							break;
						}
					}
				} catch(NumberFormatException nfe) {}
			}
			
			if(type == 0 && minData.intValue() == -1) {
				QuickAlert.show(AlertType.ERROR, "Invalid data", "No valid data found in the data text area.");
				return;
			} else if(type == 1 && minData.doubleValue() == -1) {
				QuickAlert.show(AlertType.ERROR, "Invalid data", "No valid data found in the data text area.");
				return;
			}
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("")) {
					if(type == 0) {
						data.add(Integer.parseInt(stringData[i]));
						
						if(data.get(data.size() - 1).intValue() > maxData.intValue()) {
							maxData = data.get(data.size() - 1);
						} else if(data.get(data.size() - 1).intValue() < minData.intValue()) {
							minData = data.get(data.size() - 1);
						}
					} else if(type == 1) {
						data.add(Double.parseDouble(stringData[i]));
						
						if(data.get(data.size() - 1).doubleValue() > maxData.doubleValue()) {
							maxData = data.get(data.size() - 1);
						} else if(data.get(data.size() - 1).doubleValue() < minData.doubleValue()) {
							minData = data.get(data.size() - 1);
						}
					}
				}
			}
			
			Collections.sort(data, new NumberComparator());
			
			int maxAddedStem = 0;
			
			if(type == 0) {
				maxAddedStem = data.get(0).intValue() / 10;
			} else if(type == 1) {
				maxAddedStem = data.get(0).intValue();
			}
			
			Label firstStem = new Label("Stem ");
			firstStem.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 1, 0))));
			
			Label firstLeaf = new Label(" Leaf");
			firstLeaf.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
			
			mainTable.add(firstStem, 0, 0);
			mainTable.add(firstLeaf, 1, 0);
			
			Label firstDataStem = new Label(maxAddedStem + " ");
			firstDataStem.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));
			firstDataStem.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			firstDataStem.setAlignment(Pos.CENTER_RIGHT);
			GridPane.setFillWidth(firstDataStem, true);
			
			mainTable.add(firstDataStem, 0, 1);
			mainTable.add(new Label(" "), 1, 1);
			
			for(int i = 0; i < data.size(); i++) {
				int currentStem = 0;
				
				if(type == 0) {
					currentStem = data.get(i).intValue() / 10;
				} else if(type == 1) {
					currentStem = data.get(i).intValue();
				}
				
				if(currentStem > maxAddedStem) {
					maxAddedStem++;
					
					Label stemLabel = new Label(maxAddedStem + " ");
					stemLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));
					stemLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
					stemLabel.setAlignment(Pos.CENTER_RIGHT);
					GridPane.setFillWidth(stemLabel, true);
					
					mainTable.add(stemLabel, 0, mainTable.getRowCount());
					mainTable.add(new Label(" "), 1, mainTable.getRowCount() - 1);
				}
				
				if(currentStem == maxAddedStem) {
					Label rowLeaf = ((Label) mainTable.getChildren().get(mainTable.getChildren().size() - 1));
					
					int leaf = 0;
					
					if(type == 0) {
						leaf = data.get(i).intValue() % 10;
					} else if(type == 1) {
						leaf = (int) ((data.get(i).doubleValue() * 10) % 10);
					}
					
					rowLeaf.setText(rowLeaf.getText() + leaf + "  ");
				} else {
					i--;
				}
			}
		});
		
		
		//BOX PLOT
		
		
		MenuItem boxPlotMenuItem = new MenuItem("Box plot");
		boxPlotMenuItem.setOnAction(e -> {
			String[] stringData = dataTextArea.getText().replaceAll("[^-\\d\\s]", "").split("\\s");
			
			if(stringData.length == 1 && stringData[0].equals("")) {
				QuickAlert.show(AlertType.ERROR, "Data required", "There is no value for any data.\nEnter some data values and try again.");
				return;
			}
			
			mainTable.getChildren().clear();
			classWidthLabel.setText("");
			
			ArrayList<Integer> data = new ArrayList<Integer>();
			
			int minData = -1;
			int maxData = 0;
			
			for(int i = 0; i < stringData.length; i++) {
				try {
					minData = Integer.parseInt(stringData[i]);
					
					if(minData != -1) {
						break;
					}
				} catch(NumberFormatException nfe) {}
			}
			
			if(minData == -1) {
				QuickAlert.show(AlertType.ERROR, "Invalid data", "No valid data found in the data text area.");
				return;
			}
			
			for(int i = 0; i < stringData.length; i++) {
				if(!stringData[i].equals("") && !stringData[i].equals("-")) {
					data.add(Integer.parseInt(stringData[i]));
					
					if(data.get(data.size() - 1) > maxData) {
						maxData = data.get(data.size() - 1);
					} else if(data.get(data.size() - 1) < minData) {
						minData = data.get(data.size() - 1);
					}
				}
			}
			
			Collections.sort(data);
			
			double median = getMedian(data);
			
			double q1 = getMedian(data.subList(0, (int) Math.floor(data.size() / 2)));
			
			double q3 = getMedian(data.subList((int) Math.floor((data.size() - 1) / 2) + 1, data.size()));
			
			XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<String, Number>();
			XYChart.Series<String, Number> dataSeries2 = new XYChart.Series<String, Number>();
			XYChart.Series<String, Number> dataSeries3 = new XYChart.Series<String, Number>();
			XYChart.Series<String, Number> dataSeries4 = new XYChart.Series<String, Number>();
			
			dataSeries1.getData().add(new XYChart.Data<String, Number>("", minData));
			dataSeries1.getData().add(new XYChart.Data<String, Number>("", q1 - minData));
			dataSeries1.getData().add(new XYChart.Data<String, Number>("", median - q1));
			dataSeries1.getData().add(new XYChart.Data<String, Number>("", q3 - median));
			dataSeries1.getData().add(new XYChart.Data<String, Number>("", maxData - q3));

			dataSeries2.getData().add(new XYChart.Data<String, Number>(" ", 3));
			
			dataSeries3.getData().add(new XYChart.Data<String, Number>("  ", 3));
			
			dataSeries4.getData().add(new XYChart.Data<String, Number>("   ", 3));
			
			NumberAxis xAxis = new NumberAxis();
			
			CategoryAxis yAxis = new CategoryAxis();
			
			StackedBarChart<String, Number> stackBarChart = new StackedBarChart<String, Number>(yAxis, xAxis);
			stackBarChart.setCategoryGap(0);
			stackBarChart.setId("StackedBarChart");
			stackBarChart.getData().add(dataSeries1);
			stackBarChart.getData().add(dataSeries2);
			stackBarChart.getData().add(dataSeries3);
			stackBarChart.getData().add(dataSeries4);
			stackBarChart.setTitle("Box plot");
			stackBarChart.getXAxis().setLabel("Box plots");
			stackBarChart.getYAxis().setLabel("Data");
			stackBarChart.setLegendVisible(false);
			stackBarChart.setMinWidth(500);
			
			Label textData = new Label("High = " + maxData + "\nQ3 = " + q3 + "\nMedian = " + median + "\nQ1 = " + q1 + "\nLow = " + minData + "\n\nInterquartile range:\n" + (q3 - q1));
			
			mainTable.add(stackBarChart, 0, 0);
			mainTable.add(textData, 1, 0);
		});
		
		generateGraphMenu.getItems().addAll(histogramMenuItem, relativeHistogramMenuItem, ogiveMenuItem, stemLeafDisplayenuItem, boxPlotMenuItem);
		
		VBox mainVBox = new VBox(10, dataLabel, dataTextArea, mainHBox, mainTable);
		mainVBox.setPadding(new Insets(5, 5, 5, 5));
		VBox.setVgrow(mainTable, Priority.ALWAYS);
		
		Scene scene = new Scene(mainVBox, 800, 600);
		scene.getStylesheets().add(getClass().getResource("/BarChart.css").toString());
		scene.getStylesheets().add(getClass().getResource("/StackedBarChart.css").toString());
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Frequency Table Generator");
		primaryStage.getIcons().add(new Image(getClass().getResource("/icon.png").toString()));
		primaryStage.show();
	}
	
	@SuppressWarnings("rawtypes")
	private double getMedian(List data) {
		int lowerIndex = (int) Math.floor((data.size() - 1) / 2.0);
		int upperIndex = (int) Math.ceil((data.size() - 1) / 2.0);
		
		try {
			return (((Double) data.get(lowerIndex)) + ((Double) data.get(upperIndex))) / 2.0;
		} catch(ClassCastException cce) {
			try {
				return (((Integer) data.get(lowerIndex)) + ((Integer) data.get(upperIndex))) / 2.0;
			} catch(ClassCastException cce2) {
				DebugInfoFX.show(cce2);
				return 0;
			}
		}
	}
}
