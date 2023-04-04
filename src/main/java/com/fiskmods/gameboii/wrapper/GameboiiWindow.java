package com.fiskmods.gameboii.wrapper;

import com.fiskmods.gameboii.Cartridge;
import com.fiskmods.gameboii.Engine;
import com.fiskmods.gameboii.IGame;
import com.fiskmods.gameboii.engine.InputKey;
import com.fiskmods.gameboii.graphics.IDisplayScreen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.stream.IntStream;

public class GameboiiWindow extends JPanel implements Runnable, KeyListener, IDisplayScreen
{
    private static final JFrame WINDOW = new JFrame();

    public static boolean f11Fullscreen;

    public static int width, prevWidth;
    public static int height, prevHeight;
    public static int defaultWidth;
    public static int defaultHeight;

    private Thread thread;

    public static int ticks;

    private BufferedImage displayCanvas;

    private final Cartridge selectedCartridge;
    private IGame game;

    private static GameboiiWindow instance;

    public GameboiiWindow(Cartridge cartridge, int width, int height)
    {
        GameboiiWindow.width = width;
        GameboiiWindow.height = height;
        GameboiiWindow.defaultWidth = width;
        GameboiiWindow.defaultHeight = height;
        selectedCartridge = cartridge;
        instance = this;

        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
    }

    public static GameboiiWindow getInstance()
    {
        return instance;
    }

    private void init()
    {
        Engine.init(new GameboiiWrapper(), this);
        Engine.reloadResources();
        game = Engine.get(selectedCartridge);
    }

    @Override
    public void addNotify()
    {
        super.addNotify();

        if (thread == null)
        {
            thread = new Thread(this, "Gameboii Main");
            addKeyListener(this);
            thread.start();
        }
    }

    public final Timer timer = new Timer(20.0F);

    private void runGameLoop()
    {
        width = getWidth();
        height = getHeight();

        if (prevWidth != width || prevHeight != height)
        {
            onResolutionChange();
        }

        timer.updateTimer();

        for (int i = 0; i < timer.elapsedTicks; ++i)
        {
            tick();
        }

        draw();
        SoundPool.update();
        prevWidth = width;
        prevHeight = height;
    }

    @Override
    public void run()
    {
        init();

        while (true)
        {
            runGameLoop();
        }
    }

    private void onResolutionChange()
    {
        Engine.start(selectedCartridge, 144 * 4, 144 * 3);
    }

    public void tick()
    {
        game.tick();
        ++ticks;
    }

    @Override
    public void init(BufferedImage canvas, int width, int height)
    {
        displayCanvas = canvas;
    }

    @Override
    public void draw(BufferedImage canvas)
    {
        Dimension ratio = new Dimension(game.getWidth(), game.getHeight());
        Dimension dim = new Dimension(ratio);
        double scaleX = (double) dim.width / (width < 0 ? dim.width : width);
        double scaleY = (double) dim.height / (height < 0 ? dim.height : height);

        if ((float) width / height < (float) ratio.width / ratio.height)
        {
            dim.width /= scaleX;
            dim.height /= scaleX;
        }
        else
        {
            dim.width /= scaleY;
            dim.height /= scaleY;
        }

        Rectangle r = new Rectangle((width - dim.width) / 2, (height - dim.height) / 2, dim.width, dim.height);
        Graphics g = getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, r.x, height);
        g.fillRect(r.x + r.width, 0, width - r.x, height);
        g.fillRect(0, 0, width, r.y);
        g.fillRect(0, r.y + r.height, width, height - r.y);
        g.drawImage(canvas, r.x, r.y, r.width, r.height, null);
        g.dispose();
    }

    @Override
    public void clear()
    {
        displayCanvas = null;
    }

    public void draw()
    {
        if (game != null && displayCanvas != null)
        {
            game.draw(Engine.system().partialTicks());
        }
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_F11)
        {
            setF11FullScreen();
            return;
        }

        int key = e.getExtendedKeyCode();
        InputKey.KEYS.stream().filter(in -> IntStream.of(in.keys).anyMatch(k -> k == key)).forEach(in -> in.setPressed(true));
        game.keyTyped(e.getKeyChar(), key);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int key = e.getExtendedKeyCode();
        InputKey.KEYS.stream().filter(in -> IntStream.of(in.keys).anyMatch(k -> k == key)).forEach(in -> in.setPressed(false));
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    public static void open(Cartridge cartridge) throws Exception
    {
        InputStream is = Main.class.getResourceAsStream("/assets/textures/title/icon.png");

        if (is != null)
        {
            WINDOW.setIconImage(ImageIO.read(is));
        }

        int width = 920;
        int height = 480;
        WINDOW.setTitle("Gameboii");
        WINDOW.setContentPane(new GameboiiWindow(cartridge, width, height));
        WINDOW.setLocationRelativeTo(null);
        WINDOW.setLocation(WINDOW.getX() - (width / 2), WINDOW.getY() - (height / 2));
        WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        WINDOW.setResizable(true);
        WINDOW.pack();
        WINDOW.setVisible(true);

        Runtime.getRuntime().addShutdownHook(new Thread(GameboiiWrapper::onShutdown));
    }

    public static void setF11FullScreen()
    {
        GraphicsDevice gd = WINDOW.getGraphicsConfiguration().getDevice();
        Dimension dim = gd.getDefaultConfiguration().getBounds().getSize();
        f11Fullscreen = !f11Fullscreen;

        if (f11Fullscreen)
        {
            WINDOW.setResizable(false);
            // window.setBounds(0, -20, dim.width, dim.height + 20);
            WINDOW.setPreferredSize(new Dimension(dim.width, dim.height));
            WINDOW.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        else
        {
            WINDOW.setResizable(true);
            WINDOW.setPreferredSize(new Dimension(1024, 512));
            WINDOW.setExtendedState(Frame.NORMAL);
        }
    }
}
