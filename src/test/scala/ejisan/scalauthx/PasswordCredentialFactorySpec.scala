package ejisan.scalauthx

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import org.specs2.mutable.Specification

class PasswordCredentialFactorySpec extends Specification {
  val factory = PasswordCredentialFactory(
    0, "PBKDF2WithHmacSHA512", "NativePRNGNonBlocking", 16, 16, 5000)
  val timeout = 10.second

  "PasswordCredentialFactory" should {
    "can verify" in {
      val x = Await.result(factory.generate("test"), timeout)
      Await.result(factory.verify("test", x), timeout) must beEqualTo (true)
    }

    "generates always unique (Tries 100 times)" in {
      val cs = Await.result(
        Future.sequence((1 to 100).map(_ => factory.generate("test"))),
        timeout)
      (cs.size) must beEqualTo (cs.toSet.size)
    }

    "generates same credentials with same salt" in {
      val x = Await.result(factory.generate("test"), timeout)
      val y = Await.result(factory.generate("test", x.salt), timeout)
      (x == y) must beEqualTo (true)
      (x.hashCode) must beEqualTo (y.hashCode)
    }

    "supports large password (10000 length)" in {
      val lp = scala.util.Random.alphanumeric.slice(0, 10000).mkString
      val x = Await.result(factory.generate(lp), timeout)
      val y = Await.result(factory.generate(lp, x.salt), timeout)
      (x == y) must beEqualTo (true)
      (x.hashCode) must beEqualTo (y.hashCode)
    }

    "supports empty password" in {
      val x = Await.result(factory.generate(""), timeout)
      val y = Await.result(factory.generate("", x.salt), timeout)
      val z = Await.result(factory.generate(""), timeout)
      (x == y) must beEqualTo (true)
      (x.hashCode) must beEqualTo (y.hashCode)
      (x == z) must beEqualTo (false)
    }
  }
}
