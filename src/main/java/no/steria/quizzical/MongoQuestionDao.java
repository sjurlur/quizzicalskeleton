package no.steria.quizzical;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoQuestionDao implements QuestionDao {
	
	
	private DB db;
	
	public MongoQuestionDao() {
		try {
			MongoClient client = new MongoClient();
			db = client.getDB("quizzical");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Question> getQuestions() {
		DBCollection collection = db.getCollection("questions");
		DBCursor cursor = collection.find();
		ArrayList<Question> questions = new ArrayList<Question>();
		while(cursor.hasNext()){
			DBObject next = cursor.next();
			Integer id = (Integer) next.get("id");
			String text = (String) next.get("text");
			BasicDBList alternatives = (BasicDBList) next.get("alternatives");
			Integer answer = (Integer) next.get("answer");
			Question question = new Question(id,text,alternatives,answer);
			questions.add(question);
		}
		return questions;
	}

}
