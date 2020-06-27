package it.polito.tdp.food.model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.food.model.Event.EventType;
import it.polito.tdp.food.model.Food.StatoPreparazione;

public class Simulator {

	// 1) Modello del mondo

	private List<Stazione> stazioni; // elenco stazioni disponibili
	private List<Food> cibi; // lista dei cibi possibili da cucinare

	private Graph<Food, DefaultWeightedEdge> grafo;
	// Stavolta mi serve un riferimento al model perchè mi servono i suoi metodi
	Model model;

	// 2) Parametri di simulazione

	private int K = 5; // numero stazioni disponibili

	// 3) Risultati Calcolati
	private Double tempoPreparazione;
	private int cibiPreparati;

	// 4) Coda degli eventi
	private PriorityQueue<Event> coda;

	// 5) Metodi

	public Simulator(Graph<Food, DefaultWeightedEdge> grafo, Model model) {

		this.grafo = grafo;
		this.model = model;

	}

	public void init(Food partenza) {

		this.cibiPreparati = 0;
		this.cibi = new ArrayList<>(this.grafo.vertexSet());
		for (Food cibo : this.cibi) {
			cibo.setPreparazione(StatoPreparazione.DA_PREPARARE);
		}
		this.stazioni = new ArrayList<>();

		// Aggiungo le K stazioni (libere senza cibi in prep)
		for (int i = 0; i < K; i++) {

			this.stazioni.add(new Stazione(true, null));
		}

		this.tempoPreparazione = 0.0;

		this.coda = new PriorityQueue<>();
		List<FoodCalories> vicini = model.elencoCibiConnessi(partenza);

		for (int i = 0; i < K && i < vicini.size(); i++) {

			// Occupo le stazioni con i cibi presenti nella lista
			this.stazioni.get(i).setLibera(false);
			this.stazioni.get(i).setFood(vicini.get(i).getFood());

			// Creo gli eventi

			// La stazione finisce di fare quel cibo
			Event e = new Event(vicini.get(i).getCalories(), EventType.FINE_PREPARAZIONE, vicini.get(i).getFood(),
					this.stazioni.get(i));

			coda.add(e);

		}
	}

	public void run() {

		while (!coda.isEmpty()) {
			Event e = coda.poll();
			processEvent(e);
		}

	}

	private void processEvent(Event e) {

		switch (e.getType()) {

		case INIZIO_PREPARAZIONE:

			List<FoodCalories> vicini = this.model.elencoCibiConnessi(e.getFood());
			FoodCalories prossimo = null;
			// Cerco il massimo con calorie congiunte tra i vicini
			for (FoodCalories vicino : vicini) {
				if (vicino.getFood().getPreparazione() == StatoPreparazione.DA_PREPARARE) {
					prossimo = vicino;
					break;
				}
			}

			// se ho trovato un vicino non ancora preparato
			if (prossimo != null) {
				// così le altre stazioni non si mettono a prepararlo
				prossimo.getFood().setPreparazione(StatoPreparazione.IN_CORSO);
				e.getStazione().setLibera(false); // occupiamo la stazione
				e.getStazione().setFood(prossimo.getFood());

				// creiamo il nuovo evento
				Event e2 = new Event(e.getTime() + prossimo.getCalories(), EventType.FINE_PREPARAZIONE,
						prossimo.getFood(), e.getStazione());
				// aggiungiamolo alla coda
				this.coda.add(e2);
			}

			break;

		case FINE_PREPARAZIONE:

			// 1) aggiorno lo stato del mondo
			this.cibiPreparati++;
			this.tempoPreparazione = e.getTime(); // così poi mi salvo l'ultimo
			e.getStazione().setLibera(true);
			e.getFood().setPreparazione(StatoPreparazione.PREPARATO);

			// 2) rischedulo l'inizio della nuova preparazione allo stesso istante

			// creo nuovo evento di inizio preparazione
			Event e2 = new Event(e.getTime(), EventType.INIZIO_PREPARAZIONE, e.getFood(), e.getStazione());
			// lo aggiungo alla coda
			this.coda.add(e2);

			break;
		}

	}
	
	

	public Double getTempoPreparazione() {
		return tempoPreparazione;
	}

	public int getCibiPreparati() {
		return cibiPreparati;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

}
