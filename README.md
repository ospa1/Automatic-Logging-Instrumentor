### Automatic Logging Instrumentor

## Design
this design adds logging instrumentation to java files. first, the program uses configuration files to load the paths to the project and the file that one would want to add logging to. then the file is read and a copy is made with the prefic old_ added to the name of the file. the code is then parsed into an AST tree. afterwards, when the compilation unit invokes the accept method, a new ast visitor is created with some of its visit functions overriden and implemented. when the trees ins going through the visitor methods the logging instrumentation code is added. after all the nodes in the tree are visited the same file is overwritten. TemplateClass was created to be used by the program to inject invocations static methods into the java file to log the activity of the program. 

this project used scala 2.13.0 with sbt and for the java portion it uses java 11


## Pros and Cons
  pros:
  
    -instruments most methods and variables correctly
    -the added logging instrumentation works
    -scala code is functional with the exception of 1 variable used to read from the file
    
  cons:
  
    -it doesnt do the running multiple instances of the application portion
    -if a function has a return statement the logging is injected afterwards causing it to be unreachable
    -have to specify which file you want to add logging to (one at a time)
    -I think the block of code that adds the logging to the file could have been put into a seperate generic function that could have dealt with all the node types
    therfefore covering all the visit functions (and most of the java language) in the ASTVisitor class instead of implementing one at a time


## Results

for the helloworld program:

-before the instrumentation

    public class helloworld {
      public static void main(  String[] args){
        System.out.println("hello world");
        int a=0;
      }
    }

-after the instrumentation

    public class helloworld {
      public static void main(  String[] args){
        System.out.println("hello world");
        int a=0;
        TemplateClass.instrum(5,"a");
        TemplateClass.instrum.method("in method: main");
      }
    }
    
for the calc class:

-before 

    public class calc {

    int x = 10;
    int y = 5;

    // performs operation on x and y given a string
    int calculation(String operator){

        int z = 3;

        if(operator.equals("+")){
            return x+y;
        }
        if(operator.equals("-")){
            return x-y;
        }
        if(operator.equals("*")){
            return x*y;
        }
        if(operator.equals("/")){
            return x/y;
        }
        return x%y;
    }

    void setX(int newX){
        x=newX;
    }

    void setY(int newY){
        y=newY;
    }
    }
    
-after

    public class calc {
      int x=10;
      int y=5;
      int calculation(  String operator){
        int z=3;
        TemplateClass.instrum.variable("in variable: z");
        if (operator.equals("+")) {
          return x + y;
        }
        if (operator.equals("-")) {
          return x - y;
        }
        if (operator.equals("*")) {
          return x * y;
        }
        if (operator.equals("/")) {
          return x / y;
        }
        return x % y;
        TemplateClass.instrum.method("in method: calculation");
      }
      void setX(  int newX){
        x=newX;
        TemplateClass.instrum.method("in method: setX");
      }
      void setY(  int newY){
        y=newY;
        TemplateClass.instrum.method("in method: setY");
      }
    }
the instrumentor puts the function call at the bottom of the function so if a there is a return statement the function wont get called causing intellij to complain about unreachable code  



## How to install
import the project folder into intellij

## How to run
the object file main has the object main that is the starting point of the program.

but before main is ran the configuration file application.conf in the resources folder needs to be updated with the correct paths of the java file that you want to instrument.

the first variable in application.conf called source_path will needs to point to the root folder of the project. (ex. "C:\\\Users\\\OP-Laptop\\\Google Drive\\\project")

the second variable called called file_path is the path to the file that you want to instrument (ex. "C:\\\Users\\\OP-Laptop\\\Google Drive\\\project\\\helloworld.java") dont forget to replace "\" in a path to "\\\"

the third variable called unit_name is just the name of the file from the second variable (ex. helloworld.java)



for tests

	on sbt shell within intellij:
		test
	(note: the first test fails if the configuration is not set up before hand and the files with old_ prefix might have to be moved or removed or else the compiler will complain about duplicate clases)
