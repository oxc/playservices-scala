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

import com.google.android.gms.fitness.Fitness
import de.esotechnik.playservicesscala.macros.loadApi

package object fitness {

  @loadApi(Fitness.SensorsApi, Fitness.SENSORS_API) object Sensors {}
  @loadApi(Fitness.RecordingApi, Fitness.RECORDING_API) object Recording {}
  @loadApi(Fitness.SessionsApi, Fitness.SESSIONS_API) object Sessions {}
  @loadApi(Fitness.HistoryApi, Fitness.HISTORY_API) object History {}
  @loadApi(Fitness.BleApi, Fitness.BLE_API) object Ble {}
  @loadApi(Fitness.ConfigApi, Fitness.CONFIG_API) object Config {}

}
