package com.qrlogbook.fx;

import com.qrlogbook.AccountStore;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** JavaFX front end sharing the same local account and student store as Swing. */
public final class FxApp extends Application {
    private final AccountStore store = new AccountStore();
    private Stage stage;
    private AccountStore.Account signedIn;
    private String role = "student";
    private static final String PURPLE="#5a50b5", TEXT="#302b52", BG="#f3f0ff";

    @Override public void start(Stage primaryStage) { stage=primaryStage; stage.setTitle("QR Logbook — High School for Service and Learning"); stage.setMinWidth(900); stage.setMinHeight(620); showLogin(); stage.show(); }

    private void showLogin(){
        VBox card=new VBox(12); card.setAlignment(Pos.CENTER); card.setPadding(new Insets(30,38,28,38)); card.setMaxWidth(420); card.setStyle("-fx-background-color: rgba(255,255,255,.94); -fx-background-radius: 24; -fx-effect: dropshadow(gaussian, rgba(20,14,66,.22), 28,0,0,12);");
        Label logo=new Label("◈");logo.setStyle("-fx-font-size:44px;-fx-text-fill:"+PURPLE+";"); Label title=new Label("QR Logbook");title.setStyle("-fx-font-size:30px;-fx-font-weight:bold;-fx-text-fill:"+PURPLE+";");
        Label school=new Label("HIGH SCHOOL FOR SERVICE AND LEARNING"); school.setWrapText(true); school.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#493589;");
        Label subtitle=new Label("Welcome! Sign in to continue.");subtitle.setStyle("-fx-text-fill:#625b79;");
        ToggleGroup group=new ToggleGroup(); HBox tabs=new HBox(6); tabs.setAlignment(Pos.CENTER); for(String r:new String[]{"Student","Teacher","Admin"}){ToggleButton t=new ToggleButton(r);t.setToggleGroup(group);t.setMaxWidth(Double.MAX_VALUE);HBox.setHgrow(t,Priority.ALWAYS);t.setOnAction(e->{role=r.toLowerCase();style();});tabs.getChildren().add(t);if(r.equals("Student")){t.setSelected(true);}};
        Runnable style=()->tabs.getChildren().forEach(n->{ToggleButton t=(ToggleButton)n;t.setStyle(t.isSelected()?"-fx-background-color:"+PURPLE+";-fx-text-fill:white;-fx-background-radius:10;":"-fx-background-color:#eeeaff;-fx-text-fill:"+TEXT+";-fx-background-radius:10;");}); style.run();
        TextField id=new TextField();id.setPromptText("Enter ID number");PasswordField password=new PasswordField();password.setPromptText("Password");Label error=new Label();error.setStyle("-fx-text-fill:#b91c1c;");
        Button login=primary("LOG IN");login.setOnAction(e->{if(id.getText().isBlank()||password.getText().isBlank()){error.setText("Please enter your ID and password.");return;}if(!store.authenticate(role,id.getText().trim(),password.getText().toCharArray())){error.setText("Invalid login details or account is inactive.");return;}signedIn=store.accounts(role).stream().filter(a->a.id().equalsIgnoreCase(id.getText().trim())).findFirst().orElse(null);showDashboard();});
        Button forgot=new Button("Forgot Password?");forgot.setStyle("-fx-background-color: transparent;-fx-text-fill:#241653;");forgot.setOnAction(e->showRecovery());
        card.getChildren().addAll(logo,title,school,subtitle,tabs,new Label("ID Number"),id,new Label("Password"),password,error,login,forgot,new Label("QR Logbook System • Attendance made simple • 2026"));
        StackPane root=new StackPane(card);root.setPadding(new Insets(24));root.setStyle("-fx-background-color:linear-gradient(to bottom,#615497,#40327f);");stage.setScene(new Scene(root,1100,744));
    }

    private void showRecovery(){
        VBox card=new VBox(12);card.setAlignment(Pos.CENTER);card.setPadding(new Insets(30));card.setMaxWidth(450);card.setStyle("-fx-background-color:white;-fx-background-radius:20;");
        Label title=new Label("Account Recovery");title.setStyle("-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:"+PURPLE+";"); ComboBox<String> r=new ComboBox<>(FXCollections.observableArrayList("Student","Teacher","Admin"));r.getSelectionModel().select("Student");TextField id=new TextField();id.setPromptText("Student Number / Teacher ID / Username");TextField email=new TextField();email.setPromptText("Registered recovery email");Label error=new Label();error.setStyle("-fx-text-fill:#b91c1c;");
        Button verify=primary("VERIFY ACCOUNT");verify.setOnAction(e->{String role=r.getValue().toLowerCase();if(!store.verifyRecovery(role,id.getText().trim(),email.getText().trim())){error.setText("Those details do not match an active account with a saved email.");return;}TextInputDialog d=new TextInputDialog();d.setTitle("Reset password");d.setHeaderText("Verified account: "+id.getText());d.setContentText("New password (at least 8 characters):");String p=d.showAndWait().orElse("");if(p.length()<8){error.setText("Choose a password with at least 8 characters.");return;}TextInputDialog confirm=new TextInputDialog();confirm.setTitle("Confirm password");confirm.setHeaderText("Confirm your new password");String p2=confirm.showAndWait().orElse("");if(!p.equals(p2)){error.setText("The passwords do not match.");return;}store.resetPassword(role,id.getText().trim(),p.toCharArray());new Alert(Alert.AlertType.INFORMATION,"Password changed on this device. You can now sign in.").showAndWait();showLogin();});
        Button back=new Button("Back to login");back.setOnAction(e->showLogin());card.getChildren().addAll(title,new Label("Verify your account details to choose a new password."),r,id,email,error,verify,back);stage.setScene(new Scene(new StackPane(card),1100,744));
    }

    private void showDashboard(){
        BorderPane shell=new BorderPane();shell.setStyle("-fx-background-color:"+BG+";");VBox side=new VBox(8);side.setPadding(new Insets(22,14,18,14));side.setPrefWidth(220);side.setStyle("-fx-background-color:linear-gradient(to bottom,#30276d,#5d50b7);");Label brand=new Label("QR Logbook");brand.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;");Button home=nav("Dashboard"),records=nav("Student Records"),logout=nav("Log Out");side.getChildren().addAll(brand,home);if(signedIn.role().equals("admin")||signedIn.role().equals("teacher"))side.getChildren().add(records);Region spacer=new Region();VBox.setVgrow(spacer,Priority.ALWAYS);side.getChildren().addAll(spacer,logout);
        StackPane content=new StackPane();content.setPadding(new Insets(25));showHome(content);home.setOnAction(e->showHome(content));records.setOnAction(e->showStudents(content));logout.setOnAction(e->{signedIn=null;showLogin();});shell.setLeft(side);shell.setCenter(content);stage.setScene(new Scene(shell,1100,744));
    }

    private void showHome(StackPane content){
        VBox page=new VBox(18);Label title=new Label("Dashboard");title.setStyle("-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:"+TEXT+";");Label greeting=new Label("Welcome back, "+signedIn.name());greeting.setStyle("-fx-font-size:18px;-fx-text-fill:"+TEXT+";");HBox stats=new HBox(14,stat("Students",store.count("student")),stat("Teachers",store.count("teacher")),stat("Accounts",store.count(null)));page.getChildren().addAll(title,stats,greeting,new Label("Attendance made simple for High School for Service and Learning."));content.getChildren().setAll(page);
    }

    private void showStudents(StackPane content){
        VBox page=new VBox(12);HBox head=new HBox();head.setAlignment(Pos.CENTER_LEFT);Label title=new Label("Student Records");title.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:"+TEXT+";");Region spacer=new Region();HBox.setHgrow(spacer,Priority.ALWAYS);Button add=primary("Add Student");head.getChildren().addAll(title,spacer,add);
        TableView<AccountStore.Account> table=new TableView<>();TableColumn<AccountStore.Account,String> id=col("Student Number",AccountStore.Account::id),name=col("Name",AccountStore.Account::name),email=col("Email",AccountStore.Account::email),grade=col("Grade",AccountStore.Account::grade),section=col("Section",AccountStore.Account::section);table.getColumns().addAll(id,name,email,grade,section);refresh(table);VBox.setVgrow(table,Priority.ALWAYS);
        Button edit=new Button("Edit selected"),delete=new Button("Delete selected");HBox actions=new HBox(8,edit,delete);add.setOnAction(e->studentDialog(table,null));edit.setOnAction(e->{AccountStore.Account selected=table.getSelectionModel().getSelectedItem();if(selected!=null)studentDialog(table,selected);});delete.setOnAction(e->{AccountStore.Account selected=table.getSelectionModel().getSelectedItem();if(selected!=null&&new Alert(Alert.AlertType.CONFIRMATION,"Delete student "+selected.id()+"?",ButtonType.YES,ButtonType.NO).showAndWait().orElse(ButtonType.NO)==ButtonType.YES){store.deleteStudent(selected.id());refresh(table);}});
        page.getChildren().addAll(head,table,actions);content.getChildren().setAll(page);
    }

    private void studentDialog(TableView<AccountStore.Account> table,AccountStore.Account old){
        Dialog<ButtonType>d=new Dialog<>();d.setTitle(old==null?"Add Student":"Edit Student");GridPane f=new GridPane();f.setHgap(8);f.setVgap(8);f.setPadding(new Insets(12));TextField id=new TextField(old==null?"":old.id()),name=new TextField(old==null?"":old.name()),email=new TextField(old==null?"":old.email()),grade=new TextField(old==null?"":""+old.grade()),section=new TextField(old==null?"":""+old.section()),pass=new TextField();int row=0;for(Object[] pair:new Object[][]{{"Student number",id},{"Full name",name},{"Recovery email",email},{"Grade",grade},{"Section",section}}){f.add(new Label((String)pair[0]),0,row);f.add((Control)pair[1],1,row++);}if(old==null){pass.setPromptText("Temporary password");f.add(new Label("Temporary password"),0,row);f.add(pass,1,row);}d.getDialogPane().setContent(f);d.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);if(d.showAndWait().orElse(ButtonType.CANCEL)!=ButtonType.OK)return;try{if(old==null)store.addStudent(id.getText(),name.getText(),email.getText(),grade.getText(),section.getText(),pass.getText().toCharArray());else store.updateStudent(old.id(),id.getText(),name.getText(),email.getText(),grade.getText(),section.getText());refresh(table);}catch(IllegalArgumentException ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}}

    private void refresh(TableView<AccountStore.Account> table){table.setItems(FXCollections.observableArrayList(store.accounts("student")));}
    private TableColumn<AccountStore.Account,String> col(String title,java.util.function.Function<AccountStore.Account,String> value){TableColumn<AccountStore.Account,String> c=new TableColumn<>(title);c.setCellValueFactory(data->new javafx.beans.property.SimpleStringProperty(value.apply(data.getValue())));c.setPrefWidth(160);return c;}
    private VBox stat(String title,long count){VBox box=new VBox(8,new Label(title),new Label(String.valueOf(count)));box.setPadding(new Insets(18));box.setPrefWidth(190);box.setStyle("-fx-background-color:white;-fx-background-radius:16;-fx-border-color:#e8e2f8;-fx-border-radius:16;");box.getChildren().get(1).setStyle("-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:"+PURPLE+";");return box;}
    private Button primary(String text){Button b=new Button(text);b.setMaxWidth(Double.MAX_VALUE);b.setStyle("-fx-background-color:"+PURPLE+";-fx-text-fill:white;-fx-font-weight:bold;-fx-padding:12 18 12 18;-fx-background-radius:10;");return b;}
    private Button nav(String text){Button b=new Button(text);b.setMaxWidth(Double.MAX_VALUE);b.setAlignment(Pos.CENTER_LEFT);b.setStyle("-fx-background-color:transparent;-fx-text-fill:#e6e0ff;-fx-padding:12;");return b;}
    @Override public void stop() { }
    public static void main(String[] args){launch(args);}
}
