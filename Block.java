package assignment3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.awt.Color;

import static assignment3.GameColors.BLOCK_COLORS;
import static assignment3.GameColors.FRAME_COLOR;
import static java.lang.Math.*;

public class Block {
 private int xCoord;
 private int yCoord;
 private int size; // height/width of the square
 private int level; // the root (outer most block) is at level 0
 private int maxDepth; 
 private Color color;

 private Block[] children; // {UR, UL, LL, LR}

 public static Random gen = new Random(2);
 
 
 /*
  * These two constructors are here for testing purposes. 
  */
 public Block() {}
 
 public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
  this.xCoord=x;
  this.yCoord=y;
  this.size=size;
  this.level=lvl;
  this.maxDepth = maxD;
  this.color=c;
  this.children = subBlocks;
 }
 
 

 /*
  * Creates a random block given its level and a max depth. 
  * 
  * xCoord, yCoord, size, and highlighted should not be initialized
  */
 public Block(int lvl, int maxDepth) {
  this.level = lvl;
  this.maxDepth = maxDepth;
  boolean split = gen.nextDouble() < exp(-0.25*level);
  if(lvl == maxDepth) split = false;
  if(split){
   Block[] blocks = new Block[4];
   blocks[0] = new Block(lvl+1, maxDepth);
   blocks[1] = new Block(lvl+1, maxDepth);
   blocks[2] = new Block(lvl+1, maxDepth);
   blocks[3] = new Block(lvl+1, maxDepth);
   this.children = blocks;
  } else{
   int colorNum = gen.nextInt(4);
   this.color = BLOCK_COLORS[colorNum];
   this.children = new Block[0];
  }
 }


 /*
  * Updates size and position for the block and all of its sub-blocks, while
  * ensuring consistency between the attributes and the relationship of the 
  * blocks. 
  * 
  *  The size is the height and width of the block. (xCoord, yCoord) are the 
  *  coordinates of the top left corner of the block. 
  */
 public void updateSizeAndPosition (int size, int xCoord, int yCoord) {
  if(size > 0 && size % 2 == 0){
   this.xCoord = xCoord;
   this.yCoord = yCoord;
   this.size = size;
   if(this.children.length != 0){
    this.children[0].updateSizeAndPosition(size/2, xCoord + size/2, yCoord);
    this.children[1].updateSizeAndPosition(size/2, xCoord, yCoord);
    this.children[2].updateSizeAndPosition(size/2, xCoord, yCoord + size/2);
    this.children[3].updateSizeAndPosition(size/2, xCoord+size/2, yCoord+size/2);
   }
  } else {
   throw new IllegalArgumentException("Invalid size input: " + size + " .\nCondition: size > 0 and size % 2 == 0");
  }
 }

 
 /*
  * Returns a List of blocks to be drawn to get a graphical representation of this block.
  * 
  * This includes, for each undivided Block:
  * - one BlockToDraw in the color of the block
  * - another one in the FRAME_COLOR and stroke thickness 3
  * 
  * Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  *  
  * The order in which the blocks to draw appear in the list does NOT matter.
  */

 public ArrayList<BlockToDraw> getBlocksToDraw() {
  ArrayList<BlockToDraw> blockToDraws = new ArrayList<>();
  if(this.children.length == 0){
    blockToDraws.add(new BlockToDraw(FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3));
    blockToDraws.add(new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0));
   return blockToDraws;
  } else{
   for (Block block : this.children){
    blockToDraws.addAll(block.getBlocksToDraw());
   }
   return blockToDraws;
  }
 }

 public BlockToDraw getHighlightedFrame() {
  return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
 }
 
 
 
 /*
  * Return the Block within this Block that includes the given location
  * and is at the given level. If the level specified is lower than 
  * the lowest block at the specified location, then return the block 
  * at the location with the closest level value.
  * 
  * The location is specified by its (x, y) coordinates. The lvl indicates 
  * the level of the desired Block. Note that if a Block includes the location
  * (x, y), and that Block is subdivided, then one of its sub-Blocks will 
  * contain the location (x, y) too. This is why we need lvl to identify 
  * which Block should be returned. 
  * 
  * Input validation: 
  * - this.level <= lvl <= maxDepth (if not throw exception)
  * - if (x,y) is not within this Block, return null.
  */
 public Block getSelectedBlock(int x, int y, int lvl) {
  if(lvl < this.level || lvl > maxDepth){
   throw new IllegalArgumentException("Invalid level when selecting block " + lvl);
  }

  if(this.level < lvl){
   if(this.children.length != 0) {
    for (Block block : this.children) {
     if (block.getSelectedBlock(x, y, lvl) != null) {
      return block.getSelectedBlock(x, y, lvl);
     }
    }
   }
  } else{
   if(x >= this.xCoord && x < this.xCoord + this.size && y >= this.yCoord && y < this.yCoord + this.size){
    return this;
   }
  }

  return null;
 }

 
 

 /*
  * Swaps the child Blocks of this Block. 
  * If input is 1, swap vertically. If 0, swap horizontally. 
  * If this Block has no children, do nothing. The swap 
  * should be propagate, effectively implementing a reflection
  * over the x-axis or over the y-axis.
  * 
  */
 public void reflect(int direction) {
  if(direction != 0 && direction != 1){
   throw new IllegalArgumentException("Invalid direction: " + direction);
  }
  if(this.children.length == 0) return;
//  if(direction == 1){
//   Block block0 = new Block();
//   block0.xCoord = this.children[0].xCoord;
//   block0.yCoord = this.children[0].yCoord;
//   block0.color = this.children[3].color;
//   block0.size = this.children[0].size;
//   block0.maxDepth = this.children[0].maxDepth;
//   block0.level = this.children[0].level;
//   block0.children = this.children[3].children;
//   //TODO 把子元素的位置改变
//
//   Block block3 = new Block();
//   block3.xCoord = this.children[3].xCoord;
//   block3.yCoord = this.children[3].yCoord;
//   block3.color = this.children[0].color;
//   block3.size = this.children[3].size;
//   block3.maxDepth = this.children[3].maxDepth;
//   block3.level = this.children[3].level;
//   block3.children = this.children[0].children;
//   //TODO 把子元素的位置改变
//
//   Block block2 = new Block();
//   block2.xCoord = this.children[2].xCoord;
//   block2.yCoord = this.children[2].yCoord;
//   block2.color = this.children[1].color;
//   block2.size = this.children[2].size;
//   block2.maxDepth = this.children[2].maxDepth;
//   block2.level = this.children[2].level;
//   block2.children = this.children[1].children;
//   //TODO 把子元素的位置改变
//
//
//   Block block1 = new Block();
//   block1.xCoord = this.children[1].xCoord;
//   block1.yCoord = this.children[1].yCoord;
//   block1.color = this.children[2].color;
//   block1.size = this.children[1].size;
//   block1.maxDepth = this.children[1].maxDepth;
//   block1.level = this.children[1].level;
//   block1.children = this.children[2].children;
//   //TODO 把子元素的位置改变
//
//   Block[] blocks = new Block[4];
//   blocks[0] = block0;
//   blocks[1] = block1;
//   blocks[2] = block2;
//   blocks[3] = block3;
//   this.children = blocks;
//   for(Block block : blocks){
//    block.reflect(direction);
//   }
//
//  } else{
//   Block block0 = new Block();
//   block0.xCoord = this.children[0].xCoord;
//   block0.yCoord = this.children[0].yCoord;
//   block0.color = this.children[1].color;
//   block0.size = this.children[0].size;
//   block0.maxDepth = this.children[0].maxDepth;
//   block0.level = this.children[0].level;
//   block0.children = this.children[1].children;
//   //TODO 把子元素的位置改变
//
//   Block block3 = new Block();
//   block3.xCoord = this.children[3].xCoord;
//   block3.yCoord = this.children[3].yCoord;
//   block3.color = this.children[2].color;
//   block3.size = this.children[3].size;
//   block3.maxDepth = this.children[3].maxDepth;
//   block3.level = this.children[3].level;
//   block3.children = this.children[2].children;
//   //TODO 把子元素的位置改变
//
//   Block block2 = new Block();
//   block2.xCoord = this.children[2].xCoord;
//   block2.yCoord = this.children[2].yCoord;
//   block2.color = this.children[3].color;
//   block2.size = this.children[2].size;
//   block2.maxDepth = this.children[2].maxDepth;
//   block2.level = this.children[2].level;
//   block2.children = this.children[3].children;
//   //TODO 把子元素的位置改变
//
//
//   Block block1 = new Block();
//   block1.xCoord = this.children[1].xCoord;
//   block1.yCoord = this.children[1].yCoord;
//   block1.color = this.children[0].color;
//   block1.size = this.children[1].size;
//   block1.maxDepth = this.children[1].maxDepth;
//   block1.level = this.children[1].level;
//   block1.children = this.children[0].children;
//   //TODO 把子元素的位置改变
//
//   Block[] blocks = new Block[4];
//   blocks[0] = block0;
//   blocks[1] = block1;
//   blocks[2] = block2;
//   blocks[3] = block3;
//   this.children = blocks;
//
//   for(Block block : blocks){
//    block.reflect(direction);
//   }
//  }
  if(direction == 1){
   this.children[0].turnDown();
   this.children[1].turnDown();
   this.children[2].turnUp();
   this.children[3].turnUp();
   Block tmp = this.children[0];
   this.children[0] = this.children[3];
   this.children[3] = tmp;
   tmp = this.children[1];
   this.children[1] = this.children[2];
   this.children[2] = tmp;
  } else{
   this.children[0].turnLeft();
   this.children[1].turnRight();
   this.children[2].turnRight();
   this.children[3].turnLeft();
   Block tmp = this.children[0];
   this.children[0] = this.children[1];
   this.children[1] = tmp;
   tmp = this.children[2];
   this.children[2] = this.children[3];
   this.children[3] = tmp;
  }

  for(Block block : this.children){
   block.reflect(direction);
  }
 }

 public void turnUp(){
  // y - size
  this.yCoord -= this.size;
  this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
 }

 public void turnDown(){
  // y + size
  this.yCoord += this.size;
  this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
 }

 public void turnLeft(){
  // x - size
  this.xCoord -= this.size;
  this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
 }

 public void turnRight() {
  // x + size
  this.xCoord += this.size;
  this.updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
 }
 
 /*
  * Rotate this Block and all its descendants. 
  * If the input is 1, rotate clockwise. If 0, rotate 
  * counterclockwise. If this Block has no children, do nothing.
  */
 public void rotate(int direction) {
  if(direction != 0 && direction != 1){
   throw new IllegalArgumentException("Invalid direction: " + direction);
  }
  if(this.children.length == 0) return;
  if(direction == 1){
   this.children[0].turnDown();
   this.children[1].turnRight();
   this.children[2].turnUp();
   this.children[3].turnLeft();
   Block tmp0 = this.children[0];
   Block tmp1 = this.children[1];
   Block tmp2 = this.children[2];
   Block tmp3 = this.children[3];
   this.children[0] = tmp1;
   this.children[1] = tmp2;
   this.children[2] = tmp3;
   this.children[3] = tmp0;
  } else{
   this.children[0].turnLeft();
   this.children[1].turnDown();
   this.children[2].turnRight();
   this.children[3].turnUp();
   Block tmp0 = this.children[0];
   Block tmp1 = this.children[1];
   Block tmp2 = this.children[2];
   Block tmp3 = this.children[3];
   this.children[0] = tmp3;
   this.children[1] = tmp0;
   this.children[2] = tmp1;
   this.children[3] = tmp2;
  }
  for(Block block : this.children){
   block.rotate(direction);
  }
 }
 


 /*
  * Smash this Block.
  * 
  * If this Block can be smashed,
  * randomly generate four new children Blocks for it.  
  * (If it already had children Blocks, discard them.)
  * Ensure that the invariants of the Blocks remain satisfied.
  * 
  * A Block can be smashed iff it is not the top-level Block 
  * and it is not already at the level of the maximum depth.
  * 
  * Return True if this Block was smashed and False otherwise.
  * 
  */
 public boolean smash() {
  if(this.level == 0 || this.level == this.maxDepth){
   return false;
  }
  Block[] blocks = new Block[4];
  Block block1 = new Block();
  block1.level = this.level + 1;
  block1.maxDepth = this.maxDepth;
  int colorNum = gen.nextInt(4);
  block1.color = BLOCK_COLORS[colorNum];
  block1.size = this.size/2;
  block1.xCoord = this.xCoord + this.size/2;
  block1.yCoord = this.yCoord;
  block1.children = new Block[0];

  Block block2 = new Block();
  block2.level = this.level + 1;
  block2.maxDepth = this.maxDepth;
  colorNum = gen.nextInt(4);
  block2.color = BLOCK_COLORS[colorNum];
  block2.size = this.size/2;
  block2.xCoord = this.xCoord;
  block2.yCoord = this.yCoord;
  block2.children = new Block[0];

  Block block3 = new Block();
  block3.level = this.level + 1;
  block3.maxDepth = this.maxDepth;
  colorNum = gen.nextInt(4);
  block3.color = BLOCK_COLORS[colorNum];
  block3.size = this.size/2;
  block3.xCoord = this.xCoord;
  block3.yCoord = this.yCoord + this.size/2;
  block3.children = new Block[0];

  Block block4 = new Block();
  block4.level = this.level + 1;
  block4.maxDepth = this.maxDepth;
  colorNum = gen.nextInt(4);
  block4.color = BLOCK_COLORS[colorNum];
  block4.size = this.size/2;
  block4.xCoord = this.xCoord + this.size/2;
  block4.yCoord = this.yCoord + this.size/2;
  block4.children = new Block[0];

  blocks[0] = block1;
  blocks[1] = block2;
  blocks[2] = block3;
  blocks[3] = block4;
  this.children = blocks;
  return true;
 }
 
 
 /*
  * Return a two-dimensional array representing this Block as rows and columns of unit cells.
  * 
  * Return and array arr where, arr[i] represents the unit cells in row i, 
  * arr[i][j] is the color of unit cell in row i and column j.
  * 
  * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
  */
 public Color[][] flatten() {
  int len = (int)pow(2, this.maxDepth - this.level);
  Color[][] flatten_color = new Color[len][len];
  if(this.children.length == 0){
   for(int i = 0; i < flatten_color.length; i++){
    for(int j = 0; j < flatten_color[0].length; j++){
     flatten_color[i][j] = this.color;
    }
   }
  } else{
   Color[][] tmp0 = this.children[0].flatten();
   Color[][] tmp1 = this.children[1].flatten();
   Color[][] tmp2 = this.children[2].flatten();
   Color[][] tmp3 = this.children[3].flatten();
   int row = tmp0.length;
   int col = tmp0[0].length;
   for(int i = 0; i < row; i++) {
    for (int j = 0; j < col; j++) {
     flatten_color[i][j] = tmp1[i][j];
    }
   }

   for(int i = 0; i < row; i++){
    for(int j = col; j < 2*col; j++){
     flatten_color[i][j] = tmp0[i][j-col];
    }
   }

   for(int i = row; i < 2*row; i++){
    for(int j = 0; j < col; j++){
     flatten_color[i][j] = tmp2[i-row][j];
    }
   }

   for(int i = row; i < row*2; i++){
    for(int j = col; j < col*2; j++){
     flatten_color[i][j] = tmp3[i-row][j-col];
    }
   }
  }
  return flatten_color;
 }

 
 
 // These two get methods have been provided. Do NOT modify them. 
 public int getMaxDepth() {
  return this.maxDepth;
 }
 
 public int getLevel() {
  return this.level;
 }


 public String toString() {
  return String.format("pos=(%d,%d), size=%d, level=%d"
    , this.xCoord, this.yCoord, this.size, this.level);
 }

 public void printBlock() {
  this.printBlockIndented(0);
 }

 private void printBlockIndented(int indentation) {
  String indent = "";
  for (int i=0; i<indentation; i++) {
   indent += "\t";
  }

  if (this.children.length == 0) {
   // it's a leaf. Print the color!
   String colorInfo = GameColors.colorToString(this.color) + ", ";
   System.out.println(indent + colorInfo + this);   
  } else {
   System.out.println(indent + this);
   for (Block b : this.children)
    b.printBlockIndented(indentation + 1);
  }
 }
 
 private static void coloredPrint(String message, Color color) {
  System.out.print(GameColors.colorToANSIColor(color));
  System.out.print(message);
  System.out.print(GameColors.colorToANSIColor(Color.WHITE));
 }

 public void printColoredBlock(){
  Color[][] colorArray = this.flatten();
  for (Color[] colors : colorArray) {
   for (Color value : colors) {
    String colorName = GameColors.colorToString(value).toUpperCase();
    if(colorName.length() == 0){
     colorName = "\u2588";
    }else{
     colorName = colorName.substring(0, 1);
    }
    coloredPrint(colorName, value);
   }
   System.out.println();
  }
 }

 public static void main(String[] args) {
  Block block = new Block(0, 5);
  block.updateSizeAndPosition(64, 0, 0);
  block.printColoredBlock();
 }
}
