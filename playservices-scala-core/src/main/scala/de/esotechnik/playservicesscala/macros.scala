/* Copyright 2015 Bernhard Frauendienst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.esotechnik.playservicesscala

import com.google.android.gms.common.api._

import scala.annotation.implicitNotFound
import scala.collection.mutable
import scala.concurrent.{Future, Promise}

package object macros {

  import scala.annotation.{compileTimeOnly, StaticAnnotation}
  import scala.reflect.macros.whitebox.Context
  import scala.language.experimental.macros

  @compileTimeOnly("@playApi requires macro paradise")
  class loadApi(api: Any, requiredApi: Api[_]) extends StaticAnnotation {
    def macroTransform(annottees: Any*) = macro ApiLoaderMacros.loadApi
  }

  class ApiLoaderMacros(val c: Context) {

    import c.universe._

    def bail(message: String) = c.abort(c.enclosingPosition, message)

    def loadApi(annottees: c.Expr[Any]*) = {
      val (apiTree, requiredApiTree) = c.macroApplication match {
        case Apply(Select(q"new loadApi($aTree, $rTree)", _), _) => (aTree : Tree, rTree : Tree)
      }
      val api = c.Expr(c.typecheck(apiTree))
      val requiredApi = c.Expr(c.typecheck(requiredApiTree))

      annottees.map(_.tree) match {
        case List(q"object $name extends $parent { ..$body }") => {

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

          val result = q"""
              object $name extends $parent with ApiWrapper[${requiredApi.actualType}] {
                val requiredApi = $requiredApiTree

                ..$body
                ..$defs
              }
            """

          c.info(c.enclosingPosition, s"Generated $result", false)

          c.Expr(result)
        }
        case _ => bail("@loadApi can only be applied to object definitions")
      }
    }

    private[ApiLoaderMacros] class RichParam(val param: c.universe.Symbol, nameCounters: mutable.Map[String, Counter]) {
      val myBaseName = {
        val paramName = param.name.toString
        if (!paramName.startsWith("x$")) {
          paramName
        } else {
          var t = param.typeSignature.dealias.toString
          // get simpleName
          t = t.replaceAll("[^\\[]+\\.", "")
          t = t(0).toLower + t.substring(1)
          // Int* -> ints
          t = t.replace('*', 's')
          // List[String] -> listOfString
          t = t.replace("[", "Of")
          t = t.replace("]", "")
          // Map[Int,String] -> mapOfIntAndString
          t = t.replace(",", "And")
          t
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
        count += 1;
        count
      }

      def hasMoreThan1 = {
        count > 1
      }
    }

  }
}

trait ApiWrapper[A <: Api[_]] {

  val requiredApi : A

  def apply[R](body : this.type => R)(implicit googleApiClient: GoogleApiClient) : Option[R] = {
    (this ifAvailable) map body
  }

  def ifAvailable(implicit googleApiClient: GoogleApiClient): Option[this.type] = {
    if (googleApiClient.hasConnectedApi(requiredApi)) {
      Some(this)
    } else {
      None
    }
  }

  def ?(implicit googleApiClient: GoogleApiClient) = ifAvailable

  /**
   * Converts a PendingResult to a scala Future. This is protected on purpose, since the caller
   * must ensure that only ever on such Future is created per PendingResult instance, and the
   * PendingResult is not used elsewhere (because the PendingResult overwrites its callback when
   * called multiple times).
   */
  implicit protected def pendingResult2Future[O <: Result](pendingResult: PendingResult[O]): Future[O] = {
    val promise = Promise[O]
    pendingResult.setResultCallback(new ResultCallback[O] {
      override def onResult(result: O) = promise success result
    })
    promise.future
  }
}
