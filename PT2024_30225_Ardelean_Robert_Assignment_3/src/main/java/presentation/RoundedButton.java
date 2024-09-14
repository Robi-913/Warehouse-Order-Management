package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {

    private final Dimension originalSize;
    private final float originalFontSize;
    private int originalX;
    private int originalY;

    public RoundedButton(String text) {
        super(text);
        setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        setForeground(Color.WHITE);
        setBackground(new Color(50, 36, 178));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        originalSize = new Dimension(163, 40);
        originalFontSize = getFont().getSize2D();

        setBounds(0, 0, originalSize.width, originalSize.height);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                originalX = getX();
                originalY = getY();
                setBackground(new Color(41, 57, 148));
                setFont(getFont().deriveFont(originalFontSize * 1.1f));
                setBounds(originalX - (int)(originalSize.width * 0.1), originalY - (int)(originalSize.height * 0.1),
                        (int) (originalSize.width * 1.2), (int) (originalSize.height * 1.2));
                revalidate();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(new Color(50, 36, 178));
                setFont(getFont().deriveFont(originalFontSize));
                setBounds(originalX, originalY, originalSize.width, originalSize.height);
                revalidate();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(new Color(32, 40, 108));
                setFont(getFont().deriveFont(originalFontSize * 1.1f));
                setBounds(originalX - (int)(originalSize.width * 0.1), originalY - (int)(originalSize.height * 0.1),
                        (int) (originalSize.width * 1.2), (int) (originalSize.height * 1.2));
                revalidate();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(new Color(41, 57, 148));
                setFont(getFont().deriveFont(originalFontSize * 1.1f));
                setBounds(originalX, originalY, originalSize.width, originalSize.height);
                revalidate();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.dispose();
    }
}
