import cats.effect.IO

import io.circe.Decoder
import io.circe.derivation.Configuration
import io.circe.generic.auto.deriveDecoder

import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.implicits.uri

import CityCoordinates.cityCoordinates

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

final case class Forecast(
                           dt_forecast: String,
                           temp_100: Double,
                           temp_2: Double,
                           wind_speed_100: Double,
                           wind_speed_10: Double,
                           wind_dir_100: Double,
                           wind_dir_10: Double,
                           pres_80: Double,
                           pres_surf: Double,
                           vlaga_2: Double,
                           vidimost_surf: Double,
                           insolation_surf: Double,
                           oblachnost_atmo: Double,
                           uv_index: Double
                         )

class WeatherService(client: Client[IO], apiKey: String) {
  def getWeather(city: String): IO[Seq[Forecast]] = {
    val latLon = cityCoordinates.get(city)
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH")
    val formattedTime = String.valueOf(currentTime.format(formatter)) + ":00"
    val uri = uri"https://projecteol.ru/api/weather/".withQueryParams(
      Map("lat" -> latLon.get(0), "lon" -> latLon.get(1), "date" -> formattedTime, "token" -> apiKey)
    )
    client.expect[Seq[Forecast]](uri)(jsonOf[IO, Seq[Forecast]])
  }
}
