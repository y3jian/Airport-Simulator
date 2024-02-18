/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yolandajian
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.Timer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class frame extends javax.swing.JFrame {

    drawingArea draw = new drawingArea();
    char typeArr[] = {'i', 'd', 's'};
    boolean takeoff = true;

    public static Queue<Integer> landing = new LinkedList<Integer>();
    public static Queue<Integer> takeOff = new LinkedList<Integer>();
    File takeOffsFile = new File("takeOffs.txt");
    File arrivalsFile = new File("arrivals.txt");
    Timer t;
    int duration = 600;
    String input = "";

    boolean arriving = false;
    boolean removing = false;

    int time = 0;
    int numOfPlanes = 0;
    int tDuration = 600;

    /**
     * Creates new form frame
     */
    public frame() {
        initComponents();
        t = new Timer(tDuration, new TimerListener());
        try {
            fileRead();
        } catch (FileNotFoundException ex) {

        }
    }

    public void fileRead() throws FileNotFoundException {
//reading the file and adding them to the queue 
        Scanner readOut = new Scanner(takeOffsFile);
        String input = "";
        try {
            while (true) {
                int planeID = 0;
                input = readOut.nextLine();
                takeoffsTA.append(input + "\n");//printing to the text area
                planeID = Integer.parseInt(input);
                takeOff.add(planeID);//adding to the queue 
            }
        } catch (NoSuchElementException ex) {
        }
        
        Scanner readIn = new Scanner(arrivalsFile);
        input = "";
        try {
            while (true) {
                int planeID = 0;
                input = readIn.nextLine();
                arrivalsTA.append(input + "\n");//printing to the text area
                planeID = Integer.parseInt(input);
                landing.add(planeID);//adding to the queue 
            }
        } catch (NoSuchElementException ex) {
        }
    }

    public Queue queuesUpdate(Queue theQueue, boolean queueArrival, boolean queueRemoval) {

        if (queueArrival) {//for the arrival queue 
            if (queueRemoval) {//if removing 
                arrivalsTA.setText("");//resetting the text in the text area
                theQueue.remove();//removing the first plane from the queue 
                for (int planeNum : landing) {
                    arrivalsTA.append(planeNum + "\n");//reprinting the text without the removed plane number
                }

            } else {//if adding
                theQueue.add(Integer.parseInt(input));
                arrivalsTA.append(input);//adding to the text area 
            }

        } else {//for the takeoffs queue 

            if (queueRemoval) {//if removing 
                takeoffsTA.setText("");
                theQueue.remove();
                for (int planeNum : takeOff) {
                    takeoffsTA.append(planeNum + "\n");
                }
            } else {//if adding
                theQueue.add(Integer.parseInt(input));
                takeoffsTA.append(input);
            }
        }
        return theQueue;
    }

    private class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean isLanding = false;
            if (numOfPlanes == 0 || numOfPlanes == 1) {//if less than two planes have landed 

                if (!landing.isEmpty()) {// if there are planes left in queue 
                    isLanding = true;
                    draw.getQueue(isLanding);//sending landing to draw for animation
                    switch (time) {//cycling through times for countdown 
                        case 0:
                            //calling method to reset the position of the plane animation 
                            draw.setPlaneX();
                            draw.setPlaneY();
                            draw.resetXValue();
                            draw.resetYValue();
                            time = time + tDuration;//adding to time 
                            //update status 
                            statusLabel.setText("Flight " + landing.peek().toString() + " is next to land.");
                            break;
                        case 600:
                            time = time + tDuration;
                            statusLabel.setText("Flight " + landing.peek().toString() + " is next to land. 4");
                            break;
                        case 1200:
                            time = time + tDuration;
                            statusLabel.setText("Flight " + landing.peek().toString() + " is next to land. 3");
                            break;
                        case 1800:
                            time = time + tDuration;
                            statusLabel.setText("Flight " + landing.peek().toString() + " is next to land. 2");
                            break;
                        case 2400:
                            time = time + tDuration;
                            statusLabel.setText("Flight " + landing.peek().toString() + " is next to land. 1");
                            break;
                        case 3000://after countdown 
                            time = 0;//reset time 
                            numOfPlanes++;//increase the plane count 
                            //updating the queue 
                            arriving = true;//the plane is arriving 
                            removing = true;//we are removing a value 
                            queuesUpdate(landing, arriving, removing);
                            statusLabel.setText("Plane landed.");
                            break;
                        default:
                            break;
                    }

                } else if (landing.isEmpty()) {//if there are no planes to land, go to takeoffs
                    numOfPlanes = 2;
                }

            } else if (numOfPlanes == 2) {
                if (!takeOff.isEmpty()) {//if the queue is not empty 
                    isLanding = false; //taking off
                    draw.getQueue(isLanding);//updating the draw method for animation 

                    switch (time) {
                        case 0:
                            //updating the draw method for animation 
                            draw.setPlaneX();
                            draw.setPlaneY();
                            draw.resetXValue();
                            draw.resetYValue();
                            time += tDuration;//adding to time and updating status label 
                            statusLabel.setText("Flight " + takeOff.peek().toString() + " is next to take off.");
                            break;
                        case 600:
                            time += tDuration;
                            statusLabel.setText("Flight " + takeOff.peek().toString() + " is next to take off. 2");
                            break;
                        case 1200:
                            time += tDuration;
                            statusLabel.setText("Flight " + takeOff.peek().toString() + " is next to take off. 1");
                            break;
                        case 1800:
                            arriving = false;//the plane is taking off
                            removing = true;//removing from the queue 
                            queuesUpdate(takeOff, arriving, removing);
                            time = 0;
                            numOfPlanes = 0;//go back to landings
                            statusLabel.setText("Plane departed.");
                            break;
                        default:
                            break;
                    }

                } else if (takeOff.isEmpty()) {//the program continues to run until 
                    //user inputs more flights 
                    numOfPlanes = 0;
                }
            }
        }
    }

    static boolean typeVerify(String elementCur, char arrCur) { // Error checking 
        boolean correct = false;
        switch (arrCur) {
            case 'i':
                try {
                    int typeInt = Integer.parseInt(elementCur);//converting to int
                    correct = true;

                } catch (NumberFormatException e) {//catching the error
                    correct = false;

                }
                break;

            case 'd':
                try {
                    double typeDouble = Double.parseDouble(elementCur);
                    correct = true;
                } catch (NumberFormatException e) {
                    correct = false;
                    System.out.println("two must be a double");
                }
                break;

            case 's':

                for (int i = 0; i <= elementCur.length(); i++) {
                    //uppercase 65 to 90 lowercase 97 to 122
                    if (elementCur.charAt(i) >= 65 && elementCur.charAt(i) <= 90 || elementCur.charAt(i) >= 97 && elementCur.charAt(i) <= 122) {
                        correct = true;
                        break;
                    } else {
                        System.out.println("Three must be a String");
                        correct = false;
                        break;
                    }
                }
        }
        return correct;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        takeoffsTA = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        startButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        arrivingTF = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        takeoffTF = new javax.swing.JTextField();
        exitButton = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        arrivalsTA = new javax.swing.JTextArea();
        statusLabel = new javax.swing.JLabel();
        drawingArea1 = new drawingArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 24)); // NOI18N
        jLabel1.setText("Airport Simulator (PAY)");

        takeoffsTA.setEditable(false);
        takeoffsTA.setColumns(20);
        takeoffsTA.setRows(5);
        takeoffsTA.setFocusable(false);
        jScrollPane3.setViewportView(takeoffsTA);

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel2.setText("Press 'START' to begin simulation.");

        startButton.setText("START");
        startButton.setFocusable(false);
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("Arrivals");

        jLabel5.setText("Arriving Flight:");

        jLabel4.setText("Takeoffs");

        arrivingTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arrivingTFActionPerformed(evt);
            }
        });

        jLabel6.setText("Takeoff Flight:");

        takeoffTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                takeoffTFActionPerformed(evt);
            }
        });

        exitButton.setText("Exit");
        exitButton.setFocusable(false);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        arrivalsTA.setEditable(false);
        arrivalsTA.setColumns(20);
        arrivalsTA.setRows(5);
        arrivalsTA.setFocusable(false);
        jScrollPane2.setViewportView(arrivalsTA);

        javax.swing.GroupLayout drawingArea1Layout = new javax.swing.GroupLayout(drawingArea1);
        drawingArea1.setLayout(drawingArea1Layout);
        drawingArea1Layout.setHorizontalGroup(
            drawingArea1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 247, Short.MAX_VALUE)
        );
        drawingArea1Layout.setVerticalGroup(
            drawingArea1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 190, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(36, 36, 36)
                                .addComponent(exitButton)
                                .addGap(20, 20, 20))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(arrivingTF, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(startButton)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(takeoffTF, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(44, 44, 44)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(jLabel3)
                                        .addGap(187, 187, 187)
                                        .addComponent(jLabel4)
                                        .addGap(14, 14, 14)))
                                .addGap(18, 18, 18)
                                .addComponent(drawingArea1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(98, 98, 98))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(startButton))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(drawingArea1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(67, 67, 67)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(arrivingTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(takeoffTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void arrivingTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arrivingTFActionPerformed
        input = arrivingTF.getText();
        boolean goodInt = typeVerify(input, typeArr[0]);//error checking
        if (goodInt) {
            arriving = true;//arrivals
            removing = false;//adding value 
            //update the queue 
            queuesUpdate(landing, arriving, removing);
            arrivingTF.setText("");
        } else {
            errorLabel.setText("Please enter an integer value.");
            arrivingTF.setText("");
        }
    }//GEN-LAST:event_arrivingTFActionPerformed

    private void takeoffTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_takeoffTFActionPerformed

        input = takeoffTF.getText();
        boolean goodInt = typeVerify(input, typeArr[0]);
        if (goodInt) {
            arriving = false;//landings
            removing = false;//removing values 
            //update the queue 
            queuesUpdate(takeOff, arriving, removing);

            takeoffTF.setText("");
        } else {
            errorLabel.setText("Please enter an integer value.");
            takeoffTF.setText("");
        }
    }//GEN-LAST:event_takeoffTFActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        //starting the timer and animation 
        t.start();
        draw.startAnimation();

    }//GEN-LAST:event_startButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTextArea arrivalsTA;
    private javax.swing.JTextField arrivingTF;
    private drawingArea drawingArea1;
    public static javax.swing.JLabel errorLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton startButton;
    public static javax.swing.JLabel statusLabel;
    private javax.swing.JTextField takeoffTF;
    public static javax.swing.JTextArea takeoffsTA;
    // End of variables declaration//GEN-END:variables
}
