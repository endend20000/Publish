package main;

import javax.swing.JButton;

public class Project {
private String projectName;
private String projectSource;
private String projectTarget;
private String projectLocalTarget;
public String getProjectLocalTarget() {
	return projectLocalTarget;
}
public void setProjectLocalTarget(String projectLocalTarget) {
	this.projectLocalTarget = projectLocalTarget;
}
private String[] projectShell;
private JButton button;
public JButton getButton() {
	return button;
}
public void setButton(JButton button) {
	this.button = button;
}
public String getProjectName() {
	return projectName;
}
public String[] getProjectShell() {
	return projectShell;
}
public String getProjectSource() {
	return projectSource;
}
public String getProjectTarget() {
	return projectTarget;
}
public void setProjectName(String projectName) {
	this.projectName = projectName;
}
public void setProjectShell(String[] projectShell) {
	this.projectShell = projectShell;
}
public void setProjectSource(String projectSource) {
	this.projectSource = projectSource;
}
public void setProjectTarget(String projectTarget) {
	this.projectTarget = projectTarget;
}
}
