import cats.Parallel
import cats.effect.{Async, IO, Ref}

import org.http4s.client.Client

import io.circe._
import io.circe.parser._
import io.circe.generic.auto._

import telegramium.bots.*
import telegramium.bots.high.implicits.*
import telegramium.bots.high.{Api, LongPollBot}

class WeatherBot(client: Client[IO])(implicit
                                     bot: Api[IO],
                                     asyncF: Async[IO],
                                     parallel: Parallel[IO]
) extends LongPollBot[IO](bot) {

  import tokens.weather_token

  private case class UserState(waitingForInput: Boolean)

  private val userStates: Ref[IO, Map[Long, UserState]] = Ref.unsafe(Map.empty)

  override def onMessage(msg: Message): IO[Unit] = {
    for {
      states <- userStates.get
      userId = msg.chat.id
      userState = states.getOrElse(userId, UserState(waitingForInput = false))

      response <- if (userState.waitingForInput) {
        handleUserInput(msg).flatMap { _ =>
          userStates.update(_ + (userId -> UserState(waitingForInput = false)))
        }
      } else {
        handleCommand(msg).flatMap { _ =>
          userStates.update(_ + (userId -> UserState(waitingForInput = true)))
        }
      }
    } yield response
  }

  private def handleCommand(msg: Message): IO[Unit] = {
    msg.text match {
      case Some("/weather") =>
        sendMessage(chatId = ChatIntId(msg.chat.id), text = "Введите название города:").exec.void
      case _ =>
        sendMessage(chatId = ChatIntId(msg.chat.id), text = "Неизвестная команда.").exec.void
    }
  }

  private def handleUserInput(msg: Message): IO[Unit] = {
    val cityName = msg.text.getOrElse("")

    val weatherResponse: IO[Forecast] = new WeatherService(client, weather_token).getWeather(cityName)

    weatherResponse.flatMap { jsonResponse =>
      decode[Forecast](jsonResponse) match {
        case Right(forecasts) => { weather =>
          sendMessage(
            chatId = ChatIntId(msg.chat.id),
            text = s"Погода в $cityName: ${(weather.temp_2 - 273.15)}°C, влажность ${weather.vlaga_2}%, скорость ветра ${weather.wind_speed_10} м/с"
          ).exec.void

          case Left(error)
          =>
          sendMessage(chatId = ChatIntId(msg.chat.id), text = s"Ошибка декодирования погоды для города '$cityName': ${error.getMessage}").exec.void
        }
      }
    .handleErrorWith { error =>
        sendMessage(chatId = ChatIntId(msg.chat.id), text = s"Ошибка получения погоды для города '$cityName': ${error.getMessage}").exec.void
      }
    }
  }
