package nl.tue.s2id90.group41;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class DamPun  extends DraughtsPlayer{
    private int bestValue=0;
    int maxSearchDepth;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public DamPun(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = maxSearchDepth;
    }
    
    @Override public Move getMove(DraughtsState s) {
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        try {
            // compute bestMove and bestValue in a call to alphabeta
            bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, maxSearchDepth);
            
            // store the bestMove found uptill now
            // NB this is not done in case of an AIStoppedException in alphaBeat()
            bestMove  = node.getBestMove();
            
            // print the results for debugging reasons
            System.err.format(
                "%s: depth= %2d, best move = %5s, value=%d\n", 
                this.getClass().getSimpleName(),maxSearchDepth, bestMove, bestValue
            );
        } catch (AIStoppedException ex) {  /* nothing to do */  }
        
        if (bestMove==null) {
            System.err.println("no valid move found!");
            return getRandomValidMove(s);
        } else {
            return bestMove;
        }
    } 

    /** This method's return value is displayed in the AICompetition GUI.
     * 
     * @return the value for the draughts state s as it is computed in a call to getMove(s). 
     */
    @Override public Integer getValue() { 
       return bestValue;
    }

    /** Tries to make alphabeta search stop. Search should be implemented such that it
     * throws an AIStoppedException when boolean stopped is set to true;
    **/
    @Override public void stop() {
       stopped = true; 
    }
    
    /** returns random valid move in state s, or null if no moves exist. */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty()? null : moves.get(0);
    }
    
    /** Implementation of alphabeta that automatically chooses the white player
     *  as maximizing player and the black player as minimizing player.
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     **/
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException
    {
        if (node.getState().isWhiteToMove()) {
            return alphaBetaMax(node, alpha, beta, depth);
        } else  {
            return alphaBetaMin(node, alpha, beta, depth);
        }
    }
    
    /** Does an alphabeta computation with the given alpha and beta
     * where the player that is to move in node is the minimizing player.
     * 
     * <p>Typical pieces of code used in this method are:
     *     <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     *          <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     *          <li><code>node.setBestMove(bestMove);</code></li>
     *          <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     *     </ul>
     * </p>
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth  maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been set to true.
     */
     int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        Move bestMove = null;
        int value = MAX_VALUE;
        
        if (depth == 0  || state.isEndState()){
            return evaluate(node.getState());
        }
        List<Move> moves = state.getMoves();
        for (Move move : moves){
            state.doMove(move);
            value = Math.min(value, alphaBeta(node, alpha, beta, depth - 1));
            state.undoMove(move);
            if (value < beta){
                bestMove = move;
                beta = value;
            }
            if (alpha >= beta){
                break;
            }
        }
        node.setBestMove(bestMove);
        return value;
     }
    
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; throw new AIStoppedException(); }
        DraughtsState state = node.getState();
        // ToDo: write an alphabeta search to compute bestMove and value
        Move bestMove = null;
        int value = MIN_VALUE;
        
        if (depth == 0 || state.isEndState()){
            return evaluate(node.getState());
        }
        List<Move> moves = state.getMoves();
        for (Move move : moves){
            state.doMove(move);
            value = Math.max(value, alphaBeta(node, alpha, beta, depth - 1));
            state.undoMove(move);
            if (value > alpha){
                bestMove = move;
                alpha = value;
            }
            if (alpha >= beta){
                break;
            }
        }
        node.setBestMove(bestMove);
        return value;
    }

    /** A method that evaluates the given state. */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) {
        int value = 0;
        int pieceValue = 10; //high values so that it's still the most
        int kingValue = 30;  //determining factor
        int[] pieces = state.getPieces();
        for (int i = 1; i < pieces.length; i++){
            switch (pieces[i]){
                case 1: value += pieceValue; //WHITEPIECE
                        value += position(true, i, false); //position evaluation
                        break;
                case 2: value -= pieceValue; //BLACKPIECE
                        value -= position(false, i, false); //position evaluation
                        break;
                case 3: value += kingValue; //WHITEKING
                        value += position(true, i, true); //position evaluation
                        break;
                case 4: value -= kingValue; //BLACKKING
                        value -= position(false, i, true); //position evaluation
                        break;
                default: break; //NO PIECE
            }
        }
        return value;
    }
    
    //gives an evaluation for the position of the piece, range (0-9);
    private int position(boolean white, int i, boolean king){
        int value = 0; //initial value is 0;
        int sidePieceReduction = 3;
        
        if (white){
            //king shouldn't have a tempo value
            if (!king){
                value += 9 - ((i - 1)/5); // tempo value
            }
        } else {
            //king shouldn't have a tempo value
            if (!king){
                value += (i - 1)/5; // tempo value
            }
        }
        
        //sidepieces are not preferable
        if (i % 5 == 0 || i % 5 == 1){
                value -= sidePieceReduction;
        }
        
        return value;
    }
}
