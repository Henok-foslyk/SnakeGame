import java.util.*;

class SnakeGame extends App {
    // Globals
        //have to set windowSize outside because called in main. Encapsulate?
    static int windowSize = 70;
    Vector2 headPosition;
    double headRadius;
    int wallMargins;
    double time;
    
    public class Particle {
        Vector2 position;
        Vector2 velocity;
        double radius;
        Vector3 color;
        boolean alive;
        int age;

    
    }

    Vector2 tailPosition;
    Vector2 snack;
    int snackSize;
    Vector2 lowerPlayAreaCorner;
    Vector2 upperPlayAreaCorner;
    ArrayList<Vector2> DaSnake;

    char lastkeyPressed;
    
    Vector2 segment2;
    Vector2 segment3;
    int segmentSize;
    Particle[] particles;
    int particleNum;
     
    void particleExplode(Vector2 origin, int snackSize){
        for (int particleIndex = 0; particleIndex < particles.length; particleIndex++){
            Particle particle = particles[particleIndex];
            if(!particle.alive){
                Random r = new Random();
                double randomx = origin.x - 5 + r.nextDouble() * 10 * snackSize;
                double randomy = origin.y - 5 + r.nextDouble() * 10 * snackSize;
                particle.position = new Vector2(randomx, randomy); 
                particle.velocity = Vector2.directionVectorFrom(origin, particle.position);
                particles[particleIndex].radius = 1.0;// make random from 0.1-0.5 headradius
                particle.color = Vector3.rainbowSwirl(time);
                particle.alive = true;
                particle.age = 0;
                
            }
        }
}
    
    boolean segmentIntersection(Vector2 segment1, Vector2 segment2, double radius1, double radius2){
        double distance = Vector2.distanceBetween(segment1, segment2);
        if (distance < (radius1 + radius2)){return true;}
        return false;
    }
    boolean inboundary(Vector2 head, double radius, Vector2 lowCorner, Vector2 highCorner){
        boolean inX = head.x - radius > lowCorner.x && head.x + radius < highCorner.x;
        boolean inY = head.y - radius > lowCorner.y && head.y + radius < highCorner.y;
        return inX && inY;
    }
    void setup(){
        wallMargins = 1;
        lowerPlayAreaCorner = new Vector2(wallMargins, wallMargins);
        upperPlayAreaCorner = new Vector2(windowSize - wallMargins, windowSize - wallMargins);

        particleNum = 20;
        particles = new Particle[particleNum];
         for (int particleIndex = 0; particleIndex < particles.length; particleIndex++){
                particles[particleIndex] = new Particle();
            }
        
        headPosition = new Vector2(windowSize/2, windowSize/2);
        headRadius = 2.5;
        tailPosition = headPosition;
        segmentSize = 2;
        lastkeyPressed = 'D';
        snack = new Vector2(windowSize/2 + 10, windowSize/2);
        snackSize = 1;
        DaSnake = new ArrayList<>();
        DaSnake.add(headPosition);
  
        segment2 = new Vector2 (headPosition.x - 4, headPosition.y);
        segment3 = new Vector2 (headPosition.x - 8, headPosition.y);
        DaSnake.add(segment2);
        DaSnake.add(segment3);
        
        
    }
    void loop(){
        time += 0.05;
        /*Initial mechanics*/{
        drawCornerRectangle(lowerPlayAreaCorner, upperPlayAreaCorner, Vector3.black);
        DaSnake.set(0, headPosition);
        tailPosition = DaSnake.get(DaSnake.size() - 1);
        drawCircle(headPosition, 2.5, Vector3.orange);
        }
        //particle effect for snack
        for (int i = 0; i < particles.length; i++){
            Particle particle = particles[i];
            if(particle.alive && particle.age < 20){ 
                particle.position = particle.position.plus(particle.velocity);
                drawCenterRectangle(particle.position, new Vector2(snackSize,snackSize), Vector3.rainbowSwirl(time));
                particle.age++;
                particle.position = particle.position.plus(particle.velocity);}
            else if (particle.age >= 20) { particle.alive = false; particle.age = 0;}
        }
        // Segments follow each other - snake moves
        for(int i = 1; i < DaSnake.size(); i++){

            Vector2 snakeSegment = DaSnake.get(i);
            if(snakeSegment != null){drawCircle(snakeSegment, 2.0, Vector3.yellow);}
            // 0.9 to make the snake connected but not too overlapped
            boolean intersected =  segmentIntersection(snakeSegment, DaSnake.get(i-1), 0.9 * segmentSize, 0.9 * segmentSize);
            if(!intersected){
            DaSnake.set(i, snakeSegment.plus(Vector2.directionVectorFrom(snakeSegment, DaSnake.get(i-1))));
            }
        }
        // if Snake bites itself
        for (int i = 2; i < DaSnake.size(); i++){
            Vector2 snakeSegment = DaSnake.get(i);
            boolean headintersected = segmentIntersection(DaSnake.get(0), snakeSegment, headRadius, segmentSize);
            if (headintersected){
                reset();
            }
        }
        //snake leaves boundary
        boolean snakeInsideOfPlayArea = inboundary(headPosition, headRadius, lowerPlayAreaCorner, upperPlayAreaCorner);
        if(!snakeInsideOfPlayArea){reset();}

        drawCenterRectangle(snack, new Vector2(2.0, 2.0), Vector3.red);
        boolean ateSnake = segmentIntersection(snack, headPosition, headRadius, snackSize);
        //when snack eaten
        if(ateSnake){
            tailPosition = new Vector2(tailPosition.x, tailPosition.y);
            DaSnake.add(tailPosition);
            Random random = new Random();
            particleExplode(snack, snackSize);
            assert 3 * wallMargins < windowSize: "Make sure 3 * wallMargins < windowSize";
            // snack appears between (10, 10) and (90, 90) on a 100 x 100 window
            int randomx = random.nextInt(windowSize - 4 * wallMargins) + 2 * wallMargins;
            int randomy = random.nextInt(windowSize - 4 * wallMargins) + 2 * wallMargins;
            snack = new Vector2(randomx, randomy);
        }
        
        /* movement controls */   {
        if(keyPressed('W') && lastkeyPressed != 'S'){lastkeyPressed = 'W';}
        if(keyPressed('S') && lastkeyPressed != 'W'){lastkeyPressed = 'S';}
        if(keyPressed('A') && lastkeyPressed != 'D'){lastkeyPressed = 'A';}
        if(keyPressed('D') && lastkeyPressed != 'A'){lastkeyPressed = 'D';}
        if(lastkeyPressed == 'W'){headPosition.y += 0.5;}
        if(lastkeyPressed == 'S'){headPosition.y -= 0.5;}
        if(lastkeyPressed == 'A'){headPosition.x -= 0.5;}
        if(lastkeyPressed == 'D'){headPosition.x += 0.5;}
        }
        
    }
    
    public static void main(String[] args){
        App app = new SnakeGame();
        app.setWindowBackgroundColor(Vector3.black);

        app.setWindowSizeInWorldUnits(windowSize, windowSize);
        app.setWindowCenterInWorldUnits(windowSize/2, windowSize/2);

        app.setWindowHeightInPixels(512);
        app.setWindowTopLeftCornerInPixels(16, 16);
        
        app.run(); 
    }
    
}