import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files



public class angularFormToJavaReader {

    List<String> parsedOutput; //This arraylist will follow the following pattern: NameOfObject, ObjectType, NameOfObject, ObjectType....
    String szFileLocation; 

    List<String> fileInput;


    String szOutFileName; 
    public angularFormToJavaReader(String toRead){
        
        parsedOutput = new ArrayList<String>();
        fileInput = new ArrayList<String>();
        szFileLocation = toRead;
    
    }


    public void readFile()
    {
        try {
            File angularFormFile = new File(szFileLocation);
            Scanner myReader = new Scanner(angularFormFile);
            
            
            while (myReader.hasNextLine()) {
              String data = myReader.nextLine();
              fileInput.add(data);
            }

            parseInput();
            myReader.close();
          } catch (FileNotFoundException e) {
            System.out.println("Unable to find file at location " + szFileLocation + " please try a valid location");
            e.printStackTrace();
          }
    }



    /**
     * This reads directly from the fileInput arrayList, if it's empty, nothing will be parsed
     */
    public void parseInput()
    {
        if(fileInput.size() == 0)
        {
            System.out.println("File Input is at size 0!");
            return;
        }


        System.out.println(szOutFileName);
        this.szOutFileName = fileInput.get(0).split(" ")[2].trim().replace("{", ""); //Remove { in case it was read in on accident



        for(int i = 1; i < fileInput.size(); i++)
        {
            //We have received "    cityYn: boolean = false;"
            String szLine = fileInput.get(i);
            String[] szLineSplit = szLine.split(":"); //splits "    cityYn" and " boolean = false";

            //Makes sure to skip empty spaces/information we don't need
            if(szLineSplit.length == 2)
            {
                String szObjectName = szLineSplit[0].trim(); //retrives "cityYn"
                String szObjectType = szLineSplit[1].trim().split("=")[0]; //retrieves "boolean"
    
                parsedOutput.add(szObjectName);
                parsedOutput.add(szObjectType);
            }

        }


    }


    public String getJavaType(String angularType)
    {
        switch(angularType.toLowerCase())
        {
            case("boolean"):
                return "boolean";
            case("string"):
                return "String";
            case("number"):
                return "Long";
            case("any"):
                return "string";
            default:
                return "Unknown Type";
        }
    }

    public void writeToObjectFile(String fileName, String fileType, boolean bPrivate)
    {
        //If user fails to enter a file name, use the one that was taken from the file read
        if(fileName == "" || fileName == null)
            fileName = szOutFileName;

        //If user fails to enter a file type, default to a .txt file
        if(fileType == "" || fileType == null)
            fileType = ".txt";


        String szFileOut = "./output/" + fileName + fileType;

        try {
            File fileOut = new File(szFileOut); 
            if (fileOut.createNewFile()) {
                System.out.println("File created: " + fileOut.getName());

            } else {
              
                System.out.println("File already exists.  Writing to file");
            }

            //begin writing to said file
            FileWriter myWriter = new FileWriter(szFileOut);
            myWriter.write("public class " + fileName + " { \n\n");
            

            //Set up all objects
            //We'll be incrementing by two as parsedOutput follows the pattern NameOfObject, ObjectType, NameOfObject, ObjectType....
            for(int i = 0; i < parsedOutput.size(); i+=2)
            {
                String szObjectName = parsedOutput.get(i);
                String szObjectType = parsedOutput.get(i + 1);

                if(bPrivate)
                    myWriter.append("\tprivate " + szObjectType + " " + szObjectName + ";\n");
                else
                    myWriter.append("\tpublic " + szObjectType + " " + szObjectName + ";\n");
            }


            //set up the object constructor
            myWriter.append("\n\n\tpublic " + fileName + "() {\n\n\t}\n\n");
            if(bPrivate)
            {
                    //Set up getters and setters (in case we desire the object variables to be private) 
                    for(int i = 0; i < parsedOutput.size(); i+=2)
                    {
                        String szObjectName = parsedOutput.get(i);
                        String szObjectType = parsedOutput.get(i + 1);
                        //set up getter, public objectType getObjectName() { return this.OBJECTNAME}
                        String szCapitalizedObjectName = szObjectName.substring(0, 1).toUpperCase() + szObjectName.substring(1);

                        myWriter.append("\tpublic " + szObjectType + " get" + szCapitalizedObjectName + "() {\n " + 
                                        "\t\treturn this." + szObjectName + "; \n" +
                                        "\t}\n\n" );


                        //Set up settter, public void setObjectName(ObjectType objectName) { this.objectName = objectName; }
                        myWriter.append("\tpublic void set" + szCapitalizedObjectName + "( " + szObjectType + " " + szObjectName + ") {\n" +
                                        "\t\t this." + szObjectName + " = " + szObjectName + "; \n" +
                                        "\t}\n\n");
                        
                    }
            }
            
            
            myWriter.append("}");
            myWriter.close();

          } catch (IOException e) {
            System.out.println("Unable to create/write to file " + fileName);
            e.printStackTrace();
          }
    }

    
}

