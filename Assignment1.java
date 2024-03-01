import java.io.*;
import java.util.*;

/**
 * @param <K> first value of the pair
 * @param <V> second value of the pair
 */
class Pair<K, V>{
    public K key;
    public V value;

    public Pair(K key, V value){
        this.key = key;
        this.value = value;
    }
}

/**
 * interface that gives an opportunity to sign alias to classes which are inherit this one
 */
interface Detectable{
    public char getType();
}

/**
 * Cell booker with non-empty perceptions
 */
abstract class Actor implements Detectable{
    public int x, y;
    public final ArrayList<Pair<Integer, Integer>> perceptions;

    /** constructor
     * @param x x-coordinate
     * @param y y-coordinate
     * @param perceptions perception-list
     */
    public Actor(int x, int y, ArrayList<Pair<Integer, Integer>> perceptions){
        this.x = x;
        this.y = y;
        this.perceptions = perceptions;
    }

    /**
     * @param actor object for comparing
     * @return if given actor equal to this one
     */
    public boolean equalNode(Actor actor){
        return x == actor.x && y == actor.y;
    }
    public boolean equalNode(Item item){
        return x == item.x && y == item.y;
    }

    /** check if given point in perception zone
     * @param x_ x-coordinate
     * @param y_ y-coordinate
     * @return if point in the perception zone
     */
    public boolean inPerception(int x_, int y_){
        for(var step : perceptions)
            if((x + step.key) == x_ && (y + step.value) == y_)
                return true;
        return false;
    }

}

/**
 * cell booker without perceptions
 */
abstract class Item implements Detectable{
    public int x, y;

    /** constructor
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Item(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * @param actor object to compare
     * @return is given object is equal to this one
     */
    public boolean equalNode(Actor actor){
        return x == actor.x && y == actor.y;
    }
    public boolean equalNode(Item item){
        return x == item.x && y == item.y;
    }
}


class Jack extends Actor{
    /**
     * @param x x-coordinate
     * @param y y-coordinate
     * @param perceptions perception-list
     */
    public Jack(int x, int y, ArrayList<Pair<Integer, Integer>> perceptions){
        super(x, y, perceptions);
    }

    @Override
    public char getType() {
        return 'J';
    }
}

class Davy extends Actor{
    /**
     * @param x x-coordinate
     * @param y y-coordinate
     * @param perceptions perception-list
     */
    public Davy(int x, int y, ArrayList<Pair<Integer, Integer>> perceptions){
        super(x, y, perceptions);
    }

    @Override
    public char getType() {
        return 'D';
    }
}

class Kraken extends Actor{
    /**
     * @param x x-coordinate
     * @param y y-coordinate
     * @param perceptions perception-list
     */
    public Kraken(int x, int y, ArrayList<Pair<Integer, Integer>> perceptions){
        super(x, y, perceptions);
    }

    @Override
    public char getType() {
        return 'K';
    }
}

class Tortuga extends Item{
    /**
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Tortuga(int x, int y){
        super(x, y);
    }

    @Override
    public char getType() {
        return 'T';
    }
}

class Rock extends Item{
    /**
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Rock(int x, int y){
        super(x, y);
    }

    @Override
    public char getType() {
        return 'R';
    }
}

class Chest extends Item{
    /**
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public Chest(int x, int y){
        super(x, y);
    }

    @Override
    public char getType() {
        return 'C';
    }
}

class Node implements Comparable<Node>{
    public boolean freeToGo;
    public int h, g, dangerCnt = 0;
    List<Character> objects = new ArrayList<>();
    public int x, y;
    public Node parent = null;

    /**
     * @param x x-coordinate
     * @param y y-coordinate
     * @param freeToGo if we can go to this cell
     */
    public Node(int x, int y, boolean freeToGo){
        this.x = x;
        this.y = y;
        this.freeToGo = freeToGo;
    }

    public int getF(){
        return g + h;
    }

    @Override
    public int compareTo(Node node){
        if(this.getF() == node.getF())
            return Integer.compare(this.g, node.g);
        return Integer.compare(this.getF(), node.getF());
    }
}

class Field{
    Pair<Integer, Integer> up = new Pair<>(1, 0);
    Pair<Integer, Integer> down = new Pair<>(-1, 0);
    Pair<Integer, Integer> right = new Pair<>(0, 1);
    Pair<Integer, Integer> left = new Pair<>(0, -1);
    Pair<Integer, Integer> up_right = new Pair<>(1, 1);
    Pair<Integer, Integer> up_left = new Pair<>(1, -1);
    Pair<Integer, Integer> down_right = new Pair<>(-1, 1);
    Pair<Integer, Integer> down_left = new Pair<>(-1, -1);
    Pair<Integer, Integer> up_up = new Pair<>(2, 0);
    Pair<Integer, Integer> down_down = new Pair<>(-2, 0);
    Pair<Integer, Integer> right_right = new Pair<>(0, 2);
    Pair<Integer, Integer> left_left = new Pair<>(0, -2);
    public Jack jack;
    public Davy davy;
    public Kraken kraken;
    public Rock rock;
    public Chest chest;
    public Tortuga tortuga;
    public char [][]scheme = new char[9][9];

    /**
     * @param field class which represents map
     */
    public Field(Field field){
        this.jack = field.jack;
        this.kraken = field.kraken;
        this.tortuga = field.tortuga;
        this.scheme = field.scheme;
        this.rock = field.rock;
        this.chest = field.chest;
        this.davy = field.davy;
    }
    public Field(){
        super();
    }

    /**
     * @param initial first attempt of generation map or not
     * @throws IOException if file is null
     */
    
    public void gen(boolean initial) throws IOException {
        try (Scanner sc = new Scanner(System.in)) {
            String inputType = "1";
            if(initial){
                System.out.println("Print 1 - for console input; 2 - for file input");
                inputType = sc.nextLine();
                while(!(inputType.equals("1") || inputType.equals("2"))){
                    System.out.println("Invalid input type input. Try again");
                    inputType = sc.nextLine();
                }
            }
            int scenario;
            String str;
            if(inputType.equals("1")){
                str = sc.nextLine();
                scenario = sc.nextInt();
            }
            else{
                BufferedReader in = new BufferedReader(new FileReader("input.txt"));
                str = in.readLine();
                scenario = Integer.parseInt(in.readLine());
                in.close();
            }
            ArrayList<Pair<Integer, Integer>> p0 =  new ArrayList<>(Arrays.asList(up, down, right, left, up_right, up_left, down_right, down_left));
            ArrayList<Pair<Integer, Integer>> p1 = null;
            if(scenario == 1)
                p1 = new ArrayList<>(Arrays.asList(up, down, right, left, up_right, up_left, down_right, down_left));
            else if(scenario == 2)
                p1 = new ArrayList<>(Arrays.asList(up, down, right, left, up_right, up_left, down_right, down_left, up_up, down_down, right_right, left_left));
            ArrayList<Pair<Integer, Integer>> p2 = new ArrayList<>(Arrays.asList(up, down, right, left));
            jack = new Jack(Integer.parseInt(String.valueOf(str.charAt(1))), Integer.parseInt(String.valueOf(str.charAt(3))), p1);
            davy = new Davy(Integer.parseInt(String.valueOf(str.charAt(7))), Integer.parseInt(String.valueOf(str.charAt(9))), p0);
            kraken = new Kraken(Integer.parseInt(String.valueOf(str.charAt(13))), Integer.parseInt(String.valueOf(str.charAt(15))), p2);
            rock = new Rock(Integer.parseInt(String.valueOf(str.charAt(19))), Integer.parseInt(String.valueOf(str.charAt(21))));
            chest = new Chest(Integer.parseInt(String.valueOf(str.charAt(25))), Integer.parseInt(String.valueOf(str.charAt(27))));
            tortuga = new Tortuga(Integer.parseInt(String.valueOf(str.charAt(31))), Integer.parseInt(String.valueOf(str.charAt(33))));
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                scheme[i][j] = '.';
        ArrayList<Object> a = new ArrayList<>(Arrays.asList(jack, davy, kraken, rock, chest, tortuga));
        for(var x : a){
            if(x instanceof Jack){
                scheme[((Jack) x).x][((Jack) x).y] = ((Jack) x).getType();
                for(var step : ((Jack)x).perceptions){
                    int x_ = ((Jack)x).x + step.key, y_ = ((Jack)x).y + step.value;
                    if(x_ >= 0 && x_ < 9 && y_ >= 0 && y_ < 9)
                        scheme[x_][y_] = '.';
                }
            }
            else if(x instanceof Davy){
                scheme[((Davy) x).x][((Davy) x).y] = ((Davy) x).getType();
                for(var step : ((Davy)x).perceptions){
                    int x_ = ((Davy)x).x + step.key, y_ = ((Davy)x).y + step.value;
                    if(x_ >= 0 && x_ < 9 && y_ >= 0 && y_ < 9)
                        scheme[x_][y_] = '#';
                }
            }
            else if(x instanceof Kraken){
                scheme[((Kraken) x).x][((Kraken) x).y] = ((Kraken) x).getType();
                for(var step : ((Kraken)x).perceptions){
                    int x_ = ((Kraken)x).x + step.key, y_ = ((Kraken)x).y + step.value;
                    if(x_ >= 0 && x_ < 9 && y_ >= 0 && y_ < 9)
                        scheme[x_][y_] = '#';
                }
            }
            else if(x instanceof Rock){
                scheme[((Rock) x).x][((Rock) x).y] = ((Rock) x).getType();
            }
            else if(x instanceof Chest){
                scheme[((Chest) x).x][((Chest) x).y] = ((Chest) x).getType();
            }
            else if(x instanceof Tortuga){
                scheme[((Tortuga) x).x][((Tortuga) x).y] = ((Tortuga) x).getType();
            }
        }
    }

    /**
     * @return check if map is valid or not
     */
    public boolean isValid(){
        ArrayList<Object> a = new ArrayList<>(Arrays.asList(jack, davy, kraken, rock, chest, tortuga));
        for(int i = 0; i < a.size(); i++){
            for(int j = i + 1; j < a.size(); j++){
                if(a.get(i) instanceof Jack){
                    if(((a.get(j) instanceof Actor && ((Actor)a.get(i)).equalNode((Actor)a.get(j))) || (a.get(j) instanceof Item && ((Actor)a.get(i)).equalNode((Item) a.get(j)))) && !(a.get(j) instanceof Tortuga))
                        return false;
                }
                if(a.get(i) instanceof Davy){
                    if((a.get(j) instanceof Actor && ((Actor)a.get(i)).equalNode((Actor)a.get(j))) || (a.get(j) instanceof Item && ((Actor)a.get(i)).equalNode((Item) a.get(j))))
                        return false;
                }
                if((a.get(i) instanceof Kraken) && !(a.get(j) instanceof Rock)){
                    if(((a.get(j) instanceof Actor) && ((Actor)a.get(i)).equalNode((Actor)a.get(j))) || ((a.get(j) instanceof Item) && ((Actor)a.get(i)).equalNode((Item) a.get(j))))
                        return false;
                }
                if(a.get(i) instanceof Rock && !(a.get(j) instanceof Kraken)){
                    if((a.get(j) instanceof Actor && ((Item)a.get(i)).equalNode((Actor)a.get(j))) || (a.get(j) instanceof Item && ((Item)a.get(i)).equalNode((Item) a.get(j))))
                        return false;
                }
                if(a.get(i) instanceof Chest){
                    if(a.get(j) instanceof Actor && !(a.get(j) instanceof Jack) && ((Actor) a.get(j)).inPerception(((Chest) a.get(i)).x, ((Chest) a.get(i)).y))
                        return false;
                    if(a.get(j) instanceof Jack && ((Item)a.get(i)).equalNode((Actor)a.get(j)))
                        return false;
                    if(a.get(j) instanceof Tortuga && ((Item)a.get(i)).equalNode((Item) a.get(j)))
                        return false;
                }
                if(a.get(i) instanceof Tortuga){
                    if(a.get(j) instanceof Actor && !(a.get(j) instanceof Jack) && ((Actor) a.get(j)).inPerception(((Chest) a.get(i)).x, ((Chest) a.get(i)).y))
                        return false;
                    if(a.get(j) instanceof Chest && ((Item)a.get(i)).equalNode((Item)a.get(j)))
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * @param time consumed time
     * @param out stream to output
     * @throws IOException if file is null
     */
    public void print(double time, BufferedWriter out) throws IOException {
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++)
                out.write(scheme[i][j] + " ");
            out.write("\n");
        }
        out.write("time consumed: " + time + " ms\n");
    }
}





class aStar{
    public Field field;
    ArrayList<Object> characters;

    /**
     * @param field map to work with
     */
    public aStar(Field field){
        this.field = field;
        characters = new ArrayList<>(Arrays.asList(field.jack, field.davy, field.kraken, field.rock, field.chest, field.tortuga));
    }
    ArrayList<Node> nodes = new ArrayList<>();

    /**
     * fill nodes according to the algirthm
     */
    public void fillNodes(){
        ArrayList<Actor> u = new ArrayList<>(Arrays.asList(field.jack, field.davy, field.kraken));
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(field.scheme[i][j] == '.' || field.scheme[i][j] == 'C' || field.scheme[i][j] == 'T')
                    nodes.add(new Node(i, j, true));
                else
                    nodes.add(new Node(i, j, false));
                for(Actor actor: u){
                    if(!(actor instanceof Jack) && actor.inPerception(i, j))
                        nodes.get(9 * i + j).dangerCnt++;
                }
            }
        }
        for(var x: characters){
            if(x instanceof Actor)
                nodes.get(9 * ((Actor) x).x + ((Actor) x).y).objects.add(((Actor) x).getType());
            else if(x instanceof Item)
                nodes.get(9 * ((Item) x).x + ((Item) x).y).objects.add(((Item) x).getType());
        }
        nodes.get(9 * field.davy.x + field.davy.y).dangerCnt++;
        nodes.get(9 * field.kraken.x + field.kraken.y).dangerCnt++;
        nodes.get(9 * field.rock.x + field.rock.y).dangerCnt++;
    }

    /**
     * @param a first node
     * @param b second node
     * @return distance between them
     */
    public int distance(Node a, Node b){
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) - Math.min(Math.abs(a.x - b.x), Math.abs(a.y - b.y));
    }

    /**
     * @param startNode from where to start
     * @param endNode where to finish
     * @return shortest path between given nodes
     */
    public List<Node> getPath(Node startNode, Node endNode){
        List<Node> path = new ArrayList<>();
        Node cur = endNode;
        while(cur != startNode){
            path.add(cur);
            cur = cur.parent;
        }
        path.add(cur);
        Collections.reverse(path);
        return path;
    }

    /**
     * @param start from where to start
     * @param end where to finish
     * @param flag if tortuga is visited
     * @return shortest path between given pairs in nodes
     */
    public List<Node> findPathNodes(Pair<Integer, Integer> start, Pair<Integer, Integer> end, boolean flag){
        Node startNode = nodes.get(start.key * 9 + start.value);
        Node endNode = nodes.get(end.key * 9 + end.value);
        PriorityQueue<Node> open = new PriorityQueue<>(Node::compareTo);
        HashSet<Node> close = new HashSet<>();
        open.add(startNode);

        while(!open.isEmpty()){
            Node cur = open.poll();
            close.add(cur);
            if(cur == endNode)
                return getPath(startNode, endNode);
            List<Node> neighbours = new ArrayList<>();
            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2; j++){
                    if(i == 0 && j == 0) continue;
                    if(cur.x + i >= 0 && cur.x + i < 9 && cur.y + j >= 0 && cur.y + j < 9){
                        if(flag && nodes.get(9 * (cur.x + i) + (cur.y + j)).objects.contains('K')){
                            nodes.get(9 * (cur.x + i) + (cur.y + j)).objects.remove(Character.valueOf('K'));
                            if(--nodes.get(9 * (cur.x + i) + (cur.y + j)).dangerCnt == 0)
                                nodes.get(9 * (cur.x + i) + (cur.y + j)).freeToGo = true;
                            if(cur.x + i + 1 >= 0 && cur.x + i + 1 < 9 && cur.y + j >= 0 && cur.y + j < 9)
                                if(--nodes.get(9 * (cur.x + i + 1) + (cur.y + j)).dangerCnt == 0)
                                    nodes.get(9 * (cur.x + i + 1) + (cur.y + j)).freeToGo = true;
                            if(cur.x + i - 1 >= 0 && cur.x + i - 1 < 9 && cur.y + j >= 0 && cur.y + j < 9)
                                if(--nodes.get(9 * (cur.x + i - 1) + (cur.y + j)).dangerCnt == 0)
                                    nodes.get(9 * (cur.x + i - 1) + (cur.y + j)).freeToGo = true;
                            if(cur.x + i >= 0 && cur.x + i < 9 && cur.y + j + 1 >= 0 && cur.y + j + 1 < 9)
                                if(--nodes.get(9 * (cur.x + i) + (cur.y + j + 1)).dangerCnt == 0)
                                    nodes.get(9 * (cur.x + i) + (cur.y + j + 1)).freeToGo = true;
                            if(cur.x + i >= 0 && cur.x + i < 9 && cur.y + j - 1 >= 0 && cur.y + j - 1 < 9)
                                if(--nodes.get(9 * (cur.x + i) + (cur.y + j - 1)).dangerCnt == 0)
                                    nodes.get(9 * (cur.x + i) + (cur.y + j - 1)).freeToGo = true;
                        }
                        neighbours.add(nodes.get(9 * (cur.x + i) + (cur.y + j)));
                    }
                }
            }
            for(Node neighbour: neighbours){
                if(!neighbour.freeToGo || close.contains(neighbour)) continue;
                int move = cur.g + distance(neighbour, endNode);
                if(move < neighbour.g || !open.contains(neighbour)){
                    neighbour.g = move;
                    neighbour.h = distance(neighbour, endNode);
                    neighbour.parent = cur;
                    if(!open.contains(neighbour)) open.add(neighbour);
                }
            }
        }
        return null;
    }

    /**
     * @param pathNodes list of path in nodes
     * @return converted list of pairs-coordinates of nodes
     */
    public List<Pair<Integer, Integer>> convert(List<Node> pathNodes){
        List<Pair<Integer, Integer>> pathPairs = new ArrayList<>();
        if(pathNodes != null)
            for(Node node: pathNodes)
                pathPairs.add(new Pair<>(node.x, node.y));
        return pathPairs;
    }

    /**
     * @param start wehere to start
     * @param end where to finish
     * @return list of pairs as coordinates of the shortest path between given pairs
     */
    public List<Pair<Integer, Integer>> findPath(Pair<Integer, Integer> start, Pair<Integer, Integer> end){
        //instant death
        if(field.davy.inPerception(field.jack.x, field.jack.y) || field.kraken.inPerception(field.jack.x, field.jack.y))
            return convert(null);

        // Jack -> Chest : without Tortuga
        nodes.get(9 * field.tortuga.x + field.tortuga.y).freeToGo = false;
        List<Node> J_C = findPathNodes(start, end, false);

        // Jack -> Tortuga -> Chest
        nodes.get(9 * field.tortuga.x + field.tortuga.y).freeToGo = true;
        List<Node> J_T = findPathNodes(start, new Pair<>(field.tortuga.x, field.tortuga.y), false);
        if(J_T == null)
            return convert(J_C);
        List<Node> T_C = findPathNodes(new Pair<>(field.tortuga.x, field.tortuga.y), end, true);
        if(T_C == null)
            return convert(J_C);
        List<Node> J_T_C = new ArrayList<>(J_T);
        for(int i = 1; i < T_C.size(); i++)
            J_T_C.add(T_C.get(i));
        if(J_C == null){
            for(int i = 0; i < 9; i++)
                for(int j = 0; j < 9; j++)
                    if(nodes.get(9 * i + j).freeToGo && !nodes.get(9 * i + j).objects.contains('T') && !nodes.get(9 * i + j).objects.contains('C'))
                        field.scheme[i][j] = '.';
            return convert(J_T_C);
        }
        if(J_C.size() < J_T_C.size())
            return convert(J_C);
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                if(nodes.get(9 * i + j).freeToGo && !nodes.get(9 * i + j).objects.contains('T') && !nodes.get(9 * i + j).objects.contains('C'))
                    field.scheme[i][j] = '.';
        return convert(J_T_C);
    }

    /**
     * @param a where to start
     * @param b where to finish
     * @throws IOException if file is null
     */
    public void printPath(Pair<Integer, Integer> a, Pair<Integer, Integer> b) throws IOException {
        double startTime = System.nanoTime();
        fillNodes();
        List<Pair<Integer, Integer>> path = findPath(a, b);
        double endTime = System.nanoTime();
        BufferedWriter out = new BufferedWriter(new FileWriter("outputA*.txt"));
        if(path.isEmpty()){
            out.write("Lose\n");
            out.close();
            return;
        }
        out.write("Win\n");
        for(var x: path){
            if(field.scheme[x.key][x.value] != 'J' && field.scheme[x.key][x.value] != 'C')
                field.scheme[x.key][x.value] = '$';
        }
        out.write("RESULT = " + (path.size() - 1));
        out.write("\n");
        for(var x: path)
            out.write("[" + x.key + "," + x.value + "] ");
        out.write("\n");
        field.print(((endTime - startTime) * 1e-6), out);
        out.close();
    }

}


class BackTracking {
    public Field field;

    public boolean[][] isUsed = new boolean[9][9];
    public int [][] curRes = new int[9][9];

    public boolean isKrakenDead;
    public boolean isTortugaVisited;


    /**
     * @param field map repressentation
     */
    public BackTracking(Field field){
        this.field = field;
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                curRes[i][j] = Integer.MAX_VALUE;
        curRes[field.jack.x][field.jack.y] = 0;
        isKrakenDead = false;
        isTortugaVisited = (field.jack.x == field.tortuga.x && field.jack.y == field.tortuga.y);
    }

    /**
     * @param x x - coordinate
     * @param y y - coordinate
     * @param scheme - field
     * @param used - is this point already used
     * @param isKraken - is kraken dead
     * @return is this cell is free to go or not
     */
    public boolean canUse(int x, int y, char[][]scheme, boolean [][]used, boolean isKraken) {
        return (x >= 0 && x < 9 && y >= 0 && y < 9 && scheme[x][y] != 'D' && (scheme[x][y] != 'K' || isKraken) && scheme[x][y] != 'R' && !field.davy.inPerception(x, y) && (!field.kraken.inPerception(x, y) || isKraken) && !used[x][y]);
    }

    /**
     * @param x x - coordinate start
     * @param y y - coordinate start
     * @param x_end x - coordinate end
     * @param y_end y - coordinate end
     * @param min_res min found path
     * @param res current path
     * @param scheme field
     * @param isKraken is kraken dead
     * @param isTortuga is tortuga visited
     * @return path between given points
     */
    public int findPath(int x, int y, int x_end, int y_end, int min_res, int res, char [][]scheme, boolean isKraken, boolean isTortuga){
        if(x == x_end && y == y_end){
            min_res = Math.min(min_res, res);
            return min_res;
        }
        isUsed[x][y] = true;
        isTortugaVisited = (isTortuga || (x == field.tortuga.x && y == field.tortuga.y));
        for(int i = -1; i < 2; i += 2)
            for(int j = -1; j < 2; j += 2)
                if(x + i == field.kraken.x && y + j == field.kraken.y)
                    isKrakenDead = true;
        for(int i = -1; i < 2; i++)
            for(int j = -1; j < 2; j++)
                if(canUse(x + i, y + j, scheme, isUsed, isKrakenDead) && res < curRes[x + i][y + j]){
                    min_res = findPath(x + i, y + j, x_end, y_end, min_res, res + 1, field.scheme, isKrakenDead, isTortugaVisited);
                    curRes[x + i][y + j] = Math.min(curRes[x + i][y + j], res + 1);
                }
        isUsed[x][y] = false;
        isKrakenDead = isKraken;
        isTortugaVisited = isTortuga;
        return min_res;
    }

    public int findPathLen(int x, int y, int x_end, int y_end, boolean isKraken, boolean isTortuga){
        if(field.davy.inPerception(x, y) || field.kraken.inPerception(x, y))
            return -1;
        isUsed = new boolean[9][9];
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                isUsed[i][j] = false;
        int len = Integer.MAX_VALUE;
        len = findPath(x, y, x_end, y_end, len, 0, field.scheme, isKraken, isTortuga);
        if(len != Integer.MAX_VALUE)
            return len;
        return -1;
    }

    public void getPath(int x, int y, List<Pair<Integer, Integer>> path_){
        path_.add(new Pair<>(x, y));
        if(curRes[x][y] == 0)
            return;
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){
                if((x + i >= 0 && x + i < 9 && y + j >= 0 && y + j < 9) && curRes[x + i][y + j] == curRes[x][y] - 1){
                    getPath(x + i, y + j, path_);
                    return;
                }
            }
        }

    }
    public void printPath(int x, int y, int x_end, int y_end, boolean isKraken, boolean isTortuga) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("outputBackTracking.txt"));
        double startTime = System.nanoTime();
        int pathLen = findPathLen(x, y, x_end, y_end, isKraken, isTortuga);
        double endTime = System.nanoTime();
        double totalTIme = (endTime - startTime) * 1e-6;
        if(pathLen > -1){
            out.write("Win\nRESULT = " + pathLen + "\n");
            List<Pair<Integer, Integer>> path = new ArrayList<>();
            getPath(x_end, y_end, path);
            Collections.reverse(path);
            for(var cell: path){
                out.write("[" + cell.key + "," + cell.value + "] ");
                if(field.scheme[cell.key][cell.value] != 'J' && field.scheme[cell.value][cell.value] != 'C')
                    field.scheme[cell.key][cell.value] = '$';
            }
            out.write("\n");
            for(int i = 0; i < 9; i++){
                for(int j = 0; j < 9; j++)
                    out.write(field.scheme[i][j] + " ");
                out.write("\n");
            }
            out.write("Time consumed: " + totalTIme + "\n");
        }
        else{
            out.write("Lose\n");
        }
        out.close();
    }
}


public class Assignment1 {

    public static void main(String[] args) throws IOException {
        // field creation
        Field field = new Field();
        field.gen(true);
        while(!field.isValid()){
            System.out.println("Invalid map generation. Try again manually");
            field.gen(false);
        }
       Field field2 = new Field(field);
        //A* algorithm evaluating
        aStar astar = new aStar(field);
        astar.printPath(new Pair<>(field.jack.x, field.jack.y), new Pair<>(field.chest.x, field.chest.y));

        //BackTracking algorithm evaluating
        BackTracking backTrack = new BackTracking(field2);
        backTrack.printPath(field.jack.x, field.jack.y, field.chest.x, field.chest.y, false, (field.jack.x == field.tortuga.x && field.jack.y == field.tortuga.y)); 
        
    }

}
