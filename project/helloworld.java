public class helloworld {
  public static void main(  String[] args){
    System.out.println("hello world");
    int a=0;
    TemplateClass.instrum(5,"a");
    TemplateClass.instrum.method("in method: main");
    TemplateClass.instrum.method("in method: main");
  }
}
