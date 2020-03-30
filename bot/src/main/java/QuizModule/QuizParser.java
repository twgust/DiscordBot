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

    //Constructor
    public QuizParser(String url){
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        try {
            HttpResponse response = client.execute(request); //Retrieves json-file containing questions, from Open Trivia DB
            json = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
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

    //Return an ArrayList containing all the Question objects created from parsing the quiz database's json-file
    public ArrayList<Question> getQuestions(){
        return parsedQuestions;
    }

    //Create a new Question object
    public Question createQuestion(Object object) {
        Question question = new Question();

        if (object instanceof JSONObject) {
            JSONObject jObj = (JSONObject)object;
            JSONArray inCorrectAnswers = jObj.getJSONArray("incorrect_answers"); //Create an JSONArray that contains string values of the incorrect answers
            ArrayList<String> alternatives = new ArrayList<String>(); //ArrayList containing all alternatives
            alternatives.add(fixFormat(jObj.getString("correct_answer"))); //Adding the correct answer to the alternatives
            for(Object o: inCorrectAnswers){ //Adding all the incorrect answers to the alternatives
                if(o instanceof String){
                    String wrongAnswer = (String)o;
                    alternatives.add(fixFormat(wrongAnswer));
                }
            }

            //Adding all the data to the Question object
            question.setDifficulty(QuizDifficulty.valueOf(jObj.get("difficulty").toString().toUpperCase()));
            question.setQuestion(fixFormat(fixFormat(jObj.getString("question"))));
            question.setCorrectAnswer(fixFormat(jObj.getString("correct_answer")));
            question.setAlternatives(alternatives);
            question.setCategory(fixFormat(jObj.getString("category")));
            question.setType(QuizType.valueOf(jObj.get("type").toString().toUpperCase()));
        }

        return question;
    }

    //Fixing formatting issues with json -> String
    public String fixFormat(String unfixed){
        String fixed = unfixed.replace("&amp;", "&").replace("&quot;", "\"").replace("&#039;","'");
        return fixed;
    }
}
