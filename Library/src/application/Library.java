package application;

/** The aim of this code is to:
 * 1. Accept login from administrator - done
 * 2. Choose between 'borrow' and 'return' for 'student' or 'teacher' - done
 * 3. Upon selecting any of these combinations, the outcome should be an entry for teacher and student id and book id respectively.
 * 4. After the login, the methods for borrow and return should run and the message 'Transaction successful' should be output, 
 * while showing the student/teacher's name, id, book they borrowed and the book id, date borrowed, and return date.
 * 
 * 
 */

import java.util.*;

import javax.imageio.ImageIO;

import javafx.util.Callback;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.*;
import javafx.print.PrinterJob;
//import javafx.scene.*;
//import javafx.stage.*;
//import javafx.event.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
//import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
//import javafx.scene.layout.StackPane;
//import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.*;

public class Library extends Application {
	// create 3 arraylist - one each for books, students and teachers
	// populate each with 5 objects
	// write the main method to iterate over the arraylists and print objects

	private TableView studentTable = new TableView();
	private TableView teacherTable = new TableView();
	private TableView bookTable = new TableView();
	LibraryFile rf = new LibraryFile();
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM-dd-yyyy");
	Label errMsg = new Label("");

	public ObservableList<LibraryBook> books;
	public ObservableList<Student> students;
	public ObservableList<Teacher> teachers;

	String bookfileName;
	String studentsfileName;
	String teachersfileName;
	String adminfileName;

	private static int studentCount;
	private static int teacherCount;
	private static int bookCount;
	Label err = new Label();

	public static void main(String[] args) {
		launch(args);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////

	public void start(Stage primaryStage) throws FileNotFoundException {

		final Parameters params = getParameters();
		final List<String> parameters = params.getRaw();
		bookfileName = !parameters.isEmpty() ? parameters.get(0) : "src/application/Book.txt";
		studentsfileName = !parameters.isEmpty() ? parameters.get(1) : "src/application/Student.txt";
		teachersfileName = !parameters.isEmpty() ? parameters.get(2) : "src/application/Teacher.txt";
		adminfileName = !parameters.isEmpty() ? parameters.get(3) : "src/application/Admin.txt";

		books = rf.getBookData(bookfileName);
		students = rf.getStudentData(studentsfileName);
		teachers = rf.getTeacherData(teachersfileName);
		rf.readAdminData(adminfileName);

		// populateBooksArray();

		getNextIds();

		Class<?> clazz = Library.class;
		InputStream input = clazz.getResourceAsStream("book-image.png");
		Image image = new Image(input);

		// FileInputStream input = new
		// FileInputStream("src/application/book-image.png");
		// Image image = new Image(input);
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(100);
		imageView.setFitWidth(100);
		primaryStage.setTitle("Anderson High School Library");
		primaryStage.setHeight(700);
		primaryStage.setWidth(1000);
		primaryStage.show();

		GridPane grid = new GridPane();
		grid.getStyleClass().addAll("pane");
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(20);
		grid.setVgap(20);
		grid.setPadding(new Insets(300, 300, 300, 300));
		grid.add(imageView, 0, 0);

		Scene scene = new Scene(grid, 300, 300);
		URL url = this.getClass().getResource("application.css");
		if (url == null) {
			System.out.println("Resource not found. Aborting.");
			System.exit(-1);
		}
		String css = url.toExternalForm();
		scene.getStylesheets().add(css);
		primaryStage.setScene(scene);

		Text title = new Text("Anderson High School Library");
		title.setFont(Font.font("Britannic Bold", FontWeight.NORMAL, 35));
		grid.add(title, 0, 1, 7, 1);

		VBox adminBox = new VBox();
		adminBox.setPadding(new Insets(25, 25, 25, 25));
		Text adminScreenText = new Text("Admin Login");
		adminScreenText.setFont(Font.font("Lucida Sans", FontWeight.NORMAL, 20));
		Label userName = new Label("Username: ");
		userName.getStyleClass().add("labelText");
		TextField userTextField = new TextField();
		Label pw = new Label("Password: ");
		pw.getStyleClass().add("labelText");
		PasswordField pwField = new PasswordField();
		Text blank = new Text("");

		HBox userNameBox = new HBox();
		userNameBox.setPrefWidth(500);
		userNameBox.getChildren().addAll(userName, userTextField);
		HBox pwBox = new HBox();
		pwBox.getChildren().addAll(pw, pwField);

		adminBox.getChildren().addAll(adminScreenText, userNameBox, pwBox, blank);
		adminBox.setSpacing(20);
		adminBox.setBackground(
				new Background(new BackgroundFill(Color.LIGHTSTEELBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		grid.add(adminBox, 0, 2);

		Button ebtn = new Button("Exit");
		ebtn.getStyleClass().addAll("buttonLarge");
		Button btn = new Button("Submit");
		// btn.setFont(Font.font("Lucida Sans", FontWeight.BOLD, 13));
		btn.getStyleClass().addAll("buttonLarge");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().addAll(btn, ebtn);
		grid.add(hbBtn, 0, 5);

		ebtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				primaryStage.close();
			}
		});

		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (userTextField.getText() == null || pwField.getText() == null) {
					Text errorMsg = new Text("Error: Please enter your username and password.");
					grid.add(errorMsg, 1, 6);
				}

				if (!(userTextField.getText().equals("admin")) || (!(pwField.getText().equals("admin")))) {
					Text errorMsg = new Text("Error: Invalid username or password.");
					grid.add(errorMsg, 1, 6);
				}

				else {

					grid.getChildren().clear();
					grid.setPadding(new Insets(100, 100, 200, 100));

					Text registerLabel = new Text("Register");
					registerLabel.getStyleClass().addAll("heading");
					Text chkoutReturnLabel = new Text("Checkout/Return");
					chkoutReturnLabel.getStyleClass().addAll("heading");
					Text reportsLabel = new Text("Reports");
					reportsLabel.getStyleClass().addAll("heading");

					Button editStudentBtn = new Button("Add/Edit Student");
					editStudentBtn.getStyleClass().addAll("buttonLarge");
					Button editTeacherBtn = new Button("Add/Edit Teacher");
					editTeacherBtn.getStyleClass().addAll("buttonLarge");
					Button editBookBtn = new Button("Add/Edit Book");
					editBookBtn.getStyleClass().addAll("buttonLarge");
					Button borrowBookBtn = new Button("Checkout Book");
					borrowBookBtn.getStyleClass().addAll("buttonLarge");
					Button returnBookBtn = new Button("Return Book");
					returnBookBtn.getStyleClass().addAll("buttonLarge");
					Button overDueBooksBtn = new Button("Overdue Books");
					overDueBooksBtn.getStyleClass().addAll("buttonLarge");
					Button checkedOutBooksBtn = new Button("Borrowed Books");
					checkedOutBooksBtn.getStyleClass().addAll("buttonLarge");
					Button exitBtn = new Button("Exit");
					exitBtn.getStyleClass().addAll("buttonLarge");

					grid.add(imageView, 0, 0);

					grid.add(registerLabel, 0, 1);
					grid.add(editStudentBtn, 0, 2);
					grid.add(editTeacherBtn, 0, 3);
					grid.add(editBookBtn, 0, 4);

					grid.add(chkoutReturnLabel, 1, 1);
					grid.add(borrowBookBtn, 1, 2);
					grid.add(returnBookBtn, 1, 3);

					grid.add(reportsLabel, 2, 1);
					grid.add(overDueBooksBtn, 2, 2);
					grid.add(checkedOutBooksBtn, 2, 3);

					grid.add(exitBtn, 2, 8);
					///////////////////////////////////////////////////////////////////////////////////////////////////////////////
					editStudentBtn.setOnAction(new EventHandler<ActionEvent>() {
						@SuppressWarnings("unchecked")
						public void handle(ActionEvent args) {
							// grid.getChildren().clear();
							err.setVisible(false);
							Scene scene = new Scene(new Group());
							scene.getStylesheets().add(css);
							Stage stage = new Stage();
							stage.setTitle("Student Members");
							stage.setWidth(700);
							stage.setHeight(700);
							stage.setScene(scene);

							GridPane editStudGrid = new GridPane();
							editStudGrid.setPrefWidth(700);
							editStudGrid.setPrefHeight(700);
							editStudGrid.getStyleClass().addAll("pane");

							Label label = new Label("Student Members");
							label.getStyleClass().addAll("heading");

							studentTable.setEditable(true);
							studentTable.setPrefHeight(300);
							studentTable.setPrefWidth(500);

							Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
								public TableCell call(TableColumn p) {
									return new EditingCell();
								}
							};

							TableColumn idCol = new TableColumn("ID number");
							idCol.setPrefWidth(80);
							TableColumn firstNameCol = new TableColumn("First Name");
							firstNameCol.setPrefWidth(210);
							TableColumn lastNameCol = new TableColumn("Last Name");
							lastNameCol.setPrefWidth(210);

							idCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("id"));

							firstNameCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("firstName"));
							firstNameCol.setCellFactory(cellFactory);

							String firstName = null, lastName = null;

							firstNameCol.setOnEditCommit(new EventHandler<CellEditEvent<Borrower, String>>() {
								public void handle(CellEditEvent<Borrower, String> t) {

									if (t.getNewValue().equals(null) || t.getNewValue().trim().equals("")) {
										System.out.println("Entered if for null firstName");
										err.setText("Error: Empty First Name. Please Enter Valid Value.");
										err.setVisible(true);
									}

									// Write method for the duplicate value entry.

									else {

										((Borrower) t.getTableView().getItems().get(t.getTablePosition().getRow()))
												.setFirstName(new SimpleStringProperty(t.getNewValue()));
										err.setVisible(false);
									}

								}
							});

							lastNameCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("lastName"));
							lastNameCol.setCellFactory(cellFactory);
							lastNameCol.setOnEditCommit(new EventHandler<CellEditEvent<Borrower, String>>() {
								public void handle(CellEditEvent<Borrower, String> t) {

									if (t.getNewValue().equals(null) || t.getNewValue().trim().equals("")) {
										err.setText("Error: Empty Last Name. Please Enter Valid Value.");
										err.setVisible(true);
									}

									else {
										((Borrower) t.getTableView().getItems().get(t.getTablePosition().getRow()))
												.setLastName(new SimpleStringProperty(t.getNewValue()));
										err.setVisible(false);
									}
								}
							});

							studentTable.setItems(students);

							if (fNameDuplicate(firstName, "Student") && lNameDuplicate(lastName, "Student")) {

							}

							// table.getColumns().addAll(idCol, firstNameCol, lastNameCol, memberTypeCol);
							studentTable.getColumns().addAll(idCol, firstNameCol, lastNameCol);

							TextField firstNameField = new TextField();
							firstNameField.setPromptText("First Name");
							firstNameField.setMaxWidth(100);

							TextField lastNameField = new TextField();
							lastNameField.setPromptText("Last Name");
							lastNameField.setMaxWidth(100);

							Button addButton = new Button("Add");
							addButton.getStyleClass().addAll("buttonSmall");
							Button exitButton = new Button("Cancel");
							exitButton.getStyleClass().addAll("buttonSmall");
							Button deleteButton = new Button("Delete");
							deleteButton.getStyleClass().addAll("buttonSmall");

							// Label err = new Label();

							VBox vbox = new VBox();
							vbox.setSpacing(5);
							vbox.setPadding(new Insets(90, 0, 0, 90));
							HBox hb = new HBox();
							hb.setSpacing(3);

							exitButton.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									stage.close();
								}
							});

							addButton.setOnAction(new EventHandler<ActionEvent>() {

								boolean isDuplicate = false;

								public void handle(ActionEvent e) {
									if ((firstNameField.getText() != null
											&& firstNameField.getText().trim().length() > 0)
											&& (lastNameField.getText() != null
													&& lastNameField.getText().trim().length() > 0)) {

										for (int i = 0; i < students.size(); i++) {
											if (students.get(i).getFirstName().equals(firstNameField.getText())
													&& students.get(i).getLastName().equals(lastNameField.getText())) {
												isDuplicate = true;
											}
										}

										if (isDuplicate) {
											err.setText("Error: Duplicate Student Entries not Allowed.");
											err.setVisible(true);
											isDuplicate = false;
										}

										else {
											studentCount += 1;
											students.add(new Student(Integer.toString(studentCount),
													lastNameField.getText(), firstNameField.getText()));
											lastNameField.clear();
											firstNameField.clear();
											err.setVisible(false);
										}

									} else {
										err.setText("Please Enter Valid First and Last Names.");
										err.setVisible(true);
									}
								}

							});

							deleteButton.setOnAction(new EventHandler<ActionEvent>() {

								public void handle(ActionEvent e) {
									Label newLine = new Label("");
									Label delete = new Label("Enter ID of the record you wish to delete:\t");
									TextField deleteId = new TextField();
									deleteId.setPromptText("ID to be deleted");
									HBox hb = new HBox();
									hb.setSpacing(3);
									Button deleteBorrower = new Button("Delete Student");
									deleteBorrower.getStyleClass().addAll("buttonLarge");
									hb.getChildren().addAll(delete, deleteId, deleteBorrower);
									vbox.getChildren().addAll(newLine, hb);

									deleteBorrower.setOnAction(new EventHandler<ActionEvent>() {

										public void handle(ActionEvent e) {
											for (int i = 0; i < students.size(); i++) {
												if (students.get(i).getId().equals(deleteId.getText())) {
													students.remove(i);
												}
											}

											for (int i = 0; i < books.size(); i++) {
												if (books.get(i).getCheckedOutId().equals(deleteId.getText())) {
													books.get(i).checkedOutBy = null;
													books.get(i).checkedOutDate = null;
													books.get(i).checkedOutId = null;
													books.get(i).returnDate = null;
													books.get(i).borrowerName = null;
												}
											}
										}
									});
								}

							});

							firstNameField.clear();
							lastNameField.clear();

							hb.getChildren().addAll(firstNameField, lastNameField, addButton, deleteButton, exitButton);
							vbox.getChildren().addAll(label, studentTable, err, hb);
							editStudGrid.getChildren().addAll(vbox);
							((Group) scene.getRoot()).getChildren().addAll(editStudGrid);

							stage.setScene(scene);
							stage.show();
						}

					});

					///////////////////////////////////////////////////////////////////////////////////////////////////////////////

					editTeacherBtn.setOnAction(new EventHandler<ActionEvent>() {
						@SuppressWarnings("unchecked")
						@Override
						public void handle(ActionEvent event) {

							// grid.getChildren().clear();
							Scene scene = new Scene(new Group());
							scene.getStylesheets().add(css);
							Stage stage = new Stage();
							stage.setTitle("Teacher Members");
							stage.setWidth(700);
							stage.setHeight(700);
							stage.setScene(scene);

							GridPane editTeachGrid = new GridPane();
							editTeachGrid.setPrefWidth(700);
							editTeachGrid.setPrefHeight(700);
							editTeachGrid.getStyleClass().addAll("pane");

							Label label = new Label("Teacher Members");
							label.getStyleClass().addAll("heading");

							teacherTable.setEditable(true);
							teacherTable.setPrefHeight(300);
							teacherTable.setPrefWidth(500);

							Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
								public TableCell call(TableColumn p) {
									return new EditingCell();
								}
							};

							TableColumn idCol = new TableColumn("ID number");
							idCol.setPrefWidth(80);
							TableColumn firstNameCol = new TableColumn("First Name");
							firstNameCol.setPrefWidth(210);
							TableColumn lastNameCol = new TableColumn("Last Name");
							lastNameCol.setPrefWidth(210);
							// TableColumn memberTypeCol = new TableColumn("Student/Teacher");
							// memberTypeCol.setPrefWidth(125);

							idCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("id"));

							firstNameCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("firstName"));
							firstNameCol.setCellFactory(cellFactory);
							firstNameCol.setOnEditCommit(new EventHandler<CellEditEvent<Borrower, String>>() {
								public void handle(CellEditEvent<Borrower, String> t) {
									((Borrower) t.getTableView().getItems().get(t.getTablePosition().getRow()))
											.setFirstName(new SimpleStringProperty(t.getNewValue()));
								}
							});

							lastNameCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("lastName"));
							lastNameCol.setCellFactory(cellFactory);
							lastNameCol.setOnEditCommit(new EventHandler<CellEditEvent<Borrower, String>>() {
								public void handle(CellEditEvent<Borrower, String> t) {
									((Borrower) t.getTableView().getItems().get(t.getTablePosition().getRow()))
											.setLastName(new SimpleStringProperty(t.getNewValue()));
								}
							});

							teacherTable.setItems(teachers);

							// table.getColumns().addAll(idCol, firstNameCol, lastNameCol, memberTypeCol);
							teacherTable.getColumns().addAll(idCol, firstNameCol, lastNameCol);

							TextField firstNameField = new TextField();
							firstNameField.setPromptText("First Name");
							firstNameField.setMaxWidth(100);

							TextField lastNameField = new TextField();
							lastNameField.setPromptText("Last Name");
							lastNameField.setMaxWidth(100);

							Button addButton = new Button("Add");
							addButton.getStyleClass().addAll("buttonSmall");
							Button exitButton = new Button("Cancel");
							exitButton.getStyleClass().addAll("buttonSmall");
							Button deleteButton = new Button("Delete");
							deleteButton.getStyleClass().addAll("buttonSmall");

							Label err = new Label();
							err.setVisible(false);

							VBox vbox = new VBox();
							vbox.setSpacing(5);
							vbox.setPadding(new Insets(90, 0, 0, 90));
							HBox hb = new HBox();
							hb.setSpacing(3);

							exitButton.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									stage.close();
								}
							});
							addButton.setOnAction(new EventHandler<ActionEvent>() {

								boolean isDuplicate = false;

								public void handle(ActionEvent e) {
									if ((firstNameField.getText() != null
											&& firstNameField.getText().trim().length() > 0)
											&& (lastNameField.getText() != null
													&& lastNameField.getText().trim().length() > 0)) {

										for (int i = 0; i < teachers.size(); i++) {
											if (teachers.get(i).getFirstName().equals(firstNameField.getText())
													&& teachers.get(i).getLastName().equals(lastNameField.getText())) {
												isDuplicate = true;
											}
										}

										if (isDuplicate) {
											err.setText("Error: Duplicate Teacher Entries not Allowed.");
											err.setVisible(true);
											isDuplicate = false;
										}

										else {
											teacherCount += 1;
											teachers.add(new Teacher(Integer.toString(teacherCount),
													lastNameField.getText(), firstNameField.getText()));
											err.setVisible(false);
										}

									} else {
										err.setText("Please Enter Valid First and Last Names.");
										err.setVisible(true);
									}

									firstNameField.clear();
									lastNameField.clear();
								}
							});

							deleteButton.setOnAction(new EventHandler<ActionEvent>() {

								public void handle(ActionEvent e) {
									Label newLine = new Label("");
									Label delete = new Label("Enter ID of the record you wish to delete:\t");
									TextField deleteId = new TextField();
									deleteId.setPromptText("ID to be deleted");
									HBox hb = new HBox();
									hb.setSpacing(3);
									Button deleteBorrower = new Button("Delete Teacher");
									deleteBorrower.getStyleClass().addAll("buttonLarge");
									hb.getChildren().addAll(delete, deleteId, deleteBorrower);
									vbox.getChildren().addAll(newLine, hb);

									deleteBorrower.setOnAction(new EventHandler<ActionEvent>() {

										public void handle(ActionEvent e) {
											for (int i = 0; i < teachers.size(); i++) {
												if (teachers.get(i).getId().equals(deleteId.getText())) {
													teachers.remove(i);
												}
											}

											for (int i = 0; i < books.size(); i++) {
												if (books.get(i).getCheckedOutId().equals(deleteId.getText())) {
													books.get(i).checkedOutBy = null;
													books.get(i).checkedOutDate = null;
													books.get(i).checkedOutId = null;
													books.get(i).returnDate = null;
													books.get(i).borrowerName = null;
												}
											}
										}
									});
								}

							});

							hb.getChildren().addAll(firstNameField, lastNameField, addButton, deleteButton, exitButton);
							vbox.getChildren().addAll(label, teacherTable, err, hb);
							editTeachGrid.getChildren().addAll(vbox);
							((Group) scene.getRoot()).getChildren().addAll(editTeachGrid);

							stage.setScene(scene);
							stage.show();
						}

					});
					///////////////////////////////////////////////////////////////////////////////////////////////////////////////
					editBookBtn.setOnAction(new EventHandler<ActionEvent>() {

						@SuppressWarnings("unchecked")
						public void handle(ActionEvent e) {
							Scene scene = new Scene(new Group());
							scene.getStylesheets().add(css);
							Stage stage = new Stage();
							stage.setTitle("Library Books");
							stage.setWidth(700);
							stage.setHeight(700);
							stage.setScene(scene);

							GridPane viewGrid = new GridPane();
							viewGrid.setPrefWidth(700);
							viewGrid.setPrefHeight(700);
							viewGrid.getStyleClass().addAll("pane");

							Label err = new Label();
							err.setVisible(false);

							Label label = new Label("Library Books");
							label.getStyleClass().addAll("heading");

							bookTable.setPrefHeight(300);
							bookTable.setPrefWidth(500);

							TableColumn bookIdCol = new TableColumn("Book ID");
							bookIdCol.setPrefWidth(50);
							TableColumn titleCol = new TableColumn("Title");
							titleCol.setPrefWidth(250);
							TableColumn authorNameCol = new TableColumn("Author");
							authorNameCol.setPrefWidth(100);
							TableColumn isCheckedOutCol = new TableColumn("Status");
							isCheckedOutCol.setPrefWidth(100);

							// TableColumn memberTypeCol = new TableColumn("Student/Teacher");
							// memberTypeCol.setPrefWidth(125);

							bookIdCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("bookId"));

							titleCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("bookName"));

							authorNameCol.setCellValueFactory(new PropertyValueFactory<Borrower, String>("authorName"));

							isCheckedOutCol
									.setCellValueFactory(new PropertyValueFactory<Borrower, String>("isCheckedOut"));

							bookTable.setItems(books);

							bookTable.getColumns().addAll(bookIdCol, titleCol, authorNameCol, isCheckedOutCol);

							TextField titleField = new TextField();
							titleField.setPromptText("Title");
							titleField.setMaxWidth(100);

							TextField authorField = new TextField();
							authorField.setPromptText("Author");
							authorField.setMaxWidth(100);

							Label addBookLabel = new Label("Add Book");
							Label deleteBookLabel = new Label("Delete Book");
							Button addButton = new Button("Add");
							addButton.getStyleClass().addAll("buttonSmall");
							Button cancelButton = new Button("Cancel");
							cancelButton.getStyleClass().addAll("buttonSmall");
							Label newLine = new Label("");
							// Label delete = new Label("Book ID to be deleted:\t");
							TextField deleteId = new TextField();
							deleteId.setPrefWidth(205);
							deleteId.setPromptText("Book ID");
							HBox hb = new HBox();
							Button deleteBook = new Button("Delete");
							deleteBook.getStyleClass().addAll("buttonSmall");
							HBox hb1 = new HBox();
							// hb1.getChildren().addAll(delete, deleteId, deleteBook);
							hb1.getChildren().addAll(deleteId, deleteBook, cancelButton);
							VBox vb = new VBox();
							vb.getChildren().addAll(newLine, hb);

							deleteBook.setOnAction(new EventHandler<ActionEvent>() {

								public void handle(ActionEvent e) {
									ArrayList<String> temp;
									for (int i = 0; i < teachers.size(); i++) {
										temp = teachers.get(i).getBookIds();
										if (temp.contains(deleteId.getText())) {
											teachers.get(i).getBookIds().remove(deleteId.getText());
										}
									}

									for (int i = 0; i < students.size(); i++) {
										temp = students.get(i).getBookIds();
										if (temp.contains(deleteId.getText())) {
											students.get(i).getBookIds().remove(deleteId.getText());
										}
									}

									for (int i = 0; i < books.size(); i++) {
										if (books.get(i).getBookId().equals(deleteId.getText())) {
											books.remove(i);
										}
									}
									deleteId.clear();
								}
							});

							cancelButton.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									stage.close();
								}
							});
							addButton.setOnAction(new EventHandler<ActionEvent>() {
								public void handle(ActionEvent e) {
									if ((titleField.getText() != null && titleField.getText().trim().length() > 0)
											&& (authorField.getText() != null
													&& authorField.getText().trim().length() > 0)) {

										bookCount += 1;
										boolean isCheckedOut = false;
										books.add(new LibraryBook(Integer.toString(bookCount), titleField.getText(),
												authorField.getText(), isCheckedOut, null, null, null, null, null));
									} else {
										err.setText("Please enter valid Book author and title");
										err.setVisible(true);
									}

									titleField.clear();
									authorField.clear();
								}
							});

							VBox vbox = new VBox();
							vbox.setSpacing(5);
							vbox.setPadding(new Insets(90, 0, 0, 90));
							HBox hb2 = new HBox();
							hb2.getChildren().addAll(titleField, authorField, addButton);
							hb2.setSpacing(3);
							hb1.setSpacing(3);
							vbox.getChildren().addAll(label, bookTable, err, addBookLabel, hb2, deleteBookLabel, hb1);
							viewGrid.getChildren().addAll(vbox);
							((Group) scene.getRoot()).getChildren().addAll(viewGrid);

							stage.setScene(scene);
							stage.show();

						}
					});

					///////////////////////////////////////////////////////////////////////////////////////////////////////////////

					returnBookBtn.setOnAction(new EventHandler<ActionEvent>() {

						public void handle(ActionEvent e) {

							// grid.getChildren().clear();
							Scene scene = new Scene(new Group());
							scene.getStylesheets().add(css);
							Stage stage = new Stage();
							stage.setTitle("Return Books");
							stage.setWidth(300);
							stage.setHeight(300);
							stage.setScene(scene);
							VBox vbox = new VBox();
							vbox.setPrefWidth(300);
							vbox.setPrefHeight(300);
							vbox.setSpacing(20);
							vbox.setAlignment(Pos.CENTER);
							GridPane checkGrid = new GridPane();
							checkGrid.getStyleClass().addAll("pane");
							checkGrid.setPrefWidth(300);
							checkGrid.setPrefHeight(300);
							checkGrid.setAlignment(Pos.CENTER);

							Label bookIdLabel = new Label("Book ID: ");
							TextField bookIdField = new TextField();
							Label msg = new Label();
							msg.setVisible(false);

							Button submitBtn = new Button("Submit");
							submitBtn.getStyleClass().addAll("buttonSmall");
							Button exitButton = new Button("Cancel");
							exitButton.getStyleClass().addAll("buttonSmall");

							HBox hb1 = new HBox();
							hb1.getChildren().addAll(bookIdLabel, bookIdField);
							hb1.setSpacing(3);
							hb1.setPrefWidth(300);
							hb1.setAlignment(Pos.CENTER);

							HBox hb2 = new HBox();
							hb2.getChildren().addAll(submitBtn, exitButton);
							hb2.setSpacing(3);
							hb2.setPrefWidth(300);
							hb2.setAlignment(Pos.CENTER);

							vbox.getChildren().addAll(hb1, hb2, msg);
							checkGrid.getChildren().addAll(vbox);
							((Group) scene.getRoot()).getChildren().addAll(checkGrid);

							stage.setScene(scene);
							stage.show();
							exitButton.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									stage.close();
								}
							});

							submitBtn.setOnAction(new EventHandler<ActionEvent>() {

								public void handle(ActionEvent e) {
									msg.setText(bookReturn(bookIdField));
									msg.setVisible(true);
								}
							});

						}
					});

					///////////////////////////////////////////////////////////////////////////////////////////////////////////////
					borrowBookBtn.setOnAction(new EventHandler<ActionEvent>() {

						@SuppressWarnings("unchecked")
						public void handle(ActionEvent e) {

							Scene scene = new Scene(new Group());
							scene.getStylesheets().add(css);
							Stage stage = new Stage();
							stage.setTitle("Borrow Books");
							stage.setWidth(700);
							stage.setHeight(700);
							stage.setScene(scene);

							GridPane resultsGrid = new GridPane();
							resultsGrid.setPrefWidth(700);
							resultsGrid.setPrefHeight(700);
							resultsGrid.getStyleClass().addAll("pane");
							resultsGrid.setAlignment(Pos.CENTER);

							TableView availableBooks = new TableView();
							Label label = new Label("Borrow Books");
							label.setFont(new Font("Arial", 20));
							availableBooks.setPrefHeight(300);
							availableBooks.setPrefWidth(500);

							TableColumn bookIdCol = new TableColumn("Book ID");
							bookIdCol.setPrefWidth(50);
							TableColumn titleCol = new TableColumn("Title");
							titleCol.setPrefWidth(250);
							TableColumn authorNameCol = new TableColumn("Author");
							authorNameCol.setPrefWidth(100);
							TableColumn isCheckedOutCol = new TableColumn("Status");
							isCheckedOutCol.setPrefWidth(100);
							bookIdCol.setCellValueFactory(new PropertyValueFactory<LibraryBook, String>("bookId"));
							titleCol.setCellValueFactory(new PropertyValueFactory<LibraryBook, String>("bookName"));
							authorNameCol
									.setCellValueFactory(new PropertyValueFactory<LibraryBook, String>("authorName"));
							isCheckedOutCol
									.setCellValueFactory(new PropertyValueFactory<LibraryBook, String>("isCheckedOut"));
							availableBooks.setItems(books);
							availableBooks.getColumns().addAll(bookIdCol, titleCol, authorNameCol, isCheckedOutCol);

							Label bookIdLabel = new Label("Book ID:        ");
							TextField bookIdField = new TextField();
							Label borrowerIdLabel = new Label("Borrower ID: ");
							TextField borrowerIdField = new TextField();

							ToggleGroup tg = new ToggleGroup();
							RadioButton isStudent = new RadioButton("Student");
							isStudent.setToggleGroup(tg);
							isStudent.setSelected(true);
							RadioButton isTeacher = new RadioButton("Teacher");
							isTeacher.setToggleGroup(tg);

							Button submitBtn = new Button("Check Out");
							submitBtn.getStyleClass().addAll("buttonLarge");
							Button exitButton = new Button("Cancel");
							exitButton.getStyleClass().addAll("buttonSmall");

							HBox hbox1 = new HBox();
							hbox1.setSpacing(3);
							hbox1.getChildren().addAll(bookIdLabel, bookIdField);

							HBox hbox2 = new HBox();
							hbox2.setSpacing(3);
							hbox2.getChildren().addAll(borrowerIdLabel, borrowerIdField);

							HBox hbox3 = new HBox();
							hbox3.setSpacing(3);
							hbox3.getChildren().addAll(submitBtn, exitButton);

							HBox hbox4 = new HBox();
							hbox4.setSpacing(3);
							hbox4.getChildren().addAll(isStudent, isTeacher);

							Label msg = new Label("Book Successfully Checked Out");
							msg.setVisible(false);

							VBox vbox = new VBox();
							vbox.setSpacing(20);
							vbox.setAlignment(Pos.CENTER);
							vbox.getChildren().addAll(label, availableBooks, hbox1, hbox2, hbox4, hbox3, msg);
							resultsGrid.getChildren().addAll(vbox);
							((Group) scene.getRoot()).getChildren().addAll(resultsGrid);

							stage.setScene(scene);
							stage.show();

							exitButton.setOnAction(new EventHandler<ActionEvent>() {

								public void handle(ActionEvent e) {
									stage.close();
								}
							});

							submitBtn.setOnAction(new EventHandler<ActionEvent>() {

								public void handle(ActionEvent e) {

									if (isStudent.isSelected()) {
										// msg.setText("");
										if (isValidStudent(borrowerIdField.getText()))
											msg.setText(bookBorrow(bookIdField, borrowerIdField, "Student"));
										else
											msg.setText("Not a Valid Student ID");
									}

									else if (isTeacher.isSelected()) {
										// msg.setText("");
										if (isValidTeacher(borrowerIdField.getText()))
											msg.setText(bookBorrow(bookIdField, borrowerIdField, "Teacher"));
										else {
											msg.setText("Not a Valid Teacher ID");
										}
									}

									// bookIdField.clear();
									borrowerIdField.clear();
									msg.setVisible(true);

								}
							});

						}
					});
					///////////////////////////////////////////////////////////////////////////////////////////////////////////////
					overDueBooksBtn.setOnAction(new EventHandler<ActionEvent>() {

						LocalDate startDate = null;
						LocalDate endDate = null;

						@SuppressWarnings("unchecked")
						public void handle(ActionEvent e) {
							Scene scene = new Scene(new Group());
							scene.getStylesheets().add(css);
							Stage stage = new Stage();
							stage.setTitle("Overdue Book Report");
							stage.setWidth(700);
							stage.setHeight(700);
							stage.setScene(scene);

							Text to = new Text("To");
							to.getStyleClass().add("alert");
							Text startDate = new Text();
							LocalDate odStart = LocalDate.now().minusDays(7);
							startDate.setText(LocalDate.now().minusDays(7).format(dtf));
							startDate.getStyleClass().add("alert");
							Text endDate = new Text();
							LocalDate odEnd = LocalDate.now();
							endDate.setText(LocalDate.now().format(dtf));
							endDate.getStyleClass().add("alert");
							
							Text overdueBookReportLabel = new Text("Overdue Book Report");
							overdueBookReportLabel.getStyleClass().addAll("heading");
							Text label = new Text("");

							TableView table = new TableView();
							table.setPrefSize(550, 300);
							TableColumn bookIdCol = new TableColumn("Book ID");
							bookIdCol.setPrefWidth(75);
							bookIdCol.setCellValueFactory(new PropertyValueFactory<OverdueBook, String>("bookId"));
							TableColumn bookNameCol = new TableColumn("Title");
							bookNameCol.setPrefWidth(250);
							bookNameCol.setCellValueFactory(new PropertyValueFactory<OverdueBook, String>("bookName"));
							TableColumn borrowerIdCol = new TableColumn("Borrower Id");
							borrowerIdCol.setPrefWidth(75);
							borrowerIdCol
									.setCellValueFactory(new PropertyValueFactory<OverdueBook, String>("borrowerId"));
							TableColumn returnDateCol = new TableColumn("Return Date");
							returnDateCol.setPrefWidth(100);
							returnDateCol
									.setCellValueFactory(new PropertyValueFactory<OverdueBook, String>("returnDate"));
							TableColumn fineValuesCol = new TableColumn("Fine");
							fineValuesCol.setPrefWidth(50);
							fineValuesCol.setCellValueFactory(new PropertyValueFactory<OverdueBook, String>("fine"));

							List<OverdueBook> bookList = new ArrayList<>();
							GridPane finesGrid = new GridPane();
							finesGrid.getStyleClass().addAll("pane");
							finesGrid.setPrefWidth(700);
							finesGrid.setPrefHeight(700);
							finesGrid.setAlignment(Pos.CENTER);

							String fine = "$0.00";
							OverdueBook ob;
							for (LibraryBook book : books) {
								fine = "$0.00";
								if (book.isCheckedOut && book.getCheckedOutBy().equals("Student")
										&& (book.getReturnDate().getDayOfYear() > odStart.getDayOfYear())
										&& (book.getReturnDate().getDayOfYear() < odEnd.getDayOfYear())) {

									fine = calcFine(book.getCheckedOutDate(), book.getReturnDate(),
											Student.FINE_PER_DAY);

								} else if (book.isCheckedOut && book.getCheckedOutBy().equals("Teacher")
										&& (book.getReturnDate().getDayOfYear() > odStart.getDayOfYear())
										&& (book.getReturnDate().getDayOfYear() < odEnd.getDayOfYear())) {

									fine = calcFine(book.getCheckedOutDate(), book.getReturnDate(),
											Teacher.FINE_PER_DAY);

								}
								// System.out.println("fine:" + fine);
								if (!(book.getReturnDate() == null) && !fine.equals("$0.00")) {
									String formattedDate = book.getReturnDate().format(dtf);
									ob = new OverdueBook(new SimpleStringProperty(book.getBookId()),
											new SimpleStringProperty(book.getBookName()),
											new SimpleStringProperty(book.getCheckedOutId()),
											new SimpleStringProperty(formattedDate), new SimpleStringProperty(fine));
									bookList.add(ob);
								}
							}
							table.setItems(FXCollections.observableArrayList(bookList));
							table.getColumns().addAll(bookIdCol, bookNameCol, borrowerIdCol, returnDateCol,
									fineValuesCol);

							Text dummy = new Text("");
							Text dummy1 = new Text("");

							HBox hbox1 = new HBox();
							hbox1.setSpacing(10);
							hbox1.getChildren().addAll(startDate, to, endDate);

							finesGrid.add(overdueBookReportLabel, 0, 0);
							finesGrid.add(dummy, 0, 1);
							finesGrid.add(hbox1, 0, 2);
							finesGrid.add(label, 0, 3);
							finesGrid.add(table, 0, 4);
							Button printButton = new Button("Print");
							printButton.getStyleClass().add("buttonSmall");
							Button cancelButton = new Button("Cancel");
							cancelButton.getStyleClass().add("buttonSmall");

							HBox hbox2 = new HBox();
							hbox2.setSpacing(10);
							hbox2.getChildren().addAll(printButton, cancelButton);

							finesGrid.add(dummy1, 0, 5);
							finesGrid.add(hbox2, 0, 6);
							((Group) scene.getRoot()).getChildren().addAll(finesGrid);
							stage.setScene(scene);
							stage.show();

							cancelButton.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									stage.close();
								}
							});
							printButton.setOnAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent arg0) {
									PrinterJob printerJob = PrinterJob.createPrinterJob();
									if (printerJob.showPrintDialog(primaryStage.getOwner())
											&& printerJob.printPage(table))
										printerJob.endJob();

								}

							});
						}
					});
					///////////////////////////////////////////////////////////////////////////////////////////////////////////////
					checkedOutBooksBtn.setOnAction(new EventHandler<ActionEvent>() {
						LocalDate startDate = null;
						LocalDate endDate = null;

						@Override
						public void handle(ActionEvent event) {
							// find books that are due as of date.now or later
							Scene scene = new Scene(new Group());
							scene.getStylesheets().add(css);
							Stage stage = new Stage();
							stage.setTitle("Borrowed Books Report");
							stage.setWidth(700);
							stage.setHeight(700);
							stage.setScene(scene);

							Text to = new Text("To");
							to.getStyleClass().add("alert");
							Text startDate = new Text();
							LocalDate bbStart = LocalDate.now().minusDays(7);
							startDate.setText(LocalDate.now().minusDays(7).format(dtf));
							startDate.getStyleClass().add("alert");
							Text endDate = new Text();
							LocalDate bbEnd = LocalDate.now();
							endDate.setText(LocalDate.now().format(dtf));
							endDate.getStyleClass().add("alert");

							Text BorrowedBooksReportlabel = new Text("Borrowed Books Report");
							Text label = new Text("");
							BorrowedBooksReportlabel.getStyleClass().addAll("heading");

							TableView table = new TableView();
							table.setPrefSize(600, 300);

							TableColumn bookIdCol = new TableColumn("Book ID");
							bookIdCol.setPrefWidth(50);
							bookIdCol.setCellValueFactory(new PropertyValueFactory<BorrowedBook, String>("bookId"));

							TableColumn bookNameCol = new TableColumn("Title");
							bookNameCol.setPrefWidth(250);
							bookNameCol.setCellValueFactory(new PropertyValueFactory<BorrowedBook, String>("bookName"));

							TableColumn borrowerIdCol = new TableColumn("Borrower ID");
							borrowerIdCol.setPrefWidth(50);
							borrowerIdCol
									.setCellValueFactory(new PropertyValueFactory<BorrowedBook, String>("borrowerId"));

							TableColumn borrowerNameCol = new TableColumn("Borrower Name");
							borrowerNameCol.setPrefWidth(100);
							borrowerNameCol.setCellValueFactory(
									new PropertyValueFactory<BorrowedBook, String>("borrowerName"));

							TableColumn returnDateCol = new TableColumn("Checkout Date");
							returnDateCol.setPrefWidth(75);
							returnDateCol
									.setCellValueFactory(new PropertyValueFactory<BorrowedBook, String>("borrowDate"));

							TableColumn dueInCol = new TableColumn("Due in");
							dueInCol.setPrefWidth(75);
							dueInCol.setCellValueFactory(new PropertyValueFactory<BorrowedBook, String>("leadingDays"));

							List<BorrowedBook> bookList = new ArrayList<>();
							GridPane dueDateGrid = new GridPane();
							dueDateGrid.getStyleClass().addAll("pane");
							dueDateGrid.setPrefWidth(700);
							dueDateGrid.setPrefHeight(700);
							dueDateGrid.setAlignment(Pos.CENTER);

							BorrowedBook bb;
							int leadingDays;
							SimpleStringProperty ld;
							for (int i = 0; i < books.size(); i++) {
								//System.out.println("BOOK ID: " + books.get(i));
								if (!(books.get(i).getReturnDate() == null)
										&& (books.get(i).getCheckedOutDate().getDayOfYear() > bbStart.getDayOfYear())
										&& (books.get(i).getCheckedOutDate().getDayOfYear() <= bbEnd.getDayOfYear())) {

									leadingDays = (books.get(i).getReturnDate().getDayOfYear()
											- LocalDate.now().getDayOfYear());
									if (leadingDays < 0)
										ld = new SimpleStringProperty(Math.abs(leadingDays) + " Days late");
									else
										ld = new SimpleStringProperty(Math.abs(leadingDays) + " Days");
									System.out.println("Leading days: " + ld);
									if (books.get(i).isCheckedOut) {
										bb = new BorrowedBook(new SimpleStringProperty(books.get(i).getBookId()),
												new SimpleStringProperty(books.get(i).getBookName()),
												new SimpleStringProperty(books.get(i).getCheckedOutId()),
												new SimpleStringProperty(books.get(i).getBorrowerName()), ld,
												new SimpleStringProperty(books.get(i).getCheckedOutDate().format(dtf)));
										bookList.add(bb);
									}

								}
							}
							// TODO: Fix borrower id and name fields
							table.setItems(FXCollections.observableArrayList(bookList));
							table.getColumns().addAll(bookIdCol, bookNameCol, borrowerIdCol, borrowerNameCol,
									returnDateCol, dueInCol);

							Text dummy = new Text("");
							Text dummy1 = new Text("");

							HBox hbox1 = new HBox();
							hbox1.setSpacing(10);
							hbox1.getChildren().addAll(startDate, to, endDate);

							dueDateGrid.add(BorrowedBooksReportlabel, 0, 0);
							dueDateGrid.add(dummy, 0, 1);
							dueDateGrid.add(hbox1, 0, 2);
							dueDateGrid.add(label, 0, 3);
							dueDateGrid.add(table, 0, 4);
							Button printButton = new Button("Print");
							printButton.getStyleClass().add("buttonSmall");
							Button cancelButton = new Button("Cancel");
							cancelButton.getStyleClass().add("buttonSmall");

							HBox hbox2 = new HBox();
							hbox2.setSpacing(10);
							hbox2.getChildren().addAll(printButton, cancelButton);

							dueDateGrid.add(dummy1, 0, 5);
							dueDateGrid.add(hbox2, 0, 6);
							((Group) scene.getRoot()).getChildren().addAll(dueDateGrid);
							stage.setScene(scene);
							stage.show();

							cancelButton.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									stage.close();
								}
							});
							printButton.setOnAction(new EventHandler<ActionEvent>() {

								@Override
								public void handle(ActionEvent arg0) {
									PrinterJob printerJob = PrinterJob.createPrinterJob();
									if (printerJob.showPrintDialog(primaryStage.getOwner())
											&& printerJob.printPage(table))
										printerJob.endJob();

								}

							});

						}
					});

					exitBtn.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent arg0) {
							primaryStage.close();
						}

					});

				}
			}
		});

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	void getNextIds() {
		int maxId = 0;
		int temp = 0;
		for (LibraryBook book : books) {
			temp = Integer.parseInt(book.getBookId());
			if (maxId < temp)
				maxId = temp;
		}
		bookCount = maxId;
		System.out.println("BOOK ID START: " + bookCount);
		maxId = 0;
		for (Student student : students) {
			temp = Integer.parseInt(student.getId());
			if (maxId < temp)
				maxId = temp;
		}
		studentCount = maxId;
		System.out.println("STUDENT ID START: " + studentCount);
		maxId = 0;
		for (Teacher teacher : teachers) {
			temp = Integer.parseInt(teacher.getId());
			if (maxId < temp)
				maxId = temp;
		}
		teacherCount = maxId;
		System.out.println("TEACHER ID START: " + teacherCount);
	}

	@Override
	public void stop() {

		rf.writeBookData(bookfileName);
		rf.writeStudentData(studentsfileName);
		rf.writeTeacherData(teachersfileName);

		System.out.println("Stage is closing");
		// Save file
	}

	boolean isValidStudent(String studentId) {
		boolean isValidId = false;

		for (int i = 0; i < students.size(); i++) {
			if (students.get(i).getId().equals(studentId)) {
				isValidId = true;
			}
		}

		return isValidId;
	}

	boolean isValidTeacher(String teacherId) {
		boolean isValidId = false;

		for (int i = 0; i < teachers.size(); i++) {
			if (teachers.get(i).getId().equals(teacherId)) {
				isValidId = true;
			}
		}
		return isValidId;
	}

	/**
	 * populateBooksArray
	 */
	public void populateBooksArray() {
		for (LibraryBook book : books) {
			for (Student student : students) {
				// System.out.println("Book ID: " + book.getBookId() + "::" +
				// book.getCheckedOutId() + "::" + student.getId());
				if (book.getCheckedOutId().equals(student.getId())) {
					// System.out.println("Student ID: " + student.getId());
					student.bookIds.add(new SimpleStringProperty(book.getBookId()));
					student.getBorrowedDates().add(book.getCheckedOutDate());
					student.getReturnDates().add(book.getReturnDate());
				}
			}
			for (Teacher teacher : teachers) {
				if (book.getCheckedOutId().equals(teacher.getId())) {
					// System.out.println("Book ID: " + book.getBookId() + "::" +
					// book.getCheckedOutId() + "::" + teacher.getId());
					// System.out.println("Teacher ID: " + teacher.getId());
					teacher.bookIds.add(new SimpleStringProperty(book.getBookId()));
					teacher.getBorrowedDates().add(book.getCheckedOutDate());
					teacher.getReturnDates().add(book.getReturnDate());
				}
			}
		}
	}

	/**
	 * 
	 * @param bookIdField
	 * @param borrowerIdField
	 * @param borrowerType
	 * @return
	 */
	public String bookBorrow(TextField bookIdField, TextField borrowerIdField, String borrowerType) {
		// System.out.println(bookIdField.getText());
		String msg = "";
		for (LibraryBook book : books) {
			if (book.bookId.get().equals(bookIdField.getText())) {
				if(!book.isCheckedOut) {
					if (borrowerType.equals("Student")) {
						for (Student student : students) {
							if (student.id.get().equals(borrowerIdField.getText())) {
								System.out.println(student.id.get() + " has " + student.bookIds.size() +" books.");
								if (student.bookIds.size() < student.BOOK_LIMIT) {
									int index = student.bookIds.size();
									System.out.println("adding book to index: " + index);
									student.bookIds.add(index, new SimpleStringProperty(bookIdField.getText()));
									book.isCheckedOut = true;
									book.checkedOutBy = new SimpleStringProperty("Student");
									book.borrowerName = new SimpleStringProperty(
											student.getFirstName() + " " + student.getLastName());
									book.checkedOutId = new SimpleStringProperty(borrowerIdField.getText());
									book.checkedOutDate = LocalDate.now();
									book.returnDate = LocalDate.now().plusDays(student.DAY_LIMIT);
									student.borrowedDates.add(index, LocalDate.now());
									student.returnDates.add(index, LocalDate.now().plusDays(student.DAY_LIMIT));
									msg = (bookIdField.getText() + " Checked Out to: " + borrowerIdField.getText());
								} else {
									msg = "Book limit reached";
								}
							}
						}
					}

					else if (borrowerType.equals("Teacher")) {
						for (Teacher teacher : teachers) {
							if (teacher.id.get().equals(borrowerIdField.getText())) {
								if (teacher.bookIds.size() < teacher.BOOK_LIMIT) {
									int index = teacher.bookIds.size();
									teacher.bookIds.add(index, new SimpleStringProperty(bookIdField.getText()));
									book.isCheckedOut = true;
									book.checkedOutBy = new SimpleStringProperty("Teacher");
									book.borrowerName = new SimpleStringProperty(
											teacher.getFirstName() + " " + teacher.getLastName());
									book.checkedOutId = new SimpleStringProperty(borrowerIdField.getText());
									book.checkedOutDate = LocalDate.now();
									book.returnDate = LocalDate.now().plusDays(teacher.DAY_LIMIT);
									teacher.borrowedDates.add(index, LocalDate.now());
									teacher.returnDates.add(index, LocalDate.now().plusDays(teacher.DAY_LIMIT));
									msg = (bookIdField.getText() + " Checked Out to: " + borrowerIdField.getText());
								} else {
									msg = "Book limit reached";
								}
							}
						}

					}
				}
				else {
					msg = "Book Checked Out by Another Member. Please Choose Another Book";
				}
			}
		}
		return msg;
	}

	public String bookReturn(TextField bookIdField) {
		String message = "";
		boolean bookReturned = false;
		ArrayList<SimpleStringProperty> temp = new ArrayList<>();
		for (LibraryBook book : books) {
			if(bookReturned)
				break;
			if (book.bookId.get().equals(bookIdField.getText())) {
				System.out.println(bookIdField.getText() + " Needs Returning");
				System.out.println("Checking Students: " + students.size());
				if (book.isCheckedOut) {
					for (Student student : students) {
						if(bookReturned)
							break;
						System.out.println("Checking Students");
						temp = new ArrayList<>();
						System.out.println(student.bookIds.size());
						if (student.bookIds.size() <= student.BOOK_LIMIT && student.bookIds.size() > 0) {
							temp.addAll(student.bookIds);
							for (int i = 0; i < temp.size(); i++) {
								if (temp.get(i).get().equals(bookIdField.getText())) {
									System.out.println("found book with: " + student.getId());
									System.out.println("Students's books:" + student.getBookIds().toString());
									student.bookIds.remove(i);
									System.out.println("After return:" + student.getBookIds().toString());
								}
								book.isCheckedOut = false;
								book.checkedOutBy = null;
								book.borrowerName = null;
								book.checkedOutId = null;
								book.checkedOutDate = null;
								book.returnDate = null;
								message = "Book " + bookIdField.getText() + " Returned Successfully.";
								bookReturned = true;
								break;
							}
						}
					}

					for (Teacher teacher : teachers) {
						if(bookReturned)
							break;
						temp = new ArrayList<>();
						if (teacher.bookIds.size() <= teacher.BOOK_LIMIT && teacher.bookIds.size() > 0) {
							temp.addAll(teacher.bookIds);
							// System.out.println(teacher.bookIds.size());
							for (int i = 0; i < temp.size(); i++) {
								if (temp.get(i).get().equals(bookIdField.getText())) {
									teacher.bookIds.remove(i);
								}
								book.isCheckedOut = false;
								book.checkedOutBy = null;
								book.borrowerName = null;
								book.checkedOutId = null;
								book.checkedOutDate = null;
								book.returnDate = null;
								message = "Book " + bookIdField.getText() + " Returned Successfully.";
								bookReturned = true;
								break;
							}
						}
					}
				}
				else {
					message = "Book " + bookIdField.getText() + " is not Checkedout.";
				}
			}
			else {
				message = "Not a Valid Book ID. Please Enter A Valid ID";
			}
		}
		return message;
	}

	public static String calcFine(LocalDate borrowedDate, LocalDate returnDate, double finePerDay) {

		int overLimit = 0;

		System.out.println("Today: " + LocalDate.now().getDayOfYear());
		System.out.println("Return Date: " + returnDate.getDayOfYear());

		if (LocalDate.now().getYear() == returnDate.getYear()
				&& LocalDate.now().getDayOfYear() > returnDate.getDayOfYear()) {
			overLimit = LocalDate.now().getDayOfYear() - returnDate.getDayOfYear();
		}

		else if (LocalDate.now().getYear() != returnDate.getYear()) {
			overLimit = (LocalDate.now().getDayOfYear() + 365) - returnDate.getDayOfYear();
		}
		DecimalFormat df = new DecimalFormat("#0.00");
		System.out.println("overLimit:" + overLimit);
		if (overLimit == 0)
			return "$" + df.format(0.0);
		else
			return "$" + df.format(overLimit * finePerDay);
	}

	public void printBorrowers() {

		System.out.println("Students:");
		for (Borrower b : students) {
			System.out.println(b);
		}
		System.out.println();
		System.out.println("Teachers:");
		for (Borrower b : teachers) {
			System.out.println(b);
		}
	}

	public boolean isDuplicate(String firstName, String lastName, String borrowerType) {
		boolean isDuplicate = false;

		if (borrowerType.equals("Student")) {
			for (int i = 0; i < students.size(); i++) {
				if (students.get(i).getFirstName().equals(firstName)
						&& students.get(i).getLastName().equals(lastName)) {
					isDuplicate = true;
				}
			}
		}

		else if (borrowerType.equals("Teacher")) {
			for (int i = 0; i < teachers.size(); i++) {
				if (teachers.get(i).getFirstName().equals(firstName)
						&& teachers.get(i).getLastName().equals(lastName)) {
					isDuplicate = true;
				}
			}

		}

		return isDuplicate;
	}

	public boolean fNameDuplicate(String firstName, String borrowerType) {
		boolean fNameDuplicate = false;

		if (borrowerType.equals("Student")) {
			for (int i = 0; i < students.size(); i++) {
				if (students.get(i).getFirstName().equals(firstName)) {
					fNameDuplicate = true;
				}
			}
		}

		else if (borrowerType.equals("Teacher")) {
			for (int i = 0; i < teachers.size(); i++) {
				if (teachers.get(i).getFirstName().equals(firstName)) {
					fNameDuplicate = true;
				}
			}
		}

		return fNameDuplicate;
	}

	public boolean lNameDuplicate(String lastName, String borrowerType) {
		boolean lNameDuplicate = false;

		if (borrowerType.equals(lastName)) {
			for (int i = 0; i < students.size(); i++) {
				if (students.get(i).getFirstName().equals(lastName)) {
					lNameDuplicate = true;
				}
			}
		}

		else if (borrowerType.equals("Teacher")) {
			for (int i = 0; i < teachers.size(); i++) {
				if (teachers.get(i).getFirstName().equals(lastName)) {
					lNameDuplicate = true;
				}
			}
		}
		return lNameDuplicate;
	}

}
