import parsley.Char._
import parsley.Combinator._
import parsley.Parsley._
import parsley._

import scala.collection.mutable

import scala.language.implicitConversions


sealed trait Body

case object Halt extends Body

case class Increment(i: Int, l: Int) extends Body

case class Decrement(i: Int, l1: Int, l2: Int) extends Body

object Main {
  def main(args: Array[String]): Unit = {
    val prog =
      """R4- -> L0, L1
        |R5- -> L2, L3
        |R6- -> L1, L4
        |R6- -> L3, L6
        |R5- -> L4, L5
        |R4+ -> L6
        |HALT
      """.stripMargin

    val start = 0
    val regs = mutable.ListBuffer(0, 0, 0, 0, 0, 10, 9)

    var pc = start

    val integer = token(some(digit)).map(_.mkString.toInt)

    val halt: Parsley[Body] = "HALT" #> Halt
    val inc: Parsley[Body] = lift2(Increment, attempt('R' *> integer <* '+') <* "->", 'L' *> integer)
    val dec: Parsley[Body] = lift3(Decrement, attempt('R' *> integer <* '-') <* "->", 'L' *> integer, ',' *> 'L' *> integer)

    val program = many(halt <|> inc <|> dec) <* notFollowedBy(anyChar)

    runParser(program, prog) match {
      case Success(insts) =>
        var halted = false
        while (!halted) {
          println("L" + pc + ": " + regs.zipWithIndex.map { case (x, y) => "R" + y + ": " + x }.mkString("[", ", ", "]"))
          if (pc >= insts.size) {
            halted = true
          } else {
            val inst = insts(pc)
            inst match {
              case Halt => halted = true
              case Increment(i, l) =>
                regs(i) += 1
                pc = l
              case Decrement(i, l1, l2) =>
                if (regs(i) > 0) {
                  regs(i) -= 1
                  pc = l1
                } else {
                  pc = l2
                }
            }
          }
        }
      case Failure(msg) => println(msg)
    }
  }

  implicit def charToken(c: Char): Parsley[Char] = token(char(c))
  implicit def stringToken(s: String): Parsley[String] = token(string(s) <* notFollowedBy(char('_') <|> alphaNum))
  def token[A](t: => Parsley[A]): Parsley[A] = attempt(t) <* many(whitespace)
}
