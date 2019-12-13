package controllers;

import id_generator.GeneratorMain;
import javafx.scene.chart.*;
import javafx.scene.text.Text;
import json_reader_writer.JsonReader;
import member_manager.Member;
import member_manager.Planner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.json.simple.parser.ParseException;

import java.io.IOException;

// TODO: FIX ALL ENCAPSULATION

public class MainController {

    ObservableList<String> calculatorBoxList =
            FXCollections.observableArrayList();

    // "Planned Value (PV)", "Earned Value (EV)", "Actual Cost (AC)",
    //                                                    "Budget at Completion(BAC)", "Schedule Variance (SV)",
    //                                                    "Schedule Performance Index (SPI)", "Cost Variance (CV)",
    //                                                    "Cost Performance Index (CPI)"

    @FXML
    private javafx.scene.control.Button exitApp;
    /*@FXML
    private Button registerMemberB;*/
    @FXML
    private ChoiceBox<String> calculatorBox;
    //calculatorBox.setValue("Planned Value (PV)");
    //calculatorBox.setItems(calculatorBoxList);
    @FXML
    private TextField firstNameRegister;
    @FXML
    private TextField lastNameRegister;
    @FXML
    private TextField dobRegister;
    @FXML
    private Button registerButton;
    @FXML
    private TextField searchForID;
    @FXML
    private Button searchForIDButton;
    @FXML
    private Text revealName;
    @FXML
    private Text revealID;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private BarChart chart;

    private static JsonReader reader = new JsonReader();
    private static Planner planner = reader.loadPlanner();

    /*public void initialize(){
        loadData();
    }*/

    public void loadData(){
        String a = "Planned Value (PV)";
        String b = "Earned Value (EV)";
        String c = "Actual Cost (AC)";
        String d = "Budget at Completion(BAC)";
        String e = "Schedule Variance (SV)";
        String f = "Schedule Performance Index (SPI)";
        String g = "Cost Variance (CV)";
        String h = "Cost Performance Index (CPI)";

        calculatorBoxList.addAll(a,b,c,d,e,f,g,h);
        calculatorBox.getItems().addAll(calculatorBoxList);
    }


    // Code goes into here, whatever you want to happen when you search.
    public void search() {
        Window owner = searchForIDButton.getScene().getWindow();
        int searchedID = 0;
        boolean IDValid = true;
        boolean IDFound = false;

        if (searchForID.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Info", "Search field must be filled in!");
            System.out.println("Search failed (Empty search).");
            IDValid = false;
        } else if (GeneratorMain.isParsable(searchForID.getText())) {
            searchedID = Integer.parseInt(searchForID.getText());

            if (Integer.parseInt(searchForID.getText()) < 10000) {
                AlertHelper.showAlert(Alert.AlertType.WARNING, owner, "Error", "ID's are positive 5-digit integers!");
                System.out.println("Search failed (Invalid ID format, less than 5 digits OR negative input).");
                IDValid = false;
            } else if (Integer.parseInt(searchForID.getText()) > 99999) {
                AlertHelper.showAlert(Alert.AlertType.WARNING, owner, "Error", "ID's are positive 5-digit integers!");
                System.out.println("Search failed (Invalid ID format, more than 5 digits");
                IDValid = false;
            }
        } else {
            if (!GeneratorMain.isParsable(searchForID.getText())) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Error", "ID's must be 5-digit integers!");
                System.out.println("Search failed (Invalid ID format, non-number characters).");
                IDValid = false;
            }
        }
        if (IDValid) {
            //Input the team member's information into the page
            System.out.println(">> Debugger");
            System.out.println(planner.members);

            String memberName = "";
            for (Member member : planner.members) {
                System.out.println(member.getId());
                if (searchedID == member.getId()) {
                    IDFound = true;
                    memberName = member.getFirstName().concat(" ").concat(member.getLastName());
                    break;
                }
            }

            if (IDFound) {
                AlertHelper.showAlert(Alert.AlertType.CONFIRMATION, owner, "Success", "Member found, loading information...");
                revealName.setText(memberName);
                revealID.setText(Integer.toString(searchedID));
                System.out.println("Name set to: " + memberName);
                System.out.println("ID set to: " + searchedID);
            } else {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, owner, "Error", "Member ID does not yet exist.");
            }
        }
    }

    public void showHours(ActionEvent event) throws IOException {
        //doCode();
        chart.setTitle("Hours worked by: <name>");
        xAxis.setLabel("Week");
        yAxis.setLabel("Hours");
    }

    public void showSalary(ActionEvent event) throws IOException {
        //doCode();
        chart.setTitle("Salaries earned by: <name>");
        xAxis.setLabel("Week");
        yAxis.setLabel("Amount (SEK)");
    }

    public void addMember(ActionEvent event) throws IOException{

        Parent addMemberParent = FXMLLoader.load(getClass().getResource("../fxml-files/newEmployee.fxml"));
        Scene addMemberScene = new Scene(addMemberParent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(addMemberScene);
        window.show();
    }

    @FXML
    protected void registerMember(ActionEvent event) throws NumberFormatException {
        Window owner = registerButton.getScene().getWindow(); //setup the popup
        boolean success = true;

        if (firstNameRegister.getText().isEmpty()) { //checks if first name field is empty
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Error", "First name field can not be empty!");
            System.out.println("First name field failed.");
            success = false;

        } else if (lastNameRegister.getText().isEmpty()) { //checks if last name field is empty
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Error", "Last name field can not be empty!");
            System.out.println("Last name field failed.");
            success = false;
        } else if (GeneratorMain.isParsable(dobRegister.getText())) { //checks if birthday field valid but < 0 or > 991231
            int tempInt = Integer.parseInt(dobRegister.getText());
            if (tempInt <= 0) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Error", "Age can not be less than 0!");
                System.out.println("Birthday field failed (negative input).");
                success = false;
            }
            if (tempInt > 20181212) { //The highest accepted value as of the day implemented.
                AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Error", "Please follow the appropriate format YYMMDD!");
                System.out.println("Birthday field failed (input exceeds max accepted input).");
                success = false;
            }
        } else if (!GeneratorMain.isParsable(dobRegister.getText())) { //checks if birthday field is empty or invalid
                AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Error", "Please follow the appropriate format YYMMDD!");
                System.out.println("Birthday field failed (invalid input).");
                success = false;
        }
        if (success) {
            int genID = GeneratorMain.generateID(firstNameRegister.getText(), lastNameRegister.getText(), Integer.parseInt(dobRegister.getText()));
            AlertHelper.showAlert(Alert.AlertType.CONFIRMATION, owner, "Success", "Member successfully registered! \n" +
                    firstNameRegister.getText() + "'s generated ID is: " + genID);
            System.out.println("Member registration successful.");
            //Add the member to the Planner
            planner.addMember(firstNameRegister.getText(), lastNameRegister.getText(), Integer.parseInt(dobRegister.getText()), 1);
            System.out.println(firstNameRegister.getText() + " was added to the planner.");
            System.out.println(planner.members);
        }
    }

    public void viewMember(ActionEvent event) throws IOException {

        Parent viewMemberParent = FXMLLoader.load(getClass().getResource("../fxml-files/userInfo.fxml"));
        Scene viewMemberScene = new Scene(viewMemberParent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(viewMemberScene);
        window.show();


    }

    public void editEmployee(ActionEvent event) throws IOException{

        Parent editMemberParent = FXMLLoader.load(getClass().getResource("../fxml-files/addMemberContribution.fxml"));
        Scene editMemberScene = new Scene(editMemberParent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(editMemberScene);
        window.show();

    }

    public void editInfo(ActionEvent event) throws IOException{

        Parent editInfoMemberParent = FXMLLoader.load(getClass().getResource("../fxml-files/dashboard.fxml"));
        Scene editInfoMemberScene = new Scene(editInfoMemberParent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(editInfoMemberScene);
        window.show();
    }

    public void backButtonView(ActionEvent event) throws IOException{
        Parent backMemberParent = FXMLLoader.load(getClass().getResource("../fxml-files/userInfo.fxml"));
        Scene backMemberScene = new Scene(backMemberParent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(backMemberScene);
        window.show();
    }

    @FXML
    public void projectOverview(ActionEvent event) throws IOException{

        Parent projectMemberParent = FXMLLoader.load(getClass().getResource("../fxml-files/projectOverview.fxml"));
        Scene projectMemberScene = new Scene(projectMemberParent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(projectMemberScene);
        window.show();

    }

    public void contactUs(ActionEvent event) throws IOException{
        Parent contactUsParent = FXMLLoader.load(getClass().getResource("../fxml-files/contactUs.fxml"));
        Scene contactUsScene = new Scene(contactUsParent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(contactUsScene);
        window.show();
    }

    @FXML
    public void displayValue(){

    }

    // This code is for all back buttons that go back to the dashboard.
    @FXML
    public void back(ActionEvent event) throws IOException {

        Parent backMemberParent = FXMLLoader.load(getClass().getResource("../fxml-files/dashboard.fxml"));
        Scene backMemberScene = new Scene(backMemberParent);

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

        window.setScene(backMemberScene);
        window.show();
    }

    public void exit(){
        Stage stage = (Stage) exitApp.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
}
