package org.paradicms.lib.generic.stores

import java.net.UnknownHostException

import org.apache.jena.atlas.web.HttpException
import org.apache.jena.query.QueryException
import org.scalatest.{Assertion, Matchers, WordSpec}

abstract class AbstractSparqlStoreSpec extends WordSpec with Matchers {
  def withUnknownHostExceptionCatch(test: () => Assertion): Assertion =
    try {
      test()
    } catch {
      case e: HttpException => e.getCause match {
        case _: UnknownHostException => assert(true)
        case _ => throw e
      }
      case e: QueryException => e.getCause match {
        case _: UnknownHostException => assert(true)
        case _ => throw e
      }
    }
}
