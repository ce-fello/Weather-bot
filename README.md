# Weather-bot
That's an example of an implementation of a telegram bot using Scala programing language. It uses such libraries as *cats*, *http4s*, *circe* and *telegramium*. The main usage of bot is to send user weather forecast for the city he requires.
Project contains five scala code files:
* WeatherBotApp - application object that runs bot
* WeatherBot - class that implements all logic of the bot
* WeatherService - class that makes requests to the weather API (any third-party service that provides an API to work with it)
* Tokens - object that contains variables with two tokens: your bot telegram token and your weather API token
* CityCoordinates - object that contains coordinates of cities (this bot uses russian cities)
## How to run?
1. Clone the repository using ```git clone https://github.com/ce-fello/Weather-bot.git```
2. Go to the ```@BotFather``` and get your personal telegram bot token
3. This bot uses ```https://projecteol.ru``` and it's API to get weather forecast. Go this it's website and get your personal API key to make requests
4. Insert your telegram bot token and personal weather API token in the variables in **Tokens** file
5. Run the WeatherBotApp file
6. Find your bot in telegram using the username you gave it
7. Test the bot sending comands to it!
