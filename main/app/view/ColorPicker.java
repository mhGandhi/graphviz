package app.view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Abstrakte Klasse zum Aufrufen eines Farbauswahldialoges
 */
public abstract class ColorPicker {

    /**
     * Anzeigesprache
     */
    private static ResourceBundle res;

    /**
     * Gibt Text des Keys in Anzeigesprache zurück
     * @param pKey
     * @return
     */
    private static String r(String pKey){
        try{
            return res.getString(pKey);
        }catch(MissingResourceException e){
            return "[COLKEY_"+pKey+"]";
        }
    }

    /**
     * 
     * @param pParentComponent übergeordnete Komponente (Fenster zb)
     * @param pInitialColor anfangsfarbe
     * @param pTheme
     * @param pResource
     * @return =null wenn option "Reset", =pInitialColor wenn option "Cancel", =gewählte Farbe wenn Option "Ok"
     */
    public static Color showDialog(Component pParentComponent, Color pInitialColor, Theme pTheme, ResourceBundle pResource){

        res = pResource;
        JDialog ColorPicker = new JDialog((Frame) pParentComponent, r("colorChooserTitle"), true);
        JPanel ContentPanel = new JPanel();
        JPanel rsliderContainer = new JPanel();
        JPanel gsliderContainer = new JPanel();
        JPanel bsliderContainer = new JPanel();
        JPanel asliderContainer = new JPanel();
        JPanel hexcodeContainer = new JPanel();
        JPanel buttonContainer = new JPanel();
        JLabel rLabel = new JLabel();
        JSlider r = new JSlider();
        JTextField rt = new JTextField(3);
        JLabel gLabel = new JLabel();
        JSlider g = new JSlider();
        JTextField gt = new JTextField(3);
        JLabel bLabel = new JLabel();
        JSlider b = new JSlider();
        JTextField bt = new JTextField(3);
        JLabel aLabel = new JLabel();
        JSlider a = new JSlider();
        JTextField at = new JTextField(3);
        JTextField HexCode = new JTextField(6);
        JButton option_HexCodeApply = new JButton();
        JButton option_okay = new JButton();
        JButton option_cancel = new JButton();
        JButton option_reset = new JButton();
        double writingBrightness = (299 * pTheme.getBackgroundColor().getRed() + 587 * pTheme.getBackgroundColor().getGreen() + 114 * pTheme.getBackgroundColor().getBlue()) / 1000; //heil dir Stackoverflow
        final AtomicReference<Color> FinalColor = new AtomicReference<>(pInitialColor);
        
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e){
                
                //buutons
                if (e.getSource() == option_cancel){
                    FinalColor.set(pInitialColor);
                    ColorPicker.dispose();
                }
                if (e.getSource() == option_okay){
                    FinalColor.set(new Color(r.getValue(), g.getValue(), b.getValue(), a.getValue()));
                    ColorPicker.dispose();
                }
                if (e.getSource() == option_reset){
                    FinalColor.set(null);
                    ColorPicker.dispose();
                }
                if(e.getSource() == option_HexCodeApply){
                    FinalColor.set(Theme.decodeColor(HexCode.getText()));
                    ColorPicker.dispose();
                }
                //slider shiat
                if (e.getSource() == rt){
                    int number = 0;
                    try {
                        number = Integer.parseInt(rt.getText());
                    } catch (Exception c) {
                        number = 0;
                        rt.setText("0");
                    }
                    if(number <= 255){
                        if(number >= 0){
                            r.setValue(number);
                        } else{r.setValue(0); rt.setText("0");}
                    } else{r.setValue(255); rt.setText("255");}
                }
                if (e.getSource() == gt){
                    int number = 0;
                    try {
                        number = Integer.parseInt(gt.getText());
                    } catch (Exception c) {
                        number = 0;
                        gt.setText("0");
                    }
                    if(number <= 255){
                        if(number >= 0){
                            g.setValue(number);
                        } else{g.setValue(0); gt.setText("0");}
                    } else{g.setValue(255); gt.setText("255");}
                }
                if (e.getSource() == bt){
                    int number = 0;
                    try {
                        number = Integer.parseInt(bt.getText());
                    } catch (Exception c) {
                        number = 0;
                        bt.setText("0");
                    }
                    if(number <= 255){
                        if(number >= 0){
                            b.setValue(number);
                        } else{b.setValue(0); bt.setText("0");}
                    } else{b.setValue(255); bt.setText("255");}
                }
                if (e.getSource() == at){
                    int number = 0;
                    try {
                        number = Integer.parseInt(at.getText());
                    } catch (Exception c) {
                        number = 0;
                        at.setText("0");
                    }
                    if(number <= 255){
                        if(number >= 0){
                            a.setValue(number);
                        } else{a.setValue(0); at.setText("0");}
                    } else{a.setValue(255); at.setText("255");}
                }
            }
        };
        
        ChangeListener SliderListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e){
                //more slider shiat
                if (e.getSource() == r) {
                    rt.setText("" + r.getValue());
                    HexCode.setText(Theme.encodeColor(new Color(r.getValue(), g.getValue(), b.getValue())));
                }
                if (e.getSource() == g) {
                    gt.setText("" + g.getValue());
                    HexCode.setText(Theme.encodeColor(new Color(r.getValue(), g.getValue(), b.getValue())));
                }
                if (e.getSource() == b) {
                    bt.setText("" + b.getValue());
                    HexCode.setText(Theme.encodeColor(new Color(r.getValue(), g.getValue(), b.getValue())));
                }
                if (e.getSource() == a) {
                    at.setText("" + a.getValue());
                    HexCode.setText(Theme.encodeColor(new Color(r.getValue(), g.getValue(), b.getValue())));
                }
                if(e.getSource() == r || e.getSource() == g || e.getSource() == b || e.getSource() == a){
                    //option_okay.setForeground(new Color(r.getValue(), g.getValue(), b.getValue(), a.getValue()));
                    HexCode.setBackground(new Color(r.getValue(), g.getValue(), b.getValue()));
                    HexCode.setBorder(new LineBorder((new Color(r.getValue(), g.getValue(), b.getValue()))));
                    double brightness = (299 * r.getValue() + 587 * g.getValue() + 114 * b.getValue()) / 1000; //heil dir Stackoverflow
                    if(brightness>=128){                     
                        HexCode.setForeground(Color.black);                 
                    }else{HexCode.setForeground(Color.white);}
                }
            }
        };
        BoxLayout boxLayout = new BoxLayout(ColorPicker.getContentPane(), BoxLayout.Y_AXIS);
        BoxLayout boah = new BoxLayout(ContentPanel, BoxLayout.Y_AXIS);
        ColorPicker.getContentPane().setLayout(boxLayout);
        ContentPanel.setLayout(boah);



        rLabel.setText(r("colorPicker_red") + "   ");
        r.setMajorTickSpacing(1);
        r.setMaximum(255);
        r.setValue(pInitialColor.getRed());
        r.addChangeListener(SliderListener);
        r.setBackground(pTheme.getBackgroundColor());
        r.setBorder(new LineBorder(pTheme.getBackgroundColor()));
        rt.setText("" + r.getValue());
        rt.addActionListener(al);
        rt.setBackground(pTheme.getBackgroundColor());
        rt.setBorder(new LineBorder(pTheme.getBackgroundColor()));
        if(writingBrightness>=128){                     
            rt.setForeground(Color.black);
            rLabel.setForeground(Color.black);                 
        }else{rt.setForeground(Color.white); rLabel.setForeground(Color.white);}


        gLabel.setText(r("colorPicker_green"));
        g.setMajorTickSpacing(1);
        g.setMaximum(255);
        g.setValue(pInitialColor.getGreen());
        g.addChangeListener(SliderListener);
        g.setBackground(pTheme.getBackgroundColor());
        g.setBorder(new LineBorder(pTheme.getBackgroundColor()));
        gt.setText("" + g.getValue());
        gt.addActionListener(al);
        gt.setBackground(pTheme.getBackgroundColor());
        gt.setBorder(new LineBorder(pTheme.getBackgroundColor()));
        if(writingBrightness>=128){                     
            gt.setForeground(Color.black); 
            gLabel.setForeground(Color.black);                     
        }else{gt.setForeground(Color.white); gLabel.setForeground(Color.white);}


        bLabel.setText(r("colorPicker_blue") +  " ");
        b.setMajorTickSpacing(1);
        b.setMaximum(255);
        b.setValue(pInitialColor.getBlue());
        b.addChangeListener(SliderListener);
        b.setBackground(pTheme.getBackgroundColor());
        b.setBorder(new LineBorder(pTheme.getBackgroundColor()));
        bt.setText("" + b.getValue());
        bt.addActionListener(al);
        bt.setBackground(pTheme.getBackgroundColor());
        bt.setBorder(new LineBorder(pTheme.getBackgroundColor()));
        if(writingBrightness>=128){                     
            bt.setForeground(Color.black); 
            bLabel.setForeground(Color.black);                     
        }else{bt.setForeground(Color.white); bLabel.setForeground(Color.white);}


        aLabel.setText(r("colorPicker_alpha"));
        a.setMajorTickSpacing(1);
        a.setMaximum(255);
        a.setValue(pInitialColor.getAlpha());
        a.addChangeListener(SliderListener);
        a.setBackground(pTheme.getBackgroundColor());
        a.setBorder(new LineBorder(pTheme.getBackgroundColor()));
        at.setText("" + a.getValue());
        at.addActionListener(al);
        at.setBackground(pTheme.getBackgroundColor());
        at.setBorder(new LineBorder(pTheme.getBackgroundColor()));
        if(writingBrightness>=128){                     
            at.setForeground(Color.black); 
            aLabel.setForeground(Color.black);                     
        }else{at.setForeground(Color.white); aLabel.setForeground(Color.white);}


        HexCode.setText(Theme.encodeColor(new Color(r.getValue(), g.getValue(), b.getValue(), a.getValue())));
        HexCode.setBackground(new Color(r.getValue(), g.getValue(), b.getValue()));
        HexCode.setBorder(new LineBorder((new Color(r.getValue(), g.getValue(), b.getValue()))));
        double brightness = (299 * r.getValue() + 587 * g.getValue() + 114 * b.getValue()) / 1000; //heil dir Stackoverflow
        if(brightness>=128){                     
            HexCode.setForeground(Color.black);                 
        }else{HexCode.setForeground(Color.white);}


        option_HexCodeApply.setText(r("option_apply"));
        option_HexCodeApply.addActionListener(al);
        option_okay.setText(r("option_ok"));
        option_okay.addActionListener(al);
        option_okay.addChangeListener(SliderListener);
        option_cancel.setText(r("option_cancel"));
        option_cancel.addActionListener(al);
        option_reset.setText(r("option_reset"));
        option_reset.addActionListener(al);
        

        rsliderContainer.add(rLabel);
        rsliderContainer.add(r);
        rsliderContainer.add(rt);
        gsliderContainer.add(gLabel);
        gsliderContainer.add(g);
        gsliderContainer.add(gt);
        bsliderContainer.add(bLabel);
        bsliderContainer.add(b);
        bsliderContainer.add(bt);
        asliderContainer.add(aLabel);
        asliderContainer.add(a);
        asliderContainer.add(at);
        buttonContainer.add(option_okay);
        buttonContainer.add(option_reset);
        buttonContainer.add(option_cancel);
        hexcodeContainer.add(HexCode);
        hexcodeContainer.add(option_HexCodeApply);
        rsliderContainer.setBackground(pTheme.getBackgroundColor());
        gsliderContainer.setBackground(pTheme.getBackgroundColor());
        bsliderContainer.setBackground(pTheme.getBackgroundColor());
        asliderContainer.setBackground(pTheme.getBackgroundColor());
        buttonContainer.setBackground(pTheme.getBackgroundColor());
        hexcodeContainer.setBackground(pTheme.getBackgroundColor());
        ContentPanel.add(rsliderContainer);
        ContentPanel.add(gsliderContainer);
        ContentPanel.add(bsliderContainer);
        ContentPanel.add(asliderContainer);
        ContentPanel.add(hexcodeContainer);
        ContentPanel.add(buttonContainer);
        ContentPanel.setBackground(pTheme.getBackgroundColor());
        ColorPicker.add(ContentPanel);



        ColorPicker.setSize(400, 300);
        ColorPicker.setResizable(false);
        ColorPicker.setLocationRelativeTo(pParentComponent);
        ColorPicker.setVisible(true);
        HexCode.requestFocusInWindow();
        ColorPicker.pack();
        

        return FinalColor.get(); 
    }
}

