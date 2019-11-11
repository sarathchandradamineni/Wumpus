package wumpusworld;

/**
 * Contains starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    RoomLinkedList stench_top = null,visited_rooms = null;
    RoomLinkedList path_a = null,path_b = null;
    RoomLinkedList breezes = null,destination_list=null,pit_list = null;
    int unvisited_rooms_path_a = 0,visited_rooms_path_a = 0,unvisited_rooms_path_b = 0,visited_rooms_path_b;
    int single_stench_back_track = 0;
    int breeze_back_track = 0;
    int safe_room_x = -1,safe_room_y = -1;
    Rooms roomobj;

    
    class RoomLinkedList
    {
        RoomLinkedList up;
        int cX;
        int cY;
        RoomLinkedList down; 
        
        public RoomLinkedList(int cX,int cY)
        {
            up = null;
            down = null;
            this.cX = cX;
            this.cY = cY;
        }
    }
    
    private World w;
    
    int rnd;
    
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;   
       roomobj = new Rooms(w);
    }
   
            
    /**
     * Asks your solver agent to execute an action.
     */

    public void doAction()
    {
        path_a = null;
        path_b = null;
        unvisited_rooms_path_a = 0;
        unvisited_rooms_path_b = 0;
        destination_list = null;
        //Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();
        
        if(w.isInPit())
                w.doAction(World.A_CLIMB);
        
        //Basic action:
        //Grab Gold if we can.
        if (w.hasGlitter(cX, cY))
        {
            w.doAction(World.A_GRAB);
            return;
        }
        
        System.out.println("\n\n\n\n\n\n\n\n");
        roomobj.graphUpdate(pit_list);
        if(duplicateRoomCheck(cX,cY,visited_rooms))
        {
            //System.out.println("already present in the visited room list");
        }
        else
        {
            addVisitedRoomsLL(cX,cY);
        }
        System.out.println("Visited Rooms are");
        displayRooms(visited_rooms);
        
        
        if(w.hasBreeze(cX, cY))
        {
            if(duplicateRoomCheck(cX,cY,breezes))
            {
                //System.out.println("this room is already present in the breeze list");
            }
            else
            {
               addBreezes(cX,cY); 
            }  
        }
        
        System.out.println("Breezes found until now are");
        displayRooms(breezes);
        
        pitFinder();
        System.out.println("pits found are");
        displayRooms(pit_list);
        
        
        

        
        //Test the environment
        if (w.hasStench(cX, cY) && w.hasBreeze(cX, cY) && cX == 1 && cY ==1 )
        {
            int cur_direction = w.getDirection();
            int req_direction = 0;
            changeDirection(cur_direction,req_direction);
            killWumpus();
        }
        else if (w.hasStench(cX, cY))
        {
            System.out.println("Stench Move");
            stenchFunction(cX,cY);
        }
        else if(cX == safe_room_x && cY == safe_room_y)   //breeze back tracking finish condition
        {
            breeze_back_track = 0;
            safe_room_x = -1;   
            safe_room_y = -1;
            
            safeMoveAfterBreezeBackTracking(cX,cY);
            System.out.println("Breeze back tracking finished");
        }
        /*
        else if(breeze_back_track == 1 && w.hasBreeze(cX, cY))
        {
            breeze_back_track = 0;
            nextMove(cX,cY);
        }*/
        else if(breeze_back_track == 1)
        {
            System.out.println("Breeze Back Tracking not yet ended");
            nextMove(cX,cY);
            System.out.println("In breeze back tracking "+safe_room_x+" , "+safe_room_y);
            if(safe_room_x == cX && safe_room_y == cY)
            {
                breeze_back_track = 0;
            }
        }
        
        else if(w.hasBreeze(cX, cY))
        {
            System.out.println("Breeze room move");
            if(findSafeOuterRoom())
            {
                System.out.println("Safe room available");
                if(safe_room_x == cX && safe_room_y == cY)
                {
                    System.out.println("Present room is safe room");
                    safeMoveAfterBreezeBackTracking(cX,cY);
                }
                else
                {
                    breeze_back_track = 1;
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_MOVE);
                }          
            }
            else
            {
                System.out.println("no safe outer room found Sarath Chandra Damineni");  
                noSafeRoomFoundBreeze(cX,cY);               
            }     
        }   
        else
        {     
            System.out.println("A normal move");
            nextMove(cX,cY);
        }
        
        roomobj.graphUpdate(pit_list);

    }

    public void pitFinder()
    {
        RoomLinkedList rll = breezes;
        while(rll != null)
        {
            int count = 0;
            int pit_x = 0 ,pit_y = 0;
            int cX = rll.cX;
            int cY = rll.cY;
                
            if(w.isValidPosition(cX-1, cY))
            {
                if(!(roomobj.rooms[(cX-1)-1][(cY)-1].score == 5))
                {
                    pit_x = cX-1;
                    pit_y = cY;
                    count++;
                }
            }
            if(w.isValidPosition(cX, cY+1))
            {
                if(!(roomobj.rooms[(cX)-1][(cY+1)-1].score == 5))
                {
                    pit_x = cX;
                    pit_y = cY+1;
                    count++;
                }
            }
            if(w.isValidPosition(cX+1, cY))
            {
                if(!(roomobj.rooms[(cX+1)-1][(cY)-1].score == 5))
                {
                    pit_x = cX+1;
                    pit_y = cY;
                    count++;
                }
            }
            if(w.isValidPosition(cX, cY-1))
            {
                if(!(roomobj.rooms[(cX)-1][(cY-1)-1].score == 5))
                {
                    pit_x = cX;
                    pit_y = cY-1;
                    count++;
                }
            }
            
            if(count == 1)
            {
                //System.out.println("pit found at "+pit_x+" , "+pit_y);
                if(duplicateRoomCheck(pit_x,pit_y,pit_list))
                {
                    System.out.println("This pit is already present in the list");
                }
                else
                {
                    
                    addPitsToList(pit_x,pit_y); 
                }                
            }
            rll = rll.down;
        }
    }
    
    public void addPitsToList(int cX,int cY)
    {
        
        RoomLinkedList rll = new RoomLinkedList(cX,cY);
        
        if(pit_list == null)
        {
            pit_list = rll;
        }
        else
        {
            RoomLinkedList temp = pit_list;
            while(temp.down != null)
            {
                temp = temp.down;
            }
            
            temp.down = rll;
            rll.up = temp;
        }            
    }
    
    public void breezeFunction(int cX,int cY)
    {
        
    }
    
    public int numberOfRooms(RoomLinkedList rll)
    {
        int length = 0;
        while(rll != null)
        {
            length++;
            rll = rll.down;
        }
        return length;
    }
    
    public void addVisitedRoomsLL(int cX,int cY)
    {
        RoomLinkedList q = new RoomLinkedList(cX,cY);
        if(visited_rooms == null)
        {
            visited_rooms = q;
        }
        else
        {
            visited_rooms.up = q;
            q.down = visited_rooms;
            visited_rooms = q;
        }
    }
    public void displayRooms(RoomLinkedList rll)
    {
        RoomLinkedList temp = rll;
        
        if(temp == null)
                System.out.println("No rooms are found until now");
        
        while(temp != null)
        {
            System.out.println(temp.cX+" , "+temp.cY);
            temp = temp.down;
        }
    }
    public boolean duplicateRoomCheck(int cX,int cY,RoomLinkedList rll)
    {
        while(rll != null)
        {
            if(cX == rll.cX && cY == rll.cY)
            {
                return true;
            }
            rll = rll.down;
        }
        return false;
    }
    
    public void addBreezes(int cX,int cY)
    {
        RoomLinkedList q = new RoomLinkedList(cX,cY);
        if(breezes == null)
        {
            breezes = q;
        }
        else
        {     
            q.down = breezes;
            breezes.up = q;
            breezes = q;
        }
    }
    
    
    public void nextMove(int cX,int cY)
    {
        destinationFinder(cX,cY);
        
        int mX,mY;
        if(breeze_back_track == 0) //if there is no breeze backtracking then choose path that contain more number of unvisited rooms
        {    
        if(unvisited_rooms_path_a >= unvisited_rooms_path_b)
        {
            RoomLinkedList temp = path_a;
            //System.out.println("path a choosen");
            while(temp.down.down != null)
            {
                temp = temp.down;
            }
            mX = temp.cX;
            mY = temp.cY;
            //System.out.println("move to "+mX+" , "+mY);
            
        }
        else
        {
            RoomLinkedList temp = path_b;
            //System.out.println("path b choosen");
            while(temp.down.down != null)
            {
                temp = temp.down;
            } 
            mX = temp.cX;
            mY = temp.cY;
            //System.out.println("move to "+mX+" , "+mY);
        }
        }
        else //if there is a breezeback tracking choose the path that contain more number of visited roooms
        {
        if(visited_rooms_path_a >= visited_rooms_path_b)
        {
            RoomLinkedList temp = path_a;
            //System.out.println("path a choosen");
            while(temp.down.down != null)
            {
                temp = temp.down;
            }
            mX = temp.cX;
            mY = temp.cY;
            //System.out.println("move to "+mX+" , "+mY);
            
        }
        else
        {
            RoomLinkedList temp = path_b;
            //System.out.println("path b choosen");
            while(temp.down.down != null)
            {
                temp = temp.down;
            } 
            mX = temp.cX;
            mY = temp.cY;
            //System.out.println("move to "+mX+" , "+mY);
        } 
        }
        int current_direction = w.getDirection();
        int required_direction;
        
        if(mX == cX+1 &&  mY == cY)
        {
            required_direction = 1;
        }
        else if(mX == cX && mY == cY+1)
        {
            required_direction = 0;
        }
        else if(mX == cX-1 && mY == cY)
        {
            required_direction = 3;
        }
        else
        {
            required_direction  = 2;
        }
        changeDirection(current_direction,required_direction);
        w.doAction(World.A_MOVE);
    }
    
    public void destinationFinder(int cX,int cY)
    {
        int destX = 0;
        int destY = 0;
        if(single_stench_back_track % 2 == 1)
        {
            
            for(int mx = 1;mx <= 4;mx++)
            {
                for(int my = 1;my <= 4;my++)
                {
                    if(!w.isVisited(mx, my))
                    {
                        if(!checkInPitList(mx,my))
                        {
                            addToDestinationList(mx,my);
                        }                       
                    }
                }
            }
            RoomLinkedList temp = destination_list;
            while(temp.down != null)
            {
                temp = temp.down;
            }
            temp.down = pit_list;
            
            if(breeze_back_track == 1)
            {
                addToDestinationList(safe_room_x,safe_room_y);
            }
            destX = destination_list.cX;
            destY = destination_list.cY;
        }
        else
        {
            
            for(int my = 1;my <= 4;my++)
            {
                for(int mx = 1;mx <= 4;mx++)
                {
                    if(!w.isVisited(mx, my))
                    {  
                        if(!checkInPitList(mx,my))
                        {
                            addToDestinationList(mx,my);
                        }                      
                    }
                }
            }
            RoomLinkedList temp = destination_list;
            while(temp.down != null)
            {
                temp = temp.down;
            }
            temp.down = pit_list;
            
            if(breeze_back_track == 1)
            {
                addToDestinationList(safe_room_x,safe_room_y);
            }
            destX = destination_list.cX;
            destY = destination_list.cY;
        }
        
        
        //System.out.println("So present Destination is "+destX+" , "+destY);
        findPathA(destX,destY,cX,cY);
        findPathB(destX,destY,cX,cY);
    }
    public void addToDestinationList(int cX,int cY)
    {
        RoomLinkedList rll = new RoomLinkedList(cX,cY);
        
        if(destination_list == null)
        {
            destination_list = rll;
        }
        else
        {
            RoomLinkedList temp = destination_list;
            while(temp.down != null)
            {
                temp = temp.down;
            }
            
            temp.down = rll;
            rll.up = temp;
        }
            
    }
    public void findPathA(int destX,int destY,int cX,int cY)
    {     
        int diff_x = destX - cX;
        int diff_y = destY - cY;
        int tempX = cX;
        int tempY = cY;
        addRoomsForPathA(tempX,tempY);
        if(diff_x > 0)
        {
            for(int vx = 1;vx <= diff_x;vx++)
            {
                tempX = cX + vx;
                //System.out.println(tempX+" , "+tempY);
                if(!w.isVisited(tempX, tempY))
                    unvisited_rooms_path_a++;
                else
                    visited_rooms_path_a++;
                addRoomsForPathA(tempX,tempY);
            }
        }
        else
        {
            for(int vx = 1;vx <= Math.abs(diff_x);vx++)
            {
                tempX = cX - vx;
                //System.out.println(tempX+" , "+tempY);
                if(!w.isVisited(tempX, tempY))
                    unvisited_rooms_path_a++;
                else
                    visited_rooms_path_a++;
                addRoomsForPathA(tempX,tempY);
            }
        }
        
        if(diff_y > 0)
        {
            for(int vy = 1;vy <= diff_y;vy++)
            {
                tempY = cY + vy;
                //System.out.println(tempX+" , "+tempY);
                if(!w.isVisited(tempX, tempY))
                    unvisited_rooms_path_a++;
                else
                    visited_rooms_path_a++;
                addRoomsForPathA(tempX,tempY);
            }
        }
        else
        {
            for(int vy = 1;vy <= Math.abs(diff_y);vy++)
            {
                tempY = cY - vy;
                //System.out.println(tempX+" , "+tempY);
                if(!w.isVisited(tempX, tempY))
                    unvisited_rooms_path_a++;
                else
                    visited_rooms_path_a++;
                addRoomsForPathA(tempX,tempY);
            }
        }
        
        displayPathA();
    }
    
    public void findPathB(int destX,int destY,int cX,int cY)
    {
        int diff_x = destX - cX;
        int diff_y = destY - cY;
        int tempX = cX;
        int tempY = cY;
        addRoomsForPathB(tempX,tempY);
        if(diff_y > 0)
        {
            for(int vy = 1;vy <= diff_y;vy++)
            {
                tempY = cY + vy;
                if(!w.isVisited(tempX, tempY))
                    unvisited_rooms_path_b++;
                else
                    visited_rooms_path_b++;
                addRoomsForPathB(tempX,tempY);
            }
        }
        else
        {
            for(int vy = 1;vy <= Math.abs(diff_y);vy++)
            {
                tempY = cY - vy;
                if(!w.isVisited(tempX, tempY))
                    unvisited_rooms_path_b++;
                else
                    visited_rooms_path_b++;
                addRoomsForPathB(tempX,tempY);
            }
        }
        
        if(diff_x > 0)
        {
            for(int vx = 1;vx <= diff_x;vx++)
            {
                tempX = cX + vx;
                if(!w.isVisited(tempX, tempY))
                    unvisited_rooms_path_b++;
                else
                    visited_rooms_path_b++;
                addRoomsForPathB(tempX,tempY);
            }
        }
        else
        {
            for(int vx = 1;vx <= Math.abs(diff_x);vx++)
            {
                tempX = cX - vx;
                if(!w.isVisited(tempX, tempY))
                    unvisited_rooms_path_b++;
                else
                    visited_rooms_path_b++;
                addRoomsForPathB(tempX,tempY);
            }
        }
        
        displayPathB();
    }
    
    public void addRoomsForPathA(int cX,int cY)
    {
        RoomLinkedList q = new RoomLinkedList(cX,cY);
        if(path_a == null)
        {
            path_a = q;
        }
        else
        {     
            q.down = path_a;
            path_a.up = q;
            path_a = q;
        }
    }
    
    public void displayPathA()
    {
        //System.out.println("display method Path A");
        RoomLinkedList temp = path_a;
        while(temp != null)
        {
            //System.out.println(temp.cX+" , "+temp.cY);
            temp = temp.down;
        }
        //System.out.println("number of unvisisted rooms in path a: "+unvisited_rooms_path_a);
    }
    
    public void addRoomsForPathB(int cX,int cY)
    {
        RoomLinkedList q = new RoomLinkedList(cX,cY);
        if(path_b == null)
        {
            path_b = q;
        }
        else
        {     
            q.down = path_b;
            path_b.up = q;
            path_b = q;
        }
    }
    
    public void displayPathB()
    {
        //System.out.println("display method Path B");
        RoomLinkedList temp = path_b;
        while(temp != null)
        {
            //System.out.println(temp.cX+" , "+temp.cY);
            temp = temp.down;
        }
        //System.out.println("number of unvisisted rooms in path b: "+unvisited_rooms_path_b);
    }
            
    
    public void stenchFunction(int cx,int cy)
    {
        if(w.hasPit(cx, cy))
        {
            w.doAction(World.A_CLIMB);
        }
        if(w.isValidPosition(cx-1, cy-1) && w.isVisited(cx-1, cy-1))
        {
            if(w.hasStench(cx-1, cy-1))
            {
                if(duplicateStenchRoom(cx-1,cy-1))
                {
                    
                }
                else
                {
                    addStenches(cx-1,cy-1);
                }
                
            }
            else
            {
               // System.out.println((cx-1)+","+(cy-1)+" has no stench");
            }
        }
        if(w.isValidPosition(cx-2, cy) && w.isVisited(cx-2, cy))
        {
            if(w.hasStench(cx-2, cy))
            {
                if(duplicateStenchRoom(cx-2,cy))
                {
                    
                }
                else
                {
                    addStenches(cx-2,cy);
                }     
            }
            else
            {
                //System.out.println((cx-2)+","+(cy)+" has no stench");
            }
        }
        if(w.isValidPosition(cx-1, cy+1) && w.isVisited(cx-1,cy+1))
        {
            if(w.hasStench(cx-1, cy+1))
            {
                if(duplicateStenchRoom(cx-1,cy+1))
                {
                    
                }
                else
                {
                    addStenches(cx-1,cy+1);
                }              
            }
            else
            {
               // System.out.println((cx-1)+","+(cy+1)+" has no stench");
            }
        }
        if(w.isValidPosition(cx, cy+2) && w.isVisited(cx, cy+2))
        {
            if(w.hasStench(cx, cy+2))
            {
                if(duplicateStenchRoom(cx,cy+2))
                {
                    
                }
                else
                {
                    addStenches(cx,cy+2);
                }       
            }
            else
            {
               // System.out.println((cx)+","+(cy+2)+" has no stench");
            }
        }
        if(w.isValidPosition(cx+1, cy+1) && w.isVisited(cx+1, cy+1))
        {
            if(w.hasStench(cx+1, cy+1))
            {
                if(duplicateStenchRoom(cx+1,cy+1))
                {
                    
                }
                else
                {
                    addStenches(cx+1,cy+1);
                }
                
            }
            else
            {
               // System.out.println((cx+1)+","+(cy+1)+" has no stench");
            }
        }
        if(w.isValidPosition(cx+2, cy) && w.isVisited(cx+2, cy))
        {
            if(w.hasStench(cx+2, cy))
            {
                if(duplicateStenchRoom(cx+2,cy))
                {
                    
                }
                else
                {
                    addStenches(cx+2,cy);
                }
                
            }
            else
            {
                //System.out.println((cx+2)+","+(cy)+" has no stench");
            }
        }
        if(w.isValidPosition(cx+1, cy-1) && w.isVisited(cx+1, cy-1))
        {
            if(w.hasStench(cx+1, cy-1))
            {
                if(duplicateStenchRoom(cx+1,cy-1))
                {
                    
                }
                else
                {
                   addStenches(cx+1,cy-1); 
                } 
            }
            else
            {
               // System.out.println((cx+1)+","+(cy-1)+" has no stench");
            }
        }
        if(w.isValidPosition(cx, cy-2) && w.isVisited(cx, cy-2))
        {
            if(w.hasStench(cx, cy-2))
            {
                if(duplicateStenchRoom(cx,cy-2))
                {
                    
                }
                else
                {
                    addStenches(cx,cy-2);
                }          
            }
            else
            {
                //System.out.println((cx)+","+(cy-2)+" has no stench");
            }
        }
        
        if(duplicateStenchRoom(cx,cy)) 
        {
            //System.out.println("aldready present in the queue");
        }
        else
        {
           addStenches(cx,cy); 
        }
        
        displayStenches();
        int numberOfStenchesFound = numberOfStenches();
        System.out.println(numberOfStenchesFound); 
        nextStepWhenStenchFound(numberOfStenchesFound,cx,cy);
    }
    
    public void nextStepWhenStenchFound(int numberOfStenchesFound,int cX,int cY)
    {
        int current_direction = w.getDirection();
        single_stench_back_track++;
        if(numberOfStenchesFound == 1)  //if we found only one stench (then we move to the previous node)
        {
            if(!singleStenchCondition(cX,cY))
            {
               w.doAction(World.A_TURN_LEFT);
               w.doAction(World.A_TURN_LEFT);
               w.doAction(World.A_MOVE); 
            }
            
        }
        else if(numberOfStenchesFound == 2) //if two stenches were found 
        {
            int x2 = stench_top.down.cX;
            int y2 = stench_top.down.cY;
            
            /*
            if we found two stenches then we are able to kill the wumpus if two stenches are exactly opposite
            */
            if(x2 == cX && y2 == cY+2) 
            {
                int required_direction = 0;
                changeDirection(current_direction,required_direction);
                killWumpus();
            }
            else if(x2 == cX-2 && y2 == cY)
            {
                int required_direction = 3;
                changeDirection(current_direction,required_direction);
                killWumpus();
            }
            else if(x2 == cX && y2 == cY-2)
            {
                int required_direction = 2;
                changeDirection(current_direction,required_direction);
                killWumpus();
            }
            else if(x2 == cX+2 && y2 == cY)
            {
                int required_direction = 1;
                changeDirection(current_direction,required_direction);
                killWumpus();
            }
            // if two stinches found are diagnol to each other
            else if((x2 == cX-1) && (y2 == cY-1))
            {
                if(w.isVisited(cX,cY-1))
                {
                    int required_direction = 3;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(w.isVisited(cX-1,cY))
                {
                    int required_direction = 2;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else
                {
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_MOVE);
                }
            }
            else if((x2 == cX-1) && (y2 == cY+1))
            {
                if(w.isVisited(cX-1, cY))
                {
                    int required_direction = 0;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(w.isVisited(cX, cY+1))
                {
                    int required_direction = 3;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else
                {
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_MOVE);
                }
            }
            else if((x2 == cX+1) && (y2 == cY+1))
            {
                if(w.isVisited(cX+1,cY))
                {
                    int required_direction = 0;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(w.isVisited(cX,cY+1))
                {
                    int required_direction = 1;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else
                {
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_MOVE);
                }
            }
            else if((x2 == cX+1) && (y2 == cY-1))
            {
                if(w.isVisited(cX,cY-1))
                {
                    int required_direction = 1;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(w.isVisited(cX+1,cY))
                {
                    int required_direction = 2;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else
                {
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_TURN_LEFT);
                    w.doAction(World.A_MOVE);
                }
            }
        }
        else if(numberOfStenchesFound == 3)
        {
            int x1 = cX;
            int y1 = cY;
            int x2 = stench_top.down.cX;
            int y2 = stench_top.down.cY;
            int x3 = stench_top.down.down.cX;
            int y3 = stench_top.down.down.cY;
            
            if(y1 == y2)
            {
                if(x1 < x2)
                {
                    int required_direction = 1;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(x1 > x2)
                {
                    int required_direction = 3;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
            }
            else if(y2 == y3)
            {
                if(y1 < y2)
                {
                    int required_direction = 0;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(y1 > y2)
                {
                    int required_direction = 2;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
            }
            else  if(y1 == y3)
            {
                if(x1 < x3)
                {
                    int required_direction = 1;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(x1 > x3)
                {
                    int required_direction = 3;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
            }
            else if(x1 == x2)
            {
                if(y1 < y2)
                {
                    int required_direction = 0;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(y1 > y2)
                {
                    int required_direction = 2;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
            }
            else if(x2 == x3)
            {
                if(x1 < x2)
                {
                    int required_direction = 1;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(x1 > x2)
                {
                    int required_direction = 3;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
            }
            else if(x1 == x3)
            {
                if(y1 < y3)
                {
                    int required_direction = 0;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
                else if(y1 > y3)
                {
                    int required_direction = 2;
                    changeDirection(current_direction,required_direction);
                    killWumpus();
                }
            }        
        }
    }
    
    
    /*
    @input:akes the current direction and required direction
    @process:rotates in to required direction
    @logic:if the difference between required and current direction is 2 or -2 then move in opposite direction 
    @logic:if the difference between required and current direction is 1 or -3 then move in one right direction
    @logic:if the difference between required and current direction is -3 or 1 then move in one left direction
    @output:set the pointer in to the required direction
    */
    public void changeDirection(int current_direction,int required_direction)
    {
        int diff = required_direction - current_direction;
        
        if(diff == 2 || diff == -2)
        {
            w.doAction(World.A_TURN_LEFT);
            w.doAction(World.A_TURN_LEFT);
        }
        else if(diff == 1 || diff == -3)
        {
            w.doAction(World.A_TURN_RIGHT);
        }
        else if(diff == 3 || diff == -1)
        {
            w.doAction(World.A_TURN_LEFT);
        }
    }
    
    /*
    @process:shoot the arrow if the the player has Arrow and wumpus is alive
    */
    public void killWumpus()
    {
        stenchRoomsScoreUpdate(); //updating the scores of the rooms in which stenches are present
        stench_top = null;
        if(w.hasArrow() && w.wumpusAlive())
        {
            w.doAction(World.A_SHOOT);
        }
       
    }
    public void addStenches(int cX,int cY)
    {
        RoomLinkedList q = new RoomLinkedList(cX,cY);
        if(stench_top == null)
        {
            stench_top = q;
        }
        else
        {     
            q.down = stench_top;
            stench_top.up = q;
            stench_top = q;
        }
    }
    
    public void displayStenches()
    {
        //System.out.println("So stenches found are");
        RoomLinkedList qsr = stench_top;
        while(qsr != null)
        {
            //System.out.println("cX,cY is  "+qsr.cX+","+qsr.cY);
            qsr = qsr.down;
        }
    }
    
    /*
    @process:counts number of stenches found
    */
    public int numberOfStenches()
    {
        int number = 0;
        RoomLinkedList qsr = stench_top;
        while(qsr != null)
        {
            qsr = qsr.down;
            number++;
        }
        return number;
    }
    
    /*return true if duplicate found else false*/
    public boolean duplicateStenchRoom(int cX,int cY)
    {
        RoomLinkedList qsr = stench_top;
        while(qsr != null)
        {
            if(cX == qsr.cX && cY == qsr.cY)
            {
                return true;
            }
            qsr = qsr.down;
        }
        return false;
    }
    
    public boolean findSafeOuterRoom()
    {
        RoomLinkedList temp = visited_rooms;
        int safe_x = -1,safe_y = -1;
        while(temp != null)
        {
            int x_ind = temp.cX;
            int y_ind = temp.cY;
            int arr_indx = x_ind -1;
            int arr_indy = y_ind -1;
            
            
            if(w.isValidPosition(x_ind - 1, y_ind) && roomobj.rooms[arr_indx - 1][arr_indy].score == 5 && (!w.isVisited(x_ind - 1, y_ind)))
            {
                System.out.println("safe outer room is "+x_ind+" , "+y_ind);
                safe_x = x_ind;
                safe_y = y_ind; 
                safe_room_x = x_ind;
                safe_room_y = y_ind;
                break;
            }
            else if(w.isValidPosition(x_ind,y_ind+1) && roomobj.rooms[arr_indx][arr_indy+1].score == 5 && (!w.isVisited(x_ind, y_ind + 1)))
            {
                System.out.println("safe outer room is "+x_ind+" , "+y_ind);
                safe_x = x_ind;
                safe_y = y_ind;
                safe_room_x = x_ind;
                safe_room_y = y_ind;
                break;
            }
            else if(w.isValidPosition(x_ind + 1, y_ind) && roomobj.rooms[arr_indx + 1][arr_indy].score == 5 && (!w.isVisited(x_ind + 1, y_ind)))
            {
                System.out.println("safe outer room is "+x_ind+" , "+y_ind);
                safe_x = x_ind;
                safe_y = y_ind;
                safe_room_x = x_ind;
                safe_room_y = y_ind;
                break;
            }
            else if(w.isValidPosition(x_ind, y_ind - 1) && roomobj.rooms[arr_indx][arr_indy - 1].score == 5 && (!w.isVisited(x_ind,y_ind - 1)))
            {
                System.out.println("safe outer room is "+x_ind+" , "+y_ind);
                safe_x = x_ind;
                safe_y = y_ind;
                safe_room_x = x_ind;
                safe_room_y = y_ind;
                break;
            }   
            temp = temp.down;
        }
        if(safe_x == -1 && safe_y == -1)
        {
            System.out.println("No safe room found");
            return false;
        }
        else
        {
            System.out.println("safe room is"+safe_x+" , "+safe_y);
            return true;
        }
    }
    
    public void stenchRoomsScoreUpdate()
    {
        RoomLinkedList temp = stench_top;
        while(temp != null)
        {
                roomobj.rooms[temp.cX-1][temp.cY-1].stench_removed = 1;
            temp = temp.down;
        }
        roomobj.graphUpdate(pit_list);
    }
    
    public void safeMoveAfterBreezeBackTracking(int cX,int cY)
    {
        System.out.println("safe move after breeze back tracking");
        int arr_ind_x = cX-1;
        int arr_ind_y = cY-1;
        int required_direction = -1;
        int current_direction = w.getDirection();
        
        if(!w.isVisited(cX -1, cY) && w.isValidPosition(cX-1, cY))
        {
            if(roomobj.rooms[arr_ind_x -1][arr_ind_y].score == 5)
            {
                required_direction = 3;
            }         
        }
        if(!w.isVisited(cX, cY+1) && w.isValidPosition(cX, cY+1))
        {
            if(roomobj.rooms[arr_ind_x][arr_ind_y+1].score == 5)
            {
                required_direction = 0;
            }    
        }
        if(!w.isVisited(cX+1, cY) && w.isValidPosition(cX+1, cY))
        {
            if(roomobj.rooms[arr_ind_x+1][arr_ind_y].score == 5)
            {
                required_direction = 1;
            }            
        }
        if(!w.isVisited(cX, cY -1) && w.isValidPosition(cX, cY-1))
        {
            if(roomobj.rooms[arr_ind_x][arr_ind_y -1].score == 5)
            {
               required_direction = 2;
               
            }  
        }
        
        if(required_direction != -1)
        {
            changeDirection(current_direction,required_direction);
        }
         
        w.doAction(World.A_MOVE);
    }
    
    public boolean checkInPitList(int cX,int cY)
    {
        RoomLinkedList rll = pit_list;
        while(rll != null)
        {
            if(cX == rll.cX && cY == rll.cY)
            {
                return true;
            }
            rll = rll.down;
        }
        return false;
    }
    
    public void noSafeRoomFoundBreeze(int cX,int cY)
    {
        int cur_dir = w.getDirection();
        int arr_x = cX - 1;
        int arr_y = cY - 1;
        int req_dir = -1,score_here = 1000;
        
        
        if(w.isValidPosition(cX-1, cY))
        {
            if(roomobj.rooms[arr_x - 1][arr_y].score != 3 && !w.isVisited(cX-1, cY))
            {
               req_dir = 3;
               score_here =  roomobj.rooms[arr_x - 1][arr_y].score; 
            }
            
        }
        if( w.isValidPosition(cX, cY+1))
        {
            if(roomobj.rooms[arr_x][arr_y + 1].score != 3 && !w.isVisited(cX, cY+1))
            {
                if(score_here > roomobj.rooms[arr_x][arr_y + 1].score)
                {
                    req_dir = 0; 
                    score_here = roomobj.rooms[arr_x][arr_y + 1].score;
                }                         
            }           
        }
        if(w.isValidPosition(cX+1, cY))
        {
            if(roomobj.rooms[arr_x + 1][arr_y].score != 3 && !w.isVisited(cX+1, cY))
            {
                if(score_here > roomobj.rooms[arr_x + 1][arr_y].score)
                {
                    req_dir = 1;
                    score_here = roomobj.rooms[arr_x + 1][arr_y].score;
                }          
            }
        }
        if(w.isValidPosition(cX, cY-1))
        {
            if(roomobj.rooms[arr_x][arr_y-1].score != 3 && !w.isVisited(cX, cY-1))
            {
                if(score_here > roomobj.rooms[arr_x][arr_y-1].score)
                {
                    req_dir = 2;
                    score_here = roomobj.rooms[arr_x][arr_y-1].score;
                }               
            }           
        }
        if(!(req_dir == -1))
        {
           changeDirection(cur_dir,req_dir);
           w.doAction(World.A_MOVE); 
        }
        else
        {
            System.out.println("breeze next move jabkjzbdkjdbz");   
            breeze_back_track = 1;
            nextMove(cX,cY);
        }  
    }
    public boolean singleStenchCondition(int cX,int cY)
    {
        int arr_X = cX-1;
        int arr_Y = cY-1;
        int req_dir = -1;
        int count = 0;
        int cur_dir = w.getDirection();
        System.out.println("Single Stench Condition");
        
        if(w.isValidPosition(cX-1, cY) && !w.isVisited(cX-1, cY))
        {
            if(roomobj.rooms[arr_X-1][arr_Y].possible_to_have_wumpus == 2)
            {
                req_dir = 3;
                count++;
            }
        }
        if(w.isValidPosition(cX, cY + 1) && !w.isVisited(cX, cY+1))
        {
            if(roomobj.rooms[arr_X][arr_Y+1].possible_to_have_wumpus == 2)
            {
                req_dir = 0;
                count++;
            }
        }
        if(w.isValidPosition(cX+1, cY) && !w.isVisited(cX+1, cY))
        {
            if(roomobj.rooms[arr_X+1][arr_Y].possible_to_have_wumpus == 2)
            {
                req_dir = 1;
                count++;
            }
        }
        if(w.isValidPosition(cX, cY-1) && !w.isVisited(cX, cY-1))
        {
            if(roomobj.rooms[arr_X][arr_Y-1].possible_to_have_wumpus == 2)
            {
                req_dir = 2;
                count++;
            }
        }
        
        System.out.println("Count is "+count);
        if(count == 1)
        {
            changeDirection(cur_dir,req_dir);
            killWumpus();
            System.out.println("Stench Found Using Single Stench");
            return true;
        }
        return false;
    }
}

