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

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.language.implicitConversions

package object macros {

  import scala.annotation.{StaticAnnotation, compileTimeOnly}
  import scala.reflect.macros.whitebox
  import scala.language.experimental.macros

  @compileTimeOnly("@requireApi requires macro paradise")
  class requireApi(requiredApi: Api[_]) extends StaticAnnotation {
    def macroTransform(annottees: Any*) = macro ApiLoaderMacros.requireApi
  }

  @compileTimeOnly("@provideApi requires macro paradise")
  class provideApi(api: Any) extends StaticAnnotation {
    def macroTransform(annottees: Any*) = macro ApiLoaderMacros.provideApi
  }

  @compileTimeOnly("@delegateApi requires macro paradise")
  class delegateApi extends StaticAnnotation {
    def macroTransform(annottees: Any*) = macro ApiLoaderMacros.delegateApi
  }

  class ApiLoaderMacros(val c: whitebox.Context) {

    import c.universe._

    def bail(message: String) = c.abort(c.enclosingPosition, message)

    val apiClientType = typeOf[GoogleApiClient]

    /**
     * Lets the annotated object extend the ApiLoader trait with the provided Api type.
     *
     * At the moment this could be easily implemented in code, but having it as a macro already
     * allows us to quickly add new features like Option builders etc.
     * 
     * @param annottees
     * @return
     */
    def requireApi(annottees: c.Expr[Any]*) = {
      annottees.map(_.tree) match {
        case List(q"..$annotations object $name extends $parent with ..$traits { ..$body }") => {

          val requiredApiTree = c.macroApplication match {
            case Apply(Select(q"new requireApi($rTree)", _), _) => (rTree: Tree)
          }
          val requiredApi = c.Expr(c.typecheck(requiredApiTree))

          val result = q"""
              $annotations object $name extends $parent with ..$traits with ApiRequirement[${requiredApi.actualType}] {
                val requiredApi = $requiredApiTree

                ..$body
              }
            """

          c.info(c.enclosingPosition, s"Generated $result", force = false)

          c.Expr(result)
        }
        case mismatch => bail(s"@requireApi can only be applied to object definitions. Encountered unexpected $mismatch")
      }
    }

    def delegateApi(annottees: c.Expr[Any]*) : c.Expr[Any] = {
      annottees.map(_.tree) match {
        case List(apiDef : ValDef, q"..$modifiers class $name(..$params) extends $parent with ..$traits { ..$body }" ) =>

          val apiVar = q"${apiDef.name} : ${apiDef.tpt}"
          val api = c.Expr(c.typecheck(apiDef.tpt, c.TYPEmode))

          val defs = mapMethods(api.actualType, apiVar)

          val result = q"""
              $modifiers class $name(..$params) extends $parent with ..$traits with ApiProvider {

                ..$body
                ..$defs
              }
            """

          c.info(c.enclosingPosition, s"Generated $result", force = false)

          c.Expr(result)
        case mismatch => bail(s"@delegateApi can only be applied to the value param of a class definitions. Encountered unexpected $mismatch")
      }
    }

    def provideApi(annottees: c.Expr[Any]*) : c.Expr[Any] = {
      val apiTree = c.macroApplication match {
        case Apply(Select(q"new provideApi($aTree)", _), _) => aTree: Tree
      }
      val api = c.Expr(c.typecheck(apiTree))

      annottees.map(_.tree) match {
        case List(q"..$modifiers object $name extends $parent with ..$traits { ..$body }") =>

          val defs = mapMethods(api.actualType, apiTree)

          val result = q"""
              $modifiers object $name extends $parent with ..$traits with ApiProvider {

                ..$body
                ..$defs
              }
            """

          c.info(c.enclosingPosition, s"Generated $result", force = false)

          c.Expr(result)
        case mismatch => bail(s"@provideApi can only be applied to object definitions. Encountered unexpected $mismatch")
      }
    }

    def mapMethods(apiType: Type, apiVar: Tree) = {
      val methods = apiType.members.flatMap {
        case method: MethodSymbol => Some(method)
        case _ => None
      }.filter { m => // for now we only support simple stuff. Should be sufficient
        m.typeParams.isEmpty &&
          m.paramLists.size == 1 &&
          m.paramLists.head.headOption.exists(_.asTerm.typeSignature =:= apiClientType)
      }

      if (methods.isEmpty) {
        bail(s"No methods found that take a GoogleApiClient. This doesn't look like an API object: $apiType")
      }

      methods.map { m =>
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

        q"""@inline def ${m.name.toTermName}(..$paramDefs)(implicit ${clientParam.forMethodDefinition}) : $returnType = {
          $apiVar.${m.name}(${clientParam.forMethodInvocation}, ..$paramRefs)
        }"""
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
        if (nameCounters.get(myBaseName).exists(_.hasMoreThan1)) {
          myBaseName + myNum
        } else {
          myBaseName
        }
      })

      def forMethodDefinition = q"$myName : ${param.typeSignature}"

      def forMethodInvocation = {
        val baseClasses = param.typeSignature.baseClasses

        // make sure (java) repeated types are passed along properly
        if ((baseClasses contains definitions.JavaRepeatedParamClass) ||
          (baseClasses contains definitions.RepeatedParamClass)) {
          q"$myName : _*"
        } else {
          q"$myName"
        }
      }
    }

    private[ApiLoaderMacros] class Counter {
      private var count: Int = 0

      def incrementAndGet() = {
        count += 1
        count
      }

      def hasMoreThan1 = {
        count > 1
      }
    }

  }
}

trait ApiRequirement[A <: Api[_]] {

  val requiredApi: A

  def apply[R](body: this.type => R)(implicit googleApiClient: GoogleApiClient): Option[R] = {
    this.ifAvailable map body
  }

  def ifAvailable(implicit googleApiClient: GoogleApiClient): Option[this.type] = {
    if (googleApiClient.hasConnectedApi(requiredApi)) {
      Some(this)
    } else {
      None
    }
  }

  def ?(implicit googleApiClient: GoogleApiClient) = ifAvailable

}

trait ApiProvider extends Any {
  /**
   * Converts a PendingResult to a scala Future. This is protected on purpose, since the caller
   * must ensure that only ever on such Future is created per PendingResult instance, and the
   * PendingResult is not used elsewhere (because the PendingResult overwrites its callback when
   * called multiple times).
   */
  implicit protected def pendingResult2Future[O <: Result](pendingResult: PendingResult[O]): Future[O] = {
    val promise = Promise[O]()
    pendingResult.setResultCallback(new ResultCallback[O] {
      override def onResult(result: O) = promise success result
    })
    promise.future
  }
}
