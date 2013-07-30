package no.steria.quizzical;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDemo {
	
	private static DB db;
	private static DBCollection quizzesToDB;
	private static DBCollection usersInDB;
	
	public static void main(String[] args) {
		insertQuizzesIntoDB(createQuizData());
		insertTestUsersIntoDB();
	}
	
	public static void insertTestQuizzes(){
		insertQuizzesIntoDB(createQuizData());
	}
		
	private static void init(){
		MongoClient client = null;
		try {
			client = new MongoClient();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		db = client.getDB("quizzical");		
	}

	private static List<Quiz> createQuizData(){
		List<Quiz> quizzes = new ArrayList<Quiz>();
		quizzes.add(testQuiz1());
		quizzes.add(testQuiz2());
		return quizzes;
	}
	
	private static void insertQuizzesIntoDB(List<Quiz> quizzes){
		init();
		quizzesToDB = db.getCollection("quizzes");
		quizzesToDB.drop();		
		for(Quiz quizData : quizzes){
			BasicDBObject quizToDB = new BasicDBObject();
			quizToDB.put("quizId", quizData.getQuizId());
			quizToDB.put("name", quizData.getQuizName());
			quizToDB.put("desc", quizData.getQuizDesc());
			quizToDB.put("submitMsg", quizData.getSubmitMsg());
			BasicDBList questionsToDB = new BasicDBList();
			for(Question questionData : quizData.getQuestions()){
				BasicDBObject questionToDB = new BasicDBObject();
				questionToDB.put("id", questionData.getId());
				questionToDB.put("text", questionData.getText());
				questionToDB.put("answer", questionData.getAnswer());
				BasicDBList alternativesToDB = new BasicDBList();
				for(Alternative alternativeData : questionData.getAlternatives()){
					alternativesToDB.add(new BasicDBObject().append("aid", alternativeData.getAid()).append("atext", alternativeData.getAtext()));
				}
				questionToDB.put("alternatives", alternativesToDB);
				questionsToDB.add(questionToDB);
			}
			quizToDB.put("questions", questionsToDB);
			quizToDB.put("active", quizData.getActive());
			quizzesToDB.insert(quizToDB);
		}
	}
	
	
	public static Quiz getQuizHelper(int quizId) {
		DBObject quizObject = quizzesToDB.findOne(new BasicDBObject("quizId", quizId));
		
		String quizName = (String) quizObject.get("name");
		String quizDesc = (String) quizObject.get("desc");
		String submitMsg = (String) quizObject.get("submitMsg");
		@SuppressWarnings("unchecked")
		List<Question> questions = (List<Question>) quizObject.get("questions");
		boolean active = (boolean) quizObject.get("active");
		
		Quiz quiz = new Quiz(quizId, quizName, quizDesc, submitMsg, questions, active);
		return quiz;
	}
	
	
	private static void insertTestUsersIntoDB(){
		init();
		usersInDB = db.getCollection("users");
		usersInDB.drop();
		
		int userId1=1;
		String username1="martin", password1="eple";
		ArrayList<Integer> quizzes1 = new ArrayList<Integer>();
		quizzes1.add(1);
		quizzes1.add(2);		

		BasicDBObject user1 = new BasicDBObject();
		user1.put("userId", userId1);
		user1.put("username", username1);
		user1.put("password", password1);
		user1.put("quizzes", quizzes1);
		usersInDB.insert(user1);

		int userId2=2;
		String username2="nikolai", password2="sopp";
		ArrayList<Integer> quizzes2 = new ArrayList<Integer>();
		quizzes2.add(3);
		quizzes2.add(4);		

		BasicDBObject user2 = new BasicDBObject();
		user2.put("userId", userId2);
		user2.put("username", username2);
		user2.put("password", password2);
		user2.put("quizzes", quizzes2);
		usersInDB.insert(user2);
		
		int userId3=3;
		String username3="andy", password3="sitron";
		ArrayList<Integer> quizzes3 = new ArrayList<Integer>();
		quizzes3.add(1);
		quizzes3.add(5);		

		BasicDBObject user3 = new BasicDBObject();
		user3.put("userId", userId3);
		user3.put("username", username3);
		user3.put("password", password3);
		user3.put("quizzes", quizzes3);
		usersInDB.insert(user3);
		
	}
		
	public static Quiz testQuiz1(){
		List<Question> questions = new ArrayList<Question>();
		
		List<Alternative> alternatives1 = new ArrayList<Alternative>();
		alternatives1.add(new Alternative(1, "Oslo"));
		alternatives1.add(new Alternative(2, "Bergen"));
		alternatives1.add(new Alternative(3, "Trondheim"));
		alternatives1.add(new Alternative(4, "Kristiansand"));
		questions.add(new Question(1, "What is the capital of Norway?", alternatives1, 1));
		
		List<Alternative> alternatives2 = new ArrayList<Alternative>();
		alternatives2.add(new Alternative(1, "Sognsvann"));
		alternatives2.add(new Alternative(2, "Tyrifjorden"));
		alternatives2.add(new Alternative(3, "Mjosa"));
		alternatives2.add(new Alternative(4, "Burudvann"));
		questions.add(new Question(2, "What is the largest lake in Norway?", alternatives2, 3));		
		
		return new Quiz(1,"Geography Quiz","This is a quiz about Norwegian geography. The questions span from fantastic cities to amazing lakes.", "Thank you for taking the quiz. The winner will be announced on 2. august at 4 PM.", questions, true);	
	}
	
	private static Quiz testQuiz2(){
		List<Question> questions = new ArrayList<Question>();
		
		List<Alternative> alternatives1 = new ArrayList<Alternative>();
		alternatives1.add(new Alternative(1, "Oslo"));
		alternatives1.add(new Alternative(2, "Bergen"));
		alternatives1.add(new Alternative(3, "Trondheim"));
		alternatives1.add(new Alternative(4, "Kristiansand"));
		questions.add(new Question(1, "What is the capital of Norway?", alternatives1, 1));
		
		List<Alternative> alternatives2 = new ArrayList<Alternative>();
		alternatives2.add(new Alternative(1, "Sognsvann"));
		alternatives2.add(new Alternative(2, "Tyrifjorden"));
		alternatives2.add(new Alternative(3, "Mjosa"));
		alternatives2.add(new Alternative(4, "Burudvann"));
		questions.add(new Question(2, "What is the largest lake in Norway?", alternatives2, 3));		

		return new Quiz(2,"Science Quiz For Kids","This quiz contains question relevant for children with interest in science. Use this quiz vicely as kids can be extremely interested in science, and possibly learn too much.","Thank you for taking the quiz. The winner will be announced on 2. august at 4 PM year 2017", questions, true);	
	}
}