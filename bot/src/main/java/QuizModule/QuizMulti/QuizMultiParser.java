package QuizModule.QuizMulti;

import QuizModule.QuizMulti.enums.QuizDifficulty;
import QuizModule.QuizMulti.enums.QuizType;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * QuizMultiParser is a JSON parser that retrieves question sheets from OpenTrivia DB
 * @author Carl Johan Helgstrand
 * @version 1.0
 */
public class QuizMultiParser {
    private String json = null;
    private JSONObject jQuiz = null;
    private int responseCode = -1;
    private LinkedList<QuestionMulti> parsedQuestions = new LinkedList<QuestionMulti>();



    public QuizMultiParser(String url){
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = client.execute(request);
            json = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
            jQuiz = new JSONObject(json);
            responseCode = jQuiz.getInt("response_code");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(json != null && responseCode == 0){
            try{
                JSONArray arr = jQuiz.getJSONArray("results");
                for(Object obj : arr){
                    if(obj != null) {
                        parsedQuestions.add(createQuestion(obj));
                    }
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * Returns A LinkedList with all questions gathered from the JSON string
     * @return LinkedList - filled with QuestionMulti objects
     */
    public LinkedList<QuestionMulti> getQuestions(){
        return parsedQuestions;
    }


    /**
     * Creates a new QuestionMulti object given the JSON information that we parsed into String
     * @param object Its an JSONObject, but the code that guarantee that for user hence Object
     * @return A Question - QuestionMulti Object
     */
    public QuestionMulti createQuestion(Object object) {
        QuestionMulti question = new QuestionMulti();

        if (object instanceof JSONObject) {
            JSONObject jObj = (JSONObject)object;
            JSONArray inCorrectAnswers = jObj.getJSONArray("incorrect_answers");
            ArrayList<String> alternatives = new ArrayList<String>();
            alternatives.add(fixFormat(jObj.getString("correct_answer")));
            for(Object o: inCorrectAnswers){
                if(o instanceof String){
                    String wrongAnswer = (String)o;
                    alternatives.add(fixFormat(wrongAnswer));
                }
            }


            question.setDifficulty(QuizDifficulty.valueOf(jObj.get("difficulty").toString().toUpperCase()));
            question.setQuestion(fixFormat(fixFormat(jObj.getString("question"))));
            question.setCorrectAnswer(fixFormat(jObj.getString("correct_answer")));
            question.setAlternatives(alternatives);
            question.setCategory(fixFormat(jObj.getString("category")));
            question.setType(QuizType.valueOf(jObj.get("type").toString().toUpperCase()));
        }

        return question;
    }


    /**
     * Fixing JSON formatting issues
     * @param unfixed An unfixed string
     * @return Fixed string
     */
    public String fixFormat(String unfixed){
        String fixed = unfixed.replace("&amp;", "&").replace("&quot;", "\"").replace("&#039;","'");
        return fixed;
    }
}
