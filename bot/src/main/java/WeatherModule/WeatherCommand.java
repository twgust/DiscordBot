package WeatherModule;

import Commands.Command;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;



public class WeatherCommand extends Command{

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String recievedMessage = event.getMessage().getContentRaw();
        String city = recievedMessage.substring(9);


        OWM owm = new OWM("19c501207c33f1d716f086176d524036");
        //try catch som fångar API exception, beror förmodligen på fel stad namn.
        try {
            //nedan finns kod för att få max och min temperatur.

            //String tempMax = (int) Math.round(Objects.requireNonNull(cwm.getMainData().getTempMax() - 273.15)) + "°C";
            //String tempMin = (int) Math.round(Objects.requireNonNull(cwm.getMainData().getTempMin() - 273.15)) + "°C";
            CurrentWeather cwm = owm.currentWeatherByCityName(city);
            String currentCondition = "";
            String currentConditionImg = "";
            String countryCode = "";
            String tempCurr = "";
            Date time = null;
            String humidity = "";
            double windDirectionTemp = 0;
            String windDirection = "";
            String windSpeed = "";
            String emojiId = "";
            String emojiCode = "";
            String emoji = "";

            //try catch som försöker instansiera alla variabler med data, vissa kan ej nullpointerexception.
            try {
                currentCondition = cwm.getWeatherList().get(0).getDescription();
                currentConditionImg = cwm.getWeatherList().get(0).getIconLink();
                countryCode = cwm.getSystemData().getCountryCode();
                tempCurr = (int) Math.round(cwm.getMainData().getTemp() - 273.15) + "°C";
                time = cwm.getDateTime();
                humidity = Double.toString(cwm.getMainData().getHumidity());
                windDirectionTemp = cwm.getWindData().getDegree();
                windDirection = calculateWindDirection(windDirectionTemp);windSpeed = Double.toString(cwm.getWindData().getSpeed());
                emojiId = cwm.getWeatherList().get(0).getIconCode();
                emojiCode = Integer.toString(cwm.getWeatherList().get(0).getConditionId());
                emoji = getEmojiForCondition(emojiCode, emojiId);
            }catch (NullPointerException e){
                event.getChannel().sendMessage("Something wrong with input or API, try again please").queue();
            }

            EmbedBuilder weather =  new EmbedBuilder();
            weather.setColor(0x349CEE);
            weather.setTitle("Weather in " +cwm.getCityName() + " at " + time + " " + countryCodeToEmoji(countryCode));
            weather.addField("🌡 Temperature", tempCurr , true);
            weather.addField(emoji + " Current condition", currentCondition , true);
            weather.setThumbnail( currentConditionImg);
            weather.setFooter("Powered by OpenWeatherMap.org","https://raw.githubusercontent.com/ioBroker/ioBroker.openweathermap/master/admin/openweathermap.png");
            TemporalAccessor currTime = OffsetDateTime.now();
            weather.setTimestamp(currTime);
            weather.addField("💨 Wind",windSpeed + " m/s" + " from the " + windDirection , false);
            weather.addField("💧 Humidity", humidity + "%" , false);


            event.getChannel().sendMessage(weather.build()).queue();

        }catch (APIException e) {
           event.getChannel().sendMessage("Invalid city name").queue();
        }

    }

    public String countryCodeToEmoji(String code){
        int offsetCountryCode = 127397;
        if (code == null || code.length()!=2 ) {
            return "";
        }

        if (code.equalsIgnoreCase("uk")) {
            code = "gb";
        }

        code = code.toUpperCase();

        StringBuilder emojiString = new StringBuilder();

        for(int i = 0; i < code.length(); i++) {
            emojiString.appendCodePoint(code.charAt(i) + offsetCountryCode);
        }

        return emojiString.toString();
    }

    public String calculateWindDirection(Double windDegree) {
        if (windDegree >= 335.01 || windDegree <= 25.00){
            return "north";
        }
        else if (windDegree >= 25.01 && windDegree<= 65.00){
            return "north east";
        }
        else if (windDegree >= 65.01 && windDegree <= 115.00) {
            return "east";
        }
        else if (windDegree >= 115.01 && windDegree <= 155.00) {
            return "south east";
        }
        else if (windDegree >= 155.01 && windDegree <= 205.00) {
            return "south";
        }
        else if (windDegree >= 205.01 && windDegree <= 245.00 ) {
            return "south west";
        }
        else if (windDegree >= 245.01 && windDegree <= 295.00) {
            return "west";
        }
        else if (windDegree >= 295.01 && windDegree <= 335.00) {
            return "north west";
        }
        else return "unkown direction";
    }

    public String getEmojiForCondition(String iconCode, String iconId) {

        String clearskyday= "☀";
        String fewclouds = "⛅";
        String scatterdclouds = "☁";
        String brokenclouds = "☁";
        String rain = "🌧";
        String thunderstorm = "🌩";
        String lightsnow = "❄";
        String mist = "🌫";
        String clearskynight = "🌑";

        if(iconCode.charAt(0) == 2 || iconId.equals("11d")) {
            return thunderstorm;
        }
        else if (iconCode.charAt(0) == '3' || iconId.equals("09d")) {
            return rain;
        }
        else if (iconCode.charAt(0) == '5' || iconId.equals("10d")) {
            return rain;
        }
        else if (iconCode.charAt(0) == '6' || iconId.equals("13d") || iconId.equals("13n")) {
            return lightsnow;
        }
        else if (iconCode.charAt(0) == '7' || iconId.equals("50d")){
            return mist;
        }
        else if (iconCode.charAt(0) == '8' & iconId.equals("01d")) {
            return clearskyday;
        }
        else if (iconCode.charAt(0) == '8' & iconId.equals("01n")) {
            return clearskynight;
        }
        else if (iconCode.equals("800")) {
            return clearskyday;
        }
        else if (iconCode.equals("801") || iconId.equals("02d") || iconId.equals("02n")) {
            return fewclouds;
        }
        else if (iconCode.equals("802") || iconId.equals("03d") || iconId.equals("03n")) {
            return scatterdclouds;
        }
        else if (iconCode.equals("803") || iconId.equals("04d") || iconId.equals("04n") ) {
            return brokenclouds;
        }
        else if (iconCode.equals("804")) {
            return brokenclouds;
        }
        else return brokenclouds;
    }


}
