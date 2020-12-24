/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman.controllers.examples;
import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Game;

import static pacman.game.Constants.*;
/**
 *
 * @author ultra
 */
public class MyRuleBasedPacMan extends Controller<MOVE> {
    private static int MIN_DISTANCE=15;	//if a ghost is this close, run away
    public static int observe_range=50;
    public MOVE getMove(Game game,long timeDue)
	{
            int current=game.getPacmanCurrentNodeIndex();
            
            //Strategy 1: go after the pills and power pills
            int[] pills=game.getPillIndices();
            int[] powerPills=game.getPowerPillIndices();
            ArrayList<Integer> targets=new ArrayList<Integer>();
            
            for(int i=0;i<pills.length;i++)	//check which pills are available			
                if(game.isPillStillAvailable(i))
                    targets.add(pills[i]);
            
            for(int i=0;i<powerPills.length;i++)	//check with power pills are available
		if(game.isPowerPillStillAvailable(i))
                    targets.add(powerPills[i]);
            
            int[] targetsArray=new int[targets.size()];	//convert from ArrayList to array    
            for(int i=0;i<targetsArray.length;i++)
			targetsArray[i]=targets.get(i);
            
            
            
            ArrayList<GHOST> ghost_list=new ArrayList<GHOST>();
            int nearest_ghost_value = 10000;
            GHOST nearest_ghost ;
            for(GHOST ghost : GHOST.values())
                if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
                    if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<observe_range){
                        ghost_list.add(ghost);
                        if (ghost_list.size()==1){
                            MIN_DISTANCE=15;
                        }else if(ghost_list.size()==2){
                            MIN_DISTANCE=30;
                        }else if(ghost_list.size()==3){
                            MIN_DISTANCE=50;
                        }else{
                            MIN_DISTANCE=60;
                        }
                        if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost)) < nearest_ghost_value){
                            nearest_ghost=ghost;
                            nearest_ghost_value=game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost));
                   //         System.out.println("Ghost in observe are "+ghost_list.size()+" Ghosts, shortest distance="+nearest_ghost_value+" .");
                            
                            
                                                    //decider
                            if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(nearest_ghost))<MIN_DISTANCE){
                    //            System.out.println("Ghost in observe are "+ghost_list.size()+" Ghosts, shortest distance="+nearest_ghost_value+" ."+"TRIGGER shortest");
                                return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(nearest_ghost),DM.PATH);
                            
                        }
                        }
                        

                        
			}
            
            
            
            
            
            
            
            
            
            
            
            
            
            //no Strategy: go after the pills and power pills
            //return the next direction once the closest target has been identified
            //continue eat pill
            return game.getNextMoveTowardsTarget(current,game.getClosestNodeIndexFromNodeIndex(current,targetsArray,DM.PATH),DM.PATH);
            
        
        
        
        
        
        
        
        
        
        
        
        
        }
    
}
