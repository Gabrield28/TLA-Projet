package analyseLexicale;

public class SourceReader {

	private String contenu;
	private int index=0;

	/**
	 * Constructeur 
	 * @param contenu
	 */
	public SourceReader(String contenu) {
		super();
		this.contenu = contenu;
	}

	/**
	 * Parcours la chaine de caractère pour lire chaque caractère puis
	 * renvoie un caractère ou null si fin de fichier
	 * @return char
	 */
	public Character lectureSymbole() {
		if (index >= contenu.length()) return null;
		char c = contenu.charAt(index);
		index++;
		return c;
	}

	/**
	 * retourne en arrière 
	 */
	public void goBack() {
		if (index == 0) {
			System.out.println("Appel de goBack interdit");
		} else {
			index--;
		}
	}
}
