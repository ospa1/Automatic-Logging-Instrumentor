import javafx.util.Pair;
import java.util.logging.Logger;

//this class is used to insert logging statements in to java code
public class TemplateClass {


    //log the information that is inserted
    public static void instrum(int line, String method, Object pair){
        Logger logger = Logger.getLogger("instrum");
        logger.info("instrum in line: " + line + " has " + method + " type: " + pair.getClass());
    }


    public static class instrum{

        // logs methods
        public static void method(String a){
            Logger logger = Logger.getLogger("instrum");
            logger.info(a);
        }

        //logs variables
        public static void variable(String a){
            Logger logger = Logger.getLogger("instrum");
            logger.info(a);
        }
    }
}
