

class main{
    public static void main(String[] args) {

        String szLocation = "C:/Users/Owner/Documents/Old Farts Home/OFH/src/app/user-form.ts";
        
        if(args.length != 0)
            szLocation = args[0];


        angularFormToJavaReader reader = new angularFormToJavaReader(szLocation);
        
        reader.readFile();
        reader.writeToObjectFile("", ".java", true);


    }
        
}