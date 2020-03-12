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
    TemplateClass.instrum.method("in method: calculation");
    return x % y;
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
