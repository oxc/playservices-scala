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

import com.google.android.gms.plus.Plus
import com.google.android.gms.plus.Plus.PlusOptions
import de.esotechnik.playservicesscala.macros.loadApi

package object plus {

  @loadApi(Plus.AccountApi, Plus.API) object Account {}
  @loadApi(Plus.MomentsApi, Plus.API) object Moments {}
  @loadApi(Plus.PeopleApi, Plus.API) object People {}

}
