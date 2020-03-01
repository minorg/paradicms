package org.paradicms.lib.generic.stores.sparql

import java.net.UnknownHostException

import io.lemonlabs.uri.Url
import org.apache.jena.atlas.web.HttpException
import org.apache.jena.query.QueryException
import org.paradicms.lib.generic.stores.GenericTestData
import org.scalatest.{Assertion, Matchers, WordSpec}

abstract class AbstractSparqlStoreSpec extends WordSpec with Matchers {
  protected val configuration = SparqlStoreConfiguration(sparqlQueryUrl = Url.parse("http://fuseki:3030/ds/sparql"), sparqlUpdateUrl = Url.parse("http://fuseki:3030/ds/update"))
  protected val testData = new GenericTestData
  protected val currentUserUri = Option(testData.user.uri)

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
