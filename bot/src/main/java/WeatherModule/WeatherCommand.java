package WeatherModule;

import Commands.Command;
import net.aksingh.owmjapis.api.APIException;
import net.aksingh.owmjapis.core.OWM;
import net.aksingh.owmjapis.model.CurrentWeather;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.webbitserver.HttpHandler;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


public class WeatherCommand extends Command{
    private boolean loaded = false;
    private String[] weatherInfo;
    private EmbedBuilder eb = new EmbedBuilder();

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        WeatherSQL sql = new WeatherSQL();
        setLoaded(false);
        String[] recievedMessageArr = event.getMessage().getContentRaw().split(" ");
        //System.out.println(Arrays.toString(recievedMessageArr));
        String city = "";

        if(event.getMessage().getContentRaw().split(" ").length == 3){
            if(recievedMessageArr[1].equalsIgnoreCase("set")){
                city = event.getMessage().getContentRaw().substring(13);
                city = city.replace(" ", "%20");
                sql.setCity(event.getAuthor().getId(), city);
                System.out.println("beep");
            }
            else {
                city = event.getMessage().getContentRaw().substring(13);
                city = city.replace(" ", "%20");
                System.out.println("beep else");
            }
        }
        else if(recievedMessageArr.length == 2){
            if(recievedMessageArr[1].equalsIgnoreCase("linked")){
                if(sql.checkQuery(event.getAuthor().getId())){
                    city = sql.getCity(event.getAuthor().getId());
                }
            }
            else {
                city = event.getMessage().getContentRaw().substring(9);
                city = city.replace(" ", "%20");
                System.out.println("beep else2");
            }
        }
        else{
            city = event.getMessage().getContentRaw().substring(9);
            city = city.replace(" ", "%20");
            System.out.println("beep else2");
        }

        /*
        else if(sql.checkQuery(event.getAuthor().getId())){
            city = sql.getCity(event.getAuthor().getId());
        }

         */





        /*
        if(recievedMessageArr.length == 3){
            city = recievedMessageArr[1] + recievedMessageArr[2];
        }

         */
        if(!city.equalsIgnoreCase("")) {
            connectToOWM(city);
            if (isLoaded()) {
                sql.closeConnection();
                EmbedBuilder weather = new EmbedBuilder();
                weather.setColor(0x349CEE);
                weather.setTitle("Weather in " + weatherInfo[10] + " at " + weatherInfo[5] + " " + weatherInfo[2]);
                weather.addField("🌡 Temperature", "Current: " + weatherInfo[3] + "\n Feels like: " + weatherInfo[4], true);
                weather.addField(weatherInfo[9] + " Current condition", weatherInfo[0], true);
                weather.setThumbnail(weatherInfo[1]);
                weather.setFooter("Powered by OpenWeatherMap.org", "https://raw.githubusercontent.com/ioBroker/ioBroker.openweathermap/master/admin/openweathermap.png");
                TemporalAccessor currTime = OffsetDateTime.now();
                weather.setTimestamp(currTime);
                weather.addField("💨 Wind", weatherInfo[8] + " from the " + weatherInfo[7], false);
                weather.addField("💧 Humidity", weatherInfo[6] + "%", false);

                event.getChannel().sendMessage(weather.build()).queue();


            } else {
                sql.closeConnection();
                event.getChannel().sendMessage("Invalid city name or time format").queue();
            }
        } else{
            sql.closeConnection();
            event.getChannel().sendMessage("No city linked to your account").queue();
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

    public void connectToOWM(String city){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=19c501207c33f1d716f086176d524036")).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::parse)
                    .join();
        }catch (Exception e){
            setLoaded(false);
            e.printStackTrace();
        }
    }
    public void parse(String responsebody){
        try {
            JSONObject object = new JSONObject(responsebody);
            JSONArray weatherArr = object.getJSONArray("weather");
            JSONObject weatherObj = weatherArr.getJSONObject(0);
            JSONObject sysObj = object.getJSONObject("sys");
            JSONObject mainObj = object.getJSONObject("main");
            JSONObject windObj = object.getJSONObject("wind");

            String currentCondition = weatherObj.getString("description");
            char[] arr = currentCondition.toCharArray();
            arr[0] = Character.toUpperCase(arr[0]);
            currentCondition = new String(arr);
            String icon = weatherObj.getString("icon");

            String currentConditionImg = "http://openweathermap.org/img/w/"+icon+".png";

            String countryCode = countryCodeToEmoji(sysObj.getString("country"));

            double tempCurr = mainObj.getDouble("temp");
            String tempCurrStr = (int) Math.round(tempCurr - 273.15) + "°C "  + "(" + Math.round((tempCurr)*(1.8)-(459.67)) + "°F)";

            double tempFeel = mainObj.getDouble("feels_like");
            String tempFeelStr = (int) Math.round(tempFeel - 273.15) + "°C "  + "(" + Math.round((tempFeel)*(1.8)-(459.67)) + "°F)";

            Instant time = Instant.now().plusMillis(object.getLong("timezone")*1000);
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("HH:mm, EEEE dd-MM-yyyy").toFormatter(Locale.ENGLISH).withZone(ZoneId.of("UTC"));
            String timeStr = dtf.format(time);

            int humidity = mainObj.getInt("humidity");

            double windDirection = windObj.getDouble("deg");
            String windDirectionStr = calculateWindDirection(windDirection);

            double windSpeed = windObj.getDouble("speed");
            String windspeedStr = Double.toString(windSpeed) + " m/s "+ "(" + Double.toString(Math.round(windSpeed*2.23693629)) + " mph)";

            String id = Integer.toString(weatherObj.getInt("id"));

            String emoji = getEmojiForCondition(id, icon);

            String city = object.getString("name");

            /*
            System.out.println(currentCondition);
            System.out.println(currentConditionImg);
            System.out.println(countryCode);
            System.out.println(tempCurrStr);
            System.out.println(tempFeelStr);
            System.out.println(timeStr);
            System.out.println(humidity);
            System.out.println(windDirectionStr);
            System.out.println(windspeedStr);
            System.out.println(emoji);
            System.out.println(city);

             */
            String[] result = new String[11];
            result[0] = currentCondition;
            result[1] = currentConditionImg;
            result[2] = countryCode;
            result[3] = tempCurrStr;
            result[4] = tempFeelStr;
            result[5] = timeStr;
            result[6] = Integer.toString(humidity);
            result[7] = windDirectionStr;
            result[8] = windspeedStr;
            result[9] = emoji;
            result[10] = city;
            weatherInfo = result;
            setLoaded(true);
        }catch (Exception e){
            setLoaded(false);
            e.printStackTrace();
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }


    @Override
    public EmbedBuilder getHelp() {
        eb.clear();
        eb.setTitle("Quiz Module ☁", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/WeatherModule");
        eb.setDescription("Shows the weather!");
        eb.addField("weather [city]>", "- Shows the current weather reported at given city", false);
        eb.addField("weather [city, countrycode]>", "- Shows the current weather reported at given city, \n with specification to which country the city belongs to", false);
        eb.setFooter("DM Robic#2351 if you have suggestions");
        eb.setColor(Color.WHITE);
        return eb;
    }
}
