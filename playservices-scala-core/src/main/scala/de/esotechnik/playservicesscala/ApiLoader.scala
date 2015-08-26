package de.esotechnik.playservicesscala

import com.google.android.gms.common.api.{GoogleApiClient, PendingResult, Result, ResultCallback}

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

object ApiLoader {
  def loadApi(api: Any) = macro ApiLoaderMacros.loadApi
}

class ApiLoaderMacros(val c: Context) {
  import c.universe._

  def loadApi(api: c.Expr[Any]) = {

    def bail(message: String) = c.abort(c.enclosingPosition, message)

    val apiClientType = typeOf[GoogleApiClient]

    val methods = api.actualType.members.flatMap {
      case method: MethodSymbol => Some(method)
      case _ => None
    }.filter { m => // for now we only support simple stuff. Should be sufficient
      m.typeParams.isEmpty &&
        m.paramLists.size == 1 &&
        m.paramLists.head.headOption.exists(_.asTerm.typeSignature =:= apiClientType)
    }

    if (methods.isEmpty) {
      bail(s"No methods found that take a GoogleApiClient. This doesn't look like an API object: $api")
    }

    val defs = methods.map { m =>
      val paramNameCounters = mutable.Map[String, Counter]()

      val clientParam = new RichParam(m.paramLists.head.head, paramNameCounters)
      val params = m.paramLists.head.drop(1).map(p => new RichParam(p, paramNameCounters))

      val paramDefs = params.map(_.forMethodDefinition)
      val paramRefs = params.map(_.forMethodInvocation)

      // convert PendingResults to Futures
      val returnType = if (m.returnType <:< typeOf[PendingResult[_]]) {
        appliedType(typeOf[Future[_]], m.returnType.dealias.typeArgs.head)
      } else {
        m.returnType
      }

      q"def ${m.name.toTermName}(..${paramDefs})(implicit ${clientParam.forMethodDefinition}) : ${returnType} = $api.${m.name}(${clientParam.forMethodInvocation}, ..${paramRefs})"
    }

    /** We need to define a local class below, and we'll generate a fresh name
      * to make sure we don't shadow some definition we might need in the
      * future.
      */
    val className = TypeName(c.freshName(api.actualType.getClass.getSimpleName))

    /** And now we define our anonymous class and instantiate it. See
      * [[http://stackoverflow.com/a/18485004/334519 this Stack Overflow
      * answer]] for some discussion of why we need both a local class
      * definition and an anonymous class.
      */
    val clazz = q"""
        class $className extends ApiWrapper {
          ..$defs
        }
      """

    c.info(c.enclosingPosition, s"Generated $clazz", false)

    c.Expr[Any](q"""
      $clazz

      new $className {}
    """)
  }

  private[ApiLoaderMacros] class RichParam(val param: c.universe.Symbol, nameCounters: mutable.Map[String, Counter]) {
    val myBaseName = {
      val paramName = param.name.toString
      if (!paramName.startsWith("x$")) {
        paramName
      } else {
        var t = param.typeSignature.dealias.toString
        // get simpleName
        val pos = t.lastIndexOf('.')
        if (pos >= 0) {
          t = t.substring(pos + 1)
        }
        t = t(0).toLower + t.substring(1)
        // Int* -> ints
        t.replace('*', 's')
      }
    }

    val myNum = nameCounters.getOrElseUpdate(myBaseName, new Counter).incrementAndGet()

    lazy val myName = TermName({
      // qualify if multiple
      if (nameCounters.get(myBaseName).map(_.hasMoreThan1).getOrElse(false)) {
        myBaseName + myNum
      } else {
        myBaseName
      }
    })

    def forMethodDefinition = q"${myName} : ${param.typeSignature}"

    def forMethodInvocation = {
      val baseClasses = param.typeSignature.baseClasses

      // make sure (java) repeated types are passed along properly
      if ((baseClasses contains definitions.JavaRepeatedParamClass) ||
        (baseClasses contains definitions.RepeatedParamClass)) {
        q"${myName} : _*"
      } else {
        q"${myName}"
      }
    }
  }

  private[ApiLoaderMacros] class Counter {
    private var count: Int = 0

    def incrementAndGet() = {
      count += 1; count
    }

    def hasMoreThan1 = { count > 1 }
  }
}

class ApiWrapper {
  /**
   * Converts a PendingResult to a scala Future. This is protected on purpose, since the caller
   * must ensure that only ever on such Future is created per PendingResult instance, and the
   * PendingResult is not used elsewhere (because the PendingResult overwrites its callback when
   * called multiple times).
   */
  implicit protected def pendingResult2Future[O <: Result](pendingResult: PendingResult[O]) : Future[O] = {
    val promise = Promise[O]
    pendingResult.setResultCallback(new ResultCallback[O] {
      override def onResult(result: O) = promise success result
    })
    promise.future
  }
}
