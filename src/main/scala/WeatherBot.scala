import cats.Parallel
import cats.effect.{Async, IO}
import cats.syntax.traverse._

import org.http4s.client.Client

import telegramium.bots.*
import telegramium.bots.high.implicits.*
import telegramium.bots.high.{Api, LongPollBot}


class WeatherBot(client: Client[IO])(implicit
                                     bot: Api[IO],
                                     asyncF: Async[IO],
                                     parallel: Parallel[IO]
) extends LongPollBot[IO](bot) {

  import Tokens.weatherToken

  override def onMessage(msg: Message): IO[Unit] = {
    val chatId = ChatIntId(msg.chat.id)
    val text = msg.text.getOrElse("")
    text match {
      case "/help" =>
        sendMessage(
          chatId,
          helpMessage
        ).exec.void
      case "/start" =>
        sendMessage(
          chatId,
          welcomeMessage
        ).exec.void
      case _ =>
        createForecast(msg)
    }
  }

  private def createForecast(msg: Message): IO[Unit] = {
    val cityName = msg.text.getOrElse("")
    try {
      val weatherResponse: IO[Seq[Forecast]] = new WeatherService(client, weatherToken).getWeather(cityName)
      weatherResponse.flatMap { weathers =>
        weathers.toList.traverse { weather =>
          sendMessage(
            chatId = ChatIntId(msg.chat.id),
            text = s"Погода в городе $cityName на текущий час: ${(weather.temp_2 - 273.15).toInt}°C, влажность составляет ${"%.1f".format(weather.vlaga_2)}%, " +
              s"а скорость ветра равна ${"%.1f".format(weather.wind_speed_10)} м/с. Рад помочь!"
          ).exec
        }
      }.void
    } catch {
      case e: NoSuchElementException =>
        sendMessage(
          chatId = ChatIntId(msg.chat.id),
          text = "Название города не распознано. Пожалуйста, напишите название города в именительном падеже с большой буквы, например: Москва, Новосибирск, Томск."
        ).exec.void
    }
  }

  private val welcomeMessage: String =
    """ Добро пожаловать в City Weather Bot!
      |Этот бот создан, чтобы давать прогноз погоды для какого-то города. Чтобы получить прогноз отправьте сообщение с
      |названием города с большой буквы, например: Москва. Приятного использования!
      |""".stripMargin

  private val helpMessage: String =
    """ Чтобы получить прогноз погоды введите название города с большой буквы.
      |Чтобы вывести это сообщение, используйте команду /help
      |""".stripMargin
}