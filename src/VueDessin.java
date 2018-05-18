import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

// Classe representant le panel destine a afficher les dessins de l'utilisateur
public class VueDessin extends JPanel implements Observer {

	// Liste des figures colorees a dessiner sur le panel
	private ArrayList<FigureColoree> lfg;

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof DessinModele) {
			DessinModele dessin = (DessinModele) o;
			this.lfg = dessin.getLfg();
			this.repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(lfg != null) {
			for(FigureColoree figure : lfg) {
				figure.affiche(g);
			}
		}
	}
}
