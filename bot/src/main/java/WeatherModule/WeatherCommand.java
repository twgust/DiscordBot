package WeatherModule;

import Commands.Command;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;


public class WeatherCommand extends Command{

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        String recievedMessage = event.getMessage().getContentRaw();

        OWM owm = new OWM("19c501207c33f1d716f086176d524036");
        //try catch som fångar API exception, beror förmodligen på fel stad namn.
        try {
            //nedan finns kod för att få max och min temperatur.
            //String tempMax = (int) Math.round(Objects.requireNonNull(cwm.getMainData().getTempMax() - 273.15)) + "°C";
            //String tempMin = (int) Math.round(Objects.requireNonNull(cwm.getMainData().getTempMin() - 273.15)) + "°C";
            String city = recievedMessage.substring(9);
            CurrentWeather cwm = owm.currentWeatherByCityName(city);
            String currentCondition = "";
            String currentConditionImg = "";
            String countryCode = "";
            String tempCurr = "";
            Instant time;
            ZonedDateTime zdt = null;
            ZoneId zoneId;
            String humidity = "";
            double windDirectionTemp = 0;
            String windDirection = "";
            String windSpeed = "";
            double cityLat = 0;
            double cityLng = 0;
            String emojiId = "";
            String emojiCode = "";
            String emoji = "";
            String timezone = "";
            DateTimeFormatter dtf = null;

            //följande try catcher finns för att enstaka variabler kan bli null.
            //Då skickar botten ut Invaliad city name som inte är sant

            try {
                currentCondition = cwm.getWeatherList().get(0).getDescription();
            }catch (NullPointerException e){
                currentCondition = "Could not be found.";
            }

            currentConditionImg = cwm.getWeatherList().get(0).getIconLink();
            char[] arr = currentCondition.toCharArray();
            arr[0] = Character.toUpperCase(arr[0]);
            currentCondition = new String(arr);

            try {
                countryCode = cwm.getSystemData().getCountryCode();
            }catch (NullPointerException e){
                countryCode = "FLAG 404";
            }

            try {
                tempCurr = (int) Math.round(cwm.getMainData().getTemp() - 273.15) + "°C "  + "(" + Math.round((cwm.getMainData().getTemp())*(1.8)-(459.67)) + "°F)";
            } catch (NullPointerException e) {
                tempCurr = "Temperature could not be found";
            }

            try {
                humidity = Double.toString(cwm.getMainData().getHumidity());
            }catch (NullPointerException e) {
                humidity = "Humidity could not be found";
            }


            try {
                windDirectionTemp = cwm.getWindData().getDegree();
                windDirection = calculateWindDirection(windDirectionTemp);
            }catch (NullPointerException e) {
                windDirection = "direction could not be found";
            }

            try {
                windSpeed = Double.toString(cwm.getWindData().getSpeed()) + " m/s "+ "(" + Double.toString(Math.round(cwm.getWindData().getSpeed()*2.23693629)) + " mph)";
            }catch (NullPointerException e) {
                windSpeed = "Speed could not be found";
            }

            emojiId = cwm.getWeatherList().get(0).getIconCode();

            try {
                emojiCode = Integer.toString(cwm.getWeatherList().get(0).getConditionId());
                emoji = getEmojiForCondition(emojiCode, emojiId);
            } catch (NullPointerException e) {
                emoji = "☁";
            }

            try {
                cityLat = cwm.getCoordData().getLatitude();
                cityLng = cwm.getCoordData().getLongitude();
            }catch (NullPointerException e) {
                cityLat = 0;
                cityLng = 0;
            }

            timezone = getTimeZone(cityLat, cityLng);
            time = Instant.now();
            zoneId = ZoneId.of(timezone);
            zdt = ZonedDateTime.ofInstant(time, zoneId);
            dtf = new DateTimeFormatterBuilder().appendPattern("HH:mm, EEEE dd-MM-yyyy").toFormatter(Locale.ENGLISH);
            System.out.println(cwm.getCoordData().getLatitude());
            System.out.println(cwm.getCoordData().getLongitude());
            System.out.println(timezone);


            EmbedBuilder weather =  new EmbedBuilder();
            weather.setColor(0x349CEE);
            weather.setTitle("Weather in " +cwm.getCityName() + " at " + zdt.format(dtf) + " " + countryCodeToEmoji(countryCode));
            weather.addField("🌡 Temperature", tempCurr , true);
            weather.addField(emoji + " Current condition", currentCondition , true);
            weather.setThumbnail( currentConditionImg);
            weather.setFooter("Powered by OpenWeatherMap.org","https://raw.githubusercontent.com/ioBroker/ioBroker.openweathermap/master/admin/openweathermap.png");
            TemporalAccessor currTime = OffsetDateTime.now();
            weather.setTimestamp(currTime);
            weather.addField("💨 Wind",windSpeed + " from the " + windDirection , false);
            weather.addField("💧 Humidity", humidity + "%" , false);


            event.getChannel().sendMessage(weather.build()).queue();
            city = ""; //anropas för att tomma strängen.

        }catch (APIException /*| NullPointerException */|  IndexOutOfBoundsException  e) {
           event.getChannel().sendMessage("Invalid city name or time format").queue();
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
        else if (iconCode.equals("801") && iconId.equals("02d")) {
            return fewclouds;
        }
        else if(iconCode.equals("801") && iconId.equals("02n")) {
            return scatterdclouds;
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

    public String getTimeZone(double lat, double lng){
        return  TimezoneMapper.latLngToTimezoneString(lat, lng);
    }
}
