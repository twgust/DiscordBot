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

/**
 * QuizSingleParser is a JSON parser that retrieves question sheets from Open Trivia DB
 * @author Carl Johan Helgstrand
 * @version 1.0
 */
public class QuizSingleParser {
    private String url;
    private HttpClient client;



    public QuizSingleParser(String url){
        client = HttpClientBuilder.create().build();
        this.url=url;
    }

    /**
     * Returns a question that has been parsed
     * @return Question - QuestionSingle
     */
    public QuestionSingle getQuestion(){
        return parse();
    }

    /**
     * Calls the API, which generates a new random question, that question is then retrieved and parsed into a string
     * @return A Question - QuestionSingle Object
     */
    private QuestionSingle parse(){
        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
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


    /**
     * Fixing JSON formatting issues
     * @param unfixed An unfixed string
     * @return Fixed string
     */
    private String fixFormat(String unfixed){
        String fixed = unfixed.replace("&amp;", "&").replace("&quot;", "\"").replace("&#039;","'");
        fixed = Jsoup.parse(fixed).text();
        StringUtils.capitalize(fixed);
        return fixed;
    }
}
