package wumpusworld;
public class Rooms
{
    int indexX;
    int indexY;
    int score = 0;  
    World w;
    Rooms n1=null,n2=null,n3=null,n4=null;
    int stench=0,breeze=0,wumpus=0,pit=0,glitter=0,stench_removed;
    int  possible_to_have_wumpus = 2;
    int cost = 1000;
    Rooms rooms[][];
    
    public Rooms(World w)
    {
        this.w = w;
        System.out.println("empty constructor");
        createGraph();
    }
    public Rooms(int indexX,int indexY)
    {
        this.indexX = indexX;
        this.indexY = indexY;
    }
    
    public void createGraph()
    {
        rooms = new Rooms[4][4];
        
        System.out.println("graph creation");
        for(int x = 0; x < 4;x++)
        {
            for(int y = 0; y < 4;y++)
            {
                rooms[x][y] = new Rooms(x,y);
                rooms[x][y].possible_to_have_wumpus = 2;
            }
        }
        
        /*
        for(int x = 0;x < 4;x++)
        {
            for(int y = 0 ;y < 4;y++)
            {
                int ind_x = x+1;
                int ind_y = y+1;
                
                if(w.isValidPosition(ind_x, ind_y+1))
                {
                    rooms[x][y].n1 = rooms[x][y+1];
                }
                if(w.isValidPosition(ind_x+1, ind_y))
                {
                    rooms[x][y].n2 = rooms[x+1][y];
                }
                if(w.isValidPosition(ind_x, ind_y-1))
                {
                    rooms[x][y].n3 = rooms[x][y-1];
                }
                if(w.isValidPosition(ind_x-1, ind_y))
                {
                    rooms[x][y].n4 = rooms[x-1][y];
                } 
            }
        }*/
        
        
        //0,0 node (1,1 Room)
        rooms[0][0].n1 = rooms[0][1];
        rooms[0][0].n2 = rooms[1][0];
        
        //0,1 node (1,2 Room)
        rooms[0][1].n1 = rooms[0][0];
        rooms[0][1].n2 = rooms[0][2];
        rooms[0][1].n3 = rooms[1][1];
        
        //1,0 node (2,1 Room)
        rooms[1][0].n1 = rooms[0][0];
        rooms[1][0].n2 = rooms[1][1];
        rooms[1][0].n3 = rooms[2][0];
        
        //0,2 node (1,3 Room)
        rooms[0][2].n1 = rooms[0][1];
        rooms[0][2].n2 = rooms[0][3];
        rooms[0][2].n3 = rooms[1][2];
        
        //1,1 node (2,2 Room)
        rooms[1][1].n1 = rooms[0][1];
        rooms[1][1].n2 = rooms[1][0];
        rooms[1][1].n3 = rooms[1][2];
        rooms[1][1].n4 = rooms[2][1];
        
        //2,0 node (3,1 Room)
        rooms[2][0].n1 = rooms[1][0];
        rooms[2][0].n2 = rooms[2][1];
        rooms[2][0].n3 = rooms[3][0];
        
        //0,3 node (1,4 Room)
        rooms[0][3].n1 = rooms[0][2];
        rooms[0][3].n2 = rooms[1][3];
        
        //1,2 node (2,3 Room)
        rooms[1][2].n1 = rooms[0][2];
        rooms[1][2].n2 = rooms[1][1];
        rooms[1][2].n3 = rooms[1][3];
        rooms[1][2].n4 = rooms[2][2];
        
        //2,1 node (3,2 Room)
        rooms[2][1].n1 = rooms[1][1];
        rooms[2][1].n2 = rooms[2][0];
        rooms[2][1].n3 = rooms[2][2];
        rooms[2][1].n4 = rooms[3][1];
        
        //3,0 node (4,1 Room)
        rooms[3][0].n1 = rooms[2][0];
        rooms[3][0].n2 = rooms[3][1];
        
        //1,3 node (2,4 Room)
        rooms[1][3].n1 = rooms[0][3];
        rooms[1][3].n2 = rooms[1][2];
        rooms[1][3].n3 = rooms[2][3];
        
        //2,2 node (3,3 Room)
        rooms[2][2].n1 = rooms[1][2];        
        rooms[2][2].n2 = rooms[2][1];
        rooms[2][2].n3 = rooms[2][3];
        rooms[2][2].n4 = rooms[3][2];
        
        //3,1 node (4,2 Room)
        rooms[3][1].n1 = rooms[2][1];
        rooms[3][1].n2 = rooms[3][0];
        rooms[3][1].n3 = rooms[3][2];
        
        //2,3 node (3,4 Room)
        rooms[2][3].n1 = rooms[1][3];
        rooms[2][3].n2 = rooms[2][2];
        rooms[2][3].n3 = rooms[3][3];
        
        //3,2 node (4,3 Room)
        rooms[3][2].n1 = rooms[2][2];
        rooms[3][2].n2 = rooms[3][1];
        rooms[3][2].n3 = rooms[3][3];
        
        
        //3,3 nnode (4,4 Room)
        rooms[3][3].n1 = rooms[2][3];
        rooms[3][3].n2 = rooms[3][2];
         
        
    }
    
    public void graphUpdate(MyAgent.RoomLinkedList pit_list)
    {
        for(int x = 0;x < 4;x++)
        {
            for(int y = 0 ;y < 4;y++)
            {
                int ind_x = x+1;
                int ind_y = y+1;
                int count = 0;
                
                if(w.isVisited(ind_x, ind_y))
                {  
                if(!w.hasStench(ind_x, ind_y) )
                {
                    if(w.isValidPosition(ind_x-1, ind_y))
                    {
                        if(rooms[x-1][y]. possible_to_have_wumpus == 2)
                        {
                            rooms[x-1][y]. possible_to_have_wumpus = 0;
                        }                        
                    }
                    if(w.isValidPosition(ind_x, ind_y+1))
                    {
                        if(rooms[x][y+1]. possible_to_have_wumpus == 2)
                        {
                            rooms[x][y+1]. possible_to_have_wumpus = 0;
                        }
                        
                    }
                    if(w.isValidPosition(ind_x+1, ind_y))
                    {
                        if(rooms[x+1][y]. possible_to_have_wumpus == 2)
                        {
                            rooms[x+1][y]. possible_to_have_wumpus = 0;
                        }
                        
                    }
                    if(w.isValidPosition(ind_x, ind_y-1))
                    {
                        if(rooms[x][y-1]. possible_to_have_wumpus == 2)
                        {
                            rooms[x][y-1]. possible_to_have_wumpus = 0;
                        }
                        
                    }  
                }
                }
                
                if(w.hasPit(ind_x, ind_y)&& rooms[x][y].cost == 1000 )
                {
                    rooms[x][y].cost = 1;
                    rooms[x][y].score = 3;
                    
                    if(w.hasBreeze(ind_x, ind_y))
                    {
                        rooms[x][y].breeze = 1;     
                        
                        if(w.isValidPosition(ind_x,ind_y+1) && !w.isVisited(ind_x, ind_y+1) && rooms[x][y+1].score != 5)
                        {
                            if(rooms[x][y+1].score%2 == 0)
                            {
                                rooms[x][y+1].score += 2;
                              
                            }
                            
                        }
                        if(w.isValidPosition(ind_x+1, ind_y)&& !w.isVisited(ind_x+1, ind_y) && rooms[x+1][y].score != 5)
                        {
                            if(rooms[x+1][y].score%2 == 0)
                            {
                                rooms[x+1][y].score += 2;
                                
                            }
                            
                        }
                        if(w.isValidPosition(ind_x, ind_y-1) && !w.isVisited(ind_x, ind_y-1) && rooms[x][y-1].score != 5)
                        {
                            if(rooms[x][y-1].score%2 == 0)
                            {
                                rooms[x][y-1].score += 2;
                               
                            }
                            
                        }
                        if(w.isValidPosition(ind_x-1, ind_y) && !w.isVisited(ind_x-1, ind_y) && rooms[x-1][y].score != 5)
                        {
                            if(rooms[x-1][y].score%2 == 0)
                            {
                                rooms[x-1][y].score += 2;
                               
                            }                           
                        }
                        
                        
                        //System.out.println(ind_x+" , "+ind_y+" has breeze");
                    }
                }
                else
                {    
                if(w.isVisited(ind_x,ind_y) && rooms[x][y].cost == 1000 || (rooms[x][y].stench_removed == 1) && !(w.hasBreeze(ind_x, ind_y)))
                {
                    //System.out.println("Room class node:"+ind_x+" , "+ind_y);
                    
                    rooms[x][y].cost = 1;
                    rooms[x][y].score = 5;
                    if(w.hasBreeze(ind_x, ind_y))
                    {
                        rooms[x][y].breeze = 1;     
                        
                        if(w.isValidPosition(ind_x,ind_y+1) && !w.isVisited(ind_x, ind_y+1) && rooms[x][y+1].score != 5)
                        {
                            if(rooms[x][y+1].score%2 == 0)
                            {
                                rooms[x][y+1].score += 2;
                              
                            }
                            
                        }
                        if(w.isValidPosition(ind_x+1, ind_y)&& !w.isVisited(ind_x+1, ind_y) && rooms[x+1][y].score != 5)
                        {
                            if(rooms[x+1][y].score%2 == 0)
                            {
                                rooms[x+1][y].score += 2;
                                
                            }
                            
                        }
                        if(w.isValidPosition(ind_x, ind_y-1) && !w.isVisited(ind_x, ind_y-1) && rooms[x][y-1].score != 5)
                        {
                            if(rooms[x][y-1].score%2 == 0)
                            {
                                rooms[x][y-1].score += 2;
                               
                            }
                            
                        }
                        if(w.isValidPosition(ind_x-1, ind_y) && !w.isVisited(ind_x-1, ind_y) && rooms[x-1][y].score != 5)
                        {
                            if(rooms[x-1][y].score%2 == 0)
                            {
                                rooms[x-1][y].score += 2;
                               
                            }                           
                        }
                        
                        
                        //System.out.println(ind_x+" , "+ind_y+" has breeze");
                    }
                     
                        
                    
                    if(w.hasStench(ind_x, ind_y))
                    {
                        rooms[x][y].stench = 1;
                        
                        if(w.isValidPosition(ind_x,ind_y+1) && !w.isVisited(ind_x, ind_y+1) && rooms[x][y+1].score != 5)
                        {
                            if(rooms[x][y+1].score%2 == 0)
                            {
                                rooms[x][y+1].score += 2;
                              
                            }
                            
                        }
                        if(w.isValidPosition(ind_x+1, ind_y)&& !w.isVisited(ind_x+1, ind_y) && rooms[x+1][y].score != 5)
                        {
                            if(rooms[x+1][y].score%2 == 0)
                            {
                                rooms[x+1][y].score += 2;
                                
                            }
                            
                        }
                        if(w.isValidPosition(ind_x, ind_y-1) && !w.isVisited(ind_x, ind_y-1) && rooms[x][y-1].score != 5)
                        {
                            if(rooms[x][y-1].score%2 == 0)
                            {
                                rooms[x][y-1].score += 2;
                               
                            }
                            
                        }
                        if(w.isValidPosition(ind_x-1, ind_y) && !w.isVisited(ind_x-1, ind_y) && rooms[x-1][y].score != 5)
                        {
                            if(rooms[x-1][y].score%2 == 0)
                            {
                                rooms[x-1][y].score += 2;
                               
                            }                           
                        }
                        
                        //System.out.println(ind_x+" , "+ind_y+" has Stench");
                    }
                        
                    
                    if(w.hasGlitter(ind_x, ind_y))
                    {
                        rooms[x][y].glitter = 1;
                        //System.out.println(ind_x+" , "+ind_y+" has Glitter");
                    }
                        
                    
                    if(w.hasPit(ind_x, ind_y))
                    {
                        rooms[x][y].pit = 1;
                        rooms[x][y].cost = 100;
                        //System.out.println(ind_x+" , "+ind_y+" has pit");
                    }
                        
                    
                    if(w.hasWumpus(ind_x, ind_y))
                    {
                        rooms[x][y].wumpus = 1;
                        //System.out.println(ind_x+" , "+ind_y+" has Wampus");
                    }
                    
                    if(rooms[x][y].score ==  5 && !w.hasStench(ind_x, ind_y) && !w.hasBreeze(ind_x, ind_y))
                    {
                        if(w.isValidPosition(ind_x,ind_y+1))
                        {
                            rooms[x][y+1].score = 5;
                        }
                        if(w.isValidPosition(ind_x+1, ind_y))
                        {
                            rooms[x+1][y].score = 5;
                        }
                        if(w.isValidPosition(ind_x, ind_y-1))
                        {
                            rooms[x][y-1].score = 5;
                        }
                        if(w.isValidPosition(ind_x-1, ind_y))
                        {
                            rooms[x-1][y].score = 5;
                        }
                    }
                    

                }
                }
            }
        }
        
        MyAgent.RoomLinkedList temp = pit_list;
        while(temp != null)
        {
            if(w.isValidPosition(temp.cX,temp.cY))
            {
                rooms[(temp.cX) - 1][(temp.cY) - 1].score = 3;
            }
            
            temp = temp.down;
        }
        
        System.out.println("scores for the rooms are");
        
        for(int y = 3; y >= 0;y--)
        {
            for(int x = 0; x < 4;x++)
            {
                System.out.print(rooms[x][y].score+"  ");
            }
            System.out.println("\n");
        }
    }
}