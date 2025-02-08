import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp

import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.http4s.client.middleware.Logger

import telegramium.bots.high.Api
import telegramium.bots.high.BotApi

import tokens.telegram_token

object WeatherBotApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO].resource
      .use { httpClient =>
        val http = Logger(logBody = false, logHeaders = false)(httpClient)
        implicit val api: Api[IO] = createBotBackend(http, telegram_token)
        val weatherBot = new WeatherBot(httpClient)
        weatherBot.start().as(ExitCode.Success)
      }

  private def createBotBackend(http: Client[IO], token: String) =
    BotApi(http, baseUrl = s"https://api.telegram.org/bot$token")
}