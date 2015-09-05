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

import com.google.android.gms.{fitness => gms}
import de.esotechnik.playservicesscala.macros.{provideApi, requireApi}

package object fitness {

  object Fitness {

    @requireApi(gms.Fitness.SENSORS_API)
    @provideApi(gms.Fitness.SensorsApi) object Sensors {}

    @requireApi(gms.Fitness.RECORDING_API)
    @provideApi(gms.Fitness.RecordingApi) object Recording {}

    @requireApi(gms.Fitness.SESSIONS_API)
    @provideApi(gms.Fitness.SessionsApi) object Sessions {}

    @requireApi(gms.Fitness.HISTORY_API)
    @provideApi(gms.Fitness.HistoryApi) object History {}

    @requireApi(gms.Fitness.BLE_API)
    @provideApi(gms.Fitness.BleApi) object Ble {}

    @requireApi(gms.Fitness.CONFIG_API)
    @provideApi(gms.Fitness.ConfigApi) object Config {}

  }
}
