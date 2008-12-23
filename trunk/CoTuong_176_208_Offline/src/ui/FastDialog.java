/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author dong
 */
public class FastDialog {
    private FastTextBox mTextBox;
    
    public FastDialog(int width, int height)
    {
        mTextBox = new FastTextBox(width, height, Graphics.HCENTER | Graphics.VCENTER);
    }
    
    public void setText(String aText)
    {
        mTextBox.removeAllParagraph();
        mTextBox.addFastParagraph(aText);
    }
    
    public void onActivate()
    {
        
    }   
    
    public void onKeyPressed(int aKeyCode)
    {
        mTextBox.onKeyPressed(aKeyCode);
    }
    
    public void paint(Graphics g)
    {
        mTextBox.fastPaint(g, true);
    }    
}
