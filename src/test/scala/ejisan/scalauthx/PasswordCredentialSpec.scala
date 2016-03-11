package ejisan.scalauthx

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import org.specs2.mutable.Specification

class PasswordCredentialSpec extends Specification {
  val factory = PasswordCredentialFactory.get
  val timeout = 10.second

  "PasswordCredential" should {
    "supports serialization" in {
      val x = Await.result(factory.generate("test"), timeout)
      val y = PasswordCredential.fromString(x.toString)
      (x == y) must beEqualTo (true)
    }
  }
}
