package software_masters.gui_test;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import application.MockMain;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import software_masters.planner_networking.Node;
import static org.testfx.api.FxAssert.verifyThat;

class PlanEditViewTest extends GuiTestBase
{

	String addButtonID = "#addSectionButton";
	String deleteButtonID = "#deleteSectionButton";
	String saveID = "#saveButton";
	String backID = "#backToPlansButton";
	String yearLabelID = "#yearLabel";
	String yearFieldID = "#editYearField";
	String logoutID = "#logoutButton";
	String treeViewID = "#treeView";
	String nameFieldID = "#nameField";
	String dataFieldID = "#dataField";

	@Test
	void test() throws Exception
	{
		getToPlanEditView();
		checkBranch();
		doubleClickOn("Mission");
		defaultTest();
		validAddSection();
		invalidAddSection();
		validDeleteSection();
		invalidDeleteSection();
		testSave();
		testLogout();
		testBackToPlans();
		testClose();
	}

	/**
	 * moves from login screen to the plan edit window in the gui
	 */
	private void getToPlanEditView()
	{
		clickOn("#loginButton");
		sleep(1000);
		clickOn((javafx.scene.Node) find("2019"));

	}

	/**
	 * Test that default values exist.
	 */
	private void defaultTest()
	{
		verifyThat(addButtonID, (Button button) ->
		{
			return button.getText().equals("Add Section");
		});
		verifyThat(deleteButtonID, (Button button) ->
		{
			return button.getText().equals("Delete Section");
		});
		verifyThat(saveID, (Button button) ->
		{
			return button.getText().equals("Save");
		});
		verifyThat(backID, (Button button) ->
		{
			return button.getText().equals("Back to plans");
		});
		verifyThat(yearLabelID, (Label label) ->
		{
			return label.getText().equals("Year");
		});
		verifyThat(yearFieldID, (TextField textField) ->
		{
			return textField.getText().equals("2019");
		});
		verifyThat(logoutID, (Button button) ->
		{
			return button.getText().equals("Log Out");
		});
		verifyThat(treeViewID, (TreeView<Node> tree) ->
		{
			return tree.getRoot().getValue().getName().equals("Mission");
		});
		checkPage("Mission","");
	}

	/**
	 * Tests that add section is executed properly
	 */
	private void validAddSection()
	{
		TreeView<Node> tree = find(treeViewID);
		doubleClickOn("Mission");
		clickOn((javafx.scene.Node) find("Goal"));
		((TextField) find(nameFieldID)).setText("Go 1");
		((TextField) find(dataFieldID)).setText("Go 1 content");
		clickOn(addButtonID);
		
		verifyThat(treeViewID, (TreeView<Node> treeview) ->
		{
			if (treeview.getRoot().getChildren().size() != 2)
				return false;
			return true;
		});
		
		doubleClickOn("Mission");
		clickOn((javafx.scene.Node) find("Goal")); //will fail if a goal section was not created
		checkPage("Goal","");
		
		clickOn((javafx.scene.Node) find("Go 1")); 
		checkPage("Go 1","Go 1 content");
	}
	
	/**
	 * Tests that add section does not work when a section cannot be duplicated.
	 */
	private void invalidAddSection()
	{
		TreeView<Node> tree = find(treeViewID);
		doubleClickOn("Mission");
		clickOn(addButtonID);
		checkPopupMsg("Cannot add a section of this type");
		clickOn("OK");
		/*if addsection is called anyway a null pointer exception will occur because mission's parent is null
		As long as we do not get a null pointer exception then we know the client did not 
		attempt to add a section.
		*/
		checkPage("Mission","");
		
	}
	
	/**
	 * This verifies the delete section button is working properly.
	 */
	private void validDeleteSection()
	{
		TreeView<Node> tree = find(treeViewID);
		doubleClickOn("Mission");
		clickOn((javafx.scene.Node) find("Go 1"));
		clickOn(deleteButtonID);
		checkPopupMsg("Are you sure you want to delete this section and all dependencies?"+
				"They cannot be recovered.");
		clickOn((javafx.scene.Node) find("Don't Delete"));
		checkPage("Go 1","Go 1 content");
		
		clickOn(deleteButtonID);
		checkPopupMsg("Are you sure you want to delete this section and all dependencies?"+
		"They cannot be recovered.");
		clickOn((javafx.scene.Node) find("Delete"));
		checkBranch();
		verifyThat(treeViewID, (TreeView<Node> treeview) ->
		{
			if (treeview.getRoot().getChildren().size() != 1)
				return false;
			return true;
		});
	}

	/**
	 * This verifies the delete section button is working properly.
	 */
	private void invalidDeleteSection()
	{
		TreeView<Node> tree = find(treeViewID);
		clickOn((javafx.scene.Node) find("Mission"));
		clickOn(deleteButtonID);
		checkPopupMsg("Are you sure you want to delete this section and all dependencies?"+
				"They cannot be recovered.");
		clickOn((javafx.scene.Node) find("Delete"));
		checkPopupMsg("Cannot delete this section");
		clickOn("OK");
		
		clickOn((javafx.scene.Node) find("Mission"));
		clickOn(deleteButtonID);
		checkPopupMsg("Are you sure you want to delete this section and all dependencies?"+
				"They cannot be recovered.");
		clickOn((javafx.scene.Node) find("Don't Delete"));
		checkPage("Mission","");
		
		clickOn("Goal");
		clickOn(deleteButtonID);
		checkPopupMsg("Are you sure you want to delete this section and all dependencies?"+
				"They cannot be recovered.");
		clickOn((javafx.scene.Node) find("Delete"));
		checkPopupMsg("Cannot delete this section");
		clickOn("OK");
		
	}
	
	private void testSave() {
		
	}
	
	/**
	 * Verifies the logout button works as advertised. Handles if changes have been made or not.
	 */
	private void testLogout() {
		//test that unsaved changes produce popup
		//cancel case
		clickOn("Mission");
		clickOn(dataFieldID);
		write("mission edit");
		clickOn(logoutID);
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("Cancel");
		checkPage("Mission","mission edit");
		//do not save changes, "no" case
		clickOn(logoutID);
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("No");
		getToPlanEditView();
		checkPage("Mission","");
		//save changes, "yes" case
		clickOn(dataFieldID);
		write("mission edit");
		clickOn(logoutID);
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("Yes");
		getToPlanEditView();
		checkPage("Mission","mission edit");
		
		//verify no changes doesn't create popup
		doubleClickOn("Mission");
		clickOn("Goal");
		checkPage("Goal","");
		clickOn(logoutID);
		getToPlanEditView();
		clickOn("Mission");
		((TextField) find("mission edit")).setText("");
		clickOn(saveID);
		
	}
	
	/**
	 * Verifies the back to plan button works as advertised. Handles if changes have been made or not.
	 */
	private void testBackToPlans() {
		//test that unsaved changes produce popup
		//cancel case
		clickOn("Mission");
		clickOn(dataFieldID);
		write("mission edit");
		clickOn(backID);
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("Cancel");
		checkPage("Mission","mission edit");
		//do not save changes, "no" case
		clickOn(backID);
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("No");
		clickOn((javafx.scene.Node) find("2019"));
		checkPage("Mission","");
		//save changes, "yes" case
		clickOn(dataFieldID);
		write("mission edit");
		clickOn(backID);
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("Yes");
		clickOn((javafx.scene.Node) find("2019"));
		checkPage("Mission","mission edit");
		
		//verify no changes doesn't create popup
		doubleClickOn("Mission");
		clickOn("Goal");
		checkPage("Goal","");
		clickOn(backID);
		clickOn((javafx.scene.Node) find("2019"));
		clickOn("Mission");
		((TextField) find("mission edit")).setText("");
		clickOn(saveID);
	}
	
	/**
	 * Verifies the close button works as advertised. Handles if changes have been made or not.
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	private void testClose() throws Exception {
		//test that unsaved changes produce popup
		//cancel case
		clickOn("Mission");
		clickOn(dataFieldID);
		write("mission edit");
		closeCurrentWindow();
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("Cancel");
		checkPage("Mission","mission edit");
		//do not save changes, "no" case
		closeCurrentWindow();
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("No");
		ApplicationTest.launch(MockMain.class);
		getToPlanEditView();
		checkPage("Mission","");
		//save changes, "yes" case
		clickOn(dataFieldID);
		write("mission edit");
		closeCurrentWindow();
		checkPopupMsg("You have unsaved changes. Do you wish to save before exiting?");
		clickOn("Yes");
		ApplicationTest.launch(MockMain.class);
		getToPlanEditView();
		clickOn((javafx.scene.Node) find("2019"));
		checkPage("Mission","mission edit");
		
		//verify no changes doesn't create popup
		doubleClickOn("Mission");
		clickOn("Goal");
		checkPage("Goal","");
		closeCurrentWindow();
		ApplicationTest.launch(MockMain.class);
		getToPlanEditView();
		clickOn((javafx.scene.Node) find("2019"));
		clickOn("Mission");
		((TextField) find("mission edit")).setText("");
		clickOn(saveID);
	}
	
	/**
	 * Helper method that verifies the add branch method initializes the correct nodes.
	 */
	private void checkBranch() {
		String[] names= {"Mission","Goal","Learning Objective","Assessment Process","Results"};
		for(String name: names) {
			doubleClickOn(name);
			checkPage(name,"");
		}
	}
	
	/**
	 * Helper method that checks the content displayed for a given node based on provided string.
	 * @param name
	 * @param content
	 */
	private void checkPage(String name,String content) {
		verifyThat(nameFieldID, (TextField textfield) ->
		{
			return textfield.getText().equals(name);
		});
		verifyThat(dataFieldID, (TextField textfield) ->
		{
			return textfield.getText().equals(content);
		});
	}
	
	
}
