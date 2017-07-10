package ModelPackeg;

import java.util.List;

import commons.Level2D;
/**
 * @see solve(Level2D level) return the sulosion to sokoban
 * @see add(Object o) add to DB
 * @see update(Object o) update in DB
 * @see selectScore(String query) select from DB
 * */
public interface iModel {
	/**solve(Level2D level) return the solution to sokoban in string and if d'ont have a solution return null*/
	String solve(Level2D level);
	/** add(Object o) add to data base throw if 'o' Already exists*/
	public void add(Object o) throws Exception;
	/** update(Object o) update in DB throw if 'o' not exists*/
	public void update(Object o) throws Exception;
	/** selectScore(String query) select from the DB  return result list<object>*/
	public List selectScore(String query);
}
