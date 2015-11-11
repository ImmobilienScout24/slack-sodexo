package de.is24

import com.amazonaws.services.lambda.runtime.LambdaLogger

trait SodexoLogger {
  def log(message: String)
}

class LoggerWrapper(lambdaLogger: LambdaLogger) extends SodexoLogger {
  override def log(message: String): Unit = lambdaLogger.log(message)
}

class StdoutLogger extends SodexoLogger {
  override def log(message: String): Unit = println(message)
}
