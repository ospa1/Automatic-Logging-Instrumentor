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
