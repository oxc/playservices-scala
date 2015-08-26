package de.esotechnik.playservicesscala

import com.google.android.gms.fitness.Fitness
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object fitness {

  val Sensors = loadApi(Fitness.SensorsApi)
  val Recording = loadApi(Fitness.RecordingApi)
  val Sessions = loadApi(Fitness.SessionsApi)
  val History = loadApi(Fitness.HistoryApi)
  val Ble = loadApi(Fitness.BleApi)
  val Config = loadApi(Fitness.ConfigApi)

  trait PlayServicesFitnessSensors { self : PlayServices =>
    self.addApi(Fitness.SENSORS_API)

    protected val fitnessSensors = Sensors
  }

  trait PlayServicesFitnessRecording { self : PlayServices =>
    self.addApi(Fitness.RECORDING_API)

    protected val fitnessRecording = Recording
  }

  trait PlayServicesFitnessSessions { self : PlayServices =>
    self.addApi(Fitness.SESSIONS_API)

    protected val fitnessSessions = Sessions
  }

  trait PlayServicesFitnessHistory { self : PlayServices =>
    self.addApi(Fitness.HISTORY_API)

    protected val fitnessHistory = History
  }

  trait PlayServicesFitnessBle { self : PlayServices =>
    self.addApi(Fitness.BLE_API)

    protected val fitnessBle = Ble
  }

  trait PlayServicesFitnessConfig { self : PlayServices =>
    self.addApi(Fitness.CONFIG_API)

    protected val fitnessConfig = Config
  }

  trait PlayServicesFitness extends AnyRef
    with PlayServicesFitnessSensors
    with PlayServicesFitnessRecording
    with PlayServicesFitnessSessions
    with PlayServicesFitnessHistory
    with PlayServicesFitnessBle
    with PlayServicesFitnessConfig
  {
    self : PlayServices =>
  }



}
