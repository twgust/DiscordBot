package QuizModule.QuizSingle;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import java.io.IOException;

public class QuizSingleParser {
    private String url;
    private HttpClient client;


    //Constructor
    public QuizSingleParser(String url){
        client = HttpClientBuilder.create().build();
        this.url=url;
    }

    public QuestionSingle getQuestion(){
        return parse();
    }


    private QuestionSingle parse(){
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request); //Retrieves json-file containing questions, from Open Trivia DB
            String json = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
            if(json.contains("*")){
                return null;
            }
            JSONArray arr = (JSONArray) new JSONParser().parse(json);
            return createQuestion(arr.get(0));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Create a new Question object
    private QuestionSingle createQuestion(Object object) {
        if (object instanceof JSONObject) {
            JSONObject jObj = (JSONObject) object;
            QuestionSingle question = new QuestionSingle();
            question.setId(Long.parseLong(jObj.get("id").toString()));
            question.setQuestion(fixFormat((String)jObj.get("question")));
            question.setAnswer(fixFormat((String)jObj.get("answer")));
            return question;
        }
        else {
            return null;
        }
    }


    //Fixing formatting issues with json -> String and sets first letter to uppercase
    private String fixFormat(String unfixed){
        String fixed = unfixed.replace("&amp;", "&").replace("&quot;", "\"").replace("&#039;","'");
        fixed = Jsoup.parse(fixed).text();
        StringUtils.capitalize(fixed);
        return fixed;
    }
}
