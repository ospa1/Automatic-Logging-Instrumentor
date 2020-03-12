import java.io.{BufferedReader, File, FileReader, PrintWriter}
import java.util.logging.Logger

import com.typesafe.config.{Config, ConfigFactory}
import main.logger
import org.apache.commons.io.FileUtils
import org.eclipse.core.internal.utils.FileUtil
import org.eclipse.jdt.core.dom._
import org.eclipse.jdt.core.dom.rewrite.{ASTRewrite, ListRewrite}
import org.eclipse.jdt.core.dom.Block
import org.eclipse.jdt.core.dom.rewrite.ListRewrite
import scala.collection.mutable.ListBuffer
import java.util

import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.dom.{AST, ASTParser}

//parse some java file
  //save it as old_name.java
  //get all the variables and methods
  // traverse the nodes of the tree by using visitor pattern to traverse tree in order
      // add logging instrumentation
      // add logging before or after method invocation inside or outside method
// write this back to java file
  //currently saves as name.java (same name)

// main object that adds the instrumentation to a file
object main extends App {

  //set up logger and config
  val logger:Logger = Logger.getLogger("project")
  val config: Config = ConfigFactory.load("application.conf")

  // load paths from config file
  val source_path = config.getString("details.source_path")
  logger.info(source_path)
  val filepath:String = config.getString("details.file_path")
  val unitName = config.getString("details.unit_name")

  // set up the parser
  val parser:ASTParser = ASTParser.newParser(AST.JLS8)
  parser.setKind(ASTParser.K_COMPILATION_UNIT)
  parser.setResolveBindings(true)
  val options = JavaCore.getOptions
  parser.setCompilerOptions(options)
  parser.setUnitName(unitName)
  val sources = Array(source_path)
  val classpath:Array[String] = Array()
  parser.setEnvironment(classpath, sources, Array[String]("UTF-8"), true)

  // get the code from the file
  // should probably be in a try block in case of incorrect input
  val oldCode = readFileToString(filepath).toCharArray

  // store old code to an old_filename.java file
  FileUtils.write(new File(source_path+"\\old_"+unitName) , oldCode)

  parser.setSource(oldCode)
  val cu:CompilationUnit = parser.createAST(null).asInstanceOf[CompilationUnit]
  cu.recordModifications() // allows modification

  //get AST and rewrite
  val ast:AST = cu.getAST()
  val rewriter = ASTRewrite.create(ast)

  // implement selected visitor functions
  cu.accept(new ASTVisitor(){

    // visits variables that are declared
    override def visit(node: VariableDeclarationFragment):Boolean = {

      // get some information about the variable
      val name:SimpleName = node.getName()
      val lineNum:Int = cu.getLineNumber(name.getStartPosition())
      logger.info("variable name: " + name.toString + "\tline: " + lineNum)
      println(node)

      // add instrumentation to variable
      // TODO move block into its own method
      val methodInvocation = ast.newMethodInvocation
      val qname:QualifiedName = ast.newQualifiedName(ast.newSimpleName("TemplateClass"), ast.newSimpleName("instrum"))
      methodInvocation.setExpression(qname)
      methodInvocation.setName(ast.newSimpleName("method"))
      val literal = ast.newStringLiteral()
      literal.setLiteralValue("in if statement")
      methodInvocation.arguments().asInstanceOf[util.List[Expression]].add(literal)
      node.getAST.newBlock().statements().asInstanceOf[util.List[Statement]].add(ast.newExpressionStatement(methodInvocation))

      return true
    }

    //visits methods
    override def visit(node: MethodDeclaration): Boolean = {

      //get information about method
      val name:SimpleName = node.getName()
      val lineNum:Int = cu.getLineNumber(name.getStartPosition())
      logger.info("method name: " + name.toString + "\tline: " + lineNum)

      //add instrumentation to methods
      val methodInvocation = ast.newMethodInvocation
      val qname:QualifiedName = ast.newQualifiedName(ast.newSimpleName("TemplateClass"), ast.newSimpleName("instrum"))
      methodInvocation.setExpression(qname)
      methodInvocation.setName(ast.newSimpleName("method"))
      val literal = ast.newStringLiteral()
      literal.setLiteralValue("in method: "+ node.getName)
      methodInvocation.arguments().asInstanceOf[util.List[Expression]].add(literal)
      node.getBody.statements().asInstanceOf[util.List[Statement]].add(ast.newExpressionStatement(methodInvocation))

      return true
    }

    // visit if statements
    override def visit(node: IfStatement): Boolean ={

      // add instrumentation to if statement
      val methodInvocation = ast.newMethodInvocation
      val qname:QualifiedName = ast.newQualifiedName(ast.newSimpleName("TemplateClass"), ast.newSimpleName("instrum"))
      methodInvocation.setExpression(qname)
      methodInvocation.setName(ast.newSimpleName("method"))
      val literal = ast.newStringLiteral()
      literal.setLiteralValue("in if statement")
      methodInvocation.arguments().asInstanceOf[util.List[Expression]].add(literal)
      node.getAST.newBlock().statements().asInstanceOf[util.List[Statement]].add(ast.newExpressionStatement(methodInvocation))
      return  true
    }

    // does nothing at the moment until making a generic method to handle all node types
    override def visit(node: VariableDeclarationExpression): Boolean ={
      return true
    }
  })

  // save changes to the file
  val writer = new PrintWriter(unitName)
  writer.print(cu.toString)
  writer.close()

  // open the file and begin reading from it
  // returns a string containing the contents of the file
  def readFileToString(value: String): String ={

    // open file and read line
    logger.info("reading from: " + value)
    val b: BufferedReader = new BufferedReader(new FileReader(value)) // should be in a try block in case of bad input
    val s:StringBuilder = new StringBuilder()
    var line:String = b.readLine()

    // stores all the lines into s
    while (line != null){
      s.append(line)
      s.append(System.lineSeparator())
      line = b.readLine()
    }

    logger.info(s.toString())
    return s.toString()
  }

}

