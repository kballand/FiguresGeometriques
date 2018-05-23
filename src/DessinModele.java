import java.awt.Color;
import java.util.ArrayList;
import java.util.Observable;

// Classe representant un modele de dessin pour creer et modifier des figures colorees
public class DessinModele extends Observable {

	// Liste des figures colorees crees
	private ArrayList<FigureColoree> lfg;
	// Figure coloree en cours de creation
	private FigureColoree figureEnCours;
	// Nombre de clics deja effectues pour la creation de la figure en cours
	private int nbClic;
	// Tableau des points deja cliques pour creer la figure coloree
	private Point[] points_Cliques;
	// Type d'action en cours pour le modele de dessin (0 = creation figure, 1 = Tracee a main levee, 2 = Manipulation)
	private int type;
	// La figure coloree qui est selectionnee
	private FigureColoree figureSelectionnee;
	// Liste des traits crees par le modele
	private ArrayList<Trait> traits;

	/**
	 * Constructeur d'un modele de dessin
	 */
	public DessinModele() {
		this.lfg = new ArrayList<FigureColoree>();
		this.traits = new ArrayList<Trait>();
		this.points_Cliques = new Point[0];
	}

	/**
	 * Methode permettant d'ajouter une figure coloree a la liste des celles deja construites
	 * 
	 * @param figure
	 *            La figure a ajouter a la liste
	 */
	public void ajouter(FigureColoree figure) {
		if(figure != null) {
			this.lfg.add(figure);
			this.setChanged();
			this.notifyObservers();
		}
	}

	/**
	 * Methode permettant de changer la couleur d'une figure deja construite
	 * 
	 * @param figure
	 *            La figure dont on veut changer la couleur
	 * @param couleur
	 *            La nouvelle couleur de la figure
	 */
	public void changerCouleur(FigureColoree figure, Color couleur) {
		if(figure != null && this.lfg.contains(figure)) {
			figure.changeCouleur(couleur);
			this.setChanged();
			this.notifyObservers();
		}
	}

	/**
	 * Methode permettant de changer les points d'une figure deja construite
	 * 
	 * @param figure
	 *            La figure deja construite dont on veut changer les points
	 * @param points
	 *            Les nouveaux points de la figure
	 */
	public void changerPoints(FigureColoree figure, Point[] points) {
		if(figure != null && this.lfg.contains(figure)) {
			figure.modifierPoints(points);
			this.setChanged();
			this.notifyObservers();
		}
	}

	/**
	 * Methode permettant de construire une nouvelle figure coloree a partir d'une instance de celle-ci
	 * 
	 * @param figure
	 *            L'instance de la figure coloree a construire
	 */
	public void construit(FigureColoree figure) {
		this.figureEnCours = figure;
		this.nbClic = 0;
		this.points_Cliques = new Point[figure.nbClics()];
	}

	/**
	 * Methode permettant d'ajouter un point a la construction de la figure en cours
	 * 
	 * @param x
	 *            L'abscisse du point a ajouter
	 * @param y
	 *            L'ordonnee du point a ajouter
	 */
	public void ajouterPoint(int x, int y) {
		if(this.figureEnCours != null) {
			this.points_Cliques[this.nbClic++] = new Point(x, y);
			if(this.nbClic == this.figureEnCours.nbClics()) {
				this.figureEnCours.modifierPoints(this.points_Cliques);
				this.ajouter(this.figureEnCours);
				try {
					FigureColoree nouvelle = this.figureEnCours.getClass().newInstance();
					nouvelle.changeCouleur(this.figureEnCours.couleur);
					this.figureEnCours = nouvelle;
					this.points_Cliques = new Point[this.figureEnCours.nbClics()];
				} catch(InstantiationException | IllegalAccessException e) {
					this.figureEnCours = null;
					this.points_Cliques = new Point[0];
				}
				this.nbClic = 0;
			}
		}
	}

	/**
	 * Methode accesseur permettant de recuper le nombre de clics deja effectues pour construire la figure en cours
	 * 
	 * @return Le nombre de clics deja effectues pour construire la figure en cours
	 */
	public int getNbClic() {
		return this.nbClic;
	}

	/**
	 * Methode permettant de redefinir le de nombre de clics effectues pour construire la figure en cours
	 * 
	 * @param nbClic
	 *            Le nouveau nombre de clics effectues pour constuire la figure en cours
	 */
	public void setNbClic(int nbClic) {
		this.nbClic = nbClic;
	}

	/**
	 * Methode accesseur permettant de recuperer la liste de figures crees par le modele de dessin
	 * 
	 * @return La liste des figures crees par le modele de dessin
	 */
	public ArrayList<FigureColoree> getLfg() {
		return this.lfg;
	}

	/**
	 * Methode accesseur permettant de recuperer la figure en cours de construction
	 * 
	 * @return La figure en cours de construction
	 */
	public FigureColoree getFigureEnCours() {
		return this.figureEnCours;
	}

	/**
	 * Methode permettant de redefinir la figure en cours de construction
	 * 
	 * @param figureEnCours
	 *            La nouvelle figure en cours de construction
	 */
	public void setFigureEnCours(FigureColoree figureEnCours) {
		this.figureEnCours = figureEnCours;
	}

	/**
	 * Methode permettant de redefinir la liste des figures deja construites
	 * 
	 * @param lfg
	 *            La nouvelle liste des figures deja construites
	 */
	public void setLfg(ArrayList<FigureColoree> lfg) {
		if(lfg == null) {
			this.lfg.clear();
		} else {
			this.lfg = lfg;
		}
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Methode permettant de selectionner la derniere figure construite dont le point se trouve dedans
	 * 
	 * @param x
	 *            L'abscisse du point
	 * @param y
	 *            L'ordonnee du points
	 */
	public void selectionnerFigure(int x, int y) {
		if(this.figureSelectionnee != null) {
			this.figureSelectionnee.deSelectionne();
		}
		this.figureSelectionnee = null;
		int i = this.lfg.size() - 1;
		while(i >= 0 && this.figureSelectionnee == null) {
			FigureColoree figure = this.lfg.get(i);
			if(figure != null && figure.estDedans(x, y)) {
				this.figureSelectionnee = figure;
			} else {
				--i;
			}
		}
		if(this.figureSelectionnee != null) {
			this.figureSelectionnee.selectionne();
		}
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Methode accesseur permettant de recuperer le type d'action en cours du modele dessin
	 * 
	 * @return Le type d'action en cours du modele dessin
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Methode permettant de changer le type d'action en cours pour le modele de dessin
	 * 
	 * @param type
	 *            Le nouveau type d'action
	 */
	public void changerType(int type) {
		if(type != this.type) {
			this.type = type;
			this.setChanged();
			this.notifyObservers();
		}
	}

	/**
	 * Methode accesseur permettant de recuperer la figure qui est selectionnee
	 * 
	 * @return La figure selectionnee
	 */
	public FigureColoree getFigureSelectionnee() {
		return this.figureSelectionnee;
	}

	/**
	 * Methode accesseur permettant de recuperer la liste des traits
	 * 
	 * @return La liste des traits crees
	 */
	public ArrayList<Trait> getTraits() {
		return this.traits;
	}

	/**
	 * Methode permettant d'ajouter un traint a la liste des trait du dessin
	 * 
	 * @param trait
	 *            Le nouveau trait a ajouter
	 */
	public void ajouterTrait(Trait trait) {
		if(trait != null) {
			this.traits.add(trait);
			this.setChanged();
			this.notifyObservers();
		}
	}

	/**
	 * Methode permettant de transformer une figure deja construite pour le dessin
	 * 
	 * @param dx
	 *            Le deplacement horizontal du carre de selection modifie
	 * @param dy
	 *            Le deplacement vertical du carre de selection modifie
	 * @param idxcarre
	 *            L'indice du carre de selection deplace
	 */
	public void transformerFigureSelectionnee(int dx, int dy, int idxcarre) {
		if(this.getFigureSelectionnee() != null) {
			this.getFigureSelectionnee().transformation(dx, dy, idxcarre);
			this.setChanged();
			this.notifyObservers();
		}
	}
}
