package QuizModule;

import QuizModule.enums.QuizDifficulty;
import QuizModule.enums.QuizType;
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

public class QuizParser {
    private String json = null;
    private JSONObject jQuiz = null;
    private int responseCode = -1;
    private ArrayList<Question> parsedQuestions = new ArrayList<Question>();

    public QuizParser(String url){
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = client.execute(request);
            json = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
            //json = json.replace("&quot;","\""); Not working for the moment
            jQuiz = new JSONObject(json);
            responseCode = jQuiz.getInt("response_code");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Code 0: Success Returned results successfully.
        if(json != null && responseCode == 0){
            System.out.println("Code " + responseCode + ": Success Returned results successfully."); //Temp code
                try{
                   JSONArray arr = jQuiz.getJSONArray("results"); //Getting the JSONArray containing each JSONObject of questions
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


    public ArrayList<Question> getQuestions(){
        return parsedQuestions;
    }

    public Question createQuestion(Object o) {
        Question question = new Question();

        if (o instanceof JSONObject) {
            JSONObject jObj = (JSONObject)o;
            question.setDifficulty(QuizDifficulty.valueOf(jObj.get("difficulty").toString().toUpperCase()));
            question.setQuestion(jObj.getString("question"));
            question.setCorrectAnswer(jObj.getBoolean("correct_answer"));
            question.setCategory(jObj.getString("category"));
            question.setType(QuizType.valueOf(jObj.get("type").toString().toUpperCase()));
        }

        return question;
    }
}
