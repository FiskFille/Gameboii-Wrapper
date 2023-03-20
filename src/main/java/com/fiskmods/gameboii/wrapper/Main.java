package com.fiskmods.gameboii.wrapper;

import com.fiskmods.gameboii.Cartridge;
import com.fiskmods.gameboii.games.batfish.BatfishCartridge;

import javax.swing.*;
import java.awt.*;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Cartridge.register(BatfishCartridge.INSTANCE);

        for (String arg : args)
        {
            for (Cartridge cartridge : Cartridge.values())
            {
                if (arg.equals("--game=" + cartridge.id))
                {
                    GameboiiWindow.open(cartridge);
                    return;
                }
            }
        }

        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        for (Cartridge cartridge : Cartridge.values())
        {
            JButton button = new JButton(cartridge.id);
            button.setFont(new Font("Arial", Font.PLAIN, 20));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            button.addActionListener(e ->
            {
                frame.dispose();

                try
                {
                    GameboiiWindow.open(cartridge);
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            });
        }

        frame.setTitle("Gameboii");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
