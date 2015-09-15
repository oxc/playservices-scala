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

import java.{util => ju}
import java.{lang => jl}

import com.google.android.gms.drive.{DriveFolder, DriveFile}
import com.google.android.gms.drive.metadata.{SortableMetadataField, SearchableCollectionMetadataField, SearchableMetadataField, SearchableOrderedMetadataField}
import com.google.android.gms.drive.query.{Query => PlayQuery, SortOrder, Filter, Filters}
import com.google.android.gms.drive.query.Query.{Builder => QueryBuilder}
import com.google.android.gms.drive.query.SortOrder.{Builder => SortOrderBuilder}
import com.google.android.gms.{drive => gms}
import de.esotechnik.playservicesscala.macros.{delegateApi, provideApi, requireApi}

import scala.collection.JavaConversions._

package object drive {

  @requireApi(gms.Drive.API) @provideApi(gms.Drive.DriveApi) object Drive {

    @provideApi(gms.Drive.DrivePreferencesApi) object Preferences {}
  }


  implicit class RichDriveFile(@delegateApi val driveFile: DriveFile) extends AnyVal {
  }

  implicit class RichDriveFolder(@delegateApi val driveFolder: DriveFolder) extends AnyVal {
  }

  /**************
   * Filter api *
   **************/

  @inline def allOf(filters: Filter*) = Filters.and(filters)

  @inline def anyOf(filters: Filter*) = Filters.or(filters)

  @inline implicit def not(filter: Filter) = !filter

  implicit class RichFilter(val filter: Filter) extends AnyVal {
    @inline def and(filters: Filter*) = Filters.and(filter, filters: _*)
    @inline def &(filters: Filter*) = and(filters: _*)
    @inline def &&(filters: Filter*) = and(filters: _*)

    @inline def or(filters: Filter*) = Filters.or(filter, filters: _*)
    @inline def |(filters: Filter*) = or(filters: _*)
    @inline def ||(filters: Filter*) = or(filters: _*)

    @inline def unary_! = Filters.not(filter)

    /* query builders */

    /**
     * Creates a query from the filter with the given sort order
     * @param sortOrder the sortOrder to sort by
     * @return the built query
     */
    @inline def sortBy(sortOrder: SortOrder) : PlayQuery = new QueryBuilder().addFilter(filter).setSortOrder(sortOrder).build()

    /**
     * Creates a query from the filter, sorted by the given field in ascending order
     * @param field the field to sort by
     * @return the built query
     */
    @inline def sortBy(field: SortableMetadataField[_]) : PlayQuery = sortBy(new SortOrderBuilder().addSortAscending(field).build())

    /**
     * Creates a query from the filter, sorted by the given fields
     * @param fields the fields to sort by
     * @return the built query
     */
    @inline def sortBy(fields: SortedField*) : PlayQuery = {
      val builder = new SortOrderBuilder()
      fields.foreach(_.add(builder))
      sortBy(builder.build())
    }
  }

  @inline implicit def isTrue(field: SearchableMetadataField[jl.Boolean]) = field === true

  trait SearchableMetadataFieldOps[T] extends Any {
    val field : SearchableMetadataField[T]

    @inline def eq(value: T) = Filters.eq(field, value)
    @inline def === (value: T) = eq(value)
  }

  trait SearchableMetadataStringFieldOps extends Any with SearchableMetadataFieldOps[jl.String] {
    @inline def contains(value: String) = Filters.contains(field, value)
  }

  trait SearchableOrderedMetadataFieldOps[T <: jl.Comparable[T]] extends Any with SearchableMetadataFieldOps[T] {
    override val field : SearchableOrderedMetadataField[T]

    @inline def gt (value: T) = Filters.greaterThan(field, value)
    @inline def > (value: T) = gt(value)

    @inline def gte (value: T) = Filters.greaterThanEquals(field, value)
    @inline def >= (value: T) = gte(value)

    @inline def lt (value: T) = Filters.lessThan(field, value)
    @inline def < (value: T) = lt(value)

    @inline def lte (value: T) = Filters.lessThanEquals(field, value)
    @inline def <= (value: T) = lte(value)
  }

  trait SearchableCollectionMetadataFieldOps[T] extends Any with SearchableMetadataFieldOps[ju.Collection[T]] {
    override val field: SearchableCollectionMetadataField[T]

    @inline def contains(value: T) = Filters.in(field, value)
    @inline def ∋ (value: T) = contains(value)

    @inline def ∌ (value: T) = !contains(value)
  }

  implicit class RichSearchableMetadataField[T](override val field: SearchableMetadataField[T])
    extends AnyVal with SearchableMetadataFieldOps[T] {
  }

  implicit class RichSearchableMetadataStringField(override val field: SearchableMetadataField[String])
    extends AnyVal with SearchableMetadataStringFieldOps {
  }

  implicit class RichSearchableOrderedMetadataField[T <: jl.Comparable[T]](override val field: SearchableOrderedMetadataField[T])
    extends AnyVal with SearchableOrderedMetadataFieldOps[T] {
  }

  implicit class RichSearchableCollectionMetadataField[T](override val field: SearchableCollectionMetadataField[T])
    extends AnyVal with SearchableCollectionMetadataFieldOps[T] {
  }

  implicit class RichSortableMetadataField(val field: SortableMetadataField[_]) {
    def ASC = AscendingSortedField(field)
    def DESC = DescendingSortedField(field)
  }

  sealed trait SortedField {
    def add(sortOrderBuilder: SortOrderBuilder)
  }

  final case class AscendingSortedField(field: SortableMetadataField[_]) extends SortedField {
    override def add(sortOrderBuilder: SortOrderBuilder): Unit = sortOrderBuilder.addSortAscending(field)
  }

  final case class DescendingSortedField(field: SortableMetadataField[_]) extends SortedField {
    override def add(sortOrderBuilder: SortOrderBuilder): Unit = sortOrderBuilder.addSortDescending(field)
  }
}
