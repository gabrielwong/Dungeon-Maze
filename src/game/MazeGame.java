package game;

import game.buff.BuffManager;
import game.inventory.Inventory;
import game.items.Item;
import game.map.Map;
import game.map.Obstacle;
import game.map.Portal;
import game.skill.SkillManager;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import maze.Maze;

/** Contains the game's logic and functionality */
public class MazeGame implements MouseListener, MouseMotionListener, KeyListener, Serializable {
 private Player player;

 private BuffManager buffManager;
 private Inventory inventory;
 private SkillManager skillManager;

 private boolean useMask = true; // if mask should be drawn
 private boolean isPlayerInvincible = false;

 private Map map;
 private transient BufferedImage finalImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

 private int difficultyLevel = 1;

 // Boolean of button information
 private transient boolean northPressed = false, southPressed = false, eastPressed = false, westPressed = false, portalWanted = false;

 private int cornerX, cornerY; // x and y coordinate of the top left corner of the image to be displayed
 private int width, height; // the width/height of the output image

 public static final int DAMAGE_DELAY = 500; // invincibility period of player after being hit
 private long lastDamaged = System.currentTimeMillis(); // time the player was last damaged

 public static final int PROJECTILE_DELAY = 250; // delay in ms between projectile launches
 private long lastAttack = System.currentTimeMillis(); // time the player last show something

 private boolean isTimeFrozen = false;

 private long lastUpdate = System.currentTimeMillis(); // last time the update method was called
 private static final int TIME_UNIT = 1000; // time is taken in 1 / TIME_UNITS

 private transient boolean isDrawing = false; // if it is currently drawing a new frame
 private transient AbstractBar[] bars; // array of health, exp and mana bars

 private int toolTipObject = -1; // the index of the object that the mouse is hovering over
 private long toolTipLastUpdate = System.currentTimeMillis(); // last the the tooltip text was set
 private static final int TOOL_TIP_DELAY = 250; // the amount of time in ms before the tooltip text pops up
 private static final int MAX_TEXT_WIDTH = 150;
 public ArrayList<ClickableObject> mouseObjects = new ArrayList<ClickableObject>(); // all the clickable objects
 private int mouseX; // x position of mouse
 private int mouseY; // y position of mouse

 private long[] pastTimes; // array of past times between frames
 private long lastDraw = System.currentTimeMillis(); // last draw time
 private int lastDrawIndex = 0; // index in array of last time set
 private static final int NUM_TIME_SAMPLES = 20; // number of times to take the average of

 public static final int DISPLAY_HEIGHT = 100; // height of the stuff at the bottom
 protected static final int BAR_HEIGHT = 20; // height of health, mana and exp bars
 protected static final int BAR_GAP = 15; // gap between bars
 protected static final int LEVEL_INFO_WIDTH = 75; // Width of area to display level
 protected static final double WINDOW_MOVE_MULTIPLIER = 0.3; // This multiplied by width and length is the distance from edge of screen before
 // moving window

 protected static final int STACKED_DISTANCE_VARIATION = 3;
 private static final Rectangle NULL_RECT = new Rectangle(-1, -1, 0, 0);

 private boolean soundOn; // toggle sound
 //private static AudioClip dieSound = Applet.newAudioClip(Item.class.getResource("/res/aud/haloDie.wav")); // sound effect for dying

 public MazeGame() {
  this(0);
 }

 public MazeGame(int difficultyLevel) {
  this.difficultyLevel = difficultyLevel;
  player = new Player();
  soundOn = true;

  createBars();

  pastTimes = new long[NUM_TIME_SAMPLES];
  for (int i = 0; i < NUM_TIME_SAMPLES; i++)
   pastTimes[i] = 0L;

  buffManager = new BuffManager(this);
  inventory = new Inventory(this);
  inventory.setY(height - DISPLAY_HEIGHT + BAR_HEIGHT + 2 * BAR_GAP);
  skillManager = new SkillManager(this);
  skillManager.setY(height - DISPLAY_HEIGHT + BAR_HEIGHT + 2 * BAR_GAP);
  skillManager.setX(200);
  setSize(1000, 700);
 }

 /** This will initialize this object properly by handling things that aren't serializable */
 private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
  mouseObjects = new ArrayList<ClickableObject>();
  buffManager = new BuffManager(this);
  inventory = new Inventory(this);
  skillManager = new SkillManager(this);

  in.defaultReadObject();
  createBars();

  inventory.setY(height - DISPLAY_HEIGHT + BAR_HEIGHT + 2 * BAR_GAP);
  skillManager.setY(height - DISPLAY_HEIGHT + BAR_HEIGHT + 2 * BAR_GAP);
  skillManager.setX(200);
  setSize(1000, 700);
 }

 /** Checks if sound is on */
 public boolean isSoundOn() {
  return soundOn;
 }

 /** Check if the player has reached the end point (bottom right) */
 private void checkFinish() {
  // check if the end square is reached
  if (difficultyLevel != 15 && player.getX() / Map.SQUARE_SIZE == map.getColumns() - 1 && player.getY() / Map.SQUARE_SIZE == map.getRows() - 1) {
   player.addExp(50);
   createNewLevel();
  } else if (difficultyLevel == 15 && player.getX() / Map.SQUARE_SIZE == map.getColumns() - 1
    && player.getY() / Map.SQUARE_SIZE == map.getRows() - 1)
   displayEndScreen();
  else if (!player.isAlive()) displayDeadScreen();
 }

 /** Create a new level */
 public void createNewLevel() {
  difficultyLevel++;

  int columns = (int) (difficultyLevel * 4 + 7); // creates the maze based on difficulty
  int rows = (int) (difficultyLevel * 2 + 7);

  map = new Map(columns, rows, difficultyLevel, (double) 1 / (difficultyLevel + 1.3) + 0.1, 1, (int) ((1.5 * difficultyLevel) * 5),
    difficultyLevel); // col,
  // row,
  // door,
  // item,
  // portal
  map.addLight(player);
  map.redraw();

  player.setX(0);
  player.setY(0);
 }

 /** Run the ending screen, ask if player wants to retry */
 private void displayEndScreen() {
  // TODO add in game over image
 }

 /** Run the death screen, ask if player wants to continue */
 private void displayDeadScreen() {
  // TODO add in you lose image
  // temporary dialog box
  JOptionPane.showMessageDialog(null, "You died! Click okay to try again. ", "", JOptionPane.PLAIN_MESSAGE);
  difficultyLevel--;
  // TODO clear all items that are currently in use
  player.revive();
  createNewLevel();
 }

 /**
  * Draws the current state of the game on to the given graphics context.
  * 
  * @param g the graphics context to draw on
  */
 public void draw(Graphics2D g) {
  g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
  Rectangle windowArea = new Rectangle(0, 0, width, height - DISPLAY_HEIGHT);
  Rectangle drawArea = new Rectangle(cornerX, cornerY, cornerX + width, cornerY + height - DISPLAY_HEIGHT);
  g.drawImage(
    map.mazeImage.getSubimage(cornerX, cornerY, Math.min(width, map.mazeImage.getWidth() - cornerX),
      Math.min(height - DISPLAY_HEIGHT, map.mazeImage.getHeight() - cornerY)), 0, 0, null); // draw the walls and paths
  g.setClip(windowArea);
  g.translate(-cornerX, -cornerY);
  // TODO make it show how much it takes to draw each part for optimization

  map.draw(g, drawArea);

  g.drawImage(player.getImage(), player.getX(), player.getY(), null); // draw players

  g.setClip(null);

  g.translate(cornerX, cornerY);
  map.drawMask(g, width, height - DISPLAY_HEIGHT, cornerX, cornerY, useMask);

  drawHud(g);

  pastTimes[lastDrawIndex] = System.nanoTime() - lastDraw;
  lastDrawIndex = (lastDrawIndex + 1) % NUM_TIME_SAMPLES;
  lastDraw = System.nanoTime();

  double fps = 0;
  for (int i = 0; i < NUM_TIME_SAMPLES; i++) {
   fps += pastTimes[i];
  }
  fps = 1000000000 / fps * NUM_TIME_SAMPLES;

  DecimalFormat f = new DecimalFormat("#.#");
  g.setColor(Color.BLACK);
  g.drawString("FPS: " + f.format(fps), 0, getHeight());
  g.drawImage(buffManager.getImage(), 0, 0, null); // draw buffer image
 }

 private void drawHud(Graphics2D g) {
  g.setColor(Color.WHITE);
  g.fillRect(0, height - DISPLAY_HEIGHT, width, DISPLAY_HEIGHT);

  int barTop = height - DISPLAY_HEIGHT + BAR_GAP;

  g.setFont(new Font("segoe ui", Font.BOLD, BAR_HEIGHT));
  outlineText(g, "Lv. " + player.getLevel(), BAR_GAP, barTop + BAR_HEIGHT, Color.WHITE, Color.BLACK);
  for (int i = 0; i < bars.length; i++) {
   g.drawImage(bars[i].getImage(), bars[i].getX(), bars[i].getY(), null);
  }
  g.drawImage(inventory.getImage(), inventory.getX(), inventory.getY(), null); // draw images of the inventory and skills at the bottom
  g.drawImage(skillManager.getImage(), skillManager.getX(), skillManager.getY(), null);
 }

 public static void outlineText(Graphics2D g, String text, int x, int y, Color textColor, Color outlineColor) {
  g.setColor(outlineColor);
  g.drawString(text, x - 1, y);
  g.drawString(text, x + 1, y);
  g.drawString(text, x, y - 1);
  g.drawString(text, x, y + 1);
  g.setColor(textColor);
  g.drawString(text, x, y);
 }

 /** Updates the components in the program */
 public void update() {
  int time = (int) (System.currentTimeMillis() - lastUpdate);
  if (time == 0) return;
  lastUpdate = System.currentTimeMillis();
  processProjectiles(time); // processes each component
  processMonsters(time);
  processObstacles(time);
  processPlayer(time);

  /* unless you can think of a better way, portal checking must be done all at once.
   * We can also just keep an ArrayList of moveable objects if we want instead of
   * converting here.
   * projectiles will still go through even if portals are not wanted */
  /* int offset = portalWanted ? 1 : 0;
   * MoveableObject[] MO = new MoveableObject[offset + map.projectiles.size()];
   * for (int i = offset; i < MO.length; i++)
   * MO[i] = map.projectiles.get(i - offset);
   * if (portalWanted) MO[0] = player;
   * 
   * map.checkForPortals(MO); */

  map.checkForPortals(portalWanted ? player : null, this);
  tryUpdateImage();
  checkFinish(); // check if you reach the end of the maze
 }

 /** Updates the player's characteristics */
 private void processPlayer(int time) {
  // TODO player death
  player.tryRegen();

  if (northPressed || southPressed || westPressed || eastPressed) {
   player.setIsMoving(true);
   Rectangle[] monsterBounds = new Rectangle[map.monsters.size()];
   for (int i = 0; i < map.monsters.size(); i++) {
    monsterBounds[i] = map.monsters.get(i).getBounds();
   }
   if (northPressed) {
    map.move(player, Maze.NORTH, getDistance(player.getSpeed(), time), monsterBounds);
    player.setDirection(Maze.NORTH);
   }
   if (southPressed) {
    map.move(player, Maze.SOUTH, getDistance(player.getSpeed(), time), monsterBounds);
    player.setDirection(Maze.SOUTH);
   }
   if (westPressed) {
    map.move(player, Maze.WEST, getDistance(player.getSpeed(), time), monsterBounds);
    player.setDirection(Maze.WEST);
   }
   if (eastPressed) {
    map.move(player, Maze.EAST, getDistance(player.getSpeed(), time), monsterBounds);
    player.setDirection(Maze.EAST);
   }
  } else {
   player.setIsMoving(false);
  }

  map.checkForItems(player, this);
  calculateWindow();
 }

 /**
  * Moves all projectiles.
  */
 private void processProjectiles(int time) {
  Rectangle[] obstacleBounds = map.getUserObstacleBounds();
  for (int i = 0; i < map.projectiles.size(); i++) {
   Projectile p = map.projectiles.get(i);
   // If it has crashed last update (collision image shown), remove it
   if (p.shouldRemove()) {
    map.projectiles.remove(i);
    map.removeLight(p);
    continue;
   }

   if (!p.isCollided()) { // if it hits something
    boolean hasCrashed = !map.move(p, p.getDirection(), getDistance(p.getSpeed(), time), obstacleBounds);

    // Make collision image show and set up for removal next update
    if (hasCrashed) p.setIsCollided(true);
   }
  }
 }

 /**
  * Checks and processes if any monster is collided with a player or a projectile.
  * Also moves the monsters.
  */
 private void processMonsters(int time) {
  /* Array of obstacles. obstacles[i] corresponds with monsters.get(i).
   * The last item in obstacles is the player. */
  Rectangle[] monsterBounds = new Rectangle[map.monsters.size() + 1];
  for (int i = 0; i < map.monsters.size(); i++) {
   monsterBounds[i] = map.monsters.get(i).getProtectedArea();
  }
  monsterBounds[monsterBounds.length - 1] = player.getBounds();
  Rectangle[] obstacleBounds = map.getUserObstacleBounds();

  for (int i = 0; i < map.monsters.size(); i++) {
   Monster m = map.monsters.get(i);

   // Move monster toward player
   if (!isTimeFrozen) {
    int deltaX = m.getX() - player.getX();
    int deltaY = m.getY() - player.getY();

    int distance = getDistance(m.getSpeed(), time);

    monsterBounds[i] = NULL_RECT;

    if (deltaX < 0) {
     map.move(m, Maze.EAST, Math.min(-deltaX, distance), monsterBounds, obstacleBounds);
     m.setDirection(Maze.EAST);
    } else {
     map.move(m, Maze.WEST, Math.min(deltaX, distance), monsterBounds, obstacleBounds);
     m.setDirection(Maze.WEST);
    }

    if (deltaY > 0) {
     map.move(m, Maze.NORTH, Math.min(deltaY, distance), monsterBounds, obstacleBounds);
     if (Math.abs(deltaX) < deltaY) m.setDirection(Maze.NORTH);
    } else {
     map.move(m, Maze.SOUTH, Math.min(-deltaY, distance), monsterBounds, obstacleBounds);
     if (Math.abs(deltaX) < -deltaY) m.setDirection(Maze.SOUTH);
    }
    monsterBounds[i] = m.getProtectedArea(); // update the protected area
    m.setIsMoving(true);
   } else {
    m.setIsMoving(false);
   }

   if (!isPlayerInvincible) { // monsters attack
    // Check for collision with player
    if (player.intersects(m.getBounds(1))) {
     if (System.currentTimeMillis() > DAMAGE_DELAY + lastDamaged) {
      // Damage the player
      // TODO hit animation
      player.damage((int) (m.getAttack() * player.getDefenceMultiplier()));
      map.move(player, m.getDirection(), m.getKnockback(), (Rectangle[][]) null);
      lastDamaged = System.currentTimeMillis();
     }
    }
   }

   for (int j = 0; j < map.obstacles.size(); j++) { // can't cross obstacles
    Obstacle obstacle = map.obstacles.get(j);
    if (obstacle.intersects(m.getBounds(1))) {
     obstacle.damage(m.getAttack());
     if (!obstacle.isAlive()) {
      map.obstacles.remove(j);
      map.lights.remove(obstacle);
      j--;
     }
    }
   }

   // Check for collision with projectiles
   // TODO glitches sometimes when projectile speed + monster speed > monster length + projectile length
   for (int j = 0; j < map.projectiles.size(); j++) {
    Projectile p = map.projectiles.get(j);
    if (!p.isCollided()) {
     if (m.intersects(p)) {
      // Damage the monster
      m.damage((int) (p.getAttack() * player.getAttackMultiplier()));
      p.setIsCollided(true);

      // Set projectile to be hitting monster at right spot
      switch (p.getDirection()) {
      case Maze.NORTH:
       p.setY(m.getY() + m.getHeight() - p.getHeight() / 2);
       break;
      case Maze.SOUTH:
       p.setY(m.getY() + p.getHeight() / 2);
       break;
      case Maze.EAST:
       p.setX(m.getX() + m.getWidth() - p.getWidth() / 2);
       break;
      case Maze.WEST:
       p.setX(m.getX() + p.getWidth() / 2);
      }

      int knockback = !isTimeFrozen ? p.getKnockback() - getDistance(m.getSpeed(), time) : p.getKnockback();
      map.move(m, p.getDirection(), knockback, monsterBounds);
     }
    }
   }

   m.tryRegen();

   // Remove (kill) monster if it is dead
   if (!m.isAlive()) {
    // dieSound.play();
    map.monsters.remove(i);
    player.addExp(m.getExp());
    monsterBounds[i] = NULL_RECT;
   }
  }
 }

 private void processObstacles(int time) {
  for (int j = 0; j < map.obstacles.size(); j++) { // decrease the health of it
   Obstacle obstacle = map.obstacles.get(j);
   obstacle.damage(5);
  }
 }

 /**
  * Performs an attack
  * 
  * @deprecated
  */
 /* private void doPlayerAttack() {
  * Projectile p = new Projectile();
  * if (lastAttack + PROJECTILE_DELAY <= System.currentTimeMillis() && p.getCost() <= player.getMana()) {
  * lastAttack = System.currentTimeMillis();
  * 
  * player.shoot(p); // shoots a fireball
  * //p.playSoundEffect();
  * 
  * map.projectiles.add(p);
  * map.addLight(p);
  * player.drain(p.getCost());
  * }
  * } */

 /** Deploys an item in the space in front of the player */
 public void deployObstacle(Obstacle obstacle) {
  obstacle.setX(player.getX() + (player.getWidth() - obstacle.getWidth()) / 2);
  obstacle.setY(player.getY() + (player.getHeight() - obstacle.getHeight()) / 2);

  map.obstacles.add(obstacle);
  map.addLight(obstacle);
 }

 public void placePortal(boolean isBlue) {
  int index = isBlue ? 0 : 1;
  // Create portal if there isn't one
  if (map.portals[index] == null) {
   map.portals[index] = new Portal(!isBlue);

   // Set end portals
   int otherIndex = isBlue ? 1 : 0;
   if (map.portals[otherIndex] != null) {
    map.portals[0].setEndPortal(map.portals[1]);
    map.portals[1].setEndPortal(map.portals[0]);
   }
  }

  Portal p = map.portals[index];

  int hPadding = (Map.SQUARE_SIZE - p.getWidth()) / 2;
  int vPadding = (Map.SQUARE_SIZE - p.getHeight()) / 2;
  p.setX(player.getX() / Map.SQUARE_SIZE * Map.SQUARE_SIZE + hPadding);
  p.setY(player.getY() / Map.SQUARE_SIZE * Map.SQUARE_SIZE + vPadding);

  if (isSoundOn()) {
   if (isBlue) // play the respective sounds
    Portal.blueSound.play();
   else
    Portal.orangeSound.play();
  }
 }

 private void calculateWindow() {
  int height = this.height - DISPLAY_HEIGHT;
  // Calculate x of window
  if (width < map.getWidth()) {
   int windowX = player.getX() - cornerX; // X coordinate of player in window
   if (windowX < WINDOW_MOVE_MULTIPLIER * width) {
    cornerX = Math.max(0, (int) (player.getX() - WINDOW_MOVE_MULTIPLIER * width));
   } else if (windowX > width - WINDOW_MOVE_MULTIPLIER * width) {
    cornerX = Math.min(map.getWidth() - width, (int) (player.getX() - width + WINDOW_MOVE_MULTIPLIER * width));
   }
  }

  // Calculate y of window
  if (height < map.getHeight()) {
   int windowY = player.getY() - cornerY; // Y coordinate of player in window
   if (windowY < WINDOW_MOVE_MULTIPLIER * height) {
    cornerY = Math.max(0, (int) (player.getY() - WINDOW_MOVE_MULTIPLIER * height));
   } else if (windowY > height - WINDOW_MOVE_MULTIPLIER * height) {
    cornerY = Math.min(map.getHeight() - height, (int) (player.getY() - height + WINDOW_MOVE_MULTIPLIER * height));
   }
  }
 }

 private int getDistance(int speed, int time) {
  return speed * time / TIME_UNIT;
 }

 public Player getPlayer() {
  return player;
 }

 public void tryUpdateImage() {
  if (!isDrawing) {
   isDrawing = true;
   new Thread(new Runnable(){
    public void run() {
     BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
     draw(image.createGraphics());
     finalImage = image;
     isDrawing = false;
    }
   }).start();
  }
 }

 public BufferedImage getImage() {
  if (toolTipObject != -1 && System.currentTimeMillis() > toolTipLastUpdate + TOOL_TIP_DELAY) {
   String[] text = mouseObjects.get(toolTipObject).getToolTipText();
   if (text != null) {
    BufferedImage image = new BufferedImage(finalImage.getWidth(), finalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.drawImage(finalImage, 0, 0, null); // draw maze

    // Set up for tooltip
    int maxWidth = -1;
    ArrayList<TextLayout> lines = new ArrayList<TextLayout>();
    for (String s : text) {
     AttributedCharacterIterator iterator = new AttributedString(s).getIterator();
     LineBreakMeasurer lbm = new LineBreakMeasurer(iterator, g.getFontRenderContext());
     lbm.setPosition(iterator.getBeginIndex());
     int endPosition = iterator.getEndIndex();

     while (lbm.getPosition() < endPosition) {
      TextLayout layout = lbm.nextLayout(MAX_TEXT_WIDTH);
      lines.add(layout);
      maxWidth = Math.max(maxWidth, (int) (layout.getBounds().getWidth() + 3));
     }
    }

    g.setColor(Color.BLACK);
    FontMetrics m = g.getFontMetrics();
    int outlineWidth = 1;
    int x = mouseX;
    int y = mouseY - m.getHeight() * lines.size();
    int width = maxWidth + 4 + outlineWidth * 2;
    int height = m.getHeight() * lines.size() + 4 + outlineWidth * 2;

    // Make tooltip within the screen
    if (x + width > this.width) x = mouseX - width;
    if (y - height < 0) y = mouseY;
    // Draw black background
    g.fillRect(x, y, width, height);
    g.setColor(Color.WHITE);
    // Draw each line
    for (int i = 0; i < lines.size(); i++)
     lines.get(i).draw(g, x + 2 + outlineWidth, y - m.getDescent() + 2 + outlineWidth - (-i - 1) * m.getHeight());
    // Draw white border
    g.drawRect(x + 1, y + 1, width - 3, height - 3);
    return image;
   }
  }
  return finalImage;
 }

 public void setPlayerSight(float playerSight) {
  player.setRadius(playerSight);
 }

 public int getWidth() {
  return width;
 }

 public int getHeight() {
  return height;
 }

 public void setSize(int width, int height) {
  this.width = width;
  this.height = height;
  updateBarSizes();
  inventory.setY(height - DISPLAY_HEIGHT + 2 * BAR_GAP + BAR_HEIGHT);
  skillManager.setY(height - DISPLAY_HEIGHT + 2 * BAR_GAP + BAR_HEIGHT);
  skillManager.setX(getWidth() - skillManager.getWidth());
 }

 @Override
 public void keyPressed(KeyEvent e) { // handle user key inputs
  switch (e.getKeyCode()) {
  case KeyEvent.VK_W:
   northPressed = true;
   break;
  case KeyEvent.VK_S:
   southPressed = true;
   break;
  case KeyEvent.VK_A:
   westPressed = true;
   break;
  case KeyEvent.VK_D:
   eastPressed = true;
   break;
  case KeyEvent.VK_P:
   portalWanted = true;
   break;
  case KeyEvent.VK_7: // hack
   int modifiers = e.getModifiers();
   int desiredMask = (InputEvent.CTRL_MASK | InputEvent.ALT_MASK | InputEvent.SHIFT_MASK);
   if ((modifiers & desiredMask) == desiredMask) {
    doCheatPrompt();
   }
   break;
  case KeyEvent.VK_J:  // toggle sound
   soundOn = !soundOn;
   break;
  }
  for (int i = 0; i < mouseObjects.size(); i++) {
   if (e.getKeyCode() == mouseObjects.get(i).getKeyCode()) mouseObjects.get(i).actionPerformed(e);
  }
 }

 @Override
 public void keyReleased(KeyEvent e) {
  switch (e.getKeyCode()) {
  case KeyEvent.VK_W:
   northPressed = false;
   break;
  case KeyEvent.VK_S:
   southPressed = false;
   break;
  case KeyEvent.VK_A:
   westPressed = false;
   break;
  case KeyEvent.VK_D:
   eastPressed = false;
   break;
  case KeyEvent.VK_P:
   portalWanted = false;
   break;
  }
 }

 @Override
 public void keyTyped(KeyEvent e) {}

 @Override
 public void mouseDragged(MouseEvent arg0) {}

 @Override
 public void mouseMoved(MouseEvent evt) { // for hovering over
  mouseX = evt.getX();
  mouseY = evt.getY();
  updateToolTipObject();
 }

 /** Updates the tool tip of the object the mouse is hovered over */
 public void updateToolTipObject() {
  for (int i = 0; i < mouseObjects.size(); i++) {
   ClickableObject obj = mouseObjects.get(i);
   if (mouseX >= obj.getX() && mouseX < obj.getX() + obj.getWidth() && mouseY >= obj.getY() && mouseY < obj.getY() + obj.getHeight()) {
    if (toolTipObject != i) {
     setToolTipObject(i);
    }
    return;
   }
  }
  setToolTipObject(-1);
 }

 @Override
 public void mouseClicked(MouseEvent evt) { // uses clickable objects
  mouseX = evt.getX();
  mouseY = evt.getY();
  for (int i = 0; i < mouseObjects.size(); i++) {
   ClickableObject obj = mouseObjects.get(i);
   if (mouseX >= obj.getX() && mouseX < obj.getX() + obj.getWidth() && mouseY >= obj.getY() && mouseY < obj.getY() + obj.getHeight()) {
    obj.actionPerformed(evt);
    return;
   }
  }
 }

 @Override
 public void mouseEntered(MouseEvent arg0) {}

 @Override
 public void mouseExited(MouseEvent arg0) {}

 @Override
 public void mousePressed(MouseEvent arg0) {}

 @Override
 public void mouseReleased(MouseEvent arg0) {}

 /** Creates Mana, Health, and EXP bars */
 private void createBars() {
  bars = new AbstractBar[3];
  bars[1] = new HealthBar(this);
  bars[2] = new ManaBar(this);
  bars[0] = new ExpBar(this);
  updateBarSizes();
  for (int i = 0; i < bars.length; i++) {
   mouseObjects.add(bars[i]);
  }
 }

 /** Updates the sizes */
 private void updateBarSizes() {
  int barTop = height - DISPLAY_HEIGHT + BAR_GAP;
  int barWidth = (width - LEVEL_INFO_WIDTH - 4 * BAR_GAP) / 3;
  for (int i = 0; i < bars.length; i++) {
   bars[i].setX(LEVEL_INFO_WIDTH + (i + 1) * BAR_GAP + i * barWidth);
   bars[i].setY(barTop);
   bars[i].setWidth(barWidth);
  }
 }

 public void setToolTipObject(int toolTipObject) {
  this.toolTipObject = toolTipObject;
  if (toolTipObject != -1) toolTipLastUpdate = System.currentTimeMillis();
 }

 public boolean isTimeFrozen() { // returns if time is frozen due to special item
  return isTimeFrozen;
 }

 public void setIsTimeFrozen(boolean isTimeFrozen) {
  this.isTimeFrozen = isTimeFrozen;
 }

 public BuffManager getBuffManager() {
  return buffManager;
 }

 public Inventory getInventory() {
  return inventory;
 }

 private void doCheatPrompt() { // hack
  String input = JOptionPane.showInputDialog(null);
  if ("yc with contacts".equals(input)) {
   useMask = !useMask;
  } else if ("17".equals(input)) {
   player.addExp(171717);
   player.setMaxHealth(171717);
   player.setMaxMana(171717);
   player.setHealth(171717);
   player.setMana(171717);
  } else if ("goh yun".equals(input)) {
   isPlayerInvincible = !isPlayerInvincible;
  } else if ("destroyer".equals(input)) {
   map.monsters.clear();
  } else if ("swat team".equals(input)) {
   for (int i = 0; i < map.doors.size(); i++) {
    map.doors.get(i).setClosed(false);
   }
  } else if ("supchut".equals(input)) {
   for (int i = 0; i < Inventory.NUM_ITEMS; i++) {
    inventory.getItem(i).setQuantity(1717);
   }
  } else if ("rage quit".equals(input)) {
   createNewLevel();
  }
 }

 public static MazeGame load(String file) {
  System.out.println("loading " + file);
  MazeGame mazeGame;
  FileInputStream fis = null;
  ObjectInputStream in = null;
  try {
   fis = new FileInputStream(file);
   in = new ObjectInputStream(fis);
   mazeGame = (MazeGame) in.readObject();
   in.close();
  } catch (Exception e) {
   mazeGame = new MazeGame();
   System.out.println("couldn't load");
  }
  mazeGame.getMap().redraw();
  return mazeGame;
 }

 public Map getMap() {
  return map;
 }

 public void save(String file) {
  System.out.println("saving " + file);
  FileOutputStream fos = null;
  ObjectOutputStream out = null;
  try {
   fos = new FileOutputStream(file);
   out = new ObjectOutputStream(fos);
   out.writeObject(this);
   out.close();
  } catch (IOException ex) {
   ex.printStackTrace();
  }
 }

}
