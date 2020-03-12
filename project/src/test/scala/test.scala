import org.scalatest.{FlatSpec, Matchers}

class test extends FlatSpec with Matchers{


  def f(string: String): String ={

    if (string.length > 0){
      return "success"
    }
    else{
      return "failed"
    }
  }

  "reading files" should "return code" in{
    val fil = "test"
    var result:String = ""
    try{
      result =  main.readFileToString(fil)
    }
    catch{
      case x:Exception => {
        result = ""
      }
    }
    val test = f(result)
    assert(test.contains("success"))
    assert(result.length > 0)
  }

  "instrumentation" should "log" in{
    var passed = false
    try{
      TemplateClass.instrum(0,"test")
      passed = true
    }
    catch{
      case x:Exception =>{
        passed = false
      }
    }
    assert(passed == true)
  }

  "variable and method in instrum class" should "log" in{
    //method log
    var passed = false
    try{
      TemplateClass.instrum.method("test")
      passed = true
    }
    catch{
      case x:Exception =>{
        passed = false
      }
    }
    assert(passed == true)

    //variable log
    passed = false
    try{
      TemplateClass.instrum.variable("test")
      passed = true
    }
    catch{
      case x:Exception =>{
        passed = false
      }
    }
    assert(passed == true)
  }

  "instrumented calc class test" should "not break" in{

    var passed = false
    var newCalc:calc = null
    //assignment
    try{
      newCalc = new calc
      passed = true
    }
    catch{
      case x:Exception =>{
        passed = false
      }
    }
    assert(passed == true)

    passed = false
    //invoke method
    try{
      newCalc.setX(5)
      passed = true
    }
    catch{
      case x:Exception =>{
        passed = false
      }
    }
    assert(passed == true)

    passed = false
    var newX = 0
    //data retrieval
    try{
      newX = newCalc.x
      passed = true
    }
    catch{
      case x:Exception =>{
        passed = false
      }
    }
    assert(passed == true)
    assert(newX == 5)
  }

}
