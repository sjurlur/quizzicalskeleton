package no.steria.quizzical;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoUserDao{
			
	private DB db;
	private DBCollection collection;
	
	public MongoUserDao() {
		try {
			MongoClient client = new MongoClient();
			db = client.getDB("quizzical");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		collection = db.getCollection("users");
	}
		
	public User getUser(int userId){
		User user = null;
		DBCursor cursor = collection.find(new BasicDBObject("userId",userId));
		while(cursor.hasNext()){
			DBObject document = cursor.next();
			String username = (String) document.get("username");
			String password = (String) document.get("password");			
			@SuppressWarnings("unchecked")
			ArrayList<Integer> quizzes = (ArrayList<Integer>) document.get("quizzes");
			user = new User(userId, username, password, quizzes);
		}
		return user;
	}

	public void addQuizIdToUser(int quizId, int userId) {
		DBObject document = collection.findOne(new BasicDBObject("userId",userId));
		@SuppressWarnings("unchecked")
		ArrayList<Integer> quizzes = (ArrayList<Integer>) document.get("quizzes");
		if(!quizzes.contains(quizId)){
			collection.remove(document);
			quizzes.add(quizId);
			document.put("quizzes", quizzes);
			collection.insert(document);
		}
	}

	public void removeQuizIdFromUsers(int quizId){
		DBCursor cursor = collection.find();
		while(cursor.hasNext()){
			DBObject document = cursor.next();	
			@SuppressWarnings("unchecked")
			ArrayList<Integer> quizzes = (ArrayList<Integer>) document.get("quizzes");
			if(quizzes.contains(quizId)){
				collection.remove(document);
				quizzes.remove((Object)quizId);
				document.put("quizzes", quizzes);
				collection.insert(document);
			}
		}
	}
	
}
