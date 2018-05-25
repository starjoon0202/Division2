package trash;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
  
public class WindowCommunication
{
    JDialog dialog;
    JTextField results;
  
    public WindowCommunication(final Frame f)
    {
        results = new JTextField(16);
        boolean isModal = false;
        dialog = new JDialog(f, "enter data, press enter", isModal);
        final JTextField tf = new JTextField(16);
        tf.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                transferInput(tf);
                dialog.dispose();
            }
        });
        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                transferInput(tf);
            }
        });
        JPanel panel = new JPanel();
        panel.add(tf);
        dialog.getContentPane().add(panel);
        dialog.setSize(300,160);
        dialog.setLocationRelativeTo(f);
    }
  
    private void transferInput(JTextField tf)
    {
        results.setText(tf.getText());
        tf.setText("");
    }
  
    private JPanel getNorth()
    {
        JButton open = new JButton("launch dialog");
        open.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(!dialog.isVisible())
                    dialog.setVisible(true);
            }
        });
        JPanel panel = new JPanel();
        panel.add(open);
        return panel;
    }
  
    private JPanel getCenter()
    {
        JPanel panel = new JPanel();
        panel.add(results);
        return panel;
    }
  
    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        WindowCommunication test = new WindowCommunication(f);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(test.getNorth(), "North");
        f.getContentPane().add(test.getCenter());
        f.setSize(400,240);
        f.setLocation(200,200);
        f.setVisible(true);
    }
}