package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Windows  extends JFrame implements MouseListener{

	private static final long serialVersionUID = 1L;

	private static JTextArea logTextArea;
	private JPanel buttonPanel;
	private JScrollPane scrollPane;
	
	public static Project clickedProject;
	private List<Project> projectList;
	
	public static void main(String[] args) {
		try {				
			@SuppressWarnings("unused")
			Windows windows=new Windows();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Windows() throws Exception{		
		generateTextArea();	
		generateButtons();
		setParamerer();
	}

	private void generateButtons() throws Exception{
		buttonPanel=new JPanel(new FlowLayout());
		projectList = ParameterReader.getProject();		
		for(Project project:projectList){
				JButton button=new JButton(project.getProjectName());
				button.addMouseListener(this);		
				buttonPanel.add(button);
				project.setButton(button);		
			}
		this.add(buttonPanel,BorderLayout.NORTH);	
	}
	
	private void generateTextArea() throws Exception{
		scrollPane=new JScrollPane();
		logTextArea=new JTextArea();		
		scrollPane.setViewportView(logTextArea);		
		this.add(scrollPane,BorderLayout.CENTER);		
	}
	
	private void setParamerer() {
		this.setTitle("发布小工具");
		this.setSize(1000, 800);
		this.setLocation(500, 200);
		this.setVisible(true);	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			for (Project project : projectList) {
				if (e.getSource() == project.getButton()) {					
					printLog("开始部署"+project.getProjectName());
					clickedProject = project;
					Compress.generateTarGzFIle(project.getProjectSource(),
							project.getProjectLocalTarget()+project.getProjectName());					
					printLog("压缩完成");
					
					FileUploadByFtp.fileUpload();					
					printLog("上传完成");
					
					Shell shell = new Shell();
					shell.executeCommands(project.getProjectShell());					
					printLog("命令执行完成");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void printLog(String logContent){
		logTextArea.append(logContent+"\r\n");		
		logTextArea.paintImmediately(logTextArea.getBounds());
	}
	

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
	

}
