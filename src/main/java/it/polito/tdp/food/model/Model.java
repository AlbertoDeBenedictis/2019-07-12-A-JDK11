package it.polito.tdp.food.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.food.db.FoodDao;

public class Model {

	private List<Food> cibi;
	private Graph<Food, DefaultWeightedEdge> grafo;
	FoodDao dao;

	public Model() {
		dao = new FoodDao();
	}

	// Questo metodo crea anche il grafo
	public List<Food> getCibi(int portions) {

		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		this.cibi = this.dao.getFoodsByPortions(portions);

		// Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.cibi);

		// Aggiungo gli archi
		for (Food f1 : this.cibi) {
			for (Food f2 : this.cibi) {

				if (!f1.equals(f2) && f1.getFood_code() < f2.getFood_code()) {

					Double peso = dao.calorieCongiunte(f1, f2);

					if (peso != null) {
						Graphs.addEdge(this.grafo, f1, f2, peso);
					}

				}
			}
		}
		System.out.println(this.grafo + "\n#archi " + this.grafo.edgeSet().size());

		return this.cibi;
	}

	public List<FoodCalories> elencoCibiConnessi(Food f) {

		List<FoodCalories> listaCibi = new ArrayList<>();

		List<Food> vicini = Graphs.neighborListOf(this.grafo, f);

		for (Food cibo : vicini) {

			// Prendo l'arco che collega il vicino, quindi il peso corrispondente
			DefaultWeightedEdge arco = this.grafo.getEdge(f, cibo);
			Double pesoArco = this.grafo.getEdgeWeight(arco);
			
			// Inserisco nella lista
			listaCibi.add(new FoodCalories(cibo,pesoArco));
			
		}
		
		Collections.sort(listaCibi);

		return listaCibi;
	}

}
