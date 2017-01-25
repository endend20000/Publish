package main;

 import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream; 
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties; 
 
 public class ParameterReader {
	 
	 private static String propertieFile="properties";
	 
	 private static String project="project";
	 
	 private static String source="Source";
	 
	 private static String target="Target";
	 
	 private static String shell="Shell";
	 
	 private static String localTarget="LocalTarget";
	 
	 private static Properties prop;
	 
	 private static void getPropertiesRead() throws Exception{
		 if(prop==null){
			 prop = new Properties();			 
			 String propertieFilePath = System.getProperty("user.dir")+"/"+propertieFile;			 
			 //InputStream is = new BufferedInputStream (new FileInputStream(propertieFilePath));
			 InputStream is = new BufferedInputStream (new FileInputStream("c:\\b.properties"));
			 InputStreamReader in=new InputStreamReader(is, "UTF-8");
			 prop.load(in);
		 }
	 }
	 
	 public static String getPropertiesByKey(String key) throws Exception{
		 getPropertiesRead();
		 return prop.getProperty(key);
	 }

	 public static List<Project> getProject() throws Exception {
		 getPropertiesRead();
		 
		 List<Project> projectList=new ArrayList<Project>();
		 
/*         Iterator<String> it=prop.stringPropertyNames().iterator();
             while(it.hasNext()){
                 String key=it.next();
                 System.out.println(key+":"+prop.getProperty(key));
             }*/
		 
		 for(int i=1;true;i++){
			 String projectName=prop.getProperty(project+i);
			 if(projectName==null){
				 break;
			 }
			 Project p=generateProject(i);
			 projectList.add(p);
		 }
		// in.close();
		 return projectList;
	 }
	 
	private static Project generateProject(int index) throws Exception{
		String projectItem=project+index;
		
		 String projectSource=prop.getProperty(projectItem+source);
		 String projectTarget=prop.getProperty(projectItem+target);
		 String projectShell=prop.getProperty(projectItem+shell);
		 String projectLocalTarget=prop.getProperty(projectItem+localTarget);
		 String projectName=prop.getProperty(projectItem);
		 
		 Project p=new Project();
		 p.setProjectName(projectName);
		 p.setProjectSource(projectSource);
		 p.setProjectTarget(projectTarget);
		 p.setProjectLocalTarget(projectLocalTarget);
		 p.setProjectShell(projectShell.split(","));
		return p;
	}
	
 }

