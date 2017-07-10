package ModelPackeg;



import java.util.List;
import boot.SokobanSolver;
import commons.Level2D;
import model.db.iDBManager;
import searcable.TwoPointAdaptter;
import serchAlgo.Action;
import serchAlgo.BFS;
import serchAlgo.iSearcher;
/**
 * 
 * @see solve(Level2D level) return the sulosion in string
 * @see add(Object o) add to DB
 * @see update(Object o) update in DB
 * @see selectScore(String query) return result list<object> 
 * */
public class MyModel implements iModel {

	private iDBManager DBManager;
	
	public MyModel(iDBManager DBManager) {
		this.DBManager=DBManager;
	}
	/**
	 * solve(Level2D level) return the solution in this format "u,d,l,r" and if dont have a solution return null
	 * */
	@Override
	public String solve(Level2D level) {
		if (level != null) {
			SokobanSolver s = new SokobanSolver();
			iSearcher<TwoPointAdaptter> boxToTarget = new BFS<TwoPointAdaptter>();
			iSearcher<TwoPointAdaptter> playerToBox = new BFS<TwoPointAdaptter>();
			List<Action> list = s.solveLevel(level, boxToTarget, playerToBox);
			if (list != null) {
				//resultBuilder in this format r,l,d,u,r...
				StringBuilder bildResult=new StringBuilder();
				for (Action action : list) {
					//like move[0]="Move" move[1]="up"
					String[] move= action.getName().split(" ");
						//add like "u,","d,"
						bildResult.append(move[1].charAt(0)+ ",");
					}
				//remove lest comma
				bildResult.delete(bildResult.length()-1,bildResult.length());
				return bildResult.toString();
				}
			}
		//d'ont have a solution
		return null;
		}
	@Override
	public void add(Object o) throws Exception {
		this.DBManager.add(o);
	}
	@Override
	public void update(Object o) throws Exception {
		this.DBManager.update(o);
	}
	@Override
	public List selectScore(String query) {
		return this.DBManager.selectScore(query);
	}
	

	

}
