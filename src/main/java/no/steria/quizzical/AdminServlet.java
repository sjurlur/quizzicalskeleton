package no.steria.quizzical;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class AdminServlet extends SecuredServlet {

	private Quiz quiz;
	private MongoQuizDao mongoQuizDao;
	private MongoUserDao mongoUserDao;
	private MongoResponseDao mongoResponseDao;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();		 
		JsonNode rootNode = mapper.readTree(req.getReader().readLine());
		
		int quizId = -1, userId = 0;
		String quizName = "", quizDesc = "", submitMsg = "";
		
		BasicDBList questions = new BasicDBList();
		
		Iterator<Entry<String,JsonNode>> allEntries = rootNode.getFields();
		
		while(allEntries.hasNext()){
			Entry<String, JsonNode> entry = allEntries.next();
			if(entry.getKey().equals("questions")){
				Iterator<JsonNode> questionEntries = entry.getValue().getElements();
				
				while(questionEntries.hasNext()){
					JsonNode question = questionEntries.next();
					Iterator<Entry<String, JsonNode>> questionFields = question.getFields();
					
					BasicDBObject questionObject = new BasicDBObject();
					
					int questionId = 0, correctAnswer = 0;
					String questionText = "";
					BasicDBList alternativesList = new BasicDBList();
					
					while(questionFields.hasNext()){
						Entry<String, JsonNode> questionField = questionFields.next();
						
						if (questionField.getKey().equals("id")){
							questionId = questionField.getValue().asInt();
						} else if (questionField.getKey().equals("text")){
							questionText = questionField.getValue().asText();
						} else if (questionField.getKey().equals("alternatives")){
							Iterator<JsonNode> alternatives = questionField.getValue().getElements();
							
							while(alternatives.hasNext()){
								JsonNode alternative = alternatives.next();
								Iterator<Entry<String, JsonNode>> alternativeField = alternative.getFields();
								
								int alternativeId = 0;
								String alternativeText = "";
								
								while(alternativeField.hasNext()){
									Entry<String, JsonNode> alternativeKeys = alternativeField.next();
									
									if(alternativeKeys.getKey().equals("aid")){
										alternativeId = alternativeKeys.getValue().asInt();
									} else if (alternativeKeys.getKey().equals("atext")){
										alternativeText = alternativeKeys.getValue().asText();
									}
									
								}
								BasicDBObject alternativeObject = new BasicDBObject();
								alternativeObject.append("aid", alternativeId);
								alternativeObject.append("atext", alternativeText);
								alternativesList.add(alternativeObject);
							}
						} else if (questionField.getKey().equals("answer")){
							correctAnswer = questionField.getValue().asInt();
						}
					}
					questionObject.append("id", questionId);
					questionObject.append("text", questionText);
					questionObject.append("alternatives", alternativesList);
					questionObject.append("answer", correctAnswer);
					questions.add(questionObject);
				}
				
			} else if(entry.getKey().equals("quizId")){
				quizId = entry.getValue().asInt();
			} else if(entry.getKey().equals("quizName")){
				quizName = entry.getValue().asText();
			} else if(entry.getKey().equals("quizDesc")){
				quizDesc = entry.getValue().asText();
			} else if(entry.getKey().equals("submitMsg")){
				submitMsg = entry.getValue().asText();
			} else if(entry.getKey().equals("userId")){
				userId = entry.getValue().asInt();
			}
		}
		quiz = new Quiz(quizId, quizName, quizDesc, submitMsg, questions);
		mongoQuizDao.insertQuizToDB(quiz, userId);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		
		ObjectMapper mapper = new ObjectMapper();
		PrintWriter writer = resp.getWriter();
		
		int mode = Integer.parseInt(req.getParameter("mode"));
		
		if(mode == 2){
			// Retrieves all quizzes by a userId
			int userId = Integer.parseInt(req.getParameter("userId"));
			ArrayList<Integer> usersQuizIds = mongoUserDao.getUser(userId).getQuizIds();
			ArrayList<Quiz> requestedQuizzes = new ArrayList<Quiz>();
			for(Integer quizId : usersQuizIds){
				Quiz quiz = mongoQuizDao.getQuiz(quizId);
				quiz.setResponses(mongoResponseDao.countResponsesForQuiz(quizId));
				requestedQuizzes.add(quiz);
			}			
			mapper.writeValue(writer, requestedQuizzes);
			
		}else if(mode == 3){
			// Checks current number of responses
			int quizId = Integer.parseInt(req.getParameter("quizId"));
			mapper.writeValue(writer, mongoResponseDao.countResponsesForQuiz(quizId));
			resp.setContentType("text/json");
		}else if(mode == 4){
			// Picks a random winner of a quiz
			int quizId = Integer.parseInt(req.getParameter("quizId"));
			mapper.writeValue(writer, mongoResponseDao.drawRandomWinner(quizId));
			resp.setContentType("text/json");
		}else if(mode == 5){
			// Delete a quiz
			int quizId = Integer.parseInt(req.getParameter("quizId"));
			mongoQuizDao.remove(quizId);
		}else if(mode == 6){
			// Change active status of quiz
			int quizId = Integer.parseInt(req.getParameter("quizId"));
			int userId = Integer.parseInt(req.getParameter("userId"));
			Boolean active = Boolean.parseBoolean(req.getParameter("active"));
			Quiz quiz = mongoQuizDao.getQuiz(quizId);
			quiz.setActive(active);
			mongoQuizDao.insertQuizToDB(quiz, userId);
		}else if(mode == 7){
			// Retrieves a list of respondents
			int quizId = Integer.parseInt(req.getParameter("quizId"));
			mapper.writeValue(writer, mongoResponseDao.getRespondents(quizId));
			resp.setContentType("text/json");
		}
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		mongoQuizDao = new MongoQuizDao();
		mongoUserDao = new MongoUserDao();
		mongoResponseDao = new MongoResponseDao();
	}

}
